package cn.edu.sicau.pfdistribution.Utils;

import cn.edu.sicau.pfdistribution.entity.correct.AbstractSectionCapacity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 从数据库中加载各类依赖信息
 * sectionIdDetermine():区间信息
 * lineDetermine()：区间和线路关系
 * stationIdDetermine()：区间Id和区间名称关系
 * sectionRunTime()：区间运行时间
 */
@Component
public class CommonMethod implements Serializable {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public HashMap<String, String> sectionIdDetermine() {
        String getSectionIdSql = "SELECT CZ1_ID,CZ2_ID,QJ_ID FROM SCOTT.\"dic_section\"";
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(getSectionIdSql);
        HashMap<String, String> sectionMap = new HashMap<>();
        for (Map<String, Object> map : mapList) {
            Object inId = map.get("CZ1_ID");
            Object outId = map.get("CZ2_ID");
            Object sectionId = map.get("QJ_ID");
            sectionMap.put(inId + " " + outId, String.valueOf(sectionId));
        }
        return sectionMap;
    }

    public HashMap<String, String> lineDetermine() {
        String getSectionIdSql = "SELECT CZ1_ID,CZ2_ID,QJ_LJM FROM SCOTT.\"dic_section\"";
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(getSectionIdSql);
        HashMap<String, String> lineMap = new HashMap<>();
        for (Map<String, Object> map : mapList) {
            Object inId = map.get("CZ1_ID");
            Object outId = map.get("CZ2_ID");
            Object lineId = map.get("QJ_LJM");
            lineMap.put(inId + " " + outId, String.valueOf(lineId));
        }
        return lineMap;
    }

    public HashMap<String, String> stationIdDetermine() {
//        K短路修改后的sql
//        String getStationSql = "SELECT CZ_ID,CZ_NAME from SCOTT.\"dic_station\"";
        String getStationSql = "SELECT CZ_ID,CZ_ID CZ_NAME from SCOTT.\"dic_station\"";
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(getStationSql);
        HashMap<String, String> stationIdMap = new HashMap<>();
        for (Map<String, Object> map : mapList) {
            Object stationId = map.get("CZ_ID");
            Object stationName = map.get("CZ_NAME");
            stationIdMap.put(String.valueOf(stationName), String.valueOf(stationId));
        }
        return stationIdMap;
    }

    public HashMap<String, Double> sectionRunTime() {
        String getSectionSql = "SELECT CZ1_ID,CZ2_ID,QJ_LENGTH FROM SCOTT.\"dic_section\"";
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(getSectionSql);
        HashMap<String, Double> sectionRunTimeMap = new HashMap<>();
        for (Map<String, Object> map : mapList) {
            Object startId = map.get("CZ1_ID");
            Object endId = map.get("CZ2_ID");
            Object length = map.get("QJ_LENGTH");
            Double time = Double.parseDouble(length.toString()) * 45;
            sectionRunTimeMap.put(String.valueOf(startId) + " " + String.valueOf(endId), time);
        }
        return sectionRunTimeMap;
    }

    public HashMap<String, String> stationLineDetermine() {
        String getSectionIdSql = "SELECT CZ_ID,LJM FROM SCOTT.\"dic_station\"";
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(getSectionIdSql);
        HashMap<String, String> stationLineMap = new HashMap<>();
        for (Map<String, Object> map : mapList) {
            Object station = map.get("CZ_ID");
            Object line = map.get("LJM");
            stationLineMap.put(String.valueOf(station), String.valueOf(line));
        }
        return stationLineMap;
    }

    public HashMap<String, String> stationNameToId() {
        String getStationSql = "SELECT CZ_ID,CZ_NAME FROM SCOTT.\"dic_station\"";
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(getStationSql);
        HashMap<String, String> stationNameMap = new HashMap<>();
        for (Map<String, Object> map : mapList) {
            Object station = map.get("CZ_ID");
            Object name = map.get("CZ_NAME");
            stationNameMap.put(String.valueOf(name), String.valueOf(station));
        }
        return stationNameMap;
    }

    public Map<AbstractSectionCapacity, Double> sectionCapacity() {
        String getCapacitySql = "";
        jdbcTemplate.queryForList(getCapacitySql);
        Map<AbstractSectionCapacity, Double> sectionCapacityMap = new HashMap<>();
        return sectionCapacityMap;
    }
}
