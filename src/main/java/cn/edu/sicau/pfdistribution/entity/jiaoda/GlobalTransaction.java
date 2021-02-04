package cn.edu.sicau.pfdistribution.entity.jiaoda;

import lombok.Data;

@Data
public class GlobalTransaction {
    public String timeStamp;
    public String startTime;
    public String endTime;
    public String timeInterval;
    public String commandType;
    public int odMatchTimes;
    public int odForecastTimes;
    public int stationForecastTimes;
    public int sectionForecastTimes;
    public int transferForecastTimes;

    public GlobalTransaction() {
    }
}
