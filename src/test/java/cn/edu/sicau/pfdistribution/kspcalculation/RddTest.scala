package cn.edu.sicau.pfdistribution.kspcalculation

import java.util

import cn.edu.sicau.pfdistribution.Utils._
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.QuarterSaveEspecially
import cn.edu.sicau.pfdistribution.entity.DirectedEdge
import cn.edu.sicau.pfdistribution.entity.jiaoda._
import cn.edu.sicau.pfdistribution.service.kspdistribution.{CalculateBaseInterface, MainDistribution, MainTransfer, SetRdd}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

@Service
class RddTest @Autowired()(mainDistribution: MainDistribution, calculationTest: CalculationTest, calculateBaseInterface: CalculateBaseInterface, @transient quarterSaveEspecially: QuarterSaveEspecially, @transient commonMethod: CommonMethod, @transient nameToID: NameToID) extends Serializable {
  private[this] val logger = LoggerFactory.getLogger(this.getClass)


  def theTest(): Unit = {
    val Lists = List("1", "2", "3", "4", "5", "6", "7")
    val value = SetRdd.sc.makeRDD(Lists)
    val unit = value.map(String => printRdd(String, String.toInt))
    val map = unit.reduce((x, y) => x ++ y)
    println(map)
  }

  def printRdd(string: String, int: Int): mutable.Map[String, Int] = {
    val map: mutable.Map[String, Int] = mutable.Map(string -> int)
    map
  }

  def testListTuple(string: String): List[(String, String)] = {
    List((string, string))
  }

  def testList(getQuarterPassengerFlow: GetQuarterPassengerFlow, string: String): mutable.Map[String, String] = {
    return mutable.Map(getQuarterPassengerFlow.getInName -> string)
  }

  def testReduce(list: java.util.List[GetQuarterPassengerFlow]): Unit = {
    var theMap: mutable.Map[String, String] = mutable.Map()

    for (i <- 0 to 1000) {
      val o = list.get(i).getInName
      val d = list.get(i).getOutName
      theMap += (o -> d)
    }
    val mapLists = theMap.keySet.toList
    val unit = SetRdd.sc.makeRDD(mapLists)
    val stringMap = unit.map(String => testList(list.get(0), String))
    println(stringMap.reduce((x, y) => x ++ y))
  }

