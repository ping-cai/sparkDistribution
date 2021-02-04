package cn.edu.sicau.pfdistribution.kspcalculation;

import cn.edu.sicau.pfdistribution.Utils.CommonMethod;
import cn.edu.sicau.pfdistribution.Utils.DateExtendUtil;
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.GetPassengerFlowOracle;
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.QuarterSaveEspecially;
import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow;
import cn.edu.sicau.pfdistribution.entity.jiaoda.StationInOutSave;
import cn.edu.sicau.pfdistribution.entity.jiaoda.StoreSectionPassengers;
import cn.edu.sicau.pfdistribution.service.kspdistribution.MainDistribution;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@EnableAsync
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MainDistributionTest {
    @Autowired
    ThreadTest threadTest;
    @Autowired
    GetPassengerFlowOracle getPassengerFlowOracle;
    @Autowired
    ThreadScalaTest threadScalaTest;
    @Autowired
    RddTest rddTest;
    @Autowired
    CalculationTest calculationTest;
    @Autowired
    MainDistribution mainDistribution;
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CommonMethod commonMethod;
    @Autowired
    QuarterSaveEspecially quarterSaveEspecially;

//    @Test
//    public void testGet() throws InterruptedException {
//        Map<String, Integer> stringIntegerMap = getPassengerFlowDao.quarterHourGetInclude("2018-09-01 06:30:00", "2018-09-01 06:45:00");
//        for (Map.Entry<String, Integer> entry : stringIntegerMap.entrySet()) {
//            System.out.println(entry);
//        }
//    }

    @Test
    public void testExecutor() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            threadScalaTest.toTestThread();
        }
    }

    @Test
    public void testRdd() {
        rddTest.ListReduce();
    }

    @Test
    public void testSql15() {
        int theMinutes = DateExtendUtil.timeDifference("2018-09-01 06:00:00", "2018-09-01 24:00:00", DateExtendUtil.MINUTE);
        int range = theMinutes / 15;
        String dataDt = "2018-09-01 06:00:00";
        for (int i = 0; i < range; i++) {
            String startTime = DateExtendUtil.timeAddition(dataDt, 0, i * 15);
            String endTime = DateExtendUtil.timeAddition(startTime, 0, 15);
            try {
                List<GetQuarterPassengerFlow> getQuarterPassengerFlows = getPassengerFlowOracle.quarterHourNoInclude(startTime, endTime);
                List<StoreSectionPassengers> arrayList = rddTest.testOneGetTimeSlice(getQuarterPassengerFlows);
//                quarterSaveEspecially.saveSectionTimeSliceData(arrayList);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @Test
    public void testOdQuarterDistributionResult() {
        List<GetQuarterPassengerFlow> getQuarterPassengerFlows = getPassengerFlowOracle.quarterHourNoInclude("2018-09-01 08:30:00", "2018-09-01 08:45:00");

//        mainDistribution.quarterTransferReturn("2018-09-01 00:00:00",getQuarterPassengerFlows);
    }

    @Test
    public void testMain() {
        String dataD="2018-09-01 00:00:00";
        List<GetQuarterPassengerFlow> getQuarterPassengerFlows = getPassengerFlowOracle.quarterHourNoInclude("2018-09-02 08:30:00", "2018-09-02 08:45:00");
        ArrayList<StationInOutSave> saves = mainDistribution.quarterStationInOutResult(dataD, getQuarterPassengerFlows);
//        quarterSaveEspecially.saveStationInOutTimeSliceData(dataD,saves);
//       rddTest.getQuarterStationInOut("2018-09-01 00:00:00",getQuarterPassengerFlows);
    }
}
