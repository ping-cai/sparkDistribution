package cn.edu.sicau.pfdistribution.service.netrouter;

import lombok.Data;

@Data
public class SaveQuarterOd {
    private String inNumber;
    private String inName;
    private String inTime;
    private String outNumber;
    private String outName;
    private String outTime;
    private Integer Passengers;

    public SaveQuarterOd() {
    }

    public SaveQuarterOd(String inNumber, String inName, String inTime, String outNumber, String outName, String outTime, Integer passengers) {
        this.inNumber = inNumber;
        this.inName = inName;
        this.inTime = inTime;
        this.outNumber = outNumber;
        this.outName = outName;
        this.outTime = outTime;
        Passengers = passengers;
    }
}
