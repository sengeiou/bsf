package com.weking.mapper.digital;

import com.weking.model.digital.DigitalCurrency;

import java.util.List;

public interface DigitalCurrencyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DigitalCurrency record);

    int insertSelective(DigitalCurrency record);

    DigitalCurrency selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DigitalCurrency record);

    int updateByPrimaryKey(DigitalCurrency record);

    double selectByCurrency(String currency);

    List<DigitalCurrency> selectCurrency();
}