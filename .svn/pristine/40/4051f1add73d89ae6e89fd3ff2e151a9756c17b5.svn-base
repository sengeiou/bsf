package com.weking.mapper.game;

import com.weking.model.game.BetLog;
import org.apache.ibatis.annotations.Param;

public interface BetLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BetLog record);

    int insertSelective(BetLog record);

    BetLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BetLog record);

    int updateByPrimaryKey(BetLog record);

    int deleteBetLogByGameId(@Param("gameId") long gameId);
}