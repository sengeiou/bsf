package com.weking.model.account;

import com.wekingframework.core.LibDateUtils;

public class UserBill {
    private Long id;

    private Integer userId;

    private Double amount;

    private Integer type;

    private Long addTime;

    private Integer extendId;

    private Integer coin_type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public Integer getExtendId() {
        return extendId;
    }

    public void setExtendId(Integer extendId) {
        this.extendId = extendId;
    }

    public Integer getCoin_type() {
        return coin_type;
    }

    public void setCoin_type(Integer coin_type) {
        this.coin_type = coin_type;
    }

    public static UserBill getBill(int userId,double amount,int coin_type,int extendId,int type){
        UserBill userBill = new UserBill();
        userBill.setUserId(userId);
        userBill.setAmount(amount);
        userBill.setCoin_type(coin_type);
        userBill.setExtendId(extendId);
        userBill.setType(type);
        userBill.setAddTime(LibDateUtils.getLibDateTime());
        return userBill;
    }
}