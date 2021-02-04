package cn.edu.sicau.pfdistribution.service.Web;

import java.util.List;

/**
 * author:weiyongzhao
 * json格式下status，msg，data返回类
 **/
public class ReturnResult {
    private int status;
    private String msg;
    private List<CrowdNumResult>data;

    public ReturnResult(int status, String msg, List<CrowdNumResult>data){
        this.status=status;
        this.msg=msg;
        this.data=data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<CrowdNumResult> getData() {
        return data;
    }

    public void setData(List<CrowdNumResult> data) {
        this.data = data;
    }
}
