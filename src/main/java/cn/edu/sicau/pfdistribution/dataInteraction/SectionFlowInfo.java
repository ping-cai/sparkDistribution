package cn.edu.sicau.pfdistribution.dataInteraction;

public class SectionFlowInfo {
    public int stationStartId; //区间起始车站id
    public int stationEndId; //区间终止车站id
    public int passengers; //区间客流量

    public SectionFlowInfo() {
    }

    public SectionFlowInfo(int stationStartId, int stationEndId, int passengers) {
        this.stationStartId = stationStartId;
        this.stationEndId = stationEndId;
        this.passengers = passengers;
    }

    public int getStationStartId() {
        return stationStartId;
    }

    public void setStationStartId(int stationStartId) {
        this.stationStartId = stationStartId;
    }

    public int getStationEndId() {
        return stationEndId;
    }

    public void setStationEndId(int stationEndId) {
        this.stationEndId = stationEndId;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    @Override
    public String toString() {
        return "SectionFlowInfo{" +
                "stationStartId=" + stationStartId +
                ", stationEndId=" + stationEndId +
                ", passengers=" + passengers +
                '}';
    }
}
