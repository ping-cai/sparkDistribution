package cn.edu.sicau.pfdistribution.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Data
@ConfigurationProperties(prefix = "spring.datasource")
public class AliDataSourceProperties {
    private Map<String, String> oracle;
    private Map<String, String> hive;
    private Map<String, String> commonConfig;
}
