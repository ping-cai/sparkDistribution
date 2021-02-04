package cn.edu.sicau.pfdistribution.controller;

import cn.edu.sicau.pfdistribution.Utils.ResultMessage;
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.jiaodaTest.CreateTables;
import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import cn.edu.sicau.pfdistribution.service.kspdistribution.DistributionCall;
import cn.edu.sicau.pfdistribution.service.kspdistribution.GetParameters;
import cn.edu.sicau.pfdistribution.service.netrouter.CommandReturnNetRouter;
import cn.edu.sicau.pfdistribution.service.netrouter.NetRouterService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 内部命令REST接口
 */
@CrossOrigin
@Controller
@Slf4j
public class CommandController {
    @Autowired
    private DistributionCall distributionCall;
    @Autowired
    private CommandReturnNetRouter commandReturnNetRouter;
    @Autowired
    private CreateTables createTables;
    @Autowired
    private NetRouterService netRouterService;
    @Value("${table.theDynamic.quarterSectionTable}")
    private String quarterSectionTableName;
    @Value("${url.forecast.OdUrl}")
    private String odUrl;
    private static final int SUCCESS_CODE = 200;
    private static final String SUCCESS_MSG = "success";
    private static final int FAIL_CODE = 500;
    private static final String FAIL_MSG = "failed";

    @PostMapping("/command")
    @ResponseBody
    public void doCommand(RequestCommand requestCommand, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        ResultMessage resultMessage = new ResultMessage(200, "success", requestCommand.toString());
        String message = mapper.writeValueAsString(resultMessage);
        distributionCall.mainCommandStart(requestCommand);
        writer.write(message);
    }

    @PostMapping("/createTable")
    @ResponseBody
    public ResultMessage doCreate(RequestCommand requestCommand) {
        String year = requestCommand.getStartTime().split(" ")[0].split("-")[0];
        createTables.createAllTables(year);
        return new ResultMessage(SUCCESS_CODE, "success", "创建表成功！请查看数据库中是否创建成功！");
    }

    /**
     * @param startTime  接受预测的开始时间
     * @param endTime    接受预测的结束时间
     * @param finalTable 接受预测存入的数据表
     * @return 返回收到的开始时间和结束时间
     */
    @PostMapping("/accept_forecast")
    @ResponseBody
    public ResultMessage acceptForecast(String startTime, String endTime, String finalTable, String info) {
        return transInfoToCenter(startTime, endTime, finalTable, info);
    }

    public ResultMessage transInfoToCenter(String startTime, String endTime, String finalTable, String info) {
        log.info("接受到请求开始时间{},结束时间{}", startTime, endTime);
        try {
            commandReturnNetRouter.receiver(startTime, endTime, finalTable, info);
        } catch (Exception e) {
            log.error("出错的原因是,{}", e, e);
            return new ResultMessage(FAIL_CODE, FAIL_MSG, "总线接受命令失败，请重新请求！");
        }

        return new ResultMessage(SUCCESS_CODE, SUCCESS_MSG, "总线接受命令成功！");
    }

    @GetMapping("/odToOracle")
    @ResponseBody
    public String toSaveOracle(String tableName) {
        for (int i = 0; i < 4; i++) {
            netRouterService.QuarterToOracle(tableName, i);
        }
        return "请求成功！";
    }

    @Autowired
    private GetParameters getParameters;

    @GetMapping("/checkParam")
    @ResponseBody
    public void checkParam() {
        log.warn("A参数为{}", getParameters.getA());
        log.warn("B参数为{}", getParameters.getB());
        log.warn("C参数为{}", getParameters.getDistributionCoefficient());
    }
}
