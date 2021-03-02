/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.weking.core.gash;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * 3DES 加解密元件
 * @author jerryhu
 * @version 1.0.0
 */
public class Crypt3Des implements ICrypt3Des  {

    /** 指定演算方式 */
    private String _algorithm = "DESede";
    /** 指定 Tansformation */
    private String _transformation = "DESede/CBC/PKCS5Padding";
    /** BASE64 加密物件 */
    public static sun.misc.BASE64Encoder base64encode = new sun.misc.BASE64Encoder();
    /** BASE64 解密物件 */
    public static sun.misc.BASE64Decoder base64decode = new sun.misc.BASE64Decoder();
    
    /** 3DES 密鑰 */
    private String _key;
    /** 3DES 向量值 */
    private String _iv;

    /**
     * 取得向量
     * @return 向量值
     */
    public String getIv() {
        return _iv;
    }

    /**
     * 取得密鑰
     * @return 密鑰
     */
    public String getKey() {
        return _key;
    }

    /**
     * 以密鑰和向量值建構 3DES 物件
     * @param key 密鑰
     * @param iv 向量值
     */
    public Crypt3Des(String key, String iv) {

        if ( key == null || iv == null ) {

        }else{
            _key = key;
            _iv = iv;
        }

    }

    /**
     * 雜湊演算法
     * @param txt 欲雜湊之本文
     * @return 雜湊值
     */
    public static String sha1(String txt) {

        MessageDigest sha1;

        String result;

        byte[] buff, buff2;

        result = "";

        sha1 = null;

        try{

            sha1 = MessageDigest.getInstance("SHA1");
            buff = txt.getBytes("UTF-8");
            buff2 = sha1.digest(buff);
            result = base64_encode(buff2);

        }catch(Exception ex){

        }

        return result;

    }

    /**
     * 進行 BASE64 解碼
     * @param txt BASE64 密文
     * @return 解密後之本文
     */
    public static String base64_decode(String txt) {
        if (txt == null) return null;

        try{

            byte[] bytes = base64decode.decodeBuffer(txt);
            return (new String(bytes, "UTF-8"));

        }catch(Exception ex){
            return null;
        }
    }

    /**
     * 以 Byte 進行 BASE64 加密
     * @param bytes 欲加密之 Bytes
     * @return 加密後之密文
     */
    public static String base64_encode(byte[] bytes) {
        return removeBR(base64encode.encode(bytes));
    }

    /**
     * 進行 BASE64 加密
     * @param txt 欲加密之本文
     * @return 加密後之密文
     */
    public static String base64_encode(String txt) {
        try{
            return base64_encode(txt.getBytes("UTF-8"));
        }catch(Exception ex){ return ""; }
    }

    public String encrypt(String txt) throws Exception {

        String result;

        byte[] key;
        byte[] iv;

        result = "";
        key = base64decode.decodeBuffer(_key);
        iv = base64decode.decodeBuffer( _iv );

        SecureRandom sr = new SecureRandom();

        DESedeKeySpec dks = new DESedeKeySpec(key);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(_algorithm);

        SecretKey securekey = keyFactory.generateSecret(dks);

        IvParameterSpec ips = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(_transformation);

        cipher.init(Cipher.ENCRYPT_MODE, securekey, ips, sr);

        byte[] bt = cipher.doFinal(txt.getBytes("UTF-8"));
        result = base64_encode(bt);

        return result;

    }

    public String decrypt(String txt) throws Exception {

        String result;

        byte[] key;
        byte[] iv;

        result = "";
        key = base64decode.decodeBuffer(_key);
        iv = base64decode.decodeBuffer( _iv );

        SecureRandom sr = new SecureRandom();

        DESedeKeySpec dks = new DESedeKeySpec(key);

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(_algorithm);

        SecretKey securekey = keyFactory.generateSecret(dks);

        IvParameterSpec ips = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(_transformation);

        cipher.init(Cipher.DECRYPT_MODE, securekey, ips, sr);

        byte[] bt = cipher.doFinal(txt.getBytes("UTF-8"));
        result = bt.toString();

        return result;

    }

    /**
     * 移除換行標記
     * @param str 來源字串
     * @return 移除換行以後的字串
     */
    private static String removeBR(String str) {
        StringBuffer sf = new StringBuffer(str);

        for (int i = 0; i < sf.length(); ++i)
        {
          if (sf.charAt(i) == '\n')
          {
            sf = sf.deleteCharAt(i);
          }
        }
        for (int i = 0; i < sf.length(); ++i)
          if (sf.charAt(i) == '\r')
          {
            sf = sf.deleteCharAt(i);
          }

        return sf.toString();
      }
}
