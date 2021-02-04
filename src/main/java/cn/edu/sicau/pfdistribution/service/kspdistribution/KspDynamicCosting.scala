package cn.edu.sicau.pfdistribution.service.kspdistribution

import java.io._

import cn.edu.sicau.pfdistribution.Utils.DataBaseLoading
import cn.edu.sicau.pfdistribution.entity.{DirectedEdge, LineIdAndSectionTime, StationAndSectionPassengers}
import cn.edu.sicau.pfdistribution.service.kspcalculation.Edge
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.mutable

@Service
class KspDynamicCosting @Autowired()(val getParameter: GetParameter, getParameters: GetParameters, val stationAndSectionPassengers: StationAndSectionPassengers, lineIdAndSectionTime: LineIdAndSectionTime) extends Serializable {

  def stationOperatingCosts(beforeSite: String, currentSite: String): Double = {
    val a = getParameters.getA()
    val b = getParameters.getB() //校正系数
    val str = beforeSite + " " + currentSite
    //区间起点终点
    val seat = getParameter.getSeat() //列车座位数
    val max_p = getParameter.getMaxPassengers() //列车最大乘客数
    var crowded_degree: Double = 0
    //拥挤度
    var cost: Double = 0
    val mapPassengers: mutable.Map[String, java.util.List[String]] = stationAndSectionPassengers.getSectionP.asScala
    if (mapPassengers != null && mapPassengers.contains(str)) {
      val mapList: mutable.Buffer[String] = mapPassengers(str).asScala
      val s: String = mapList.head
      val passengers = s.toDouble
      //区间载客量
      //val passengers = getParameter.intervalPassenger()
      if (passengers <= seat)
        crowded_degree = 0
      else if (passengers >= seat && passengers <= max_p)
        crowded_degree = a * (passengers - seat) / seat
      else
        crowded_degree = a * (passengers - seat) / seat + b * (seat - max_p) / max_p
    }
    cost = getParameter.getTwoSiteTime(beforeSite, currentSite) * (1 + crowded_degree) + getParameter.getSiteStopTime(beforeSite) * (1 + crowded_degree)
    return cost
  }

  def transferFee(): Double = {
    var cost = 24 * 60 //换乘费用
    /* cost= getParameter.transferTime()*/
    return cost
  }

  def perceivedCosts(beforeSite: String, currentSite: String): Double = {
    //站台感知费用
    /*
    具体计算预留
     */
    //val perceived:Double=0
    val mapTime: mutable.Map[Integer, java.util.List[String]] = lineIdAndSectionTime.getSectionTime.asScala
    var t: Double = 5 * 60
    val I: Double = 360
    val Fl: Double = 1 / I //线路发车频率
    val Cl: Double = 500
    val a = getParameters.getA()
    val b = getParameters.getB() //校正系数
    var sectionPassengers: Double = 500
    var stationPassengers: Double = 500
    var Cost: Double = 0
    if (!mapTime.isEmpty && lineIdAndSectionTime.getSectionTime.containsKey(beforeSite.toInt) && lineIdAndSectionTime.getSectionTime.containsKey(currentSite.toInt)) {
      val mapTimeList: mutable.Buffer[String] = mapTime(beforeSite.toInt).asScala
      val mapTimeList1: mutable.Buffer[String] = mapTime(currentSite.toInt).asScala
      val beforeTime = mapTimeList.tail.head
      val currentTime = mapTimeList1.head
      t = changeTime(beforeTime, currentTime)
    }
    val mapSa: mutable.Map[String, java.util.List[String]] = stationAndSectionPassengers.getStationP.asScala
    val mapSe: mutable.Map[String, java.util.List[String]] = stationAndSectionPassengers.getSectionP.asScala
    if (mapSa != null && mapSa.contains(currentSite)) {
      val mapSaList: mutable.Buffer[String] = mapSa(currentSite).asScala
      stationPassengers = mapSaList.tail.head.toDouble
      //val crowded_degree=mapSaList.head
    }
    if (mapSe != null && mapSe.contains(currentSite)) {
      val mapSeList: mutable.Buffer[String] = mapSe(currentSite).asScala
      sectionPassengers = mapSeList.tail.head.toDouble
    }
    if (Cl - sectionPassengers / (Fl * t) >= stationPassengers)
      Cost = 0.5 * I
    else if (Cl - sectionPassengers / (Fl * t) < stationPassengers)
      Cost = 0.5 * I + a * Math.pow((stationPassengers / t) / (Fl * Cl - sectionPassengers / t), b)
    //val inPlatformCost = 5*60
    // 进站站台感知费用
    return Cost
  }

  def costCount(kspArray: mutable.Map[Array[DirectedEdge], Double]): mutable.Map[Array[DirectedEdge], Double] = {
    val sectionRunTime = DataBaseLoading.sectionRunTime
    var DynamicArray: mutable.Map[Array[DirectedEdge], Double] = mutable.Map()
    val CZMap: mutable.Map[Integer, Integer] = lineIdAndSectionTime.getStationIdToLineId.asScala
    for (key <- kspArray.keys) {
      var count: Double = 0
      var n: Int = 1
      //判断路径有无换乘和换乘次数
      for (i <- 0 to (key.length - 2)) {
        val dEdge1: DirectedEdge = key(i)
        val eg1: Edge = dEdge1.getEdge
        val dEdge2: DirectedEdge = key(i + 1)
        val eg2: Edge = dEdge2.getEdge
        val a = eg1.getFromNode
        val b = eg2.getToNode
        if (CZMap(a.toInt) != CZMap(b.toInt)) {
          n = n + 1
        }
      }
      //计算总费用
      for (i <- 0 to (key.length - 2)) {
        val dEdge3: DirectedEdge = key(i)
        val eg3: Edge = dEdge3.getEdge
        val a = eg3.getFromNode
        val b = eg3.getToNode
        val routeCost = sectionRunTime.get(a + " " + b)
        val platformPerceptionCost = perceivedCosts(a, b)
        count += stationOperatingCosts(a, b) + platformPerceptionCost + routeCost
      }
      count += n * transferFee()
      DynamicArray += (key -> (count / 60.0))
    }
    return DynamicArray
  }
  def changeTime(before: String, after: String): Double = {
    //格式为小时.分钟.秒的字符串转化为Double类型的秒
    val beforeArray = before.split("\\.")
    val afterArray = after.split("\\.")
    var before1: String = beforeArray(0)
    var after1: String = afterArray(0)
    if (before == "-0")
      before1 = "0"
    if (after1 == "-0")
      after1 = "0"
    val beTime = before1.toDouble * 3600 + beforeArray(1).toDouble * 60 + beforeArray(2).toDouble
    val afTime = after1.toDouble * 3600 + afterArray(1).toDouble * 60 + afterArray(2).toDouble
    val time = afTime - beTime
    return time
  }
}
