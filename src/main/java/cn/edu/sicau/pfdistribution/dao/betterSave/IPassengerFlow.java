package cn.edu.sicau.pfdistribution.dao.betterSave;

import cn.edu.sicau.pfdistribution.dataInteraction.SectionFlowInfo;
import cn.edu.sicau.pfdistribution.dataInteraction.StationFlowInfo;

import java.util.List;

public interface IPassengerFlow {
    //    数据库中获取车站进出站数据
    List<StationFlowInfo> getStationFlowInfo(String dataDT, String startTime, String endTime, String stationTable,String transferTable);

    //     数据库中获取区间断面数据
    List<SectionFlowInfo> getSectionFlowInfo(String dataDT, String startTime, String endTime, String tableName);
}
