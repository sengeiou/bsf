package com.weking.mapper.pocket;

import com.weking.model.pocket.PocketInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PocketInfoMapper {

    int insert(PocketInfo record);
    
    PocketInfo selectByUserid(Integer userid);
    //去扣送出者的钻石 付费emo
    int deductDiamondByUserid(@Param("diamond") int diamond, @Param("userid") int userid);

    //去扣送出者的钻石 免费emo
    int deductFreeDiamondByUserId(@Param("diamond") int diamond, @Param("userId") int userId);


    int frozenDiamondByUserId(@Param("diamond") int diamond, @Param("userId") int userId);

    int deductFrozenByUserId(@Param("diamond") int diamond, @Param("userId") int userId);

    int backFrozenByUserId(@Param("diamond") int diamond, @Param("userId") int userId);
    //去增加主播的赤票
    int increaseTicketByUserid(@Param("ticket") int ticket, @Param("userid") int userid);

    int increaseDiamondByUserId(@Param("userId")int userId,@Param("diamond")int diamond); //增加用户虚拟货币

    int increaseFreeDiamondByUserId(@Param("userId")int userId,@Param("diamond")int diamond); //增加免费用户虚拟货币


    //获取送出者剩余的钻石
    int getSenderLeftDiamondbyid(Integer userid);
    //获取主播的赤票
    int getAnchorTicketbyid(Integer userid);

    int batchIncreaseDiamond(@Param("map") Map map);

    List<PocketInfo> batchSelectUsersDiamond(@Param("map") Map map);

    int deductAllDiamondByUserId(@Param("diamond") int diamond, @Param("userid") int userid);

    int getAnchorMoneyByUserId(Integer userId);

    int batchIncreaseDiamondByAccount(@Param("map") Map map);

    List<Map<String, Object>> getTotalTicketOrder(@Param("offset") int index, @Param("limit") int count);

    List<Map<String, Object>> getContributionOrder(@Param("offset") int index, @Param("limit") int count);

    // 更新用户sca gold
    int updateScaGoldByUserId(@Param("userId")int userId,@Param("sca_gold")int sca_gold);


    // 减少用户sca gold
    int updateUserScaGoldByUserId(@Param("userId")int userId,@Param("sca_gold")int sca_gold);

    List<Map<String,Object>> selectScaGoldUser();



}