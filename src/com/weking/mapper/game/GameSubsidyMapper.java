package com.weking.mapper.game;

import com.weking.model.game.GameSubsidy;

public interface GameSubsidyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GameSubsidy record);

    int insertSelective(GameSubsidy record);

    GameSubsidy selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GameSubsidy record);

    int updateByPrimaryKey(GameSubsidy record);
}