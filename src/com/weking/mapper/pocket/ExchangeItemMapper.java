package com.weking.mapper.pocket;

import com.weking.model.pocket.ExchangeItem;

import java.util.List;

public interface ExchangeItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ExchangeItem record);

    int insertSelective(ExchangeItem record);

    ExchangeItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ExchangeItem record);

    int updateByPrimaryKey(ExchangeItem record);

    List<ExchangeItem> selectAllExchangeItems();


}