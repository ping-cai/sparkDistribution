package cn.edu.sicau.pfdistribution.service.jiaodaTest;

import cn.edu.sicau.pfdistribution.dao.sql.CreateOracleTables;
import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 第一步，接受RequestCommand请求
 * 第二步，装载hive中的数据
 * 第三步，计算hive中的数据，调用计算方法
 * public void quarterHourDistribution(String dateDt, String startTime, String endTime, Integer timeInterval, String command)
 * 静态计算和动态计算都要存在
 * 第四步，分配数据入库
 * @Author LiYongPing
 * @Date 2021-02-02
 * @LastUpdate 2021-02-02
 */
@Service
@Slf4j
public class CorrectionDataAllocation {
    @Autowired
    private BetterDistribution betterDistribution;
    @Autowired
    private CreateOracleTables createTestTables;
    public void CorrectDataTest(String fromTable,String targetTable, RequestCommand requestCommand) {
        createTestTables.createTable(targetTable, CreateOracleTables.CREATE_SECTION_TABLE);
        createTestTables.createTable(targetTable, CreateOracleTables.CREATE_STATION_TABLE);
        createTestTables.createTable(targetTable, CreateOracleTables.CREATE_TRANSFER_TABLE);
        log.info("开始执行{}","intervalTriggerTask方法");
        betterDistribution.intervalTriggerTask(fromTable,targetTable, requestCommand);
    }
}
