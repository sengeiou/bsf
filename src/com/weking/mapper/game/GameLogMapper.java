package com.weking.mapper.game;

import com.weking.model.game.GameLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GameLogMapper {

    int insert(GameLog record);

    List<Integer> selectGameByLive(@Param("liveId") Integer liveId, @Param("index") int index, @Param("count") int count);

    int updateByPrimaryKeySelective(GameLog record);
}