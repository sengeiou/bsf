package com.weking.mapper.game;

import com.weking.model.game.GameFire;
import org.apache.ibatis.annotations.Param;

/**
 * Created by zb on 2017/6/21.
 * 游戏抓娃娃，打飞机
 */
public interface GameFireMapper {
    /**
     * 每天游戏胜利次数
     */
    int getDailyWinTime(@Param("userId") Integer userId, @Param("today") Long today);

    int insert(GameFire gameFire);

    GameFire selectByPrimaryKey(Integer id);
}
