package cn.edu.sicau.pfdistribution.dao;

public interface CheckDataValidateInter {
    boolean checkDataExistence(String tableName, String startTime, Integer timeInterval);
    void deleteIncompleteData(String tableName, String startTime, String endTime, Integer timeInterval);
}
