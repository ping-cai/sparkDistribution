package cn.edu.sicau.pfdistribution.dao.Impl;

import cn.edu.sicau.pfdistribution.config.AliDataSourceProperties;
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.GetPassengerFlowHive;
import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.*;
import java.util.List;
import java.util.Map;

@Repository
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class HiveTest {
    @Autowired
    @Qualifier("hiveJdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    @Autowired
    GetPassengerFlowHive getPassengerFlowHive;
    @Autowired
    AliDataSourceProperties aliDataSourceProperties;
    @Autowired
//    DataSourceCommonProperties dataSourceCommonProperties;

    @Test
    public void testHive() {
        String sql = "create table testHive1(id int,name string,sex string,age int,department string) row format  delimited fields terminated by ','";
        jdbcTemplate.execute(sql);
    }

    @Test
    public void testHiveOne() {
        Map<String, Integer> map = getPassengerFlowHive.oneHourGet("2018-09-01 07:00:00", "2018-09-01 07:00:00", "2018-09-01 09:00:00");
        System.out.println(map);
    }

    @Test
    public void testHiveHalf() {
        Map<String, Integer> map = getPassengerFlowHive.halfHourGet("2018-10-01 00:00:00", "2018-10-01 07:00:00", "2018-10-01 07:30:00");
        System.out.println(map);
    }

    @Test
    public void testHiveQuarterHourNoInclude() {
        List<GetQuarterPassengerFlow> list = getPassengerFlowHive.quarterHourNoInclude("2018-09-01 07:00:00", "2018-09-01 07:15:00");
        System.out.println(list);
    }

    @Test
    public void testHiveJdbc() {
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

    @Test
    public void testDataSource() {
        System.out.println(aliDataSourceProperties.getCommonConfig());
    }
}
