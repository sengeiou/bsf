package com.weking.core.payNow;

import com.weking.cache.WKCache;
import com.weking.core.C;
import com.weking.core.HttpClient4Utils;
import com.weking.core.HttpXmlUtils;
import com.weking.core.newebpay.AESJAVA;
import com.weking.core.newebpay.EzPay;
import com.weking.core.newebpay.NewebPayNew;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import netscape.javascript.JSObject;
import org.apache.http.Consts;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class PayNowUtil {

    private static Logger log = Logger.getLogger(PayNowUtil.class);
    private static HttpClient httpClient = HttpClient4Utils.createHttpClient(100, 20, 1000, 1000, 1000);

    private static final String webNo;
    private static final String receiverTel;
    private static final String ECPlatform;
    private static final String pay_code;
    private static final String key="paynowepointpaynowcomtw282299550";
    private static final String iv="epoint2822995500";
    private static final String MemCid="42867399";
    private static final String ApiCode="marcus0919";
    private static final String mEngine = "AES";
    private static final String mCrypto5 = "AES/CBC/PKCS5Padding";
    private static final String mCryptoN = "AES/CBC/NoPadding";
    private static final String url = "https://epoint.paynow.com.tw";


    static {
        webNo = WKCache.get_system_cache(C.WKSystemCacheField.payNow_webNo);
        receiverTel = WKCache.get_system_cache(C.WKSystemCacheField.payNow_receiverTel);
        ECPlatform = WKCache.get_system_cache(C.WKSystemCacheField.payNow_ECPlatform);
        pay_code = WKCache.get_system_cache(C.WKSystemCacheField.pay_code);
    }

    public static JSONObject getPayNowData(String orderSn, int amount, String user_name, String email,String payType,int buy_num) {
        JSONObject obj = new JSONObject();
        String PassCode = webNo + orderSn + amount + pay_code;
        PassCode = ShaPayNow.getSha1(PassCode);
        try {
            obj.put("webNo", URLEncoder.encode(webNo, "UTF-8"));
            obj.put("PassCode", URLEncoder.encode(PassCode.toUpperCase(), "UTF-8"));
            obj.put("ReceiverName", URLEncoder.encode(user_name, "UTF-8"));
            obj.put("ReceiverID", URLEncoder.encode(email, "UTF-8"));
            obj.put("ReceiverTel", URLEncoder.encode(receiverTel, "UTF-8"));
            obj.put("OrderNo", URLEncoder.encode(orderSn, "UTF-8"));
            obj.put("ECPlatform", URLEncoder.encode(ECPlatform, "UTF-8"));
            obj.put("TotalPrice", URLEncoder.encode(LibSysUtils.toString(amount), "UTF-8"));
            obj.put("Note1", URLEncoder.encode(LibSysUtils.toString(buy_num), "UTF-8"));
            obj.put("OrderInfo", URLEncoder.encode("EMO", "UTF-8"));
            obj.put("PayType", URLEncoder.encode(payType, "UTF-8"));
            //if(payType.equals("03")){
            obj.put("AtmRespost", URLEncoder.encode("1", "UTF-8"));
            //}


            return obj;
        } catch (UnsupportedEncodingException e) {
            return null;
        }

    }


    public static String Encrypt( String incontent) {
        try {
            SecretKeySpec sks = new SecretKeySpec(key.getBytes(Charset.forName("UTF-8")), mEngine);
            IvParameterSpec ivs = new IvParameterSpec(iv.getBytes(Charset.forName("UTF-8")));
            Cipher cipher = Cipher.getInstance(mCryptoN);
            cipher.init(Cipher.ENCRYPT_MODE, sks, ivs);

            String paddingContent = zeroPadding(incontent);
            byte[] byteContent = paddingContent.getBytes(Charset.forName("UTF-8"));
            byte[] cryptograph = cipher.doFinal(byteContent);
            return Base64.getEncoder().encodeToString(cryptograph);
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    public static String Decrypt( String content) {
        try {
            byte[] contentBytes = Base64.getDecoder().decode(content.getBytes());
            SecretKeySpec sks = new SecretKeySpec(key.getBytes("UTF-8"), mEngine);
            IvParameterSpec ivs = new IvParameterSpec(iv.getBytes("UTF-8"));
            Cipher cipher = Cipher.getInstance(mCryptoN);
            cipher.init(Cipher.DECRYPT_MODE, sks, ivs);
            return new String(RemovePadding(cipher.doFinal(contentBytes)));
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    public static String zeroPadding(String data) {
        int bs = 16;
        int padding = bs - (data.length() % bs);
        String padding_text = "";
        for (int i = 0; i < padding; i++) {
            padding_text += (char) 0;
        }
        return data + padding_text;
    }

    private static byte[] RemovePadding(byte[] data) {
        int iLength = data[data.length - 1];
        byte[] output = new byte[data.length - 1];
        System.arraycopy(data, 0, output, 0, output.length);
        return output;
    }
    //查询剩余点数
    public static int findSurplusNumber(String account) {
        try {
            int number=0;
            JSONObject object = new JSONObject();
            object.put("MemCid",MemCid);
            object.put("ApiCode",ApiCode);
            object.put("Account",account);
            String passCode=ShaPayNow.getSHA256(MemCid+ApiCode+account);//sha 加密
            object.put("PassCode",passCode);
            String jStr = Encrypt(object.toString());//aes加密
            Map<String, String> params = new HashMap<>();
            params.put("JStr", jStr);
            String post = HttpXmlUtils.sendPost(httpClient,url+"/Api/EPointApi/Sel_Repoint", params, Consts.UTF_8);
            String decrypt = Decrypt( post);
            if (!LibSysUtils.isNullOrEmpty(decrypt)) {
                JSONObject obj = JSONObject.fromObject(decrypt);
                number = obj.optInt("RePoint");
            }
            return number;

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            return 0;
        }
    }


    //扣除点数
    public static String deductNumber(String account,Integer epoint ,String memOrderNo) {
        try {
            String status="";
            String passCode = MemCid+ApiCode+ memOrderNo + account + epoint.toString();
            passCode = ShaPayNow.getSHA256(passCode);
            JSONObject obj = new JSONObject();
            obj.put("MemCid", MemCid);
            obj.put("ApiCode", ApiCode);
            obj.put("Account", account);
            obj.put("Point", epoint.toString());
            obj.put("MemOrderNo", memOrderNo);
            obj.put("PassCode", passCode);
            String jStr = Encrypt( obj.toString());
            Map<String, String> params = new HashMap<>();
            params.put("JStr", jStr);
            String post = HttpXmlUtils.sendPost(httpClient,url+"/Api/EPointApi/Up_Point_Used", params, Consts.UTF_8);
            String decrypt = Decrypt(post);
            if (!LibSysUtils.isNullOrEmpty(decrypt)) {
                JSONObject object = JSONObject.fromObject(decrypt);
                status = object.optString("Status");
            }
            return status;
            //log.info("返回===========" +post);
          /* JSONObject json = JSONObject.fromObject(post);
            String status = json.get("Status").toString();
            if("INV10002".equals(status)){
                getPostData( merchantOrderNo, tradeNo,amount,user_name,email);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            return null;
        }
    }


    public static void main(String[] args) {
       int buyNumber = findSurplusNumber("245998488");
        //String s = deductNumber("118001618", 332, "118001618_2");
        System.out.println(buyNumber);

    }
}
