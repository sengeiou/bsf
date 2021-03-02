package com.weking.mapper.follow;

import com.weking.model.follow.FollowInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface FollowInfoMapper {
    
    int deleteByUserid(@Param("followid") int followid, @Param("befollowid") int befollowid);

    int insertSelective(FollowInfo record);
    
    Map<String, Integer> getStarsFansNum(int userid);

    int getFansNum(int userid);
    
    int  verifyIsFollowed(@Param("followId") int followId, @Param("befollowedId") int befollowedId);

    List<Map<String, Object>> getFansOrder(@Param("followTime") Long followTime ,@Param("offset") int offset, @Param("limit") int limit);

    List<Integer> getUserFollowerList(@Param("userId") int userId );

    List<Map<String,Object>> findUserFollowerList(@Param("userId") int user_id);
}