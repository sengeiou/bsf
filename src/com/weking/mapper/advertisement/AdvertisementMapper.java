package com.weking.mapper.advertisement;

import com.weking.model.advertisement.Advertisement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdvertisementMapper {

    List<Advertisement> selectByType(@Param("type") Integer type,@Param("project_name") String project_name);

}