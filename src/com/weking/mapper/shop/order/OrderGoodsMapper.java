package com.weking.mapper.shop.order;

import com.weking.model.shop.order.OrderGoods;

import java.util.List;

public interface OrderGoodsMapper {

    int insertBatch(List<OrderGoods> record);

    List<OrderGoods> selectListByOrderId(Integer orderId); //查询订单商品列表

    List<OrderGoods> selectGoodsListByOrderIds(List<Integer> orderIds); //通过订单集合查询商品列表

    List<OrderGoods> selectOrderGoodsListByOrderIds(String[] orderIds);

}