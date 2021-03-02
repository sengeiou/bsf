package com.weking.core.newebpay;

import com.weking.cache.WKCache;
import com.wekingframework.core.LibProperties;
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

public class NewebPay {
    private static Logger log = Logger.getLogger(NewebPay.class);

    private static final String key;
    private static final String iv;
    private static final String returnUrl;
    private static final String MerchantID;
    private static final String email;
    private static final String mEngine = "AES";
    private static final String mCrypto5 = "AES/CBC/PKCS5Padding";
    private static final String mCryptoN = "AES/CBC/NoPadding";
    static {
        key = WKCache.get_system_cache("pay.NewebPay.key");
        iv = WKCache.get_system_cache("pay.NewebPay.iv");
        returnUrl = WKCache.get_system_cache("pay.NewebPay.h5");
        MerchantID=WKCache.get_system_cache("pay.NewebPay.MerchantID");
        email=WKCache.get_system_cache("pay.NewebPay.email");
    }

    public static JSONObject getPostData(String orderSn,int amount) {
       // log.info("加密文件==========="+orderSn+amount);
        try {
         //   log.info("数据库获取==========="+key+"iv:"+iv+"returnUrl:"+returnUrl+"MerchantID:"+MerchantID+"email:"+email);
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
            //log.error("AES加密前==========="+object.toString());
            String TradeInfo = Encrypt(str);//获得 AES 加密
           // log.error("AES加密==========="+TradeInfo);
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

       /* JSONObject object = new JSONObject();
        object.put("MerchantID","3430112");
        object.put("RespondType","JSON");
        object.put("TimeStamp","1485232229");
        object.put("Version","1.4");
        object.put("MerchantOrderNo","S_1485232229");
        object.put("Amt",40);
        object.put("ItemDesc","UnitTest");
       String mer_key = "12345678901234567890123456789012";
       String mer_iv = "1234567890123456";
       String str="MerchantID=3430112&RespondType=JSON&TimeStamp=1485232229&Version=1.4&MerchantOrderNo=S_1485232229&Amt=40&ItemDesc=UnitTest";
        String encrypt = Encrypt(str, mer_key, mer_iv);
        System.out.println(encrypt);*/
        String decrypt="415b1e1afeea23ace0d01df4a241f88d0a13ebe053b996f90c8fffd00ea62e4765b7d65a134157223cd4574f3bc0f7ea6a197048d40015f3e805e5b78729ba67b6b74539a19ca265abaf4edd320ded9005a74156b5ae9d091be084a9300152bcc14a807c0f6d400222fbd2958789f5c92b0ae06df1a009076694af448f447cf5cef40cef8deadd6228c9232eec5af0c4d9f837ed62e9973376de8f7f122c880ce65c8486cd14835a7972808a42d74d6ab75824afb6d3804b4e5a08c9702f1632c53b90ac122bc554ad16923961b8b1d95cde97d95a734948bb8202a6d71734343e00d59d10070463c5d468a82ccc2fd4869a58191f7ec42ef4fb0362bcf92a2b749d6529a7b93e6b8242e4953438d8028510d570ccae122c2af831f53d21e68fa75b20f6bd7ce02bbe5e2e2749fe382861a0b67d2c377470dc83e160d173e335352f7650ab07987f51d13b222f7615587c3f5db8f405ede2d307d4e921945927a2706a77c169d3dab6745c74642a97860adce8785ad1948d450d97f8015b2c354a871c9bd694487a21a7f3830f7e1c29c61d6315cb4df3182a828ab99820d9b49e619199c748230e300cdeda69d29825a21696988c9fa3c19345cd9da4eadb0fd185d0850ab48894b73287ac9e5970f1b96afcab20cc4fba8f9563e9ae4dfb79";
        String s = decrypt(hexStringToByteArray(decrypt));
        JSONObject dataObj = JSONObject.fromObject(s);
        String status = dataObj.optString("Status");
        //String decrypt = Decrypt(s, "12345678901234567890123456789012", "1234567890123456");
        String msg;
        if (status.equalsIgnoreCase("SUCCESS")) {
            //交易成功加钱
            msg = LibProperties.getLanguage("zh_CN", "weking.lang.app.gash.jump.success");
            JSONObject result = dataObj.optJSONObject("Result");
            String merchantOrderNo = result.optString("MerchantOrderNo");//订单号
            String tradeNo = result.optString("TradeNo");
        } else {
            log.error("解密之后-----status:" + status);
            msg = LibProperties.getLanguage("zh_CN", "weking.lang.app.gash.jump.error");
        }

    }
}
