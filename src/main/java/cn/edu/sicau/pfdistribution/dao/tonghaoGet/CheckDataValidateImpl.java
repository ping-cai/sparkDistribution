package cn.edu.sicau.pfdistribution.dao.tonghaoGet;

import cn.edu.sicau.pfdistribution.Utils.DateExtendUtil;
import cn.edu.sicau.pfdistribution.dao.CheckDataValidateInter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@Slf4j
public class CheckDataValidateImpl implements CheckDataValidateInter {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean checkDataExistence(String tableName, String startTime, Integer timeInterval) {
        int month = DateExtendUtil.strDateGet(startTime, DateExtendUtil.MONTH);
        int endDay = (int) (15 + Math.random() * 15);
        String sql = "SELECT count(count(START_TIME)) dataVolume FROM %s WHERE TO_CHAR(START_TIME,'mm')=? and TO_NUMBER(TO_CHAR(START_TIME,'dd'))  BETWEEN 1 and ? GROUP BY START_TIME ORDER BY START_TIME";
        String dataNumberSql = String.format(sql, tableName);
        log.info("正在检查表中存在数据量的合法性！请稍后……");
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(dataNumberSql, month, endDay);
            int dataVolume = Integer.parseInt(String.valueOf(map.get("dataVolume")));
            log.info("检查表{}在1到{}天内存在的聚合数据量为{}", tableName, endDay, dataVolume);
            return dataVolume >= (endDay * 14 * 60) / timeInterval;
        } catch (Exception e) {
            log.warn("检查数据失败，原因是{}", e, e);
            return false;
        }


    }

    @Override
    public void deleteIncompleteData(String tableName, String startTime, String endTime, Integer timeInterval) {
        if (!checkDataExistence(tableName, startTime, timeInterval)) {
            String sql = "delete FROM %s WHERE START_TIME BETWEEN TO_DATE(?,'yyyy-mm-dd hh24:mi:ss') and TO_DATE(?,'yyyy-mm-dd hh24:mi:ss')";
            String deleteSql = String.format(sql, tableName);
            log.info("正在删除不合法数据中……");
            try {
                int update = jdbcTemplate.update(deleteSql, startTime, endTime);
                log.info("删除的数据量大小为{},时间位于{},{}之间", update, startTime, endTime);
            } catch (Exception e) {
                log.warn("删除数据失败，原因是{}", e, e);
            }
        }
    }
}
