package cn.edu.sicau.pfdistribution.service

import cn.edu.sicau.pfdistribution.entity.DirectedEdge
import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow
import cn.edu.sicau.pfdistribution.entity.jiaodaTest.{OdWithPath, OdWithTimeAndPath}

import scala.collection.mutable

trait BetterCalculate {
  def odQuarterDistributionResult(getQuarterPassengerFlow: GetQuarterPassengerFlow): (String, String, mutable.Map[Array[DirectedEdge], Double], Int)

  def odQuarterDistributionResultDynamic(getQuarterPassengerFlow: GetQuarterPassengerFlow): (String, String, mutable.Map[Array[DirectedEdge], Double], Int)

  def dynamicOdPathSearch(targetOd: String): OdWithPath

  def dynamicOdDistributionResult(targetOd: String, odMap: mutable.Map[String, Integer]): mutable.Map[Array[DirectedEdge], Double]
}
