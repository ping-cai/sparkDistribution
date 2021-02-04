package cn.edu.sicau.pfdistribution.entity.jiaoda;

public class StoreSectionPassengers {
    private String dataDT;
    private String sectionId;
    private String inId;
    private String outId;
    private String inTime;
    private String outTime;
    private Double passengers;

    public StoreSectionPassengers(String dataDT, String sectionId, String inId, String outId, String inTime, String outTime, Double passengers) {
        this.dataDT = dataDT;
        this.sectionId = sectionId;
        this.inId = inId;
        this.outId = outId;
        this.inTime = inTime;
        this.outTime = outTime;
        this.passengers = passengers;
    }

    public StoreSectionPassengers() {
    }

    public String getDataDT() {
        return dataDT;
    }

    public void setDataDT(String dataDT) {
        this.dataDT = dataDT;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getInId() {
        return inId;
    }

    public void setInId(String inId) {
        this.inId = inId;
    }

    public String getOutId() {
        return outId;
    }

    public void setOutId(String outId) {
        this.outId = outId;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public Double getPassengers() {
        return passengers;
    }

    public void setPassengers(Double passengers) {
        this.passengers = passengers;
    }

    @Override
    public String toString() {
        return "StoreSectionPassengers{" +
                "dataDT='" + dataDT + '\'' +
                ", sectionId='" + sectionId + '\'' +
                ", inId='" + inId + '\'' +
                ", outId='" + outId + '\'' +
                ", inTime='" + inTime + '\'' +
                ", outTime='" + outTime + '\'' +
                ", passengers=" + passengers +
                '}';
    }
}
