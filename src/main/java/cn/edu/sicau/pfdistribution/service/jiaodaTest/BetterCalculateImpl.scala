package cn.edu.sicau.pfdistribution.service.jiaodaTest

import java.util

import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow
import cn.edu.sicau.pfdistribution.entity.jiaodaTest.OdWithPath
import cn.edu.sicau.pfdistribution.entity.{DirectedEdge, DirectedPath, Risk}
import cn.edu.sicau.pfdistribution.service.BetterCalculate
import cn.edu.sicau.pfdistribution.service.kspdistribution.{CalculateBaseImplementation, KspDynamicCosting}
import cn.edu.sicau.pfdistribution.service.road.KServiceImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.Map

/**
  * @Author LiYongPing
  * @Date 2021-02-02
  * @LastUpdate 2021-02-02
  */
@Service
class BetterCalculateImpl @Autowired()(kServiceImpl: KServiceImpl, calculateBaseImplementation: CalculateBaseImplementation, risk: Risk, dynamicCosting: KspDynamicCosting) extends BetterCalculate with Serializable {
  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  override def odQuarterDistributionResult(getQuarterPassengerFlow: GetQuarterPassengerFlow): (String, String, mutable.Map[Array[DirectedEdge], Double], Int) = {
    val targetOd = getQuarterPassengerFlow.getInName + " " + getQuarterPassengerFlow.getOutName
    val OD = mutable.Map(targetOd -> targetOd)
    val ksp1 = kServiceImpl.computeStatic(OD.asJava, "PARAM_NAME", "RETURN_ID")
    val ksp = ksp1.get(targetOd)
    if (ksp == null) {
      logger.warn("错误OD{}", targetOd)
      return null
    }
    val passengers = getQuarterPassengerFlow.getPassengers.toInt
    val it = ksp.iterator()
    var text: mutable.Map[Iterator[DirectedEdge], Double] = mutable.Map()
    var text1: mutable.Map[Array[DirectedEdge], Double] = mutable.Map()
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
    val edgesToDouble = calculateBaseImplementation.kspDistribution(pathArray, passengers)
    Tuple4(getQuarterPassengerFlow.getInTime, getQuarterPassengerFlow.getOutTime, edgesToDouble, getQuarterPassengerFlow.getMinutes)
  }

  /**
    *
    * @param getQuarterPassengerFlow 从数据库中获取的原始待分配OD数据
    * @return 要进行拆解操作的OD
    */
  override def odQuarterDistributionResultDynamic(getQuarterPassengerFlow: GetQuarterPassengerFlow): (String, String, mutable.Map[Array[DirectedEdge], Double], Int) = {
    val targetOd = getQuarterPassengerFlow.getInName + " " + getQuarterPassengerFlow.getOutName
    val OD = mutable.Map(targetOd -> targetOd)
    val ksp1 = kServiceImpl.computeDynamic(OD.asJava, "PARAM_NAME", "RETURN_ID", risk)
    val ksp = ksp1.get(targetOd)
    if (ksp == null) {
      logger.warn("错误OD{}", targetOd)
      return null
    }
    val passengers = getQuarterPassengerFlow.getPassengers.toInt
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
    val edgesToDouble = calculateBaseImplementation.kspDistribution(dynamicCosting.costCount(pathArray), passengers)
    Tuple4(getQuarterPassengerFlow.getInTime, getQuarterPassengerFlow.getOutTime, edgesToDouble, getQuarterPassengerFlow.getMinutes)
  }

  override def dynamicOdPathSearch(targetOd: String): OdWithPath = {
    val OD = mutable.Map(targetOd -> targetOd)
    val ksp1 = kServiceImpl.computeDynamic(OD.asJava, "PARAM_NAME", "RETURN_ID", risk)
    val ksp = ksp1.get(targetOd)
    val od = targetOd.split(" ")
    val origin = od(0)
    val destination = od(1)
    new OdWithPath(origin, destination, ksp)
  }
}
