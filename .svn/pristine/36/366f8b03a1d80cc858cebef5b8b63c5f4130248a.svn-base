package com.weking.model.log;

import java.math.BigDecimal;

public class ScaGoldLog {
    private Long id;

    private Integer userId;

    private BigDecimal origNum;

    private BigDecimal convNum;

    private Integer type;

    private Long addTime;

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

    public BigDecimal getOrigNum() {
        return origNum;
    }

    public void setOrigNum(BigDecimal origNum) {
        this.origNum = origNum;
    }

    public BigDecimal getConvNum() {
        return convNum;
    }

    public void setConvNum(BigDecimal convNum) {
        this.convNum = convNum;
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

    public static ScaGoldLog getScaGoldLog(int userId,BigDecimal origNum,BigDecimal convNum,int type,long addTime){
        ScaGoldLog scaGoldLog = new ScaGoldLog();
        scaGoldLog.setUserId(userId);
        scaGoldLog.setOrigNum(origNum);
        scaGoldLog.setConvNum(convNum);
        scaGoldLog.setAddTime(addTime);
        scaGoldLog.setType(type);
        return scaGoldLog;
    }
}