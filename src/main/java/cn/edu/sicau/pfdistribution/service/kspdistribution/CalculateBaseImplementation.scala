package cn.edu.sicau.pfdistribution.service.kspdistribution

import java.io._
import java.{lang, util}

import cn.edu.sicau.pfdistribution.Utils.DateExtendUtil
import cn.edu.sicau.pfdistribution.entity.jiaoda.{GetQuarterPassengerFlow, QuarterSectionSave}
import cn.edu.sicau.pfdistribution.entity.{DirectedEdge, DirectedPath, LineIdAndSectionTime, Risk}
import cn.edu.sicau.pfdistribution.service.kspcalculation.Edge
import cn.edu.sicau.pfdistribution.service.netrouter.JsonTransfer
import cn.edu.sicau.pfdistribution.service.road.KServiceImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.Map

@Service
class CalculateBaseImplementation @Autowired()(dynamicCosting: KspDynamicCosting, getParameter: GetParameter, getParameters: GetParameters, kServiceImpl: KServiceImpl, jsonTransfer: JsonTransfer, risk: Risk,
                                               lineIdAndSectionTime: LineIdAndSectionTime) extends CalculateBaseInterface with Serializable {
  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  override def staticOdPathSearch(targetOd: String): mutable.Map[Array[DirectedEdge], Double] = {
    val aList = targetOd.split(" ")
    val sou = aList(0)
    val tar = aList(1)
    val OD: Map[String, String] = Map(targetOd -> targetOd)
    val ksp1: util.Map[String, util.List[DirectedPath]] = kServiceImpl.computeStatic(OD.asJava, "PARAM_NAME", "RETURN_ID")
    val ksp: util.List[DirectedPath] = ksp1.get(targetOd)
    if (ksp == null) {
      logger.info("错误OD:{}", targetOd)
      return null
    }
    val iter = ksp.iterator()
    var text: mutable.Map[Iterator[DirectedEdge], Double] = mutable.Map()
    var text1: mutable.Map[Array[DirectedEdge], Double] = mutable.Map()
    while (iter.hasNext) {
      val p = iter.next()
      //      一条路径的站点构成
      val nodesIter = p.getEdges.iterator()
      text += (nodesIter.asScala -> p.getTotalCost) //静态费用
    }
    for (key <- text.keys) {
      val myArray = key.toArray
      text1 += (myArray -> text.apply(key))
    }

    return text1
  }

  override def dynamicOdPathSearch(targetOd: String): mutable.Map[Array[DirectedEdge], String] = {
    //    OD进行切分
    val aList = targetOd.split(" ")
    val sou = aList(0)
    val tar = aList(1)
    val OD: Map[String, String] = Map(targetOd -> targetOd)
    val ksp1: util.Map[String, util.List[DirectedPath]] = kServiceImpl.computeDynamic(OD.asJava, "PARAM_NAME", "RETURN_ID", risk)
    val ksp: util.List[DirectedPath] = ksp1.get(targetOd)
    if (ksp == null) {
      logger.info("错误OD:{}", targetOd)
      return null
    } else {
      val iter = ksp.iterator()
      var text: mutable.Map[Iterator[DirectedEdge], Double] = mutable.Map()
      var text1: mutable.Map[Array[DirectedEdge], Double] = mutable.Map()
      while (iter.hasNext) {
        val p = iter.next()
        //      一条路径的站点构成
        val nodesIter = p.getEdges.iterator()
        text += (nodesIter.asScala -> p.getTotalCost) //静态费用
      }
      for (key <- text.keys) {
        val myArray = key.toArray
        text1 += (myArray -> text.apply(key))
      }
      val dynamicCost = dynamicCosting.costCount(text1)
      var distanceAndTimeCost: mutable.Map[Array[DirectedEdge], String] = mutable.Map()
      for (key <- text1.keys) {
        val str: String = text1(key).toString + " " + (dynamicCost(key).toInt / 60).toString
        distanceAndTimeCost += (key -> str)
      }
      return distanceAndTimeCost

    }
  }

  //logic模型
  override def kspDistribution(map: Map[Array[DirectedEdge], Double], x: Int): mutable.Map[Array[DirectedEdge], Double] = {
    val e = Math.E
    val Q = -1 * getParameters.getDistributionCoefficient() //分配系数
    var p = 0.0
    var fenMu = 0.0
    val probability_Passenger = new Array[Double](1000)
    val costMin = map.values.min
    val kspMap = scala.collection.mutable.Map[Array[DirectedEdge], Double]()
    for (value <- map.values) {
      //分配概率
      fenMu = fenMu + Math.pow(e, Q * value / costMin)
    }
    var count = 0
    for (value <- map.values) {
      p = Math.pow(e, Q * value / costMin) / fenMu
      val kspPassenger = x.asInstanceOf[Double] * p //计算人数
      probability_Passenger(count) = kspPassenger
      count = count + 1
    }
    val keys = map.keySet
    var count1 = 0
    for (key <- keys) {
      kspMap += (key -> probability_Passenger(count1))
      count1 = count1 + 1
    }
    return kspMap
  }

  override def odDistributionResult(targetOd: String, odMap: mutable.Map[String, Integer]): mutable.Map[Array[DirectedEdge], Double] = {
    val OD: mutable.Map[String, String] = mutable.Map(targetOd -> targetOd)
    val ksp1: util.Map[String, util.List[DirectedPath]] = kServiceImpl.computeStatic(OD.asJava, "PARAM_NAME", "RETURN_ID")
    val ksp: util.List[DirectedPath] = ksp1.get(targetOd)
    if (ksp == null) {
      logger.warn("错误OD{}", targetOd)
      return null
    }
    val passengers: Int = odMap(targetOd).toInt
    val iter = ksp.iterator()
    var text: mutable.Map[Iterator[DirectedEdge], Double] = mutable.Map()
    var text1: mutable.Map[Array[DirectedEdge], Double] = mutable.Map()
    while (iter.hasNext) {
      val p = iter.next()
      //      一条路径的站点构成
      val nodesIter = p.getEdges.iterator()
      text += (nodesIter.asScala -> p.getTotalCost) //静态费用
    }
    //      println(text.toList)
    for (key <- text.keys) {
      val myArray = key.toArray
      text1 += (myArray -> text.apply(key))
    }
    return kspDistribution(text1, passengers)
  }

  //动态路径分配
  override def dynamicOdDistributionResult(targetOd: String, odMap: mutable.Map[String, Integer]): mutable.Map[Array[DirectedEdge], Double] = {
    val OD: Map[String, String] = Map(targetOd -> targetOd)
    val ksp1: util.Map[String, util.List[DirectedPath]] = kServiceImpl.computeDynamic(OD.asJava, "PARAM_NAME", "RETURN_ID", risk)
    val ksp: util.List[DirectedPath] = ksp1.get(targetOd)
    if (ksp == null) {
      logger.warn("错误OD{}", targetOd)
      return null
    }
    val passengers: Int = odMap(targetOd).toInt
    val iter = ksp.iterator()
    var text: mutable.Map[Iterator[DirectedEdge], Double] = mutable.Map()
    var text1: mutable.Map[Array[DirectedEdge], Double] = mutable.Map()
    while (iter.hasNext) {
      val p = iter.next()
      //      一条路径的站点构成
      val nodesIter = p.getEdges.iterator()
      text += (nodesIter.asScala -> p.getTotalCost) //静态费用
    }
    for (key <- text.keys) {
      val myArray = key.toArray
      text1 += (myArray -> text.apply(key))
    }
    return kspDistribution(dynamicCosting.costCount(text1), passengers)
  }

  override def odRegion(map: mutable.Map[Array[DirectedEdge], Double]): mutable.Map[String, Double] = {
    val odMap = scala.collection.mutable.Map[String, Double]()
    for (key <- map.keys) {
      for (i <- key.indices) {
        val dEdge: DirectedEdge = key(i)
        val edge: Edge = dEdge.getEdge
        val str = edge.getFromNode + " " + edge.getToNode
        if (odMap.contains(str)) {
          odMap += (str -> (map(key) + odMap(str)))
        }
        else {
          odMap += (str -> map(key))
        }
      }
    }
    return odMap
  }

  //站点进出站人数
  override def stationInAndOutP(map: mutable.Map[Array[DirectedEdge], Double]): List[mutable.Map[String, Double]] = {
    val stationIn = scala.collection.mutable.Map[String, Double]()
    val stationOut = scala.collection.mutable.Map[String, Double]()
    for (key <- map.keys) {
      val dEdge1: DirectedEdge = key(0)
      val eg1: Edge = dEdge1.getEdge
      val dEdge2: DirectedEdge = key(key.length - 1)
      val eg2: Edge = dEdge2.getEdge
      val inStation = eg1.getFromNode
      val outStation = eg2.getToNode
      if (stationIn.contains(inStation)) {
        stationIn += (inStation -> (map(key) + stationIn(inStation)))
      }
      else {
        stationIn += (inStation -> map(key))
      }
      if (stationOut.contains(outStation)) {
        stationOut += (outStation -> (map(key) + stationOut(outStation)))
      }
      else {
        stationOut += (outStation -> map(key))
      }
    }
    val dataList: List[Map[String, Double]] = List(stationIn, stationOut)
    return dataList
  }

  //换乘人数
  override def transferPassengers(map: mutable.Map[Array[DirectedEdge], Double]): mutable.Map[String, Double] = {
    val transferPassengerMap = scala.collection.mutable.Map[String, Double]()
    val CZMap: mutable.Map[Integer, Integer] = lineIdAndSectionTime.getStationIdToLineId.asScala
    for (key <- map.keys) {
      for (i <- 0 to (key.length - 2)) {
        val dEdge1: DirectedEdge = key(i)
        val eg1: Edge = dEdge1.getEdge
        val dEdge2: DirectedEdge = key(i + 1)
        val eg2: Edge = dEdge2.getEdge
        val a = eg1.getFromNode
        val b = eg2.getToNode
        if (CZMap(a.toInt) != CZMap(b.toInt)) {
          val station = eg1.getToNode
          if (transferPassengerMap.contains(station)) {
            transferPassengerMap += (station -> (map(key) + transferPassengerMap(station)))
          } else
            transferPassengerMap += (station -> map(key))
        }
      }
    }
    return transferPassengerMap
  }


  override def odQuarterDistributionResult(getQuarterPassengerFlow: GetQuarterPassengerFlow): List[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = {
    val targetOd = getQuarterPassengerFlow.getInName + " " + getQuarterPassengerFlow.getOutName
    val OD: mutable.Map[String, String] = mutable.Map(targetOd -> targetOd)
    val ksp1: util.Map[String, util.List[DirectedPath]] = kServiceImpl.computeStatic(OD.asJava, "PARAM_NAME", "RETURN_ID")
    val ksp: util.List[DirectedPath] = ksp1.get(targetOd)
    if (ksp == null) {
      logger.warn("错误OD{}", targetOd)
      return null
    }
    val passengers: Int = getQuarterPassengerFlow.getPassengers.toInt
    val it = ksp.iterator()
    var text: mutable.Map[Iterator[DirectedEdge], Double] = mutable.Map()
    var pathArray: mutable.Map[Array[DirectedEdge], Double] = mutable.Map()
    while (it.hasNext) {
      val p = it.next()
      //      一条路径的站点构成
      val nodesIt = p.getEdges.iterator()
      //      println("费用:"  + p.getTotalCost)
      text += (nodesIt.asScala -> p.getTotalCost) //静态费用
    }
    for (key <- text.keys) {
      val myArray = key.toArray
      pathArray += (myArray -> text.apply(key))
    }
    val edgesToDouble: mutable.Map[Array[DirectedEdge], Double] = kspDistribution(pathArray, passengers)
    List(Tuple4(getQuarterPassengerFlow.getInTime, getQuarterPassengerFlow.getOutTime, edgesToDouble, getQuarterPassengerFlow.getMinutes))
  }

  /**
    *
    * @param pathInfo 起止时间，完整路径分配信息，起止时间差
    * @return 路径拆解后每一个区间的通过起止时间以及人流量
    */
  override def quarterGetSectionResult(pathInfo: (String, String, mutable.Map[Array[DirectedEdge], Double], Int)): List[QuarterSectionSave] = {
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
    *
    * @param getQuarterPassengerFlow 从数据库中获取的原始待分配OD数据
    * @return 要进行拆解操作的OD
    */
  override def odQuarterDistributionResultDynamic(getQuarterPassengerFlow: GetQuarterPassengerFlow): List[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)] = {
    val targetOd = getQuarterPassengerFlow.getInName + " " + getQuarterPassengerFlow.getOutName
    val OD: mutable.Map[String, String] = mutable.Map(targetOd -> targetOd)
    val ksp1: util.Map[String, util.List[DirectedPath]] = kServiceImpl.computeDynamic(OD.asJava, "PARAM_NAME", "RETURN_ID", risk)
    val ksp: util.List[DirectedPath] = ksp1.get(targetOd)
    if (ksp == null) {
      logger.warn("错误OD{}", targetOd)
      return null
    }
    val passengers: Int = getQuarterPassengerFlow.getPassengers.toInt
    val it = ksp.iterator()
    var text: mutable.Map[Iterator[DirectedEdge], Double] = mutable.Map()
    var pathArray: mutable.Map[Array[DirectedEdge], Double] = mutable.Map()
    while (it.hasNext) {
      val p = it.next()
      //      一条路径的站点构成
      val nodesIt = p.getEdges.iterator()
      text += (nodesIt.asScala -> p.getTotalCost) //静态费用
    }
    for (key <- text.keys) {
      val myArray = key.toArray
      pathArray += (myArray -> text.apply(key))
    }
    val edgesToDouble: mutable.Map[Array[DirectedEdge], Double] = kspDistribution(dynamicCosting.costCount(pathArray), passengers)
    List(Tuple4(getQuarterPassengerFlow.getInTime, getQuarterPassengerFlow.getOutTime, edgesToDouble, getQuarterPassengerFlow.getMinutes))
  }
}
