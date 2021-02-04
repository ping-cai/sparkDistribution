package cn.edu.sicau.pfdistribution.entity.jiaoda;

import lombok.Data;

@Data
public class TransferPassengers {
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

    public TransferPassengers() {
    }

    public TransferPassengers(String transfer, String inLineId, String outLineId, Double upInUpOutNum, Double upInDownOutNum, Double downInUpOutNum, Double downInDownOutNum) {
        this.transfer = transfer;
        this.inLineId = inLineId;
        this.outLineId = outLineId;
        this.upInUpOutNum = upInUpOutNum;
        this.upInDownOutNum = upInDownOutNum;
        this.downInUpOutNum = downInUpOutNum;
        this.downInDownOutNum = downInDownOutNum;
    }
}
