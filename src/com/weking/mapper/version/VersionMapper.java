package com.weking.mapper.version;

import com.weking.model.version.Version;
import org.apache.ibatis.annotations.Param;

public interface VersionMapper {
    
    Version selectByType(@Param("type") int type,@Param("projectName")String project_name);
}