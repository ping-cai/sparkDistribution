package cn.edu.sicau.pfdistribution.service.netrouter;

import NetRouterClient.NetRouterClient;
import NetRouterClient.Address;
import NetRouterClient.RecvMessage;
import NetRouterClient.SendMessage;
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.HalfPassengerFlowInfoImpl;
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.OnePassengerFlowInfoImpl;
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.QuarterPassengerFlowInfoImpl;
import cn.edu.sicau.pfdistribution.dataInteraction.PassengerFlowInfo;
import cn.edu.sicau.pfdistribution.dataInteraction.RequestTime;
import cn.edu.sicau.pfdistribution.dataInteraction.SectionFlowInfo;
import cn.edu.sicau.pfdistribution.dataInteraction.StationFlowInfo;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

import java.util.LinkedList;
import java.util.List;

import static cn.edu.sicau.pfdistribution.service.netrouter.IntervalDistributionNetRouter.getLinuxLocalIp;

@Component
@Service
public class DataReturnNetRouter {
    @Autowired
    OnePassengerFlowInfoImpl onePassengerFlowInfo;
    @Autowired
    HalfPassengerFlowInfoImpl halfPassengerFlowInfo;
    @Autowired
    QuarterPassengerFlowInfoImpl quarterPassengerFlowInfo;
    private Logger logger = LoggerFactory.getLogger(DataReturnNetRouter.class);
    @Value("${netRouter.ip1}")
    private String ip1;
    @Value("${netRouter.ip2}")
    private String ip2;
    @Value("${netRouter.port1}")
    private int port1;
    @Value("${netRouter.port2}")
    private int port2;

    public boolean SendData(NetRouterClient netClient, List<Address> f_list, String data) {
        SendMessage f_msg = new SendMessage(f_list, data);
        if (!netClient.sendMessage(f_msg)) {
            logger.info("发送失败！");
            return false;
        }
        logger.info("发送成功！");
        return true;
    }


    @Async("netRouterExecutor")
    public void receiver() throws Exception {
        String systemType = System.getProperty("os.name");
        if (systemType.toLowerCase().contains("win")) {
            IntervalDistributionNetRouter.loadJNILibDynamically("NetRouterCppClient");
        } else {
            System.load("//usr/lib/libNetRouterCppClient.so");
        }
        Address localAddress = new Address((byte) 8, (byte) 2, (short) 2, (byte) 2, (short) 6);
        List<Address> addresses = new LinkedList<Address>();
        Address destAddress = new Address((byte) 8, (byte) 1, (short) 2, (byte) 1, (short) 6);
        addresses.add(destAddress);
        String ip = (systemType.toLowerCase().contains("win")) ? InetAddress.getLocalHost().getHostAddress() : getLinuxLocalIp();
        NetRouterClient netRouterClient = new NetRouterClient("Test", ip1, port1, ip2, port2, localAddress, "");
        while (!netRouterClient.start()) {
            logger.info("{}启动失败!", this.getClass());
            Thread.sleep(10000);
        }
        logger.info("{}启动成功！", this.getClass());

        while (true) {
            if (netRouterClient.isNet1Connected() || netRouterClient.isNet2Connected()) {
                RecvMessage recvMessage = new RecvMessage();
                if (netRouterClient.receiveBlockMessage(recvMessage)) {
                    if (recvMessage.getMessage() != null) {
                        String message = recvMessage.getMessage();
                        ObjectMapper mapper = new ObjectMapper();
                        RequestTime requestTime = mapper.readValue(message, RequestTime.class);
                        if (requestTime.timeInterval == 60) {
                            PassengerFlowInfo passengerFlowInfo = returnOnePassengerFlowInfo(requestTime);
                            String flowInfo = mapper.writeValueAsString(passengerFlowInfo);
                            SendData(netRouterClient, addresses, flowInfo);
                            logger.info("发送数据成功！数据起止时间为{},{}", passengerFlowInfo.startTime, passengerFlowInfo.endTime);
                        } else if (requestTime.timeInterval == 30) {
                            PassengerFlowInfo passengerFlowInfo = returnHalfPassengerFlowInfo(requestTime);
                            String flowInfo = mapper.writeValueAsString(passengerFlowInfo);
                            logger.info("发送数据成功！数据起止时间为{},{}", passengerFlowInfo.startTime, passengerFlowInfo.endTime);
                            SendData(netRouterClient, addresses, flowInfo);
                        } else if (requestTime.timeInterval == 15) {
                            PassengerFlowInfo passengerFlowInfo = returnQuarterPassengerFlowInfo(requestTime);
                            String flowInfo = mapper.writeValueAsString(passengerFlowInfo);
                            logger.info("发送数据成功！数据起止时间为{},{}", passengerFlowInfo.startTime, passengerFlowInfo.endTime);
                            SendData(netRouterClient, addresses, flowInfo);
                        }
                    }
                }
            }
        }
    }

    @Async("netRouterExecutor")
    public PassengerFlowInfo returnOnePassengerFlowInfo(RequestTime requestTime) {
        List<StationFlowInfo> stationFlowInfo = onePassengerFlowInfo.getStationFlowInfo(requestTime.timeStamp, requestTime.startTime, requestTime.endTime);
        List<SectionFlowInfo> sectionFlowInfo = onePassengerFlowInfo.getSectionFlowInfo(requestTime.timeStamp, requestTime.startTime, requestTime.endTime);
        return new PassengerFlowInfo(requestTime.timeStamp, requestTime.startTime, requestTime.endTime, stationFlowInfo, sectionFlowInfo);
    }

    @Async("netRouterExecutor")
    public PassengerFlowInfo returnHalfPassengerFlowInfo(RequestTime requestTime) {
        List<StationFlowInfo> stationFlowInfo = halfPassengerFlowInfo.getStationFlowInfo(requestTime.timeStamp, requestTime.startTime, requestTime.endTime);
        List<SectionFlowInfo> sectionFlowInfo = halfPassengerFlowInfo.getSectionFlowInfo(requestTime.timeStamp, requestTime.startTime, requestTime.endTime);
        return new PassengerFlowInfo(requestTime.timeStamp, requestTime.startTime, requestTime.endTime, stationFlowInfo, sectionFlowInfo);
    }

    @Async("netRouterExecutor")
    public PassengerFlowInfo returnQuarterPassengerFlowInfo(RequestTime requestTime) {
        List<StationFlowInfo> stationFlowInfo = quarterPassengerFlowInfo.getStationFlowInfo(requestTime.timeStamp, requestTime.startTime, requestTime.endTime);
        List<SectionFlowInfo> sectionFlowInfo = quarterPassengerFlowInfo.getSectionFlowInfo(requestTime.timeStamp, requestTime.startTime, requestTime.endTime);
        return new PassengerFlowInfo(requestTime.timeStamp, requestTime.startTime, requestTime.endTime, stationFlowInfo, sectionFlowInfo);
    }
}
