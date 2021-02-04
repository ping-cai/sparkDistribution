package cn.edu.sicau.pfdistribution.Utils

import java.util
import scala.collection.mutable
import cn.edu.sicau.pfdistribution.entity.jiaoda.QuarterSectionSave

object TimeSlice {
  def getTimeSliceList(dateTime: String, sectionSaveList: util.List[QuarterSectionSave]): List[mutable.Map[(String, String, String, String), Double]] = {
    val sectionHashMap = mutable.Map[(String, String, String, String), Double]()
    for (i <- 0 until sectionSaveList.size()) {
      val startTime = sectionSaveList.get(i).getInTime
      val endTime = sectionSaveList.get(i).getOutTime
      val timeAdd = DateExtendUtil.timeDifference(startTime, endTime, DateExtendUtil.SECEND)
      val middlePoint = DateExtendUtil.timeAdditionSecond(startTime, timeAdd / 2)
      val minutes = DateExtendUtil.timeDifference(dateTime, middlePoint, DateExtendUtil.MINUTE)
      val theMinuteSlice = minutes / 15
      val theSaveStartTime = DateExtendUtil.timeAddition(dateTime, 0, theMinuteSlice * 15)
      val theSaveEndTime = DateExtendUtil.timeAddition(theSaveStartTime, 0, 15)
      val tuple = Tuple4(theSaveStartTime, theSaveEndTime, sectionSaveList.get(i).getInId, sectionSaveList.get(i).getOutId)
      if (!sectionHashMap.contains(tuple)) {
        sectionHashMap.put(tuple, sectionSaveList.get(i).getPassengers)
      } else {
        sectionHashMap.put(tuple, sectionHashMap(tuple) + sectionSaveList.get(i).getPassengers)
      }
    }
    List(sectionHashMap)
  }

  def getTimeSlice(dateTime: String, sectionSave: QuarterSectionSave): mutable.Map[(String, String, String, String), Double] = {
    val startTime = sectionSave.getInTime
    val endTime = sectionSave.getOutTime
    val timeAdd = DateExtendUtil.timeDifference(startTime, endTime, DateExtendUtil.SECEND)
    val middlePoint = DateExtendUtil.timeAdditionSecond(startTime, timeAdd / 2)
    val minutes = DateExtendUtil.timeDifference(dateTime, middlePoint, DateExtendUtil.MINUTE)
    val theMinuteSlice = minutes / 15
    val theSaveStartTime = DateExtendUtil.timeAddition(dateTime, 0, theMinuteSlice * 15)
    val theSaveEndTime = DateExtendUtil.timeAddition(theSaveStartTime, 0, 15)
    val tuple = Tuple4(theSaveStartTime, theSaveEndTime, sectionSave.getInId, sectionSave.getOutId)
    val sectionHashMap = mutable.Map[(String, String, String, String), Double]()
    sectionHashMap.put(tuple, sectionSave.getPassengers)
    sectionHashMap
  }

  def main(args: Array[String]): Unit = {
    val dateTime = "2018-09-01 00:00:00"
    val sectionSave = new QuarterSectionSave("2018-09-01 09:15:46", "2018-09-01 09:33:21", "1", "2", 1.1)
    val startTime = sectionSave.getInTime
    val endTime = sectionSave.getOutTime
    val timeAdd = DateExtendUtil.timeDifference(startTime, endTime, DateExtendUtil.SECEND)
    val middlePoint = DateExtendUtil.timeAdditionSecond(startTime, timeAdd / 2)
    val minutes = DateExtendUtil.timeDifference(dateTime, middlePoint, DateExtendUtil.MINUTE)
    val theMinuteSlice = minutes / 15
    val theSaveStartTime = DateExtendUtil.timeAddition(dateTime, 0, theMinuteSlice * 15)
    val theSaveEndTime = DateExtendUtil.timeAddition(theSaveStartTime, 0, 15)
    val theLastSave = new QuarterSectionSave(theSaveStartTime, theSaveEndTime, sectionSave.getInId, sectionSave.getOutId, sectionSave.getPassengers)
    println(theLastSave)

    val ints = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)
    val list = ints.grouped(6).toList
    println(list)
  }


}
