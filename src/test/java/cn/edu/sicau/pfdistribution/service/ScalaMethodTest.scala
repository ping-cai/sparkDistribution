package cn.edu.sicau.pfdistribution.service

import java.io.Serializable
import java.util
import java.util.Map

import cn.edu.sicau.pfdistribution.Utils.{Constants, DataBaseLoading}
import cn.edu.sicau.pfdistribution.dao.Impl.MysqlGetID
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.GetPassengerFlowOracle
import cn.edu.sicau.pfdistribution.entity.jiaoda.{RequestCommand, TransferPoint}
import cn.edu.sicau.pfdistribution.entity.{DirectedEdge, LineIdAndSectionTime, TongHaoReturnResult}
import cn.edu.sicau.pfdistribution.service.kspdistribution._
import cn.edu.sicau.pfdistribution.service.road.KServiceImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


@Service
case class ScalaMethodTest @Autowired()(calBase: CalculateBaseInterface, getOdList: GetOdList, getParameter: GetParameter, kServiceImpl: KServiceImpl, dataDeal: DataDeal, tongHaoReturnResult: TongHaoReturnResult, getLineID: GetLineID, mysqlGetID: MysqlGetID

                                        , lineIdAndSectionTime: LineIdAndSectionTime, mainDistribution: MainDistribution, getPassengerFlowDao: GetPassengerFlowOracle) extends Serializable {

  //  不可序列化的属性需要
  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  def intervalTriggerTask(args: Map[String, String]): Unit = {
    getLineID.setStationId()
//    val command: String = args("command")
//    val time = args("timeInterval")
    /*重庆样例数据获取
    odMapObtain：获取数据库数据
     */
    val odMapObtain: mutable.Map[String, Integer] = getPassengerFlowDao.oneHourGet(Constants.DATA_DATE_DAY, Constants.SAVE_DATE_START, Constants.SAVE_DATE_TEMP).asScala
    val odMap = MainTransfer.odMapTransferScala(odMapObtain)
    println("日期：" + 6 + 7)
    println("OD条数" + odMap.keys.size)
  }

  //  对换乘线路等情况进行处理
  def transferSituation(rddIntegration: mutable.Map[Array[DirectedEdge], Double]): util.ArrayList[TransferPoint] = {
    val directedEdgeBuffer = new ArrayBuffer[(DirectedEdge, DirectedEdge, DirectedEdge, Double)]()
    for ((k, v) <- rddIntegration) {
      val length = k.length - 1
      for (i <- 0 to length) {
        if (k(i).getDirection == "o") {
          val eEdge1: DirectedEdge = k(i - 1)
          val eEdge2: DirectedEdge = k(i)
          val eEdge3: DirectedEdge = k(i + 1)
          val tuple = Tuple4(eEdge1, eEdge2, eEdge3, v)
          directedEdgeBuffer.append(tuple)
        }
      }
    }
    val transferPoints = new util.ArrayList[TransferPoint]()
    for (i <- directedEdgeBuffer) {
      val point = new TransferPoint()
      point.setTransferBefore(i._1.getEdge.getFromNode)
      point.setTransferPointBefore(i._2.getEdge.getFromNode)
      point.setTransferPointAfter(i._2.getEdge.getToNode)
      point.setTransferAfter(i._3.getEdge.getToNode)
      point.setDirection(i._1.getDirection + "" + i._3.getDirection)
      point.setTransferFlows(i._4)
      transferPoints.add(point)
    }
    transferPoints
  }

  //  返回区间断面的分配结果（静态）
//  def intervalResult(odMap: mutable.Map[String, Integer], requestCommand: RequestCommand): mutable.Map[String, Double] = {
//    val odList: List[String] = odMap.keySet.toList
//    val rdd = SetRdd.sc.makeRDD(odList)
//    val odDistributionRdd = rdd.map(String => calBase.odDistributionResult(String, odMap)) //各个OD的分配结果
//    val rddIntegration = odDistributionRdd.reduce((x, y) => x ++ y) //对OD分配结果的RDD的整合
//    logger.info("ksp:{}", rddIntegration.keys.size)
//    val dataListStation = calBase.stationInAndOutP(rddIntegration) //进出站人数
//    val transferPoints = transferSituation(rddIntegration)
//    val transferDatas = passengerFlowService.stationTransferData(transferPoints)
//    val regionMap = calBase.odRegion(rddIntegration) //各个区间的加和结果
//    logger.info("section:{}", regionMap.keys.size)
//    dataDeal.saveTransferLineInfo(transferDatas, requestCommand) //最新换乘详细信息加和
//    dataDeal.stationInAndOut(dataListStation, requestCommand)
//    dataDeal.sectionDataSave(regionMap, requestCommand)
//    return regionMap
//  }
  def testOdPathSearch(): Unit ={
  getLineID.setStationId()
  val stationNameToId = DataBaseLoading.stationNameToId
  val startStation ="杨公桥"
  val endStation = "石桥铺"
  val startId = stationNameToId.get(startStation)
  val endId = stationNameToId.get(endStation)
  val edgesToString:mutable.Map[Array[DirectedEdge], String] = calBase.dynamicOdPathSearch(startId+" "+endId)
  println(edgesToString)
}

}

