package cn.edu.sicau.pfdistribution.dao.tonghaoGet.jiaodaTest;


import cn.edu.sicau.pfdistribution.dao.BatchSaveInter;
import cn.edu.sicau.pfdistribution.entity.Command;
import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import cn.edu.sicau.pfdistribution.entity.jiaoda.StoreTransferData;
import cn.edu.sicau.pfdistribution.entity.jiaoda.TransferPassengers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class TransferDataSaveImpl implements BatchSaveInter, Serializable {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public <T> void saveData(String sql, Command requestCommand, List<T> passengersList) {
        ArrayList<Object[]> arrayList = new ArrayList<>(5000);
        for (TransferPassengers transfer : (List<TransferPassengers>) passengersList) {
            Object[] objects = {requestCommand.getDateDt(), requestCommand.getStartTime(), requestCommand.getEndTime(),
                    transfer.getTransfer(), transfer.getOutLineId(), transfer.getInLineId(),
                    transfer.getUpInUpOutNum(), transfer.getUpInDownOutNum(), transfer.getDownInUpOutNum(), transfer.getDownInDownOutNum()};
            arrayList.add(objects);
        }
        try {
            jdbcTemplate.batchUpdate(sql, arrayList);
            log.info("换乘数据批量插入数据成功！,时间为{}{}", requestCommand.getStartTime(), requestCommand.getEndTime());
        } catch (Exception e) {
            log.error("", e, e);
        }
    }

    @Override
    public <T> void saveTimeSliceData(String sql, Command requestCommand, List<T> passengersList) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArrayList<Object[]> arrayList = new ArrayList<>();
        for (StoreTransferData transfer : (List<StoreTransferData>) passengersList) {
            float UU = transfer.getUpInUpOutNum().floatValue();
            float UD = transfer.getUpInDownOutNum().floatValue();
            float DU = transfer.getDownInUpOutNum().floatValue();
            float DD = transfer.getDownInDownOutNum().floatValue();
            Object[] objects = {simpleDateFormat.format(transfer.getDate()), simpleDateFormat.format(transfer.getStartTime()), simpleDateFormat.format(transfer.getEndTime()),
                    transfer.getTransfer(), transfer.getOutLineId(), transfer.getInLineId(),
                    UU, UD, DU, DD};
            arrayList.add(objects);
        }
        try {
            jdbcTemplate.batchUpdate(sql, arrayList);
            log.info("换乘数据批量插入成功！时间日期为{},{}", requestCommand.getStartTime(), requestCommand.getEndTime());
        } catch (Exception e) {
            log.error("换乘数据批量插入失败！时间日期为{},{} 原因是{}", requestCommand.getStartTime(), requestCommand.getEndTime(), e.getMessage());
        }
    }

}
