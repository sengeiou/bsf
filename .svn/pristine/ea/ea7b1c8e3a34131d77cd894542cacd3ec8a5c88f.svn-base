package com.weking.mapper.digital;

import com.weking.model.digital.DigitalWalletLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DigitalWalletLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DigitalWalletLog record);

    int insertSelective(DigitalWalletLog record);

    DigitalWalletLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DigitalWalletLog record);

    int updateByPrimaryKey(DigitalWalletLog record);

    List<DigitalWalletLog> selectByUserIdSymbol(@Param("user_id") int user_id, @Param("symbol") String symbol);

    List<DigitalWalletLog> selectListByUserIdSymbol(@Param("user_id") int user_id, @Param("symbol") String symbol,
                                                    @Param("offset") int offset, @Param("limit") int limit);

    List<DigitalWalletLog> selectListByUserIdSymbolAndType(@Param("user_id") int user_id, @Param("optTypeList") String[] optTypeList,@Param("symbol") String symbol,
                                                    @Param("offset") int offset, @Param("limit") int limit);

}