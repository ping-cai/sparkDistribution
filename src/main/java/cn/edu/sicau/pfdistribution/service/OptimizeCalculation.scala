package cn.edu.sicau.pfdistribution.service


trait OptimizeCalculation[T] {
  def calculateResult(odData: T)
}
