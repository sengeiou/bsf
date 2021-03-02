package com.weking.mapper.log;

import com.weking.model.log.FrozenLog;
import org.apache.ibatis.annotations.Param;

public interface FrozenLogMapper {

    int insert(FrozenLog record);

    Integer findDiamondById(Integer id);

    int updateFrozenState(@Param("id")Integer id,@Param("state")Integer state);

}