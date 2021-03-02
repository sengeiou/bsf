package com.weking.core.newebpay;

import com.weking.cache.WKCache;
import com.weking.core.C;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class NewebPayNew {
    private static Logger log = Logger.getLogger(NewebPayNew.class);

    private static final String key;
    private static final String iv;
    private static final String returnUrl;
    private static final String MerchantID;
    private static final String email;
    private static final String mEngine = "AES";
    private static final String mCrypto5 = "AES/CBC/PKCS5Padding";
    private static final String mCryptoN = "AES/CBC/NoPadding";
    static {
        key = WKCache.get_system_cache(C.WKSystemCacheField.pay_new_NewebPay_key);
        iv = WKCache.get_system_cache(C.WKSystemCacheField.pay_new_NewebPay_iv);
        returnUrl = WKCache.get_system_cache(C.WKSystemCacheField.pay_new_NewebPay_h5);
        MerchantID = WKCache.get_system_cache(C.WKSystemCacheField.pay_new_NewebPay_MerchantID);
        email = WKCache.get_system_cache(C.WKSystemCacheField.pay_new_NewebPay_email);
    }

    public static JSONObject getPostData(String orderSn,int amount) {
        //log.info("3加密文件==========="+orderSn+amount);
        try {
            //log.info("3数据库获取==========="+key+"iv:"+iv+"returnUrl:"+returnUrl+"MerchantID:"+MerchantID+"email:"+email);
            JSONObject object = new JSONObject();
            object.put("MerchantID",MerchantID);
            object.put("RespondType","json");
            object.put("TimeStamp",LibSysUtils.toString(System.currentTimeMillis()));
            object.put("Version","1.5");
            object.put("MerchantOrderNo",orderSn);
            object.put("Amt",amount);
            object.put("ItemDesc","emo");
            object.put("ReturnURL",returnUrl);
            object.put("NotifyURL",returnUrl);
            object.put("Email",email);
            object.put("LoginType",0);
            String str="MerchantID="+MerchantID+"&RespondType=JSON&TimeStamp="+LibSysUtils.toString(System.currentTimeMillis())+"&Version=1.5&" +
                    "MerchantOrderNo="+orderSn+"&Amt="+amount+"&ItemDesc=emo&ReturnURL="+returnUrl+"&Email="+email+"&LoginType=0&NotifyURL="+returnUrl;
            System.out.println(str);
            String TradeInfo = Encrypt(str);//获得 AES 加密
            //log.error("3AES加密==========="+TradeInfo);
            JSONObject json = new JSONObject();
            json.put("MerchantID",MerchantID);
            json.put("TradeInfo",TradeInfo);
            String TradeSha="HashKey="+key+"&"+TradeInfo+"&HashIV="+iv;
            //String sha256 = sha256_HMAC(TradeSha, key);
            String sha256 = getSHA256StrJava(TradeSha);
             sha256 = sha256.toUpperCase();
            //log.error("sha256==========="+sha256);
            json.put("TradeSha",sha256);
            json.put("Version","1.5");
            return json;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e);
            return null;
        }
    }
    /**
     *  加密
     */
   /* public static String Encrypt(String encData, String secretKey, String iv) throws Exception {
        SecretKeySpec keyspec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes("UTF-8"));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
       // cipher.init(Cipher.ENCRYPT_MODE, getKey(secretKey), ivspec);
        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
        byte[] encrypted = cipher.doFinal(encData.getBytes("UTF-8"));
        return parseByte2HexStr(encrypted);
    }*/
    public static String Encrypt(String content) {
        try {
            SecretKeySpec sks = new SecretKeySpec(key.getBytes(Charset.forName("UTF-8")), mEngine);
            IvParameterSpec ivs = new IvParameterSpec(iv.getBytes(Charset.forName("UTF-8")));
            Cipher cipher = Cipher.getInstance(mCrypto5);
            cipher.init(Cipher.ENCRYPT_MODE, sks, ivs);
            byte[] byteContent = content.getBytes("UTF-8");
            byte[] cryptograph = cipher.doFinal(byteContent);
            return parseByte2HexStr(cryptograph);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        return null;
    }

    private static Key getKey(String password) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom(password.getBytes());
        // 生成KEY
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        //en.init(128, secureRandom);
        //解决linux 系统下面出错问题
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password.getBytes());
        kgen.init(128, random);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        // 转换KEY
        return new SecretKeySpec(enCodeFormat, "AES");
    }

    /**
     *  解密
     */
    public static String Decrypt(String sSrc, String sKey, String iv) {
        try{
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes("UTF-8"));
            SecretKeySpec key = new SecretKeySpec(sKey.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
            byte[] data =sSrc.getBytes("UTF-8");
            return new String(RemovePKCS7Padding(cipher.doFinal(data)));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static byte[] RemovePKCS7Padding(byte[] data){
        int iLength = data[data.length - 1];
        byte[] output = new byte[data.length - 1];
        System.arraycopy(data, 0, output, 0, output.length);
        return output;
    }


    public static String getSHA256StrJava(String str){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }


    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] removePadding(byte[] paddedInput) {
        int numPadBytes = paddedInput[paddedInput.length - 1];
        int originalSize = paddedInput.length - numPadBytes;
        byte[] original = new byte[originalSize];
        System.arraycopy(paddedInput, 0, original, 0, originalSize);
        return original;
    }

    /**
     * 将byte转为16进制
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    public static String decrypt(byte[] cryptograph) {
        try {
            SecretKeySpec sks = new SecretKeySpec(key.getBytes(Charset.forName("UTF-8")), mEngine);
            IvParameterSpec ivs = new IvParameterSpec(iv.getBytes(Charset.forName("UTF-8")));
            Cipher cipher = Cipher.getInstance(mCryptoN);

            cipher.init(Cipher.DECRYPT_MODE, sks, ivs);
            byte[] content = removePadding(cipher.doFinal(cryptograph));
            log.error("content============="+content);
            return new String(content);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        return null;
    }


    public static void main(String[] args) throws Exception{

        //解密
        String decrypt = NewebPay.decrypt(NewebPay.hexStringToByteArray("7147aaa402024850adfe85c0ca367bab3aa103032d13c84944f772b0798fe163da7af40b6ba7b5232e6d80ae88d781e2050eaa3894ef84f8a124f2c5fafac30011cfbccbcdf204f309874894e45959949880659c95637a0f13108e95cd3c6fd74d6274799c6693b31802daf0000867b77f0629cded1eb563d95fcfc2a88a1248bdb289261b0bf88ced2f50c9075ccbd714e56a5bfa23ec5194f96c286b0cded35ff20b7c3fa2a680afd222714cd7f5f2011d0d862f525b053e73ca36762fba54f7217929759917d2b9aa29565d42aa5ff7df1a8a474b50140a71b78c4a8fc44e47cebd57ffc3fdd6b8bcf74370f3641052949a7611918d5f1d7fd77c1614060b9d9696c5785999adcab7c5ee60fc98b681bc88dd064f8df687fcf7c128f0f90e271a0d0ba7f616d9c07a3f0ebc11faac13edf22b6eb8faf0fb4fe623a5a623c9bd82c620d904ce52ec10800ea7895619b28650d85c485fb0ce6c0543e5b74adff282260c58f01dec17d8534ab260218ee20416354988fc1fa2a9145a91721446ea614f82e31498232fd4bc67a7431bd34bd056124d77c6c81e9677c744524dc92de4fe3f0268be59e90d1eccbb27b47fd7e0cf155f6da4fe1e861403bc132422b94e3fbc8a0a5282ffb13c12f9607ce9b513e5b023081d06fd1741c72a594b73"));
        log.info("解密之后-----decrypt:" + decrypt);
        JSONObject dataObj = JSONObject.fromObject(decrypt);
        log.info("解密转json-----dataObj:" + dataObj);
        String status = dataObj.optString("Status");
        JSONObject result = dataObj.optJSONObject("Result");
        System.out.println(status);
        System.out.println(result.toString());

    }
}
