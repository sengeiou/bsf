package com.weking.model.live;

public class LiveAdvanceNotice {
    private Integer id;

    private Integer userId;

    private Long liveTime;

    private String account;

    private String nickname;

    private String pichead_url;

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

    public Long getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(Long liveTime) {
        this.liveTime = liveTime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPichead_url() {
        return pichead_url;
    }

    public void setPichead_url(String pichead_url) {
        this.pichead_url = pichead_url;
    }
}