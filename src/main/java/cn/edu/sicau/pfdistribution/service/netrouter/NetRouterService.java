package cn.edu.sicau.pfdistribution.service.netrouter;

import cn.edu.sicau.pfdistribution.Utils.DateExtendUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NetRouterService {
    @Autowired
    private SaveSliceData saveSliceData;

    @Async("dataCalculationExecutor")
    public void QuarterToOracle(String originTable, Integer cycle) {
        String[] split = originTable.split("_");
        String year = split[1];
        String month = split[2];
        String tableName = "OD_%s_%s_%s";
        int startDay = (cycle + 1) * 7 - 6;
        int endDay = (cycle + 1) * 7;
        String targetTable = String.format(tableName, year, month, (cycle + 1) * 7);
        saveSliceData.createQuarterOracleTable(targetTable);
        List<SaveQuarterOd> saveQuarterOdList = saveSliceData.getQuarterDataToOracle(originTable, startDay, endDay);
        if (!saveQuarterOdList.isEmpty()) {
            saveSliceData.saveQuarterDataToOracle(saveQuarterOdList, targetTable);
        }
    }

}
