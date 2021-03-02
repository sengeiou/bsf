package com.weking.mapper.live;

import com.weking.model.live.LiveTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LiveTagMapper {

    int insert(LiveTag record);

    LiveTag selectByPrimaryKey(Integer id);

    LiveTag selectByTagType(@Param("tag_type") int tag_type);

    List<LiveTag> getAll(@Param("tag_type") int tag_type);
    List<LiveTag> getAllAndActivity(@Param("tag_type") int tag_type);

    List<LiveTag> getNewAll();
}