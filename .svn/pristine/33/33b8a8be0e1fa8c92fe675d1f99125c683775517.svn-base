package com.weking.mapper.live;

import com.weking.model.live.VideoChat;
import org.apache.ibatis.annotations.Param;

/**
 * @author Administrator
 */
public interface VideoChatMapper {

    int insert(VideoChat record);

    VideoChat findVideoChatById(Integer id);

    int updateEndTimeById(@Param("id") int id, @Param("endTime") long addTime, @Param("diffTime") long diffTime);

}