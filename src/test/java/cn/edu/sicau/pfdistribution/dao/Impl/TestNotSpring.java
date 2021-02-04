package cn.edu.sicau.pfdistribution.dao.Impl;


import java.sql.*;

public class TestNotSpring {
    public static void main(String[] args) {
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            Connection connection = DriverManager.getConnection("jdbc:hive2://10.2.55.99:10000/shujuku6", "root", "");
            Statement statement = connection.createStatement();
            String sql = "select trim(in_name) inName,\n" +
                    "trim(out_name) outName,\n" +
                    "count(*) passengers\n" +
                    "from shujuku6.18_9_15 origin left outer join comDep.dic_section target on\n" +
                    "origin.in_name = target.cz1_name\n" +
                    "where trim(recivetime1)\n" +
                    "between '2018-09-01 14:25:37'\n" +
                    "and '2018-09-01 15:17:10'\n" +
                    "and trim(origin.recivetime2)\n" +
                    "between '2018-09-01 14:25:37'\n" +
                    "and '2018-09-01 15:17:10'\n" +
                    "and trim(origin.in_name) <> trim(origin.out_name)\n" +
                    "group by\n" +
                    "in_name,out_name";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
