package cn.edu.sicau.pfdistribution.dao.tonghaoSave;

import cn.edu.sicau.pfdistribution.Utils.CommonMethod;
import cn.edu.sicau.pfdistribution.Utils.DateExtendUtil;
import cn.edu.sicau.pfdistribution.entity.TableNamePojo;
import cn.edu.sicau.pfdistribution.entity.jiaoda.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.*;

@Repository
public class QuarterSaveEspecially implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(QuarterSaveEspecially.class);
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CommonMethod commonMethod;

    public void saveSectionTimeSliceData(List<StoreSectionPassengers> sectionTimeSlice, RequestCommand requestCommand) {
        String split = requestCommand.getStartTime().split(" ")[0];
        String year = split.split("-")[0];
        TableNamePojo tableNamePojo = new TableNamePojo(year);
        ArrayList<Object[]> arrayList = new ArrayList<>();
        for (StoreSectionPassengers sectionPassengers : sectionTimeSlice) {
            Object[] objects = {sectionPassengers.getDataDT(), sectionPassengers.getSectionId(), sectionPassengers.getInId(), sectionPassengers.getOutId(),
                    sectionPassengers.getInTime(), sectionPassengers.getOutTime(), sectionPassengers.getPassengers()};
            arrayList.add(objects);
        }
        StaticSQL staticSQL = new StaticSQL();
        String quarterSectionTable;
        if ("static".equals(requestCommand.getCommand())) {
            quarterSectionTable = tableNamePojo.getTheStatic().get("quarterSectionTable").toString();
        } else {
            quarterSectionTable = tableNamePojo.getTheDynamic().get("quarterSectionTable").toString();
        }
        String sql = staticSQL.getStaticQuarterSection(quarterSectionTable);
        try {
            jdbcTemplate.batchUpdate(sql, arrayList);
            logger.info("区间断面数据批量插入成功!,时间日期为{},{}", requestCommand.getStartTime(), requestCommand.getEndTime());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void saveTransferTimeSliceData(List<StoreTransferData> transferDataList, RequestCommand requestCommand) {
        String split = requestCommand.getStartTime().split(" ")[0];
        String year = split.split("-")[0];
        TableNamePojo tableNamePojo = new TableNamePojo(year);
        ArrayList<Object[]> arrayList = new ArrayList<>();
        for (StoreTransferData transfer : transferDataList) {
            Object[] objects = {transfer.getDate(), transfer.getStartTime(), transfer.getEndTime(), transfer.getTransfer(), transfer.getOutLineId(), transfer.getInLineId(),
                    transfer.getUpInUpOutNum(), transfer.getUpInDownOutNum(), transfer.getDownInUpOutNum(), transfer.getDownInDownOutNum()};
            arrayList.add(objects);
        }
        StaticSQL staticSQL = new StaticSQL();
        String quarterTransferTable;
        if ("static".equals(requestCommand.getCommand())) {
            quarterTransferTable = tableNamePojo.getTheStatic().get("quarterTransferTable");
        } else {
            quarterTransferTable = tableNamePojo.getTheDynamic().get("quarterTransferTable");
        }
        String sql = staticSQL.getStaticQuarterTransfer(quarterTransferTable);
        try {
            jdbcTemplate.batchUpdate(sql, arrayList);
            logger.info("换乘数据批量插入成功！时间日期为{},{}", requestCommand.getStartTime(), requestCommand.getEndTime());
        } catch (Exception e) {
            logger.info("换乘数据批量插入失败！时间日期为{},{}", requestCommand.getStartTime(), requestCommand.getEndTime());
        }
    }

    public void saveStationInOutTimeSliceData(String dataDt, List<StationInOutSave> stationInOutSaveList, RequestCommand requestCommand) {
        String split = requestCommand.getStartTime().split(" ")[0];
        String year = split.split("-")[0];
        TableNamePojo tableNamePojo = new TableNamePojo(year);
        HashMap<String, String> stationIdMap = commonMethod.stationIdDetermine();
        StaticSQL staticSQL = new StaticSQL();
        String quarterStationTable;
        if ("static".equals(requestCommand.getCommand())) {
            quarterStationTable = tableNamePojo.getTheStatic().get("quarterStationTable");
        } else {
            quarterStationTable = tableNamePojo.getTheDynamic().get("quarterStationTable");
        }
        String sql = staticSQL.getStaticQuarterStation(quarterStationTable);
        ArrayList<Object[]> arrayList = new ArrayList<>();
        for (StationInOutSave station :
                stationInOutSaveList) {
            Object[] objects = {DateExtendUtil.stringToDate(dataDt, DateExtendUtil.FULL), stationIdMap.get(station.getInName()),
                    DateExtendUtil.stringToDate(station.getStartTime(), DateExtendUtil.FULL), DateExtendUtil.stringToDate(station.getEndTime(), DateExtendUtil.FULL), station.getInPassengers(), station.getOutPassengers(), station.getInPassengers() + station.getOutPassengers()};
            arrayList.add(objects);
        }
        try {
            jdbcTemplate.batchUpdate(sql, arrayList);
            logger.info("批量插入车站进出量数据成功!时间日期为{},{}", arrayList.get(0)[2], arrayList.get(0)[3]);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("批量插入车站进出量数据失败!时间日期为{},{}", arrayList.get(0)[2], arrayList.get(0)[3]);
        }
    }
}
