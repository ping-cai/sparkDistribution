package cn.edu.sicau.pfdistribution.service.Web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KspServiceImpl implements KspService {
    @Autowired
    SectionIdAndSectionCrowdNum sectionIdAndSectionCrowdNum;

    /**
     * @return 含区间id、区间拥挤度和区间拥挤度等级的对象列表
     * @author weiyongzhao
     */
    @Override
    public List<CrowdNumResult> getSectionCrowdNumBySectionId(GetSectionCrowdNumInitialParameter getSectionCrowdNumInitialParameter) {
        List<CrowdNumResult> list = new ArrayList<>();
        Map<Integer, String> SectionIdAndSectionCrowdNum;
        if (!getSectionCrowdNumInitialParameter.getSectionId().equals("")) {
            int sectionID = Integer.parseInt(getSectionCrowdNumInitialParameter.getSectionId());
            Map<Integer, String> map = sectionIdAndSectionCrowdNum.getSectionIdAndSectionCrowdNum(sectionID);
            SectionIdAndSectionCrowdNum = map;
        } else {
            Map<Integer, String> map = sectionIdAndSectionCrowdNum.getSectionIdAndSectionCrowdNum();
            SectionIdAndSectionCrowdNum = map;
        }
        for (Map.Entry<Integer, String> entry : SectionIdAndSectionCrowdNum.entrySet()) {
            CrowdNumResult crowdNumResult = new CrowdNumResult();
            Integer sectionId = entry.getKey();
            String crowdNum = entry.getValue();
            String crowdGrade;
            if (Double.parseDouble(crowdNum) < 0.5) {
                crowdGrade = "不拥挤";
            } else if (Double.parseDouble(crowdNum) > 0.5 && Double.parseDouble(crowdNum) < 0.8) {
                crowdGrade = "轻度拥挤";
            } else {
                crowdGrade = "十分拥挤";
            }
            crowdNumResult.setSectionId(sectionId);
            crowdNumResult.setSectionCrowdNum(crowdNum);
            crowdNumResult.setSectionCrowdInfo(crowdGrade);
            list.add(crowdNumResult);
        }
        return list;
    }
}
