package cn.edu.sicau.pfdistribution.entity.correct;

import cn.edu.sicau.pfdistribution.entity.DirectedEdge;
import scala.Array;
import scala.Double;
import scala.collection.mutable.Map;

public class OverflowData extends PassengerFlow {
    private scala.collection.mutable.Map<scala.Array<DirectedEdge>, scala.Double> odPath;

    public OverflowData() {
    }

    public OverflowData(String inTime, String outTime, String inName, String outName, java.lang.Double passengers, Integer minutes, Map<Array<DirectedEdge>, Double> odPath) {
        super(inTime, outTime, inName, outName, passengers, minutes);
        this.odPath = odPath;
    }

    public Map<Array<DirectedEdge>, Double> getOdPath() {
        return odPath;
    }

    public void setOdPath(Map<Array<DirectedEdge>, Double> odPath) {
        this.odPath = odPath;
    }
}
