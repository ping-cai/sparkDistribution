package cn.edu.sicau.pfdistribution.entity.correct;

import java.util.Objects;

public abstract class AbstractSectionFlow {
    public String dataDT;
    public String sectionId;
    public String inId;
    public String outId;
    public String inTime;
    public String outTime;
    public Double passengers;

    public AbstractSectionFlow() {
    }

    public AbstractSectionFlow(String inId, String outId, String inTime, String outTime) {
        this.inId = inId;
        this.outId = outId;
        this.inTime = inTime;
        this.outTime = outTime;
    }

    public AbstractSectionFlow(String inId, String outId, String inTime, String outTime, Double passengers) {
        this.inId = inId;
        this.outId = outId;
        this.inTime = inTime;
        this.outTime = outTime;
        this.passengers = passengers;
    }

    public AbstractSectionFlow(String dataDT, String sectionId, String inId, String outId, String inTime, String outTime, Double passengers) {
        this.dataDT = dataDT;
        this.sectionId = sectionId;
        this.inId = inId;
        this.outId = outId;
        this.inTime = inTime;
        this.outTime = outTime;
        this.passengers = passengers;
    }

    public String getDataDT() {
        return dataDT;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getInId() {
        return inId;
    }

    public String getOutId() {
        return outId;
    }

    public String getInTime() {
        return inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public Double getPassengers() {
        return passengers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractSectionFlow that = (AbstractSectionFlow) o;
        return Objects.equals(inId, that.inId) &&
                Objects.equals(outId, that.outId) &&
                Objects.equals(inTime, that.inTime) &&
                Objects.equals(outTime, that.outTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inId, outId, inTime, outTime);
    }
}
