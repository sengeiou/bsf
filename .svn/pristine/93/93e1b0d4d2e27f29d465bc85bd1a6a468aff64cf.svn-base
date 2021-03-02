package com.weking.mapper.log;

import com.weking.model.log.EditLog;
import org.apache.ibatis.annotations.Param;

public interface EditLogMapper {

    int insert(EditLog record);

    Integer findIdByUserId(@Param("userId") Integer userId,@Param("editKey")String editKey,@Param("addTime")Long addTime);
}