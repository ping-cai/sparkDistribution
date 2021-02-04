package cn.edu.sicau.pfdistribution.service.netrouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class SaveSliceData {
    @Qualifier("hiveJdbcTemplate")
    @Autowired
    private JdbcTemplate hiveJdbcTemplate;

    @Qualifier("oracleJdbcTemplate")
    @Autowired
    private JdbcTemplate oracleJdbcTemplate;
    public List<SaveQuarterOd> getQuarterDataToOracle(String originTable, Integer startDay, Integer endDay) {
        String sql = "select in_number inNumber,in_name inName,from_unixtime(in_time*900,'yyyy-MM-dd HH:mm:ss') inTime,out_number outNumber,out_name outName,from_unixtime(out_time*900,'yyyy-MM-dd HH:mm:ss') outTime,count(*) passengers\n" +
                "from (select in_number,in_name,cast(unix_timestamp(recivetime1)/900 as bigint) in_time,out_number,out_name,cast(unix_timestamp(recivetime2)/900 as bigint) out_time from %s where day(recivetime1) between %s and %s ) a group by in_number,in_name,in_time,out_number,out_name,out_time";
        String selectSql = String.format(sql, originTable, startDay, endDay);
        RowMapper<SaveQuarterOd> rowMapper = new BeanPropertyRowMapper<>(SaveQuarterOd.class);
        try {
            List<SaveQuarterOd> quarterOdList = hiveJdbcTemplate.query(selectSql, rowMapper);
            log.info("OD数据从hive中读取成功！读取的表名为{},天数分别为{},{}", originTable, startDay, endDay);
            return quarterOdList;
        } catch (Exception e) {
            log.error("OD数据从hive中读取成功失败！失败原因{}", e.getMessage());
            log.error("报错堆栈{}", e, e);
        }
        return new ArrayList<>();
    }

    public void createQuarterOracleTable(String tableName) {
        String sql = "CREATE TABLE SCOTT.%s (\n" +
                "inNumber VARCHAR2 ( 30 ),\n" +
                "inName VARCHAR2 ( 30 ),\n" +
                "inTime VARCHAR2 ( 30 ),\n" +
                "outNumber VARCHAR2 ( 30 ),\n" +
                "outName VARCHAR2 ( 30 ),\n" +
                "outTime VARCHAR2 ( 30 ),\n" +
                "passengers NUMBER)";
        String createSql = String.format(sql, tableName);
        try {
            oracleJdbcTemplate.execute(createSql);
            log.info("OD数据表在oracle中创建成功！创建的表名为{}", tableName);
        } catch (Exception e) {
            log.warn("OD数据表在oracle中创建失败！失败原因{}", e.getMessage());
        }
    }

    public void saveQuarterDataToOracle(List<SaveQuarterOd> quarterOdList, String insertTable) {
        List<Object[]> arrayList = new ArrayList<>();
        for (SaveQuarterOd od :
                quarterOdList) {
            Object[] objects = {od.getInNumber(), od.getInName(), od.getInTime(), od.getOutNumber(), od.getOutName(), od.getOutTime(), od.getPassengers()};
            arrayList.add(objects);
        }
        String sql = "insert into SCOTT.%s(inNumber,inName,inTime,outNumber,outName,outTime,passengers) values(?,?,?,?,?,?,?)";
        String insertSql = String.format(sql, insertTable);
        try {
            oracleJdbcTemplate.batchUpdate(insertSql, arrayList);
            log.info("OD数据批量存入Oracle成功！插入的表名为{}", insertTable);
        } catch (Exception e) {
            log.error("OD数据批量存入Oracle失败！失败原因{}", e.getMessage());
        }
    }
}
