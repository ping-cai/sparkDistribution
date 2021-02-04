package cn.edu.sicau.pfdistribution.service.Web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author weiyongzhao
 * return 两个车站id和区间id的map
 */

@Repository
public class GetCZ12IdFromDatabaseImpl implements GetCZ12IdFromDatabase {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    JdbcTemplate jdbcTemplate;


    @Override
    public Map getCZ12IDNoParameterFromDatabase() {
        Map result = new HashMap();
        try {
            List rows = jdbcTemplate.queryForList("SELECT CZ1_ID,CZ2_ID,QJ_ID FROM \"SCOTT\".\"dic_section\"");
            Iterator it = rows.iterator();
            while (it.hasNext()) {
                Map userMap = (Map) it.next();
                Integer CZ1_ID = Integer.parseInt(userMap.get("CZ1_ID").toString());
                Integer CZ2_ID = Integer.parseInt(userMap.get("CZ2_ID").toString());
                String CZ12_ID = CZ1_ID.toString() + " " + CZ2_ID.toString();
                Integer SectionID = Integer.parseInt(userMap.get("QJ_ID").toString());
                result.put(CZ12_ID, SectionID);
            }
        } catch (Exception e) {
            result.put("0", 0);
        }

        return result;
    }

    @Override
    public Map<String, Integer> getCZ12IDWithParameterFromDatabase(Integer id) {
        Map result = new HashMap();
        try {
            List rows = jdbcTemplate.queryForList("SELECT CZ1_ID,CZ2_ID,QJ_ID FROM \"SCOTT\".\"dic_section\" where QJ_ID =" + id + "");
            Iterator it = rows.iterator();
            while (it.hasNext()) {
                Map userMap = (Map) it.next();
                Integer SectionID = Integer.parseInt(userMap.get("QJ_ID").toString());
                if (SectionID.equals(id)) {
                    Integer CZ1_ID = Integer.parseInt(userMap.get("CZ1_ID").toString());
                    Integer CZ2_ID = Integer.parseInt(userMap.get("CZ2_ID").toString());
                    String CZ12_ID = CZ1_ID.toString() + " " + CZ2_ID.toString();
                    result.put(CZ12_ID, SectionID);
                }
            }
        } catch (Exception e) {
            result.put("0", 0);
        }
        return result;
    }

}
