package cn.edu.sicau.pfdistribution.service.kspdistribution;

import cn.edu.sicau.pfdistribution.dao.Impl.MysqlGetID;
import cn.edu.sicau.pfdistribution.entity.LineIdAndSectionTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author xieyuan
 */
@Service

public class GetLineID implements Serializable{
    transient
    @Autowired
    private MysqlGetID GetID;

    @Autowired
    private LineIdAndSectionTime stationIdToLineId;

    public void setStationId(){
        Map<Integer,Integer> idToLine=GetID.carID();
        Map<Integer, List<String>> sectionTime=GetID.idTime();
        stationIdToLineId.setStationIdToLineId(idToLine);
        stationIdToLineId.setSectionTime(sectionTime);
    }

}
