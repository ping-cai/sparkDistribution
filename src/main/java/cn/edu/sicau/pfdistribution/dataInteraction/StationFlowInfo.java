package cn.edu.sicau.pfdistribution.dataInteraction;

public class StationFlowInfo {
    public int stationId; //车站id
    public int inPassengers; //进站客流
    public int outPassengers; //出站客流
    public int transferPassengers; //换乘客流

    public StationFlowInfo() {

    }

    public StationFlowInfo(int stationId, int inPassengers, int outPassengers, int transferPassengers) {
        this.stationId = stationId;
        this.inPassengers = inPassengers;
        this.outPassengers = outPassengers;
        this.transferPassengers = transferPassengers;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public int getInPassengers() {
        return inPassengers;
    }

    public void setInPassengers(int inPassengers) {
        this.inPassengers = inPassengers;
    }

    public int getOutPassengers() {
        return outPassengers;
    }

    public void setOutPassengers(int outPassengers) {
        this.outPassengers = outPassengers;
    }

    public int getTransferPassengers() {
        return transferPassengers;
    }

    public void setTransferPassengers(int transferPassengers) {
        this.transferPassengers = transferPassengers;
    }

    @Override
    public String toString() {
        return "StationFlowInfo{" +
                "stationId=" + stationId +
                ", inPassengers=" + inPassengers +
                ", outPassengers=" + outPassengers +
                ", transferPassengers=" + transferPassengers +
                '}';
    }
}
