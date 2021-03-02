package com.weking.mapper.log;

import com.weking.model.log.LiveLogInfo;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface LiveLogInfoMapper {

    LiveLogInfo selectByPrimaryKey(Integer id);

    String findLiveStreamIdById(Integer id);

    int insertSelective(LiveLogInfo record);

    int updateByPrimaryKeySelective(LiveLogInfo record);

    int updateLiveStatus(@Param("userId") int userId, @Param("liveId") int liveId, @Param("status") int status);

    //更新结束时间
    int updateLiveEndByUserId(@Param("live_end") long live_end, @Param("user_id") int user_id, @Param("diff") long diff);

    Map getLiveTime(@Param("userId") int userId, @Param("liveStart") long liveStart);

    //获取最新直播
    List<LiveLogInfo> getNewLiving(@Param("project_name")String project_name,@Param("offset") int offset, @Param("limit") int limit);

    //获取热门直播
    List<LiveLogInfo> getHotLiving(@Param("project_name")String project_name,@Param("offset") int offset, @Param("limit") int limit, @Param("liveId") int liveId);

    //获取热门直播
    List<LiveLogInfo> getHotLivingIsOfficial(@Param("project_name")String project_name,@Param("offset") int offset, @Param("limit") int limit, @Param("liveId") int liveId);

    //获取关注直播
    List<LiveLogInfo> getFollowLiving(@Param("project_name")String project_name,@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    //获取tags直播
    List<LiveLogInfo> getTagsLiving(@Param("project_name")String project_name,@Param("tagName") String tagName, @Param("offset") int offset, @Param("limit") int limit);

    //获取附近直播
    List<LiveLogInfo> getNearbyLiving(@Param("project_name")String project_name,@Param("minLng") Double minLng, @Param("maxLng") Double maxlng, @Param("minLat") Double minLat, @Param("maxLat") Double maxLat, @Param("offset") int offset, @Param("limit") int limit);

    //获取用户录播记录
    List<LiveLogInfo> getPlayback(@Param("project_name")String project_name,@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit, @Param("status1") int status1, @Param("status2") int status2);

    //获取用户正在直播的记录
    LiveLogInfo selectLivingByUserId(@Param("userId") int userId);

    //获取推荐的录播列表
    List<LiveLogInfo> getRecommendRecord(@Param("project_name")String project_name,@Param("offset") int offset, @Param("limit") int limit);

    //获取电商直播列表
    List<LiveLogInfo> getShopLiveList(@Param("project_name")String project_name,@Param("offset") int offset, @Param("limit") int limit);

    //获得活跃榜单
    List<Map<String, Object>> getMyLiveTimeOrder(@Param("liveStart") Long liveStart, @Param("offset") int offset, @Param("limit") int limit);

    //获取VIP直播
    List<LiveLogInfo> getVIPLiving(@Param("project_name")String project_name,@Param("offset") int offset, @Param("limit") int limit);

    //获取VIP直播
    List<LiveLogInfo> getVIPLivingByTag(@Param("project_name")String project_name,@Param("tagName") String tagName, @Param("offset") int offset, @Param("limit") int limit);

    //获取私密直播
    List<LiveLogInfo> getPrivacyLiving(@Param("project_name")String project_name,@Param("offset") int offset, @Param("limit") int limit);

    //获取私密直播
    List<LiveLogInfo> getPrivacyLivingByTag(@Param("project_name")String project_name,@Param("tagName") String tagName, @Param("offset") int offset, @Param("limit") int limit);

    //获取推荐的VIP录播列表
    List<LiveLogInfo> getVIPRecommendRecord(@Param("project_name")String project_name,@Param("offset") int offset, @Param("limit") int limit);



    //更新点赞数量
    int updateLike(@Param("like_count") int like_count, @Param("live_id") int live_id);

    //获取游戏直播
    List<LiveLogInfo> getGameLiveList(@Param("project_name")String project_name,@Param("game_category_id") String game_category_id, @Param("offset") int offset, @Param("limit") int limit);

    //正在直播的直播数量
    int getIsLivingCount();


    //获取所有直播
    List<LiveLogInfo> getAllLivingIsOfficial(@Param("project_name")String project_name, @Param("liveId") int liveId);

    LiveLogInfo getRemandLivingIsOfficial();

    //获取最近的一条直播记录
    LiveLogInfo findLiveLogInfoByUserId(@Param("userId") int userId);

    //获取上一条最近的一条直播记录
    LiveLogInfo findLiveLogInfoByEndAndUserId(@Param("userId") int userId);

    //获取热榜直播
    List<LiveLogInfo> getHotRankList(@Param("project_name")String project_name,@Param("offset") int offset, @Param("limit") int limit, @Param("sorts") int sorts, @Param("liveId") int liveId);

    //获取直播收益
    List<Map<String, Object>> getMonthIncome(@Param("user_id") int user_id,@Param("beginTime") Long beginTime,@Param("endTime") Long endTime);

    //更新录播地址
    int updateLiveReplayUrl(@Param("replayUrl") String replayUrl,@Param("liveStreamId") String liveStreamId);

    //根据流获取房间id和用户id
    LiveLogInfo selectByStreamId(@Param("liveStreamId") String liveStreamId);
}