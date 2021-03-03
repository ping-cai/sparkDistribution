package cn.edu.sicau.pfdistribution.service.jiaodaTest

import cn.edu.sicau.pfdistribution.entity.{DirectedEdge, Risk}
import cn.edu.sicau.pfdistribution.entity.correct.{OverflowData, PassengerFlow}
import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow
import cn.edu.sicau.pfdistribution.service.OverflowCalculate
import cn.edu.sicau.pfdistribution.service.road.KServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.mutable
import scala.collection.JavaConverters._

@Service
class OdOverflowCalculate @Autowired()(kServiceImpl: KServiceImpl, mathModelImpl: MathModelImpl, risk: Risk) extends OverflowCalculate {
  override def odQuarterDistributionResult(passengerFlow: PassengerFlow): OverflowData = {
    val targetOd = passengerFlow.getInName + " " + passengerFlow.getOutName
    val OD = mutable.Map(targetOd -> targetOd)
    val ksp1 = kServiceImpl.computeDynamic(OD.asJava, "PARAM_NAME", "RETURN_ID", risk)
    val ksp = ksp1.get(targetOd)
    val minutes = passengerFlow.getMinutes
    val edgesAndTimeCost = mathModelImpl.distanceCostTime(ksp, minutes.toDouble)
    val passengers = passengerFlow.getPassengers
    val edgesByLogic = mathModelImpl.kspDistribution(edgesAndTimeCost, passengers.toInt)
    val data = new OverflowData(passengerFlow.getInTime, passengerFlow.getOutTime,
      passengerFlow.getInName, passengerFlow.getOutName,
      passengerFlow.getPassengers, passengerFlow.getMinutes,
      edgesByLogic
    )
    data
  }
}
