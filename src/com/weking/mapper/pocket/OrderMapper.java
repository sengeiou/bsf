package com.weking.mapper.pocket;

import com.weking.model.pocket.Order;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderMapper {

    int insert(Order record);

    Order selectByPrimaryKey(@Param("id")long Id);

    Order findByRechargeId(@Param("userId")int userId,@Param("rechargeId")int rechargeId);

    Order selectByOrderSn(@Param("userId")int userId,@Param("orderSn")String orderSn);

    Order selectByOrderSnOne(@Param("orderSn")String orderSn);

    int updateByOrderId(@Param("Id")long Id,@Param("tradeNo")String tradeNo,@Param("buyNum")int buyNum);

    int updateByOrderIdAndApplePay(@Param("Id")long Id,@Param("tradeNo")String tradeNo,@Param("buyNum")int buyNum);

    List<Order> selectPayListByUserId(@Param("userId")int userId,@Param("index")int index,@Param("count")int count);

    List<Order> selectRechargeListByUserId(@Param("userId")int userId,@Param("index")int index,@Param("count")int count);

    int selectCountByTradeNo(String tradeNo);

    int updateOrderSettle(String orderSn);

    int updateOrderRatio(@Param("id")long orderId, @Param("ratio")BigDecimal ratio);

    List<Map<String, Object>>  selectPayListByUserIdOrOther(@Param("userId")int userId, @Param("paymentCode")int paymentCode, @Param("state")int state, @Param("beginTime") Long beginTime, @Param("endTime") Long endTime, @Param("index")int index, @Param("count")int count);

}