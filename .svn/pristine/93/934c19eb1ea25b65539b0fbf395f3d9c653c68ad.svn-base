package com.weking.core.payNow;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Base64;

public class AESSample {

    
    public static String encrypt(String param, String keyString, String ivString) throws Exception
    {
        try {
            byte[] key = Base64.getDecoder().decode(keyString);
            byte[] iv = Base64.getDecoder().decode(ivString);
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            byte[] byteCipherText = cipher.doFinal(param.getBytes("UTF-8"));
            String encryptedString = Base64.getEncoder().encodeToString(byteCipherText);
            
            return encryptedString;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static String decrypt(String encryptedString, String keyString, String ivString) throws Exception {
        try {
            byte[] key = Base64.getDecoder().decode(keyString);
            byte[] iv = Base64.getDecoder().decode(ivString);
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            byte[] byteCipherText = cipher.doFinal(Base64.getDecoder().decode(encryptedString));
            String decryptedString = new String(byteCipherText, "UTF-8");
            
            return decryptedString;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static String sha1(String str) throws Exception {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
	        digest.reset();
	        digest.update(str.getBytes("UTF-8"));
	        return String.format("%040x", new BigInteger(1, digest.digest()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
