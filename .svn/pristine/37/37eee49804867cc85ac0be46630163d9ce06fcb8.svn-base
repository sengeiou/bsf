package com.weking.core.payNow;

import com.weking.cache.WKCache;
import com.weking.core.C;
import com.weking.core.DateUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class HongYangUtil {

    private static Logger log = Logger.getLogger(HongYangUtil.class);
    private static final String web;
    private static final String ATMWeb;
    private static final String storeWeb;
    private static final String password;

    private static final String mEngine = "AES";
    private static final String mCrypto5 = "AES/CBC/PKCS5Padding";
    private static final String mCryptoN = "AES/CBC/NoPadding";


    static {
        web = WKCache.get_system_cache(C.WKSystemCacheField.hy_web_card);
        ATMWeb = WKCache.get_system_cache(C.WKSystemCacheField.hy_web_atm);
        password = WKCache.get_system_cache(C.WKSystemCacheField.hy_password);
        storeWeb = WKCache.get_system_cache(C.WKSystemCacheField.hy_store_web);
    }

    public static JSONObject getHongYangData(String orderSn, int amount, String user_name, String email, String payType,String phone) {
        JSONObject obj = new JSONObject();
        try {
            //log.info("getHongYangData:web="+web+"  ATMWeb="+ATMWeb+" password="+password);
            obj.put("MN", amount);//数量
            obj.put("Td", URLEncoder.encode(orderSn, "UTF-8"));//订单
            obj.put("sna", URLEncoder.encode(user_name, "UTF-8"));//姓名
            obj.put("sdt", URLEncoder.encode(phone, "UTF-8"));//电话
            obj.put("email", URLEncoder.encode(email, "UTF-8"));
            obj.put("Note1", URLEncoder.encode("EMO", "UTF-8"));
            String ChkValue="";
            if("0".equals(payType)||"1".equals(payType)) {
                obj.put("web", URLEncoder.encode(web, "UTF-8"));
                obj.put("Card_Type", payType);//付款方式
                ChkValue =web+password+amount ;//获得 AES 加密

            }else if("6".equals(payType)) {
                obj.put("web", URLEncoder.encode(ATMWeb, "UTF-8"));
                ChkValue =ATMWeb+password+amount ;//获得 AES 加密

            }else if("7".equals(payType)){//超商
                obj.put("web", URLEncoder.encode(storeWeb, "UTF-8"));
                obj.put("Note2", URLEncoder.encode("ATM", "UTF-8"));
                obj.put("AgencyType", 2);//繳款方式
                obj.put("AgencyBank", 1);//ATM 轉帳
                String DueDate = DateUtils.BeforeNowByDay(-7);
                obj.put("DueDate", DueDate);
                obj.put("ProductName1", "EMO");//
                obj.put("ProductPrice1", 1);//单价
                obj.put("ProductQuantity1", amount);//数量
                ChkValue =storeWeb+password+amount ;//获得 AES 加密
            }
            System.out.println(ChkValue);
            ChkValue = ShaPayNow.getSha1(ChkValue); //SHA1
            ChkValue = ChkValue.toUpperCase();
            obj.put("ChkValue", URLEncoder.encode(ChkValue, "UTF-8"));//检验码

            obj.put("pay_type",payType);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return obj;


    }



}
