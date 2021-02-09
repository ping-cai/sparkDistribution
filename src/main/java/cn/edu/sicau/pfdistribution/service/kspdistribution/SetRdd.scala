package cn.edu.sicau.pfdistribution.service.kspdistribution

import java.util
import java.util.logging.Logger

import cn.edu.sicau.pfdistribution.Utils.{CommonMethod, DataBaseLoading, NameToID}
import cn.edu.sicau.pfdistribution.dao.Impl.{MysqlGetID, RoadDistributionDaoImpl}
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.GetPassengerFlowOracle
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.{HalfSavePassengerFlow, OneSavePassengerFlow, QuarterSaveEspecially}
import cn.edu.sicau.pfdistribution.entity._
import cn.edu.sicau.pfdistribution.entity.jiaoda._
import cn.edu.sicau.pfdistribution.exceptionhandle.CheckService
import cn.edu.sicau.pfdistribution.service.jiaodaTest.MathModelImpl
import cn.edu.sicau.pfdistribution.service.kspcalculation.{Edge, Graph, KSPUtil, Node}
import cn.edu.sicau.pfdistribution.service.road.{KServiceImpl, PathCheckServiceImpl}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object SetRdd {
  val conf = new SparkConf().setMaster("local[*]").setAppName("PfAllocationApp")
  conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  conf.registerKryoClasses(Array(classOf[DirectedEdge], classOf[DirectedPath], classOf[KspDynamicCosting], classOf[GetParameters],
    classOf[LineIdAndSectionTime], classOf[Risk], classOf[SectionRisk], classOf[StationAndSectionPassengers],
    classOf[StationRisk], classOf[TongHaoPathType], classOf[TongHaoReturnResult],
    classOf[GetQuarterPassengerFlow], classOf[QuarterSectionSave], classOf[RequestCommand],
    classOf[StoreTransferData], classOf[TransferPassengers], classOf[TransferPoint],
    classOf[CalculateBaseImplementation], classOf[DataDeal], classOf[GetLineID],
    classOf[GetOdList], classOf[GetParameter], classOf[MainDistribution], classOf[NameToID],
    classOf[CommonMethod], classOf[KServiceImpl], classOf[PathCheckServiceImpl],
    classOf[MysqlGetID], classOf[RoadDistributionDaoImpl], classOf[GetPassengerFlowOracle],
    classOf[HalfSavePassengerFlow], classOf[OneSavePassengerFlow], classOf[QuarterSaveEspecially],
    classOf[Edge], classOf[Graph], classOf[KSPUtil], classOf[Node], classOf[CheckService],
    classOf[MathModelImpl]))
  val sc = new SparkContext(conf)
  sc.setLogLevel("WARN")

  def sectionRdd(edgeMapRDD: RDD[mutable.Map[Array[DirectedEdge], Double]]): util.ArrayList[SectionPassengers] = {
    val sectionChange = edgeMapRDD.map(sectionMapChange)
    val sectionRed = sectionChange.reduce(sectionRddRed)
    sectionMapToEntity(sectionRed)
  }

  def sectionMapChange(edgeMap: mutable.Map[Array[DirectedEdge], Double]): mutable.Map[(String, String), Double] = {
    val sectionTuple = mutable.Map[(String, String), Double]()
    for ((k, v) <- edgeMap) {
      var passenger = v
      if (v.isNaN) {
        passenger = 0
      }
      for (station <- k) {
        if (sectionTuple.contains((station.getEdge.getFromNode, station.getEdge.getToNode))) {
          sectionTuple.put((station.getEdge.getFromNode, station.getEdge.getToNode), sectionTuple((station.getEdge.getFromNode, station.getEdge.getToNode)) + passenger)
        } else {
          sectionTuple.put((station.getEdge.getFromNode, station.getEdge.getToNode), passenger)
        }
      }
    }
    sectionTuple
  }

  def sectionRddRed(sectionMap1: mutable.Map[(String, String), Double], sectionMap2: mutable.Map[(String, String), Double]): mutable.Map[(String, String), Double] = {
    for ((k, v) <- sectionMap2) {
      if (sectionMap1.contains(k)) {
        sectionMap1.put(k, sectionMap1(k) + v)
      } else {
        sectionMap1.put(k, v)
      }
    }
    sectionMap1
  }

  def sectionMapToEntity(map: mutable.Map[(String, String), Double]): util.ArrayList[SectionPassengers] = {
    val sectionPassengers = new util.ArrayList[SectionPassengers](10000)
    for ((k, v) <- map) {
      sectionPassengers.add(new SectionPassengers(k._1, k._2, v))
    }
    sectionPassengers
  }

  def transferRdd(edgeMapRDD: RDD[mutable.Map[Array[DirectedEdge], Double]]): util.ArrayList[TransferPassengers] = {
    val transferChange = edgeMapRDD.map(transferMapChange)
    val transferRed = transferChange.reduce(transferRddRed)
    transferMapToEntity(transferRed)
  }

  def transferMapChange(edgeMap: mutable.Map[Array[DirectedEdge], Double]): mutable.Map[(String, String, String), (Double, Double, Double, Double)] = {
    val directedEdgeBuffer = new ArrayBuffer[(DirectedEdge, DirectedEdge, Double)]()
    for ((k, v) <- edgeMap) {
      var passengers = v
      if (v.isNaN) {
        passengers = 0.0
      }
      for (i <- k.indices) {
        if ("o".equals(k(i).getDirection)) {
          if (!"o".equals(k(i - 1).getDirection) && !"o".equals(k(i + 1).getDirection)) {
            val eEdge1: DirectedEdge = k(i - 1)
            val eEdge2: DirectedEdge = k(i + 1)
            val tuple = Tuple3(eEdge1, eEdge2, passengers)
            directedEdgeBuffer.append(tuple)
          }
        }
      }

    }
    val transferTuple = mutable.Map[(String, String, String), (Double, Double, Double, Double)]()
    val lineMap = DataBaseLoading.lineMap
    for (i <- directedEdgeBuffer) {
      val beforeIn = i._1.getEdge.getFromNode
      val in = i._1.getEdge.getToNode
      val out = i._2.getEdge.getFromNode
      val after = i._2.getEdge.getToNode
      val transferInLine = lineMap.get(beforeIn + " " + in)
      val transferOutLine = lineMap.get(out + " " + after)
      val direction = i._1.getDirection + "" + i._2.getDirection
      val transferInfo = Tuple3(in, transferInLine, transferOutLine)
      if (transferTuple.contains(transferInfo)) {
        val t1 = transferTuple(transferInfo)._1
        val t2 = transferTuple(transferInfo)._2
        val t3 = transferTuple(transferInfo)._3
        val t4 = transferTuple(transferInfo)._4
        direction match {
          case "11" => transferTuple.put(transferInfo, (t1, t2, t3, t4 + i._3))
          case "12" => transferTuple.put(transferInfo, (t1, t2, t3 + i._3, t4))
          case "21" => transferTuple.put(transferInfo, (t1, t2 + i._3, t3, t4))
          case "22" => transferTuple.put(transferInfo, (t1 + i._3, t2, t3, t4))
        }
      } else {
        direction match {
          case "11" => transferTuple.put(transferInfo, (0.0, 0.0, 0.0, i._3))
          case "12" => transferTuple.put(transferInfo, (0.0, 0.0, i._3, 0.0))
          case "21" => transferTuple.put(transferInfo, (0.0, i._3, 0.0, 0.0))
          case "22" => transferTuple.put(transferInfo, (i._3, 0.0, 0.0, 0.0))
        }
      }
    }
    transferTuple
  }

  def transferRddRed(transferMap1: mutable.Map[(String, String, String), (Double, Double, Double, Double)], transferMap2: mutable.Map[(String, String, String), (Double, Double, Double, Double)])
  : mutable.Map[(String, String, String), (Double, Double, Double, Double)] = {
    for ((k, v) <- transferMap2) {
      if (transferMap1.contains(k)) {
        transferMap1.put(k, (transferMap1(k)._1 + v._1, transferMap1(k)._2 + v._2, transferMap1(k)._3 + v._3, transferMap1(k)._4 + v._4))
      } else {
        transferMap1.put(k, v)
      }
    }
    transferMap1
  }

  def transferMapToEntity(map: mutable.Map[(String, String, String), (Double, Double, Double, Double)]): util.ArrayList[TransferPassengers] = {
    val transferPSGS = new util.ArrayList[TransferPassengers](10000)
    for ((k, v) <- map) {
      transferPSGS.add(new TransferPassengers(k._1, k._2, k._3, v._1, v._2, v._3, v._4))
    }
    transferPSGS
  }

  def stationRdd(odRdd: RDD[String], odMap: mutable.Map[String, Integer]): util.ArrayList[StationPassengers] = {
    val stationToMap = odRdd.map(stationMap(_, odMap))
    val stationRed = stationToMap.reduce(stationMapRed)
    stationMapToEntity(stationRed)
  }

  def stationMap(targetOd: String, odMap: mutable.Map[String, Integer]): mutable.Map[String, (Int, Int)] = {
    val passengers = odMap(targetOd)
    val stations = targetOd.split(" ")
    val inStation = stations(0)
    val outStation = stations(1)
    val stationPassengers = mutable.Map[String, (Int, Int)]()
    stationPassengers.put(inStation, (passengers, 0))
    stationPassengers.put(outStation, (0, passengers))
    stationPassengers
  }

  def stationMapRed(stationMap1: mutable.Map[String, (Int, Int)], stationMap2: mutable.Map[String, (Int, Int)]): mutable.Map[String, (Int, Int)] = {
    for ((k, v) <- stationMap2) {
      if (stationMap1.contains(k)) {
        stationMap1.put(k, (stationMap1(k)._1 + v._1, stationMap1(k)._2 + v._2))
      } else {
        stationMap1.put(k, v)
      }
    }
    stationMap1
  }

  def stationMapToEntity(map: mutable.Map[String, (Int, Int)]): util.ArrayList[StationPassengers] = {
    val stationList = new util.ArrayList[StationPassengers]()
    for ((k, v) <- map) {
      val station = new StationPassengers(k, v._1, v._2)
      stationList.add(station)
    }
    stationList
  }
}
