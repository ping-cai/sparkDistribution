package cn.edu.sicau.pfdistribution.exceptionhandle;

import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.io.Serializable;

@Controller
@Slf4j
public class CheckService implements Serializable {
    public void dataIsEmpty(RequestCommand requestCommand) {
        log.warn("查询到的数据为空！查询的时间为{},{},请检查该时间是否有数据，没有则可以略过", requestCommand.getStartTime(), requestCommand.getEndTime());
    }
}
