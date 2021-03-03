package cn.edu.sicau.pfdistribution.service.kspdistribution

import java.util
import java.util.Date

import cn.edu.sicau.pfdistribution.Utils.{DataBaseLoading, DateExtendUtil}
import cn.edu.sicau.pfdistribution.entity.DirectedEdge
import cn.edu.sicau.pfdistribution.entity.jiaoda._
import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer
import scala.collection.{immutable, mutable}
import scala.collection.JavaConverters._

object MainTransfer {
  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  def transfer(map: immutable.Map[String, Integer]): mutable.Map[String, Integer] = {
    mutable.Map(map.toSeq: _*)
  }

  def odListToOdMap(odList: List[String]): mutable.Map[String, String] = {
    var transfer: mutable.Map[String, String] = mutable.Map()
    for (i <- odList.toArray) {
      val od = i.split(" ")
      val str = od(0) + " " + od(1)
      if (od(0) != od(1)) {
        if (transfer.contains(str)) {
          val v1: Int = transfer(str).toInt
          val v2: Int = od(2).toInt
          val valueAll = v1 + v2
          transfer += (str -> valueAll.toString)
        } else
          transfer += (str -> od(2))
      }
    }
    return transfer
  }

  def odMapToJavaOdMap(odMap: mutable.Map[String, String]): java.util.Map[String, String] = {
    var transfer: Map[String, String] = Map()
    for (key <- odMap.keys) {
      val od = key.split(" ")
      transfer += (od(0) -> od(1))
    }
    return transfer.asJava
  }

  def odListTransfer(map: mutable.Map[String, Integer]): List[String] = {
    var odMap: Map[String, String] = Map()
    for (key <- map.keys) {
      val str = key + " " + map(key)
      odMap += (key -> str)
    }
    return odMap.values.toList
  }

  def odMapTransfer(map: mutable.Map[String, Integer]): java.util.Map[String, String] = {
    var transfer: Map[String, String] = Map()
    for (key <- map.keys) {
      val OD = key.split(" ")
      if (OD(0) != OD(1))
        if (transfer.contains(key)) {
          val v1: Int = transfer(key).toInt
          val v2: Int = map(key)
          val valueAll = v1 + v2
          transfer += (key -> valueAll.toString)
        } else
          transfer += (key -> map(key).toString)
    }
    return transfer.asJava
  }

  def odMapTransferScala(map: mutable.Map[String, Integer]): mutable.Map[String, Integer] = {
    var transfer: mutable.Map[String, Integer] = mutable.Map()
    for (key <- map.keys) {
      val OD = key.split(" ")
      if (OD(0) != OD(1))
        transfer += (key -> map(key))
    }
    return transfer
  }

  def mapTransfer(map: mutable.Map[String, Double]): mutable.Map[String, String] = {
    var transfer: mutable.Map[String, String] = mutable.Map()
    if (map == null) {
      return null
    } else {
      for (key <- map.keys) {
        val K: Int = map(key).toInt
        val V: String = K.toString
        transfer += (key -> V)
      }
    }
    return transfer
  }

  def displayResult(data: mutable.Map[String, Double]): Unit = {
    for (key <- data.keys) {
      logger.info("section:{}", key)
      logger.info("passengers:{}", data(key).toInt)
    }
  }

  /** 对换乘线路等情况进行处理
    *
    * @author LiYongPing
    * @param rddIntegration :OD分配结果的RDD的整合
    * @return JavaArrayList<TransferPoint>
    */
  def transferSituation(rddIntegration: mutable.Map[Array[DirectedEdge], Double]): util.ArrayList[TransferPoint] = {
    val directedEdgeBuffer = new ArrayBuffer[(DirectedEdge, DirectedEdge, DirectedEdge, Double)]()
    for ((k, v) <- rddIntegration) {
      val length = k.length - 1
      for (i <- 0 to length) {
        if (k(i).getDirection == "o") {
          val eEdge1: DirectedEdge = k(i - 1)
          val eEdge2: DirectedEdge = k(i)
          val eEdge3: DirectedEdge = k(i + 1)
          val tuple = Tuple4(eEdge1, eEdge2, eEdge3, v)
          directedEdgeBuffer.append(tuple)
        }
      }
    }
    val transferPoints = new util.ArrayList[TransferPoint]()
    for (i <- directedEdgeBuffer) {
      val point = new TransferPoint()
      point.setTransferBefore(i._1.getEdge.getFromNode)
      point.setTransferPointBefore(i._2.getEdge.getFromNode)
      point.setTransferPointAfter(i._2.getEdge.getToNode)
      point.setTransferAfter(i._3.getEdge.getToNode)
      point.setDirection(i._1.getDirection + "" + i._3.getDirection)
      point.setTransferFlows(i._4)
      transferPoints.add(point)
    }
    transferPoints
  }

