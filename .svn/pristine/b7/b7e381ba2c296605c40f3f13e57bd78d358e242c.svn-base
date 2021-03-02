package com.weking.mapper.pocket;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.weking.model.pocket.ContributionInfo;

public interface ContributionInfoMapper {

    int insert(ContributionInfo record);

    int updateByPrimaryKeySelective(ContributionInfo record);

    //修改一条数据
    int updateContirbution(@Param("sendid") int sendid, @Param("anchorid") int anchorid, @Param("ticket") int ticket);

    List<ContributionInfo> selectContributionUserList(@Param("anchorId") int anchorId, @Param("offset") int index, @Param("limit") int count);

    List<Map<String, Object>> getContributionOrder(@Param("offset") int index, @Param("limit") int count); //总消费榜单

    List<Map<String, Object>> getAllIncomeOrder(@Param("offset") int index, @Param("limit") int count); //总收入榜单

    //根据id获取赠送给主播的赤币
    Integer getSendTotalTicket(@Param("send_id") int send_id, @Param("anchor_id") int anchor_id);

}