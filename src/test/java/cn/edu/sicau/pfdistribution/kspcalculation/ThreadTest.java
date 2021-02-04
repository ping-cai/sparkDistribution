package cn.edu.sicau.pfdistribution.kspcalculation;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadTest {
    @Async("dataCalculationExecutor")
    public Integer executor1() throws InterruptedException {
        for (int i = 0; i < 10000; i++) {
            System.out.println("线程" + Thread.currentThread().getName() + "打印" + i);
        }
        return 1;
    }

    @Async("dataCalculationExecutor")
    public Integer executor2() throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            System.out.println("线程" + Thread.currentThread().getName() + "打印" + i);
            Thread.sleep(2);
        }
        return 2;
    }
}
