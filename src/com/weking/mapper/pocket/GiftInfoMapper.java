package com.weking.mapper.pocket;

import com.weking.model.pocket.GiftInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GiftInfoMapper {

    GiftInfo selectByPrimaryKey(Integer giftId);
    //获取所有的礼物
    List<GiftInfo>  selectAllGift();

    List<GiftInfo> selectGiftListByLiveType(@Param("liveType") int liveType);

    List<Integer> selectGiftIdByPrice(@Param("price") int price);

}