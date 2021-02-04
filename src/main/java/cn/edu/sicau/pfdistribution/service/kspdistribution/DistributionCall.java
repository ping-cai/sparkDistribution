package cn.edu.sicau.pfdistribution.service.kspdistribution;

import cn.edu.sicau.pfdistribution.Utils.DateExtendUtil;
import cn.edu.sicau.pfdistribution.entity.TableNamePojo;
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.CheckDataValidateImpl;
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.jiaodaTest.CreateTables;
import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import cn.edu.sicau.pfdistribution.service.netrouter.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class DistributionCall {

    @Autowired
    private DistributionCallService distributionCallService;
    @Value("${url.forecast.SectionUrl}")
    private String forecastSectionUrl;
    @Value("${url.forecast.TransferUrl}")
    private String forecastTransferUrl;
    @Value("${url.forecast.AccessStation}")
    private String forecastAccessStationUrl;
    @Autowired
    private CreateTables createTables;
    @Autowired
    private CheckDataValidateImpl checkDataValidate;

    public void oneHourDistribution(String dateDt, String startTime, String endTime, Integer timeInterval, String command) {
        int timeDifference = DateExtendUtil.timeDifference(startTime, endTime, DateExtendUtil.HOUR);
        for (int i = 0; i < timeDifference; i++) {
            String start = DateExtendUtil.timeAddition(startTime, 0, i * 60);
            String end = DateExtendUtil.timeAddition(startTime, 0, (i + 1) * 60);
            RequestCommand requestCommand = new RequestCommand(dateDt, start, end, timeInterval, command);
            distributionCallService.callMainMethod(requestCommand);
        }
    }

    public void halfHourDistribution(String dateDt, String startTime, String endTime, Integer timeInterval, String command) {
        ArrayList<Future<Map<RequestCommand, String>>> futureList = new ArrayList<>();
        int minute = DateExtendUtil.timeDifference(startTime, endTime, DateExtendUtil.MINUTE) / 30;
        for (int i = 0; i < minute; i++) {
            String start = DateExtendUtil.timeAddition(startTime, 0, i * 30);
            String end = DateExtendUtil.timeAddition(startTime, 0, (i + 1) * 30);
            RequestCommand requestCommand = new RequestCommand(dateDt, start, end, timeInterval, command);
            Future<Map<RequestCommand, String>> future = distributionCallService.callMainMethod(requestCommand);
            futureList.add(future);
            try {
                for (Future<Map<RequestCommand, String>> theFuture : futureList) {
                    theFuture.get(200, TimeUnit.SECONDS);
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }


    public void quarterHourDistribution(String dateDt, String startTime, String endTime, Integer timeInterval, String command) {
        ArrayList<Future<Map<RequestCommand, String>>> futureList = new ArrayList<>();
        int minute = DateExtendUtil.timeDifference(startTime, endTime, DateExtendUtil.MINUTE) / 15;
        for (int i = 0; i < minute; i++) {
            String start = DateExtendUtil.timeAddition(startTime, 0, i * 15);
            String end = DateExtendUtil.timeAddition(startTime, 0, (i + 1) * 15);
            RequestCommand requestCommand = new RequestCommand(dateDt, start, end, timeInterval, command);
            Future<Map<RequestCommand, String>> future = distributionCallService.callMainMethod(requestCommand);
            futureList.add(future);
            try {
                for (Future<Map<RequestCommand, String>> theFuture : futureList) {
                    theFuture.get(200, TimeUnit.SECONDS);
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    public void send2Transfer(String startTime, String endTime, Map<String, String> tableMap, String forecastTransferUrl, String halfTransferTable, String successInfo, String errorInfo) {
        try {
            SendMessage.sendPost(forecastTransferUrl, startTime, endTime, tableMap.get(halfTransferTable));
            log.info(successInfo);
        } catch (Exception e) {
            log.error(errorInfo, e, e);
        }
    }

    public void send2SectionAndStation(String startTime, String endTime, Map<String, String> tableMap) {
        send2Transfer(startTime, endTime, tableMap, forecastSectionUrl, "quarterSectionTable", "发送post请求给15分钟区间断面进行预测成功！", "发送post请求给15分钟区间断面进行预测失败，原因是{}");
        send2Transfer(startTime, endTime, tableMap, forecastAccessStationUrl, "quarterStationTable", "发送post请求给15分钟区间进出站进行预测成功！", "发送post请求给15分钟区间进出站进行预测失败，原因是{}");
    }

    @Async("dataCalculationExecutor")
    public void mainCommandStart(RequestCommand requestCommand) {
        String year = requestCommand.getStartTime().split(" ")[0].split("-")[0];
        createTables.createAllTables(year);
        TableNamePojo tableNamePojo = new TableNamePojo(year);
        Map<String, String> tableMap;
        if ("static".equals(requestCommand.getCommand())) {
            tableMap = tableNamePojo.getTheStatic();
        } else {
            tableMap = tableNamePojo.getTheDynamic();
        }
        log.info("请求进行分配的参数为{}", requestCommand);
        String startTime = requestCommand.getStartTime();
        String endTime = requestCommand.getEndTime();
        String dateDt = requestCommand.getDateDt();
        Integer timeInterval = requestCommand.getTimeInterval();
        String command = requestCommand.getCommand();
        boolean checkDistribution = checkDistribution(tableMap, startTime, endTime, timeInterval);
        switch (timeInterval) {
            case 15:
                if (checkDistribution) {
                    quarterHourDistribution(dateDt, startTime, endTime, timeInterval, command);
                }
                send2SectionAndStation(startTime, endTime, tableMap);
                break;
            case 30:
                if (checkDistribution) {
                    halfHourDistribution(dateDt, startTime, endTime, timeInterval, command);
                }
                send2Transfer(startTime, endTime, tableMap, forecastTransferUrl, "halfTransferTable", "发送post请求给半小时换乘站进行预测成功！", "发送post请求给半小时换乘站进行预测失败，原因是{}");
                break;
            case 60:
                if (checkDistribution) {
                    oneHourDistribution(dateDt, startTime, endTime, timeInterval, command);
                }
                break;
        }

    }

    public boolean checkDistribution(Map<String, String> tableMap, String startTime, String endTime, Integer timeInterval) {
        boolean distributionFlag = false;
        for (Map.Entry<String, String> entry : tableMap.entrySet()) {
            switch (timeInterval) {
                case 15:
                    if (entry.getKey().contains("quarter")) {
                        boolean existence = checkDataValidate.checkDataExistence(entry.getValue(), startTime, timeInterval);
                        if (!existence) {
                            checkDataValidate.deleteIncompleteData(entry.getValue(), startTime, endTime, timeInterval);
                            distributionFlag = true;
                        }
                    }
                    break;
                case 30:
                    if (entry.getKey().contains("half")) {
                        boolean existence = checkDataValidate.checkDataExistence(entry.getValue(), startTime, timeInterval);

                        if (!existence) {
                            checkDataValidate.deleteIncompleteData(entry.getValue(), startTime, endTime, timeInterval);
                            distributionFlag = true;
                        }
                    }
                    break;
                case 60:
                    if (entry.getKey().contains("one")) {
                        boolean existence = checkDataValidate.checkDataExistence(entry.getValue(), startTime, timeInterval);
                        if (!existence) {
                            distributionFlag = true;
                            checkDataValidate.deleteIncompleteData(entry.getValue(), startTime, endTime, timeInterval);
                        }
                    }
                    break;
            }
        }
        return distributionFlag;
    }
}
