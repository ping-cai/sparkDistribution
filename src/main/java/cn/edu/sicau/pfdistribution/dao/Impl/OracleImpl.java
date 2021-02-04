package cn.edu.sicau.pfdistribution.dao.Impl;

import cn.edu.sicau.pfdistribution.dao.oracle.OracleGetOd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class OracleImpl implements OracleGetOd {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public void kspRegionAdd(String path, double passenger, String startStation, String endStation) {
        jdbcTemplate.update("insert into \"SCOTT\".\"kspregion\"values(?,?,?,?)", path, passenger, startStation, endStation);
    }

    @Override
    public void deleteAllKspRegion() {
        jdbcTemplate.update("delete from \"SCOTT\".\"kspregion\" where 1=1");
    }

    //储存换乘站点和换乘人数
    @Override
    public void saveTransfer(String data_day, String data_hour, String transfer_station, double transfer_passengers) {
        jdbcTemplate.update("insert into \"SCOTT\".\"transferTable\" values(?,?,?,?)", data_day, data_hour, transfer_station, transfer_passengers);
    }


}
