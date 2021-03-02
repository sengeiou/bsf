package com.weking.controller.shop;

import com.weking.controller.out.OutControllerBase;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.service.shop.AddressService;
import com.weking.service.shop.OrderService;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO 2017/2/14.
 */
@Controller
@RequestMapping({"/order","/mall/order"})
public class OrderController extends OutControllerBase {

    @Resource
    private OrderService orderService;
    @Resource
    private AddressService addressService;


    /**
     * 确认订单
     */
    @RequestMapping("/confirm")
    public void confirm(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.optInt("user_id");
            String goods_info = getParameter(request,"goods_info");
            int address_id = getParameter(request,"address_id",0);
            String lang_code = object.optString("lang_code");
            object = orderService.getOrderConfirmInfo(userId,goods_info,address_id,lang_code);
        }
        out(response,object,api_version);
    }

    /**
     * 提交订单
     */
    @RequestMapping("/submitOrder")
    public void submitOrder(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.optInt("user_id");
            String goods_info = getParameter(request,"goods_info");
            String store_msg = getParameter(request,"store_msg");
            int type = getParameter(request,"type",1);
            int address_id = getParameter(request,"address_id",0);
            //double api_version = getParameter(request, C.RequestParam.api_version, 1.0);
            //String ip = request.getRemoteAddr();
            String lang_code = object.optString("lang_code");
            JSONObject deliveryAddress = addressService.getDeliveryAddressInfo(userId,address_id,lang_code);
            System.out.println(" 1 " + deliveryAddress.toString());
            JSONObject orderInfo = orderService.getOrderInfo(goods_info,address_id,lang_code);
            System.out.println(" 2 " + orderInfo.toString());
            object = orderService.submitOrderInfo(userId,orderInfo,store_msg,deliveryAddress,lang_code,api_version);
            System.out.println(" 3 " + object.toString());
            if(type == 2 && object.getInt("code")== ResultCode.success){ //清除购物车商品
                orderService.removeOrderGoodsInCart(userId,goods_info);
            }
        }
        out(response,object,api_version);
    }

    /**
     * 用户订单列表
     */
    @RequestMapping("/getOrderList")
    public void getOrderList(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.optInt("user_id");
            int type = getParameter(request,"type",0);
            int index = getParameter(request,"index",0);
            int count = getParameter(request,"count",10);
            object = orderService.getShopOrderList(userId,type,index,count);
            System.out.println(object.toString()
            );
        }
        out(response,object,api_version);
    }

    /**
     * 用户订单详情
     */
    @RequestMapping("/getOrderDetail")
    public void getOrderDetail(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.optInt("user_id");
            int orderId = getParameter(request,"order_id",0);
            String lang_code = object.optString("lang_code");
            object = orderService.getOrderDetail(userId,orderId,lang_code);
        }
        out(response,object,api_version);
    }

    /**
     * 支付订单
     */
    @RequestMapping("/paymentOrder")
    public void paymentOrder(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.getInt("user_id");
            long paySn = LibSysUtils.toLong(getParameter(request,"pay_sn"));
            int payment_id = getParameter(request,"payment_id",0);
            String ip = request.getRemoteAddr();
            String lang_code = object.optString("lang_code","zh_CN");
            //double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
            object = orderService.paymentOrder(userId, paySn, payment_id,ip,lang_code,api_version);
        }
        out(response,object,api_version);
    }

    /**
     * 取消订单
     */
    @RequestMapping("/cancelOrder")
    public void cancelOrder(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.getInt("user_id");
            String orderIds = getParameter(request,"order_ids");
            object = orderService.cancelOrder(userId, orderIds);
        }
        out(response,object,api_version);
    }

    /**
     * 删除订单
     */
    @RequestMapping("/delOrder")
    public void delOrder(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.getInt("user_id");
            int orderId = getParameter(request,"order_id",0);
            object = orderService.delOrder(userId, orderId);
        }
        out(response,object,api_version);
    }

    /**
     * 确认收货
     */
    @RequestMapping("/confirmReceipt")
    public void confirmReceipt(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.getInt("user_id");
            int orderId = getParameter(request,"order_id",0);
            object = orderService.confirmReceipt(userId, orderId);
        }
        out(response,object,api_version);
    }

    /**
     * 确认付款
     */
    @RequestMapping("/confirmPay")
    public void confirmPay(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.getInt("user_id");
            String lang_code = object.getString("lang_code");
            String coin_amount = getParameter(request,"coin_amount");
            object = orderService.confirmPay(userId,lang_code, coin_amount);
        }
        out(response,object,api_version);
    }

}
