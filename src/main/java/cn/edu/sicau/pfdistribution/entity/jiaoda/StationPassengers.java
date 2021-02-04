package cn.edu.sicau.pfdistribution.entity.jiaoda;

import lombok.Data;

@Data
public class StationPassengers {
    private String Station;
    private Integer inPassengers;
    private Integer outPassengers;

    public StationPassengers() {
    }

    public StationPassengers(String station, Integer inPassengers, Integer outPassengers) {
        Station = station;
        this.inPassengers = inPassengers;
        this.outPassengers = outPassengers;
    }
}
