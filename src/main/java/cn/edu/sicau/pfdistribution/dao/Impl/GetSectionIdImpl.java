package cn.edu.sicau.pfdistribution.dao.Impl;
/**
 *@author ZhouZhiYuan
 */
import cn.edu.sicau.pfdistribution.dao.oracle.GetSectionId;
import cn.edu.sicau.pfdistribution.dao.oracle.OracleGetTransferStationsById;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository
public class GetSectionIdImpl implements GetSectionId {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    OracleGetTransferStationsById oracleGetTransferStationsById;

    @Override
    public List<Integer> sectionList(List<String> odStations) {
        List<Integer> sectionList=new ArrayList<>();

        /**
         *得到车站id对应的车站名
         */
        List<String> stations=oracleGetTransferStationsById.getStationsById(odStations);
        GetSectionId(sectionList, stations);

        return sectionList;
    }

    private void GetSectionId(List<Integer> sectionList, List<String> stations) {
        /**
         * 查询站名所对应的区间id
         */
        for(int i=0;i<stations.size()-1;i++){
            String station1 = stations.get(i);
            String station2 = stations.get(i+1);
            List sectionId=jdbcTemplate.queryForList("SELECT QJ_ID\n" +
                    "FROM \"SCOTT\".\"dic_section\"\n" +
                    "WHERE CZ1_NAME="+"'"+ station1+"'" +"AND CZ2_NAME="+"'"+ station2 +"'");
            Integer qjID;
            Iterator QJIDit = sectionId.iterator();
            while (QJIDit.hasNext()){
                Map qjMap= (Map) QJIDit.next();
                qjID= Integer.parseInt(qjMap.get("QJ_ID").toString());
                sectionList.add(qjID);
            }
        }
    }
}
