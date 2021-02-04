package cn.edu.sicau.pfdistribution.entity.jiaoda;

import java.util.TreeMap;

public class HalfTransferDataEntity {
    private String startTime;
    private String transferId;
    private Integer passengers;

    public HalfTransferDataEntity() {
    }

    public HalfTransferDataEntity(String startTime, String tranferId, Integer passengers) {
        this.startTime = startTime;
        this.transferId = tranferId;
        this.passengers = passengers;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public Integer getPassengers() {
        return passengers;
    }

    public void setPassengers(Integer passengers) {
        this.passengers = passengers;
    }

}
