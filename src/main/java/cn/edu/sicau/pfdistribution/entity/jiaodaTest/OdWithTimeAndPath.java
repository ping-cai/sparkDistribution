package cn.edu.sicau.pfdistribution.entity.jiaodaTest;

import cn.edu.sicau.pfdistribution.entity.DirectedPath;

import java.util.List;

public class OdWithTimeAndPath extends OdWithPath {
    private String startTime;
    private String endTime;
    private Integer passengers;
    private Integer minutes;

    public OdWithTimeAndPath() {
    }

    public OdWithTimeAndPath(String origin, String destination, List<DirectedPath> pathList, String startTime, String endTime, Integer passengers, Integer minutes) {
        super(origin, destination, pathList);
        this.startTime = startTime;
        this.endTime = endTime;
        this.passengers = passengers;
        this.minutes = minutes;
    }

}
