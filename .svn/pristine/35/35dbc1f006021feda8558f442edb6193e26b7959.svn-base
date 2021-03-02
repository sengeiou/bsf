package com.weking.mapper.task;

import com.weking.model.task.TaskLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskLogMapper {

    int insert(TaskLog record);

    TaskLog selectByPrimaryKey(Integer id);

    TaskLog findEndFreeCoin(@Param("userId") int userId, @Param("addTime") long addTime); //

    int selectFreeCoinCountReceive(@Param("userId") int userId, @Param("addTime") long addTime); //

    int selectTaskCountByUserId(@Param("userId") int userId, @Param("taskId") int taskId, @Param("addTime") long addTime); //

    TaskLog selectLastLoginTaskLogByUserId(int userId); //查询最后一条登录奖励记录

    Integer selectShareTaskCountByUserId(@Param("userId") int userId, @Param("taskId") int taskId, @Param("addTime") long addTime);

    TaskLog selectShareTaskLogByUserId(@Param("userId") int userId, @Param("taskId") int taskId, @Param("addTime") long addTime);

    int editShareTaskByUserId(@Param("rewardCoin") int rewardCoin, @Param("id") int id);

    int selectReceiveCountByToday(@Param("userId") int userId, @Param("addTime") long addTime);

    List<TaskLog> selectLogByType(@Param("user_id") int userId, @Param("task_type") int task_type, @Param("bTime") long bTime, @Param("eTime") long eTime);

    TaskLog selectLogByTaskId(@Param("user_id") int userId, @Param("task_id") int task_id, @Param("bTime") long bTime, @Param("eTime") long eTime);

    TaskLog selectLastLiveSinInByUserId(int userId); //查询最后一条直播间签到记录

}