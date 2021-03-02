package com.weking.mapper.digital;

import com.weking.model.digital.DigitalWallet;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DigitalWalletMapper {
    int deleteByPrimaryKey(Long id);
    //获取下注者剩余钱包里的sca
    int getSenderLeftDiamondbyid(@Param("userId") int userId);

    //获取下注者分红的sca
    int getUserWithdrawAmount(@Param("userId") int userId);

    List<DigitalWallet> batchSelectUsersDiamond(@Param("map") Map map);
    //去增加主播的Sca
    int increaseTicketByUserid(@Param("currAmount") int currAmount, @Param("userid") int userid);
    int increaseTicketWithDrawAmountByUserid(@Param("withdrawAmount") int withDrawAmount, @Param("userid") int userid);

    int increaseDiamondByUserId(@Param("userId")int userId,@Param("currAmount")int currAmount); //增加用户钱包SCA
    int increaseWithDrawAmountByUserId(@Param("userId")int userId,@Param("withdrawAmount")int withdrawAmount); //增加用户分红SCA

    int deductAllDiamondByUserId(@Param("currAmount") int currAmount, @Param("userid") int userid);

    int deductAllDiamondWithDrawAmountByUserId(@Param("withdrawAmount") int withDrawAmount, @Param("userid") int userid);

    int batchIncreaseDiamond(@Param("map") Map map);

    int batchIncreaseDiamondWithdrawAmount(@Param("map") Map map);

    int insert(DigitalWallet record);

    int insertSelective(DigitalWallet record);

    DigitalWallet selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DigitalWallet record);

    int updateByPrimaryKey(DigitalWallet record);

    List<DigitalWallet> selectByUserId(Long user_id);

    DigitalWallet selectByUserIdSymbol(@Param("user_id") int user_id, @Param("symbol") String symbol);

    DigitalWallet selectByAddress(@Param("address") String address, @Param("symbol") String symbol);

    DigitalWallet selectByKey(@Param("id") long id);
}