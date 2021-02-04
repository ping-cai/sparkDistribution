package cn.edu.sicau.pfdistribution.entity.jiaoda;

import java.io.Serializable;

public class QuarterSectionSave implements Serializable {
    private String inTime;
    private String outTime;
    private String inId;
    private String outId;
    private Double passengers;

    public QuarterSectionSave(String inTime, String outTime, String inId, String outId, Double passengers) {
        this.inTime = inTime;
        this.outTime = outTime;
        this.inId = inId;
        this.outId = outId;
        this.passengers = passengers;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getInId() {
        return inId;
    }

    public void setInId(String inId) {
        this.inId = inId;
    }

    public String getOutId() {
        return outId;
    }

    public void setOutId(String outId) {
        this.outId = outId;
    }

    public Double getPassengers() {
        return passengers;
    }

    public void setPassengers(Double passengers) {
        this.passengers = passengers;
    }

    public QuarterSectionSave() {
    }

    @Override
    public String toString() {
        return "QuarterSectionSave{" +
                "inTime='" + inTime + '\'' +
                ", outTime='" + outTime + '\'' +
                ", inId='" + inId + '\'' +
                ", outId='" + outId + '\'' +
                ", passengers=" + passengers +
                '}';
    }
}
