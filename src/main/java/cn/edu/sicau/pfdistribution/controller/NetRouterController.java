package cn.edu.sicau.pfdistribution.controller;

import cn.edu.sicau.pfdistribution.service.netrouter.DataReturnNetRouter;
import cn.edu.sicau.pfdistribution.service.netrouter.IntervalDistributionNetRouter;
import cn.edu.sicau.pfdistribution.service.netrouter.RiskLevelNetRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class NetRouterController {

    @Autowired
    private IntervalDistributionNetRouter intervalDistributionNetRouter;
    @Autowired
    private RiskLevelNetRouter riskLevelNetRouter;
    @Autowired
    private DataReturnNetRouter dataReturnNetRouter;

    @PostConstruct
    public void StartAllNetRouter() {
//        try {
//            intervalDistributionNetRouter.receiver();
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            log.error("分配任务总线服务程序出现问题!");
//        }
        try {
            riskLevelNetRouter.receiver();
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("风险接收总线服务程序出现问题!");
        }
        try {
            dataReturnNetRouter.receiver();
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("数据返回总线服务程序出现问题!");
        }
    }
}