  /**
    * 不推荐使用的区间时间整合15分钟的算法，数据量过大
    *
    * @author LiYongPing
    * @param dateTime    :数据日期
    * @param sectionSave :Od分配后的区间断面客流
    * @return ：区间分配List结果
    */
  def getTimeSlice(dateTime: String, sectionSave: QuarterSectionSave): List[QuarterSectionSave] = {
    val startTime = sectionSave.getInTime
    val endTime = sectionSave.getOutTime
    val timeAdd = DateExtendUtil.timeDifference(startTime, endTime, DateExtendUtil.SECEND)
    val middlePoint = DateExtendUtil.timeAdditionSecond(startTime, timeAdd / 2)
    val minutes = DateExtendUtil.timeDifference(dateTime, middlePoint, DateExtendUtil.MINUTE)
    val theMinuteSlice = minutes / 15
    val theSaveStartTime = DateExtendUtil.timeAddition(dateTime, 0, theMinuteSlice * 15)
    val theSaveEndTime = DateExtendUtil.timeAddition(theSaveStartTime, 0, 15)
    val theLastSave = new QuarterSectionSave(theSaveStartTime, theSaveEndTime, sectionSave.getInId, sectionSave.getOutId, sectionSave.getPassengers)
    List(theLastSave)
  }

  /**
    * @author LiYongPing
    * @param dateTime        :数据开始时间
    * @param sectionSaveList ：区间分配的部分结果集合
    * @return 包含不重复时间和区间的区间分配叠加的Map集合
    */
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

  /**
    * Map集合的整合
    *
    * @author LiYongPing
    * @author LiYongPing
    * @param sectionMap1 ：待整合Map1
    * @param sectionMap2 ：待整合Map2
    * @return
    */
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
    * Map集合转化为区间断面流量数据库实例
    *
    * @author LiYongPing
    * @param dataDt      ：数据日期时间
    * @param sectionMaps ：待插入数据库的Map集合
    * @return
    */
  def sectionMapsToEntity(dataDt: String, sectionMaps: mutable.Map[(String, String, String, String), Double]): java.util.List[StoreSectionPassengers] = {
    val sectionIdMap = DataBaseLoading.sectionIdMap
    val arrayList = new util.ArrayList[StoreSectionPassengers]()
    for ((k, v) <- sectionMaps) {
      var double: Double = v
      val sectionId = sectionIdMap.get(k._3 + " " + k._4)
      if (double.isNaN) {
        double = 0.0
      }
      val sectionPassengers = new StoreSectionPassengers(dataDt, sectionId, k._3, k._4, k._1, k._2, double)
      arrayList.add(sectionPassengers)
    }
    arrayList
  }

