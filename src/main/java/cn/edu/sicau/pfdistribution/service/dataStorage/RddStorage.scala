package cn.edu.sicau.pfdistribution.service.dataStorage

import java.util

import cn.edu.sicau.pfdistribution.dao.betterSave.BatchSave
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.QuarterSaveEspecially
import cn.edu.sicau.pfdistribution.entity.jiaoda._
import cn.edu.sicau.pfdistribution.service.kspdistribution.{CalculateBaseInterface, DataDeal, MainTransfer}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

import scala.collection.mutable

@Service
class RddStorage @Autowired()(calBase: CalculateBaseInterface, dataDeal: DataDeal, @transient quarterSaveEspecially: QuarterSaveEspecially, batchSave: BatchSave) extends Serializable {
  private[this] val logger = LoggerFactory.getLogger(this.getClass)

  @Async("dataStorage")
  def StationInAndOutStorage(requestCommand: RequestCommand, stationList: util.ArrayList[StationPassengers]): Unit = {
    batchSave.saveStation(requestCommand, stationList)
    logger.info("车站进出量数据存储完毕！日期为{}", requestCommand.getStartTime + "," + requestCommand.getEndTime)
  }

  @Async("dataStorage")
  def sectionStorage(requestCommand: RequestCommand, sectionList: util.ArrayList[SectionPassengers]): Unit = {
    batchSave.saveSection(requestCommand, sectionList)
    logger.info("车站区间断面数据存储完毕！日期为{}", requestCommand.getStartTime + "," + requestCommand.getEndTime)
  }

  def transferLineStorage(requestCommand: RequestCommand, transferList: util.ArrayList[TransferPassengers]): Unit = {
    batchSave.saveTransfer(requestCommand, transferList)
    logger.info("换乘客流数据存储完毕！日期为{}", requestCommand.getStartTime + "," + requestCommand.getEndTime)
  }

  /**
    *
    * @param dataDt         数据的大致日期
    * @param sectionMaps    分配后的区间集合
    * @param requestCommand 请求命令
    */
  def quarterSectionStorage(dataDt: String, sectionMaps: mutable.Map[(String, String, String, String), Double], requestCommand: RequestCommand): Unit = {
    val allSectionEntity = MainTransfer.sectionMapsToEntity(dataDt, sectionMaps)
    quarterSaveEspecially.saveSectionTimeSliceData(allSectionEntity, requestCommand)
  }

  def quarterTransferStorage(transferMaps: util.List[StoreTransferData], requestCommand: RequestCommand): Unit = {
    quarterSaveEspecially.saveTransferTimeSliceData(transferMaps, requestCommand)
  }

  def quarterStationStorage(dataDt: String, stationInOutSaves: util.ArrayList[StationInOutSave], requestCommand: RequestCommand): Unit = {
    quarterSaveEspecially.saveStationInOutTimeSliceData(dataDt, stationInOutSaves, requestCommand)
  }
}
