package cn.edu.sicau.pfdistribution.kspcalculation

import java.util

import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow
import cn.edu.sicau.pfdistribution.entity.{DirectedEdge, DirectedPath}
import cn.edu.sicau.pfdistribution.service.kspdistribution.CalculateBaseInterface
import cn.edu.sicau.pfdistribution.service.road.KServiceImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.mutable

@Service
class CalculationTest @Autowired()(kServiceImpl: KServiceImpl, calBase: CalculateBaseInterface) extends Serializable {
  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  def odQuarterDistributionResult(getQuarterPassengerFlow: GetQuarterPassengerFlow): List[(String, String, mutable.Map[Array[DirectedEdge], Double])] = {
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
    val edgesToDouble: mutable.Map[Array[DirectedEdge], Double] = calBase.kspDistribution(pathArray, passengers)
    List(Tuple3(getQuarterPassengerFlow.getInTime, getQuarterPassengerFlow.getOutTime, edgesToDouble))
  }


}
