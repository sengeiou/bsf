package com.weking.mapper.log;

import com.weking.model.log.OperationLog;
import org.apache.ibatis.annotations.Param;

public interface OperationLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OperationLog record);

    int insertSelective(OperationLog record);

    OperationLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OperationLog record);

    int updateByPrimaryKey(OperationLog record);

    OperationLog selectByUserIdAndType(@Param("userId") int user_id,@Param("type") int type);
}