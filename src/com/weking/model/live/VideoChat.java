package com.weking.model.live;

public class VideoChat {
    private Integer id;

    private Integer userId;

    private Integer otherId;

    private String userStream;

    private Byte type;

    private Long startTime;

    private Long endTime;

    private Integer diffTime;

    private Integer costPrice;

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

    public Integer getOtherId() {
        return otherId;
    }

    public void setOtherId(Integer otherId) {
        this.otherId = otherId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getDiffTime() {
        return diffTime;
    }

    public void setDiffTime(Integer diffTime) {
        this.diffTime = diffTime;
    }

    public Integer getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Integer costPrice) {
        this.costPrice = costPrice;
    }

    public String getUserStream() {
        return userStream;
    }

    public void setUserStream(String userStream) {
        this.userStream = userStream;
    }
}