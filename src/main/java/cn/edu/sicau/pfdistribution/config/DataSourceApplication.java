package cn.edu.sicau.pfdistribution.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;

@Service
public class DataSourceApplication {
    @Configuration
    public class DataSourceConfig {

        @Bean(name = "mysqlDataSource")
        @Qualifier("mysqlDataSource")
        @ConfigurationProperties(prefix = "spring.datasource.mysql")
        public DataSource mysqlDataSource() {
            return DataSourceBuilder.create().build();
        }

        @Bean(name = "hiveDataSource")
        @Qualifier("hiveDataSource")
        @ConfigurationProperties(prefix = "spring.datasource.hive")
        public DataSource hiveDataSource() {
            return DataSourceBuilder.create().build();
        }

        @Qualifier("oracleDataSource")
        @Primary
        @Bean
        @ConfigurationProperties(prefix = "spring.datasource.oracle")
        public DataSource oracleDataSource() {
            return DataSourceBuilder.create().build();
        }

        @Bean(name = "mysqlJdbcTemplate")
        public JdbcTemplate mysqlJdbcTemplate(
                @Qualifier("mysqlDataSource") DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }


        @Primary
        @Bean(name = "oracleJdbcTemplate")
        public JdbcTemplate oracleJdbcTemplate(
                @Qualifier("oracleDataSource") DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean(name = "hiveJdbcTemplate")
        public JdbcTemplate hiveJdbcTemplate(
                @Qualifier("hiveDruidDataSource") DataSource dataSource) {
            try {
                dataSource.setLoginTimeout(0);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new JdbcTemplate(dataSource);
        }
    }
}
