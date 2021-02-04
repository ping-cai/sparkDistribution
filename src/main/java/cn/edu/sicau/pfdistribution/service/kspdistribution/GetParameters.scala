package cn.edu.sicau.pfdistribution.service.kspdistribution

import java.io.Serializable

import org.springframework.stereotype.Service

@Service
class GetParameters extends Serializable {
  //  a,b为参数，a一般取0.15,b一般取4。
  private var A: Double = 0.15
  private var B = 4.0
  private var coefficient = 3.2

  def getA(): Double = {
    return A
  }

  def getB(): Double = {
    return B
  }

  def getDistributionCoefficient(): Double = {
    return coefficient
  }

  def setA(a: Double) {
    this.A = a
  }

  def setB(b: Double) {
    this.B = b
  }

  def setCoefficient(coefficient: Double) {
    this.coefficient = coefficient
  }

}
