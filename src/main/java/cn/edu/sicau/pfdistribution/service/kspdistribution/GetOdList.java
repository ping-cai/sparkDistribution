package cn.edu.sicau.pfdistribution.service.kspdistribution;

import cn.edu.sicau.pfdistribution.dao.Impl.MysqlGetID;
import cn.edu.sicau.pfdistribution.dao.Impl.OracleImpl;
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.HalfSavePassengerFlow;
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.OneSavePassengerFlow;
import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@Slf4j
public class GetOdList implements Serializable {
    transient
    @Autowired
    MysqlGetID getOD;
    transient
    @Autowired
    OracleImpl test;
    transient
    @Autowired
    OneSavePassengerFlow oneSavePassengerFlow;
    transient
    @Autowired
    HalfSavePassengerFlow halfSavePassengerFlow;

    //存储AFC区间分配结果
    public void saveOD(String date_day, String section_in, String section_out, String startTime, String endTime, double passengers, RequestCommand requestCommand) {
        float p = (float) passengers;
        if (Double.isNaN(passengers)) {
            p = 0;
        }
        try {
            if (requestCommand.getTimeInterval() == 60) {
                oneSavePassengerFlow.saveSectionData(date_day, section_in, section_out, startTime, endTime, p, requestCommand);
            }
            if (requestCommand.getTimeInterval() == 30) {
                halfSavePassengerFlow.saveSectionData(date_day, section_in, section_out, startTime, endTime, p, requestCommand);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(p);
        }
    }
    /**
     * 进出站数据
     *
     * @param data_day       数据日期
     * @param startTime      开始小时
     * @param endTime        结束分钟
     * @param station        站点id
     * @param in_passengers  进站人数
     * @param out_passengers 出站人数
     */
    public void saveStationPassengers(String data_day, String startTime, String endTime, String station, double in_passengers, double out_passengers, RequestCommand requestCommand) {
        float in = (float) in_passengers;
        if (Double.isNaN(in_passengers)) {
            in = 0;
        }
        float out = (float) out_passengers;
        if (Double.isNaN(out_passengers)) {
            out = 0;
        }
        try {
            if (requestCommand.getTimeInterval() == 60) {
                oneSavePassengerFlow.saveStationInAndOutData(data_day, station, startTime, endTime, in, out, requestCommand);
            }
            if (requestCommand.getTimeInterval() == 30) {
                halfSavePassengerFlow.saveStationInAndOutData(data_day, station, startTime, endTime, in, out, requestCommand);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(station + "--" + in + "--" + out);

        }

    }
}
