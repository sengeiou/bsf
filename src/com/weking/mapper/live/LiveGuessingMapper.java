package com.weking.mapper.live;

import com.weking.model.live.LiveGuessing;
import org.apache.ibatis.annotations.Param;

public interface LiveGuessingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LiveGuessing record);

    int insertSelective(LiveGuessing record);

    LiveGuessing selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LiveGuessing record);

    int updateByPrimaryKey(LiveGuessing record);

    int updateByIdAndEndTime(@Param("id")int id,@Param("end_time") Long end_time);
}