package cn.edu.sicau.pfdistribution.scala

import cn.edu.sicau.pfdistribution.entity.DirectedEdge
import org.apache.spark.rdd.RDD

import scala.collection.mutable

object SparkTest {
  def testRdd(edgeMapRDD: RDD[mutable.Map[Array[DirectedEdge], Double]]): Unit = {
    val edgesToDouble: mutable.Map[Array[DirectedEdge], Double] = edgeMapRDD.reduce(sectionRddReduce)
    var sum: Double = 0.0
    for ((k, v) <- edgesToDouble) {
      for (i <- k) {
        sum = sum + v
      }
    }
    println("分配出来的总人数为：" + sum)
  }

  def sectionRddReduce(sectionMap1: mutable.Map[Array[DirectedEdge], Double], sectionMap2: mutable.Map[Array[DirectedEdge], Double]): mutable.Map[Array[DirectedEdge], Double] = {
    for ((k, v) <- sectionMap2) {
      if (sectionMap1.contains(k)) {
        sectionMap1.put(k, sectionMap1(k) + v)
      } else {
        sectionMap1.put(k, v)
      }
    }
    sectionMap1
  }

}
