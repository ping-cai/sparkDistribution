package cn.edu.sicau.pfdistribution.service;

import cn.edu.sicau.pfdistribution.dao.tonghaoGet.GetPassengerFlowOracle;
import cn.edu.sicau.pfdistribution.dao.tonghaoGet.GetTransferData;
import cn.edu.sicau.pfdistribution.service.kspdistribution.CalculateBaseInterface;
import cn.edu.sicau.pfdistribution.service.kspdistribution.MainDistribution;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DistributionTest {
    @Autowired
    MainDistribution mainDistribution;
    @Autowired
    CalculateBaseInterface calculateBaseInterface;
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    JdbcTemplate jdbcTemplate;
    @Autowired
    GetTransferData getTransferData;
    @Autowired
    GetPassengerFlowOracle getPassengerFlowOracle;

//    @Test
//    public void testDistribution() {
//        List<GetSourceData> sourceDataList = getTransferDataDao.getSourceDataList();
//        ArrayList<StoreTransferData> storeTransferData = new ArrayList<>();
//        for (int num = 0; num < sourceDataList.size(); num++) {
////        测试一个od
//            mainDistribution.getDistribution(sourceDataList.get(num).getInStation() + " " + sourceDataList.get(num).getOutStation());
////        动态分配该od
//            Map<DirectedEdge[], String> dynamicOdPathSearch = calculateBaseInterface.dynamicOdPathSearch(sourceDataList.get(num).getInStation() + " " + sourceDataList.get(num).getOutStation());
////        客流分配换乘最少
//            DirectedEdge[] totalTransfer = mainDistribution.totalTransfer(dynamicOdPathSearch);
//////        客流分配费用最少
////            DirectedEdge[] minWeightPath = mainDistribution.minWeightPath(dynamicOdPathSearch);
////        暂存换入线路
//            ArrayList<DirectedEdge> switchIn = new ArrayList<>();
////        暂存换出线路
//            ArrayList<DirectedEdge> switchOut = new ArrayList<>();
////        暂存换乘站编号
//            ArrayList<String> transferId = new ArrayList<>();
//
////        模拟乘客人数
//            Integer passenger = sourceDataList.get(num).getPassengers();
////        循环找到换乘车站
//            for (int i = 0; i < totalTransfer.length; i++) {
//                if (totalTransfer[i].getDirection().equals("o")) {
//                    switchIn.add(totalTransfer[i - 1]);
//                    switchOut.add(totalTransfer[i + 1]);
//                    transferId.add(totalTransfer[i].getEdge().getToNode());
//                }
//            }
//            System.out.println(transferId);
////            判断若没有换乘站，则跳出循环
//            if (transferId.isEmpty()) {
//                continue;
//            }
//            for (int i = 0; i < switchOut.size(); i++) {
////          初始化数据库模型
//                StoreTransferData data = new StoreTransferData();
//                try {
//                    data.setDate(new SimpleDateFormat("yyyy-mm-dd").parse(sourceDataList.get(num).getDataDT()));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
////          获得正确的换乘id
//                String transfer = getTransferDataDao.getTransferId(transferId.get(i));
//                data.setTransfer(transfer);
////          得换乘方向
//                String transferDirectionSign = switchOut.get(i).getDirection() + "" + switchIn.get(i).getDirection();
////          数据库取换入换出线路
//                String outLine = getTransferDataDao.getLine(switchOut.get(i).getEdge().getFromNode(), switchOut.get(i).getEdge().getToNode());
//                String inline = getTransferDataDao.getLine(switchIn.get(i).getEdge().getFromNode(), switchIn.get(i).getEdge().getToNode());
//                data.setOutLineId(outLine);
//                data.setInLineId(inline);
////            四种换乘结果处理
//                switch (transferDirectionSign) {
//                    case "11":
//                        data.setDownInDownOutNum(passenger);
//                        break;
//                    case "12":
//                        data.setDownInUpOutNum(passenger);
//                        break;
//                    case "22":
//                        data.setUpInUpOutNum(passenger);
//                        break;
//                    case "21":
//                        data.setUpInDownOutNum(passenger);
//                        break;
//                }
//                storeTransferData.add(data);
//                System.out.println(data.toString());
//            }
//        }
//        for (int i = 0; i < storeTransferData.size(); i++) {
//            try {
//                int update = getTransferDataDao.updateData(storeTransferData.get(i).getUpInUpOutNum(), storeTransferData.get(i).getUpInDownOutNum(), storeTransferData.get(i).getDownInUpOutNum(), storeTransferData.get(i).getDownInDownOutNum(),
//                        storeTransferData.get(i).getDate(), storeTransferData.get(i).getTransfer(), storeTransferData.get(i).getInLineId(), storeTransferData.get(i).getOutLineId());
//                if (update == 0) {
//                    getTransferDataDao.insertData(storeTransferData.get(i).getDate(), storeTransferData.get(i).getTransfer(), storeTransferData.get(i).getInLineId(), storeTransferData.get(i).getOutLineId(),
//                            storeTransferData.get(i).getUpInUpOutNum(), storeTransferData.get(i).getUpInDownOutNum(), storeTransferData.get(i).getDownInUpOutNum(), storeTransferData.get(i).getDownInDownOutNum());
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Test
    public void testPassengerFlow() {
//        passengerFlowService.passengerFlow();
    }

    @Test
    public void testPassengerFlowService() {
    }
}
