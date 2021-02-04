package cn.edu.sicau.pfdistribution.dataInteraction;

import java.util.List;

public interface IPassengerFlowInfo {
    //    数据库中获取车站进出站数据
    List<StationFlowInfo> getStationFlowInfo(String dataDT, String startTime, String endTime);

    //     数据库中获取区间断面数据
    List<SectionFlowInfo> getSectionFlowInfo(String dataDT, String startTime, String endTime);

}
