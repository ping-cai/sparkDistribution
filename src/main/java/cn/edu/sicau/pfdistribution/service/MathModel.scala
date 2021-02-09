package cn.edu.sicau.pfdistribution.service

import java.util

import cn.edu.sicau.pfdistribution.entity.{DirectedEdge, DirectedPath}

import scala.collection.mutable
import scala.collection.mutable.Map

trait MathModel {
  /**
    * 两点间的时间距离计算模型
    *
    * @param minutes          OD相差时间
    * @param distance         OD之间的距离，通常是用路径的所有区间作为距离，每条路径的距离都不同
    * @param conversionWeight 时间与距离的转换权值
    * @return 时间距离
    */
  def distanceFormula(minutes: Double, distance: Double, conversionWeight: Double): Double

  /**
    * 计算单个OD对的OD分配结果
    *
    * @param map 路径以及每条路径的总费用
    * @param x   乘客人数
    * @return
    */
  def kspDistribution(map: Map[Array[DirectedEdge], Double], x: Int): mutable.Map[Array[DirectedEdge], Double]

  def distanceCostTime(ksp: util.List[DirectedPath], minutes: Double): mutable.Map[Array[DirectedEdge], Double]
}
