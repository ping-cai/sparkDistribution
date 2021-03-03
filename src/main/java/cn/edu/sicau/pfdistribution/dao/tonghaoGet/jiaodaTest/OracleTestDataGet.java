package cn.edu.sicau.pfdistribution.dao.tonghaoGet.jiaodaTest;

import cn.edu.sicau.pfdistribution.dao.GetFlowInter;
import cn.edu.sicau.pfdistribution.dao.GetPassengerFlowInter;
import cn.edu.sicau.pfdistribution.entity.correct.GetQuarterFlow;
import cn.edu.sicau.pfdistribution.entity.correct.PassengerFlow;
import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class OracleTestDataGet implements GetFlowInter {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Integer> oneHourGet(String dataDT, String startTime, String endTime) {
        return null;
    }

    @Override
    public Map<String, Integer> halfHourGet(String dataDT, String startTime, String endTime) {
        return null;
    }

    @Override
    public List<GetQuarterPassengerFlow> quarterHourNoInclude(String startTime, String endTime) {
        RowMapper<GetQuarterPassengerFlow> rowMapper = new BeanPropertyRowMapper<>(GetQuarterPassengerFlow.class);
        String sql = "SELECT\n" +
                "origin.IN_TIME inTime,\n" +
                "origin.OUT_TIME outTime,\n" +
                "TRIM(t1.CZ_ID) inName,\n" +
                "TRIM(t2.CZ_ID) outName,\n" +
                "sum(PASSENGERS) passengers,\n" +
                "(ROUND(TO_NUMBER(OUT_TIME - IN_TIME) * 24 * 60)) minutes\n" +
                "FROM\n" +
                "SCOTT.\"TEST_QUARTER_DATA\" origin\n" +
                "JOIN SCOTT.TRUE_OD t1 ON origin.IN_NUMBER = t1.STATIONID\n" +
                "JOIN SCOTT.TRUE_OD t2 ON origin.OUT_NUMBER= t2.STATIONID\n" +
                "WHERE\n" +
                "origin.IN_TIME\n" +
                "BETWEEN TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "AND TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss' )\n" +
                "AND TRIM(t1.CZ_ID)<>TRIM(t2.CZ_ID)\n" +
                "GROUP BY\n" +
                "origin.IN_TIME,\n" +
                "origin.OUT_TIME,\n" +
                "TRIM(t1.CZ_ID),\n" +
                "TRIM(t2.CZ_ID)";
        List<GetQuarterPassengerFlow> passengerFlows = null;
        try {
            log.info("正在进行查询，请稍后……");
            passengerFlows = jdbcTemplate.query(sql, rowMapper, startTime, endTime);
            log.info("查询完毕，稍后进行计算,数据量大小：{}", passengerFlows.size());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return passengerFlows;
    }

    @Override
    public List<GetQuarterFlow> quarterPassengers(String startTime, String endTime) {
        RowMapper<GetQuarterFlow> rowMapper = new BeanPropertyRowMapper<>(GetQuarterFlow.class);
        String sql = "SELECT\n" +
                "origin.IN_TIME inTime,\n" +
                "origin.OUT_TIME outTime,\n" +
                "TRIM(t1.CZ_ID) inName,\n" +
                "TRIM(t2.CZ_ID) outName,\n" +
                "sum(PASSENGERS) passengers,\n" +
                "(ROUND(TO_NUMBER(OUT_TIME - IN_TIME) * 24 * 60)) minutes\n" +
                "FROM\n" +
                "SCOTT.\"TEST_QUARTER_DATA\" origin\n" +
                "JOIN SCOTT.TRUE_OD t1 ON origin.IN_NUMBER = t1.STATIONID\n" +
                "JOIN SCOTT.TRUE_OD t2 ON origin.OUT_NUMBER= t2.STATIONID\n" +
                "WHERE\n" +
                "origin.IN_TIME\n" +
                "BETWEEN TO_DATE('2018-10-17 06:00:00', 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "AND TO_DATE('2018-10-17 07:00:00', 'yyyy-mm-dd hh24:mi:ss' )\n" +
                "GROUP BY\n" +
                "origin.IN_TIME,\n" +
                "origin.OUT_TIME,\n" +
                "TRIM(t1.CZ_ID),\n" +
                "TRIM(t2.CZ_ID)";
        List<GetQuarterFlow> passengerFlows = null;
        try {
            log.info("正在进行查询，请稍后……");
            passengerFlows = jdbcTemplate.query(sql, rowMapper, startTime, endTime);
            log.info("查询完毕，稍后进行计算,数据量大小：{}", passengerFlows.size());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return passengerFlows;
    }
}
