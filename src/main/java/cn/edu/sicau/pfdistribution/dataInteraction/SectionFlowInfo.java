package cn.edu.sicau.pfdistribution.dataInteraction;

public class SectionFlowInfo {
    public int StationStartId; //区间起始车站id
    public int StationEndId; //区间终止车站id
    public int passengers; //区间客流量

    public SectionFlowInfo() {
    }

    public SectionFlowInfo(int stationStartId, int stationEndId, int passengers) {
        StationStartId = stationStartId;
        StationEndId = stationEndId;
        this.passengers = passengers;
    }

    public int getStationStartId() {
        return StationStartId;
    }

    public void setStationStartId(int stationStartId) {
        StationStartId = stationStartId;
    }

    public int getStationEndId() {
        return StationEndId;
    }

    public void setStationEndId(int stationEndId) {
        StationEndId = stationEndId;
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
                "StationStartId=" + StationStartId +
                ", StationEndId=" + StationEndId +
                ", passengers=" + passengers +
                '}';
    }
}
