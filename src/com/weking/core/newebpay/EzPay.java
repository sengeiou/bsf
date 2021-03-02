package com.weking.core.newebpay;

import com.weking.cache.WKCache;
import com.weking.core.C;
import com.weking.core.HttpClient4Utils;
import com.weking.core.HttpXmlUtils;
import com.wekingframework.core.LibSysUtils;
import org.apache.http.Consts;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class EzPay {

    private static Logger log = Logger.getLogger(EzPay.class);
    private static HttpClient httpClient = HttpClient4Utils.createHttpClient(100, 20, 1000, 1000, 1000);
    private static final String key;
    private static final String iv;
    private static final String merchantID;
    private static final String email;
    private static final String url;
    private static final String mEngine = "AES";
    private static final String mCrypto5 = "AES/CBC/PKCS5Padding";
    private static final String mCryptoN = "AES/CBC/NoPadding";
    static {
        key = WKCache.get_system_cache(C.WKSystemCacheField.pay_ezpay_hashKey);
        iv = WKCache.get_system_cache(C.WKSystemCacheField.pay_ezpay_iv);
        url = WKCache.get_system_cache(C.WKSystemCacheField.pay_ezpay_url);
        merchantID = WKCache.get_system_cache(C.WKSystemCacheField.pay_ezpay_MerchantID);
        email = WKCache.get_system_cache(C.WKSystemCacheField.pay_ezpay_email);
    }

    public static String getPostData(String merchantOrderNo, String tradeNo,int amount,String user_name,String user_email) {
        try {
            //log.info("数据库获取===========" + key + "iv:" + iv + "url:" + url + "MerchantID:" + merchantID + "email:" + email);
            String timeStamp = LibSysUtils.toString(System.currentTimeMillis()).substring(0, 10);
            //老版本
           /* int taxAmt = (int)amount * 5/100;
            int Amt=amount-taxAmt;*/
            Double temp = amount / 1.05;
            int Amt=(int)Math.round(temp);
            int taxAmt=amount-Amt;

            String str="RespondType=JSON&Version=1.4&TimeStamp="+ timeStamp+
                    "&TransNum="+merchantOrderNo+"&MerchantOrderNo="+tradeNo+"&Status=1&Category=B2C&BuyerName=" +user_name+
                    "&BuyerEmail="+user_email+"&PrintFlag=Y&TaxType=1&TaxRate=5&Amt="+Amt+"&TaxAmt="+taxAmt+"&TotalAmt="+amount+
                    "&ItemName=emo&ItemCount=1&ItemUnit=個&ItemPrice="+amount+"&ItemAmt="+amount+"&ItemTaxType=1" ;

            String postData = Encrypt(str);

           /* json.put("PostData_",postData);
            json.put("MerchantID_",merchantID);*/
            Map<String, String> params = new HashMap<String, String>();
            params.put("PostData_", postData);
            params.put("MerchantID_", merchantID);
            //log.info("返回=======+++++====" +postData);
            String post = HttpXmlUtils.sendPost(httpClient,url, params, Consts.UTF_8);
            //log.info("返回===========" +post);
          /* JSONObject json = JSONObject.fromObject(post);
            String status = json.get("Status").toString();
            if("INV10002".equals(status)){
                getPostData( merchantOrderNo, tradeNo,amount,user_name,email);
            }*/

            return post;

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            return null;
        }
    }
    public static String Encrypt(String content) {
        try {
            SecretKeySpec sks = new SecretKeySpec(key.getBytes(Charset.forName("UTF-8")), mEngine);
            IvParameterSpec ivs = new IvParameterSpec(iv.getBytes(Charset.forName("UTF-8")));
            Cipher cipher = Cipher.getInstance(mCrypto5);
            cipher.init(Cipher.ENCRYPT_MODE, sks, ivs);
            byte[] byteContent = content.getBytes("UTF-8");
            byte[] cryptograph = cipher.doFinal(byteContent);
            new AESJAVA(key,iv);
            return AESJAVA.parseByte2HexStr(cryptograph);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        return null;
    }

    public static void main(String[] args) {
        if (LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.pay_ezpay_switch), false)) {
            log.info("开始调用开发票接口==========");
            String postData = getPostData("19070615161146029", "37861562397360350", 10,"lizhiguo","15571169379@163.com");
            System.out.println(postData);
        }




    }
}
