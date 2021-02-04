package cn.edu.sicau.pfdistribution.service.Web;

import org.springframework.stereotype.Service;

/**
 * author：weiyongzhao
*传入参数SectionID的类
* */

@Service
public class GetSectionCrowdNumInitialParameter {
    private String sectionId;

    public GetSectionCrowdNumInitialParameter() {
        this.sectionId = null;
    }

    public GetSectionCrowdNumInitialParameter(String sectionId) {
        if(sectionId.equals(""))
            this.sectionId = null;
        else
            this.sectionId = sectionId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }
}
