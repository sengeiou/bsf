package com.weking.core.payNow;

import com.braintreegateway.org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ShaPayNow {


    public static String getSha1(String str) {
        if (null == str || 0 == str.length()) {
            return null;
        }
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getSHA256( String data) {
        try {
            String key="marcus0919";
            byte[] keyBytes = key.getBytes(Charset.forName("UTF-8")); // 把密鑰字串轉為byte[]
            Key hmacKey = new SecretKeySpec(keyBytes, "HmacSHA256"); // 建立HMAC加密用密鑰
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256"); // 取得SHA256 HMAC的Mac實例
            hmacSHA256.init(hmacKey); // 使用密鑰對Mac進行初始化
            byte[] macData = hmacSHA256.doFinal(data.getBytes(Charset.forName("UTF-8"))); // 對原始訊息進行雜湊計算

            return Hex.encodeHexString(macData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public static void main(String[] args) {


    }


}
