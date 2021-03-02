package com.weking.mapper.pocket;

import com.weking.model.pocket.PlatformIncome;
import org.apache.ibatis.annotations.Param;

public interface PlatformIncomeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PlatformIncome record);

    int insertSelective(PlatformIncome record);

    PlatformIncome selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PlatformIncome record);

    int updateByPrimaryKey(PlatformIncome record);

    Double getTodayPlatformIncome(@Param("day_date") long day_date);

    PlatformIncome findTodayPlatformIncome(@Param("day_date") long day_date,@Param("type") int type);



}