  /**
    * @author LiYongPing
    * @param pathInfo :单个OD分配的路径包含时间的4元组
    * @return
    */
  def quarterTransferReturn(pathInfo: (String, String, mutable.Map[Array[DirectedEdge], Double], Int)): List[StoreTransferData] = {
    val directedEdgeBuffer = new ArrayBuffer[(String, String, DirectedEdge, DirectedEdge, DirectedEdge, Double)]()
    val startTime = pathInfo._1
    for ((k, v) <- pathInfo._3) {
      val timeDiff = (pathInfo._4 * 60) / k.length
      val length = k.length - 1
      for (i <- 0 to length) {
        try{
        if (k(i).getDirection == "o") {
          if (!"o".equals(k(i - 1).getDirection) && !"o".equals(k(i + 1).getDirection)) {
            val theStart = DateExtendUtil.timeAdditionSecond(startTime, timeDiff * i)
            val theEnd = DateExtendUtil.timeAdditionSecond(theStart, timeDiff)
            val eEdge1: DirectedEdge = k(i - 1)
            val eEdge2: DirectedEdge = k(i)
            val eEdge3: DirectedEdge = k(i + 1)
            val tuple = Tuple6(theStart, theEnd, eEdge1, eEdge2, eEdge3, v)
            directedEdgeBuffer.append(tuple)
          }
        }
      }catch {
          case e:Exception=>{
            println(pathInfo)
          }
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

  /**
    * @author LiYongPing
    * @param dateTime 日期时间
    * @param list     换乘详细数据实体List集合
    * @return 包含各类换乘方向的人数的元组信息的Map集合
    *         给下一步的两个Map的人数整合做准备
    */
  def transferGetTimeSlice(dateTime: String, list: List[StoreTransferData]): mutable.Map[(Date, Date, Date, String, String, String), (Double, Double, Double, Double)] = {
    val storeTransferDataMap = mutable.Map[(Date, Date, Date, String, String, String), (Double, Double, Double, Double)]()
    val theDateTime = DateExtendUtil.stringToDate(dateTime, DateExtendUtil.FULL)
    for (i <- list.indices) {
      val date = list(i).getDate
      val startTime = list(i).getStartTime
      val endTime = list(i).getEndTime
      val timeAdd = DateExtendUtil.dateDiff(startTime, endTime, DateExtendUtil.SECEND)
      val middlePoint = DateExtendUtil.dateAdditionSecond(startTime, timeAdd / 2)
      val minutes = DateExtendUtil.dateDiff(theDateTime, middlePoint, DateExtendUtil.MINUTE)
      val theMinuteSlice = minutes / 15
      val theSaveStartTime = DateExtendUtil.dateAddition(theDateTime, 0, theMinuteSlice * 15)
      val theSaveEndTime = DateExtendUtil.dateAddition(theSaveStartTime, 0, 15)
      val tuple6 = Tuple6(date, theSaveStartTime, theSaveEndTime, list(i).getTransfer, list(i).getInLineId, list(i).getOutLineId)
      var UU = list(i).getUpInUpOutNum
      if (UU.isNaN) {
        UU = 0.0
      }
      var UD = list(i).getUpInDownOutNum
      if (UD.isNaN) {
        UD = 0.0
      }
      var DU = list(i).getDownInUpOutNum
      if (DU.isNaN) {
        DU = 0.0
      }
      var DD = list(i).getDownInDownOutNum
      if (DD.isNaN) {
        DD = 0.0
      }
      if (storeTransferDataMap.contains(tuple6)) {
        storeTransferDataMap.put(tuple6, (storeTransferDataMap(tuple6)._1 + UU, storeTransferDataMap(tuple6)._2 + UD, storeTransferDataMap(tuple6)._3 + DU, storeTransferDataMap(tuple6)._4 + DD))
        storeTransferDataMap(tuple6)._1
      } else {
        storeTransferDataMap.put(tuple6, (UU, UD, DU, DD))
      }
    }
    storeTransferDataMap
  }

  /**
    *
    * 承接上面的transferGetTimeSlice的结果
    *
    * @author LiYongPing
    * @param map1 包含各类换乘方向的人数的元组信息的Map集合1
    * @param map2 包含各类换乘方向的人数的元组信息的Map集合2
    * @return 两个集合的Map集合人数整合信息
    *         给下一步的Map转Entity实体做准备
    */
  def transferMapIntegration(map1: mutable.Map[(Date, Date, Date, String, String, String), (Double, Double, Double, Double)], map2: mutable.Map[(Date, Date, Date, String, String, String), (Double, Double, Double, Double)]): mutable.Map[(Date, Date, Date, String, String, String), (Double, Double, Double, Double)] = {
    for ((k, v) <- map2) {
      if (map1.contains(k)) {
        map1.put(k, (map1(k)._1 + v._1, map1(k)._2 + v._2, map1(k)._3 + v._3, map1(k)._4 + v._4))
      } else {
        map1.put(k, v)
      }
    }
    map1
  }

  /**
    * @author LiYongPing
    * @param allTransferMap 承接上面的transferMapIntegration的结果
    * @return 换乘详细数据实体，给数据库存储做准备
    */
  def transferMapToEntity(allTransferMap: mutable.Map[(Date, Date, Date, String, String, String), (Double, Double, Double, Double)]): util.List[StoreTransferData] = {
    val transferDataS = new util.ArrayList[StoreTransferData]
    for ((k, v) <- allTransferMap) {
      val transferData = new StoreTransferData(k._1, k._2, k._3, k._4, k._5, k._6, v._1, v._2, v._3, v._4)
      transferDataS.add(transferData)
    }
    transferDataS
  }

  /**
    * @author ZhouZhiYuan
    * @param dataDt 数据时间日期
    * @param od     数据库的一条记录
    * @return 进出站的List<Map>集合，给下一步stationMapIntegration方法的进出站信息整合做准备
    */
  def mapStationPassenger(dataDt: String, od: GetQuarterPassengerFlow): List[mutable.Map[(String, String, String), Int]] = {
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
    val stationIn = Tuple3(theOStart, theOEnd, inName)
    val stationOut = Tuple3(theDStart, theDEnd, outName)
    val stationMapIn = mutable.Map[(String, String, String), Int]()
    val stationMapOut = mutable.Map[(String, String, String), Int]()
    stationMapIn.put(stationIn, passengers)
    stationMapOut.put(stationOut, passengers)
    val stationList = List(stationMapIn, stationMapOut)
    stationList
  }

  /**
    * 承接上面mapStationPassenger的结果
    *
    * @author ZhouZhiYuan
    * @param list1 结果集1
    * @param list2 结果集2
    * @return 两结果集的人数整合，为转化为Entity做准备
    */
  def stationMapIntegration(list1: List[mutable.Map[(String, String, String), Int]], list2: List[mutable.Map[(String, String, String), Int]]): List[mutable.Map[(String, String, String), Int]] = {
    val stationInMap = list1.head
    val stationOutMap = list1.tail.head
    val stationInMap2 = list2.head
    val stationOutMap2 = list2.tail.head
    for ((k, v) <- stationInMap2) {
      if (stationInMap.contains(k)) {
        stationInMap.put(k, stationInMap(k) + v)
      }
      else {
        stationInMap.put(k, v)
      }
    }
    for ((k, v) <- stationOutMap2) {
      if (stationOutMap.contains(k)) {
        stationOutMap.put(k, stationOutMap(k) + v)
      }
      else {
        stationOutMap.put(k, v)
      }
    }
    List(stationInMap, stationOutMap)
  }

  /**
    * 承接stationMapIntegration方法的结果集
    *
    * @author ZhouZhiYuan
    * @param stationInOutList 上述方法得到的结果集
    * @return 转换为StationInOutSave进出站存储实体集
    */
  def stationInOutSaveToEntity(stationInOutList: List[mutable.Map[(String, String, String), Int]]): util.ArrayList[StationInOutSave] = {
    val inMap = stationInOutList.head
    val outMap = stationInOutList(1)
    val inOutSaves = new util.ArrayList[StationInOutSave]()
    val stationCompose = mutable.Map[(String, String, String), (Int, Int)]()
    for ((k, v) <- inMap) {
      stationCompose.put(k, (v, 0))
    }
    for ((k, v) <- outMap) {
      if (stationCompose.contains(k)) {
        stationCompose.put(k, (stationCompose(k)._1, v))
      } else {
        stationCompose.put(k, (0, v))
      }
    }
    for ((k, v) <- stationCompose) {
      val startTime = k._1
      val endTime = k._2
      val stationName = k._3
      val inPassengers = v._1
      val outPassengers = v._2
      val stationInOutSave = new StationInOutSave(startTime, endTime, stationName, inPassengers, outPassengers)
      inOutSaves.add(stationInOutSave)
    }
    inOutSaves
  }


}
