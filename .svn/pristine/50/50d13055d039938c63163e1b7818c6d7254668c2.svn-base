package com.weking.mapper.log;

import com.weking.model.log.ConsumeInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ConsumeInfoMapper {

    int insertSelective(ConsumeInfo record);

    int updateLiveIdById(@Param("id") int id,@Param("live_id") int live_id);

    //获取当次直播的总收入
    long getThisTimeTotalTiecket(@Param("anchorid") int anchorid, @Param("liveLogid") int liveLogid);

    //获取当次直播的前三名贡献榜
    List getTopThreeSender(@Param("anchorid") int anchorid, @Param("liveLogid") int liveLogid);

    //获取本场榜消费排名
    List<ConsumeInfo> getCurrentConsumptionList(@Param("liveLogId") int liveLogId, @Param("offset") int index, @Param("limit") int count);

    //获得月消费榜单
    List<Map<String, Object>> getConsumeOrder(@Param("sendTime") Long liveStart, @Param("offset") int offset, @Param("limit") int limit);

    //获得收入榜单
    List<Map<String, Object>> getIncomeOrder(@Param("sendTime") Long liveStart, @Param("offset") int offset, @Param("limit") int limit);

    //获取获取用户最近一次购买门票的记录
    ConsumeInfo getLastPayTicket(@Param("user_id") int user_id);

    //获取获取用户购买门票的记录
    ConsumeInfo getPayTicket(@Param("user_id") int user_id,@Param("live_id") int live_id,@Param("buy_type") int buy_type);

    //获取获取用户购买门票的记录
    ConsumeInfo getPayTicketByAnchorId(@Param("user_id") int user_id,@Param("anchor_id") int anchor_id,@Param("buy_type") int buy_type);

    //判断用户是否有消费日志记录
    int getDailyConsumeTime(@Param("userId") Integer userId, @Param("today") Long today);

    //获得区间消费榜单
    List<Map<String, Object>> getConsumeOrderByDay(@Param("beginTime") Long beginTime,@Param("endTime") Long endTime, @Param("offset") int offset, @Param("limit") int limit);

    //获得区间收入榜单
    List<Map<String, Object>> getIncomeOrderByDay(@Param("beginTime") Long beginTime,@Param("endTime") Long endTime, @Param("offset") int offset, @Param("limit") int limit);

    //获得区间收入榜单
    List<Map<String, Object>> getGiftNumRank(@Param("giftId") int giftId,@Param("beginTime") Long beginTime,@Param("endTime") Long endTime, @Param("offset") int offset, @Param("limit") int limit);

    //获得端午节活动礼物的合计
    List<Map<String, Object>> getGiftCountBoat(@Param("beginTime") Long beginTime,@Param("endTime") Long endTime, @Param("offset") int offset, @Param("limit") int limit);


    //获得区间 七夕收入榜单  多个礼物
    List<Map<String, Object>> getIncomeOrderBySection(@Param("beginTime") Long beginTime,@Param("endTime") Long endTime, @Param("offset") int offset, @Param("limit") int limit,@Param("list") List<Integer> list);


    //获得区间 七夕收入榜单  多个礼物 大于等于56有条件
    List<Map<String, Object>> getIncomeOrderBySectionAndAnchorLevel(@Param("beginTime") Long beginTime,@Param("endTime") Long endTime, @Param("offset") int offset, @Param("limit") int limit,@Param("list") List<Integer> list);

    //获得区间 七夕收入榜单  多个礼物 有条件
    List<Map<String, Object>> getIncomeOrderBySectionAndLevel(@Param("beginTime") Long beginTime,@Param("endTime") Long endTime, @Param("offset") int offset, @Param("limit") int limit,@Param("list") List<Integer> list);

    //获得区间  七夕消费榜单 多个礼物
    List<Map<String, Object>> getConsumeOrderBySection(@Param("beginTime") Long beginTime,@Param("endTime") Long endTime, @Param("offset") int offset, @Param("limit") int limit,@Param("list") List<Integer> list);


    //获得区间 七夕收入榜单  单个礼物
    List<Map<String, Object>> getIncomeOrderBySectionOne(@Param("giftId") int giftId,@Param("beginTime") Long beginTime,@Param("endTime") Long endTime, @Param("offset") int offset, @Param("limit") int limit);

    //获得区间  七夕消费榜单 单个礼物
    List<Map<String, Object>> getConsumeOrderBySectionOne(@Param("giftId") int giftId,@Param("beginTime") Long beginTime,@Param("endTime") Long endTime, @Param("offset") int offset, @Param("limit") int limit);

    //获取守护收益
    List<Map<String, Object>> getGuardIncome(@Param("receive_id") int receive_id,@Param("beginTime") Long beginTime,@Param("endTime") Long endTime);

    //获取使用记录
    List<Map<String, Object>> getConsumeByUserId(@Param("send_id") Integer send_id, @Param("offset") int offset, @Param("limit") int limit);
    //获得送礼记录
    List<Map<String, Object>> getConsumeByUserIdAndTime(@Param("send_id") Integer send_id, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime, @Param("offset") int offset, @Param("limit") int limit);

    //获取未核销用户的合计
    List<Map<String, Object>> payNowCancel(@Param("sendTime") Long sendTime);

    //修改核销状态
    int updateConsumeIsDeduct(@Param("send_id") int send_id,@Param("sendTime") Long sendTime);



}