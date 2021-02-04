package cn.edu.sicau.pfdistribution.service.Web;
import java.util.List;

public interface KspService {
    List<CrowdNumResult> getSectionCrowdNumBySectionId(GetSectionCrowdNumInitialParameter getSectionCrowdNumInitialParameter);
}
