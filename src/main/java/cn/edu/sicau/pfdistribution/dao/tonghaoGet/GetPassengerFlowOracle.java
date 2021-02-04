package cn.edu.sicau.pfdistribution.dao.tonghaoGet;

import cn.edu.sicau.pfdistribution.dao.GetPassengerFlowInter;
import cn.edu.sicau.pfdistribution.dao.sql.GetOracleSql;
import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow;
import cn.edu.sicau.pfdistribution.entity.jiaoda.ODPassengers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.*;

/**
 * 获取全面详细od信息数据的类
 * 操作：查询，插入，更新
 */
@Repository
public class GetPassengerFlowOracle implements GetPassengerFlowInter, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(GetPassengerFlowOracle.class);
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Integer> oneHourGet(String dataDT, String startTime, String endTime) {
        RowMapper<ODPassengers> rowMapper = new BeanPropertyRowMapper<>(ODPassengers.class);
//        String sql = "SELECT\n" +
//                "\tTRIM( \"one_hour_distribution\".\"in_name\" ) inName,\n" +
//                "\tTRIM( \"one_hour_distribution\".\"out_name\" ) outName,\n" +
//                "\tSUM( \"one_hour_distribution\".\"passengers\" ) passengers \n" +
//                "FROM\n" +
//                "\tSCOTT.\"one_hour_distribution\"\n" +
//                "\tLEFT JOIN SCOTT.\"dic_section\" ON \"one_hour_distribution\".\"in_name\" = \"dic_section\".CZ1_NAME \n" +
//                "WHERE\n" +
//                "\t\"one_hour_distribution\".\"data_dt\" = ? \n" +
//                "\tAND TO_DATE( \"one_hour_distribution\".\"start_time\", 'yyyy-mm-dd hh24:mi:ss' ) \n" +
//                "\tBETWEEN TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
//                "\tAND TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
//                "GROUP BY\n" +
//                "\t\"one_hour_distribution\".\"in_name\",\n" +
//                "\t\"one_hour_distribution\".\"out_name\"";
        String oneTable = GetOracleSql.oneHourTable();
        HashMap<String, Integer> odPassengers = new HashMap<>();
        List<ODPassengers> list = jdbcTemplate.query(oneTable, rowMapper, dataDT, startTime, endTime);
        for (ODPassengers passengers : list) {
            odPassengers.put(passengers.getInName() + " " + passengers.getOutName(), passengers.getPassengers());
        }
        return odPassengers;
    }

    @Override
    public Map<String, Integer> halfHourGet(String dataDT, String startTime, String endTime) {
        RowMapper<ODPassengers> rowMapper = new BeanPropertyRowMapper<>(ODPassengers.class);
//        String sql = "SELECT\n" +
//                "\tTRIM( \"half_hour_distribution\".\"in_name\" ) inName,\n" +
//                "\tTRIM( \"half_hour_distribution\".\"out_name\" ) outName,\n" +
//                "\tsum( \"half_hour_distribution\".\"passengers\" ) passengers \n" +
//                "FROM\n" +
//                "\tSCOTT.\"half_hour_distribution\",\n" +
//                "\tSCOTT.\"dic_section\" \n" +
//                "WHERE\n" +
//                "\t\"data_dt\" = ? \n" +
//                "\tAND \"half_hour_distribution\".\"start_time\" = ? \n" +
//                "\tAND \"half_hour_distribution\".\"end_time\" = ? \n" +
//                "\tAND \"half_hour_distribution\".\"out_name\" = \"dic_section\".CZ2_NAME \n" +
//                "GROUP BY\n" +
//                "\t\"half_hour_distribution\".\"in_name\",\n" +
//                "\t\"half_hour_distribution\".\"out_name\"";
        String hourTable = GetOracleSql.halfHourTable();
        HashMap<String, Integer> odPassengers = new HashMap<>();
        List<ODPassengers> list = null;
        try {
            list = jdbcTemplate.query(hourTable, rowMapper, dataDT, startTime, endTime);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        for (ODPassengers passengers : list) {
            odPassengers.put(passengers.getInName() + " " + passengers.getOutName(), passengers.getPassengers());
        }
        return odPassengers;
    }

    @Override
    public List<GetQuarterPassengerFlow> quarterHourNoInclude(String startTime, String endTime) {
        RowMapper<GetQuarterPassengerFlow> rowMapper = new BeanPropertyRowMapper<>(GetQuarterPassengerFlow.class);
        String sql = "SELECT\n" +
                "\"quarter_hour_distribution\".IN_TIME inTime,\n" +
                "\"quarter_hour_distribution\".OUT_TIME outTime,\n" +
                "TRIM(\"quarter_hour_distribution\".\"inName\") inName,\n" +
                "TRIM( \"quarter_hour_distribution\".\"outName\") outName,\n" +
                "COUNT(*) passengers,\n" +
                "(ROUND(TO_NUMBER(TO_DATE(OUT_TIME, 'yyyy-mm-dd hh24:mi:ss') - TO_DATE(IN_TIME, 'yyyy-mm-dd hh24:mi:ss')) * 24 * 60)) minutes \n" +
                "FROM\n" +
                "SCOTT.\"quarter_hour_distribution\"\n" +
                "LEFT JOIN SCOTT.\"dic_section\" ON \"quarter_hour_distribution\".\"inName\" = \"dic_section\".CZ1_NAME \n" +
                "WHERE\n" +
                "TO_DATE( \"quarter_hour_distribution\".IN_TIME,'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "BETWEEN TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "AND TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss' )\n" +
                "AND TRIM(\"quarter_hour_distribution\".\"inName\") <> TRIM( \"quarter_hour_distribution\".\"outName\")\n" +
                "GROUP BY\n" +
                "\"quarter_hour_distribution\".\"inName\",\n" +
                "\"quarter_hour_distribution\".\"outName\",\n" +
                "\"quarter_hour_distribution\".IN_TIME,\n" +
                "\"quarter_hour_distribution\".OUT_TIME\n" +
                "ORDER BY\n" +
                "\"quarter_hour_distribution\".OUT_TIME";
        List<GetQuarterPassengerFlow> passengerFlows = null;
        try {
            logger.info("正在进行查询，请稍后……");
            passengerFlows = jdbcTemplate.query(sql, rowMapper, startTime, endTime);
            logger.info("查询完毕，稍后进行计算,数据量大小：{}", passengerFlows.size());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return passengerFlows;
    }
}
