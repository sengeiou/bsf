package com.weking.mapper.activity;

import com.weking.model.activity.ActivityList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ActivityListMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ActivityList record);

    int insertSelective(ActivityList record);

    ActivityList selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ActivityList record);

    int updateByPrimaryKey(ActivityList record);

    List<ActivityList> selectAllActivity();
    List<ActivityList> selectUnclosedActivity(@Param("end_time") long end_time);

    List<ActivityList> selectGiftIdByListId(@Param("list") List<Integer> list);
}