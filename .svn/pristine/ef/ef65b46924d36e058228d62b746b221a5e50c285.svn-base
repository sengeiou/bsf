package com.weking.core.payNow;

import com.weking.cache.WKCache;
import com.weking.core.C;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

public class YiPayUtil {

    private static Logger log = Logger.getLogger(YiPayUtil.class);
    private static final String yiPayMerchantId;
    private static final String key;
    private static final String iv;
    private static final String notificationEmail;
    private static final String mEngine = "AES";
    private static final String mCrypto5 = "AES/CBC/PKCS5Padding";
    private static final String mCryptoN = "AES/CBC/NoPadding";


    static {
        yiPayMerchantId = WKCache.get_system_cache(C.WKSystemCacheField.yiPay_webNo);
        key = WKCache.get_system_cache(C.WKSystemCacheField.yiPay_key);
        iv = WKCache.get_system_cache(C.WKSystemCacheField.yiPay_iv);
        notificationEmail = WKCache.get_system_cache(C.WKSystemCacheField.yiPay_notificationEmail);
    }

    public static JSONObject getYiPayData(String orderSn, int amount, String user_name, String email, String payType) {
        JSONObject obj = new JSONObject();
        JSONObject jsonObject = new JSONObject();

            obj.put("merchantId", yiPayMerchantId);
            obj.put("type", payType);
            obj.put("amount", amount);
            obj.put("orderNo", orderSn);
            obj.put("orderDescription", "EMO");
            obj.put("notificationEmail", notificationEmail);
            obj.put("returnURL", "http://appsme.chidaotv.com/pay/yiPayJump");
            obj.put("Note1", "EMO");
            obj.put("cancelURL", "http://appsme.chidaotv.com/pay/yiPayJump");
            obj.put("backgroundURL", "http://appsme.chidaotv.com/pay/yiPayJump");

            jsonObject.put("merchantId", yiPayMerchantId);
            jsonObject.put("amount", amount+"");
            jsonObject.put("orderNo", orderSn);
            jsonObject.put("returnURL", "http://appsme.chidaotv.com/pay/yiPayJump");
            jsonObject.put("cancelURL", "http://appsme.chidaotv.com/pay/yiPayJump");
            jsonObject.put("backgroundURL", "http://appsme.chidaotv.com/pay/yiPayJump");
       /* jsonObject.put("merchantId", yiPayMerchantId);
        jsonObject.put("amount", 1500+"");
        jsonObject.put("orderNo", "YP2016111503353");
        jsonObject.put("returnURL", "https://gateway-test.yipay.com.tw/demo/return");
        jsonObject.put("cancelURL", "https://gateway-test.yipay.com.tw/demo/cancel");
        jsonObject.put("backgroundURL", "https://gateway-test.yipay.com.tw/demo/background");*/

            System.out.println(jsonObject.toString());
        String TradeInfo = null;//获得 AES 加密
        try {
            TradeInfo = AESSample.encrypt(jsonObject.toString(),key,iv);
            System.out.println(TradeInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
            String checkCode = ShaPayNow.getSha1(TradeInfo); //SHA1
        System.out.println(checkCode);

            obj.put("checkCode",checkCode);
            obj.put("yiPay","yiPay");



            return obj;


    }



}
