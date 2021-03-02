package com.weking.core.digital;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Administrator on 2018/5/24.
 */
public class HelpUtils {
    private static final Logger log = Logger.getLogger(HelpUtils.class);


    /**
     * 检查一个字符串是null还是空的
     *
     * @param param
     * @return boolean
     */
    public static boolean nullOrBlank(Object param) {
        return (param == null || param.toString().length() == 0 || param.toString().trim().equals("")
                || param.toString().trim().equalsIgnoreCase("null") || param.toString().trim().equals("undefined"))
                ? true
                : false;
    }


    public static Map newHashMap(Object... args) {
        return toMap(args);
    }

    public static Map toMap(Object[] args) {
        Map map = new HashMap();
        for (int i = 1; i < args.length; i += 2) {
            map.put(args[i - 1], args[i]);
        }
        return map;
    }



    /**
     * 将泛型形参给出的类中设置的属性值转换为Map形式的键值对 t一般是pojo类

     * @param t
     */
    public static <T extends Object> Map<String, Object> objToMap(T t) {
        Map<String, Object> params = new HashMap<String, Object>();

        Class<?> clazz = t.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field[] fields = clazz.getDeclaredFields();

                for (int j = 0; j < fields.length; j++) { // 遍历所有属性
                    String name = fields[j].getName(); // 获取属性的名字
                    Object value = null;

                    Method method = t.getClass()
                            .getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
                    value = method.invoke(t);

                    if (value != null)
                        params.put(name, value);
                }
            } catch (Exception e) {
            }
        }

        return params;
    }

    /**
     * 当前时间戳
     */
    public static String getNowTimeStamp() {
        long time = System.currentTimeMillis();
        String nowTimeStamp = String.valueOf(time / 1000);
        return nowTimeStamp;
    }

    /**
     * 当前时间戳
     */
    public static long getNowTimeStampInt() {
        return Long.parseLong(getNowTimeStamp());
    }

    /**
     * 签名验证
     *
     * @param params
     * @param apiSecret
     * @return
     */
    private static String createSign(Map<String, Object> params, String apiSecret) {
        SortedMap<String, Object> sortedMap = new TreeMap<String, Object>(params);

        StringBuffer sb = new StringBuffer();
        Set es = sortedMap.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        if ("MD5".equals(params.get("sign_type"))) {
            sb.append("apiSecret=" + apiSecret); // MD5签名时，apiSecret放在最后
        } else if ("HmacSHA256".equals(params.get("sign_type"))) {
            sb.deleteCharAt(sb.length() - 1); // 删除最后的&
        } else {
            return null;
        }

        String valueToDigest = sb.toString();
        String actualSign = "";
        if ("MD5".equals(params.get("sign_type"))) {
            actualSign = MacMD5.CalcMD5(valueToDigest, 28);
        } else if ("HmacSHA256".equals(params.get("sign_type"))) {
            //byte[] hash = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, apiSecret).hmac(valueToDigest); // TODO: 2018/5/24
            //actualSign = Base64.encodeBase64String(hash);
            actualSign = MacMD5.CalcMD5(actualSign, 28); // 因为256算法算出的加密字符串中含/，导致Get请求无效，因此再进行一次MD5
        }

        return actualSign;
    }

    /**
     * API预校验
     *
     * @param reqBaseSecret
     * @return
     */
    public static String preValidateBaseSecret(ReqBaseSecret reqBaseSecret) {
        //  这里直接return了
        if (null == reqBaseSecret.getTimestamp() || (reqBaseSecret.getTimestamp() + "").length() != 10) {
            return Resp.ILLEGAL_TIMESTAMP_FORMAT;
        }
        if (HelpUtils.nullOrBlank(reqBaseSecret.getSign_type())) {
            return Resp.ILLEGAL_SIGN_TYPE;
        }
        if (!"MD5".equals(reqBaseSecret.getSign_type()) && !"HmacSHA256".equals(reqBaseSecret.getSign_type())) {
            return Resp.ILLEGAL_SIGN_TYPE;
        }
        if (HelpUtils.nullOrBlank(reqBaseSecret.getApi_key())) {
            return Resp.ILLEGAL_API_KEY;
        }
        if (HelpUtils.nullOrBlank(reqBaseSecret.getSign())) {
            return Resp.ILLEGAL_SIGN;
        }
        if (Math.abs(reqBaseSecret.getTimestamp() - HelpUtils.getNowTimeStampInt()) > 15) {
            return Resp.ILLEGAL_TIMESTAMP;
        }

        return "";
    }

    /**
     * API校验
     *
     * @param map
     * @param apiSecret
     * @return
     */
    public static String validateBaseSecret(Map<String, Object> map, String apiSecret) {
        // 签名校验
        if (!(map.get("sign") + "").equals(createSign(map, apiSecret))) {
            return Resp.ILLEGAL_SIGN;
        }

        return "";
    }
}
