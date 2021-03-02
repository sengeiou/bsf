package com.weking.service.shop;

import com.braintreegateway.BraintreeGateway;
import com.weking.cache.WKCache;
import com.weking.core.*;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.core.gash.gash;
import com.weking.mapper.digital.DigitalWalletMapper;
import com.weking.mapper.shop.order.*;
import com.weking.model.digital.DigitalWallet;
import com.weking.model.shop.order.*;
import com.weking.service.digital.DigitalService;
import com.weking.service.user.UserService;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

@Service("orderService")
public class OrderService extends LibServiceBase {

    private static Logger log = Logger.getLogger(OrderService.class);

    @Resource
    private GoodsService goodsService;
    @Resource
    private AddressService addressService;
    @Resource
    private OrderPayMapper orderPayMapper;
    @Resource
    private PaymentMapper paymentMapper;
    @Resource
    private ShopOrderMapper shopOrderMapper;
    @Resource
    private OrderGoodsMapper orderGoodsMapper;
    @Resource
    private OrderCommonMapper orderCommonMapper;
    @Resource
    private StoreService storeService;
    @Resource
    private UserService userService;
    @Resource
    private ShopTransLogMapper shopTransLogMapper;
    @Resource
    private DigitalWalletMapper digitalWalletMapper;
    @Resource
    private DigitalService digitalService;

    /**
     * 获得用户商城订单列表
     */
    public JSONObject getShopOrderList(int userId, int type, int index, int count) {
        List<ShopOrder> list = shopOrderMapper.selectUserOrderList(userId, type, index, count);
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray jsonArray = new JSONArray();
        if (list.size() > 0) {
            JSONObject goodsObject = getOrderGoodsByOrderIds(getOrderIdsList(list));
            JSONArray orderArr = new JSONArray();
            JSONObject orderObj = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            long paySn = 0L;
            BigDecimal orderAmount = new BigDecimal("0");
            BigDecimal coinAmount = new BigDecimal("0");
            for (ShopOrder info : list) {
                orderObj.put("store_id", info.getStoreId());
                orderObj.put("store_name", info.getStoreName());
                orderObj.put("goods_list", goodsObject.optJSONArray(LibSysUtils.toString(info.getId())));
                if (info.getOrderState() == 10 && paySn == info.getPaySn()) {   //处理未付款订单合并
                    orderAmount = orderAmount.add(info.getOrderAmount());
                    coinAmount = coinAmount.add(info.getCoin_amount());
                    orderArr.add(orderObj);
                } else {
                    if (paySn != 0) {
                        jsonObject.put("order_list", orderArr);
                        jsonObject.put("order_amount", orderAmount);
                        jsonObject.put("coin_amount", coinAmount);
                        jsonArray.add(jsonObject);
                    }
                    jsonObject = new JSONObject();
                    orderArr = new JSONArray();
                    orderAmount = info.getOrderAmount();
                    coinAmount = info.getCoin_amount();
                    orderArr.add(orderObj);
                    jsonObject.put("pay_sn", info.getPaySn());
                    jsonObject.put("order_state", info.getOrderState());
                    jsonObject.put("order_id", info.getId());
                }
                paySn = info.getPaySn();
            }
            jsonObject.put("order_list", orderArr);
            jsonObject.put("order_amount", orderAmount);
            jsonObject.put("coin_amount", coinAmount);
            jsonArray.add(jsonObject);
        }
        object.put("list", jsonArray);
        return object;
    }

    private List<Integer> getOrderIdsList(List<ShopOrder> list) {
        List<Integer> orderIdsList = new ArrayList<>();
        for (ShopOrder info : list) {
            orderIdsList.add(info.getId());
        }
        return orderIdsList;
    }

