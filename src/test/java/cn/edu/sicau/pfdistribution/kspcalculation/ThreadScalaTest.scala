package cn.edu.sicau.pfdistribution.kspcalculation

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class ThreadScalaTest {
  @Async("dataStorage")
  def toTestThread(): Unit = {
    println(Thread.currentThread().getName + " " + 100)
  }
}
