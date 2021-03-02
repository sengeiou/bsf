package com.weking.model.log;

import com.weking.core.enums.OperateTypeEnum;
import com.wekingframework.core.LibDateUtils;

public class OperationLog {
    private Integer id;

    private Integer userId;

    private Short operateType;

    private String operateName;

    private Long addTime;

    private Integer extend_id;

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

    public Short getOperateType() {
        return operateType;
    }

    public void setOperateType(Short operateType) {
        this.operateType = operateType;
    }

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName == null ? null : operateName.trim();
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public Integer getExtend_id() {
        return extend_id;
    }

    public void setExtend_id(Integer extend_id) {
        this.extend_id = extend_id;
    }

    public static OperationLog getOperationLog(int userId,short operateType,int extend_id){
        OperationLog operationLog = new OperationLog();
        operationLog.setUserId(userId);
        operationLog.setOperateType(operateType);
        operationLog.setAddTime(LibDateUtils.getLibDateTime());
        operationLog.setOperateName(OperateTypeEnum.getTypeEnum(operateType).getName());
        operationLog.setExtend_id(extend_id);
        return operationLog;
    }

}