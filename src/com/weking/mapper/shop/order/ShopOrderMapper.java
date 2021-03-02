package com.weking.mapper.shop.order;

import com.weking.model.shop.order.ShopOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShopOrderMapper {

    int insert(ShopOrder record);

    ShopOrder findByOrderId(@Param("id") Integer orderId,@Param("userId")int userId);

    int updateShopOrderStateByPaySn(@Param("paymentTime") long paymentTime,@Param("paySn")long paySn);

    //查询用户订单列表
    List<ShopOrder> selectUserOrderList(@Param("userId") int userId,@Param("orderState")int orderState,@Param("index")int index,@Param("count")int count);

    List<ShopOrder> selectShopOrderListByPaySn(@Param("userId") int userId,@Param("paySn") long paySn);

    int updateShopOrderPaymentCodeByPaySn(@Param("paymentCode")String paymentCode,@Param("paySn")long paySn);

    List<ShopOrder> selectOrderByPaySn(@Param("userId") int userId,@Param("paySn")long paySn);

    int cancelOrderByOrderIds(@Param("array")String[] orderIds,@Param("userId")int userId);

    int delOrderByOrderId(@Param("orderId")int orderId,@Param("userId")int userId);

    int confirmReceiptByOrderId(@Param("orderId")int orderId,@Param("userId")int userId,@Param("finnshedTime")long finnshedTime);

    List<ShopOrder> selectNotPayOrderByList(@Param("addTime")long addTime);

    List<ShopOrder> findListByPaySn(@Param("paySn")long paySn);

    Double findOrderAmountByPaySn(@Param("paySn")long paySn);

    ShopOrder selectShopOrderByPaySn(@Param("paySn") long paySn);
}