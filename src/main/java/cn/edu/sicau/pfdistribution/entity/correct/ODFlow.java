package cn.edu.sicau.pfdistribution.entity.correct;

public class ODFlow extends PassengerFlow {
    public ODFlow() {
    }

    public ODFlow(String inTime, String outTime, String inName, String outName, Double passengers, Integer minutes) {
        super(inTime, outTime, inName, outName, passengers, minutes);
    }
}