  def quarterIntervalResult(odList: java.util.List[GetQuarterPassengerFlow]): List[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = {
    val rdd = SetRdd.sc.makeRDD(odList.asScala)
    val odAllPathList = rdd.map(GetQuarterPassengerFlow => calculateBaseInterface.odQuarterDistributionResult(GetQuarterPassengerFlow))
    val pathLists = odAllPathList.reduce((x, y) => x ++ y)
    pathLists
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

  def QuarterSectionReturn(odList: java.util.List[GetQuarterPassengerFlow]): List[QuarterSectionSave] = {
    val tuples: List[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = quarterIntervalResult(odList)
    val rddTuples = SetRdd.sc.makeRDD(tuples)
    val tupleCompute = (paths: (String, String, mutable.Map[Array[DirectedEdge], Double], Int)) => quarterGetSectionResult(paths)
    val rddTupleMaps = rddTuples.map(tupleCompute)
    val rddReduce = rddTupleMaps.reduce((x, y) => x ++ y)
    rddReduce
  }

  def ListReduce(): Unit = {
    val list: List[String] = List("1", "2", "3", "4", "5")
    val rdd = SetRdd.sc.makeRDD(list)
    val unit = rdd.map(String => testListTuple(String))
    val tuples = unit.reduce((x, y) => x ++ y)
    println(tuples)
  }

  def testJdbcQuarter(odList: java.util.List[GetQuarterPassengerFlow]): Unit = {
    val quarterSectionSaves = mainDistribution.quarterSectionReturn(mainDistribution.quarterIntervalResult(odList))
    val asJava = quarterSectionSaves.asJava
//    quarterSaveEspecially.saveSectionData("2018-09-01 00:00:00", asJava)
  }

  def quarterTransferReturn(pathInfo: (String, String, mutable.Map[Array[DirectedEdge], Double], Int)): List[StoreTransferData] = {
    val directedEdgeBuffer = new ArrayBuffer[(String, String, DirectedEdge, DirectedEdge, DirectedEdge, Double)]()
    val startTime = pathInfo._1
    val timeDiff = pathInfo._4 * 60
    for ((k, v) <- pathInfo._3) {
      val length = k.length - 1
      for (i <- 0 to length) {
        if (k(i).getDirection == "o") {
          val theStart = DateExtendUtil.timeAdditionSecond(startTime, timeDiff * i)
          val theEnd = DateExtendUtil.timeAdditionSecond(theStart, timeDiff)
          val eEdge1: DirectedEdge = k(i - 1)
          val eEdge2: DirectedEdge = k(i)
          val eEdge3: DirectedEdge = k(i + 1)
          val tuple = Tuple6(theStart, theEnd, eEdge1, eEdge2, eEdge3, v)
          directedEdgeBuffer.append(tuple)
        }
      }
    }
    val lineMap = DataBaseLoading.lineMap
    val transferDatas = new util.ArrayList[StoreTransferData]()
    val date = startTime.split(" ")
    val dataDate = DateExtendUtil.stringToDate(date(0), DateExtendUtil.PART)
    for (i <- directedEdgeBuffer) {
      val transferData = new StoreTransferData()
      val transferBefore = i._3.getEdge.getFromNode
      val transferIn = i._4.getEdge.getFromNode
      val transferOut = i._4.getEdge.getToNode
      val transferAfter = i._5.getEdge.getToNode
      val transferInLine = transferBefore + " " + transferIn
      val transferOutLine = transferOut + " " + transferAfter
      val lineInId = lineMap.get(transferInLine)
      val lineOutId = lineMap.get(transferOutLine)
      val direction = i._3.getDirection + "" + i._5.getDirection
      direction match {
        case "11" => transferData.setDownInDownOutNum(i._6)
        case "12" => transferData.setDownInUpOutNum(i._6)
        case "21" => transferData.setUpInDownOutNum(i._6)
        case "22" => transferData.setUpInUpOutNum(i._6)
      }
      transferData.setDate(dataDate)
      transferData.setStartTime(DateExtendUtil.stringToDate(i._1, DateExtendUtil.FULL))
      transferData.setEndTime(DateExtendUtil.stringToDate(i._2, DateExtendUtil.FULL))
      transferData.setTransfer(transferIn)
      transferData.setInLineId(lineInId)
      transferData.setOutLineId(lineOutId)
      transferDatas.add(transferData)
    }
    transferDatas.asScala.toList
  }

  def testQuarterTransferReturn(odList: java.util.List[GetQuarterPassengerFlow]): util.List[StoreTransferData] = {
    val tuples: List[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = quarterIntervalResult(odList)
    val rddTuples = SetRdd.sc.makeRDD(tuples)
    val tupleCompute = (paths: (String, String, mutable.Map[Array[DirectedEdge], Double], Int)) => quarterTransferReturn(paths)
    val rddTupleMaps = rddTuples.map(tupleCompute)
    val rddReduce = rddTupleMaps.reduce((x, y) => x ++ y)
    rddReduce.asJava
  }

  def getQuarterTimeSliceRdd(odList: java.util.List[GetQuarterPassengerFlow]): Array[mutable.Map[(String, String, String, String), Double]] = {
    val quarterSectionSaves = mainDistribution.quarterSectionReturn(mainDistribution.quarterIntervalResult(odList))
    val sectionSaves = quarterSectionSaves
    val theListSize = sectionSaves.size / 120
    println(theListSize)
    val sectionList = sectionSaves.grouped(theListSize).toList
    val rddSection = SetRdd.sc.makeRDD(sectionList)
    val quarterSectionList = rddSection.map((lists: List[QuarterSectionSave]) => MainTransfer.getTimeSliceList("2018-09-01 00:00:00", lists))
    val tupleToDoubles = quarterSectionList.collect()
    tupleToDoubles
  }

  def testQuarterSectionReturn(odList: java.util.List[GetQuarterPassengerFlow]): Unit = {
    val tuples = mainDistribution.quarterIntervalResult(odList)
    val sectionSaves = mainDistribution.quarterSectionReturn(tuples)
    val tupleToDoubles = mainDistribution.getQuarterTimeSliceRdd("2018-09-01 00:00:00", sectionSaves)
    println(tupleToDoubles)
  }

  def oneGetTimeSlice(odList: java.util.List[GetQuarterPassengerFlow]): mutable.Map[(String, String, String, String), Double] = {
    val quarterSectionSaves = mainDistribution.quarterSectionReturn(mainDistribution.quarterIntervalResult(odList))
    val oneSection = SetRdd.sc.makeRDD(quarterSectionSaves)
    val oneSectionMap = oneSection.map(QuarterSectionSave => TimeSlice.getTimeSlice("2018-09-01 00:00:00", QuarterSectionSave))
    val allSectionMaps = oneSectionMap.reduce((x, y) => MainTransfer.quarterMapIntegration(x, y))
    allSectionMaps
  }

  def testOneGetTimeSlice(odList: java.util.List[GetQuarterPassengerFlow]): util.List[StoreSectionPassengers] = {
    val dataDt = "2018-09-01 00:00:00"
    val tupleToDouble = oneGetTimeSlice(odList)
    val saveList = MainTransfer.sectionMapsToEntity(dataDt, tupleToDouble)
    saveList
  }

  def getTimeSliceList(dateTime: String, sectionSaveList: List[QuarterSectionSave]): mutable.Map[(String, String, String, String), Double] = {
    val sectionHashMap = mutable.Map[(String, String, String, String), Double]()
    for (i <- sectionSaveList.indices) {
      val startTime = sectionSaveList(i).getInTime
      val endTime = sectionSaveList(i).getOutTime
      val timeAdd = DateExtendUtil.timeDifference(startTime, endTime, DateExtendUtil.SECEND)
      val middlePoint = DateExtendUtil.timeAdditionSecond(startTime, timeAdd / 2)
      val minutes = DateExtendUtil.timeDifference(dateTime, middlePoint, DateExtendUtil.MINUTE)
      val theMinuteSlice = minutes / 15
      val theSaveStartTime = DateExtendUtil.timeAddition(dateTime, 0, theMinuteSlice * 15)
      val theSaveEndTime = DateExtendUtil.timeAddition(theSaveStartTime, 0, 15)
      val tuple = Tuple4(theSaveStartTime, theSaveEndTime, sectionSaveList(i).getInId, sectionSaveList(i).getOutId)
      if (!sectionHashMap.contains(tuple)) {
        sectionHashMap.put(tuple, sectionSaveList(i).getPassengers)
      } else {
        sectionHashMap.put(tuple, sectionHashMap(tuple) + sectionSaveList(i).getPassengers)
      }
    }
    sectionHashMap
  }

  def getQuarterStationInOut(dataDt: String, odList: java.util.List[GetQuarterPassengerFlow],requestCommand: RequestCommand): Unit ={
    val rdd = SetRdd.sc.makeRDD(odList.asScala)
    val stationRddMap = rdd.map(GetQuarterPassengerFlow => mapStationPassenger(dataDt, GetQuarterPassengerFlow))
    val stationMapS = stationRddMap.reduce((x,y)=>stationMapIntegration(x,y))
    print(stationMapS.size)
    val stationInOutSaves = stationInOutSaveToEntity(stationMapS)
    quarterSaveEspecially.saveStationInOutTimeSliceData(dataDt,stationInOutSaves,requestCommand)
    return stationMapS
  }

  def mapStationPassenger(dataDt: String,od:GetQuarterPassengerFlow): List[mutable.Map[(String,String,String), Int]] ={
    val oStart = od.getInTime
    val inName = od.getInName
    val dStart = od.getOutTime
    val outName = od.getOutName
    val passengers = od.getPassengers
    val oMinute = DateExtendUtil.timeDifference(dataDt, oStart, DateExtendUtil.MINUTE)
    val theQuarterO = oMinute / 15
    val theOStart = DateExtendUtil.timeAddition(dataDt, 0, theQuarterO * 15)
    val dMinute = DateExtendUtil.timeDifference(dataDt, dStart, DateExtendUtil.MINUTE)
    val theQuarterD = dMinute / 15
    val theDStart = DateExtendUtil.timeAddition(dataDt, 0, theQuarterD * 15)
    val theOEnd = DateExtendUtil.timeAddition(theOStart, 0, 15)
    val theDEnd = DateExtendUtil.timeAddition(theDStart, 0, 15)
    val stationIn = Tuple3(theOStart, theOEnd,inName)
    val stationOut = Tuple3(theDStart,theDEnd, outName)
    val stationMapIn = mutable.Map[(String,String,String), Int]()
    val stationMapOut = mutable.Map[(String,String,String), Int]()
    stationMapIn.put(stationIn,passengers)
    stationMapOut.put(stationOut,passengers)
    val stationList = List(stationMapIn, stationMapOut)
    return stationList
  }

  def stationMapIntegration(list1: List[mutable.Map[(String,String,String), Int]],list2: List[mutable.Map[(String,String,String), Int]]): List[mutable.Map[(String,String,String), Int]] ={
    val stationInMap = list1.head
    val stationOutMap = list1.tail.head
    val stationInMap2 = list2.head
    val stationOutMap2 = list2.tail.head
    for ((k,v)<-stationInMap2){
      if (stationInMap.contains(k)){
        stationInMap.put(k,stationInMap(k)+v)
      }
      else {
        stationInMap.put(k,v)
      }
    }
    for ((k,v)<-stationOutMap2){
      if (stationOutMap.contains(k)){
        stationOutMap.put(k,stationOutMap(k)+v)
      }
      else {
        stationOutMap.put(k,v)
      }
    }
    return List(stationInMap,stationOutMap)
  }

  def stationInOutSaveToEntity(stationInOutList:List[mutable.Map[(String,String,String), Int]]): util.ArrayList[StationInOutSave] ={
    val inMap = stationInOutList.head
    val outMap = stationInOutList(1)
    val inOutSaves = new util.ArrayList[StationInOutSave]()
    val stationCompose =mutable.Map[(String, String, String), (Int, Int)]()
    for ((k,v)<-inMap){
      stationCompose.put(k,(v,0))
    }
    for ((k,v)<-outMap){
      if(stationCompose.contains(k)){
        stationCompose.put(k,(stationCompose(k)._1,v))
      }else{
        stationCompose.put(k,(0,v))
      }
    }
    for((k,v)<-stationCompose){
      val startTime = k._1
      val endTime = k._2
      val stationName = k._3
      val inPassengers = v._1
      val outPassengers = v._2
      val stationInOutSave = new StationInOutSave(startTime,stationName,endTime,inPassengers,outPassengers)
    inOutSaves.add(stationInOutSave)
    }
    return inOutSaves;
  }

}

