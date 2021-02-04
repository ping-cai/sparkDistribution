package cn.edu.sicau.pfdistribution.dao.tonghaoSave;

/**
 * @author LiYongPing
 * 专门将SQL语句独立进行管理，方便添加修改表名
 * 存储功能相关表
 */
public class StaticSQL {
    public String getStaticStationInAndOut(String tableName) {
        String sql = "INSERT INTO %s ( DATA_DT, STATION_ID, START_TIME, END_TIME, ENTRY_QUATITY, EXIT_QUATITY, ENTRY_EXIT_QUATITY )\n" +
                "VALUES\n" +
                "\t(\n" +
                "\tTO_DATE( ?, 'yyyy-mm-dd HH24:mi:ss' ),\n" +
                "\t?,\n" +
                "\tTO_DATE( ?, 'yyyy-mm-dd HH24:mi:ss' ),\n" +
                "\tTO_DATE( ?, 'yyyy-mm-dd HH24:mi:ss' ),\n" +
                "\t?,\n" +
                "\t?,\n" +
                "\t?)";
        return String.format(sql, tableName);
    }

    public String getStaticSection(String tableName) {
        String sql = "INSERT INTO %s\n" +
                "VALUES\n" +
                "\t(\n" +
                "\tTO_DATE( ?,'yyyy-mm-dd HH24:mi:ss' ),\n" +
                "\t?,\n" +
                "\t?,\n" +
                "\t?,\n" +
                "\tTO_DATE( ?, 'yyyy-mm-dd HH24:mi:ss' ),\n" +
                "\tTO_DATE(?, 'yyyy-mm-dd HH24:mi:ss' ),\n" +
                "\t?)";
        return String.format(sql, tableName);
    }

    public String getStaticTransferInsert(String tableName) {
        String sql = "INSERT INTO %s VALUES(\n" +
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
        return String.format(sql, tableName);
    }

    public String getStaticTransferUpdate(String tableName) {
        String sql = "UPDATE %s \n" +
                "SET TRAF_SIN_SOUT = TRAF_SIN_SOUT + ?,\n" +
                "TRAF_SIN_XOUT = TRAF_SIN_XOUT + ?,\n" +
                "TRAF_XIN_SOUT = TRAF_XIN_SOUT + ?,\n" +
                "TRAF_XIN_XOUT = TRAF_XIN_XOUT + ? \n" +
                "WHERE\n" +
                "\tSTART_TIME = TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "\tAND END_TIME = TO_DATE( ?, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
                "\tAND XFER_STATION_ID = ? \n" +
                "\tAND TRAF_IN_LINE_ID = ? \n" +
                "\tAND TRAF_OUT_LINE_ID = ?";
        return String.format(sql, tableName);
    }

    public String getStaticQuarterSection(String tableName) {
        String sql = "INSERT INTO %s VALUES(TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss'),\n" +
                "?,\n" +
                "?,\n" +
                "?,\n" +
                "TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss'),\n" +
                "TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss'),\n" +
                "?)";
        return String.format(sql, tableName);
    }

    public String getStaticQuarterStation(String tableName) {
        String sql = "INSERT INTO %s\n" +
                "VALUES\n" +
                "\t(\n" +
                "\t?,\n" +
                "\t?,\n" +
                "\t?,\n" +
                "\t?,\n" +
                "\t?,\n" +
                "\t?,\n" +
                "\t? \n" +
                "\t)";
        return String.format(sql, tableName);
    }

    public String getStaticQuarterTransfer(String tableName) {
        String sql = "INSERT INTO %s VALUES(?,?,?,?,?,?,?,?,?,?)";
        return String.format(sql, tableName);
    }

    public String saveSection(String tableName) {
        String sql = "INSERT INTO %s VALUES(TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss'),\n" +
                "?,\n" +
                "?,\n" +
                "?,\n" +
                "TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss'),\n" +
                "TO_DATE(?, 'yyyy-mm-dd hh24:mi:ss'),\n" +
                "?)";
        return String.format(sql, tableName);
    }
}