    /**
     * 通过订单ID列表查询所有商品
     */
    private JSONObject getOrderGoodsByOrderIds(List<Integer> orderIds) {
        JSONObject object = new JSONObject();
        List<OrderGoods> orderGoods = orderGoodsMapper.selectGoodsListByOrderIds(orderIds);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        int orderId = 0;
        for (OrderGoods info : orderGoods) {
            jsonObject = new JSONObject();
            jsonObject.put("goods_id", info.getGoodsId());
            jsonObject.put("goods_name", info.getGoodsName());
            jsonObject.put("goods_image", WkUtil.combineUrl(info.getGoodsImage(), UploadTypeEnum.SHOP, true));
            jsonObject.put("goods_price", info.getGoodsPrice());
            jsonObject.put("goods_marketprice", info.getGoodsMarketprice());
            jsonObject.put("goods_num", info.getGoodsNum());
            jsonObject.put("goods_spec", info.getGoodsSpec());
            jsonObject.put("coin_price", info.getCoin_price());
            if (orderId != info.getOrderId()) {
                if (orderId != 0) {
                    object.put(orderId, jsonArray);
                    jsonArray = new JSONArray();
                }
            }
            jsonArray.add(jsonObject);
            orderId = info.getOrderId();
        }
        object.put(orderId, jsonArray);
        return object;
    }

    /**
     * 获取订单详情
     */
    public JSONObject getOrderDetail(int userId, int orderId, String lang_code) {
        ShopOrder orderInfo = shopOrderMapper.findByOrderId(orderId, userId);
        JSONArray orderArray = new JSONArray();
        if (orderInfo == null) {
            return LibSysUtils.getResultJSON(ResultCode.order_no_exist, LibProperties.getLanguage(lang_code, "weking.lang.app.order.no.exist"));
        }
        BigDecimal orderAmount = new BigDecimal("0");
        if (orderInfo.getOrderState() == 10) { //如果订单在未付款状态
            List<ShopOrder> list = shopOrderMapper.selectOrderByPaySn(userId, orderInfo.getPaySn());
            for (ShopOrder info : list) {
                JSONObject orderObj = orderInfo(info);
                orderAmount = orderAmount.add(info.getOrderAmount());
                orderArray.add(orderObj);
            }
        } else {
            JSONObject orderObj = orderInfo(orderInfo);
            orderAmount = orderInfo.getOrderAmount();
            orderArray.add(orderObj);
        }
        OrderCommon orderCommon = orderCommonMapper.findByOrderId(orderId);
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONObject addressInfo = addressService.getDeliveryAddressInfo(userId, orderCommon.getAddressId(), lang_code);
        object.put("pay_sn", orderInfo.getPaySn());
        object.put("address_info", addressInfo);
        object.put("order_state", orderInfo.getOrderState());
        object.put("order_list", orderArray);
        object.put("order_amount", orderAmount);
        object.put("coin_amount", orderInfo.getCoin_amount());
        System.out.println(object.toString());
        return object;
    }

    /**
     * 获取订单信息
     */
    private JSONObject orderInfo(ShopOrder orderInfo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("store_id", orderInfo.getStoreId());
        jsonObject.put("order_id", orderInfo.getId());
        jsonObject.put("store_name", orderInfo.getStoreName());
        jsonObject.put("goods_amount", orderInfo.getGoodsAmount());
        jsonObject.put("shipping_fee", orderInfo.getShippingFee());
        jsonObject.put("store_amount", orderInfo.getOrderAmount());
        jsonObject.put("order_sn", orderInfo.getOrderSn());
        jsonObject.put("order_time", orderInfo.getAddTime());
        jsonObject.put("goods_list", getOrderGoodsList(orderInfo.getId()));
        jsonObject.put("shipping_company", orderInfo.getShipping_company());
        jsonObject.put("shipping_code", orderInfo.getShippingCode());
        jsonObject.put("delivery_time", orderInfo.getDelivery_time());
        jsonObject.put("coin_amount", orderInfo.getCoin_amount());
        return jsonObject;
    }

