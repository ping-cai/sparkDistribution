package cn.edu.sicau.pfdistribution;

import cn.edu.sicau.pfdistribution.Utils.DataBaseLoading;
import cn.edu.sicau.pfdistribution.entity.DirectedEdge;
import cn.edu.sicau.pfdistribution.entity.correct.ODFlow;
import cn.edu.sicau.pfdistribution.entity.correct.OverflowData;
import cn.edu.sicau.pfdistribution.entity.correct.PassengerFlow;
import cn.edu.sicau.pfdistribution.entity.correct.SectionFlow;
import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow;
import cn.edu.sicau.pfdistribution.entity.jiaodaTest.OdWithPath;
import cn.edu.sicau.pfdistribution.service.jiaodaTest.BetterCalculateImpl;
import cn.edu.sicau.pfdistribution.service.jiaodaTest.OdOverflowCalculate;
import cn.edu.sicau.pfdistribution.service.jiaodaTest.SectionCalculationImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import scala.Tuple4;

import java.util.Map;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class BetterCalculateImplTest {
    @Autowired
    private BetterCalculateImpl betterCalculate;

    @Test
    public void dynamicOdPathSearch() {
        Map<String, String> stationNameToId = DataBaseLoading.stationNameToId;
        String origin = stationNameToId.get("小什字");
        String destination = stationNameToId.get("观音桥");
        OdWithPath odWithPath = betterCalculate.dynamicOdPathSearch(origin + " " + destination);
        System.out.println(odWithPath);
    }

    /**
     * 0606	小什字	2018-10-17 17:26:39	0321	观音桥	2018-10-17 17:56:11	30
     * 小什字	2018-10-17 13:01:30	0321	观音桥	2018-10-17 13:41:02	40
     */
    @Test
    public void odQuarterDistributionResultDynamicTest() {
        Map<String, String> stationNameToId = DataBaseLoading.stationNameToId;
        String origin = stationNameToId.get("小什字");
        String destination = stationNameToId.get("观音桥");
        String startTime = "2018-10-17 13:01:30";
        String endTime = "2018-10-17 13:41:02";
        GetQuarterPassengerFlow quarterPassengerFlow = new GetQuarterPassengerFlow(startTime, endTime, origin, destination, 1, 30);
        Tuple4<String, String, scala.collection.mutable.Map<DirectedEdge[], Object>, Object> tuple4 = betterCalculate.odQuarterDistributionResultDynamic(quarterPassengerFlow);
        System.out.println(tuple4);
    }

    @Autowired
    OdOverflowCalculate odOverflowCalculate;
    @Autowired
    SectionCalculationImpl sectionCalculation;
    @Test
    public void odOverflowCalculate() {
        Map<String, String> stationNameToId = DataBaseLoading.stationNameToId;
        String origin = stationNameToId.get("小什字");
        String destination = stationNameToId.get("观音桥");
        String startTime = "2018-10-17 13:01:30";
        String endTime = "2018-10-17 13:51:02";
        PassengerFlow odFlow = new ODFlow(startTime, endTime, origin, destination, 8.0, 41);
        OverflowData overflowData = odOverflowCalculate.odQuarterDistributionResult(odFlow);
        SectionFlow sectionFlow = sectionCalculation.overflowRDDToSectionCapacity(overflowData);
        System.out.println(sectionFlow);
    }
}
