package com.weking.mapper.log;

import com.weking.model.log.MiningLog;

public interface MiningLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MiningLog record);

    int insertSelective(MiningLog record);

    MiningLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MiningLog record);

    int updateByPrimaryKey(MiningLog record);
}