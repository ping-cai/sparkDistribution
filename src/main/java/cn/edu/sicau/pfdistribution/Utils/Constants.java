package cn.edu.sicau.pfdistribution.Utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final String DIRECTION_1 = "u";
    public static final String DIRECTION_2 = "d";
    public static final String PARAM_ID = "PARAM_ID";
    public static final String PARAM_NAME = "PARAM_NAME";
    public static final String RETURN_EDGE_ID = "RETURN_ID";
    public static final String RETURN_EDGE_NAME = "RETURN_NAME";
    public static final String CHANGE_STATION = "o";
    public static final String ENVIRONMENT_RISK = "u0001u0001";
    public static final String DEVICE_RISK = "DEVICE_RISK";
    public static final int CHANGE_LENGTH = 0;
    public static final String STATION_ID = "StationId";
    public static final String SECTION_ID = "SectionId";
    public static final String ALARM_LEVEL = "AlarmLevel";
    public static final String ALARM_START_TIME = "StartTime";
    public static final String ALARM_END_TIME = "EndTime";
    public static final String ALARM__TIME_FORMAT = "yyyy/MM/DD HH:mm:ss";
    public static String DATA_DATE_DAY = "2018/9/1";
    public static String SAVE_DATE_START;
    public static String SAVE_DATE_TEMP;
    public static int DATA_DATE_START_HOUR = 0;
    public static int DATA_DATE_MIN = 0;
    public static int timeInterval = 0;

    public static final Map<String, String> javaToOracle;

    static {
        javaToOracle = new HashMap<>();
        javaToOracle.put("java.lang.String", "varchar(50)");
        javaToOracle.put("int", "number");
    }

}
