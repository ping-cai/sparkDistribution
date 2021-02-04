package cn.edu.sicau.pfdistribution.dao.betterSave;

import cn.edu.sicau.pfdistribution.Utils.CommonMethod;
import cn.edu.sicau.pfdistribution.Utils.DataBaseLoading;
import cn.edu.sicau.pfdistribution.entity.TableNamePojo;
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.StaticSQL;
import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import cn.edu.sicau.pfdistribution.entity.jiaoda.SectionPassengers;
import cn.edu.sicau.pfdistribution.entity.jiaoda.StationPassengers;
import cn.edu.sicau.pfdistribution.entity.jiaoda.TransferPassengers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class BatchSave {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CommonMethod commonMethod;

    public void saveSection(RequestCommand requestCommand, List<SectionPassengers> passengersList) {
        Map<String, String> sectionIdMap = DataBaseLoading.sectionIdMap;
        String split = requestCommand.getStartTime().split(" ")[0];
        String year = split.split("-")[0];
        TableNamePojo tableNamePojo = new TableNamePojo(year);
        ArrayList<Object[]> arrayList = new ArrayList<>(5000);
        double sum = 0;
        for (SectionPassengers section : passengersList) {
            Double passengers = section.getPassengers();
            sum += passengers;
            float floatPassengers = passengers.floatValue();
            Object[] objects = {requestCommand.getDateDt(), sectionIdMap.get(section.getInId() + " " + section.getOutId()),
                    section.getInId(), section.getOutId(), requestCommand.getStartTime(), requestCommand.getEndTime(), floatPassengers};
            arrayList.add(objects);
        }
        log.info("区间流量总人数为{}", sum);
        StaticSQL staticSQL = new StaticSQL();
        String tableName;
        if ("static".equals(requestCommand.getCommand())) {
            if (requestCommand.getTimeInterval() == 60) {
                tableName = tableNamePojo.getTheStatic().get("oneSectionTable");
            } else {
                tableName = tableNamePojo.getTheStatic().get("halfSectionTable");
            }
        } else {
            if (requestCommand.getTimeInterval() == 60) {
                tableName = tableNamePojo.getTheDynamic().get("oneSectionTable");
            } else {
                tableName = tableNamePojo.getTheDynamic().get("halfSectionTable");
            }
        }
        String saveSectionSql = staticSQL.getStaticQuarterSection(tableName);
        try {
            jdbcTemplate.batchUpdate(saveSectionSql, arrayList);
            log.info("区间断面数据批量插入数据成功！,时间为{}{}", requestCommand.getStartTime(), requestCommand.getEndTime());
        } catch (Exception e) {
            log.error("", e, e);
        }
    }

    public void saveTransfer(RequestCommand requestCommand, List<TransferPassengers> passengersList) {
        String split = requestCommand.getStartTime().split(" ")[0];
        String year = split.split("-")[0];
        TableNamePojo tableNamePojo = new TableNamePojo(year);
        ArrayList<Object[]> arrayList = new ArrayList<>(5000);
        for (TransferPassengers transfer : passengersList) {
            Object[] objects = {requestCommand.getDateDt(), requestCommand.getStartTime(), requestCommand.getEndTime(),
                    transfer.getTransfer(), transfer.getOutLineId(), transfer.getInLineId(),
                    transfer.getUpInUpOutNum(), transfer.getUpInDownOutNum(), transfer.getDownInUpOutNum(), transfer.getDownInDownOutNum()};
            arrayList.add(objects);
        }
        StaticSQL staticSQL = new StaticSQL();
        String tableName;
        if ("static".equals(requestCommand.getCommand())) {
            if (requestCommand.getTimeInterval() == 60) {
                tableName = tableNamePojo.getTheStatic().get("oneTransferTable").toString();
            } else {
                tableName = tableNamePojo.getTheStatic().get("halfTransferTable").toString();
            }
        } else {
            if (requestCommand.getTimeInterval() == 60) {
                tableName = tableNamePojo.getTheDynamic().get("oneTransferTable").toString();
            } else {
                tableName = tableNamePojo.getTheDynamic().get("halfTransferTable").toString();
            }
        }
        String saveSectionSql = staticSQL.getStaticTransferInsert(tableName);
        try {
            jdbcTemplate.batchUpdate(saveSectionSql, arrayList);
            log.info("换乘数据批量插入数据成功！,时间为{}{}", requestCommand.getStartTime(), requestCommand.getEndTime());
        } catch (Exception e) {
            log.error("", e, e);
        }
    }

    public void saveStation(RequestCommand requestCommand, List<StationPassengers> passengersList) {
        String split = requestCommand.getStartTime().split(" ")[0];
        String year = split.split("-")[0];
        TableNamePojo tableNamePojo = new TableNamePojo(year);
        ArrayList<Object[]> arrayList = new ArrayList<>(5000);
        HashMap<String, String> stationIdDetermine = commonMethod.stationIdDetermine();
        for (StationPassengers station : passengersList) {
            Object[] objects = {requestCommand.getDateDt(), stationIdDetermine.get(station.getStation()),
                    requestCommand.getStartTime(), requestCommand.getEndTime(),
                    station.getInPassengers(), station.getOutPassengers(), station.getInPassengers() + station.getOutPassengers()};
            arrayList.add(objects);
        }
        StaticSQL staticSQL = new StaticSQL();
        String tableName;
        if ("static".equals(requestCommand.getCommand())) {
            if (requestCommand.getTimeInterval() == 60) {
                tableName = tableNamePojo.getTheStatic().get("oneStationTable").toString();
            } else {
                tableName = tableNamePojo.getTheStatic().get("halfStationTable").toString();
            }
        } else {
            if (requestCommand.getTimeInterval() == 60) {
                tableName = tableNamePojo.getTheDynamic().get("oneStationTable").toString();
            } else {
                tableName = tableNamePojo.getTheDynamic().get("halfStationTable").toString();
            }
        }
        String stationSql = staticSQL.getStaticStationInAndOut(tableName);
        try {
            jdbcTemplate.batchUpdate(stationSql, arrayList);
            log.info("批量插入进出站数据成功！日期为{},{}", requestCommand.getStartTime(), requestCommand.getEndTime());
        } catch (Exception e) {
            log.warn("批量插入进出站数据失败！原因为{}", e.getMessage());
        }
    }
}
