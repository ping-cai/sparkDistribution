package cn.edu.sicau.pfdistribution.Utils;

import cn.edu.sicau.pfdistribution.entity.KspQueryResult;

import java.util.List;

/**
 * @author 李勇平
 * @param status状态码
 * @param msg消息状态码
 * @param data数据体
 * 定义返回的json数据的格式
 */
public class ResultMsg2 {
    private int status;
    private String msg;
    private List<KspQueryResult> data;
    public ResultMsg2(int status,String msg,List<KspQueryResult> data){
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

    public List<KspQueryResult> getData() {
        return data;
    }

    public void setData(List<KspQueryResult> data) {
        this.data = data;
    }
}
