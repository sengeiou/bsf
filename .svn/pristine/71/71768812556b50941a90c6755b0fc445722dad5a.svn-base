package com.weking.mapper.log;

import com.weking.model.log.ScaGoldLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScaGoldLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ScaGoldLog record);

    int insertSelective(ScaGoldLog record);

    ScaGoldLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ScaGoldLog record);

    int updateByPrimaryKey(ScaGoldLog record);

    List<ScaGoldLog> selectScaGoldLogsByUserId(@Param("userId") int userId,@Param("index") int index,@Param("count") int count);

}