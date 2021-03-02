package com.weking.mapper.task;

import com.weking.model.task.TaskCountsLog;
import org.apache.ibatis.annotations.Param;

public interface TaskCountsLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TaskCountsLog record);

    int insertSelective(TaskCountsLog record);

    TaskCountsLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TaskCountsLog record);

    int updateByPrimaryKey(TaskCountsLog record);

    int increaseCountsByPrimaryKey(@Param("id")int id, @Param("time")long time, @Param("count")long count);

    int updateTaskStateByKey(@Param("id")int id, @Param("state")int state);

    TaskCountsLog selectTaskCountsLogByIdAndTime(@Param("userId")int userId, @Param("taskId")int task_id, @Param("time")long time);

}