package cn.edu.sicau.pfdistribution.service;

import cn.edu.sicau.pfdistribution.Utils.Constants;
import cn.edu.sicau.pfdistribution.Utils.DateExtendUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Async
@Component
public class ThreadTest2 {
    public void testThread(String startTime) throws InterruptedException {
        Constants.SAVE_DATE_TEMP = startTime;
        for (int i = 0; i < 10; i++) {
            Constants.SAVE_DATE_TEMP = DateExtendUtil.timeAddition(Constants.SAVE_DATE_TEMP, 1, 0);
            log.info("时间变化为：{}", Thread.currentThread().getName() + Constants.SAVE_DATE_TEMP);
        }

        Thread.sleep(1000);
    }
}
