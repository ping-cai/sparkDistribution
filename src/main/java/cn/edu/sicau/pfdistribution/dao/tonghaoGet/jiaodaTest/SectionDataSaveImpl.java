package cn.edu.sicau.pfdistribution.dao.tonghaoGet.jiaodaTest;

import cn.edu.sicau.pfdistribution.Utils.DataBaseLoading;
import cn.edu.sicau.pfdistribution.dao.BatchSaveInter;
import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import cn.edu.sicau.pfdistribution.entity.jiaoda.SectionPassengers;
import cn.edu.sicau.pfdistribution.entity.jiaoda.StoreSectionPassengers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class SectionDataSaveImpl implements BatchSaveInter, Serializable {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public <T> void saveData(String sql, RequestCommand requestCommand, List<T> passengersList) {
        Map<String, String> sectionIdMap = DataBaseLoading.sectionIdMap;
        ArrayList<Object[]> arrayList = new ArrayList<>(5000);
        double sum = 0;
        for (SectionPassengers section : (List<SectionPassengers>) passengersList) {
            Double passengers = section.getPassengers();
            sum += passengers;
            float floatPassengers = passengers.floatValue();
            Object[] objects = {requestCommand.getDateDt(), sectionIdMap.get(section.getInId() + " " + section.getOutId()),
                    section.getInId(), section.getOutId(), requestCommand.getStartTime(), requestCommand.getEndTime(), floatPassengers};
            arrayList.add(objects);
        }
        log.info("区间流量总人数为{}", sum);
        try {
            jdbcTemplate.batchUpdate(sql, arrayList);
            log.info("区间断面数据批量插入数据成功！,时间为{}{}", requestCommand.getStartTime(), requestCommand.getEndTime());
        } catch (Exception e) {
            log.error("", e, e);
        }
    }

    @Override
    public <T> void saveTimeSliceData(String sql, RequestCommand requestCommand, List<T> passengersList) {
        ArrayList<Object[]> arrayList = new ArrayList<>();
        for (StoreSectionPassengers sectionPassengers : (List<StoreSectionPassengers>) passengersList) {
            Object[] objects = {sectionPassengers.getDataDT(), sectionPassengers.getSectionId(), sectionPassengers.getInId(), sectionPassengers.getOutId(),
                    sectionPassengers.getInTime(), sectionPassengers.getOutTime(), sectionPassengers.getPassengers()};
            arrayList.add(objects);
        }
        try {
            jdbcTemplate.batchUpdate(sql, arrayList);
            log.info("区间断面数据批量插入成功!,时间日期为{},{}", requestCommand.getStartTime(), requestCommand.getEndTime());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
