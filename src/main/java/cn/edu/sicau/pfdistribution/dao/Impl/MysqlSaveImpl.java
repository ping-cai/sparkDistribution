package cn.edu.sicau.pfdistribution.dao.Impl;

import cn.edu.sicau.pfdistribution.dao.RegionSaveInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class MysqlSaveImpl implements RegionSaveInterface {

    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<Integer, Integer> selectLineId() {
        Map LineId = new HashMap();
        List rows = jdbcTemplate.queryForList("SELECT LINE_ID,CZ_ID\n" +
                "from \"SCOTT\".\"dic_linestation\"");
        Iterator it = rows.iterator();
        while (it.hasNext()) {
            Map userMap = (Map) it.next();
            Integer careID = Integer.parseInt(userMap.get("CZ_ID").toString());
            Integer lineID = Integer.parseInt(userMap.get("LINE_ID").toString());
            LineId.put(careID, lineID);
        }
        return LineId;
    }

    @Override
    public Map<Integer, List<String>> selectTime() {
        Map idTime = new HashMap();
        List rows = jdbcTemplate.queryForList("SELECT CZ_ID,ARR_TIME,DEP_TIME\n" +
                "from \"SCOTT\".\"base_khsk\"");
        Iterator it = rows.iterator();
        while (it.hasNext()) {
            Map userMap = (Map) it.next();
            Integer CZ_ID = Integer.parseInt(userMap.get("CZ_ID").toString());
            String ARR_TIME = (String) userMap.get("ARR_TIME");
            String ARR_TIME1 = ARR_TIME.replace(" ", "");
            String DEP_TIME = (String) userMap.get("DEP_TIME");
            String DEP_TIME1 = DEP_TIME.replace(" ", "");
            List<String> timeList = Arrays.asList(ARR_TIME1, DEP_TIME1);
            idTime.put(CZ_ID, timeList);
        }
        return idTime;
    }
}
