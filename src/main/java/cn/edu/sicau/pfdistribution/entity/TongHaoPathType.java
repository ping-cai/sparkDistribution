package cn.edu.sicau.pfdistribution.entity;

import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @author 谭华波
 */
@Service
public class TongHaoPathType implements Serializable{
    private String path;
    private String passengers;
    private String startStation;
    private String endStation;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPassengers() {
        return passengers;
    }

    public void setPassengers(String passengers) {
        this.passengers = passengers;
    }
    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }
}
