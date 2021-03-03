package cn.edu.sicau.pfdistribution.entity;

public abstract class Command {
    //    数据日期
    public String dateDt;
    //    分配开始时间
    public String startTime;
    //    分配结束时间
    public String endTime;
    //    分配粒度，15,30,60分钟
    public Integer timeInterval;
    //    分配命令，动态dynamic,静态static
    public String command;
    //      来源表名
    public String originTable;
    //      目标表名
    public String targetTable;

    public String getDateDt() {
        return dateDt;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Integer getTimeInterval() {
        return timeInterval;
    }

    public String getCommand() {
        return command;
    }

    public String getOriginTable() {
        return originTable;
    }

    public String getTargetTable() {
        return targetTable;
    }
}
