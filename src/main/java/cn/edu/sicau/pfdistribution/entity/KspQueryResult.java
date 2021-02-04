package cn.edu.sicau.pfdistribution.entity;

/**
 * @author 李勇平
 * 定义查询线路的返回结果
 */
public class KspQueryResult {
    private int lineId;
    private String lineInfo;
    private String startTimeStr;
    private String finalTimeStr;
    public KspQueryResult(int lineId,String lineInfo,String startTimeStr,String finalTimeStr){
        this.lineId=lineId;
        this.lineInfo=lineInfo;
        this.startTimeStr=startTimeStr;
        this.finalTimeStr=finalTimeStr;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public String getLineInfo() {
        return lineInfo;
    }

    public void setLineInfo(String lineInfo) {
        this.lineInfo = lineInfo;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getFinalTimeStr() {
        return finalTimeStr;
    }

    public void setFinalTimeStr(String finalTimeStr) {
        this.finalTimeStr = finalTimeStr;
    }
}
