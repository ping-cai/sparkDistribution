package cn.edu.sicau.pfdistribution.dao;

import cn.edu.sicau.pfdistribution.entity.correct.PassengerFlow;

import java.util.List;

public interface GetFlowInter extends GetPassengerFlowInter {
    List<? extends PassengerFlow> quarterPassengers(String startTime, String endTime);

}
