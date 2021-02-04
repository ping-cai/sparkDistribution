package cn.edu.sicau.pfdistribution.dao.Impl;

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
public class OracleGetTransferStationsByIdImpl implements OracleGetTransferStationsById {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    /**
     * 在数据库中查询线路中站点的站名
     */
    private String getLineSql = "select * from \"SCOTT\".\"dic_linestation\" where CZ_ID=";

    @Override
    public List<List<String>> getTransferStations(List<String> odStations) {
        int JudgeTransfer[] = new int[odStations.size()];
        List<List<String>> TransferResult = new ArrayList<>();

        //初始化前一个站点所属线路id
        List pre = jdbcTemplate.queryForList(getLineSql + Integer.parseInt(odStations.get(0)));
        int preLine;
        Iterator preIt = pre.iterator();
        Map LineMap_pre = (Map) preIt.next();
        preLine = Integer.parseInt(LineMap_pre.get("LINE_ID").toString());
        //初始化当前站点所属线路id
        List mid = jdbcTemplate.queryForList(getLineSql + Integer.parseInt(odStations.get(1)));
        int midLine;
        Iterator midIt = mid.iterator();
        Map LineMap_mid = (Map) midIt.next();
        midLine = Integer.parseInt(LineMap_mid.get("LINE_ID").toString());
        //标记第一个站和第二个站
        JudgeTransfer[0] = 1;
        JudgeTransfer[1] = 1;
        //迭代判断每个站点是否是换乘点
        for (int i = 1; i < odStations.size() - 1; i++) {
            //获取到后一个站所属的线路名
            List rear = jdbcTemplate.queryForList(getLineSql + Integer.parseInt(odStations.get(i + 1)));
            int rearLine;
            Iterator rearIt = rear.iterator();
            Map LineMap_rear = (Map) rearIt.next();
            rearLine = Integer.parseInt(LineMap_rear.get("LINE_ID").toString());
            //判断当前站点是否是换乘点
            JudgeTransfer[i] = 1;
            if (preLine == rearLine)
                JudgeTransfer[i] = 0;
            //更新前、中、后站点所属线路
            preLine = midLine;
            midLine = rearLine;
        }
        JudgeTransfer[odStations.size() - 1] = 1;
        //换乘站标记完毕，将换乘站列表装入返回结果
        List<String> transferStationsList = new ArrayList<>();
        for (int i = 0; i < odStations.size(); i++) {
            if (JudgeTransfer[i] == 1) {
                List cz_name = jdbcTemplate.queryForList(getLineSql + Integer.parseInt(odStations.get(i)));
                String Cz_name;
                Iterator czIt = cz_name.iterator();
                Map czMap = (Map) czIt.next();
                Cz_name = (String) czMap.get("CZ_NAME");
                int len = transferStationsList.size();
                if (len != 0 && Cz_name.equals(transferStationsList.get(len - 1)))
                    continue;
                else {
                    transferStationsList.add(Cz_name);
                    //获得当前站点所在线路
                    String lineName = selectLineName(Integer.parseInt(odStations.get(i)));
                    transferStationsList.add(lineName);
                }
            }
        }
        for (int i = 0; i < transferStationsList.size() - 3; i = i + 4) {
            List<String> transferResult = new ArrayList<>();
            transferResult.add(transferStationsList.get(i));
            transferResult.add(transferStationsList.get(i + 2));
            transferResult.add(transferStationsList.get(i + 3));
            TransferResult.add(transferResult);
        }
        return TransferResult;
    }


    /**
     * 查询站点id所对应的站名
     *
     * @param odStations 一条路径所经过的所有站点的id
     * @return
     */
    @Override
    public List<String> getStationsById(List<String> odStations) {
        List<String> stations = new ArrayList<>();
        for (int i = 0; i < odStations.size(); i++) {
            List cz_name = jdbcTemplate.queryForList(getLineSql + Integer.parseInt(odStations.get(i)));
            String Cz_name;
            Iterator czIt = cz_name.iterator();
            Map czMap = (Map) czIt.next();
            Cz_name = (String) czMap.get("CZ_NAME");
            int len = stations.size();
            /**
             * 筛出重复站点
             */
            if (len != 0 && Cz_name.equals(stations.get(len - 1)))
                continue;
            else stations.add(Cz_name);
        }
        return stations;
    }

    /**
     * 通过站点id查询当前路径经过的站点所在的线路
     * 不同线路上的每个站点都有唯一的id与之对应，同一站点可能有多个id（如：换乘点）
     *
     * @param id 车站的Id
     * @return 返回查询出来的线路名
     */

    @Override
    public String selectLineName(Integer id) {
        Map stationNameMap = jdbcTemplate.queryForMap("SELECT LJM\n" +
                "from \"SCOTT\".\"dic_station\"\n" +
                "WHERE CZ_ID=" + id + "");
        return (String) stationNameMap.get("LJM");
    }
}

