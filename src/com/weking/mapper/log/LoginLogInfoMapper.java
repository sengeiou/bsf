package com.weking.mapper.log;

import com.weking.model.log.LoginLogInfo;

public interface LoginLogInfoMapper {

    int insert(LoginLogInfo record);

    LoginLogInfo selectByPrimaryKey(Integer id);
}