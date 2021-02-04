package cn.edu.sicau.pfdistribution.dao.oracle;

import java.util.List;

public interface OracleGetTransferStationsById {
    /**
     * 查询站点中的的换乘点，并返回结果
     */
    List<List<String>> getTransferStations(List<String> odStations);

    /**
     * @param odStations
     * @return
     */
    List<String> getStationsById(List<String> odStations);

    String selectLineName(Integer id);
}
