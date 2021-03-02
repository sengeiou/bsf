package com.weking.model.shop.order;

import java.util.LinkedHashMap;
import java.util.Map;

public class OrderInfo {

    public static Map<String,String> notPayOrderMap = new LinkedHashMap<>(); //缓存未支付订单时间

    public static Long LastOrderTime = 0L;  //记录最后查询订单时间

    public static int intervalTime = 60;  //记录查询未支付订单时间间隔距过期间隔时间(分钟)

    public static void removeOrderInfo(String orderId){
        if(notPayOrderMap != null){
            notPayOrderMap.remove(orderId);
        }
    }

}