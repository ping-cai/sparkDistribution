package cn.edu.sicau.pfdistribution.dao.tonghaoSave;

import cn.edu.sicau.pfdistribution.Utils.CommonMethod;
import cn.edu.sicau.pfdistribution.dao.SavePassengerFlow;
import cn.edu.sicau.pfdistribution.entity.TableNamePojo;
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.GetPassengerFlowOracle;
import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import cn.edu.sicau.pfdistribution.entity.jiaoda.StoreTransferData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.*;

@Repository
public class OneSavePassengerFlow implements SavePassengerFlow, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(GetPassengerFlowOracle.class);
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    @Autowired
    CommonMethod commonMethod;

    @Override
    public void saveStationInAndOutData(String dataDT, String stationId, String startTime, String endTime, float entryNum, float exitNum, RequestCommand requestCommand) {
        String split = startTime.split(" ")[0];
        String year = split.split("-")[0];
        TableNamePojo tableNamePojo = new TableNamePojo(year);
        {
            StaticSQL staticSQL = new StaticSQL();
            String oneStationTable;
            if ("static".equals(requestCommand.getCommand())) {
                oneStationTable = tableNamePojo.getTheStatic().get("oneStationTable").toString();
            } else {
                oneStationTable = tableNamePojo.getTheDynamic().get("oneStationTable").toString();
            }
            String sql = staticSQL.getStaticStationInAndOut(oneStationTable);
            Object[] args = new Object[]{dataDT, stationId, startTime, endTime, entryNum, exitNum, entryNum + exitNum};
            try {
                jdbcTemplate.update(sql, args);
            } catch (Exception e) {
                logger.error("1个小时进出站数据入库失败！失败原因:{}", e.getMessage());
            }
        }
    }

    @Override
    public void saveSectionData(String dataDT, String stationIn, String stationOut, String startTime, String endTime, double passengers, RequestCommand requestCommand) {
        String split = startTime.split(" ")[0];
        String year = split.split("-")[0];
        TableNamePojo tableNamePojo = new TableNamePojo(year);
        HashMap<String, String> sectionIdMap = commonMethod.sectionIdDetermine();
        String sectionId = sectionIdMap.get(stationIn + " " + stationOut);
        StaticSQL staticSQL = new StaticSQL();
        String oneSectionTable;
        if ("static".equals(requestCommand.getCommand())) {
            oneSectionTable = tableNamePojo.getTheStatic().get("oneSectionTable").toString();
        } else {
            oneSectionTable = tableNamePojo.getTheDynamic().get("oneSectionTable").toString();
        }
        String sql = staticSQL.getStaticSection(oneSectionTable);
        Object[] sectionData = new Object[]{dataDT, sectionId, stationIn, stationOut, startTime, endTime, passengers};
        try {
            jdbcTemplate.update(sql, sectionData);
        } catch (Exception e) {
            logger.error("1个小时分配的区间断面数据入库失败！失败原因:{}", e.getMessage());
        }
    }

    @Override
    public void saveTransferInfo(String dataDT, String startTime, String endTime, ArrayList<StoreTransferData> arrayList, RequestCommand requestCommand) {
        String split = startTime.split(" ")[0];
        String year = split.split("-")[0];
        TableNamePojo tableNamePojo = new TableNamePojo(year);
        {
            StaticSQL staticSQL = new StaticSQL();
            String oneTransferTable;
            if ("static".equals(requestCommand.getCommand())) {
                oneTransferTable = tableNamePojo.getTheStatic().get("oneTransferTable").toString();
            } else {
                oneTransferTable = tableNamePojo.getTheDynamic().get("oneTransferTable").toString();
            }
            String insertSql = staticSQL.getStaticTransferInsert(oneTransferTable);
            String updateSql = staticSQL.getStaticTransferUpdate(oneTransferTable);
            for (int i = 0; i < arrayList.size(); i++) {
                Object[] object2 = new Object[]{arrayList.get(i).getUpInUpOutNum(), arrayList.get(i).getUpInDownOutNum(), arrayList.get(i).getDownInUpOutNum(), arrayList.get(i).getDownInDownOutNum(),
                        startTime, endTime, arrayList.get(i).getTransfer(), arrayList.get(i).getInLineId(), arrayList.get(i).getOutLineId()};
                Object[] object3 = new Object[]{dataDT, startTime, endTime, arrayList.get(i).getTransfer(), arrayList.get(i).getInLineId(), arrayList.get(i).getOutLineId(),
                        arrayList.get(i).getUpInUpOutNum(), arrayList.get(i).getUpInDownOutNum(), arrayList.get(i).getDownInUpOutNum(), arrayList.get(i).getDownInDownOutNum()};
                try {
                    int update = jdbcTemplate.update(updateSql, object2);
                    if (update == 0) {
                        jdbcTemplate.update(insertSql, object3);
                    }
                } catch (Exception e) {
                    logger.error("1个小时换乘数据入库失败！失败原因:{}", e.getMessage());
                }
            }
        }
    }
}
