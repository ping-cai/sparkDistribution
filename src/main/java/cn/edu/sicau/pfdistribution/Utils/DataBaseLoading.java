package cn.edu.sicau.pfdistribution.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据预加载类，项目启动即加载到内存中，是常用类
 * lineMap:加载线路信息
 * sectionIdMap:加载区间信息
 * sectionRunTime:加载区间运行时间信息
 */
@Component
public class DataBaseLoading {
    public static Map<String, String> lineMap = new HashMap<>();
    public static Map<String, String> sectionIdMap = new HashMap<>();
    public static Map<String, Double> sectionRunTime = new HashMap<>();
    public static Map<String, String> stationLine = new HashMap<>();
    public static Map<String, String> stationNameToId = new HashMap<>();
    @Autowired
    private CommonMethod commonMethod;

    @PostConstruct
    public void lineDetermineInit() {
        HashMap<String, String> lineDetermine = commonMethod.lineDetermine();
        for (String theLineId : lineDetermine.keySet()) {
            lineMap.put(theLineId, lineDetermine.get(theLineId));
        }
    }

    @PostConstruct
    public void sectionIdDetermineInit() {
        HashMap<String, String> sectionIdDetermine = commonMethod.sectionIdDetermine();
        for (String theSectionId : sectionIdDetermine.keySet()) {
            sectionIdMap.put(theSectionId, sectionIdDetermine.get(theSectionId));
        }
    }

    @PostConstruct
    public void sectionRunTimeInit() {
        HashMap<String, Double> runTime = commonMethod.sectionRunTime();
        for (String theSection : runTime.keySet()) {
            sectionRunTime.put(theSection, runTime.get(theSection));
        }
    }

    @PostConstruct
    public void stationDetermineInit() {
        HashMap<String, String> stationLineDetermine = commonMethod.stationLineDetermine();
        for (String station : stationLineDetermine.keySet()) {
            stationLine.put(station, stationLineDetermine.get(station));
        }
    }

    @PostConstruct
    public void stationNameInit() {
        HashMap<String, String> stationLineDetermine = commonMethod.stationNameToId();
        for (String station : stationLineDetermine.keySet()) {
            stationNameToId.put(station, stationLineDetermine.get(station));
        }
    }
}
