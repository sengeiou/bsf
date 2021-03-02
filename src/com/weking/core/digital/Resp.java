package com.weking.core.digital;

/**
 * Created by Administrator on 2018/5/24.
 */
public class Resp extends ErrorMsg {
    public static final Integer SUCCESS = 1;
    public static final Integer FAIL = -1;
    public static final String SUCCESS_MSG = "success";

    //@ApiModelProperty(value = "状态值，1表示成功，其他值表示失败")
    private Integer status;
    //@ApiModelProperty(value = "错误信息")
    private String msg;

    public Resp(Integer _status, String _msg) {
        this.status = _status;
        this.msg = _msg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Resp{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                '}';
    }
}
