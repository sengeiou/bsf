package com.weking.mapper.game;

import com.weking.model.game.GameData;

public interface GameDataMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GameData record);

    int insertSelective(GameData record);

    GameData selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GameData record);

    int updateByPrimaryKey(GameData record);
}