package cn.edu.sicau.pfdistribution.service.jiaodaTest

import java.util

import cn.edu.sicau.pfdistribution.Utils.{DataBaseLoading, DateExtendUtil, TimeSlice}
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.jiaodaTest.OracleTestDataGet
import cn.edu.sicau.pfdistribution.entity.correct.{SectionFlow, _}
import cn.edu.sicau.pfdistribution.entity.{Command, DirectedEdge}
import cn.edu.sicau.pfdistribution.entity.jiaoda.{GetQuarterPassengerFlow, QuarterSectionSave, StationInOutSave, StoreSectionPassengers}
import cn.edu.sicau.pfdistribution.service.SectionCalculation
import cn.edu.sicau.pfdistribution.service.kspdistribution.{MainTransfer, SetRdd}
import org.apache.spark.rdd.RDD
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.mutable
import scala.collection.JavaConverters._

@Service
class SectionCalculationImpl @Autowired()(@transient getPassengerFlowOracle: OracleTestDataGet, betterCalculateImpl: BetterCalculateImpl, odOverflowCalculate: OdOverflowCalculate) extends SectionCalculation {
  override def sectionFlow(command: Command): util.List[AbstractSectionFlow] = {
    val passengerFlows = getPassengerFlowOracle.quarterPassengers(command.getStartTime, command.getEndTime)
    lastAllSectionResult(command.getTargetTable, passengerFlows, command)
  }

  def lastAllSectionResult(targetTable: String, odList: java.util.List[GetQuarterFlow], requestCommand: Command): util.List[AbstractSectionFlow] = {
    val dateDt = requestCommand.getDateDt
    val passengerFlows = odList.asScala
    val FlowRDD = SetRdd.sc.makeRDD(passengerFlows)
    val overflowRDD = FlowRDD.map(odOverflowCalculate.odQuarterDistributionResult)
    val sectionflow = overflowRDD.map(overflowRDDToSectionCapacity)
    //    overflowRDD.map()
    //    val list: RDD[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = odListToArrayDirectedEdge(requestCommand, odList)
    //    val sectionSaves: RDD[QuarterSectionSave] = quarterSectionReturn(list)
    //    val sectionMaps: mutable.Map[(String, String, String, String), Double] = oneGetTimeSlice(dateDt, sectionSaves)
    //    val allSectionEntity = sectionMapsToEntity(dateDt, sectionMaps)
    //    allSectionEntity
    null
  }

  def sectionFlowAggregation(sectionFlow1: SectionFlow, sectionFlow2: SectionFlow): SectionFlow = {
    val sectionCapacityMap1 = sectionFlow1.getSectionCapacityMap
    val sectionCapacityMap2 = sectionFlow2.getSectionCapacityMap
    for ((k, v) <- sectionCapacityMap2) {
      if (sectionCapacityMap1.contains(k)) {
        sectionCapacityMap1.put(k, sectionCapacityMap1(k).toDouble + v)
      } else {
        sectionCapacityMap1.put(k, v)
      }
    }
    sectionFlow1
  }

  def overflowRDDToSectionCapacity(overflowData: OverflowData): SectionFlow = {
    val odPath = overflowData.getOdPath
    val inName = overflowData.getInName
    val outName = overflowData.getOutName
    val inTime = overflowData.getInTime
    val outTime = overflowData.getOutTime
    val minutes = overflowData.getMinutes
    val passengers = overflowData.getPassengers
    val capacityMap = odPath.map(odPathToSectionCapacityMap(_, minutes, inTime)).reduce((x, y) => {
      for ((k, v) <- y) {
        var double: Double = v
        if (double.isNaN) {
          double = 0.0
        }
        if (x.contains(k)) {
          x.put(k, x(k) + double)
        } else {
          x.put(k, v)
        }
      }
      x
    })
    val sectionFlow = new SectionFlow(inName, outName, inTime, outTime, passengers, capacityMap)
    sectionFlow
  }

  def odPathToSectionCapacityMap(directedEdgeTuple: (Array[DirectedEdge], Double), minutes: Int, inTime: String): mutable.Map[AbstractSectionCapacity, Double] = {
    val sectionMap = mutable.Map[AbstractSectionCapacity, Double]()
    val directedEdge = directedEdgeTuple._1
    val passengers = directedEdgeTuple._2
    val length = directedEdge.length
    val secondAdd = (minutes * 60) / length
    val theDate = inTime.split(" ")(0)
    val date = DateExtendUtil.stringToDate(theDate, DateExtendUtil.PART)
    val startTime = DateExtendUtil.stringToDate(inTime, DateExtendUtil.FULL)
    for (i <- 0 until length) {
      val edge = directedEdge(i).getEdge
      val sectionCapacity = new SectionCapacity()
      sectionCapacity.setInId(edge.getFromNode)
      sectionCapacity.setOutId(edge.getToNode)
      val startDate = DateExtendUtil.dateAdditionSecond(startTime, secondAdd * i)
      val endDate = DateExtendUtil.dateAdditionSecond(startTime, secondAdd * (i + 1))
      val dateSecondDiff = DateExtendUtil.dateDiff(startDate, endDate, DateExtendUtil.SECEND) / 2
      val finalDate = DateExtendUtil.dateAdditionSecond(startDate, dateSecondDiff)
      val inTime = DateExtendUtil.dateToInterval(finalDate, date, 15)
      sectionCapacity.setInTime(inTime)
      val outTime = DateExtendUtil.timeAddition(inTime, 0, 15)
      sectionCapacity.setOutTime(outTime)
      sectionMap.put(sectionCapacity, passengers)
    }
    sectionMap
  }

