package cn.edu.sicau.pfdistribution.service;

import cn.edu.sicau.pfdistribution.dao.tonghaoGet.GetPassengerFlowOracle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AsyncTest {
    @Autowired
    GetPassengerFlowOracle getPassengerFlowOracle;
    @Autowired
    ThreadTest2 testThread2;

    @Test
    public void test() throws InterruptedException {
        String startTime1 = "2018-09-01 00:00:00";
        String startTime2 = "2018-09-01 00:00:00";
        String startTime3 = "2018-09-01 00:00:00";
        String startTime4 = "2018-09-01 00:00:00";
        String startTime5 = "2018-09-01 00:00:00";
        testThread2.testThread(startTime1);
        testThread2.testThread(startTime2);
        testThread2.testThread(startTime3);
        testThread2.testThread(startTime4);
        testThread2.testThread(startTime5);
    }
}
