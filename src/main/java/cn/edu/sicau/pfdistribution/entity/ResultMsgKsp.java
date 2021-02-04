package cn.edu.sicau.pfdistribution.entity;

import java.util.List;
/**
 * @author 周致远
 */


public class ResultMsgKsp {
    private int status;
    private String msg;
    private List<PathSearch> data;
    /**
     * @param status 状态码
     * @param msg 消息状态
     * @param data 数据body
     * 定义返回的json数据的格式
     */
    public ResultMsgKsp(int status, String msg, List<PathSearch> data) {
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

    public List<PathSearch> getData() {
        return data;
    }

    public void setData(List<PathSearch> data) {
        this.data = data;
    }
}
