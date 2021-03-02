package com.weking.model.task;

public class TaskCountsLog {
    private Integer id;

    private Integer userId;

    private Integer taskId;

    private Integer count;

    private Long addTime;

    private Byte taskState;

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

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public Byte getTaskState() {
        return taskState;
    }

    public void setTaskState(Byte taskState) {
        this.taskState = taskState;
    }
}