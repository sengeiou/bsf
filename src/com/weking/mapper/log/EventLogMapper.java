package com.weking.mapper.log;

import com.weking.model.log.EventLog;

public interface EventLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(EventLog record);

    int insertSelective(EventLog record);

    EventLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EventLog record);

    int updateByPrimaryKey(EventLog record);

    EventLog selectByUserId(Integer user_id);
}