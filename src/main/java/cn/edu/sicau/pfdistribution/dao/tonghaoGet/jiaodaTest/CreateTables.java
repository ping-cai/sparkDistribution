package cn.edu.sicau.pfdistribution.dao.tonghaoGet.jiaodaTest;

import cn.edu.sicau.pfdistribution.entity.TableNamePojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class CreateTables {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public void createSectionTable(String year) {
        TableNamePojo tableNamePojo = new TableNamePojo(year);
        List<String> tableList = new ArrayList<>(13);
        tableList.add(tableNamePojo.getTheStatic().get("oneSectionTable"));
        tableList.add(tableNamePojo.getTheStatic().get("halfSectionTable"));
        tableList.add(tableNamePojo.getTheStatic().get("quarterSectionTable"));
        tableList.add(tableNamePojo.getTheDynamic().get("oneSectionTable"));
        tableList.add(tableNamePojo.getTheDynamic().get("halfSectionTable"));
        tableList.add(tableNamePojo.getTheDynamic().get("quarterSectionTable"));
        for (String tableName : tableList) {
            String sql = "CREATE TABLE " + tableName + " (\n" +
                    "  \"DATA_DT\" DATE ,\n" +
                    "  \"SECTION_ID\" VARCHAR2(30 BYTE) ,\n" +
                    "  \"START_STATION_ID\" VARCHAR2(30 BYTE) ,\n" +
                    "  \"END_STATION_ID\" VARCHAR2(30 BYTE) ,\n" +
                    "  \"START_TIME\" DATE ,\n" +
                    "  \"END_TIME\" DATE ,\n" +
                    "  \"PASGR_FLOW_QTTY\" BINARY_DOUBLE \n" +
                    ")";
            try {
                jdbcTemplate.update(sql);
                log.info("创建新表{}成功！", tableName);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
    }

    public void createStationTable(String year) {
        TableNamePojo tableNamePojo = new TableNamePojo(year);
        List<String> tableList = new ArrayList<>(13);
        tableList.add(tableNamePojo.getTheStatic().get("oneStationTable"));
        tableList.add(tableNamePojo.getTheStatic().get("halfStationTable"));
        tableList.add(tableNamePojo.getTheStatic().get("quarterStationTable"));
        tableList.add(tableNamePojo.getTheDynamic().get("oneStationTable"));
        tableList.add(tableNamePojo.getTheDynamic().get("halfStationTable"));
        tableList.add(tableNamePojo.getTheDynamic().get("quarterStationTable"));
        for (String tableName : tableList) {
            String sql = "CREATE TABLE " + tableName + " (\n" +
                    "  \"DATA_DT\" DATE ,\n" +
                    "  \"STATION_ID\" VARCHAR2(30 BYTE) ,\n" +
                    "  \"START_TIME\" DATE ,\n" +
                    "  \"END_TIME\" DATE ,\n" +
                    "  \"ENTRY_QUATITY\" BINARY_DOUBLE ,\n" +
                    "  \"EXIT_QUATITY\" BINARY_DOUBLE ,\n" +
                    "  \"ENTRY_EXIT_QUATITY\" BINARY_DOUBLE \n" +
                    ")";
            try {
                jdbcTemplate.update(sql);
                log.info("创建新表{}成功！", tableName);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
    }

    public void createTransferTable(String year) {
        TableNamePojo tableNamePojo = new TableNamePojo(year);
        List<String> tableList = new ArrayList<>(13);
        tableList.add(tableNamePojo.getTheStatic().get("oneTransferTable"));
        tableList.add(tableNamePojo.getTheStatic().get("halfTransferTable"));
        tableList.add(tableNamePojo.getTheStatic().get("quarterTransferTable"));
        tableList.add(tableNamePojo.getTheDynamic().get("oneTransferTable"));
        tableList.add(tableNamePojo.getTheDynamic().get("halfTransferTable"));
        tableList.add(tableNamePojo.getTheDynamic().get("quarterTransferTable"));
        for (String tableName : tableList) {
            String sql = "CREATE TABLE " + tableName + " (\n" +
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
            try {
                jdbcTemplate.update(sql);
                log.info("创建新表{}成功！", tableName);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
    }

    public void createAllTables(String year) {
        createSectionTable(year);
        createStationTable(year);
        createTransferTable(year);
    }
}
