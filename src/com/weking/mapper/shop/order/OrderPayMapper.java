package com.weking.mapper.shop.order;

import com.weking.model.shop.order.OrderPay;

public interface OrderPayMapper {

    int insert(OrderPay record);

    int updatePayState(Long paySn); //更新支付状态为已支付

}