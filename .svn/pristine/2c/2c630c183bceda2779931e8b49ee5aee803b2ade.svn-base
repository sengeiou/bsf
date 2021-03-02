package com.weking.core.pay;

import com.weking.cache.WKCache;
import com.weking.core.C;
import com.weking.core.DateUtils;
import com.weking.core.HttpClient4Utils;
import com.weking.core.HttpXmlUtils;
import com.weking.core.payNow.ShaPayNow;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.http.Consts;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class PayssionUtil {

    private static Logger log = Logger.getLogger(PayssionUtil.class);
    private static HttpClient httpClient = HttpClient4Utils.createHttpClient(100, 20, 1000, 1000, 1000);

    private static final String api_key;
    private static final String secret_key;
    private static final String url;


    static {
        api_key = WKCache.get_system_cache(C.WKSystemCacheField.payssion_api_key);
        secret_key = WKCache.get_system_cache(C.WKSystemCacheField.payssion_secret_key);
        url = WKCache.get_system_cache(C.WKSystemCacheField.payssion_url);
    }

    public static JSONObject getPayssionData(String orderSn, double amount, String user_name, String email, String payType,String phone,String currency) {
        JSONObject obj = new JSONObject();
            obj.put("api_key", api_key);//数量
            obj.put("pm_id", payType);//支付方式
            obj.put("amount", amount);//金额
            obj.put("currency", currency);//货币
            obj.put("description", "EMO");//描述
            obj.put("order_id", orderSn);//订单号
            obj.put("secret_key", secret_key);//secret_key
            obj.put("payer_email", email);//邮箱
            obj.put("payer_name", user_name);//姓名

            obj.put("pay_type",payType);
        return obj;

    }

    public static String getData(String orderSn, String transactionNo) {

        String state="";
        String s=api_key+"|"+transactionNo+"|"+orderSn+"|"+secret_key;
        String notify_sig = getMD5Str(s);

        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", api_key);
        params.put("transaction_id", transactionNo);
        params.put("order_id", orderSn);
        params.put("api_sig", notify_sig);
        //log.info("返回=======+++++====" +postData);
        String post = HttpXmlUtils.sendPost(httpClient,url, params, Consts.UTF_8);
        if (!LibSysUtils.isNullOrEmpty(post)){
            JSONObject json = JSONObject.fromObject(post);
            String transaction = json.optString("transaction");
            if (!LibSysUtils.isNullOrEmpty(transaction)){
                JSONObject obj = JSONObject.fromObject(transaction);
                state = obj.optString("state");
            }
        }
        System.out.println(state);

        return state;

    }


    public static String getMD5Str(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest  = md5.digest(str.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //16是表示转换为16进制数
        String md5Str = new BigInteger(1, digest).toString(16);
        return md5Str;
    }

    public static void main(String[] args) {
        getData("A25921602665099024","TA14625466992462");
    }

}
