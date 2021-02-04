package cn.edu.sicau.pfdistribution.entity.jiaoda;

public class ODPassengers {
    private String inName;
    private String outName;
    private int passengers;

    public ODPassengers() {
    }

    public ODPassengers(String inName, String outName, int passengers) {
        this.inName = inName;
        this.outName = outName;
        this.passengers = passengers;
    }

    public String getInName() {
        return inName;
    }

    public void setInName(String inName) {
        this.inName = inName;
    }

    public String getOutName() {
        return outName;
    }

    public void setOutName(String outName) {
        this.outName = outName;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }
}
