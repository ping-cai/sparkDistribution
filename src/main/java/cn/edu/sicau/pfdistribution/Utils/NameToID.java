package cn.edu.sicau.pfdistribution.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NameToID implements Serializable {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public  HashMap<String,String> findID(){
        String sql = "SELECT CZ_ID,CZ_NAME FROM SCOTT.\"dic_station\"";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        HashMap<String,String> result = new HashMap<>();
        for (Map<String,Object> map : maps){
            String cz_id = map.get("CZ_ID").toString();
            String cz_name = map.get("CZ_NAME").toString();
            result.put(cz_name,cz_id);
        }
        return result;
    }
}
