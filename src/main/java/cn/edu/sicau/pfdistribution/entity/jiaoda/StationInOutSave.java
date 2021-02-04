package cn.edu.sicau.pfdistribution.entity.jiaoda;

public class StationInOutSave {
    private String startTime;
    private String endTime;
    private String inName;
    private Integer inPassengers;
    private Integer outPassengers;

    public StationInOutSave(String startTime, String endTime, String inName, Integer inPassengers, Integer outPassengers) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.inName = inName;
        this.inPassengers = inPassengers;
        this.outPassengers = outPassengers;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getInName() {
        return inName;
    }

    public void setInName(String inName) {
        this.inName = inName;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getInPassengers() {
        return inPassengers;
    }

    public void setInPassengers(Integer inPassengers) {
        this.inPassengers = inPassengers;
    }

    public Integer getOutPassengers() {
        return outPassengers;
    }

    public void setOutPassengers(Integer outPassengers) {
        this.outPassengers = outPassengers;
    }
}
