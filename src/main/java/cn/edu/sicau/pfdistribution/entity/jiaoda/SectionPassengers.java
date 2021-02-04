package cn.edu.sicau.pfdistribution.entity.jiaoda;

import lombok.Data;

@Data
public class SectionPassengers {
    private String inId;
    private String outId;
    private Double passengers;

    public SectionPassengers() {
    }

    public SectionPassengers(String inId, String outId, Double passengers) {
        this.inId = inId;
        this.outId = outId;
        this.passengers = passengers;
    }
}
