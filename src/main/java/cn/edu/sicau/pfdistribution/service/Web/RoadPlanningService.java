package cn.edu.sicau.pfdistribution.service.Web;
/**
 *@author:周致远
 */
import cn.edu.sicau.pfdistribution.entity.PathSearch;
import cn.edu.sicau.pfdistribution.entity.SWJTU_DTO;

import java.util.List;

public interface RoadPlanningService {
    List<PathSearch> kspResult(SWJTU_DTO swjtu_dto);

}
