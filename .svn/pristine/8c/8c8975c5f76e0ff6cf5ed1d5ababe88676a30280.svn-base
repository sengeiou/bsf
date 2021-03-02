package com.weking.mapper.task;

import com.weking.model.task.TaskItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务列表接口
 */
public interface TaskItemMapper {

    TaskItem selectByPrimaryKey(Integer id);

    List<TaskItem> getList();

    List<TaskItem> getLoginTaskList();

    int getNumTask(@Param("taskType") int taskType, @Param("taskNumber") int taskNumber);

    int getCountTask(); //获取直播间任务总数

    List<TaskItem> getListByTaskType(@Param("task_type") int task_type);

}