    /**
     * 获得订单商品列表
     */
    public JSONArray getOrderGoodsList(int orderId) {
        List<OrderGoods> orderGoodsList = orderGoodsMapper.selectListByOrderId(orderId);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (OrderGoods info : orderGoodsList) {
            jsonObject = new JSONObject();
            jsonObject.put("goods_id", info.getGoodsId());
            jsonObject.put("goods_name", info.getGoodsName());
            jsonObject.put("goods_image", WkUtil.combineUrl(info.getGoodsImage(), UploadTypeEnum.SHOP, true));
            jsonObject.put("goods_price", info.getGoodsPayPrice());
            jsonObject.put("goods_marketprice", info.getGoodsMarketprice());
            jsonObject.put("goods_num", info.getGoodsNum());
            jsonObject.put("goods_spec", info.getGoodsSpec());
            jsonObject.put("coin_price", info.getCoin_price());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /**
     * 获取购物车订单确认信息
     */
    public JSONObject getOrderConfirmInfo(int userId, String goods_info, int address_id, String lang_code) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONObject addressInfo;
        if (address_id != 0) {
            addressInfo = addressService.getDeliveryAddressInfo(userId, address_id, lang_code);
        } else {
            addressInfo = addressService.getDeliveryAddressInfo(userId, lang_code);
            if (addressInfo != null) {
                address_id = addressInfo.getInt("country_id");
            }
        }
        object.put("address_info", addressInfo);
        object.put("order_info", getOrderInfo(goods_info, address_id, lang_code));
        System.out.println(object.toString());
        return object;
    }

    /**
     * 获得订单信息
     */
    public JSONObject getOrderInfo(String goods_info, int country_id, String lang_code) {
        if (LibSysUtils.isNullOrEmpty(goods_info)) {
            return null;
        }
        return goodsService.getOrderGoodsByShop(goods_info, country_id, lang_code);
    }

    /**
     * 提交订单信息
     */
    @Transactional
    public JSONObject submitOrderInfo(int userId, JSONObject orderInfo, String store_msg, JSONObject deliveryAddress, String lang_code,double api_version) {
        if (deliveryAddress == null) {
            return LibSysUtils.getResultJSON(ResultCode.delivery_address_no_exist, LibProperties.getLanguage(lang_code, "weking.lang.app.delivery.address.no.exist"));
        }
        if (orderInfo == null) {
            return LibSysUtils.getResultJSON(ResultCode.data_error, LibProperties.getLanguage(lang_code, "weking.lang.app.data.error"));
        }
        if (!orderInfo.getBoolean("is_effective")) {
            return LibSysUtils.getResultJSON(ResultCode.order_failure, LibProperties.getLanguage(lang_code, "weking.lang.app.order.failure"));
        }
        BigDecimal order_coin_amount = new BigDecimal(orderInfo.getString("order_coin_amount"));

        if (api_version >= 3.8 ){
            DigitalWallet digitalWallet = digitalWalletMapper.selectByUserIdSymbol(userId,"SCA");
            if (digitalWallet == null || digitalWallet.getWithdrawAmount().compareTo(new BigDecimal(orderInfo.getString("order_coin_amount"))) == -1){
                return LibSysUtils.getResultJSON(ResultCode.digital_wallet_insufficient, LibProperties.getLanguage(lang_code, "digital.wallet.insufficient"));
            }
        }else {
            order_coin_amount = new BigDecimal("0");
        }

        JSONObject object;
        Boolean flag;
        String goods_info = orderInfo.getString("goods_info");
        try {
            flag = goodsService.updateGoodsNumBatch(goods_info);
        } catch (Exception e) {
            return LibSysUtils.getResultJSON(ResultCode.insufficient_stock, LibProperties.getLanguage(lang_code, "weking.lang.app.insufficient.stock"));
        }
        if (flag) {
            JSONObject storeMsgObj = null;
            if (!LibSysUtils.isNullOrEmpty(store_msg)) {
                storeMsgObj = JSONObject.fromObject(store_msg);
            }
            BigDecimal storeAmount;
            BigDecimal storeFreight;
            JSONArray orderStoreInfo = orderInfo.getJSONArray("list");
            long paySn = makePaySn(userId);
            int payId = recordOrderPay(userId, paySn);
            Iterator it = orderStoreInfo.iterator();
            int num = 1;
            ShopOrder record;
            OrderCommon orderCommonRecord;
            Long time = LibDateUtils.getLibDateTime();
            String nickname = WKCache.get_user(userId, "nickname");
            int storeId;
            String store_name;
            List<OrderCommon> orderCommonList = new ArrayList<>();
            int orderId = 0;
            while (it.hasNext()) {
                JSONObject ob = (JSONObject) it.next();
                storeAmount = new BigDecimal(LibSysUtils.toString(ob.get("store_amount")));
                storeFreight = new BigDecimal(LibSysUtils.toString(ob.get("store_freight")));
                record = new ShopOrder();
                record.setAddTime(time);
                record.setBuyerId(userId);
                record.setGoodsAmount(storeAmount.subtract(storeFreight));
                record.setOrderAmount(storeAmount);
                record.setBuyerName(nickname);
                record.setOrderSn(makeOrderSn(payId, num));
                record.setPaySn(paySn);
                storeId = LibSysUtils.toInt(ob.get("store_id"));
                store_name = LibSysUtils.toString(ob.get("store_name"));
                record.setStoreId(storeId);
                record.setStoreName(store_name);
                record.setShippingFee(storeFreight);
                record.setCoin_amount(order_coin_amount);
                shopOrderMapper.insert(record);
                orderId = record.getId();
                //记录订单商品信息
                recordOrderGoods(userId, orderId, storeId, store_name, ob.getJSONArray("goods_list"));
                orderCommonRecord = new OrderCommon();
                orderCommonRecord.setId(orderId);
                if (storeMsgObj != null) {
                    orderCommonRecord.setOrderMessage(storeMsgObj.optString(ob.getString("store_id")));
                }
                orderCommonRecord.setStoreId(storeId);
                orderCommonRecord.setAddressId(deliveryAddress.getInt("address_id"));
                orderCommonList.add(orderCommonRecord);
                num++;
            }
            int re = recordOrderCommon(orderCommonList);
            if (re > 0) { //付款
                object = LibSysUtils.getResultJSON(ResultCode.success);
                object.put("pay_sn", paySn);
                object.put("order_id", orderId);
                object.put("coin_amount", order_coin_amount);
                object.put("order_amount", new BigDecimal(orderInfo.getString("order_amount")));
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.order_failure, LibProperties.getLanguage(lang_code, "weking.lang.app.order.failure"));
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.order_failure, LibProperties.getLanguage(lang_code, "weking.lang.app.order.failure"));
        }
        return object;
    }

