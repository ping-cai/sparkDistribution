package cn.edu.sicau.pfdistribution.entity.jiaoda;

import java.io.Serializable;

/**
 * 暂时存储换乘点，方向和人数
 * 在scala中进行中间转换
 */
public class TransferPoint implements Serializable {
    //    换乘站前一个站点
    private String transferBefore;
    //    换乘站点第一个ID
    private String transferPointBefore;
    //    换乘站点第二个ID
    private String transferPointAfter;
    //    换乘后一个站点
    private String transferAfter;
    //    换乘方向
    private String direction;
    //    换乘人数
    private double transferFlows;

    public TransferPoint() {
    }

    public TransferPoint(String transferBefore, String transferPointBefore, String transferPointAfter, String transferAfter, String direction, double transferFlows) {
        this.transferBefore = transferBefore;
        this.transferPointBefore = transferPointBefore;
        this.transferPointAfter = transferPointAfter;
        this.transferAfter = transferAfter;
        this.direction = direction;
        this.transferFlows = transferFlows;
    }

    public String getTransferBefore() {
        return transferBefore;
    }

    public void setTransferBefore(String transferBefore) {
        this.transferBefore = transferBefore;
    }

    public String getTransferPointBefore() {
        return transferPointBefore;
    }

    public void setTransferPointBefore(String transferPointBefore) {
        this.transferPointBefore = transferPointBefore;
    }

    public String getTransferPointAfter() {
        return transferPointAfter;
    }

    public void setTransferPointAfter(String transferPointAfter) {
        this.transferPointAfter = transferPointAfter;
    }

    public String getTransferAfter() {
        return transferAfter;
    }

    public void setTransferAfter(String transferAfter) {
        this.transferAfter = transferAfter;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public double getTransferFlows() {
        return transferFlows;
    }

    public void setTransferFlows(double transferFlows) {
        this.transferFlows = transferFlows;
    }

    @Override
    public String toString() {
        return "TransferPoint{" +
                "transferBefore='" + transferBefore + '\'' +
                ", transferPointBefore='" + transferPointBefore + '\'' +
                ", transferPointAfter='" + transferPointAfter + '\'' +
                ", transferAfter='" + transferAfter + '\'' +
                ", direction='" + direction + '\'' +
                ", transferFlows=" + transferFlows +
                '}';
    }
}
