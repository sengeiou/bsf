package com.weking.model.game;

public class GameLog {
    private Long id;

    private Integer liveId;

    private Integer userId;

    private Byte gameType;

    private Byte winId;

    private Long gameTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLiveId() {
        return liveId;
    }

    public void setLiveId(Integer liveId) {
        this.liveId = liveId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Byte getGameType() {
        return gameType;
    }

    public void setGameType(Byte gameType) {
        this.gameType = gameType;
    }

    public Byte getWinId() {
        return winId;
    }

    public void setWinId(Byte winId) {
        this.winId = winId;
    }

    public Long getGameTime() {
        return gameTime;
    }

    public void setGameTime(Long gameTime) {
        this.gameTime = gameTime;
    }
}