    //微信支付
    private JSONObject buyByWx(int userId, BigDecimal payMoney, long paySn, String ip, String lang_code) {
        JSONObject object;
        String body = LibProperties.getConfig("weking.config.project.name");
        payMoney = payMoney.multiply(new BigDecimal(100));
        String result = WeixinPay.setParam(userId, body, LibSysUtils.toString(paySn), payMoney.intValue(), ip);
        Map map = ParseXMLUtils.getjdomParseXml(result); //解析xml文件
        if (map.size() > 0) {
            String appid = map.get("appid").toString();
            String partnerid = map.get("mch_id").toString();
            if (userId == 1008765) {
                appid = "wxf65e958bde077156";
                partnerid = "1500208792";
            }
            String prepayid = map.get("prepay_id").toString();
            String packages = "Sign=WXPay";
            String noncestr = LibSysUtils.getRandomString(16);
            int timestamp = LibSysUtils.toInt(System.currentTimeMillis() / 1000);
            //参数：开始生成签名
            SortedMap<Object, Object> parameters = new TreeMap<>();
            parameters.put("appid", appid);
            parameters.put("partnerid", partnerid);
            parameters.put("prepayid", prepayid);
            parameters.put("package", packages);
            parameters.put("noncestr", noncestr);
            parameters.put("timestamp", timestamp);
            String sign = WeixinPay.createSign(userId, parameters);
            object = LibSysUtils.getResultJSON(ResultCode.success);
            object.put("appid", appid);
            object.put("partnerid", partnerid);
            object.put("prepayid", prepayid);
            object.put("wx_package", packages);
            object.put("noncestr", noncestr);
            object.put("timestamp", timestamp);
            object.put("sign", sign);
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.order_failure, LibProperties.getLanguage(lang_code, "weking.lang.app.order.failure"));
        }
        return object;
    }

    /**
     * 贝宝支付
     */
    private JSONObject buyByPaypal(int userId, BigDecimal payMoney, long paySn, String ip, String lang_code) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        BraintreeGateway gateway = new BraintreeGateway("access_token$sandbox$dgk9w8kn75jfhb2p$30dea7199b1d84b10e1ba97d9f00c93a");
        Object clientToken = gateway.clientToken().generate();
        object.put("client_token", clientToken.toString());
        return object;
    }

    /**
     * GASH支付
     */
    private JSONObject buyByGash(int userId, BigDecimal payMoney, long paySn) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("data", gash.execute(userId, LibSysUtils.toString(paySn), LibSysUtils.toString(payMoney)));
        return object;
    }

    /**
     * iPay88支付
     */
    private JSONObject buyByiPay88(int userId, BigDecimal payMoney, long paySn) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("merchantCode", WKCache.get_system_cache(C.WKSystemCacheField.ipay88_merchant_code));
        object.put("merchantKey", WKCache.get_system_cache(C.WKSystemCacheField.ipay88_merchant_key));

        if (payMoney.compareTo(new BigDecimal("1")) < 0){
            object.put("amount", LibSysUtils.toString(payMoney));
        }else {
            DecimalFormat df = new DecimalFormat(",###,###,###.00");
            object.put("amount", df.format(payMoney));
        }
        object.put("currency", "MYR");
        object.put("prodDesc", "Purchase goods");
        object.put("refNo", LibSysUtils.toString(paySn));
        object.put("refID", LibSysUtils.toString(paySn));
        object.put("remark", LibSysUtils.toString(userId));
        object.put("country", "MY");
        object.put("paymentId", "");
