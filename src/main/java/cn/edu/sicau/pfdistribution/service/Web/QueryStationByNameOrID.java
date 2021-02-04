package cn.edu.sicau.pfdistribution.service.Web;

import org.springframework.stereotype.Service;

/**
 * @author LiYongPing
 */
@Service
public class QueryStationByNameOrID {
    private int station;
    private String stationName;

    public QueryStationByNameOrID() {
        this.station = -1;
    }

    public QueryStationByNameOrID(int station, String stationName) {
        this.station = station;
        this.stationName = stationName;
    }

    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        this.station = station;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
}
