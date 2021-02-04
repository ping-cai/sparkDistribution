package cn.edu.sicau.pfdistribution.dao.sql;

/**
 * @author LiYongPing
 * 专门将SQL语句独立进行管理，方便添加修改表名和在Hive中获得数据
 * 获取数据功能相关表
 */
public class GetHiveSql extends GetDataSql {
    @Override
    public String oneHourTable(String database, String tableName) {
        String sql = "select trim(target1.od_target) inName,\n" +
                "trim(target2.od_target) outName,\n" +
                "sum(passengers) passengers\n" +
                "from %s.%s origin join shujuku6.od_reference target1 on origin.in_number= cast(target1.od_origin as int)\n" +
                "join shujuku6.od_reference target2 on origin.out_number=cast(target2.od_origin as int) where recivetime1 between ? and ? and\n" +
                "recivetime2 between ? and ?\n" +
                "group by target1.od_target,target2.od_target";
        return String.format(sql, database, tableName);
    }

    @Override
    public String halfHourTable(String database, String tableName) {
        String sql = "select trim(target1.od_target) inName,\n" +
                "trim(target2.od_target) outName,\n" +
                "sum(passengers) passengers\n" +
                "from %s.%s origin join shujuku6.od_reference target1 on origin.in_number= cast(target1.od_origin as int)\n" +
                "join shujuku6.od_reference target2 on origin.out_number=cast(target2.od_origin as int) where recivetime1 between ? and ? and\n" +
                "recivetime2 between ? and ?\n" +
                "group by target1.od_target,target2.od_target";
        return String.format(sql, database, tableName);
    }

    @Override
    public String quarterHourGet(String database, String tableName) {
        String sql="select trim(recivetime1) inTime,\n" +
                "trim(recivetime2) outTime,\n" +
                "trim(target1.od_target) inName,\n" +
                "trim(target2.od_target) outName,\n" +
                "count(*) passengers,\n" +
                "cast(((unix_timestamp(recivetime2,'yyyy-MM-dd HH:mm')- unix_timestamp(recivetime1,'yyyy-MM-dd HH:mm'))/60) as int) minutes\n" +
                "from %s.%s origin join shujuku6.od_reference target1 on origin.in_number= cast(target1.od_origin as int)\n" +
                "join shujuku6.od_reference target2 on origin.out_number=cast(target2.od_origin as int) where recivetime1 between ? and ?\n" +
                "and trim(origin.recivetime2) > ?\n" +
                "and trim(target1.od_target) <> trim(target2.od_target)\n" +
                "group by recivetime1,recivetime2,target1.od_target,target2.od_target";
        return String.format(sql, database, tableName);
    }

    public String getAllData(String database, String tableName){
        String sql = "select trim(in_time) as inTime,\n" +
                "from_unixtime(unix_timestamp(trim(in_time))+ cast(15*60 as bigint)) as outTime,\n" +
                "trim(target1.od_target) inName,\n" +
                "trim(target2.od_target) outName,\n" +
                "passengers,\n" +
                "cast(15 as int) minutes\n" +
                "from %s.%s origin join shujuku6.od_reference target1 on origin.in_number= cast(target1.od_origin as int)\n" +
                "join shujuku6.od_reference target2 on origin.out_number=cast(target2.od_origin as int)\n" +
                "where in_time between ? and ? and passengers>0 and target1.od_target<>target2.od_target";
        return String.format(sql, database, tableName);
    }
}
