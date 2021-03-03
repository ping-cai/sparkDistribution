package cn.edu.sicau.pfdistribution.entity.correct;

import scala.Double;
import scala.collection.mutable.Map;

public class SectionFlow extends AbstractSectionFlow {
    private scala.collection.mutable.Map<AbstractSectionCapacity, scala.Double> sectionCapacityMap;

    public SectionFlow() {
    }

    public SectionFlow(String inId, String outId, String inTime, String outTime, java.lang.Double passengers, Map<AbstractSectionCapacity, Double> sectionCapacityMap) {
        super(inId, outId, inTime, outTime, passengers);
        this.sectionCapacityMap = sectionCapacityMap;
    }

    public SectionFlow(String dataDT, String sectionId, String inId, String outId, String inTime, String outTime, java.lang.Double passengers) {
        super(dataDT, sectionId, inId, outId, inTime, outTime, passengers);
    }

    public Map<AbstractSectionCapacity, Double> getSectionCapacityMap() {
        return sectionCapacityMap;
    }

    public void setSectionCapacityMap(Map<AbstractSectionCapacity, Double> sectionCapacityMap) {
        this.sectionCapacityMap = sectionCapacityMap;
    }
}
