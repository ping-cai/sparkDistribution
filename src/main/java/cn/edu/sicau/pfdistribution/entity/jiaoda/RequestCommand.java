package cn.edu.sicau.pfdistribution.entity.jiaoda;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author LiYongPing
 * 请求信息实体类
 */
public class RequestCommand implements Serializable {
//    数据日期
    private String dateDt;
//    分配开始时间
    private String startTime;
//    分配结束时间
    private String endTime;
//    分配粒度，15,30,60分钟
    private Integer timeInterval;
//    分配命令，动态dynamic,静态static
    private String command;

    public RequestCommand() {
    }

    public RequestCommand(String dateDt, String startTime, String endTime, Integer timeInterval, String command) {
        this.dateDt = dateDt;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeInterval = timeInterval;
        this.command = command;
    }

    public String getDateDt() {
        return dateDt;
    }

    public void setDateDt(String dateDt) {
        this.dateDt = dateDt;
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

    public Integer getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(Integer timeInterval) {
        this.timeInterval = timeInterval;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "RequestCommand{" +
                "dateDt='" + dateDt + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", timeInterval=" + timeInterval +
                ", command='" + command + '\'' +
                '}';
    }
}
