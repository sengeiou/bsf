package com.weking.mapper.digital;

import com.weking.model.digital.SCAWalletLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SCAWalletLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SCAWalletLog record);

    int insertSelective(SCAWalletLog record);

    SCAWalletLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SCAWalletLog record);

    int updateByPrimaryKey(SCAWalletLog record);


    List<SCAWalletLog> selectByUserIdSymbol(@Param("user_id") int user_id, @Param("symbol") String symbol);

    List<SCAWalletLog> selectListByUserIdSymbol(@Param("user_id") int user_id, @Param("symbol") String symbol,
                                                    @Param("offset") int offset, @Param("limit") int limit);

    List<SCAWalletLog> selectListByUserIdSymbolAndType(@Param("user_id") int user_id, @Param("optTypeList") String[] optTypeList,@Param("symbol") String symbol,
                                                           @Param("offset") int offset, @Param("limit") int limit);
}