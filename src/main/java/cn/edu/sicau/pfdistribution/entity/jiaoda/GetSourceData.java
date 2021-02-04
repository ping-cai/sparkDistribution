package cn.edu.sicau.pfdistribution.entity.jiaoda;

public class GetSourceData {
    private String dataDT;
    private Integer time;
    private Integer inStationId;
    private String inStation;
    private Integer outStationId;
    private String outStation;
    private Integer passengers;

    public GetSourceData() {
    }

    public GetSourceData(String dataDT, Integer time, Integer inStationId, String inStation, Integer outStationId, String outStation, Integer passengers) {
        this.dataDT = dataDT;
        this.time = time;
        this.inStationId = inStationId;
        this.inStation = inStation;
        this.outStationId = outStationId;
        this.outStation = outStation;
        this.passengers = passengers;
    }

    public String getDataDT() {
        return dataDT;
    }

    public void setDataDT(String dataDT) {
        this.dataDT = dataDT;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getInStationId() {
        return inStationId;
    }

    public void setInStationId(Integer inStationId) {
        this.inStationId = inStationId;
    }

    public String getInStation() {
        return inStation;
    }

    public void setInStation(String inStation) {
        this.inStation = inStation;
    }

    public Integer getOutStationId() {
        return outStationId;
    }

    public void setOutStationId(Integer outStationId) {
        this.outStationId = outStationId;
    }

    public String getOutStation() {
        return outStation;
    }

    public void setOutStation(String outStation) {
        this.outStation = outStation;
    }

    public Integer getPassengers() {
        return passengers;
    }

    public void setPassengers(Integer passengers) {
        this.passengers = passengers;
    }

    @Override
    public String toString() {
        return "GetSourceData{" +
                "dataDT='" + dataDT + '\'' +
                ", time=" + time +
                ", inStationId=" + inStationId +
                ", inStation='" + inStation + '\'' +
                ", outStationId=" + outStationId +
                ", outStation='" + outStation + '\'' +
                ", passengers=" + passengers +
                '}';
    }
}
