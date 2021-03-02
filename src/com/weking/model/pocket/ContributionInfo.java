package com.weking.model.pocket;

public class ContributionInfo {
    private Integer id;

    private Integer anchorId;

    private Integer sendId;

    private Long sendTotalTicket;

    private String nickname;

    private String picheadUrl;

    private String account;

    private Integer level;

    private Integer sex;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(Integer anchorId) {
        this.anchorId = anchorId;
    }

    public Integer getSendId() {
        return sendId;
    }

    public void setSendId(Integer sendId) {
        this.sendId = sendId;
    }

    public Long getSendTotalTicket() {
        return sendTotalTicket;
    }

    public void setSendTotalTicket(Long sendTotalTicket) {
        this.sendTotalTicket = sendTotalTicket;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPicheadUrl() {
        return picheadUrl;
    }

    public void setPicheadUrl(String picheadUrl) {
        this.picheadUrl = picheadUrl;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }
}