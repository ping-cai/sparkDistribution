package cn.edu.sicau.pfdistribution.dao.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author xieyuan
 */
@Service
public class MysqlGetID implements Serializable {
    transient
    @Autowired
    private MysqlSaveImpl mysqlSaveImpl;

    public Map<Integer, Integer> carID() {
        return mysqlSaveImpl.selectLineId();
    }

    public Map<Integer, List<String>> idTime() {
        return mysqlSaveImpl.selectTime();
    }
}
