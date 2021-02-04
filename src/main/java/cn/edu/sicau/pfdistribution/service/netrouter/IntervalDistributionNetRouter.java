package cn.edu.sicau.pfdistribution.service.netrouter;

import NetRouterClient.Address;
import NetRouterClient.NetRouterClient;
import NetRouterClient.RecvMessage;
import NetRouterClient.SendMessage;
import cn.edu.sicau.pfdistribution.Utils.DateExtendUtil;
import cn.edu.sicau.pfdistribution.service.kspdistribution.DistributionCall;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

@Component
@Service
@Slf4j
public class IntervalDistributionNetRouter {
    @Autowired
    private DistributionCall distributionCall;
    private Gson gson = new GsonBuilder().create();
    @Value("${netRouter.ip1}")
    private String ip1;
    @Value("${netRouter.ip2}")
    private String ip2;
    @Value("${netRouter.port1}")
    private int port1;
    @Value("${netRouter.port2}")
    private int port2;

    public static void loadJNILibDynamically(String libName) throws IOException {// synchronized static

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
                if (IntervalDistributionNetRouter.class.getResource("/" + libFullName) == null) {
                    throw new IllegalStateException("Lib " + libFullName + " not found!");
                }
                in = IntervalDistributionNetRouter.class.getResourceAsStream("/" + libFullName);
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

    public boolean SendData(NetRouterClient netClient, List<Address> f_list, String data) {

        SendMessage f_msg = new SendMessage(f_list, gson.toJson(data));
        if (!netClient.sendMessage(f_msg)) {
            log.info("Send fail");
            return false;
        }
        log.info("Interval Send suc");
        return true;
    }


//    @Async("netRouterExecutor")
//    public void receiver() throws Exception {
//        String systemType = System.getProperty("os.name");
//        if (systemType.toLowerCase().contains("win")) {
//            loadJNILibDynamically("NetRouterCppClient");
//        } else {
//            System.load("//usr/lib/libNetRouterCppClient.so");
//        }
//        Address localAddress = new Address((byte) 8, (byte) 1, (short) 2, (byte) 2, (short) 6);
//        List<Address> addresses = new LinkedList<Address>();
//        Address destAddress = new Address((byte) 8, (byte) 1, (short) 4, (byte) 1, (short) 6);
//        addresses.add(destAddress);
//
//        String ip = (systemType.toLowerCase().contains("win")) ? InetAddress.getLocalHost().getHostAddress() : getLinuxLocalIp();
//        log.info("NetRouterClint检测到的ip地址:{}", ip);
//        log.info("发送给总线监听程序的ip1地址为:{}", ip1);
//        log.info("发送给总线监听程序的ip2地址为:{}", ip2);
//        log.info("发送给总线监听程序的端口1地址为:{}", port1);
//        log.info("发送给总线监听程序的端口2地址为:{}", port2);
//        NetRouterClient netRouterClient = new NetRouterClient("Test", ip1, port1, ip2, port2, localAddress, "");
//        while (!netRouterClient.start()) {
//            log.info("IntervalDistributionNetRouter Start fails.");
//            Thread.sleep(10000);
//        }
//        log.info("IntervalDistributionNetRouter Start succeeds.");
//
//        while (true) {
//            if (netRouterClient.isNet1Connected() || netRouterClient.isNet2Connected()) {
//                RecvMessage recvMessage = new RecvMessage();
//                if (netRouterClient.receiveBlockMessage(recvMessage)) {
//                    Map<String, String> message = gson.fromJson(recvMessage.getMessage(), new TypeToken<Map<String, String>>() {
//                    }.getType());
//                    if (message != null) {
////                        final List<Tuple2<String, String>> list = new java.util.ArrayList<>(message.size());
////                        for (final Map.Entry<String, String> entry : message.entrySet()) {
////                            list.add(Tuple2.apply(entry.getKey(), entry.getValue()));
////                        }
////                        final scala.collection.Seq<Tuple2<String, String>> seq = scala.collection.JavaConverters.asScalaBufferConverter(list).asScala().toSeq();
//                        log.info("IntervalDistributionNetRouter数据接受成功");
//                        String dateDt = message.get("dateDt");
//                        String startTime = message.get("startTime");
//                        String endTime = message.get("endTime");
//                        Integer timeInterval = Integer.parseInt(message.get("timeInterval"));
//                        String command = message.get("command");
//                        if (dateDt == null) {
//                            dateDt = DateExtendUtil.dateToString(DateExtendUtil.stringToDate(startTime.split(" ")[0], DateExtendUtil.PART));
//                        }
//                        String dataPassengers = null;
//                        if (timeInterval == 15) {
//                            dataPassengers = distributionCall.quarterHourDistribution(dateDt, startTime, endTime, timeInterval, command);
//                        } else if (timeInterval == 60) {
//                            dataPassengers = distributionCall.oneHourDistribution(dateDt, startTime, endTime, timeInterval, command);
//                        } else if (timeInterval == 30) {
//                            dataPassengers = distributionCall.halfHourDistribution(dateDt, startTime, endTime, timeInterval, command);
//                        }
//                        SendData(netRouterClient, addresses, dataPassengers);
//                    }
//                }
//            }
//        }
//    }

    /**
     * 获取Linux下的IP地址
     *
     * @return IP地址
     * @throws SocketException
     */
    public static String getLinuxLocalIp() throws SocketException {
        String ip = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                String name = networkInterface.getName();
                if (!name.contains("docker") && !name.contains("lo")) {
                    for (Enumeration<InetAddress> enumIpAddress = networkInterface.getInetAddresses();
                         enumIpAddress.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddress.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipAddress = inetAddress.getHostAddress().toString();
                            if (!ipAddress.contains("::") && !ipAddress.contains("0:0:")
                                    && !ipAddress.contains("fe80")) {
                                ip = ipAddress;
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            log.warn("获取ip地址异常!");
            log.warn(ex.getMessage());
        }
        log.info("Linux的IP地址为:" + ip);
        return ip;
    }
}
