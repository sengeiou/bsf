package com.weking.service.pay;

import com.weking.cache.UserCacheInfo;
import com.weking.cache.WKCache;
import com.weking.core.*;
import com.weking.core.gash.gash;
import com.weking.core.newebpay.EzPay;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.digital.DigitalCurrencyMapper;
import com.weking.mapper.pocket.*;
import com.weking.mapper.shop.order.ShopOrderMapper;
import com.weking.mapper.shop.order.ShopTransLogMapper;
import com.weking.model.pocket.*;
import com.weking.model.shop.order.ShopOrder;
import com.weking.service.shop.OrderService;
import com.weking.service.user.TaskService;
import com.weking.service.user.UserService;
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
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;


@Service("payService")
@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class PayService {

    private static Logger log = Logger.getLogger(PayService.class);
    private static final int TIMEOUT = 2 * 1000;

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private RechargeListMapper rechargeListMapper;
    @Resource
    private PocketInfoMapper pocketInfoMapper;
    @Resource
    private AppleReceiptMapper appleReceiptMapper;
    @Resource
    private OrderService orderService;
    @Resource
    private ShopTransLogMapper shopTransLogMapper;
    @Resource
    private ShopOrderMapper shopOrderMapper;
    @Resource
    private PlatformIncomeMapper platformIncomeMapper;
    @Resource
    private DigitalCurrencyMapper digitalCurrencyMapper;
    @Resource
    private UserService userService;
    @Resource
    private AccountInfoMapper accountInfoMapper;
    @Resource
    private UserGainMapper userGainMapper;
    @Resource
    private TaskService taskService;

    /**
     * 校验Google签名
     *
     * @param signture     签名字符串
     * @param signtureData 签名数据
     */
    public JSONObject checkGoogleSignture(int userId, String signture, String signtureData, String project_name) {
        JSONObject object;
        String publicKey = WKCache.get_system_cache("weking.config." + project_name + "googlepay.key");
        log.info("google_pay: ---signture:" + signture + "---signtureData:" + signtureData + "---key:" + publicKey);
        boolean flag = RSASignature.doCheck(signtureData, signture, publicKey);
        log.info("google_pay flag:" + LibSysUtils.toString(flag));
        UserCacheInfo userCacheInfo = WKCache.get_user(userId);
        if (flag) {
            JSONObject jsonObject = JSONObject.fromObject(signtureData);
            String tradeNo = jsonObject.optString("orderId", "");
            String orderSn = jsonObject.optString("developerPayload", "");
            Order order = orderMapper.selectByOrderSn(userId, orderSn);
            log.info("google签名验证：tradeNo：" + tradeNo + "--orderSn:" + orderSn);
            if (order != null && order.getState() == 1) {
                String productId = jsonObject.optString("productId", "");
                RechargeList rechargeInfo = rechargeListMapper.findByThirdId(productId, 3);
                int re = updateOrder(userId, order.getId(), tradeNo, rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum(),orderSn);
                if (re > 0) {
                    PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(userId);
                    object = LibSysUtils.getResultJSON(ResultCode.success);
                    object.put("total_diamond", pocketInfo.getTotalDiamond());
                    recordPlatformIncome( 1, rechargeInfo.getPayMoney(),"");
                } else {
                    object = LibSysUtils.getResultJSON(ResultCode.order_not_exist, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.payment.error"));
                }
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.order_not_exist, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.payment.error"));
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.order_not_exist, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.payment.error"));
        }
        log.info("google3：object：" + object.toString());
        return object;
    }

    /**
     * 验证苹果支付
     * 苹果反馈的状态码；
     * <p>
     * 21000 App Store无法读取你提供的JSON数据
     * 21002 收据数据不符合格式
     * 21003 收据无法被验证
     * 21004 你提供的共享密钥和账户的共享密钥不一致
     * 21005 收据服务器当前不可用
     * 21006 收据是有效的，但订阅服务已经过期。当收到这个信息时，解码后的收据信息也包含在返回内容中
     * 21007 收据信息是测试用（sandbox），但却被发送到产品环境中验证
     * 21008 收据信息是产品环境中使用，但却被发送到测试环境中验证
     */
    public JSONObject checkApplePay(int userId, String orderSn, String apple_receipt) {
        JSONObject object;
        log.info("苹果支付orderSn:" + orderSn + "---userId:" + LibSysUtils.toString(userId) + "---apple_receipt:" + apple_receipt);
        UserCacheInfo userCacheInfo = WKCache.get_user(userId);
       /* // 开始加锁
        log.error("开始加锁..." + userId);
        String key = "applePay_" + userId;
        String requestId = UUID.randomUUID().toString();
        if (!LibRedis.tryGetDistributedLock(key, requestId,TIMEOUT)) {
            log.error("订单处理中..." + userId);
            object = LibSysUtils.getResultJSON(ResultCode.system_error);
            return object ;
        }
        log.error("加锁后..." + userId);*/
        String langCode = userCacheInfo.getLang_code();
        Order order = orderMapper.selectByOrderSn(userId, orderSn);
        if (order != null && order.getState() == 1) { //是否存在该订单号并且为未付款状态
            if (appleReceiptMapper.selectCountByReceipt(apple_receipt) == 0) {
                log.error("验证中..." + userId);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("receipt-data", apple_receipt);
                String url = "https://buy.itunes.apple.com/verifyReceipt";
                String post = HttpXmlUtils.httpsRequest(url, "POST", jsonObject.toString());
                log.info("orderSn:" + orderSn + "---苹果支付post:" + post);
                JSONObject cp = JSONObject.fromObject(post);
                int status = cp.getInt("status");
                System.out.println("苹果支付状态：" + status);
                if (status == 21007) {//如果正式验证不过则验证沙盒
                    url = "https://sandbox.itunes.apple.com/verifyReceipt";
                    post = HttpXmlUtils.httpsRequest(url, "POST", jsonObject.toString());
                    log.info("苹果支付沙盒post:" + post);
                    cp = JSONObject.fromObject(post);
                    status = cp.getInt("status");
                }
                if (status == 0) {//如果支付成功
                    String receipt = cp.getString("receipt");//解析苹果返回的数据
                    JSONObject receiptObj = JSONObject.fromObject(receipt);
                    JSONArray jsonArray = receiptObj.optJSONArray("in_app");
                    String tradeNo = "";
                    boolean success = false;
                    RechargeList rechargeInfo = null;
                    if (jsonArray != null && jsonArray.size() > 0) {
                        Iterator<Object> it = jsonArray.iterator();
                        while (it.hasNext()) {
                            JSONObject ob = (JSONObject) it.next();
                            String productId = ob.optString("product_id", "");
                            rechargeInfo = rechargeListMapper.findByThirdId(productId, 2);
                            if (rechargeInfo != null) {
                                if (rechargeInfo.getId() == order.getRechargeId()) {
                                    success = true;
                                    tradeNo = ob.optString("transaction_id", "");
                                } else {
                                    Order orderInfo = orderMapper.findByRechargeId(userId, rechargeInfo.getId());
                                    updateApplePayOrder(userId, orderInfo.getId(), ob.optString("transaction_id", ""), orderInfo.getBuyNum(),orderSn);
                                }
                            } else {
                                log.error("orderSn:" + orderSn + "---userId:" + LibSysUtils.toString(userId) + "---productId:" + productId);
                            }
                        }
                    }
                    if (success) {
                        int re = updateApplePayOrder(userId, order.getId(), tradeNo, order.getBuyNum(),orderSn);
                        if (re > 0) {
                            recordAppleReceipt(orderSn, apple_receipt);
                            // 记录平台收入
                            if (rechargeInfo != null) {
                                recordPlatformIncome(1, rechargeInfo.getPayMoney(),"");
                            }
                            object = LibSysUtils.getResultJSON(ResultCode.success);
                        } else {
                            log.info("苹果支付错误：userid:" + userId + "--order_sn:" + orderSn + "--trade_no:" + tradeNo);
                            object = LibSysUtils.getResultJSON(ResultCode.order_not_exist, LibProperties.getLanguage(langCode, "weking.lang.payment.error"));
                        }
                    } else {
                        object = LibSysUtils.getResultJSON(ResultCode.apple_pay_error, LibProperties.getLanguage(langCode, "weking.lang.payment.error"));
                    }
                } else {
                    object = LibSysUtils.getResultJSON(ResultCode.order_not_exist, LibProperties.getLanguage(langCode, "weking.lang.payment.error"));
                }
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.success);
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.success);
        }
      /*  // 开始解锁
        LibRedis.releaseDistributedLock(key,requestId);
        log.error("解锁后..." + object);*/
      log.info("=========="+object.toString());
        return object;
    }

    // 记录平台收入
    public void recordPlatformIncome(int type, Double payMoney,String way) {
        if (type == 1){
            if(!way.equalsIgnoreCase("wx")) {
                // 充值
                double cny_to_usd_rate = digitalCurrencyMapper.selectByCurrency("CNY");
                payMoney = payMoney * cny_to_usd_rate;
            }else {
                payMoney = payMoney * 1.0;
            }
        }
        long day_date = LibDateUtils.getLibDateTime("yyyyMMdd");
        PlatformIncome platformIncome = platformIncomeMapper.findTodayPlatformIncome(day_date, type);
        if (platformIncome == null) {
            platformIncome = new PlatformIncome();
            platformIncome.setType((byte) type);
            platformIncome.setDayDate(day_date);
            platformIncome.setAmount(payMoney);
            platformIncomeMapper.insertSelective(platformIncome);
        } else {
            platformIncome.setAmount(platformIncome.getAmount() + payMoney);
            platformIncomeMapper.updateByPrimaryKeySelective(platformIncome);
        }
    }

    //验证乐点gash支付
    public String checkGashPay(String data) {
        String result = "";
        JSONObject dataObj = gash.checkData(data);
        if (dataObj.getBoolean("is_success")) {
            String orderSn = dataObj.getString("order_sn");
            log.info("order_sn" + orderSn + "gash_back:" + data);
            int userId = dataObj.getInt("user_id");
            Order order = orderMapper.selectByOrderSn(userId, orderSn);
            String tradeNo = dataObj.getString("trade_no");
            if (order != null && order.getState() == 1) { //是否存在该订单号并且为未付款状态
                RechargeList rechargeInfo = rechargeListMapper.findByRechargeId(order.getRechargeId());
                int re = updateOrder(userId, order.getId(), tradeNo, rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum(),orderSn);
                if (re > 0) {
                    WKCache.add_gash_settle_info(orderSn, dataObj.toString());
                    //gash.settle(userId,orderSn,dataObj.getString("amount"));
                    //gash.checkOrder(orderSn,dataObj.getString("amount"));
                    result = tradeNo + "|" + dataObj.getString("pay_status");
//                    recordPlatformIncome(LibDateUtils.getLibDateTime("yyyyMMdd"), 1, rechargeInfo.getPayMoney());
                }
            } else {
                result = tradeNo + "|" + dataObj.getString("pay_status");
            }
        }
        return result;
    }

    //请款乐点gash支付
    public void settleGashPay(String data) {
        JSONObject dataObj = gash.settleData(data);
        if (dataObj.getBoolean("is_success")) {
            String orderSn = dataObj.getString("order_sn");
            orderMapper.updateOrderSettle(orderSn);
        }
    }

    //验证微信支付
    public String checkWxPay(SortedMap parameters) {
        String result = "";
        log.info("微信回调参数：" + parameters.toString());
        if (parameters.get("result_code").equals("SUCCESS")) {
            String wxSign = parameters.get("sign").toString();
            parameters.remove("sign");
            String mySign = WeixinPay.createSign(0, parameters);
            if (wxSign.equals(mySign)) { //验签成功
                int re = 0;
                int userId = LibSysUtils.toInt(parameters.get("attach"));
                String orderSn = parameters.get("out_trade_no").toString();
                //log.info("orderSn:"+orderSn+"===="+orderSn.length());
                String tradeNo = parameters.get("transaction_id").toString();
                if (orderSn.length() == 17) { //购买钻石订单号为十七位
                    Order order = orderMapper.selectByOrderSn(userId, orderSn);
                    if (order != null && order.getState() == 1) { //是否存在该订单号并且为未付款状态
                        RechargeList rechargeInfo = rechargeListMapper.findByRechargeId(order.getRechargeId());
                        re = updateOrder(userId, order.getId(), tradeNo, rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum(),orderSn);
                        recordPlatformIncome(1, rechargeInfo.getPayMoney(),"wx");
                    }
                } else { //处理商品订单
                    re = orderService.updateShopOrder(LibSysUtils.toLong(orderSn), tradeNo, C.RechargeType.wx);
                    Double amount = shopOrderMapper.findOrderAmountByPaySn(LibSysUtils.toLong(orderSn));
                    if (amount > 0) {
                        recordPlatformIncome( 2, amount,"wx");
                    }
                }
                if (re > 0) {
                    result = checkWxPaySuccess();
                } else {
                    result = checkWxPayError();
                }
            }
        } else {
            result = checkWxPayError();
        }
        return result;
    }

    /**
     * 微信验证成功输出数据
     */
    public String checkWxPaySuccess() {
        return "<xml>" +
                "<return_code><![CDATA[SUCCESS]]></return_code>" +
                "<return_msg><![CDATA[OK]]></return_msg>" +
                "</xml> ";
    }

    /**
     * 验证微信失败输出数据
     */
    public String checkWxPayError() {
        return "<xml>" +
                "<return_code><![CDATA[FAIL]]></return_code>" +
                "<return_msg><![CDATA[OK]]></return_msg>" +
                "</xml> ";
    }

    @Transactional
    private int updateOrder(int userId, long orderId, String tradeNo, int buyNum,String orderSn){
        int re = orderMapper.updateByOrderId(orderId, tradeNo, buyNum);
        Order order = orderMapper.selectByOrderSn(userId, orderSn);
        JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));
        double rmb2twd = coin_proportion.optDouble("rmb2twd");
        double usd2twd = coin_proportion.optDouble("usd2twd");
        double coinRate = coin_proportion.optDouble("coinRate");
        String currency = order.getCurrency();//币别
        Double amount = order.getAmount();//价格
        Integer totalDiamond=0;
        BigDecimal newRatio;
        PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(userId);
        if(pocketInfo!=null) {
            totalDiamond = pocketInfo.getTotalDiamond();//现有的emo
        }
        //现有的比值
        Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(userId, "ratio"));

        if (re > 0) {
            //如果是人民币
            if("RMB".equalsIgnoreCase(currency)){
                if(ratio==0){
                    double temp = (buyNum + totalDiamond) / ( amount /rmb2twd*coinRate);
                    newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                }else {
                    double temp = (buyNum + totalDiamond) / ((totalDiamond / ratio) + amount / rmb2twd * coinRate);
                    newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                }
                orderMapper.updateOrderRatio(userId,newRatio);
                accountInfoMapper.updateRatioByUserid(newRatio,userId);
            }else if("USD".equalsIgnoreCase(currency)){
                if(ratio==0){
                    double temp = (buyNum + totalDiamond) / ( amount / usd2twd*coinRate);
                    newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                }else {
                    double temp = (buyNum + totalDiamond) / ((totalDiamond/ratio) + amount / usd2twd*coinRate);
                    newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                }
                orderMapper.updateOrderRatio(orderId,newRatio);
                accountInfoMapper.updateRatioByUserid(newRatio,userId);
                //WKCache.add_user(LibSysUtils.toInt(userId), "ratio", LibSysUtils.toString(newRatio));
            }
            //增加用户货币
            //谷歌充值存入新的表单中
            UserGain gain = UserGain.getGain(userId, C.UserGainType.google_pay, buyNum, (int)orderId);
            userGainMapper.insertSelective(gain);

            //加钱时候  充值金额存入缓存 VIP
            addCache(order.getBuyNum(),order.getUserId());

            return pocketInfoMapper.increaseDiamondByUserId(userId, buyNum);
        }
        return re;
    }

    /**
     * 更新苹果订单
     */
    @Transactional
    private int updateApplePayOrder(int userId, long orderId, String tradeNo, int buyNum,String orderSn) {
        int re = 0;
        JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));
        double rmb2twd = coin_proportion.optDouble("rmb2twd");
        double usd2twd = coin_proportion.optDouble("usd2twd");
        double coinRate = coin_proportion.optDouble("coinRate");

        Integer totalDiamond=0;
        BigDecimal newRatio;
        PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(userId);
        if(pocketInfo!=null) {
            totalDiamond = pocketInfo.getTotalDiamond();//现有的emo
        }
        //现有的比值
        Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(userId, "ratio"));
        int count = orderMapper.selectCountByTradeNo(tradeNo);
        if (count == 0) {
            re = orderMapper.updateByOrderIdAndApplePay(orderId, tradeNo, buyNum);
            if (re > 0) {
                Order order = orderMapper.selectByPrimaryKey(orderId);
                String currency = order.getCurrency();//币别
                Double amount = order.getAmount();//价格
                log.info("比值排查======"+userId+"金额==="+amount);
                //增加用户货币
                //如果是人民币
                if("RMB".equalsIgnoreCase(currency)){
                    if(ratio==0) {
                        double temp = (buyNum + totalDiamond) / ( amount / rmb2twd * coinRate);
                        newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                    }else {
                        double temp = (buyNum + totalDiamond) / ((totalDiamond / ratio) + amount /rmb2twd * coinRate);
                        newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                    }
                    accountInfoMapper.updateRatioByUserid(newRatio,userId);
                }else if("USD".equalsIgnoreCase(currency)){
                    if(ratio==0){
                        double temp = (buyNum + totalDiamond) / ( amount /usd2twd*coinRate);
                        newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                    }else {
                        double temp = (buyNum + totalDiamond) / ((totalDiamond / ratio) + amount / usd2twd * coinRate);
                        newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                    }
                    orderMapper.updateOrderRatio(orderId,newRatio);
                    accountInfoMapper.updateRatioByUserid(newRatio,userId);
                    //WKCache.add_user(LibSysUtils.toInt(userId), "ratio", LibSysUtils.toString(newRatio));
                }

                //收入存入新的表单中
                UserGain gain = UserGain.getGain(userId, C.UserGainType.apple_pay, buyNum, (int)orderId);
                userGainMapper.insertSelective(gain);
                //加钱时候  充值金额存入缓存 VIP
                addCache(order.getBuyNum(),order.getUserId());

                return pocketInfoMapper.increaseDiamondByUserId(userId, buyNum);
            }
        }
        return re;
    }

    //记录苹果支付凭证
    private int recordAppleReceipt(String orderSn, String receipt) {
        AppleReceipt record = new AppleReceipt();
        record.setOrderSn(orderSn);
        record.setReceipt(receipt);
        record.setAddTime(LibDateUtils.getLibDateTime());
        return appleReceiptMapper.insert(record);
    }

    /**
     * iPay88支付回调  验证iPay88支付
     *
     * @param merchantCode
     * @param paymentId
     * @param refNo
     * @param amount
     * @param currency
     * @param remark
     * @param transId
     * @param authCode
     * @param status
     * @param errDesc
     * @param signature
     * @return
     */
    public String checkiPay88(String merchantCode, String paymentId, String refNo, String amount, String currency, String remark,
                              String transId, String authCode, String status, String errDesc, String signature) {
        String result = "";
        if (LibSysUtils.toInt(status) != 1) {
            return "Failed " + status + " errDesc:" + errDesc;
        }
        String rightSign = createIPay88Sign(merchantCode, paymentId, refNo, amount, currency, status);
        String check_signature = new String(org.apache.commons.codec.binary.Base64.decodeBase64(signature.getBytes()));
//        log.error("rightSign : "+rightSign);
//        log.error("signature : "+check_signature);
        if (!rightSign.equals(check_signature)) {
            return "Signature error";
        }
        if (refNo.length() == 17) { //购买钻石订单号为十七位
            Order order = orderMapper.selectByOrderSn(LibSysUtils.toInt(remark), refNo);
            if (order == null) {
                return "Payment failed";
            }
            if (order.getState() != 1) {
                return "Payment canceled or finished";
            }
            // TODO 加钻石操作
        } else { //处理商品订单
            if (shopTransLogMapper.selectCountByTransId(transId) == 0) {
                List<ShopOrder> shopOrderList = shopOrderMapper.findListByPaySn(LibSysUtils.toLong(refNo));
                boolean orderState = true;
                double money = 0;
                if (shopOrderList.size() > 0) {
                    for (ShopOrder temp : shopOrderList) {
                        if (temp.getOrderState() != 10 || temp.getDeleteState() != 0) {
                            orderState = false;
                            break;
                        }
                        money += temp.getOrderAmount().doubleValue();
                    }
                    if (orderState) {
                        log.error("订单完成...处理订单");
                        orderService.updateShopOrder(LibSysUtils.toLong(refNo), transId, C.RechargeType.ipay88);
                        recordPlatformIncome(2,money,"");
                        return "RECEIVEOK";
                    } else {
                        return "Order error";
                    }
                } else {
                    return "Order error";
                }
            } else {
                return "Trans finished";
            }
        }
        return result;
    }

    private String createIPay88Sign(String merchantCode, String paymentId, String refNo, String amount, String currency, String status) {
        String merchantKey = WKCache.get_system_cache(C.WKSystemCacheField.ipay88_merchant_key);
        amount = amount.replace(",", "").replace(".", "");
        String dataStr = merchantKey + merchantCode + paymentId + refNo + amount + currency + status;
        String hashStr = WkUtil.hash(dataStr, "SHA1");
//        log.error(hashStr);
        return WkUtil.hexStr2Str(hashStr);

    }

    public static void main(String[] args) {
       JSONObject jsonObject = new JSONObject();
        jsonObject.put("receipt-data", "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");
        String url = "https://buy.itunes.apple.com/verifyReceipt";
        String post = HttpXmlUtils.httpsRequest(url, "POST", jsonObject.toString());
        System.out.println(post);
        JSONObject cp = JSONObject.fromObject(post);
        int status = cp.getInt("status");
        if (status == 21007) {//如果正式验证不过则验证沙盒
            url = "https://sandbox.itunes.apple.com/verifyReceipt";
            post = HttpXmlUtils.httpsRequest(url, "POST", jsonObject.toString());
            log.info("苹果支付沙盒post:" + post);
            cp = JSONObject.fromObject(post);
            status = cp.getInt("status");
            System.out.println(post);
        }
//        log.info( "---苹果支付post:" + post);

    }

    //创蓝 支付成功后  加钱 更新订单
    @Transactional
    public void updateMerchantOrderNo(String merchantOrderNo, String tradeNo) {
        JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));

        double lanXin = coin_proportion.optDouble("lanxin",1);
        double newb_pay = coin_proportion.optDouble("newbpay",0.85);
        Order order = orderMapper.selectByOrderSnOne(merchantOrderNo);
        if(order!=null){
            if(order.getState()!=3) {
                log.error("订单状态============"+order.getState());
                //现有的比值
                Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(order.getUserId(), "ratio"));
                Double amount = order.getAmount();//价格
                Integer totalDiamond = 0;
                BigDecimal newRatio=new BigDecimal(0);
                PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(order.getUserId());
                if (pocketInfo != null) {
                    totalDiamond = pocketInfo.getTotalDiamond();//现有的emo
                }

                if (ratio == 0) {
                    if(order.getPaymentCode()==11) {
                        double temp = (order.getBuyNum() + totalDiamond) / (amount * lanXin);
                        newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                    }else if(order.getPaymentCode()==12){
                        double temp = (order.getBuyNum() + totalDiamond) / (amount * newb_pay);
                        newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                    } else if(order.getPaymentCode()==16){
                        double temp = (order.getBuyNum() + totalDiamond) / (amount * newb_pay);
                        newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                    }
                } else {
                    if(order.getPaymentCode()==11) {
                        double temp = (order.getBuyNum() + totalDiamond) / ((totalDiamond / ratio) + (amount * lanXin));
                        newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                    }else if(order.getPaymentCode()==12){
                        double temp = (order.getBuyNum() + totalDiamond) / ((totalDiamond / ratio) + (amount * newb_pay));
                        newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                    }else if(order.getPaymentCode()==16){
                        double temp = (order.getBuyNum() + totalDiamond) / ((totalDiamond / ratio) + (amount * newb_pay));
                        newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);
                    }
                }
                int i = orderMapper.updateByOrderId(order.getId(), tradeNo, order.getBuyNum());//更新订单
                if(i>0) {
                    accountInfoMapper.updateRatioByUserid(newRatio, order.getUserId());
                    pocketInfoMapper.increaseDiamondByUserId(order.getUserId(), order.getBuyNum());//给用户加钱
                    orderMapper.updateOrderRatio(order.getId(), newRatio);//更新账单的比值

                    //加钱时候  充值EMO存入缓存 VIP
                    addCache(order.getBuyNum(),order.getUserId());

                    //收入存入新的表单中
                    UserGain gain = UserGain.getGain(order.getUserId(),C.UserGainType.neweb_pay, order.getBuyNum(), LibSysUtils.toInt(order.getId()));
                    userGainMapper.insertSelective(gain);
                }

                //开发票
                if(order.getPaymentCode()==11) {
                    if (LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.pay_ezpay_switch), false) && !"消费者".equals(order.getUser_name())) {
                        log.info("开始调用开发票接口==========");
                        EzPay.getPostData(merchantOrderNo, tradeNo, order.getAmount().intValue(), order.getUser_name(), order.getEmail());
                    }
                }


            }
        }

    }


    //paynow 支付成功后  加钱 更新订单
    @Transactional
    public void updatePayNowOrder(String merchantOrderNo, String tradeNo,String card_foreign,String totalPrice) {
        JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));

        double payNow = coin_proportion.optDouble("payNow",0.98);
        Order order = orderMapper.selectByOrderSnOne(merchantOrderNo);
        if(order!=null){
            if(order.getState()!=3) {
                log.error("订单状态============"+order.getState());
                Integer buyNum = order.getBuyNum();
                if(card_foreign!=null) {
                    log.error("信用卡支付 特殊回调参数："+card_foreign+"==订单号："+tradeNo);
                    if ("1".equals(card_foreign)) {
                        RechargeList rechargeList = rechargeListMapper.selectByTypeAndMoney(LibSysUtils.toDouble(totalPrice), 14);
                        if (rechargeList != null) {
                            buyNum = rechargeList.getBuyNum();
                            payNow=coin_proportion.optDouble("payNowNew",0.609);
                        }
                    }else if("0".equals(card_foreign)){
                        RechargeList rechargeList = rechargeListMapper.selectByTypeAndMoney(LibSysUtils.toDouble(totalPrice), 13);
                        if (rechargeList != null) {
                            buyNum = rechargeList.getBuyNum();
                        }
                    }
                }
                //现有的比值
                Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(order.getUserId(), "ratio"));
                Double amount = order.getAmount();//价格
                Integer totalDiamond = 0;
                BigDecimal newRatio;
                PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(order.getUserId());
                if (pocketInfo != null) {
                    totalDiamond = pocketInfo.getTotalDiamond();//现有的emo
                }

                if (ratio == 0) {
                    double temp = (buyNum + totalDiamond) / (amount * payNow);
                    newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);

                } else {
                    double temp = (buyNum + totalDiamond) / ((totalDiamond / ratio) + (amount * payNow));
                    newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);

                }
                int i = orderMapper.updateByOrderId(order.getId(), tradeNo, buyNum);//更新订单
                if(i>0) {
                    accountInfoMapper.updateRatioByUserid(newRatio, order.getUserId());
                    pocketInfoMapper.increaseDiamondByUserId(order.getUserId(), buyNum);//给用户加钱
                    orderMapper.updateOrderRatio(order.getId(), newRatio);//更新账单的比值
                    //收入存入新的表单中
                    UserGain gain = UserGain.getGain(order.getUserId(),C.UserGainType.paynow_pay, order.getBuyNum(), LibSysUtils.toInt(order.getId()));
                    userGainMapper.insertSelective(gain);

                    //加钱时候  充值EMO存入缓存 VIP
                    addCache(order.getBuyNum(),order.getUserId());

                }

              /*  //开发票
                if(order.getPaymentCode()==11) {
                    if (LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.pay_ezpay_switch), false) && !"消费者".equals(order.getUser_name())) {
                        log.info("开始调用开发票接口==========");
                        EzPay.getPostData(merchantOrderNo, tradeNo, order.getAmount().intValue(), order.getUser_name(), order.getEmail());
                    }
                }*/


            }
        }

    }



    //paynow 支付成功后  加钱 更新订单
    @Transactional
    public void updateYiPayOrder(String merchantOrderNo, String tradeNo) {
        JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));

        double payNow = coin_proportion.optDouble("yiPay",0.95);
        Order order = orderMapper.selectByOrderSnOne(merchantOrderNo);
        if(order!=null){
            if(order.getState()!=3) {
                log.error("订单状态============"+order.getState());
                Integer buyNum = order.getBuyNum();

                //现有的比值
                Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(order.getUserId(), "ratio"));
                Double amount = order.getAmount();//价格
                Integer totalDiamond = 0;
                BigDecimal newRatio;
                PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(order.getUserId());
                if (pocketInfo != null) {
                    totalDiamond = pocketInfo.getTotalDiamond();//现有的emo
                }

                if (ratio == 0) {
                    double temp = (buyNum + totalDiamond) / (amount * payNow);
                    newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);

                } else {
                    double temp = (buyNum + totalDiamond) / ((totalDiamond / ratio) + (amount * payNow));
                    newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);

                }
                int i = orderMapper.updateByOrderId(order.getId(), tradeNo, buyNum);//更新订单
                if(i>0) {
                    accountInfoMapper.updateRatioByUserid(newRatio, order.getUserId());
                    pocketInfoMapper.increaseDiamondByUserId(order.getUserId(), buyNum);//给用户加钱
                    orderMapper.updateOrderRatio(order.getId(), newRatio);//更新账单的比值

                    //收入存入新的表单中
                    UserGain gain = UserGain.getGain(order.getUserId(),C.UserGainType.YiPay_pay, order.getBuyNum(), LibSysUtils.toInt(order.getId()));
                    userGainMapper.insertSelective(gain);


                    //加钱时候  充值EMO存入缓存 VIP
                    addCache(order.getBuyNum(),order.getUserId());

                }

              /*  //开发票
                if(order.getPaymentCode()==11) {
                    if (LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.pay_ezpay_switch), false) && !"消费者".equals(order.getUser_name())) {
                        log.info("开始调用开发票接口==========");
                        EzPay.getPostData(merchantOrderNo, tradeNo, order.getAmount().intValue(), order.getUser_name(), order.getEmail());
                    }
                }*/


            }
        }

    }

    //红阳 支付成功后  加钱 更新订单
    @Transactional
    public void updatehyPayOrder(String merchantOrderNo, String tradeNo) {
        JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));

        double payNow = coin_proportion.optDouble("hyPay",0.985);
        Order order = orderMapper.selectByOrderSnOne(merchantOrderNo);
        if(order!=null){
            if(order.getState()!=3) {
                log.error("订单状态============"+order.getState());
                Integer buyNum = order.getBuyNum();

                //现有的比值
                Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(order.getUserId(), "ratio"));
                Double amount = order.getAmount();//价格
                Integer totalDiamond = 0;
                BigDecimal newRatio;
                PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(order.getUserId());
                if (pocketInfo != null) {
                    totalDiamond = pocketInfo.getTotalDiamond();//现有的emo
                }

                if (ratio == 0) {
                    double temp = (buyNum + totalDiamond) / (amount * payNow);
                    newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);

                } else {
                    double temp = (buyNum + totalDiamond) / ((totalDiamond / ratio) + (amount * payNow));
                    newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);

                }
                int i = orderMapper.updateByOrderId(order.getId(), tradeNo, buyNum);//更新订单
                if(i>0) {
                    accountInfoMapper.updateRatioByUserid(newRatio, order.getUserId());
                    pocketInfoMapper.increaseDiamondByUserId(order.getUserId(), buyNum);//给用户加钱
                    orderMapper.updateOrderRatio(order.getId(), newRatio);//更新账单的比值

                    //收入存入新的表单中
                    UserGain gain = UserGain.getGain(order.getUserId(),C.UserGainType.HongYang_pay, order.getBuyNum(), LibSysUtils.toInt(order.getId()));
                    userGainMapper.insertSelective(gain);


                    //加钱时候  充值EMO存入缓存 VIP
                    addCache(order.getBuyNum(),order.getUserId());
                }


            }
        }

    }


    //支付成功后  加钱 更新订单
    @Transactional
    public void updatePayssion(String merchantOrderNo, String tradeNo) {
        JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));

        double payNow = coin_proportion.optDouble("payssion",0.95);
        Order order = orderMapper.selectByOrderSnOne(merchantOrderNo);
        if(order!=null){
            if(order.getState()!=3) {
                log.error("订单状态============"+order.getState());
                Integer buyNum = order.getBuyNum();

                //现有的比值
                Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(order.getUserId(), "ratio"));
                Double amount = order.getAmount();//价格
                Integer totalDiamond = 0;
                BigDecimal newRatio;
                PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(order.getUserId());
                if (pocketInfo != null) {
                    totalDiamond = pocketInfo.getTotalDiamond();//现有的emo
                }

                if (ratio == 0) {
                    double temp = (buyNum + totalDiamond) / (amount * payNow);
                    newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);

                } else {
                    double temp = (buyNum + totalDiamond) / ((totalDiamond / ratio) + (amount * payNow));
                    newRatio = new BigDecimal(temp).setScale(8, BigDecimal.ROUND_HALF_UP);

                }
                int i = orderMapper.updateByOrderId(order.getId(), tradeNo, buyNum);//更新订单
                if(i>0) {
                    accountInfoMapper.updateRatioByUserid(newRatio, order.getUserId());
                    pocketInfoMapper.increaseDiamondByUserId(order.getUserId(), buyNum);//给用户加钱
                    orderMapper.updateOrderRatio(order.getId(), newRatio);//更新账单的比值

                    //收入存入新的表单中
                    UserGain gain = UserGain.getGain(order.getUserId(),C.UserGainType.payssion_pay, order.getBuyNum(), LibSysUtils.toInt(order.getId()));
                    userGainMapper.insertSelective(gain);


                    //加钱时候  充值EMO存入缓存 VIP
                    addCache(order.getBuyNum(),order.getUserId());

                }


            }
        }

    }


    public void addCache( int buyNum, int userId){
        //加钱时候  充值EMO存入缓存 VIP
        long monthTime = LibDateUtils.getLibDateTime("yyyyMM");
        WKCache.add_recharge_rank_month(monthTime, buyNum, LibSysUtils.toString(userId));
        taskService.dayTaskHandle(C.TaskId.buy_emo,userId, buyNum);
    }


}
