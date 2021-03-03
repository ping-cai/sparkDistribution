package cn.edu.sicau.pfdistribution.service

import java.util
import java.util.List

import cn.edu.sicau.pfdistribution.entity.Command
import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow

import scala.collection.mutable


/**
  * 分配算法抽象特质
  * 分配模块，属于业务逻辑的上一层抽象
  */
trait OptimizeDistribution {

  def distributeOneHour(command: Command): mutable.Map[String, Integer]

  def distributeHalfHour(command: Command): mutable.Map[String, Integer]

  def distributeQuarter(command: Command): util.List[GetQuarterPassengerFlow]
}
