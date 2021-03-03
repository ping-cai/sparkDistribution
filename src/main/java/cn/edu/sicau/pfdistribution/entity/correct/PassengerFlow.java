package cn.edu.sicau.pfdistribution.entity.correct;

import java.util.Objects;

public abstract class PassengerFlow {
    private String inTime;
    private String outTime;
    private String inName;
    private String outName;
    private Double passengers;
    private Integer minutes;

    public PassengerFlow() {
    }

    public PassengerFlow(String inTime, String outTime, String inName, String outName, Double passengers, Integer minutes) {
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

    public Double getPassengers() {
        return passengers;
    }

    public void setPassengers(Double passengers) {
        this.passengers = passengers;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PassengerFlow that = (PassengerFlow) o;
        return Objects.equals(inTime, that.inTime) &&
                Objects.equals(outTime, that.outTime) &&
                Objects.equals(inName, that.inName) &&
                Objects.equals(outName, that.outName) &&
                Objects.equals(passengers, that.passengers) &&
                Objects.equals(minutes, that.minutes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inTime, outTime, inName, outName, passengers, minutes);
    }
}
