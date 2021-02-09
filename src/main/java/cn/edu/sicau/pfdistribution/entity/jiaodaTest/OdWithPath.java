package cn.edu.sicau.pfdistribution.entity.jiaodaTest;

import cn.edu.sicau.pfdistribution.entity.DirectedPath;

import java.util.List;


public class OdWithPath {
    private String origin;
    private String destination;
    private List<DirectedPath> pathList;

    public OdWithPath() {
    }

    public OdWithPath(String origin, String destination, List<DirectedPath> pathList) {
        this.origin = origin;
        this.destination = destination;
        this.pathList = pathList;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public List<DirectedPath> getPathList() {
        return pathList;
    }
}
