package com.weking.core.digital;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2018/5/24.
 */
public class MacMD5 {
    private static byte[] digesta;

    /**
     * 对登录面膜进行MD5
     *
     * @param pwd
     *            需要加密的字符串
     * @return 通过MD5加密后的字符串
     */
    public static String CalcPWD(String pwd) {
        return CalcMD5(CalcMD5(pwd, 15), 15);
    }

    /**
     * 对字符串进行MD5加密
     *
     * @param myinfo
     *            需要加密的字符串
     * @return 通过MD5加密后的字符串
     */
    public static String CalcMD5(String myinfo, int length) {
        try {
            MessageDigest alga = MessageDigest.getInstance("MD5");
            alga.update(myinfo.getBytes());
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return byte2hex(digesta, length);

    }

    private static String byte2hex(byte[] b, int length) { // 二行制转字符串
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }

        // return hs;
        // 2012.11.12
        return hs.substring(0, length);
    }
}
