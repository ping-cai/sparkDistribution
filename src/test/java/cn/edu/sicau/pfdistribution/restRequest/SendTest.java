package cn.edu.sicau.pfdistribution.restRequest;

import cn.edu.sicau.pfdistribution.Utils.SendCommand;
import cn.edu.sicau.pfdistribution.entity.jiaoda.RequestCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SendTest {
    @Autowired
    SendCommand sendCommand;
    @Test
    public void test() {
        RequestCommand command = new RequestCommand("2018-09-01 00:00:00", "2018-09-01 07:00:00", "2018-09-01 09:00:00", 60, "dynamic");
        System.out.println(sendCommand.sendPost(command));
    }
}
