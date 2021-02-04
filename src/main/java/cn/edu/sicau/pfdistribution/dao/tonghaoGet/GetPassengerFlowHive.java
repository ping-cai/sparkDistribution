package cn.edu.sicau.pfdistribution.dao.tonghaoGet;

import cn.edu.sicau.pfdistribution.Utils.DateExtendUtil;
import cn.edu.sicau.pfdistribution.dao.GetPassengerFlowInter;
import cn.edu.sicau.pfdistribution.dao.sql.GetHiveSql;
import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow;
import cn.edu.sicau.pfdistribution.entity.jiaoda.ODPassengers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class GetPassengerFlowHive implements GetPassengerFlowInter, Serializable {
    @Autowired
    @Qualifier("hiveJdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    @Value("${theGet}")
    private String database;

    //    获取一个小时OD数据
    public Map<String, Integer> oneHourGet(String dataDT, String startTime, String endTime) {
        GetHiveSql getDataSQL = new GetHiveSql();
        int year = DateExtendUtil.strDateGet(startTime, DateExtendUtil.YEAR) - 2000;
        int month = DateExtendUtil.strDateGet(startTime, DateExtendUtil.MONTH);
        int day = DateExtendUtil.strDateGet(startTime, "day");
        String tableName = String.format("%s_%s_%s_60", year, month, day);
        String sql = getDataSQL.oneHourTable(database, tableName);
        RowMapper<ODPassengers> rowMapper = new BeanPropertyRowMapper<>(ODPassengers.class);
        HashMap<String, Integer> odPassengers = new HashMap<>();
        hourGet(startTime, endTime, sql, rowMapper, odPassengers);
        return odPassengers;
    }

    //      获取半个小时OD数据
    public Map<String, Integer> halfHourGet(String dataDT, String startTime, String endTime) {
        GetHiveSql getDataSQL = new GetHiveSql();
        int year = DateExtendUtil.strDateGet(startTime, DateExtendUtil.YEAR) - 2000;
        int month = DateExtendUtil.strDateGet(startTime, DateExtendUtil.MONTH);
        int day = DateExtendUtil.strDateGet(startTime, "day");
        String tableName = String.format("%s_%s_%s_30", year, month, day);
        String sql = getDataSQL.halfHourTable(database, tableName);
        RowMapper<ODPassengers> rowMapper = new BeanPropertyRowMapper<>(ODPassengers.class);
        HashMap<String, Integer> odPassengers = new HashMap<>();
        hourGet(startTime, endTime, sql, rowMapper, odPassengers);
        return odPassengers;
    }

    //      获取15分钟OD数据,O在一个区间D不在一个区间的数据
    public List<GetQuarterPassengerFlow> quarterHourNoInclude(String startTime, String endTime) {
        GetHiveSql getDataSQL = new GetHiveSql();
        int year = DateExtendUtil.strDateGet(startTime, DateExtendUtil.YEAR) - 2000;
        int month = DateExtendUtil.strDateGet(startTime, DateExtendUtil.MONTH);
        int day = DateExtendUtil.strDateGet(startTime, "day");
        String tableName = String.format("%s_%s_%s_15", year, month, day);
        String sql = getDataSQL.quarterHourGet(database, tableName);
        RowMapper<GetQuarterPassengerFlow> rowMapper = new BeanPropertyRowMapper<>(GetQuarterPassengerFlow.class);
        List<GetQuarterPassengerFlow> passengerFlows = new ArrayList<>(30000);
        try {
            log.info("正在查询数据进行分配中，请稍后……");
            passengerFlows = jdbcTemplate.query(sql, rowMapper, startTime, endTime, startTime);
            log.info("查询完毕，稍后进行计算,数据量大小：{}", passengerFlows.size());
        } catch (Exception e) {
            log.error("查询出现错误,原因是{}", e.getMessage(), e);
        }
        return passengerFlows;
    }

    public void hourGet(String startTime, String endTime, String sql, RowMapper<ODPassengers> rowMapper, HashMap<String, Integer> odPassengers) {
        try {
            log.info("正在查询数据进行分配中，请稍后……");
            List<ODPassengers> list = jdbcTemplate.query(sql, rowMapper, startTime, endTime, startTime, endTime);
            log.info("查询完毕，稍后进行计算,数据量大小：{}", list.size());
            for (ODPassengers passengers : list) {
                odPassengers.put(passengers.getInName() + " " + passengers.getOutName(), passengers.getPassengers());
            }
        } catch (Exception e) {
            log.error("查询出现错误,原因是{}", e.getMessage(), e);
        }
    }
    public List<GetQuarterPassengerFlow> quarterHourWithOneDay(String fromTable,String startTime,String endTime){
        GetHiveSql getDataSQL = new GetHiveSql();
        String sql = getDataSQL.getAllData(database, fromTable);
        RowMapper<GetQuarterPassengerFlow> rowMapper = new BeanPropertyRowMapper<>(GetQuarterPassengerFlow.class);
        List<GetQuarterPassengerFlow> passengerFlows = new ArrayList<>(2000000);
        try {
            log.info("正在查询数据进行分配中，请稍后……");
            passengerFlows = jdbcTemplate.query(sql, rowMapper,startTime,endTime);
            log.info("查询完毕，稍后进行计算,数据量大小：{}", passengerFlows.size());
            for (int i = 0; i < 10; i++) {
                log.info("数据为{}",passengerFlows.get(i));
            }
        } catch (Exception e) {
            log.error("查询出现错误,原因是{}", e.getMessage(), e);
        }
        return passengerFlows;
    }

}
