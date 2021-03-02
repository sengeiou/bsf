package com.weking.model.shop.order;

public class OrderCommon {
    private Integer id;

    private Integer storeId;

    private Integer evaluationTime;

    private String evalsellerState;

    private Integer evalsellerTime;

    private String orderMessage;

    private Integer addressId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer getEvaluationTime() {
        return evaluationTime;
    }

    public void setEvaluationTime(Integer evaluationTime) {
        this.evaluationTime = evaluationTime;
    }

    public String getEvalsellerState() {
        return evalsellerState;
    }

    public void setEvalsellerState(String evalsellerState) {
        this.evalsellerState = evalsellerState == null ? null : evalsellerState.trim();
    }

    public Integer getEvalsellerTime() {
        return evalsellerTime;
    }

    public void setEvalsellerTime(Integer evalsellerTime) {
        this.evalsellerTime = evalsellerTime;
    }

    public String getOrderMessage() {
        return orderMessage;
    }

    public void setOrderMessage(String orderMessage) {
        this.orderMessage = orderMessage == null ? null : orderMessage.trim();
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }
}