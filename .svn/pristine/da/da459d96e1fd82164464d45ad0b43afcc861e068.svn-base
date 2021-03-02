package com.weking.model.pocket;

import com.wekingframework.core.LibDateUtils;

public class UserGain {
    private Integer id;

    private Integer userId;

    private Integer type;

    private Integer buyNum;

    private Long addTime;

    private Integer extendId;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(Integer buyNum) {
        this.buyNum = buyNum;
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


    public static UserGain getGain(Integer userId,Integer type,Integer buyNum,int extendId){
        UserGain userGain = new UserGain();
        userGain.setAddTime(LibDateUtils.getLibDateTime());
        userGain.setUserId(userId);
        userGain.setBuyNum(buyNum);
        userGain.setExtendId(extendId);
        userGain.setType(type);
        return userGain;
    }
}