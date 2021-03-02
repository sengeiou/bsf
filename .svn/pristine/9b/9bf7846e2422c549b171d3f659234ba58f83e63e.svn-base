package com.weking.mapper.live;

import com.weking.model.live.LiveGuard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LiveGuardMapper {

    int insert(LiveGuard record);

    int insertSelective(LiveGuard record);

    LiveGuard findLiveGuardByAnchorId(Integer anchorId);

    int updateStateByAnchorId(Integer anchorId);

    int updateStateById(Integer id);

    List<LiveGuard> selectInvalidList(Integer anchorId);

    List<LiveGuard> selectLiveGuardList();

    LiveGuard findLiveGuardById(Integer id);

    List<LiveGuard> getLiveGuardRand(int anchorId);

    Integer getAllLiveGuardPrice(@Param("userId")Integer userId,@Param("anchorId")Integer anchorId);

}