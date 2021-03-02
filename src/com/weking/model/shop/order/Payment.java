package com.weking.model.shop.order;

public class Payment {
    private Byte id;

    private String paymentCode;

    private String paymentName;

    private String paymentState;

    private String paymentConfig;

    public Byte getId() {
        return id;
    }

    public void setId(Byte id) {
        this.id = id;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode == null ? null : paymentCode.trim();
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName == null ? null : paymentName.trim();
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState == null ? null : paymentState.trim();
    }

    public String getPaymentConfig() {
        return paymentConfig;
    }

    public void setPaymentConfig(String paymentConfig) {
        this.paymentConfig = paymentConfig == null ? null : paymentConfig.trim();
    }
}