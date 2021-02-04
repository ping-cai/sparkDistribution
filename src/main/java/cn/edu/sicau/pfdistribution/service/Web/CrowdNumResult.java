package cn.edu.sicau.pfdistribution.service.Web;

/**
 *
 * @author：weiyongzhao
 * @return: 区间id、区间拥挤度和区间拥挤度等级
 */

public class CrowdNumResult {
    private int sectionId;
    private String sectionCrowdNum;
    private String sectionCrowdInfo;

    public CrowdNumResult(){

    }
    public CrowdNumResult(int sectionId, String sectionCrowdNum, String sectionCrowdInfo){
        this.sectionId=sectionId;
        this.sectionCrowdNum=sectionCrowdNum;
        this.sectionCrowdInfo=sectionCrowdInfo;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionCrowdNum() {
        return sectionCrowdNum;
    }

    public void setSectionCrowdNum(String sectionCrowdNum) {
        this.sectionCrowdNum = sectionCrowdNum;
    }

    public String getSectionCrowdInfo() {
        return sectionCrowdInfo;
    }

    public void setSectionCrowdInfo(String sectionCrowdInfo) {
        this.sectionCrowdInfo = sectionCrowdInfo;
    }
}
