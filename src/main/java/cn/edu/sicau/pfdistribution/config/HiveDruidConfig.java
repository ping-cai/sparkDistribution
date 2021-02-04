package cn.edu.sicau.pfdistribution.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@Slf4j
@EnableConfigurationProperties({DataSourceProperties.class, DataSourceCommonProperties.class})
public class HiveDruidConfig {
    @Autowired
    AliDataSourceProperties aliDataSourceProperties;
    @Autowired
    DataSourceCommonProperties dataSourceCommonProperties;

    @Bean("hiveDruidDataSource")
    @Qualifier("hiveDruidDataSource")
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
//        配置数据源属性
        dataSource.setUrl(aliDataSourceProperties.getHive().get("jdbc_url"));
        dataSource.setUsername(aliDataSourceProperties.getHive().get("username"));
        dataSource.setPassword(aliDataSourceProperties.getHive().get("password"));
        dataSource.setDriverClassName(aliDataSourceProperties.getHive().get("driver-class-name"));
//        配置统一属性
        dataSource.setInitialSize(dataSourceCommonProperties.getInitialSize());
        dataSource.setMinIdle(dataSourceCommonProperties.getMinIdle());
        dataSource.setMaxActive(dataSourceCommonProperties.getMaxActive());
        dataSource.setMaxWait(dataSourceCommonProperties.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(dataSourceCommonProperties.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(dataSourceCommonProperties.getMinEvictableIdleTimeMillis());
        dataSource.setValidationQuery(dataSourceCommonProperties.getValidationQuery());
        dataSource.setTestWhileIdle(dataSourceCommonProperties.isTestWhileIdle());
        dataSource.setTestOnBorrow(dataSourceCommonProperties.isTestOnBorrow());
        dataSource.setTestOnReturn(dataSourceCommonProperties.isTestOnReturn());
        try {
            dataSource.setFilters(dataSourceCommonProperties.getFilters());
        } catch (SQLException e) {
            log.error("Druid configuration initialization filter error.", e);
        }
        return dataSource;
    }

}
