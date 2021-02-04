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
public class QuarterPassengerFlowInfoImpl implements IPassengerFlowInfo {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(QuarterPassengerFlowInfoImpl.class);

    @Override
    public List<StationFlowInfo> getStationFlowInfo(String dataDT, String startTime, String endTime) {
        RowMapper<StationFlowInfo> mapper = new BeanPropertyRowMapper<>(StationFlowInfo.class);
        String sql = "SELECT\n" +
                "\tSTATION_ID StationId,\n" +
                "\tENTRY_QUATITY inPassengers,\n" +
                "\tEXIT_QUATITY outPassengers,\n" +
                "\tNVL( TRAF_SIN_SOUT + TRAF_SIN_XOUT + TRAF_XIN_SOUT + TRAF_XIN_XOUT, 0 ) transferPassengers \n" +
                "FROM\n" +
                "\tSCOTT.QUARTER_STATION_ACCESS\n" +
                "\tFULL JOIN SCOTT.QUARTER_STATION_TRANSFER ON SCOTT.QUARTER_STATION_ACCESS.STATION_ID = SCOTT.QUARTER_STATION_TRANSFER.XFER_STATION_ID \n" +
                "\tAND QUARTER_STATION_ACCESS.START_TIME = QUARTER_STATION_TRANSFER.START_TIME \n" +
                "\tAND QUARTER_STATION_ACCESS.END_TIME = QUARTER_STATION_TRANSFER.END_TIME \n" +
                "\tAND QUARTER_STATION_ACCESS.DATA_DT = QUARTER_STATION_TRANSFER.DATA_DT \n" +
                "WHERE\n" +
                "\tQUARTER_STATION_ACCESS.DATA_DT = TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "\tAND QUARTER_STATION_ACCESS.START_TIME BETWEEN TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "\tAND TO_DATE(\n" +
                "\t?,\n" +
                "\t'yyyy-mm-dd hh24:mi:ss')";
        endTime = DateExtendUtil.timeAddition(endTime, 0, -15);
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
        String sql = "SELECT\n" +
                "\tSTART_STATION_ID StationStartId,\n" +
                "\tEND_STATION_ID StationEndId,\n" +
                "\tPASGR_FLOW_QTTY passengers\n" +
                "FROM\n" +
                "\tSCOTT.\"QUARTER_SECTION_PASSENGER\" \n" +
                "WHERE\n" +
                "\tDATA_DT = TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "\tAND START_TIME BETWEEN TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ) AND TO_DATE(\n" +
                "\t?,\n" +
                "\t'yyyy-mm-dd hh24:mi:ss')";
        endTime = DateExtendUtil.timeAddition(endTime, 0, -15);
        Object[] objects = {dataDT, startTime, endTime};
        try {
            return jdbcTemplate.query(sql, mapper, objects);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}