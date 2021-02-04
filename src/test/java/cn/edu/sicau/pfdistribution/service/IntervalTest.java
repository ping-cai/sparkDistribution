package cn.edu.sicau.pfdistribution.service;

import cn.edu.sicau.pfdistribution.Utils.Constants;
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.GetTransferData;
import cn.edu.sicau.pfdistribution.entity.jiaoda.StoreTransferData;
import cn.edu.sicau.pfdistribution.entity.jiaoda.TransferPoint;
import cn.edu.sicau.pfdistribution.service.kspdistribution.GetLineID;
import cn.edu.sicau.pfdistribution.service.kspdistribution.MainDistribution;
import cn.edu.sicau.pfdistribution.service.kspdistribution.MainTransfer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import scala.collection.JavaConverters;
import scala.collection.Map$;
import scala.collection.mutable.Builder;
import scala.collection.mutable.Map;

import java.util.ArrayList;
import java.util.HashMap;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class IntervalTest {
    @Autowired
    MainDistribution mainDistribution;
    @Autowired
    GetLineID getLineID;
    @Autowired
    ScalaMethodTest scalaMethodTest;
    @Autowired
    GetTransferData getTransferData;
    private static final Logger logger = LoggerFactory.getLogger(IntervalTest.class);


    @Test
    public void interval() {
        HashMap<String, String> command = new HashMap<>();
        command.put("command", "static");
        command.put("timeInterval", "15");
        Map<String, String> scalaCommand = JavaConverters.mapAsScalaMapConverter(command).asScala();
        Object objectCommand = Map$.MODULE$.<String, String>newBuilder().$plus$plus$eq(scalaCommand.toSeq());
        Object result = ((Builder) objectCommand).result();
        scala.collection.immutable.Map resultCommand = (scala.collection.immutable.Map) result;
        System.out.println(resultCommand.keySet());
        Constants.timeInterval=15;
//        Constants.SAVE_DATE_START = "2018-09-01 07:00:00";
//        Constants.SAVE_DATE_TEMP = "2018-09-01 07:15:00";
//        mainDistribution.intervalTriggerTask(resultCommand);
    }

    public ArrayList<StoreTransferData> stationTransferData(ArrayList<TransferPoint> arrayList) {
        ArrayList<StoreTransferData> allTransferData = new ArrayList<>();
        for (TransferPoint anArrayList : arrayList) {
            StoreTransferData transferData = new StoreTransferData();
            String transferInLine = getTransferData.getLine(anArrayList.getTransferBefore(), anArrayList.getTransferPointBefore());
            String transferOutLine = getTransferData.getLine(anArrayList.getTransferPointAfter(), anArrayList.getTransferAfter());
            String transferPoint = anArrayList.getTransferPointBefore();
            transferData.setInLineId(transferInLine);
            transferData.setOutLineId(transferOutLine);
            transferData.setTransfer(transferPoint);
            String direction = anArrayList.getDirection();
            double transferFlows = anArrayList.getTransferFlows();
            switch (direction) {
                case "11":
                    transferData.setDownInDownOutNum(transferFlows);
                    break;
                case "12":
                    transferData.setDownInUpOutNum(transferFlows);
                    break;
                case "22":
                    transferData.setUpInUpOutNum(transferFlows);
                    break;
                case "21":
                    transferData.setUpInDownOutNum(transferFlows);
                    break;
            }
            allTransferData.add(transferData);
        }
        return allTransferData;
    }

    @Test
    public void intervalResultTest() {
        getLineID.setStationId();
        HashMap<String, Integer> odMap = new HashMap<>();
        odMap.put("鱼洞 较场口", 15);
        scala.collection.immutable.Map map = HashToScalaIMap(odMap);
        Map transfer = MainTransfer.transfer(map);
        Map odMapTransferScala = MainTransfer.odMapTransferScala(transfer);
//        Map result = mainDistribution.intervalResult(odMapTransferScala);
//        System.out.println(result);
    }

    public static scala.collection.immutable.Map HashToScalaIMap(HashMap hashMap) {
        Map scalaCommand = (Map) JavaConverters.mapAsScalaMapConverter(hashMap).asScala();
        Object objectCommand = Map$.MODULE$.<String, String>newBuilder().$plus$plus$eq(scalaCommand.toSeq());
        Object result = ((Builder) objectCommand).result();
        scala.collection.immutable.Map resultCommand = (scala.collection.immutable.Map) result;
        return resultCommand;
    }

    @Test
    public void testContain() {
        System.out.println(Constants.DATA_DATE_START_HOUR + 1);
        Constants.DATA_DATE_START_HOUR++;
        System.out.println(Constants.DATA_DATE_START_HOUR);
    }
    @Test
    public void testOdPathSearch(){
        scalaMethodTest.testOdPathSearch();
    }

}
