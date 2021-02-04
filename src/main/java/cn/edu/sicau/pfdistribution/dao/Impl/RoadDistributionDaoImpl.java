package cn.edu.sicau.pfdistribution.dao.Impl;

import cn.edu.sicau.pfdistribution.dao.RoadDistributionDao;
import cn.edu.sicau.pfdistribution.entity.Section;
import cn.edu.sicau.pfdistribution.entity.SimpleStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoadDistributionDaoImpl implements RoadDistributionDao, Serializable {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取所有站点的所属的线路信息
     *
     * @return
     */
    @Override
    public Map<String, List<String>> getAllStationInfo() {
//        获取含站点名的sql语句
//        String sql = "SELECT station.CZ_NAME stationName, station.LJM line FROM \"SCOTT\".\"dic_station\" station";
        String getIdLineInfoSql = "SELECT station.CZ_ID stationName, target.LJM line \n" +
                "FROM SCOTT.\"dic_station\" station JOIN SCOTT.\"dic_station\" target on  station.CZ_NAME=target.CZ_NAME ORDER BY station.CZ_ID\n";
        RowMapper rowMapper = new BeanPropertyRowMapper(SimpleStation.class);
        List<SimpleStation> stations = jdbcTemplate.query(getIdLineInfoSql, rowMapper);
        Map<String, List<String>> stationList = new HashMap<>();
        for (int i = 0; i < stations.size(); i++) {
            String stationName = stations.get(i).getStationName();
            if (stationList.containsKey(stationName)) {
                List<String> lines = stationList.get(stationName);
                lines.add(stations.get(i).getLine());
                stationList.put(stationName, lines);
            } else {
                List<String> lines = new ArrayList<>();
                lines.add(stations.get(i).getLine());
                stationList.put(stationName, lines);
            }
        }
        return stationList;
    }

    /**
     * 获取所有的区间信息
     *
     * @return List<Section> 区间List集合
     */
    @Override
    public List<Section> getAllSection() {
        return getMoreSection();
    }

    public List<Section> getSection() {
        String sql = "SELECT QJ_ID sectionId, CZ1_ID fromId, CZ1_NAME fromName, \n" +
                "\tCZ2_ID toId, CZ2_NAME toName, \n" +
                "\tQJ_SXX direction, QJ_LENGTH weight FROM \"SCOTT\".\"dic_section\"";
        String getIdSectionSql = "SELECT QJ_ID sectionId,CZ1_ID fromId,CZ1_ID fromName,\n" +
                "CZ2_ID toId, CZ2_ID toName,\n" +
                "QJ_SXX direction, QJ_LENGTH weight\n" +
                "FROM SCOTT.\"dic_section\"";
        RowMapper<Section> rowMapper = new BeanPropertyRowMapper<>(Section.class);
        return jdbcTemplate.query(getIdSectionSql, rowMapper);
    }

    public Map<String, String> getTransferStation() {
        HashMap<String, String> hashMap = new HashMap<>();
        String sql = " SELECT CZ_ID,CZ_NAME FROM SCOTT.\"dic_station\" WHERE CZ_NAME in (\n" +
                "  SELECT CZ1_NAME FROM (select DISTINCT CZ1_ID,CZ1_NAME FROM SCOTT.\"dic_section\") GROUP BY CZ1_NAME HAVING count(*)>=2) ORDER BY CZ_NAME";
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql);
        for (int i = 0; i < mapList.size(); i++) {
            if (hashMap.containsKey(String.valueOf(mapList.get(i).get("CZ_NAME")))) {
                hashMap.put(String.valueOf(mapList.get(i).get("CZ_NAME")), hashMap.get(String.valueOf(mapList.get(i).get("CZ_NAME"))) + " " + String.valueOf(mapList.get(i).get("CZ_ID")));
            } else {
                hashMap.put(String.valueOf(mapList.get(i).get("CZ_NAME")), String.valueOf(mapList.get(i).get("CZ_ID")));
            }
        }
        return hashMap;
    }

    /**
     * 本应该是处理业务的逻辑层
     * section:
     * private Integer sectionId;
     * private Integer fromId;
     * private String fromName;
     * private Integer toId;
     * private String toName;
     * private String direction;
     * private double weight;
     *
     * @return List<Section>
     */
    public List<Section> getMoreSection() {
        List<Section> sectionList = getSection();
        Map<String, String> transferStation = getTransferStation();
        for (Map.Entry<String, String> entry : transferStation.entrySet()) {
            String key = entry.getKey();
            String values = transferStation.get(key);
            String[] split = values.split(" ");
            for (int i = 0; i < split.length - 1; i++) {
                Section fromSection = new Section(sectionList.size() + 1, Integer.valueOf(split[i]), split[i], Integer.valueOf(split[i + 1]), split[i + 1], "o", 0.004);
                Section toSection = new Section(sectionList.size() + 2, Integer.valueOf(split[i + 1]), split[i + 1], Integer.valueOf(split[i]), split[i], "o", 0.004);
                sectionList.add(fromSection);
                sectionList.add(toSection);
            }
        }
        return sectionList;
    }
}