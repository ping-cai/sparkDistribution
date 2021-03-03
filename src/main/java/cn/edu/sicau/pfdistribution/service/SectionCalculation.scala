package cn.edu.sicau.pfdistribution.service

import cn.edu.sicau.pfdistribution.entity.Command
import cn.edu.sicau.pfdistribution.entity.correct.AbstractSectionFlow

trait SectionCalculation {
  def sectionFlow(command: Command): java.util.List[AbstractSectionFlow]
}
