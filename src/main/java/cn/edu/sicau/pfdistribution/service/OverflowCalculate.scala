package cn.edu.sicau.pfdistribution.service

import cn.edu.sicau.pfdistribution.entity.correct.{OverflowData, PassengerFlow}
import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow

trait OverflowCalculate {
  def odQuarterDistributionResult(PassengerFlow: PassengerFlow):OverflowData
}
