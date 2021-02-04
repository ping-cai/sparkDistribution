package cn.edu.sicau.pfdistribution.service.netrouter;

import NetRouterClient.Address;
import NetRouterClient.NetRouterClient;
import NetRouterClient.RecvMessage;
import NetRouterClient.SendMessage;
import cn.edu.sicau.pfdistribution.Utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import static cn.edu.sicau.pfdistribution.service.netrouter.IntervalDistributionNetRouter.getLinuxLocalIp;

@Component
@Service
public class RiskLevelNetRouter {
    Logger log = LoggerFactory.getLogger(RiskLevelNetRouter.class);
    @Autowired
    private JsonTransfer jsonTransfer;
    @Value("${netRouter.ip1}")
    private String ip1;
    @Value("${netRouter.ip2}")
    private String ip2;
    @Value("${netRouter.port1}")
    private int port1;
    @Value("${netRouter.port2}")
    private int port2;

    private static void loadJNILibDynamically(String libName) throws IOException {// synchronized static

        String systemType = System.getProperty("os.name");
        String libExtension = (systemType.toLowerCase().contains("win")) ? ".dll" : ".so";

        String libFullName = (systemType.toLowerCase().contains("win")) ? libName + libExtension : "lib" + libName + libExtension;

        String nativeTempDir = System.getProperty("user.dir");

        InputStream in = null;
        BufferedInputStream reader = null;
        FileOutputStream writer = null;

        File extractedLibFile = new File(nativeTempDir + File.separator + libFullName);

        if (!extractedLibFile.exists()) {
            try {
                if (RiskLevelNetRouter.class.getResource("/" + libFullName) == null) {
                    throw new IllegalStateException("Lib " + libFullName + " not found!");
                }
                in = RiskLevelNetRouter.class.getResourceAsStream("/" + libFullName);
                reader = new BufferedInputStream(in);
                writer = new FileOutputStream(extractedLibFile);

                byte[] buffer = new byte[1024];

                while (reader.read(buffer) > 0) {
                    writer.write(buffer);
                    buffer = new byte[1024];
                }
            } finally {
                if (in != null)
                    in.close();
                if (writer != null)
                    writer.close();
            }
        }
        System.load(extractedLibFile.toString());
    }

    private boolean SendData(NetRouterClient netClient, List<Address> f_list, String data) {
        SendMessage f_msg = new SendMessage(f_list, data);
        if (!netClient.sendMessage(f_msg)) {
            log.info("Send fail");
            return false;
        }
        System.out.println("Risk Send suc");
        return true;
    }

    @Async("netRouterExecutor")
    public void receiver() throws Exception {
        String systemType = System.getProperty("os.name");
        if (systemType.toLowerCase().contains("win")) {
            loadJNILibDynamically("NetRouterCppClient");
        } else {
            System.load("//usr/lib/libNetRouterCppClient.so");
        }
        Address localaddr = new Address((byte) 8, (byte) 1, (short) 2, (byte) 2, (short) 6);
        List<Address> destAddrs = new LinkedList<Address>();
        Address destaddr1 = new Address((byte) 8, (byte) 1, (short) 4, (byte) 1, (short) 6);
        destAddrs.add(destaddr1);
//注册信息
        String reginfo =
                "<in_condition>\n" +
                        "<rec>\n" +
                        "<protocol418_condition>\n" +
                        "<type_func>0x04,0x09</type_func>\n" +
                        "<type_func>0x04,0x09</type_func>\n" +
                        "</protocol418_condition>\n" +
                        "</rec>\n" +
                        "</in_condition>\n";
        String ip = (systemType.toLowerCase().contains("win")) ? InetAddress.getLocalHost().getHostAddress() : getLinuxLocalIp();
        NetRouterClient netRouterClient = new NetRouterClient("Test", ip1, port1, ip2, port2, localaddr, "");
        while (!netRouterClient.start()) {
            log.info("RiskLevelNetRouter Start fails.");
            Thread.sleep(10000);
        }
        log.info("RiskLevelNetRouter Start succeeds.");

//        SendData(netRouterClient, destAddrs,args);

        while (true) {
            if (netRouterClient.isNet1Connected() || netRouterClient.isNet2Connected()) {
                RecvMessage recvMessage = new RecvMessage();
                if (netRouterClient.receiveBlockMessage(recvMessage)) {
                    try {
                        String message = recvMessage.getMessage();
//                       log.info("RiskNetRouter"+message);
                        String[] fields = message.split("\\[");
                        String risk = fields[0];
                        byte[] risk1 = risk.getBytes();
                        log.info("数据的前两个类型码{},{}", risk1[0], risk1[1]);
                        if (Constants.ENVIRONMENT_RISK.equals(risk)) {
                            JSONArray jsonArray = new JSONArray("[" + fields[1]);
                            jsonTransfer.riskDataAnalysis(jsonArray);
                            log.info("RiskNetRouter数据处理成功");
                        }
                    } catch (Exception e) {
                        log.debug("RiskNetRouter数据不对应");
                    }
                }
            }
        }
    }
}
