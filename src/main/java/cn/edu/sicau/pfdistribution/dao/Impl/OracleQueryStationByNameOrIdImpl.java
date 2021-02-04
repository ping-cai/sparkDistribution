package cn.edu.sicau.pfdistribution.dao.Impl;

import cn.edu.sicau.pfdistribution.entity.KspQueryResult;
import cn.edu.sicau.pfdistribution.service.Web.QueryStationByNameOrID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class OracleQueryStationByNameOrIdImpl {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private String getLineODName = "select LINE_START_CZM,LINE_END_CZM,LINE_NAME from \"SCOTT\".\"1-yxtLine\" where LINE_NAME= ";
    private String getLineNameAndStationID = "select LJM,CZ_ID from\"SCOTT\".\"dic_station\" where CZ_NAME=";
    private String getLineTime = "select QSZM,ZZZM,QSSJ,ZZSJ,QSQJH,ZZQJH from \"SCOTT\".\"base_lckxfa\" where QSZM=";

    /**
     * @param queryStationByNameOrID PostMan传递过来的StationNameAndId
     * @author LiYongPing
     * 数据库查询
     * 输入车站名CZ_Name
     * 表Dic_station中查询LJM线路
     * 通过LJM线路查起点站与终点站与起终点时间
     */

    public List<KspQueryResult> findAll(QueryStationByNameOrID queryStationByNameOrID) {
        return getKspQueryResultList(queryStationByNameOrID);
    }

    List<String> getLinesNames(QueryStationByNameOrID queryStationByNameOrID) {
        List<String> linesNames = new ArrayList<>();
        try {
            List<Map<String, Object>> Line_ID_map = jdbcTemplate.queryForList(getLineNameAndStationID + "'" + queryStationByNameOrID.getStationName() + "'");
            Iterator<Map<String, Object>> it1 = Line_ID_map.iterator();
            while (it1.hasNext()) {
                Map<String, Object> LineName = it1.next();
                Object ljm = LineName.get("LJM");
                linesNames.add(ljm.toString());
            }
        } catch (Exception e) {
            exceptionPrint(e);
        }
        return linesNames;
    }

    Map<String, String> getLineOD(QueryStationByNameOrID queryStationByNameOrID) {
        List<String> linesNames = getLinesNames(queryStationByNameOrID);
        List<Map<String, Object>> lineODNames = new ArrayList<>();
        try {
            for (String line : linesNames) {
                Map<String, Object> map2 = jdbcTemplate.queryForMap(getLineODName + "'" + line + "'");
                lineODNames.add(map2);
            }
        } catch (Exception e) {
            exceptionPrint(e);
        }
        Map<String, String> lineOD = new HashMap<>();
        for (Map<String, Object> name : lineODNames) {
            String lineName = (String) name.get("LINE_NAME");
            String startId = (String) name.get("LINE_START_CZM");
            String endId = (String) name.get("LINE_END_CZM");
            lineOD.put(lineName, startId + " " + endId);
        }
        return lineOD;
    }

    Map<String, String> getStartAndEndTime(QueryStationByNameOrID queryStationByNameOrID) {
        Map<String, String> lineOD = getLineOD(queryStationByNameOrID);
        List<Map<String, Object>> lineTimes = new ArrayList<>();
        try {
            for (String od : lineOD.values()) {
                String startId = od.split(" ")[0];
                String endId = od.split(" ")[1];
                Map<String, Object> map3 = jdbcTemplate.queryForMap(getLineTime + "'" + startId + "'" + "and ZZZM=" + "'" + endId + "'" + "AND ROWNUM < 2");
                lineTimes.add(map3);
            }
        } catch (Exception e) {
            exceptionPrint(e);
        }
        Map<String, String> StartAndEndTime = new HashMap<>();
        for (Map<String, Object> name : lineTimes) {
            String startId = (String) name.get("QSZM");
            String endId = (String) name.get("ZZZM");
            String startTime = (String) name.get("QSSJ");
            String endTime = (String) name.get("ZZSJ");
            String startSection = name.get("QSQJH").toString();
            String endSection = name.get("ZZQJH").toString();
            StartAndEndTime.put(startId + " " + endId + " " + startSection, startTime + " " + endTime + " " + endSection);
        }
        return StartAndEndTime;
    }

    List<KspQueryResult> getKspQueryResultList(QueryStationByNameOrID queryStationByNameOrID) {
        Map<String, String> StartAndEndTime = getStartAndEndTime(queryStationByNameOrID);
        List<KspQueryResult> result = new ArrayList<>();
        for (String od : StartAndEndTime.keySet()) {
            String startId = od.split(" ")[0];
            String endId = od.split(" ")[1];
            String startSection = od.split(" ")[2];
            String time = StartAndEndTime.get(od);
            String startTime = time.split(" ")[0];
            String endTime = time.split(" ")[1];
            String endSection = time.split(" ")[2];
            KspQueryResult kspQueryResult1 = new KspQueryResult(Integer.parseInt(startSection), "往" + startId + "方向", startTime, endTime);
            KspQueryResult kspQueryResult2 = new KspQueryResult(Integer.parseInt(endSection), "往" + endId + "方向", startTime, endTime);
            result.add(kspQueryResult1);
            result.add(kspQueryResult2);
        }
        return result;
    }


    private void exceptionPrint(Exception e) {
        log.error("LocalizedMessage:{}", e.getLocalizedMessage());
        log.error("exception message:{}", e.getMessage());
        log.error("exception cause:{}", e.getCause());
        log.error("exception suppressed:{}", e.getSuppressed());
        log.error("exception toString and track space : {}", "\r\n" + e);
        e.printStackTrace();
    }
}