package cn.edu.sicau.pfdistribution.dao;

import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow;

import java.util.List;
import java.util.Map;

public interface GetPassengerFlowInter {
    //    获取一个小时OD数据
    Map<String, Integer> oneHourGet(String dataDT, String startTime, String endTime);

    //      获取半个小时OD数据
    Map<String, Integer> halfHourGet(String dataDT, String startTime, String endTime);

    //      获取15分钟OD数据，包含不在15分钟区间的
    List<GetQuarterPassengerFlow> quarterHourNoInclude(String startTime, String endTime);

}
