package cn.edu.sicau.pfdistribution.dao.sql;

/**
 * @author LiYongPing
 * 专门将SQL语句独立进行管理，方便添加修改表名
 * 获取数据功能相关表
 */
public class GetDataSql {
    public String oneHourTable(String database, String tableName) {
        String sql = "select trim(target1.od_target) inName,\n" +
                "trim(target2.od_target) outName,\n" +
                "sum(passengers) passengers\n" +
                "from %s.%s origin join shujuku6.od_reference target1 on origin.in_number=target1.od_origin \n" +
                "join shujuku6.od_reference target2 on origin.out_number=target2.od_origin where recivetime1 between ? and ? and recivetime2 between ? and ?\n" +
                "group by target1.od_target,target2.od_target";
        return String.format(sql, database, tableName);
    }

    public String halfHourTable(String database, String tableName) {
        String sql = "select trim(in_name) inName,\n" +
                "trim(out_name) outName,\n" +
                "sum(passengers) passengers\n" +
                "from %s.%s origin left outer join comDep.dic_section target on \n" +
                "origin.out_name = target.cz2_name\n" +
                "where trim(origin.recivetime1) = ? \n" +
                "and trim(origin.recivetime2) = ?\n" +
                "group by\n" +
                "in_name,out_name";
        return String.format(sql, database, tableName);
    }

    public String quarterHourGet(String database, String tableName) {
        String sql="select trim(recivetime1) inTime,\n" +
                "trim(recivetime2) outTime,\n" +
                "trim(in_name) inName,\n" +
                "trim(out_name) outName,\n" +
                "count(*) passengers,\n" +
                "cast(((unix_timestamp(recivetime2,'yyyy-MM-dd HH:mm')- unix_timestamp(recivetime1,'yyyy-MM-dd HH:mm'))/60) as int) minutes \n" +
                "from %s.%s origin left outer join comDep.dic_section target on\n" +
                "origin.in_name = target.cz1_name\n" +
                "where trim(origin.recivetime1)\n" +
                "between ?\n" +
                "and ?\n" +
                "and trim(origin.recivetime2) > ?\n" +
                "and trim(origin.in_name) <> trim(origin.out_name)\n" +
                "group by\n" +
                "in_name,out_name,recivetime1,recivetime2";
        return String.format(sql, database, tableName);
    }
}
