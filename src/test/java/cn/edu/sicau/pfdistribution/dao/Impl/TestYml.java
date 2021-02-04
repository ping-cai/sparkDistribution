package cn.edu.sicau.pfdistribution.dao.Impl;

import cn.edu.sicau.pfdistribution.config.AliDataSourceProperties;
import cn.edu.sicau.pfdistribution.config.DataSourceCommonProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestYml {
    @Autowired
    DataSourceCommonProperties dataSourceCommonProperties;
    @Autowired
    AliDataSourceProperties aliDataSourceProperties;
    @Test
    public void testData(){
        System.out.println(aliDataSourceProperties.getHive().get("jdbc_url"));
        System.out.println(aliDataSourceProperties.getHive().get("username"));
        System.out.println(aliDataSourceProperties.getHive().get("password"));
        System.out.println(aliDataSourceProperties.getHive().get("driver-class-name"));
    }
}
