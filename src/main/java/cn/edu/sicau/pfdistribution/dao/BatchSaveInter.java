package cn.edu.sicau.pfdistribution.dao;

import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import java.util.List;

/**
 * 修改时间
 * @Author LiYongPing
 * @Date 2021-02-02
 */
public interface BatchSaveInter {
    String SECTION_SQL = "INSERT INTO %s\n" +
            "VALUES\n" +
            "\t(\n" +
            "\tTO_DATE( ?,'yyyy-mm-dd HH24:mi:ss' ),\n" +
            "\t?,\n" +
            "\t?,\n" +
            "\t?,\n" +
            "\tTO_DATE( ?, 'yyyy-mm-dd HH24:mi:ss' ),\n" +
            "\tTO_DATE(?, 'yyyy-mm-dd HH24:mi:ss' ),\n" +
            "\t?)";
    String STATION_SQL = "INSERT INTO %s ( DATA_DT, STATION_ID, START_TIME, END_TIME, ENTRY_QUATITY, EXIT_QUATITY, ENTRY_EXIT_QUATITY )\n" +
            "VALUES\n" +
            "\t(\n" +
            "\tTO_DATE( ?, 'yyyy-mm-dd HH24:mi:ss' ),\n" +
            "\t?,\n" +
            "\tTO_DATE( ?, 'yyyy-mm-dd HH24:mi:ss' ),\n" +
            "\tTO_DATE( ?, 'yyyy-mm-dd HH24:mi:ss' ),\n" +
            "\t?,\n" +
            "\t?,\n" +
            "\t?)";
    String TRANSFER_SQL = "INSERT INTO %s VALUES(\n" +
            "TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ),\n" +
            "TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ),\n" +
            "TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ),\n" +
            "?,\n" +
            "?,\n" +
            "?,\n" +
            "?,\n" +
            "?,\n" +
            "?,\n" +
            "?)";

    <T> void saveData(String sql,RequestCommand requestCommand, List<T> passengersList);
    <T> void saveTimeSliceData(String sql, RequestCommand requestCommand, List<T> passengersList);
}
