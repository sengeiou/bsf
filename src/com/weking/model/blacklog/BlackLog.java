package com.weking.model.blacklog;

public class BlackLog {
    private Integer Id;

    private Integer userId;

    private Integer beuserId;

    private Long addtime;

    private String nickname;

    private String account;

    private String picheadUrl;

    private Integer level;

    private Short sex;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBeuserId() {
        return beuserId;
    }

    public void setBeuserId(Integer beuserId) {
        this.beuserId = beuserId;
    }

    public Long getAddtime() {
        return addtime;
    }

    public void setAddtime(Long addtime) {
        this.addtime = addtime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPicheadUrl() {
        return picheadUrl;
    }

    public void setPicheadUrl(String picheadUrl) {
        this.picheadUrl = picheadUrl;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Short getSex() {
        return sex;
    }

    public void setSex(Short sex) {
        this.sex = sex;
    }
}