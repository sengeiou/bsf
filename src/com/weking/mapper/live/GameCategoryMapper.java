package com.weking.mapper.live;

import com.weking.model.live.GameCategory;

import java.util.List;

public interface GameCategoryMapper {

    int insert(GameCategory record);

    GameCategory selectByPrimaryKey(Integer id);

    List<GameCategory> getAll();
}