package cn.edu.sicau.pfdistribution.dao;

import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import cn.edu.sicau.pfdistribution.entity.jiaoda.StoreTransferData;

import java.util.ArrayList;

public interface SavePassengerFlow {
    /**
     * 进出站数据存储
     *
     * @param dataDT    数据日期
     * @param stationId 车站id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param entryNum  进站人数
     * @param exitNum   出站人数
     */
    void saveStationInAndOutData(String dataDT, String stationId, String startTime, String endTime, float entryNum, float exitNum, RequestCommand requestCommand);

    /**
     * 区间断面数据存储
     *
     * @param dataDT     数据日期
     * @param stationIn  进站名
     * @param stationOut 出站名
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param passengers 人数
     */
    void saveSectionData(String dataDT, String stationIn, String stationOut, String startTime, String endTime, double passengers, RequestCommand requestCommand);

    /**
     * 换乘数据存储
     *
     * @param dataDT    数据日期
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param arrayList 换乘数据集合
     */
    void saveTransferInfo(String dataDT, String startTime, String endTime, ArrayList<StoreTransferData> arrayList, RequestCommand requestCommand);
}
