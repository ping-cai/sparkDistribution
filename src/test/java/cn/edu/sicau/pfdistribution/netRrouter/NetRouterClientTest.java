package cn.edu.sicau.pfdistribution.netRrouter;

import NetRouterClient.Address;
import NetRouterClient.NetRouterClient;
import NetRouterClient.RecvMessage;
import NetRouterClient.SendMessage;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
/*
测试用例内容：
向目的客户端发送的数据，收到回复后发送下一包
*/
public class NetRouterClientTest {

	private static void loadJNILibDynamically() {
        try {
            System.setProperty("java.library.path", System.getProperty("java.library.path")
                    + ";.\\bin\\");
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
 
            System.loadLibrary("NetRouterCppClient");
        } catch (Exception e) {
            // do nothing for exception
        }
    }

	private static boolean SendData(NetRouterClient netClient, List<Address> f_list){
        String buf = "Hello,world";
        SendMessage f_msg = new SendMessage(f_list, buf);
        if (!netClient.sendMessage(f_msg))
        {
            System.out.println("Send fail");
            return false;
        }
        System.out.println("Send suc");
        return true;
    }
	
    public static void main(String[] args) throws Exception{
    	loadJNILibDynamically();
        Address localaddr = new Address((byte)8,(byte)1,(short)2,(byte)2,(short)6);
        List<Address> destAddrs = new LinkedList<Address>();
        Address destaddr = new Address((byte)8,(byte)1,(short)2,(byte)1,(short)6);
        destAddrs.add(destaddr);
        
        NetRouterClient netRouterClient = new NetRouterClient("Test","192.168.1.108", 9003, "192.168.1.108",9005,localaddr,"");
        while(!netRouterClient.start()){
            System.out.println("Start fails.");
            Thread.sleep(10);
        }
        System.out.println("Start succeeds.");
        
        SendData(netRouterClient, destAddrs);
        
        while(true){
        	if(netRouterClient.isNet1Connected()||netRouterClient.isNet2Connected()) {
        		RecvMessage recvMessage = new RecvMessage();
        		if(netRouterClient.receiveBlockMessage(recvMessage)) {
        			System.out.println("recvData: "+recvMessage.getMessage());
        			SendData(netRouterClient, destAddrs);
        		}
        	}
        }
    }
}
