package cn.edu.sicau.pfdistribution.service.Web;
/**
 *@author:周致远
 */
import cn.edu.sicau.pfdistribution.dao.oracle.GetSectionId;
import cn.edu.sicau.pfdistribution.dao.oracle.OracleGetTransferStationsById;
import cn.edu.sicau.pfdistribution.entity.PathSearch;
import cn.edu.sicau.pfdistribution.entity.SWJTU_DTO;
import cn.edu.sicau.pfdistribution.service.kspdistribution.MainDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoadPlanningServiceImpl implements RoadPlanningService {

    @Autowired
    private MainDistribution mainDistribution;

    @Autowired
    private OracleGetTransferStationsById oracleGetTransferStationsById;

    @Autowired
    private GetSectionId getSectionId;


    @Override
    public List<PathSearch> kspResult(SWJTU_DTO swjtu_dto) {

        List<PathSearch> PlanningResults=new ArrayList<>();
        //搜索到的时间最少、换乘最少的两条路径
        Map map = (Map) mainDistribution.getDistribution(swjtu_dto.getStartStation()+" "+swjtu_dto.getEndStation());
        Iterator it=map.keySet().iterator();
        for(int i=1;it.hasNext();i++){
            List<String> odStations;
            //map的key值
            String odStation=it.next().toString();
            //实例：["24","46","35",...]得到车站id列表
            odStations= Arrays.asList(odStation.split(","));

            //map的value值:路径长度和总时间  "25.0 850.1354"
            String LengthTime= (String) map.get(odStation);
            List<String> ValueLT;
            //从value值中分出路径长度、总时间
            ValueLT= Arrays.asList(LengthTime.split(" "));
            Double value= Double.valueOf(ValueLT.get(0));
            String Time= ValueLT.get(1);
            String price = getCost(value);

            List<List<String>> transferStations=oracleGetTransferStationsById.getTransferStations(odStations);
            List<String> stations=oracleGetTransferStationsById.getStationsById(odStations);
            List<Integer> sectionList = getSectionId.sectionList(odStations);
            PathSearch networkResult=new PathSearch(Integer.toString(i), Time, price,sectionList,stations,transferStations);
            PlanningResults.add(networkResult);
        }
        return PlanningResults;
    }

    private String getCost(Double value) {
        /**
         * 优先判断里程数中最集中的部分，再判断分散的部分，提高运行效率
         */
        String price;
        if(value>24 && value<=32){
            price = "6";
        }
        else if(value>17 && value<=24){
            price = "5";
        }
        else if(value>32 && value<=41){
            price = "7";
        }
        else if(value>11 && value<=17){
            price = "4";
        }
        else if(value>41 && value<=51){
            price = "8";
        }
        else if(value>6 && value<=11){
            price = "3";
        }
        else if(value>51 && value<=63){
            price = "9";
        }
        else if(value<=6){
            price = "2";
        }
        else
            price = "10";
        return price;
    }


}
