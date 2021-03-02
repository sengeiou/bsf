package com.weking.core.digital;

/**
 * Created by Administrator on 2018/5/24.
 */
public class RespObj extends  Resp {
    //@ApiModelProperty(value = "返回数据")
    private Object data;

    public RespObj(Integer _status, String _message, Object _data) {
        super(_status, _message);
        this.data = _data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespObj{" +
                "data=" + data +
                '}';
    }
}
