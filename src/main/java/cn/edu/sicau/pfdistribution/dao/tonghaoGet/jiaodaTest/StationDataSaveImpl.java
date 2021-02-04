package cn.edu.sicau.pfdistribution.dao.tonghaoGet.jiaodaTest;

import cn.edu.sicau.pfdistribution.Utils.CommonMethod;
import cn.edu.sicau.pfdistribution.dao.BatchSaveInter;
import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import cn.edu.sicau.pfdistribution.entity.jiaoda.StationInOutSave;
import cn.edu.sicau.pfdistribution.entity.jiaoda.StationPassengers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@Slf4j
public class StationDataSaveImpl implements BatchSaveInter, Serializable {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CommonMethod commonMethod;

    @Override
    public <T> void saveData(String sql, RequestCommand requestCommand, List<T> passengersList) {
        ArrayList<Object[]> arrayList = new ArrayList<>(5000);
        HashMap<String, String> stationIdDetermine = commonMethod.stationIdDetermine();
        for (StationPassengers station : (List<StationPassengers>) passengersList) {
            Object[] objects = {requestCommand.getDateDt(), stationIdDetermine.get(station.getStation()),
                    requestCommand.getStartTime(), requestCommand.getEndTime(),
                    station.getInPassengers(), station.getOutPassengers(), station.getInPassengers() + station.getOutPassengers()};
            arrayList.add(objects);
        }
        try {
            jdbcTemplate.batchUpdate(sql, arrayList);
            log.info("批量插入进出站数据成功！日期为{},{}", requestCommand.getStartTime(), requestCommand.getEndTime());
        } catch (Exception e) {
            log.warn("批量插入进出站数据失败！原因为{}", e.getMessage());
        }
    }

    @Override
    public <T> void saveTimeSliceData(String sql, RequestCommand requestCommand, List<T> passengersList) {
        String dataDt = requestCommand.getDateDt();
        HashMap<String, String> stationIdMap = commonMethod.stationIdDetermine();
        ArrayList<Object[]> arrayList = new ArrayList<>();
        for (StationInOutSave station :
                (List<StationInOutSave>) passengersList) {
            Object[] objects = {dataDt, stationIdMap.get(station.getInName()),
                    station.getStartTime(), station.getEndTime(), station.getInPassengers(), station.getOutPassengers(), station.getInPassengers() + station.getOutPassengers()};
            arrayList.add(objects);
        }
        try {
            jdbcTemplate.batchUpdate(sql, arrayList);
            log.info("批量插入车站进出量数据成功!时间日期为{},{}", arrayList.get(0)[2], arrayList.get(0)[3]);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("批量插入车站进出量数据失败!时间日期为{},{}", arrayList.get(0)[2], arrayList.get(0)[3]);
        }
    }
}
