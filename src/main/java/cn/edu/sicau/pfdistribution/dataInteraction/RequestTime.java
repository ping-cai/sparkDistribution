package cn.edu.sicau.pfdistribution.dataInteraction;

public class RequestTime {
    public String timeStamp; //当前时间戳 格式：yyyy-MM-dd HH:mm:ss
    public String startTime; //请求时间段起始时间 格式：yyyy-MM-dd HH:mm:ss
    public String endTime; //请求时间段结束时间 格式：yyyy-MM-dd HH:mm:ss
    public int timeInterval;//请求的时间粒度，比如15分钟，30分钟，60分钟

    public RequestTime() {
    }

    public RequestTime(String timeStamp, String startTime, String endTime, int timeInterval) {
        this.timeStamp = timeStamp;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeInterval = timeInterval;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    @Override
    public String toString() {
        return "RequestTime{" +
                "timeStamp='" + timeStamp + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", timeInterval=" + timeInterval +
                '}';
    }
}
