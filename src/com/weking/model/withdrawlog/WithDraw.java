package com.weking.model.withdrawlog;

public class WithDraw {
    private Integer id;

    private Integer userId;

    private Long drawTime;

    private Double drawMoney;

    private Byte approveState;

    private Long approveTime;

    private String paymentSn;

    private String tradeNo;

    private Double draw_num;

    private Byte draw_type;

    private Byte pay_type;

    private String pay_account;

    private String pay_name;

    private String bank_account;

    private String bank_name;

    private Long extend_id;

    private Double fee;

    private String currency;

    private String eth_address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(Long drawTime) {
        this.drawTime = drawTime;
    }

    public Double getDrawMoney() {
        return drawMoney;
    }

    public void setDrawMoney(Double drawMoney) {
        this.drawMoney = drawMoney;
    }

    public Byte getApproveState() {
        return approveState;
    }

    public void setApproveState(Byte approveState) {
        this.approveState = approveState;
    }

    public Long getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Long approveTime) {
        this.approveTime = approveTime;
    }

    public String getPaymentSn() {
        return paymentSn;
    }

    public void setPaymentSn(String paymentSn) {
        this.paymentSn = paymentSn == null ? null : paymentSn.trim();
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
    }

    public Double getDraw_num() {
        return draw_num;
    }

    public void setDraw_num(Double draw_num) {
        this.draw_num = draw_num;
    }

    public Byte getDraw_type() {
        return draw_type;
    }

    public void setDraw_type(Byte draw_type) {
        this.draw_type = draw_type;
    }

    public Byte getPay_type() {
        return pay_type;
    }

    public void setPay_type(Byte pay_type) {
        this.pay_type = pay_type;
    }

    public String getPay_account() {
        return pay_account;
    }

    public void setPay_account(String pay_account) {
        this.pay_account = pay_account;
    }

    public String getPay_name() {

        return pay_name;
    }

    public void setPay_name(String pay_name) {
        this.pay_name = pay_name;
    }

    public String getBank_account() {
        return bank_account;
    }

    public void setBank_account(String bank_account) {
        this.bank_account = bank_account;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public Long getExtend_id() {
        return extend_id;
    }

    public void setExtend_id(Long extend_id) {
        this.extend_id = extend_id;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEth_address() {
        return eth_address;
    }

    public void setEth_address(String eth_address) {
        this.eth_address = eth_address;
    }
}