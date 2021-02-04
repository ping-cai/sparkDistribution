package cn.edu.sicau.pfdistribution.dao;


import java.util.List;
import java.util.Map;

public interface RegionSaveInterface {

    //查询出车站所在的线路ID
    Map<Integer, Integer> selectLineId();
    //查询id对应的车站名
    Map<Integer, List<String>> selectTime();

}
