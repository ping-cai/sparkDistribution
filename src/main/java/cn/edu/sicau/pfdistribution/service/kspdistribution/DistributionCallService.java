package cn.edu.sicau.pfdistribution.service.kspdistribution;

import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Future;

@Service
@Slf4j
public class DistributionCallService {
    @Autowired
    private MainDistribution mainDistribution;

    @Async("dataCalculationExecutor")
    public Future<Map<RequestCommand, String>> callMainMethod(RequestCommand requestCommand) {
        HashMap<RequestCommand, String> callMap = new HashMap<>();
        try {
            mainDistribution.intervalTriggerTask(requestCommand);
            log.info("分配成功的日期:{},{}", requestCommand.getStartTime(), requestCommand.getEndTime());
            log.info("分配成功的日期有" + requestCommand.getStartTime() + " " + requestCommand.getEndTime());
            callMap.put(requestCommand, "分配成功");
            return new AsyncResult<>(callMap);
        } catch (Exception e) {
            log.warn("分配出现警告原因:{}", e.getMessage());
            log.warn("分配出现警告日期:{},{}", requestCommand.getStartTime(), requestCommand.getEndTime());
            callMap.put(requestCommand, "分配失败");
            return new AsyncResult<>(callMap);
        }
    }
}
