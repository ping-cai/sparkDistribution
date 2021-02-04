package cn.edu.sicau.pfdistribution.dao.sql;

public class GetOracleSql {
    public static String halfHourTable() {
        return "SELECT\n" +
                "\ttrim(b.od_target) inName,\n" +
                "\ttrim(c.od_target) outName,\n" +
                "\tsum( a.\"passengers\" ) passengers \n" +
                "FROM\n" +
                "\tSCOTT.\"half_hour_distribution\" a\n" +
                "\tLEFT JOIN SCOTT.OD_TURE b ON to_number( a.\"in_id\" ) = b.OD_ORIGIN\n" +
                "\tLEFT JOIN SCOTT.OD_TURE c ON TO_NUMBER( a.\"out_id\" ) = c.od_origin \n" +
                "\tWHERE a.\"data_dt\"=? and\n" +
                "\ta.\"start_time\" = ?\n" +
                "\tand a.\"end_time\"= ?\n" +
                "GROUP BY\n" +
                "\tb.od_target,\n" +
                "\tc.od_target";
    }

    public static String oneHourTable() {
        return "SELECT\n" +
                "\ttrim(b.od_target) inName,\n" +
                "\ttrim(c.od_target) outName,\n" +
                "\tsum( a.\"passengers\" ) passengers \n" +
                "FROM\n" +
                "\tSCOTT.\"one_hour_distribution\" a\n" +
                "\tLEFT JOIN SCOTT.OD_TURE b ON to_number( a.\"in_id\" ) = b.OD_ORIGIN\n" +
                "\tLEFT JOIN SCOTT.OD_TURE c ON TO_NUMBER( a.\"out_id\" ) = c.od_origin \n" +
                "\tWHERE a.\"data_dt\"=? and\n" +
                "\ta.\"start_time\" >= ?\n" +
                "\tand a.\"end_time\"<= ?\n" +
                "GROUP BY\n" +
                "\tb.od_target,\n" +
                "\tc.od_target";
    }
}
