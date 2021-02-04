package cn.edu.sicau.pfdistribution.kspcompute;

import cn.edu.sicau.pfdistribution.entity.DirectedPath;
import cn.edu.sicau.pfdistribution.service.road.KServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ComputeStaticTest {
    @Autowired
    KServiceImpl kService;
    @Test
    public void testComputeStatic(){
        HashMap<String, String> stationOd = new HashMap<>();
        stationOd.put("鱼洞 尖顶坡","鱼洞 尖顶坡");
        Map<String, List<DirectedPath>> computeStatic = kService.computeStatic(stationOd, "PARAM_NAME", "RETURN_ID");
        for (Map.Entry<String, List<DirectedPath>> entry:computeStatic.entrySet()){
            System.out.println(entry);
        }
    }
}
