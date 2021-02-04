package cn.edu.sicau.pfdistribution.service.Web;

import java.util.Map;

/**
* author： 魏永昭
* */
public interface GetCZ12IdFromDatabase {
   Map getCZ12IDNoParameterFromDatabase();
   Map<String,Integer>  getCZ12IDWithParameterFromDatabase(Integer id);
}
