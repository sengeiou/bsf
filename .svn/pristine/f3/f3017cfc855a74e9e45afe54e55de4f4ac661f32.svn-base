package com.weking.mapper.digital;

import com.weking.model.digital.DigitalWalletAddress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DigitalWalletAddressMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DigitalWalletAddress record);

    int insertSelective(DigitalWalletAddress record);

    DigitalWalletAddress selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DigitalWalletAddress record);

    int updateByPrimaryKey(DigitalWalletAddress record);

    DigitalWalletAddress selectUnuseAddress(String symbol);

}