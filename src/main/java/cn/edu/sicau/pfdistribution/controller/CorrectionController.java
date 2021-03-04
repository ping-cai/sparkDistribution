package cn.edu.sicau.pfdistribution.controller;

import NetRouterClient.NetRouterClient;
import cn.edu.sicau.pfdistribution.Utils.TypeTransfer;
import cn.edu.sicau.pfdistribution.dao.betterSave.PassengerFlowToSys;
import cn.edu.sicau.pfdistribution.dao.sql.CreateOracleTables;
import cn.edu.sicau.pfdistribution.dataInteraction.NetRouterMessage;
import cn.edu.sicau.pfdistribution.dataInteraction.PassengerFlowInfo;
import cn.edu.sicau.pfdistribution.dataInteraction.SectionFlowInfo;
import cn.edu.sicau.pfdistribution.dataInteraction.StationFlowInfo;
import cn.edu.sicau.pfdistribution.entity.Command;
import cn.edu.sicau.pfdistribution.entity.jiaoda.GetQuarterPassengerFlow;
import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import cn.edu.sicau.pfdistribution.service.jiaodaTest.CorrectionDataAllocation;
import cn.edu.sicau.pfdistribution.service.jiaodaTest.ShortTermDistribution;
import cn.edu.sicau.pfdistribution.service.netrouter.TestSendDataToSys;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @Author LiYongPing
 * @Date 2021-02-02
 * @LastUpdate 2021-02-02
 */
@Slf4j
@Controller
public class CorrectionController {
    @Autowired
    private CorrectionDataAllocation correctionDataAllocation;
    @Autowired
    private ShortTermDistribution shortTermDistribution;

    @Autowired
    private CreateOracleTables createOracleTables;

    @PostMapping("/correctData")
    @ResponseBody
    public void correctData(String fromTable, String targetTable, RequestCommand requestCommand) {
        correctionDataAllocation.CorrectDataTest(fromTable, targetTable, requestCommand);
    }

    @PostMapping("/testOracleData")
    @ResponseBody
    public void testOracleData(RequestCommand command) {
        System.out.println(command);
        createOracleTables.createTable(command.getTargetTable(), CreateOracleTables.CREATE_SECTION_TABLE);
        createOracleTables.createTable(command.getTargetTable(), CreateOracleTables.CREATE_STATION_TABLE);
        createOracleTables.createTable(command.getTargetTable(), CreateOracleTables.CREATE_TRANSFER_TABLE);
        shortTermDistribution.distributeQuarter(command);
    }

    @Autowired
    PassengerFlowToSys passengerFlowToSys;

    @Autowired
    TestSendDataToSys testSendDataToSys;

    @PostMapping("/testGetStationFlowInfo")
    @ResponseBody
    public void testGetStationFlowInfo(String dateDt, String startTime, String endTime, String stationTable, String endTable) throws Exception {
        List<StationFlowInfo> stationFlowInfo = passengerFlowToSys.getStationFlowInfo(dateDt, startTime, endTime, stationTable, endTable);
        List<SectionFlowInfo> sectionFlowInfo = passengerFlowToSys.getSectionFlowInfo(dateDt, startTime, endTime, stationTable);
        ObjectMapper mapper = new ObjectMapper();
        NetRouterClient receiver = testSendDataToSys.receiver();
        String sectionFlowInfoJson = mapper.writeValueAsString(sectionFlowInfo);
        byte functionCode = 0x07;
        byte typeCode = 0x0e;
        byte[] bytes = {(byte) 0x07, (byte) 0x0e};
        String hexHead = TypeTransfer.toHex(bytes);
        String message = hexHead + sectionFlowInfoJson;
        testSendDataToSys.SendData(receiver, TestSendDataToSys.addressList(), message);
        System.out.println(hexHead);
    }

    @PostMapping("/testGetPassengerFlowInfo")
    @ResponseBody
    public String testGetPassengerFlowInfo(String dateDt, String startTime, String endTime, String stationTable, String sectionTable, String transferTable) throws Exception {
        List<StationFlowInfo> stationFlowInfo = passengerFlowToSys.getStationFlowInfo(dateDt, startTime, endTime, stationTable, transferTable);
        List<SectionFlowInfo> sectionFlowInfo = passengerFlowToSys.getSectionFlowInfo(dateDt, startTime, endTime, sectionTable);
        ObjectMapper mapper = new ObjectMapper();
        NetRouterClient receiver = testSendDataToSys.receiver();
        PassengerFlowInfo passengerFlowInfo = new PassengerFlowInfo(dateDt, startTime, endTime, stationFlowInfo, sectionFlowInfo);
        String passengerFlowInfoJson = mapper.writeValueAsString(passengerFlowInfo);
        byte functionCode = 0x07;
        byte typeCode = 0x0e;
        byte[] bytes = {(byte) 0x07, (byte) 0x0e};
        String hexHead = TypeTransfer.toHex(bytes);
        String message = hexHead + passengerFlowInfoJson;
        testSendDataToSys.SendData(receiver, TestSendDataToSys.addressList(), message);
        return message;
    }

    @GetMapping("/testNetRouter")
    @ResponseBody
    public String testNetRouter() throws Exception {
        NetRouterClient receiver = testSendDataToSys.receiver();
        byte functionCode = 0x01;
        byte typeCode = 0x0e;
        byte requestTypeCode=0x01;
        byte[] bytes = {functionCode, typeCode,requestTypeCode};
        String hexHead = TypeTransfer.toHex(bytes);
        testSendDataToSys.SendData(receiver, TestSendDataToSys.addressList(), hexHead);
        return hexHead;
    }

}
