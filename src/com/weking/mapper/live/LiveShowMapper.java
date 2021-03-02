package com.weking.mapper.live;

import com.weking.model.live.LiveShow;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LiveShowMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LiveShow record);

    int insertSelective(LiveShow record);

    LiveShow selectByPrimaryKey(Integer id);

    List<Map<String, Object>>  selectAllLiveShow(@Param("live_time") long live_time);

    int updateByPrimaryKeySelective(LiveShow record);

    int updateByPrimaryKey(LiveShow record);
}