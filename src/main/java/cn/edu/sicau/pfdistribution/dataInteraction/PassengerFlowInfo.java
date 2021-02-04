package cn.edu.sicau.pfdistribution.dataInteraction;

import java.util.List;

public class PassengerFlowInfo {
    public String recordTime;//统计指标的时间，格式：yyyy-MM-dd HH:mm:ss
    public String startTime;//预测起始时间 格式：yyyy-MM-dd HH:mm:ss
    public String endTime;//预测结束时间 格式：yyyy-MM-dd HH:mm:ss
    public List<StationFlowInfo> stationFlowInfo;//车站客流量数据
    public List<SectionFlowInfo> sectionFlowInfo;//区间断面客流量数据

    public PassengerFlowInfo(String recordTime, String startTime, String endTime, List<StationFlowInfo> stationFlowInfo, List<SectionFlowInfo> sectionFlowInfo) {
        this.recordTime = recordTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.stationFlowInfo = stationFlowInfo;
        this.sectionFlowInfo = sectionFlowInfo;
    }

    public PassengerFlowInfo() {
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<StationFlowInfo> getStationFlowInfo() {
        return stationFlowInfo;
    }

    public void setStationFlowInfo(List<StationFlowInfo> stationFlowInfo) {
        this.stationFlowInfo = stationFlowInfo;
    }

    public List<SectionFlowInfo> getSectionFlowInfo() {
        return sectionFlowInfo;
    }

    public void setSectionFlowInfo(List<SectionFlowInfo> sectionFlowInfo) {
        this.sectionFlowInfo = sectionFlowInfo;
    }

    @Override
    public String toString() {
        return "PassengerFlowInfo{" +
                "recordTime='" + recordTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", stationFlowInfo=" + stationFlowInfo +
                ", sectionFlowInfo=" + sectionFlowInfo +
                '}';
    }
}
