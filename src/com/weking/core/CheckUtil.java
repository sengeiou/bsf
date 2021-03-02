package com.weking.core;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.wekingframework.core.LibSysUtils;
import io.jsonwebtoken.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtil {

    /**
     * KEYS请求URL
     */
    private static final String KEYS_REQUEST_URL = "https://appleid.apple.com/auth/keys";

    //验证手机号格式(只验证位数和纯数字)
    public static boolean checkPhone(String phone) {
        boolean flag = false;
        if (!LibSysUtils.isNullOrEmpty(phone)) {
            Pattern p = Pattern.compile("[0-9]\\d{4,12}$");
            Matcher m = p.matcher(phone);
            flag = m.matches();
        }
        return flag;
    }


    //验证邮箱格式
    public static boolean checkEmail(String email) {
//        boolean flag = false;
//        if (!LibSysUtils.isNullOrEmpty(email)) {
//            try{
//                String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
//                Pattern regex = Pattern.compile(check);
//                Matcher matcher = regex.matcher(email);
//                flag = matcher.matches();
//            }catch(Exception e){
//                flag = false;
//            }
//        }
        return true;
    }

    //验证字符串长度
    public static boolean checkStrLength(String str, int minLeng, int maxLeng) {
        boolean flag = false;
        int leng = getStrLength(str);
        if (leng >= minLeng && leng <= maxLeng) {
            flag = true;
        }
        return flag;
    }

    //字符串长度
    public static int getStrLength(String s) {
        if (s == null)
            return 0;
        char[] c = s.toCharArray();
        int len = 0;
        int k = 0x80;
        for (char i : c) {
            len++;
            if (i / k != 0) {
                len++;
            }
        }
        return len;
    }

    public static Jws<Claims> getPublicKey(String jwt) {
        try {
            // 获取加密id
            if (jwt.split("\\.").length < 1) {
                return null;
            }
            String signInfo = new String(Base64.decodeBase64(jwt.split("\\.")[0]));
            String kid = new JSONObject(signInfo).get("kid").toString();
            String claim = new String(Base64.decodeBase64(jwt.split("\\.")[1]));
            JSONObject jsonObject1 = new JSONObject(claim);
            String aud = jsonObject1.get("aud").toString();
            String sub = jsonObject1.get("sub").toString();
            String keysResult = HttpXmlUtils.sendGet(KEYS_REQUEST_URL, "");
            JSONObject jsonObject = new JSONObject(keysResult);
            JSONArray keys = jsonObject.optJSONArray("keys");
            JSONObject item = null;
            for (int i = 0; i < keys.length(); i++) {
                JSONObject itemKid = keys.optJSONObject(i);
                String kid1 = itemKid.optString("kid");
                if (kid.equals(kid1)) {
                    item = itemKid;
                    break;
                }
            }
            if (item == null) {
                item = keys.getJSONObject(0);
            }
            String n = item.optString("n");
            String e = item.optString("e");
            return verify(getPublicKey(n, e), jwt, aud, sub);

        } catch (Exception e) {

        }
        return null;
    }


    //	通过下面这个方法验证JWT的有效性
    public static Jws<Claims> verify(PublicKey key, String jwt, String audience, String subject) {
        JwtParser jwtParser = Jwts.parser().setSigningKey(key);
        jwtParser.requireIssuer("https://appleid.apple.com");
        jwtParser.requireAudience(audience);
        jwtParser.requireSubject(subject);
        try {
            Jws<Claims> claim = jwtParser.parseClaimsJws(jwt);
            if (claim != null && claim.getBody().containsKey("auth_time")) {
                return claim;
            }
            return null;
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static PublicKey getPublicKey(String n,String e) throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger bigIntModulus = new BigInteger(1,Base64.decodeBase64(n));
        BigInteger bigIntPrivateExponent = new BigInteger(1,Base64.decodeBase64(e));
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


    public static void main(String[] args) {
        Pattern p = Pattern.compile("[1-9]\\d{4,12}$");
        Matcher m = p.matcher("1236445265488");
        boolean flag = m.matches();
        System.out.println(flag);
    }
}
