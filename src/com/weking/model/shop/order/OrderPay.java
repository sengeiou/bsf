package com.weking.model.shop.order;

public class OrderPay {
    private Integer id;

    private Long paySn;

    private Integer buyerId;

    private String apiPayState;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getPaySn() {
        return paySn;
    }

    public void setPaySn(Long paySn) {
        this.paySn = paySn;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public String getApiPayState() {
        return apiPayState;
    }

    public void setApiPayState(String apiPayState) {
        this.apiPayState = apiPayState == null ? null : apiPayState.trim();
    }
}