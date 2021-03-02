package com.weking.mapper.game;

import com.weking.model.game.GameBet;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GameBetMapper {

    int insert(GameBet record);

    /**
     * 每天游戏胜利次数
     */
    int getDailyWinTime(@Param("userId") Integer userId, @Param("today") Long today);

    List<Map<String, Object>> getGameOrder(@Param("month") Long month, @Param("offset") int index, @Param("limit") int count); //用户游戏排行

    List<Map<String, Object>> getDailyGameOrder(@Param("day") Long day, @Param("offset") int index, @Param("limit") int count); //用户游戏日排行

    int insertBatch(List list);
}