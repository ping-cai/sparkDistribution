package cn.edu.sicau.pfdistribution.dao.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @Author LiYongPing
 * @Date 2021-02-02
 * @LastUpdate 2021-02-02
 */
@Repository
@Slf4j
public class CreateOracleTables {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    public static final String CREATE_SECTION_TABLE = "CREATE TABLE %s_section(\n" +
            "  \"DATA_DT\" DATE ,\n" +
            "  \"SECTION_ID\" VARCHAR2(30 BYTE) ,\n" +
            "  \"START_STATION_ID\" VARCHAR2(30 BYTE) ,\n" +
            "  \"END_STATION_ID\" VARCHAR2(30 BYTE) ,\n" +
            "  \"START_TIME\" DATE ,\n" +
            "  \"END_TIME\" DATE ,\n" +
            "  \"PASGR_FLOW_QTTY\" BINARY_DOUBLE \n" +
            ")";
    public static final String CREATE_STATION_TABLE = "CREATE TABLE %s_station(\n" +
            "  \"DATA_DT\" DATE ,\n" +
            "  \"STATION_ID\" VARCHAR2(30 BYTE) ,\n" +
            "  \"START_TIME\" DATE ,\n" +
            "  \"END_TIME\" DATE ,\n" +
            "  \"ENTRY_QUATITY\" BINARY_DOUBLE ,\n" +
            "  \"EXIT_QUATITY\" BINARY_DOUBLE ,\n" +
            "  \"ENTRY_EXIT_QUATITY\" BINARY_DOUBLE \n" +
            ")";

    public static final String CREATE_TRANSFER_TABLE = "CREATE TABLE %s_transfer(\n" +
            "  \"DATA_DT\" DATE ,\n" +
            "  \"START_TIME\" DATE ,\n" +
            "  \"END_TIME\" DATE ,\n" +
            "  \"XFER_STATION_ID\" VARCHAR2(30 BYTE) ,\n" +
            "  \"TRAF_IN_LINE_ID\" VARCHAR2(30 BYTE) ,\n" +
            "  \"TRAF_OUT_LINE_ID\" VARCHAR2(30 BYTE) ,\n" +
            "  \"TRAF_SIN_SOUT\" BINARY_DOUBLE NOT NULL ,\n" +
            "  \"TRAF_SIN_XOUT\" BINARY_DOUBLE NOT NULL ,\n" +
            "  \"TRAF_XIN_SOUT\" BINARY_DOUBLE NOT NULL ,\n" +
            "  \"TRAF_XIN_XOUT\" BINARY_DOUBLE NOT NULL \n" +
            ")";

    public void createTable(String tableName, String sql) {
        String createSql = String.format(sql, tableName);
        try {
            jdbcTemplate.update(createSql);
            log.info("创建新表{}成功！", tableName);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}