  /**
    * 第一步
    * 动态和静态计算分类,独立一个方法解决scala的val不变量赋值问题
    *
    * @author LiYongPing
    * @param requestCommand 请求信息
    * @param odList         GetQuarterPassengerFlow,数据库原始数据List
    * @return
    */
  def odListToArrayDirectedEdge(requestCommand: Command, odList: java.util.List[GetQuarterPassengerFlow]): RDD[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = {
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
    *
    * 第二步
    * 15分钟所有的区间分配结果叠加
    *
    * @author LiYongPing
    * @param rddTuples ：15分钟每个OD分配后的与起止时间同一元组的List集合
    * @return ：大批量区间分配的List集合准确时间结果,可以作为最终返回结果，精确度很高，但数据量百万级别
    */
  def quarterSectionReturn(rddTuples: RDD[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)]): RDD[QuarterSectionSave] = {
    val tupleCompute = (paths: (String, String, mutable.Map[Array[DirectedEdge], Double], Int)) => quarterGetSectionResult(paths)
    val rddTupleMaps = rddTuples.flatMap(tupleCompute)
    rddTupleMaps
  }

  def quarterGetSectionResult(pathInfo: (String, String, mutable.Map[Array[DirectedEdge], Double], Int)): List[QuarterSectionSave] = {
    val keys = pathInfo._3.keys
    val startTime = pathInfo._1
    val timeDiff = pathInfo._4 * 60

    val sectionSaveList = new java.util.ArrayList[QuarterSectionSave]
    for (i <- keys) {
      val passengers = pathInfo._3(i)
      val length = i.length
      val timeInterval = timeDiff / length
      var theStart = startTime
      for (j <- 0 until length) {
        val inId = i(j).getEdge.getFromNode
        val outId = i(j).getEdge.getToNode
        val theEnd = DateExtendUtil.timeAdditionSecond(theStart, timeInterval)
        val quarterSectionSave = new QuarterSectionSave(theStart, theEnd, inId, outId, passengers)
        theStart = theEnd
        sectionSaveList.add(quarterSectionSave)
      }
    }
    sectionSaveList.asScala.toList
  }

  /**
    * 第三步
    * 15分钟所有区间结果按照15分钟一个区间进行时间整合，耗时较短(改进算法)
    *
    * @author LiYongPing
    * @param dataDt     :数据分配时间，通常由开始时间得到
    * @param oneSection :大批量区间RDD分配准确时间结果
    * @return :包含时间和分配区间的Map，可以作为最终返回结果，精确度为15分钟级别，数据量每次千条级别
    */
  def oneGetTimeSlice(dataDt: String, oneSection: RDD[QuarterSectionSave]): mutable.Map[(String, String, String, String), Double] = {
    val oneSectionMap = oneSection.map(QuarterSectionSave => TimeSlice.getTimeSlice(dataDt, QuarterSectionSave))
    val allSectionMaps = oneSectionMap.reduce((x, y) => quarterMapIntegration(x, y))
    allSectionMaps
  }

  def quarterMapIntegration(sectionMap1: mutable.Map[(String, String, String, String), Double], sectionMap2: mutable.Map[(String, String, String, String), Double]): mutable.Map[(String, String, String, String), Double] = {
    for ((k, v) <- sectionMap2) {
      var double: Double = v
      if (double.isNaN) {
        double = 0.0
      }
      if (sectionMap1.contains(k)) {
        if (sectionMap1(k).isNaN) {
          sectionMap1.put(k, 0 + double)
        }
        sectionMap1.put(k, sectionMap1(k) + double)
      } else {
        sectionMap1.put(k, double)
      }
    }
    sectionMap1
  }

  /**
    * 第四步
    *
    * @param dataDt      数据日期
    * @param sectionMaps 含有区间流量的Map集合
    * @return
    */
  def sectionMapsToEntity(dataDt: String, sectionMaps: mutable.Map[(String, String, String, String), Double]): java.util.List[AbstractSectionFlow] = {
    val sectionIdMap = DataBaseLoading.sectionIdMap
    val arrayList: java.util.List[AbstractSectionFlow] = new util.ArrayList[AbstractSectionFlow]()
    for ((k, v) <- sectionMaps) {
      var double: Double = v
      val sectionId = sectionIdMap.get(k._3 + " " + k._4)
      if (double.isNaN) {
        double = 0.0
      }
      val sectionPassengers: AbstractSectionFlow = new SectionFlow(dataDt, sectionId, k._3, k._4, k._1, k._2, double)
      arrayList.add(sectionPassengers)
    }
    arrayList
  }

}
