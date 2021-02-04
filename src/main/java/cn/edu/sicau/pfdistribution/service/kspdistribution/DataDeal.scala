package cn.edu.sicau.pfdistribution.service.kspdistribution

import java.util

import cn.edu.sicau.pfdistribution.Utils.Constants
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.{HalfSavePassengerFlow, OneSavePassengerFlow}
import cn.edu.sicau.pfdistribution.entity.jiaoda.{RequestCommand, StoreTransferData}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.mutable


@Service
class DataDeal @Autowired()(val getOdList: GetOdList, @transient oneSavePassengerFlow: OneSavePassengerFlow, @transient halfSavePassengerFlow: HalfSavePassengerFlow) extends Serializable {
  /**
    * od矩阵处理测试
    *
    * @param data
    */
  def sectionDataSave(data: mutable.Map[String, Double], requestCommand: RequestCommand): Unit = {
    for (key <- data.keys) {
      val v = data(key)
      val passengers = v
      val section = key.split(" ")
      val section_in = section(0)
      val section_out = section(1)
      getOdList.saveOD(requestCommand.getDateDt, section_in, section_out, requestCommand.getStartTime, requestCommand.getEndTime, passengers, requestCommand)
    }
  } //od矩阵处理测试
  /**
    * od矩阵处理测试
    *
    * @param data List(Map(进站id,人数),Map(出站id,人数))
    */
  def stationInAndOut(data: List[mutable.Map[String, Double]], requestCommand: RequestCommand): Unit = {
    val stationIn = data.head
    val stationOut = data.tail.head
    var in_passengers: Double = 0
    var out_passengers: Double = 0
    for (key <- stationIn.keys) {
      val station = key
      if (stationIn.contains(key)) {
        in_passengers = stationIn(key)
      } else {
        in_passengers = 0
      }
      if (stationOut.contains(key)) {
        out_passengers = stationOut(key)
      } else {
        out_passengers = 0
      }
      getOdList.saveStationPassengers(requestCommand.getDateDt, requestCommand.getStartTime, requestCommand.getEndTime, station, in_passengers, out_passengers, requestCommand)
    }
  }

  /**
    *
    * @param arrayList 换乘详细数据List集合
    */
  def saveTransferLineInfo(arrayList: util.ArrayList[StoreTransferData], requestCommand: RequestCommand): Unit = {
    if (requestCommand.getTimeInterval == 60) {
      oneSavePassengerFlow.saveTransferInfo(requestCommand.getDateDt, requestCommand.getStartTime, requestCommand.getEndTime, arrayList, requestCommand)
    }
    if (requestCommand.getTimeInterval == 30) {
      halfSavePassengerFlow.saveTransferInfo(requestCommand.getDateDt, requestCommand.getStartTime, requestCommand.getEndTime, arrayList, requestCommand)
    }
  }
}

object DataDeal {
  def dataTransfer(hour: Int, minute: Int): String = {
    var Hour: String = ""
    var Minute: String = ""
    if (hour < 10)
      Hour = "0" + hour.toString
    else Hour = hour.toString

    if (minute < 10)
      Minute = "0" + minute.toString
    else Minute = minute.toString
    val Date: String = Hour + ":" + Minute + ":" + "00"
    return Date
  }
}
