package com.weking.mapper.shop.order;

import com.weking.model.shop.order.ShopTransLog;

public interface ShopTransLogMapper {

    int insert(ShopTransLog record);

    ShopTransLog selectByPrimaryKey(Integer id);

    int selectCountByTransId(String transId);
}