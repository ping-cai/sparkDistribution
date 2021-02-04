package cn.edu.sicau.pfdistribution.service.kspdistribution

import java.io.Serializable

import org.springframework.stereotype.Service

import scala.collection.JavaConverters._

@Service
class GetParameter extends Serializable{


  //获得OD列表

  def getOdList():List[String] ={
    val odList:List[String] = List("20 20 1000") //"牛角沱 较场口 1000"
    return odList
  }

  def getOdMap():java.util.Map[String, String]={
    val odMap:Map[String, String] = Map("20 42" -> "1000") //,"璧山 较场口" -> 1000,"红岩村 较场口" -> 1000
    return odMap.asJava
  }

/*  def getTime():Int={   //返回定义的区间粒度时间
    val int_Time: Int = 15
    return int_Time
  }*/

  //获得列车最大座位数

  def getSeat():Int = {
    val seat = 200
    return seat
  }
  //列车最大乘客数

  def getMaxPassengers():Int={
    val maxPassengers = 1900
    return maxPassengers
  }
  //获得区间载客量

  def intervalPassenger():Int={
    val passengers = 1000
    return passengers
  }
  //获得站点停站时间

  def getSiteStopTime(currentSite: String):Int={
    val stopTime:Int = 2 * 60 //秒
    return stopTime
  }

  def getTwoSiteTime(beforeSite: String,currentSite: String):Int={
    val time:Int = 5*60
    return time
  }

  //获得换乘时间

  def transferTime():Int={  //具体参数需确认
    val time = 24*60
    return time
  }

}
