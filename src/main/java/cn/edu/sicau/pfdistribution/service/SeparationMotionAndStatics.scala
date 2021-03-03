package cn.edu.sicau.pfdistribution.service

/**
  * 拆分动态和静态的计算
  * 动静分离
  * 动态分配和静态分配分离
  * 这个属于业务逻辑和计算的底层模块，也是拿到数据库的计算
  *
  */
trait SeparationMotionAndStatics {
  def motion()

  def statics()
}
