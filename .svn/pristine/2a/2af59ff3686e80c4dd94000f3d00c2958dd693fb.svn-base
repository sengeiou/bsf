package com.weking.mapper.blacklog;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.weking.model.blacklog.BlackLog;

public interface BlackLogMapper {
    int deleteByPrimaryKey(Integer blackId);

    int insert(BlackLog record);

    int selectUserRelation(@Param("userId")int userId,@Param("beuserId") int beuserId);

    BlackLog verifiUserBlack(@Param("userId")int userId,@Param("beuserId") int beuserId);

    List<BlackLog> selectBlackList(@Param("userId") Integer userId,@Param("offset") int offset,@Param("limit") int limit);
}