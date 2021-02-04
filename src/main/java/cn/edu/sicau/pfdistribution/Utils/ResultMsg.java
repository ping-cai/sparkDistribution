package cn.edu.sicau.pfdistribution.Utils;

import cn.edu.sicau.pfdistribution.entity.KspSearchResult;

import java.util.List;

/**
 * 定义返回的json数据的格式
 */
public class ResultMsg<T> {
    private int status;
    private String msg;
    private List<T> data;

    public ResultMsg() {
    }

    public ResultMsg(int status, String msg, List<T> data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
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

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
