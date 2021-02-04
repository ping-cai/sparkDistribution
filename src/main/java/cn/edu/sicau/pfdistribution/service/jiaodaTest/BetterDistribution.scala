package cn.edu.sicau.pfdistribution.service.jiaodaTest

import java.util
import java.util.Date

import cn.edu.sicau.pfdistribution.Utils.TimeSlice
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.GetPassengerFlowHive
import cn.edu.sicau.pfdistribution.entity.DirectedEdge
import cn.edu.sicau.pfdistribution.entity.jiaoda._
import cn.edu.sicau.pfdistribution.exceptionhandle.CheckService
import cn.edu.sicau.pfdistribution.service.kspdistribution.{CalculateBaseInterface, GetLineID, MainTransfer, SetRdd}
import org.apache.spark.rdd.RDD
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.mutable


/**
  * @Author LiYongPing
  * @Date 2021-02-02
  * @LastUpdate 2021-02-02
  */
@Service
class BetterDistribution @Autowired()(calBase: CalculateBaseInterface, getLineID: GetLineID, @transient getPassengerFlowHive: GetPassengerFlowHive,
                                      exceptionController: CheckService, @transient saveDistribution: SaveDistribution, betterCalculateImpl: BetterCalculateImpl,
                                     ) extends Serializable {

  def intervalTriggerTask(fromTable: String, targetTable: String, requestCommand: RequestCommand): Unit = {
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
      val getQuarterPassengerFlows = getPassengerFlowHive.quarterHourWithOneDay(fromTable, requestCommand.getStartTime, requestCommand.getEndTime)
      lastAllSectionResult(targetTable, getQuarterPassengerFlows, requestCommand)
    }
    else {
      if (odMapObtain.isEmpty) {
        exceptionController.dataIsEmpty(requestCommand)
      } else {
        val odMap = MainTransfer.odMapTransferScala(odMapObtain)
        if (command.equals("static")) {
          //        静态的路径分配
          intervalResult(targetTable, odMap, requestCommand)
        } else
        //      动态的路径分配
          intervalResultDynamic(targetTable, odMap, requestCommand)
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
  def intervalResult(targetTable: String, odMap: mutable.Map[String, Integer], requestCommand: RequestCommand): Unit = {
    //    取得odMap的所有键序列为List
    val odList: List[String] = odMap.keySet.toList
    val rdd = SetRdd.sc.makeRDD(odList)
    //RDD转换操作，可得到每个OD的分配结果
    val odDistributionRdd = rdd.map(String => calBase.odDistributionResult(String, odMap))

    val stationList = SetRdd.stationRdd(rdd, odMap)
    //RDD行动操作，进行OD分配结果的RDD的整合,Map集合增量操作，x,y代指每个RDD集，
    //    val rddIntegration = odDistributionRdd.reduce((x, y) => x ++ y)
    //    logger.info("ksp:{}", rddIntegration.keys.size)
    //    SparkTest.testRdd(odDistributionRdd)
    //    分配后进出站的数据存储
    saveDistribution.saveStation(targetTable, requestCommand, stationList)
    //    分配后每个区间的数据存储
    val sectionList = SetRdd.sectionRdd(odDistributionRdd)
    saveDistribution.saveSection(targetTable, requestCommand, sectionList)
    val transferList = SetRdd.transferRdd(odDistributionRdd)
    //    分配后换乘的数据存储
    saveDistribution.saveTransfer(targetTable, requestCommand, transferList)
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
  def intervalResultDynamic(targetTable: String, odMap: mutable.Map[String, Integer], requestCommand: RequestCommand): Unit = {
    val odList: List[String] = odMap.keySet.toList
    val rdd = SetRdd.sc.makeRDD(odList)
    val odDistributionRdd: RDD[mutable.Map[Array[DirectedEdge], Double]] = rdd.map(String => calBase.dynamicOdDistributionResult(String, odMap))
    val stationList = SetRdd.stationRdd(rdd, odMap)
    val transferList = SetRdd.transferRdd(odDistributionRdd)
    val sectionList = SetRdd.sectionRdd(odDistributionRdd)
    saveDistribution.saveStation(targetTable, requestCommand, stationList)
    saveDistribution.saveSection(targetTable, requestCommand, sectionList)
    saveDistribution.saveTransfer(targetTable, requestCommand, transferList)
  }

  /**
    * 15分钟高精确度分配入口方法，数据库取出Od不在同一个15分钟区间数据后，进入此方法进行计算
    * 其中计算和存储分离
    *
    * @author LiYongPing
    * @param targetTable :目标生成和插入的表
    * @param odList      ：GetQuarterPassengerFlow集合，最先取出的数据集合
    */
  def lastAllSectionResult(targetTable: String, odList: java.util.List[GetQuarterPassengerFlow], requestCommand: RequestCommand): Unit = {
    val dateDt = requestCommand.getDateDt
    val list: RDD[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = odListToArrayDirectedEdge(requestCommand, odList)
    val sectionSaves: RDD[QuarterSectionSave] = quarterSectionReturn(list)
    val transferMaps = quarterTransferReturn(dateDt, list)
    val sectionMaps: mutable.Map[(String, String, String, String), Double] = oneGetTimeSlice(dateDt, sectionSaves)
    val stationList: util.ArrayList[StationInOutSave] = quarterStationInOutResult(dateDt, odList)
    val allSectionEntity = MainTransfer.sectionMapsToEntity(dateDt, sectionMaps)
    //    rddStorage.quarterSectionStorage(dateDt, sectionMaps, requestCommand)
    saveDistribution.saveTimeSliceSection(targetTable, requestCommand, allSectionEntity)
    //    rddStorage.quarterTransferStorage(transferMaps, requestCommand)
    saveDistribution.saveTimeSliceStation(targetTable, requestCommand, stationList)
    //    rddStorage.quarterStationStorage(dateDt, stationList, requestCommand)
    saveDistribution.saveTimeSliceTransfer(targetTable, requestCommand, transferMaps)
  }

  /**
    * 动态和静态计算分类,独立一个方法解决scala的val不变量赋值问题
    *
    * @author LiYongPing
    * @param requestCommand 请求信息
    * @param odList         GetQuarterPassengerFlow,数据库原始数据List
    * @return
    */
  def odListToArrayDirectedEdge(requestCommand: RequestCommand, odList: java.util.List[GetQuarterPassengerFlow]): RDD[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = {
    if (requestCommand.getCommand == "static") {
      quarterIntervalResult(odList)
    } else {
      quarterIntervalResultDynamic(odList)
    }
  }

  /**
    * 15分钟蕴含时间的精确度计算，静态方法计算
    *
    * @author LiYongPing
    * @param odList ：获得的Od原始数据
    * @return List((起始时间，终止时间,单个Od分配结果,时间差),)
    *
    */
  def quarterIntervalResult(odList: java.util.List[GetQuarterPassengerFlow]): RDD[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = {
    val rdd = SetRdd.sc.makeRDD(odList.asScala)
    val odAllPathList = rdd.map(GetQuarterPassengerFlow => betterCalculateImpl.odQuarterDistributionResult(GetQuarterPassengerFlow))
    odAllPathList
  }

  /**
    * 15分钟蕴含时间的精确度计算，动态方法计算
    *
    * @author LiYongPing
    * @param odList ：获得的Od原始数据
    * @return List((起始时间，终止时间,单个Od分配结果,时间差),)
    *
    */
  def quarterIntervalResultDynamic(odList: java.util.List[GetQuarterPassengerFlow]): RDD[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = {
    val rdd = SetRdd.sc.makeRDD(odList.asScala)
    val odAllPathList = rdd.map(GetQuarterPassengerFlow => betterCalculateImpl.odQuarterDistributionResultDynamic(GetQuarterPassengerFlow))
    odAllPathList
  }

  /**
    * 15分钟所有的区间分配结果叠加
    *
    * @author LiYongPing
    * @param rddTuples ：15分钟每个OD分配后的与起止时间同一元组的List集合
    * @return ：大批量区间分配的List集合准确时间结果,可以作为最终返回结果，精确度很高，但数据量百万级别
    */
  def quarterSectionReturn(rddTuples: RDD[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)]): RDD[QuarterSectionSave] = {
    val tupleCompute = (paths: (String, String, mutable.Map[Array[DirectedEdge], Double], Int)) => calBase.quarterGetSectionResult(paths)
    val rddTupleMaps = rddTuples.flatMap(tupleCompute)
    rddTupleMaps
  }

  /**
    * 15分钟所有区间结果按照15分钟一个区间进行时间整合，耗时较短(改进算法)
    *
    * @author LiYongPing
    * @param dataDt     :数据分配时间，通常由开始时间得到
    * @param oneSection :大批量区间RDD分配准确时间结果
    * @return :包含时间和分配区间的Map，可以作为最终返回结果，精确度为15分钟级别，数据量每次千条级别
    */
  def oneGetTimeSlice(dataDt: String, oneSection: RDD[QuarterSectionSave]): mutable.Map[(String, String, String, String), Double] = {
    val oneSectionMap = oneSection.map(QuarterSectionSave => TimeSlice.getTimeSlice(dataDt, QuarterSectionSave))
    val allSectionMaps = oneSectionMap.reduce((x, y) => MainTransfer.quarterMapIntegration(x, y))
    allSectionMaps
  }

  /**
    * 15分钟换乘数据最终结果，可入库
    *
    * @author LiYongPing
    * @param rddTuples ：最初请求分配的OD数据的RDD集合
    * @return ：可以入库的StoreTransferData集合数据
    */
  def quarterTransferReturn(dateDt: String, rddTuples: RDD[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)]): util.List[StoreTransferData] = {
    val tupleCompute = (paths: (String, String, mutable.Map[Array[DirectedEdge], Double], Int)) => MainTransfer.quarterTransferReturn(paths)
    val rddTupleMaps = rddTuples.map(tupleCompute)
    val transferMaps = rddTupleMaps.map((x: List[StoreTransferData]) => MainTransfer.transferGetTimeSlice(dateDt, x))
    val theAllTransferMaps = transferMaps.reduce((x: mutable.Map[(Date, Date, Date, String, String, String), (Double, Double, Double, Double)], y: mutable.Map[(Date, Date, Date, String, String, String), (Double, Double, Double, Double)]) => MainTransfer.transferMapIntegration(x, y))
    val storeTransferDataS = MainTransfer.transferMapToEntity(theAllTransferMaps)
    storeTransferDataS
  }

  /** 15分钟进出站数据精确算法
    *
    * @author ZhouZhiYuan
    * @param dataDt ：数据日期
    * @param odList ：待分配的OD数据源
    * @return
    */
  def quarterStationInOutResult(dataDt: String, odList: java.util.List[GetQuarterPassengerFlow]): util.ArrayList[StationInOutSave] = {
    val rdd = SetRdd.sc.makeRDD(odList.asScala)
    val stationRddMap = rdd.map(GetQuarterPassengerFlow => MainTransfer.mapStationPassenger(dataDt, GetQuarterPassengerFlow))
    val stationMapS = stationRddMap.reduce((x, y) => MainTransfer.stationMapIntegration(x, y))
    val stationInOutSaves = MainTransfer.stationInOutSaveToEntity(stationMapS)
    stationInOutSaves
  }
}
