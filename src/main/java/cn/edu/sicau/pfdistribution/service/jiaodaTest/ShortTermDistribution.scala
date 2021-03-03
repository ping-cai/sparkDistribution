package cn.edu.sicau.pfdistribution.service.jiaodaTest

import java.util

import cn.edu.sicau.pfdistribution.dao.tonghaoGet.GetPassengerFlowOracle
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.jiaodaTest.OracleTestDataGet
import cn.edu.sicau.pfdistribution.entity.Command
import cn.edu.sicau.pfdistribution.entity.jiaoda.{GetQuarterPassengerFlow, RequestCommand}
import cn.edu.sicau.pfdistribution.service.OptimizeDistribution
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.mutable
import scala.collection.JavaConverters._

/**
  * 获取不同数据源的数据
  * 传递给下一层
  *
  * @param getPassengerFlowOracle 传入来自oracle的数据源
  */
@Service
class ShortTermDistribution @Autowired()(@transient getPassengerFlowOracle: OracleTestDataGet, betterDistribution: BetterDistribution) extends OptimizeDistribution {
  override def distributeOneHour(command: Command): mutable.Map[String, Integer] = {
    getPassengerFlowOracle.oneHourGet(command.getDateDt, command.getStartTime, command.getEndTime).asScala
  }

  override def distributeHalfHour(command: Command): mutable.Map[String, Integer] = {
    getPassengerFlowOracle.halfHourGet(command.getDateDt, command.getStartTime, command.getEndTime).asScala
  }

  override def distributeQuarter(command: Command): util.List[GetQuarterPassengerFlow] = {
    val passengerFlows = getPassengerFlowOracle.quarterHourNoInclude(command.getStartTime, command.getEndTime)
    betterDistribution.lastAllSectionResult(command.getTargetTable, passengerFlows, command)
    passengerFlows
  }
}
