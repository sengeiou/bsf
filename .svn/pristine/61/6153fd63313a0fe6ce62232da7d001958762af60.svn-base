package com.weking.mapper.level;

import com.weking.model.level.LevelEffect;
import org.apache.ibatis.annotations.Param;

public interface LevelEffectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LevelEffect record);

    int insertSelective(LevelEffect record);

    LevelEffect selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LevelEffect record);

    int updateByPrimaryKey(LevelEffect record);

    LevelEffect selectByLevel(@Param("level") int level);
}