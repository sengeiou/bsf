package com.weking.mapper.game;

import com.weking.model.game.AppGameLog;

public interface AppGameLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppGameLog record);

    int insertSelective(AppGameLog record);

    AppGameLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppGameLog record);

    int updateByPrimaryKey(AppGameLog record);
}