package com.weking.core;

import com.weking.cache.WKCache;
import com.weking.model.weixin.Unifiedorder;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 微信支付
 *
 * @author xiebin
 * @date 2015年11月26日上午9:58:19
 */
public class WeixinPay {

    static Logger log = Logger.getLogger(WeixinPay.class);

    private static final String appid;
    private static final String mch_id;
    private static final String notify_url;
    private static final String key;

    static {
        appid = WKCache.get_system_cache("weking.config.weixin.pay.appid");
        mch_id = WKCache.get_system_cache("weking.config.weixin.pay.mch_id");
        notify_url = WKCache.get_system_cache("weking.config.pay.url") + "pay/wxPay";
        key = WKCache.get_system_cache("weking.config.weixin.pay.key");
    }

    //设置参数下订单
    @SuppressWarnings("static-access")
    public static String setParam(int userId, String body, String orderSn, int total_fee, String ip) {
        //参数组
        String nonce_str = LibSysUtils.getRandomString(16);
        String detail = LibProperties.getConfig("weking.config.project.name");
        long time = LibDateUtils.getLibDateTime();
        String time_start = LibSysUtils.toString(time);
        String attach = LibSysUtils.toString(userId);
        String time_expire = timeExpire(); //三十分钟后结束交易
        String trade_type = "APP";
        //参数：开始生成签名
        SortedMap<Object, Object> parameters = new TreeMap<>();
        String aid = appid;
        String mid = mch_id;
        if(userId == 1008765){
            aid = "wxf65e958bde077156";
            mid = "1500208792";
        }
        parameters.put("appid", aid);
        parameters.put("mch_id", mid);
        parameters.put("nonce_str", nonce_str);
        parameters.put("body", body);
        parameters.put("detail", detail);
        parameters.put("attach", attach);
        parameters.put("out_trade_no", orderSn);
        parameters.put("total_fee", total_fee);
        parameters.put("time_start", time_start);
        parameters.put("time_expire", time_expire);
        parameters.put("notify_url", notify_url);
        parameters.put("trade_type", trade_type);
        parameters.put("spbill_create_ip", ip);
        String sign = createSign(userId,parameters);
        System.out.println("签名是：" + sign);

        Unifiedorder unifiedorder = new Unifiedorder();
        unifiedorder.setAppid(aid);
        unifiedorder.setMch_id(mid);
        unifiedorder.setNonce_str(nonce_str);
        unifiedorder.setSign(sign);
        unifiedorder.setBody(body);
        unifiedorder.setDetail(detail);
        unifiedorder.setAttach(attach);
        unifiedorder.setOut_trade_no(orderSn);
        unifiedorder.setTotal_fee(total_fee);
        unifiedorder.setSpbill_create_ip(ip);
        unifiedorder.setTime_start(time_start);
        unifiedorder.setTime_expire(time_expire);
        unifiedorder.setNotify_url(notify_url);
        System.out.println(notify_url);
        unifiedorder.setTrade_type(trade_type);
        //构造xml参数
        String xmlInfo = HttpXmlUtils.xmlInfo(unifiedorder);
        String wxUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        return HttpXmlUtils.httpsRequest(wxUrl, "POST", xmlInfo);
    }


    @SuppressWarnings("static-access")
    public static String getCash(String openids, String amounts, String ip) throws Exception {
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMddHHmmss");
        String number = myFmt.format(new Date()) + LibSysUtils.getRandomString(8);
        //参数组
        String mch_appid = "wx7e2a999d482c5c75";
        System.out.println("mch_id是：" + mch_appid);
        String mchid = "1390466502";
        String nonce_str = LibSysUtils.getRandomString(16);
        System.out.println("随机字符串是：" + nonce_str);
        String partner_trade_no = LibDateUtils.getLibDateTime() + LibSysUtils.getRandomString(2);
        String openid = openids;
        String check_name = "NO_CHECK";
        String re_user_name = "咔嚓用户";
        //单位是分，即是0.01元
        String amount = String.valueOf(Float.parseFloat(amounts) * 100);
        //amount = String.valueOf(Integer.parseInt(amounts) * 100);
        String desc = "咔嚓直播用户提现";

        String spbill_create_ip = ip;


        //参数：开始生成签名
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("mch_appid", mch_appid);
        parameters.put("mchid", mchid);
        parameters.put("nonce_str", nonce_str);
        parameters.put("partner_trade_no", partner_trade_no);
        parameters.put("openid", openid);
        parameters.put("check_name", check_name);
        parameters.put("re_user_name", re_user_name);
        parameters.put("amount", amount);
        parameters.put("desc", desc);
        parameters.put("spbill_create_ip", spbill_create_ip);


        String sign = createSign(0,parameters);
        log.info("sign:" + sign);
        System.out.println("签名是：" + sign);

        //构造xml参数
        String data = HttpXmlUtils.PayxmlInfo(mch_appid, mchid, nonce_str, partner_trade_no, openid, check_name, re_user_name, amount, desc, spbill_create_ip, sign);
        log.info("提现XML：" + data);
        String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File("C:/apiclient_cert.p12"));
        keyStore.load(instream, "1390466502".toCharArray());
        instream.close();
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, "1390466502".toCharArray()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        HttpPost httpost = new HttpPost(url); // 
        httpost.addHeader("Connection", "keep-alive");
        httpost.addHeader("Accept", "*/*");
        httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpost.addHeader("Host", "api.mch.weixin.qq.com");
        httpost.addHeader("X-Requested-With", "XMLHttpRequest");
        httpost.addHeader("Cache-Control", "max-age=0");
        httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
        httpost.setEntity(new StringEntity(data, "UTF-8"));
        CloseableHttpResponse response = httpclient.execute(httpost);
        HttpEntity entity = response.getEntity();
        String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
        EntityUtils.consume(entity);
        //PocketController.wexinMap.put(out_trade_no, total_fee);
        return jsonStr;
        //ParseXMLUtils.jdomParseXml(weixinPost);

    }

    /**
     * 微信支付签名算法sign
     */
    @SuppressWarnings("rawtypes")
    public static String createSign(int userId,SortedMap<Object, Object> parameters) {
        String ke = key;
        if(userId == 1008765){
            ke = "wekingtianxin66wekingtianxin8key";
        }
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + ke);
        return MethodUtil.MD5(sb.toString()).toUpperCase();
    }

    /*
     * 订单开始交易的时间
	 */
    public static String timeExpire() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, 1);
        return df.format(now.getTimeInMillis());
    }
}
