package cn.edu.sicau.pfdistribution.dao.betterSave;

import cn.edu.sicau.pfdistribution.Utils.DateExtendUtil;
import cn.edu.sicau.pfdistribution.dataInteraction.SectionFlowInfo;
import cn.edu.sicau.pfdistribution.dataInteraction.StationFlowInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class PassengerFlowToSys implements IPassengerFlow {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<StationFlowInfo> getStationFlowInfo(String dataDT, String startTime, String endTime, String stationTable, String transferTable) {
        RowMapper<StationFlowInfo> mapper = new BeanPropertyRowMapper<>(StationFlowInfo.class);
        String sql = "SELECT\n" +
                "\tSTATION_ID stationId,\n" +
                "\tENTRY_QUATITY inPassengers,\n" +
                "\tEXIT_QUATITY outPassengers,\n" +
                "\tNVL( TRAF_SIN_SOUT + TRAF_SIN_XOUT + TRAF_XIN_SOUT + TRAF_XIN_XOUT, 0 ) transferPassengers \n" +
                "FROM\n" +
                "\tSCOTT.%s stationTable\n" +
                "\tFULL JOIN SCOTT.%s transferTable ON stationTable.STATION_ID = transferTable.XFER_STATION_ID \n" +
                "\tAND stationTable.START_TIME = transferTable.START_TIME \n" +
                "\tAND stationTable.END_TIME = transferTable.END_TIME \n" +
                "\tAND stationTable.DATA_DT = transferTable.DATA_DT \n" +
                "WHERE\n" +
                "\tstationTable.DATA_DT = TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "\tAND stationTable.START_TIME BETWEEN TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "\tAND TO_DATE(\n" +
                "\t?,\n" +
                "\t'yyyy-mm-dd hh24:mi:ss')";
        String stationSql = String.format(sql, stationTable, transferTable);
        endTime = DateExtendUtil.timeAddition(endTime, 0, -15);
        Object[] objects = {dataDT, startTime, endTime};
        try {
            return jdbcTemplate.query(stationSql, mapper, objects);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<SectionFlowInfo> getSectionFlowInfo(String dataDT, String startTime, String endTime, String tableName) {

        RowMapper<SectionFlowInfo> mapper = new BeanPropertyRowMapper<>(SectionFlowInfo.class);
        String sql = "\tSELECT\n" +
                "\tSTART_STATION_ID stationStartId,\n" +
                "\tEND_STATION_ID stationEndId,\n" +
                "\tPASGR_FLOW_QTTY passengers\n" +
                "FROM\n" +
                "\tSCOTT.\"%s\" \n" +
                "WHERE\n" +
                "\tDATA_DT = TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "\tAND START_TIME BETWEEN TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss' ) AND TO_DATE(\n" +
                "\t?,\n" +
                "\t'yyyy-mm-dd hh24:mi:ss')";
        String sectionSql = String.format(sql, tableName);
        endTime = DateExtendUtil.timeAddition(endTime, 0, -15);
        Object[] objects = {dataDT, startTime, endTime};
        try {
            return jdbcTemplate.query(sectionSql, mapper, objects);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
