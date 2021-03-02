package com.weking.mapper.live;

import com.weking.model.live.LiveAdvanceNotice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LiveAdvanceNoticeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LiveAdvanceNotice record);

    int insertSelective(LiveAdvanceNotice record);

    LiveAdvanceNotice selectByPrimaryKey(Integer id);

    LiveAdvanceNotice selectByUserIdAndTime(@Param("userId") int userId, @Param("live_time") Long live_time);

    int updateByPrimaryKeySelective(LiveAdvanceNotice record);

    int updateByPrimaryKey(LiveAdvanceNotice record);

    int updateById(@Param("id") int id, @Param("live_time") Long live_time);

    List<LiveAdvanceNotice> findAnchorNoticeByUserId(@Param("userId") int userId, @Param("live_time") Long live_time);

    List<LiveAdvanceNotice> findAnchorNoticeListByTime(@Param("start_time") Long start_time, @Param("end_time") Long end_time);
}