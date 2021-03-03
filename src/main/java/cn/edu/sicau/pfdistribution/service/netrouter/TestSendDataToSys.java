package cn.edu.sicau.pfdistribution.service.netrouter;

import NetRouterClient.NetRouterClient;
import NetRouterClient.Address;
import NetRouterClient.SendMessage;
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.HalfPassengerFlowInfoImpl;
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.OnePassengerFlowInfoImpl;
import cn.edu.sicau.pfdistribution.dao.tonghaoSave.QuarterPassengerFlowInfoImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import static cn.edu.sicau.pfdistribution.service.netrouter.IntervalDistributionNetRouter.getLinuxLocalIp;

@Component
@Slf4j
public class TestSendDataToSys {
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

    public static List<Address> addressList() {
        List<Address> addresses = new LinkedList<Address>();
        Address destAddress = new Address((byte) 8, (byte) 1, (short) 1, (byte) 1, (short) 6);
        addresses.add(destAddress);
        return addresses;
    }

    public NetRouterClient receiver() throws Exception {
        String systemType = System.getProperty("os.name");
        if (systemType.toLowerCase().contains("win")) {
            IntervalDistributionNetRouter.loadJNILibDynamically("NetRouterCppClient");
        } else {
            System.load("//usr/lib/libNetRouterCppClient.so");
        }
        Address localAddress = new Address((byte) 8, (byte) 1, (short) 2, (byte) 2, (short) 65535);
        String ip = (systemType.toLowerCase().contains("win")) ? InetAddress.getLocalHost().getHostAddress() : getLinuxLocalIp();
        NetRouterClient netRouterClient = new NetRouterClient("Test", "10.2.55.70", 9003, "127.0.0.1", 9003, localAddress, "");
        while (!netRouterClient.start()) {
            logger.info("{}启动失败!", this.getClass());
            Thread.sleep(10000);
        }
        logger.info("{}启动成功！", this.getClass());
        return netRouterClient;
    }
}
