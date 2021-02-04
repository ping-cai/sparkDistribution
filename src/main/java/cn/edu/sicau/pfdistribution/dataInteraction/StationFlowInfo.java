package cn.edu.sicau.pfdistribution.dataInteraction;

public class StationFlowInfo {
    public int StationId; //车站id
    public int inPassengers; //进站客流
    public int outPassengers; //出站客流
    public int transferPassengers; //换乘客流

    public StationFlowInfo() {
    }

    public StationFlowInfo(int stationId, int inPassengers, int outPassengers, int transferPassengers) {
        StationId = stationId;
        this.inPassengers = inPassengers;
        this.outPassengers = outPassengers;
        this.transferPassengers = transferPassengers;
    }

    public int getStationId() {
        return StationId;
    }

    public void setStationId(int stationId) {
        StationId = stationId;
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
                "StationId=" + StationId +
                ", inPassengers=" + inPassengers +
                ", outPassengers=" + outPassengers +
                ", transferPassengers=" + transferPassengers +
                '}';
    }
}
