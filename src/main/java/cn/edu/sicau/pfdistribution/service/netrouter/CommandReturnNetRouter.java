package cn.edu.sicau.pfdistribution.service.netrouter;

import NetRouterClient.Address;
import NetRouterClient.NetRouterClient;
import cn.edu.sicau.pfdistribution.Utils.ResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static cn.edu.sicau.pfdistribution.service.netrouter.IntervalDistributionNetRouter.getLinuxLocalIp;

@Component
@Slf4j
@Service
public class CommandReturnNetRouter extends DataReturnNetRouter {
    @Value("${netRouter.ip1}")
    private String ip1;
    @Value("${netRouter.ip2}")
    private String ip2;
    @Value("${netRouter.port1}")
    private int port1;
    @Value("${netRouter.port2}")
    private int port2;

    public void receiver(String startTime, String endTime, String finalTable, String info) throws Exception {
        String systemType = System.getProperty("os.name");
        if (systemType.toLowerCase().contains("win")) {
            IntervalDistributionNetRouter.loadJNILibDynamically("NetRouterCppClient");
        } else {
            System.load("//usr/lib/libNetRouterCppClient.so");
        }
        Address localAddress = new Address((byte) 8, (byte) 2, (short) 2, (byte) 2, (short) 6);
        List<Address> addresses = new LinkedList<Address>();
        Address destAddress = new Address((byte) 8, (byte) 1, (short) 1, (byte) 1, (short) 6);
        addresses.add(destAddress);
        String ip = (systemType.toLowerCase().contains("win")) ? InetAddress.getLocalHost().getHostAddress() : getLinuxLocalIp();
        NetRouterClient netRouterClient = new NetRouterClient("Test", ip1, port1, ip2, port2, localAddress, "");
        while (!netRouterClient.start()) {
            log.error("{}启动失败!", this.getClass());
            Thread.sleep(10000);
        }
        log.info("{}启动成功！", this.getClass());
        sendMapData(startTime, endTime, finalTable, info, addresses, netRouterClient);
    }

    public void sendMapData(String startTime, String endTime, String finalTable, String info, List<Address> addresses, NetRouterClient netRouterClient) throws IOException {
        ResultMessage<String> message = new ResultMessage<>();
        message.setStatus(200);
        message.setMessage("success");
        HashMap<String, String> map = new HashMap<>();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("finalTable", finalTable);
        map.put("info", info);
        ObjectMapper mapper = new ObjectMapper();
        String string = mapper.writeValueAsString(map);
        message.setData(string);
        String messageJson = mapper.writeValueAsString(message);
        log.info("发送给总线数据成功！{}", messageJson);
        SendData(netRouterClient, addresses, messageJson);
    }
}
