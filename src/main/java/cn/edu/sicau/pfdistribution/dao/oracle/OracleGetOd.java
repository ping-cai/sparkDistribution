package cn.edu.sicau.pfdistribution.dao.oracle;


import java.util.List;
import java.util.Map;

public interface OracleGetOd {
    void kspRegionAdd(String path, double passenger, String startStation, String endStation);

    void deleteAllKspRegion();

    void saveTransfer(String data_day, String data_hour, String transfer_station, double transfer_passengers);
}

