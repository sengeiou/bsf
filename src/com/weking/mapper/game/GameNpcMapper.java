package com.weking.mapper.game;

import com.weking.model.game.GameNpc;

import java.util.List;

public interface GameNpcMapper {

    //获取所有的礼物
    List<GameNpc> selectAllNpc();

}