//        Map<String,String> userMap = userService.getUserInfoByUserId(userId,"nickname","email","phone");
//        object.put("userName", LibSysUtils.isNullOrEmpty(userMap.get("nickname")) ? "Appsme" : userMap.get("nickname"));
//        object.put("userContact", LibSysUtils.isNullOrEmpty(userMap.get("phone")) ? "60123456789" : userMap.get("phone"));
//        object.put("userEmail", LibSysUtils.isNullOrEmpty(userMap.get("email")) ? "test@appsme.tv" : userMap.get("email"));
        object.put("backendPostURL", WKCache.get_system_cache(C.WKSystemCacheField.ipay88_backend_post_url));

        System.out.println(object);
        return object;
    }

    /**
     * 记录订单商品
     */
    private void recordOrderGoods(int userId, int orderId, int storeId, String storeName, JSONArray goodsList) {
        List<OrderGoods> order_goods_list = new ArrayList<>();
        Iterator it = goodsList.iterator();
        while (it.hasNext()) {
            JSONObject ob = (JSONObject) it.next();
            OrderGoods record = new OrderGoods();
            record.setBuyerId(userId);
            record.setStoreId(storeId);
            record.setStoreName(storeName);
            record.setOrderId(orderId);
            record.setGoodsPrice(new BigDecimal(ob.getString("goods_price")));
            record.setGoodsNum(LibSysUtils.toInt(ob.getString("goods_num")));
            record.setGoodsMarketprice(new BigDecimal(ob.getString("goods_marketprice")));
            record.setGoodsPayPrice(new BigDecimal(ob.getString("pay_price")));
            record.setGoodsId(ob.getInt("goods_id"));
            record.setGoodsName(ob.getString("goods_name"));
            record.setGoodsSpec(ob.getString("goods_spec"));
            record.setGoodsImage(WkUtil.getRelativeHeadPic(ob.getString("goods_image")));
            record.setCoin_price(new BigDecimal(ob.getString("coin_price")));
            order_goods_list.add(record);
        }
        orderGoodsMapper.insertBatch(order_goods_list);
    }

    /**
     * 记录订单扩展信息
     */
    private int recordOrderCommon(List<OrderCommon> orderCommonList) {
        return orderCommonMapper.insertBatch(orderCommonList);
    }

    /**
     * 生成支付单号
     */
    private long makePaySn(int userId) {
        StringBuffer str = new StringBuffer(LibSysUtils.getRandomNum(2)).append(System.currentTimeMillis())
                .append(String.format("%03d", userId % 1000));
        return LibSysUtils.toLong(str.toString());
    }

    /**
     * 生成订单号
     *
     * @param pay_id 支付表ID
     * @param num    子订单数量
     */
    private long makeOrderSn(int pay_id, int num) {
        //记录生成子订单的个数，如果生成多个子订单，该值会累加
        return LibSysUtils.toLong((LibDateUtils.getLibDateTime("y") % 9 + 1) + String.format("%013d", pay_id) + String.format("%02d", num));
    }

    /**
     * 记录支付编号
     */
    private int recordOrderPay(int userId, long paySn) {
        OrderPay record = new OrderPay();
        record.setBuyerId(userId);
        record.setPaySn(paySn);
        orderPayMapper.insert(record);
        return record.getId();
    }

    /**
     * 获得支付方式信息
     */
    public JSONObject getPaymentInfo(int payment_id) {
        Payment payment = paymentMapper.findByPrimaryKey((byte) payment_id);
        if (payment == null) {
            return null;
        }
        JSONObject object = new JSONObject();
        object.put("payment_id", payment.getId());
        object.put("payment_code", payment.getPaymentCode());
        object.put("payment_name", payment.getPaymentName());
        object.put("payment_config", payment.getPaymentConfig());
        return object;
    }

    /**
     * 支付订单
     */
    public JSONObject paymentOrder(int userId, long paySn, int paymentId, String ip, String lang_code,double api_version ) {
        List<ShopOrder> shopOrderList = shopOrderMapper.selectShopOrderListByPaySn(userId, paySn);
        if (shopOrderList == null) {
            return LibSysUtils.getResultJSON(ResultCode.pay_sn_no_exist, LibProperties.getLanguage(lang_code, "weking.lang.app.pay.sn.no.exist"));
        }
        long intervalTime = LibDateUtils.getDateTimeTick(shopOrderList.get(0).getAddTime(), LibDateUtils.getLibDateTime());
        if (intervalTime >= 86400000) {  //订单超过一天取消
            return LibSysUtils.getResultJSON(ResultCode.pay_sn_no_exist, LibProperties.getLanguage(lang_code, "weking.lang.app.pay.sn.no.exist"));
        }
        JSONObject paymentInfo = getPaymentInfo(paymentId);
        if (paymentInfo == null) {
            return LibSysUtils.getResultJSON(ResultCode.payment_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.payment.not.exist"));
        }

        BigDecimal orderTotalAmount = new BigDecimal("0");
        BigDecimal totalCoinAmount = new BigDecimal("0");
        for (ShopOrder info : shopOrderList) {
            orderTotalAmount = orderTotalAmount.add(info.getOrderAmount());
            totalCoinAmount = totalCoinAmount.add(info.getCoin_amount());
        }

        if (api_version >= 3.8){
            DigitalWallet digitalWallet = digitalWalletMapper.selectByUserIdSymbol(userId,"SCA");
            if (digitalWallet.getWithdrawAmount().compareTo(totalCoinAmount) == -1){
                return LibSysUtils.getResultJSON(ResultCode.digital_wallet_insufficient, LibProperties.getLanguage(lang_code, "digital.wallet.insufficient"));
            }
        }

        JSONObject object = payment(userId, orderTotalAmount, paySn, ip, paymentInfo, lang_code);
        if (object.getInt("code") == ResultCode.success) {
            //支付方式
            shopOrderMapper.updateShopOrderPaymentCodeByPaySn(paymentInfo.getString("payment_code"), paySn);
        }
        return object;
    }

    /**
     * 支付
     */
    private JSONObject payment(int userId, BigDecimal payMoney, long paySn, String ip, JSONObject paymentInfo, String lang_code) {
        JSONObject object;
        switch (paymentInfo.getInt("payment_id")) {
            case C.RechargeType.wx: //微信支付
                object = buyByWx(userId, payMoney, paySn, ip, lang_code);
                break;
            case C.RechargeType.alipay://支付宝
                object = LibSysUtils.getResultJSON(ResultCode.payment_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.payment.not.exist"));
                break;
            case 2: //贝宝
                object = LibSysUtils.getResultJSON(ResultCode.payment_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.payment.not.exist"));
                break;
            case C.RechargeType.gashpay: //GASH支付
                object = buyByGash(userId, payMoney, paySn);
                break;
            case C.RechargeType.ipay88: //iPay88
                double rate = LibSysUtils.toDouble(WKCache.get_system_cache(C.WKSystemCacheField.cny_to_myr_rate));
                payMoney = payMoney.multiply(new BigDecimal(rate)).setScale(2, BigDecimal.ROUND_HALF_UP);
                object = buyByiPay88(userId, payMoney, paySn);
                break;
            default:
                object = LibSysUtils.getResultJSON(ResultCode.payment_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.payment.not.exist"));
                break;
        }
        return object;
    }

    /**
     * 取消订单
     */
    public JSONObject cancelOrder(int userId, String orderIds) {
        if (!LibSysUtils.isNullOrEmpty(orderIds)) {
            String[] orderArr = orderIds.split(",");
            int re = shopOrderMapper.cancelOrderByOrderIds(orderArr, userId);
            if (re > 0) {
                returnOrderGoodsStorage(orderArr);
            }
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    public Map<String, String> getNotPayOrderMap() {
        Map<String, String> map = OrderInfo.notPayOrderMap;
        if (map.size() == 0) {
            Boolean flag = true;
            if (OrderInfo.LastOrderTime != 0) {
                long intervalTime = LibDateUtils.getDateTimeTick(OrderInfo.LastOrderTime, LibDateUtils.getLibDateTime());
                flag = intervalTime >= OrderInfo.intervalTime * 60 * 1000;
            }
            if (flag) {
                long time = LibSysUtils.toLong(WkUtil.futureTime(OrderInfo.intervalTime - 24 * 60));
                JSONObject orderInfo;
                List<ShopOrder> list = shopOrderMapper.selectNotPayOrderByList(time);
                for (ShopOrder info : list) {
                    orderInfo = new JSONObject();
                    orderInfo.put("order_id", info.getId());
                    orderInfo.put("user_id", info.getBuyerId());
                    orderInfo.put("add_time", info.getAddTime());
                    map.put(LibSysUtils.toString(info.getId()), orderInfo.toString());
                }
                OrderInfo.notPayOrderMap = map;
                OrderInfo.LastOrderTime = LibDateUtils.getLibDateTime();
            }
        }
        return map;
    }

    /**
     * 返还订单商品库存
     */
    public int returnOrderGoodsStorage(String[] orderArr) {
        List<OrderGoods> list = orderGoodsMapper.selectOrderGoodsListByOrderIds(orderArr);
        Map<Integer, Integer> goodsMap = new HashMap<>();
        for (OrderGoods info : list) {
            int goodsParentId = LibSysUtils.toInt(goodsService.getGoodsFieldInfo(info.getGoodsId(), "goods_parentid"));
            if (goodsParentId > 0) {
                goodsMap.put(goodsParentId, info.getGoodsNum());
            } else {
                goodsMap.put(info.getGoodsId(), info.getGoodsNum());
            }
        }
        return goodsService.updateIncGoodsStorageBatch(goodsMap);
    }

    /**
     * 确认收货
     */
    @Transactional
    public JSONObject confirmReceipt(int userId, int orderId) {
        int re = shopOrderMapper.confirmReceiptByOrderId(orderId, userId, LibDateUtils.getLibDateTime());
        if (re > 0) {
            updateOrderGoodsSaleNum(orderId);
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 更新订单商品销售数量
     */
    private int updateOrderGoodsSaleNum(int orderId) {
        List<OrderGoods> list = orderGoodsMapper.selectListByOrderId(orderId);
        Map<Integer, Integer> goodsMap = new HashMap<>();
        for (OrderGoods info : list) {
            goodsMap.put(info.getGoodsId(), info.getGoodsNum());
        }
        int re = goodsService.updateGoodsSaleNumBatch(goodsMap);
        if (re > 0) {
            storeService.updateStoreSalesByStoreId(list.get(0).getStoreId(), list.get(0).getGoodsNum());
        }
        return re;
    }

    /**
     * 删除订单
     */
    public JSONObject delOrder(int userId, int orderId) {
        shopOrderMapper.delOrderByOrderId(orderId, userId);
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 更新商城订单信息
     */
    @Transactional
    public int updateShopOrder(long paySn,String transId,int payType) {
        int re = orderPayMapper.updatePayState(paySn);
        if (re > 0) {
            re = shopOrderMapper.updateShopOrderStateByPaySn(LibDateUtils.getLibDateTime(), paySn);
            ShopTransLog shopTransLog = new ShopTransLog();
            shopTransLog.setOrderSn(LibSysUtils.toString(paySn));
            shopTransLog.setTransId(transId);
            shopTransLog.setPayType(payType);
            shopTransLog.setAddTime(LibDateUtils.getLibDateTime());
            shopTransLogMapper.insert(shopTransLog);

            // TODO 扣除用户虚拟币
            ShopOrder shopOrder = shopOrderMapper.selectShopOrderByPaySn(paySn);
            if (shopOrder != null && shopOrder.getCoin_amount().compareTo(new BigDecimal("0")) == 1){
                digitalService.OptWallect(shopOrder.getBuyerId(),
                        userService.getUserInfoByUserId(shopOrder.getBuyerId(),"lang_code"),
                        shopOrder.getId(),"SCA",shopOrder.getCoin_amount(),(short)40,
                        LibSysUtils.getRandomNum(16), "购买商品花费搜秀链", "", 3.8);
            }

        }
        return re;
    }

    /**
     * 移除购物车订单商品
     */
    public void removeOrderGoodsInCart(int userId, String goods_info) {
        JSONObject goodsObject = JSONObject.fromObject(goods_info);
        String[] goodsIdsArr = new String[goodsObject.size()];
        Iterator it = goodsObject.keys();
        int i = 0;
        while (it.hasNext()) {
            goodsIdsArr[i] = LibSysUtils.toString(it.next());
            i++;
        }
        goodsService.deleteByUserIdAndGoodsId(userId, goodsIdsArr);
    }


    /**
     * 确认付款
     * @param userId
     * @param coin_amount
     * @return
     */
    public JSONObject confirmPay(int userId,String lang_code, String coin_amount) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        DigitalWallet digitalWallet = digitalWalletMapper.selectByUserIdSymbol(userId,"SCA");
        if (!LibSysUtils.isNullOrEmpty(coin_amount) &&
                new BigDecimal(coin_amount).compareTo(new BigDecimal("0")) != 0 &&
                digitalWallet.getWithdrawAmount().compareTo(new BigDecimal(coin_amount)) == -1){
            return LibSysUtils.getResultJSON(ResultCode.digital_wallet_insufficient, LibProperties.getLanguage(lang_code, "digital.wallet.insufficient"));
        }
        return result;
    }


}
