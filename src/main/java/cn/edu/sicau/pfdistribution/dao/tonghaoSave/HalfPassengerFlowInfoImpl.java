package cn.edu.sicau.pfdistribution.dao.tonghaoSave;

import cn.edu.sicau.pfdistribution.Utils.DateExtendUtil;
import cn.edu.sicau.pfdistribution.dataInteraction.IPassengerFlowInfo;
import cn.edu.sicau.pfdistribution.dataInteraction.SectionFlowInfo;
import cn.edu.sicau.pfdistribution.dataInteraction.StationFlowInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 从数据库中取出数据
 * 返回给通号需要的数据格式
 */
@Repository
public class HalfPassengerFlowInfoImpl implements IPassengerFlowInfo {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(HalfPassengerFlowInfoImpl.class);

    @Override
    public List<StationFlowInfo> getStationFlowInfo(String dataDT, String startTime, String endTime) {
        RowMapper<StationFlowInfo> mapper = new BeanPropertyRowMapper<>(StationFlowInfo.class);
        String sql = "SELECT\n" +
                "\tHALF_STATION_ACCESS.STATION_ID StationId,\n" +
                "\tHALF_STATION_ACCESS.ENTRY_QUATITY inPassengers,\n" +
                "\tHALF_STATION_ACCESS.EXIT_QUATITY outPassengers,\n" +
                "\tNVL( HALF_STATION_TRANSFER.TRAF_SIN_SOUT + HALF_STATION_TRANSFER.TRAF_SIN_XOUT + HALF_STATION_TRANSFER.TRAF_XIN_SOUT + HALF_STATION_TRANSFER.TRAF_XIN_XOUT, 0 ) transferPassengers \n" +
                "FROM\n" +
                "\tSCOTT.HALF_STATION_TRANSFER\n" +
                "\tFULL JOIN SCOTT.HALF_STATION_ACCESS ON HALF_STATION_TRANSFER.XFER_STATION_ID = HALF_STATION_ACCESS.STATION_ID\n" +
                "\tAND HALF_STATION_ACCESS.START_TIME = HALF_STATION_TRANSFER.START_TIME \n" +
                "\tAND HALF_STATION_ACCESS.END_TIME =HALF_STATION_TRANSFER.END_TIME \n" +
                "\tAND HALF_STATION_ACCESS.DATA_DT = HALF_STATION_TRANSFER.DATA_DT \n" +
                "WHERE\n" +
                "\tHALF_STATION_ACCESS.DATA_DT = TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "\tAND HALF_STATION_ACCESS.START_TIME BETWEEN TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "\tAND TO_DATE(\n" +
                "\t?,\n" +
                "\t'yyyy-mm-dd hh24:mi:ss')";
        endTime = DateExtendUtil.timeAddition(endTime, 0, -30);
        Object[] objects = {dataDT, startTime, endTime};
        try {
            return jdbcTemplate.query(sql, mapper, objects);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public List<SectionFlowInfo> getSectionFlowInfo(String dataDT, String startTime, String endTime) {

        RowMapper<SectionFlowInfo> mapper = new BeanPropertyRowMapper<>(SectionFlowInfo.class);
        String sql = "\tSELECT\n" +
                "\tSTART_STATION_ID StationStartId,\n" +
                "\tEND_STATION_ID StationEndId,\n" +
                "\tPASGR_FLOW_QTTY passengers\n" +
                "FROM\n" +
                "\tSCOTT.\"HALF_SECTION_PASSENGER\" \n" +
                "WHERE\n" +
                "\tDATA_DT = TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "\tAND START_TIME BETWEEN TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss' ) AND TO_DATE(\n" +
                "\t?,\n" +
                "\t'yyyy-mm-dd hh24:mi:ss')";
        endTime = DateExtendUtil.timeAddition(endTime, 0, -30);
        Object[] objects = {dataDT, startTime, endTime};
        try {
            return jdbcTemplate.query(sql, mapper, objects);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
