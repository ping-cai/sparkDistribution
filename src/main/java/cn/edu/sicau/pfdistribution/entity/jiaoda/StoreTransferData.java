package cn.edu.sicau.pfdistribution.entity.jiaoda;

import java.io.Serializable;
import java.util.Date;

/**
 * 换乘详细数据实体
 *
 * @author LiYongPing
 */
public class StoreTransferData implements Serializable {
    //    数据日期
    private Date date;
    //    开始时间
    private Date startTime;
    //    结束时间
    private Date endTime;
    //    换乘点
    private String transfer;
    //    换入线路
    private String inLineId;
    //    换出线路
    private String outLineId;
    //    上进上出
    private Double upInUpOutNum = 0.0;
    //    上进下出
    private Double upInDownOutNum = 0.0;
    //    下进上出
    private Double downInUpOutNum = 0.0;
    //    下进下出
    private Double downInDownOutNum = 0.0;

    public StoreTransferData() {
    }

    public StoreTransferData(Date date, Date startTime, Date endTime, String transfer, String inLineId, String outLineId, Double upInUpOutNum, Double upInDownOutNum, Double downInUpOutNum, Double downInDownOutNum) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.transfer = transfer;
        this.inLineId = inLineId;
        this.outLineId = outLineId;
        this.upInUpOutNum = upInUpOutNum;
        this.upInDownOutNum = upInDownOutNum;
        this.downInUpOutNum = downInUpOutNum;
        this.downInDownOutNum = downInDownOutNum;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTransfer() {
        return transfer;
    }

    public void setTransfer(String transfer) {
        this.transfer = transfer;
    }

    public String getInLineId() {
        return inLineId;
    }

    public void setInLineId(String inLineId) {
        this.inLineId = inLineId;
    }

    public String getOutLineId() {
        return outLineId;
    }

    public void setOutLineId(String outLineId) {
        this.outLineId = outLineId;
    }

    public Double getUpInUpOutNum() {
        return upInUpOutNum;
    }

    public void setUpInUpOutNum(Double upInUpOutNum) {
        this.upInUpOutNum = upInUpOutNum;
    }

    public Double getUpInDownOutNum() {
        return upInDownOutNum;
    }

    public void setUpInDownOutNum(Double upInDownOutNum) {
        this.upInDownOutNum = upInDownOutNum;
    }

    public Double getDownInUpOutNum() {
        return downInUpOutNum;
    }

    public void setDownInUpOutNum(Double downInUpOutNum) {
        this.downInUpOutNum = downInUpOutNum;
    }

    public Double getDownInDownOutNum() {
        return downInDownOutNum;
    }

    public void setDownInDownOutNum(Double downInDownOutNum) {
        this.downInDownOutNum = downInDownOutNum;
    }

    @Override
    public String toString() {
        return "StoreTransferData{" +
                "date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", transfer='" + transfer + '\'' +
                ", inLineId='" + inLineId + '\'' +
                ", outLineId='" + outLineId + '\'' +
                ", upInUpOutNum=" + upInUpOutNum +
                ", upInDownOutNum=" + upInDownOutNum +
                ", downInUpOutNum=" + downInUpOutNum +
                ", downInDownOutNum=" + downInDownOutNum +
                '}';
    }
}
