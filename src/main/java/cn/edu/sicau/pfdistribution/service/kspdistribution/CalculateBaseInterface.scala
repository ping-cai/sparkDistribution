package cn.edu.sicau.pfdistribution.service.kspdistribution

import cn.edu.sicau.pfdistribution.entity.jiaoda.{GetQuarterPassengerFlow, QuarterSectionSave}
import cn.edu.sicau.pfdistribution.entity.{DirectedEdge, DirectedPath}

import scala.collection.mutable
import scala.collection.mutable.Map

trait CalculateBaseInterface {
  /**
    *
    * @param targetOd (进站 出站)字符串
    * @return K短路集
    */
  def staticOdPathSearch(targetOd: String): mutable.Map[Array[DirectedEdge], Double]

  /**
    *
    * @param targetOd (进站 出站)字符串
    * @return 动态路径搜索集合
    */
  def dynamicOdPathSearch(targetOd: String): mutable.Map[Array[DirectedEdge], String]

  //计算单个OD对的OD分配结果
  def kspDistribution(map: Map[Array[DirectedEdge], Double], x: Int): mutable.Map[Array[DirectedEdge], Double]

  def odDistributionResult(targetOd: String, odMap: mutable.Map[String, Integer]): mutable.Map[Array[DirectedEdge], Double]

  //调用KSP算法和distribution，迭代计算各个OD对的分配结果(以动态费用分配)
  def dynamicOdDistributionResult(targetOd: String, odMap: mutable.Map[String, Integer]): mutable.Map[Array[DirectedEdge], Double]

  //将分配到各个路径下的结果划分到区间上，返回区间断面图（未考虑时间，每个OD都能到达）
  def odRegion(map: mutable.Map[Array[DirectedEdge], Double]): mutable.Map[String, Double]

  //计算站点进出站人数
  def stationInAndOutP(map: mutable.Map[Array[DirectedEdge], Double]): List[mutable.Map[String, Double]]

  //计算换乘人数
  def transferPassengers(map: mutable.Map[Array[DirectedEdge], Double]): mutable.Map[String, Double]

  //15分钟路径分配计算
  def odQuarterDistributionResult(getQuarterPassengerFlow: GetQuarterPassengerFlow): List[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)]

  def quarterGetSectionResult(pathInfo: (String, String, mutable.Map[Array[DirectedEdge], Double], Int)): List[QuarterSectionSave]

  def odQuarterDistributionResultDynamic(getQuarterPassengerFlow: GetQuarterPassengerFlow): List[(String, String, mutable.Map[Array[DirectedEdge], Double], Int)]
}
