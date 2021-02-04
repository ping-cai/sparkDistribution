package cn.edu.sicau.pfdistribution.entity;

/**
 *@author:周致远
 */
import java.util.List;
/**
 * 定义搜索到的路径的返回类型
 */
public class PathSearch {
    private String type;
    private List<String> stationList;
    private List<List<String>> transferList;
    private String totalTimeStr;
    private String totalPriceStr;
    private List<Integer> sectionList;
    public PathSearch(String type, String totalTimeStr, String totalPriceStr, List<Integer> sectionList, List<String> stationList, List<List<String>> transferList){
        this.type=type;
        this.stationList=stationList;
        this.transferList=transferList;
        this.sectionList=sectionList;
        this.totalPriceStr=totalPriceStr;
        this.totalTimeStr=totalTimeStr;
    }

    public String getTotalTimeStr() {
        return totalTimeStr;
    }

    public void setTotalTimeStr(String totalTimeStr) {
        this.totalTimeStr = totalTimeStr;
    }

    public String getTotalPriceStr() {
        return totalPriceStr;
    }

    public void setTotalPriceStr(String totalPriceStr) {
        this.totalPriceStr = totalPriceStr;
    }

    public List<Integer> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<Integer> sectionList) {
        this.sectionList = sectionList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getStationList() {
        return stationList;
    }

    public void setStationList(List<String> stationList) {
        this.stationList = stationList;
    }

    public List<List<String>> getTransferList() {
        return transferList;
    }

    public void setTransferList(List<List<String>> transferList) {
        this.transferList = transferList;
    }
}
