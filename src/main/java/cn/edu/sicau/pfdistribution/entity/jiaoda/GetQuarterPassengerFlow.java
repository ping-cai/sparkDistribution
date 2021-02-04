package cn.edu.sicau.pfdistribution.entity.jiaoda;

import java.io.Serializable;

public class GetQuarterPassengerFlow implements Serializable {
    private String inTime;
    private String outTime;
    private String inName;
    private String outName;
    private Integer passengers;
    private Integer minutes;

    public GetQuarterPassengerFlow() {
    }

    public GetQuarterPassengerFlow(String inTime, String outTime, String inName, String outName, Integer passengers, Integer minutes) {
        this.inTime = inTime;
        this.outTime = outTime;
        this.inName = inName;
        this.outName = outName;
        this.passengers = passengers;
        this.minutes = minutes;
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

    public String getInName() {
        return inName;
    }

    public void setInName(String inName) {
        this.inName = inName;
    }

    public String getOutName() {
        return outName;
    }

    public void setOutName(String outName) {
        this.outName = outName;
    }

    public Integer getPassengers() {
        return passengers;
    }

    public void setPassengers(Integer passengers) {
        this.passengers = passengers;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    @Override
    public String toString() {
        return "GetQuarterPassengerFlow{" +
                "inTime='" + inTime + '\'' +
                ", outTime='" + outTime + '\'' +
                ", inName='" + inName + '\'' +
                ", outName='" + outName + '\'' +
                ", passengers=" + passengers +
                ", minutes=" + minutes +
                '}';
    }
}
