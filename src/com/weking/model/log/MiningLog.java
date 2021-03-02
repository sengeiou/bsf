package com.weking.model.log;

public class MiningLog {
    private Long id;

    private Integer userId;

    private Integer amount;

    private Long addTime;

    private Byte optType;

    private String remark;

    private Long extendId;

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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public Byte getOptType() {
        return optType;
    }

    public void setOptType(Byte optType) {
        this.optType = optType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Long getExtendId() {
        return extendId;
    }

    public void setExtendId(Long extendId) {
        this.extendId = extendId;
    }

    public static MiningLog getMiningLog(int userId,int amount,long addTime,byte optType,String remark,long extendId){
        MiningLog miningLog = new MiningLog();
        miningLog.setUserId(userId);
        miningLog.setAmount(amount);
        miningLog.setAddTime(addTime);
        miningLog.setOptType(optType);
        miningLog.setRemark(remark);
        miningLog.setExtendId(extendId);
        return miningLog;
    }
}