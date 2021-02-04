package cn.edu.sicau.pfdistribution.service.kspdistribution

import java.io._
import java.util.Date
import java.util

import cn.edu.sicau.pfdistribution.Utils.TimeSlice
import cn.edu.sicau.pfdistribution.dao.Impl.MysqlGetID
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.{GetPassengerFlowHive, GetPassengerFlowOracle}
import cn.edu.sicau.pfdistribution.entity._
import cn.edu.sicau.pfdistribution.entity.jiaoda._
import cn.edu.sicau.pfdistribution.exceptionhandle.CheckService
import cn.edu.sicau.pfdistribution.service.dataStorage.RddStorage
import cn.edu.sicau.pfdistribution.service.kspcalculation.Edge
import cn.edu.sicau.pfdistribution.service.road.KServiceImpl
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.mutable


//该段代码把Object改成Class定义
@Service
case class MainDistribution @Autowired()(calBase: CalculateBaseInterface, getOdList: GetOdList, getParameter: GetParameter, kServiceImpl: KServiceImpl, dataDeal: DataDeal, tongHaoReturnResult: TongHaoReturnResult, getLineID: GetLineID, mysqlGetID: MysqlGetID

                                         , lineIdAndSectionTime: LineIdAndSectionTime, @transient getPassengerFlowHive: GetPassengerFlowHive, @transient rddStorage: RddStorage, exceptionController: CheckService) extends Serializable { //,val getOdList: GetOdList
  @transient
  private val sc: SparkContext = SetRdd.sc
  //该段代码移植到KafkaReceiver中
  /** intervalTriggerTask方法用于返回静态和动态的区间分配结果
    * 交大使用
    *
    * @author LiYongPing
    * @param requestCommand 请求信息（日期时间：dateDt；起止时间：startTime，endTime；时间粒度：timeInterval；分配命令：command;）
    * @return ava.util.Map（section->passengers）
    */
  def intervalTriggerTask(requestCommand: RequestCommand): Unit = {
    // 从数据库中读取K短路径计算所需要的各类参数
    getLineID.setStationId()
    //    获取分配命令
    val command: String = requestCommand.getCommand
    //    odMapObtain：暂存数据库中获取的数据
    var odMapObtain: mutable.Map[String, Integer] = mutable.Map[String, Integer]()
    if (requestCommand.getTimeInterval == 30) {
      //      半小时的待分配数据获取
      odMapObtain = getPassengerFlowHive.halfHourGet(requestCommand.getDateDt, requestCommand.getStartTime, requestCommand.getEndTime).asScala
    } else if (requestCommand.getTimeInterval == 60) {
      //      一小时的待分配数据获取
      odMapObtain = getPassengerFlowHive.oneHourGet(requestCommand.getDateDt, requestCommand.getStartTime, requestCommand.getEndTime).asScala
    }
    if (requestCommand.getTimeInterval == 15) {
      //      15分钟的待分配数据获取，从此处前面不一致
      val getQuarterPassengerFlows = getPassengerFlowHive.quarterHourNoInclude(requestCommand.getStartTime, requestCommand.getEndTime)
      lastAllSectionResult(requestCommand.getDateDt, getQuarterPassengerFlows, requestCommand)
    }
    else {
      if (odMapObtain.isEmpty) {
        exceptionController.dataIsEmpty(requestCommand)
      } else {
        val odMap = MainTransfer.odMapTransferScala(odMapObtain)
        if (command.equals("static")) {
          //        静态的路径分配
          intervalResult(odMap, requestCommand)
        } else
        //      动态的路径分配
          intervalResultDynamic(odMap, requestCommand)
      }
    }
  }

  /**
    * 静态分配计算，返回区间断面的分配结果（半小时和一小时）
    *
    * @param odMap          HashMap(进站名 出站名,人数)
    *                       odList：odMap的所有键
    *                       rddIntegration：HashMap((一条路径,分配的人数))
    *                       dataListStation：进出站人数
    *                       transferPoints：换乘详细信息
    *                       regionMap：区间断面信息
    * @param requestCommand :请求信息
    * @return 区间断面数据
    */
  def intervalResult(odMap: mutable.Map[String, Integer], requestCommand: RequestCommand): Unit = {
    //    取得odMap的所有键序列为List
    val odList: List[String] = odMap.keySet.toList
    val rdd = sc.makeRDD(odList)
    //RDD转换操作，可得到每个OD的分配结果
    val odDistributionRdd = rdd.map(String => calBase.odDistributionResult(String, odMap))

    val stationList = SetRdd.stationRdd(rdd, odMap)
    //RDD行动操作，进行OD分配结果的RDD的整合,Map集合增量操作，x,y代指每个RDD集，
    //    val rddIntegration = odDistributionRdd.reduce((x, y) => x ++ y)
    //    logger.info("ksp:{}", rddIntegration.keys.size)
//    SparkTest.testRdd(odDistributionRdd)
    //    分配后进出站的数据存储
    rddStorage.StationInAndOutStorage(requestCommand, stationList)
    //    分配后每个区间的数据存储
    val sectionRdd = SetRdd.sectionRdd(odDistributionRdd)
    rddStorage.sectionStorage(requestCommand, sectionRdd)
    val transferRdd = SetRdd.transferRdd(odDistributionRdd)
    //    分配后换乘的数据存储
    rddStorage.transferLineStorage(requestCommand, transferRdd)
  }

  /**
    * 动态分配计算，返回区间断面的分配结果，（半小时和一小时），具体每行代码可参考intervalResult静态分配
    *
    * @param odMap          HashMap(进站名 出站名,人数)
    *                       odList：odMap的所有键
    *                       rddIntegration：HashMap((一条路径,分配的人数))
    *                       dataListStation：进出站人数
    *                       transferPoints：换乘详细信息
    *                       regionMap：区间断面信息
    * @param requestCommand :请求信息
    * @return
    */
  def intervalResultDynamic(odMap: mutable.Map[String, Integer], requestCommand: RequestCommand): Unit = {
    val odList: List[String] = odMap.keySet.toList
    val rdd = sc.makeRDD(odList)
    val odDistributionRdd: RDD[mutable.Map[Array[DirectedEdge], Double]] = rdd.map(String => calBase.dynamicOdDistributionResult(String, odMap))
    val stationList = SetRdd.stationRdd(rdd, odMap)
    val transferRdd = SetRdd.transferRdd(odDistributionRdd)
    val sectionRdd = SetRdd.sectionRdd(odDistributionRdd)
    //    val rddIntegration: mutable.Map[Array[DirectedEdge], Double] = odDistributionRdd.reduce((x, y) => x ++ y)
    //    logger.info("ksp:{}", rddIntegration.keys.size)
    rddStorage.StationInAndOutStorage(requestCommand, stationList)
    rddStorage.sectionStorage(requestCommand, sectionRdd)
    rddStorage.transferLineStorage(requestCommand, transferRdd)
  }

  /**
    * 15分钟高精确度分配入口方法，数据库取出Od不在同一个15分钟区间数据后，进入此方法进行计算
    * 其中计算和存储分离
    *
    * @author LiYongPing
    * @param dataDt :数据日期时间
    * @param odList ：GetQuarterPassengerFlow集合，最先取出的数据集合
    */
  def lastAllSectionResult(dataDt: String, odList: java.util.List[GetQuarterPassengerFlow], requestCommand: RequestCommand): Unit = {
    val list = odListToArrayDirectedEdge(requestCommand, odList)
    val sectionSaves: List[QuarterSectionSave] = quarterSectionReturn(list)
    val transferMaps = quarterTransferReturn(dataDt, list)
    val sectionMaps: mutable.Map[(String, String, String, String), Double] = oneGetTimeSlice(dataDt, sectionSaves)
    val stationList: util.ArrayList[StationInOutSave] = quarterStationInOutResult(dataDt, odList)
    rddStorage.quarterSectionStorage(dataDt, sectionMaps, requestCommand)
    rddStorage.quarterTransferStorage(transferMaps, requestCommand)
    rddStorage.quarterStationStorage(dataDt, stationList, requestCommand)
  }

  /**
    * 动态和静态计算分类,独立一个方法解决scala的val不变量赋值问题
    *
    * @author LiYongPing
    * @param requestCommand 请求信息
    * @param odList         GetQuarterPassengerFlow,数据库原始数据List
    * @return
    */
  def odListToArrayDirectedEdge(requestCommand: RequestCommand, odList: java.util.List[GetQuarterPassengerFlow]): List[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = {
    if (requestCommand.getCommand == "static") {
      quarterIntervalResult(odList)
    } else {
      quarterIntervalResultDynamic(odList)
    }
  }

  /** 15分钟进出站数据精确算法
    *
    * @author ZhouZhiYuan
    * @param dataDt ：数据日期
    * @param odList ：待分配的OD数据源
    * @return
    */
  def quarterStationInOutResult(dataDt: String, odList: java.util.List[GetQuarterPassengerFlow]): util.ArrayList[StationInOutSave] = {
    val rdd = sc.makeRDD(odList.asScala)
    val stationRddMap = rdd.map(GetQuarterPassengerFlow => MainTransfer.mapStationPassenger(dataDt, GetQuarterPassengerFlow))
    val stationMapS = stationRddMap.reduce((x, y) => MainTransfer.stationMapIntegration(x, y))
    val stationInOutSaves = MainTransfer.stationInOutSaveToEntity(stationMapS)
    stationInOutSaves
  }

  /**
    * 15分钟蕴含时间的精确度计算，静态方法计算
    *
    * @author LiYongPing
    * @param odList ：获得的Od原始数据
    * @return List((起始时间，终止时间,单个Od分配结果,时间差),)
    *
    */
  def quarterIntervalResult(odList: java.util.List[GetQuarterPassengerFlow]): List[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = {
    val rdd = sc.makeRDD(odList.asScala)
    val odAllPathList = rdd.map(GetQuarterPassengerFlow => calBase.odQuarterDistributionResult(GetQuarterPassengerFlow))
    odAllPathList.reduce((x, y) => x ++ y)
  }

  /**
    * 15分钟蕴含时间的精确度计算，动态方法计算
    *
    * @author LiYongPing
    * @param odList ：获得的Od原始数据
    * @return List((起始时间，终止时间,单个Od分配结果,时间差),)
    *
    */
  def quarterIntervalResultDynamic(odList: java.util.List[GetQuarterPassengerFlow]): List[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = {
    val rdd = sc.makeRDD(odList.asScala)
    val odAllPathList = rdd.map(GetQuarterPassengerFlow => calBase.odQuarterDistributionResultDynamic(GetQuarterPassengerFlow))
    odAllPathList.reduce((x, y) => x ++ y)
  }

  /**
    * 15分钟所有的区间分配结果叠加
    *
    * @author LiYongPing
    * @param odDistributionList ：15分钟每个OD分配后的与起止时间同一元组的List集合
    * @return ：大批量区间分配的List集合准确时间结果,可以作为最终返回结果，精确度很高，但数据量百万级别
    */
  def quarterSectionReturn(odDistributionList: List[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)]): List[QuarterSectionSave] = {
    val rddTuples = sc.makeRDD(odDistributionList)
    val tupleCompute = (paths: (String, String, mutable.Map[Array[DirectedEdge], Double], Int)) => calBase.quarterGetSectionResult(paths)
    val rddTupleMaps = rddTuples.map(tupleCompute)
    val rddReduce = rddTupleMaps.reduce((x, y) => x ++ y)
    rddReduce
  }

  /**
    * 15分钟所有区间结果按照15分钟一个区间进行时间整合，耗时较长
    *
    * @author LiYongPing
    * @param dataDt              :数据分配时间，通常由开始时间得到
    * @param quarterSectionSaves :大批量区间分配准确时间结果
    * @return :包含时间和分配区间的Map，可以作为最终返回结果，精确度为15分钟级别，数据量数万条级别
    */
  def getQuarterTimeSliceRdd(dataDt: String, quarterSectionSaves: List[QuarterSectionSave]): mutable.Map[(String, String, String, String), Double] = {
    val theListSize = quarterSectionSaves.size / 360
    val sectionList = quarterSectionSaves.grouped(theListSize).toList
    val rddSection = sc.makeRDD(sectionList)
    val quarterSectionList = rddSection.map((lists: List[QuarterSectionSave]) => MainTransfer.getTimeSliceList(dataDt, lists))
    val allSectionMaps = quarterSectionList.reduce((x, y) => MainTransfer.quarterMapIntegration(x, y))
    allSectionMaps
  }

  /**
    * 15分钟所有区间结果按照15分钟一个区间进行时间整合，耗时较短(改进算法)
    *
    * @author LiYongPing
    * @param dataDt              :数据分配时间，通常由开始时间得到
    * @param quarterSectionSaves :大批量区间分配准确时间结果
    * @return :包含时间和分配区间的Map，可以作为最终返回结果，精确度为15分钟级别，数据量每次千条级别
    */
  def oneGetTimeSlice(dataDt: String, quarterSectionSaves: List[QuarterSectionSave]): mutable.Map[(String, String, String, String), Double] = {
    val oneSection = sc.makeRDD(quarterSectionSaves)
    val oneSectionMap = oneSection.map(QuarterSectionSave => TimeSlice.getTimeSlice(dataDt, QuarterSectionSave))
    val allSectionMaps = oneSectionMap.reduce((x, y) => MainTransfer.quarterMapIntegration(x, y))
    allSectionMaps
  }

  /**
    * 15分钟换乘数据最终结果，可入库
    *
    * @author LiYongPing
    * @param list ：最初请求分配的OD数据
    * @return ：可以入库的StoreTransferData集合数据
    */
  def quarterTransferReturn(dataDt: String, list: List[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)]): util.List[StoreTransferData] = {
    val rddTuples = sc.makeRDD(list)
    val tupleCompute = (paths: (String, String, mutable.Map[Array[DirectedEdge], Double], Int)) => MainTransfer.quarterTransferReturn(paths)
    val rddTupleMaps = rddTuples.map(tupleCompute)
    val transferMaps = rddTupleMaps.map((x: List[StoreTransferData]) => MainTransfer.transferGetTimeSlice(dataDt, x))
    val theAllTransferMaps = transferMaps.reduce((x: mutable.Map[(Date, Date, Date, String, String, String), (Double, Double, Double, Double)], y: mutable.Map[(Date, Date, Date, String, String, String), (Double, Double, Double, Double)]) => MainTransfer.transferMapIntegration(x, y))
    val storeTransferDataS = MainTransfer.transferMapToEntity(theAllTransferMaps)
    storeTransferDataS
  }

  /**
    * 此处是提供基础web服务的算法
    *
    * @param od 需要查询路径的od对
    * @return 返回搜索到的时间最少、换乘最少的两条路径
    */
  def getDistribution(od: String): Object = {
    //    K短路径计算需要的数据
    getLineID.setStationId()
    //    动态路径搜索
    val data2: mutable.Map[Array[DirectedEdge], String] = calBase.dynamicOdPathSearch(od)
    var result: Map[String, String] = Map()
    val minPathMap1: Array[DirectedEdge] = totalTransfer(data2)
    val minPathMap2: Array[DirectedEdge] = minWeightPath(data2)
    val minDataArray: Array[Array[DirectedEdge]] = Array(minPathMap1, minPathMap2)
    for (i <- minDataArray.indices) {
      val minPath: Array[DirectedEdge] = minDataArray(i)
      val directedEdge: DirectedEdge = minPath(0)
      val edge: Edge = directedEdge.getEdge
      var str = edge.getFromNode
      for (i <- 1 until minPath.length) {
        val dEdge: DirectedEdge = minPath(i)
        val eg: Edge = dEdge.getEdge
        str = str + "," + eg.getFromNode
      }
      val dEdge2: DirectedEdge = minPath(minPath.length - 1)
      val eg2: Edge = dEdge2.getEdge
      str = str + "," + eg2.getToNode
      result += (str -> data2(minPath))

    }
    return result.map { case (k, v) => (k, v) }.asJava
  }


  //筛选出所有路径中权值和最小的路径

  def minWeightPath(data: mutable.Map[Array[DirectedEdge], String]): Array[DirectedEdge] = {
    //费用最短路径
    var result: mutable.Map[Array[DirectedEdge], Double] = mutable.Map()
    val value = data.values.head.split(" ").head.toDouble
    var minPath: Double = value
    for (key <- data.keys) {
      val distance: Double = data(key).split(" ").head.toDouble
      if (distance <= minPath)
        minPath = distance
      result.clear()
      result += (key -> minPath)
    }
    return result.keys.head
  }

  //换乘最少路径
  def totalTransfer(data: mutable.Map[Array[DirectedEdge], String]): Array[DirectedEdge] = {
    var result: mutable.Map[Array[DirectedEdge], Double] = mutable.Map()
    val CZMap: mutable.Map[Integer, Integer] = lineIdAndSectionTime.getStationIdToLineId.asScala
    var totalTransfer = 10000
    for (key <- data.keys) {
      var n: Int = 1
      //判断路径有无换乘和换乘次数
      for (i <- 0 to (key.length - 2)) {
        val dEdge1: DirectedEdge = key(i)
        val eg1: Edge = dEdge1.getEdge
        val dEdge2: DirectedEdge = key(i + 1)
        val eg2: Edge = dEdge2.getEdge
        val a = eg1.getFromNode
        val b = eg2.getToNode
        if (CZMap(a.toInt) != CZMap(b.toInt))
          n = n + 1
      }
      if (n < totalTransfer) {
        result.clear()
        totalTransfer = n
        result += (key -> totalTransfer)
      }
    }
    return result.keys.head
  }
}
