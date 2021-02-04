package cn.edu.sicau.pfdistribution.service.jiaodaTest;

import cn.edu.sicau.pfdistribution.dao.BatchSaveInter;
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.jiaodaTest.SectionDataSaveImpl;
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.jiaodaTest.StationDataSaveImpl;
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.jiaodaTest.TransferDataSaveImpl;
import cn.edu.sicau.pfdistribution.entity.jiaoda.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
/**
 * @Author LiYongPing
 * @Date 2021-02-02
 * @LastUpdate 2021-02-02
 */
@Service
public class SaveDistribution implements Serializable {
    @Autowired
    private SectionDataSaveImpl sectionDataSave;
    @Autowired
    private StationDataSaveImpl stationDataSave;
    @Autowired
    private TransferDataSaveImpl transferDataSave;

    public void saveSection(String targetTable, RequestCommand requestCommand, List<SectionPassengers> sectionPassengersList) {
        String format = "%s_section";
        String sectionTable = String.format(format, targetTable);
        String sql = String.format(BatchSaveInter.SECTION_SQL, sectionTable);
        sectionDataSave.saveData(sql, requestCommand, sectionPassengersList);
    }

    public void saveStation(String targetTable, RequestCommand requestCommand, List<StationPassengers> stationPassengersList) {
        String format = "%s_station";
        String stationTable = String.format(format, targetTable);
        String sql = String.format(BatchSaveInter.STATION_SQL, stationTable);
        stationDataSave.saveData(sql, requestCommand, stationPassengersList);
    }

    public void saveTransfer(String targetTable, RequestCommand requestCommand, List<TransferPassengers> transferPassengersList) {
        String format = "%s_transfer";
        String transferTable = String.format(format, targetTable);
        String sql = String.format(BatchSaveInter.TRANSFER_SQL, transferTable);
        stationDataSave.saveData(sql, requestCommand, transferPassengersList);
    }

    public void saveTimeSliceSection(String targetTable, RequestCommand requestCommand, List<StoreSectionPassengers> sectionTimeSlice) {
        String format = "%s_section";
        String sectionTable = String.format(format, targetTable);
        String sql = String.format(BatchSaveInter.SECTION_SQL, sectionTable);
        sectionDataSave.saveTimeSliceData(sql, requestCommand, sectionTimeSlice);
    }

    public void saveTimeSliceStation(String targetTable, RequestCommand requestCommand, List<StationInOutSave> stationInOutSaveList) {
        String format = "%s_station";
        String stationTable = String.format(format, targetTable);
        String sql = String.format(BatchSaveInter.STATION_SQL, stationTable);
        stationDataSave.saveTimeSliceData(sql, requestCommand, stationInOutSaveList);
    }

    public void saveTimeSliceTransfer(String targetTable, RequestCommand requestCommand, List<StoreTransferData> transferDataList) {
        String format = "%s_transfer";
        String transferTable = String.format(format, targetTable);
        String sql = String.format(BatchSaveInter.TRANSFER_SQL, transferTable);
        transferDataSave.saveTimeSliceData(sql, requestCommand, transferDataList);
    }
}
