package com.weking.model.log;

public class EventLog {
    private Integer id;

    private Integer userId;

    private String luckycode;

    private Long event1Time;

    private Long event2Time;

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

    public String getLuckycode() {
        return luckycode;
    }

    public void setLuckycode(String luckycode) {
        this.luckycode = luckycode == null ? null : luckycode.trim();
    }

    public Long getEvent1Time() {
        return event1Time;
    }

    public void setEvent1Time(Long event1Time) {
        this.event1Time = event1Time;
    }

    public Long getEvent2Time() {
        return event2Time;
    }

    public void setEvent2Time(Long event2Time) {
        this.event2Time = event2Time;
    }
}