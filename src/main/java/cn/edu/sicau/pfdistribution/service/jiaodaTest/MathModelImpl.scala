package cn.edu.sicau.pfdistribution.service.jiaodaTest

import java.util

import cn.edu.sicau.pfdistribution.entity.{DirectedEdge, DirectedPath}
import cn.edu.sicau.pfdistribution.service.MathModel
import cn.edu.sicau.pfdistribution.service.kspdistribution.GetParameters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import scala.collection.JavaConverters._
import scala.collection.mutable

@Service
class MathModelImpl @Autowired()(getParameters: GetParameters)() extends MathModel with Serializable {
  /**
    * 两点间的时间距离计算模型
    *
    * @param minutes          OD相差时间
    * @param distance         OD之间的距离，通常是用路径的所有区间作为距离，每条路径的距离都不同
    * @param conversionWeight 时间与距离的转换权值
    * @return 时间距离
    */
  override def distanceFormula(minutes: Double, distance: Double, conversionWeight: Double): Double = {
    val distanceTime = distance * conversionWeight
    Math.pow(minutes - distanceTime, 2)
  }

  /**
    * 计算单个OD对的OD分配结果
    *
    * @param map 路径以及每条路径的总费用
    * @param x   乘客人数
    * @return
    */
  override def kspDistribution(map: mutable.Map[Array[DirectedEdge], Double], x: Int): mutable.Map[Array[DirectedEdge], Double] = {
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
    kspMap
  }

  override def distanceCostTime(ksp: util.List[DirectedPath], minutes: Double): mutable.Map[Array[DirectedEdge], Double] = {
    val scalaKsp = ksp.asScala
    scalaKsp.map(MathModelImpl.listKspToMap).reduce(_ ++ _)
  }
}

object MathModelImpl {
  var conversionWeight = 2.0

  def transferFee(): Double = {
    30
  }

  def judgeTransfer(edge: DirectedEdge): Int = {
    if (edge.getDirection.equals("o")) {
      1
    } else {
      0
    }
  }

  def sectionFee(sectionWeight: Double): Double = {
    sectionWeight * conversionWeight
  }

  def listKspToMap(path: DirectedPath): mutable.Map[Array[DirectedEdge], Double] = {
    val edges = path.getEdges
    val edgesScala = edges.asScala
    val sectionFeeCompute = (x: DirectedEdge) =>
      sectionFee(x.getEdge.getWeight) + judgeTransfer(x) * transferFee()
    val feeSum = edgesScala.map(sectionFeeCompute).sum
    val directedEdges = edgesScala.toArray
    mutable.Map(directedEdges -> feeSum)
  }

  def distanceFormula(minutes: Double, distance: Double, conversionWeight: Double): Double = {
    val distanceTime = distance * conversionWeight
    Math.pow(minutes - distanceTime, 2)
  }
}
