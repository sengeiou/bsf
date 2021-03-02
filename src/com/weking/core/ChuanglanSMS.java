package com.weking.core;

import com.weking.cache.WKCache;
import com.weking.controller.system.SystemController;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class ChuanglanSMS {

    private static Logger loger = Logger.getLogger(ChuanglanSMS.class);

    public static boolean SendMsg(String areaCode, String phone, String content) throws Exception {
        boolean flag = false;
        try {
            if ("86".equals(areaCode)) {
                String str = sendLocalMsg(phone, content);
                System.out.println("----------str:" + str);
                if (str.contains("\n")) {
                    flag = true;
                }
            } else {
                CloseableHttpResponse response = sendInternationalMsg(areaCode + phone, content);
                if (response != null && response.getStatusLine().getStatusCode() == 200) {
                    String str = EntityUtils.toString(response.getEntity());
                    JSONObject array = JSONObject.fromObject(str);
                    if (array.getBoolean("success")) {
                        flag = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return flag;
    }

    public static CloseableHttpResponse sendInternationalMsg(String phone, String content) {
        String account = WKCache.get_system_cache("chuanglan.international.username");
        String password = WKCache.get_system_cache("chuanglan.international.password");
        String international_url = "http://222.73.117.140:8044/mt";
        String encodedContent = null;
        try {
            encodedContent = URLEncoder.encode(content, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        StringBuffer strBuf = new StringBuffer(international_url);
        strBuf.append("?un=").append(account);
        strBuf.append("&pw=").append(password);
        strBuf.append("&da=").append(phone);
        strBuf.append("&sm=").append(encodedContent);
        strBuf.append("&dc=15&rd=1&rf=2&tf=3");
        HttpGet get = new HttpGet(strBuf.toString());
        try {
            return HttpClients.createDefault().execute(get);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param phone 手机号码，多个号码使用","分割
     * @param msg   短信内容
     * @throws Exception
     */
    public static String sendLocalMsg(String phone, String msg) throws Exception {
        String url = "http://sms.253.com/msg/";// 应用地址
        System.out.println("----------msg:" + msg);
        String un = WKCache.get_system_cache("chuanglan.batch.username");
        System.out.println("----------un:" + un);
        String pw = WKCache.get_system_cache("chuanglan.batch.password");
        System.out.println("----------pw:" + pw);
        HttpClient client = new HttpClient(new HttpClientParams(), new SimpleHttpConnectionManager(true));
        GetMethod method = new GetMethod();
        try {
            URI base = new URI(url, false);
            method.setURI(new URI(base, "send", false));
            method.setQueryString(new NameValuePair[]{
                    new NameValuePair("un", un),
                    new NameValuePair("pw", pw),
                    new NameValuePair("phone", phone),
                    new NameValuePair("rd", "1"),//是否需要状态报告，需要1，不需要0
                    new NameValuePair("msg", msg),
                    new NameValuePair("ex", null),
            });
            int result = client.executeMethod(method);
            if (result == HttpStatus.SC_OK) {
                InputStream in = method.getResponseBodyAsStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                return URLDecoder.decode(baos.toString(), "UTF-8");
            } else {
                throw new Exception("HTTP ERROR Status: " + method.getStatusCode() + ":" + method.getStatusText());
            }
        } finally {
            method.releaseConnection();
        }
    }

    //手机号
    public static boolean checkPhone(String phone){
        boolean flag = false;
        String un = WKCache.get_system_cache("chuanglan.international.username");
        String pw = WKCache.get_system_cache("chuanglan.international.password");
        if(LibSysUtils.isNullOrEmpty(un)){
            return true;
        }
        String result = "";
        try {
            result = WkUtil.sendGet("http://222.73.117.140:8044/validate","account="+un+"&password="+pw+"&mobile="+phone);
            JSONObject resObj = JSONObject.fromObject(result);
            if(resObj.optInt("status") == 0){
                flag = true;
            }
        }catch (Exception e){
            flag = true;
            loger.error("result:"+result+"==="+"phone:"+phone+"==="+e.getMessage());
        }
        return flag;
    }

    /**
     * 验证手机是否羊毛党
     */
    public static boolean verifyValidMobile(String mobile){
        //String httpsUrl ="http://fym.253.com/api/startCall";
        String httpsUrl ="https://fym.253.com/api/startCall";
        String appId ="18032811545310038";
        String response = null;
        try {
            response = UtilHttpRequest.post(httpsUrl)
                    .put("mobile", mobile) // 手机号
                    .put("appId",appId)   //appId,请注意大小写
                    //.put("ip","103.201.25.64")          //手机号的ip地址
                    .send();
            JSONObject jsonObject = JSONObject.fromObject(response);
            if(jsonObject.optInt("retCode",0) == 0000){
                JSONObject data = jsonObject.getJSONObject("data");
                switch (data.getString("status")){
                    case "W1":
                        break;
                    case "W2":
                        break;
                    case "B1":
                        break;
                    case "B2":
                        break;
                    case "N":
                        break;
                    default:
                     break;
                }
            }
            loger.error("verifyValidMobile:"+response);
            return false;
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | IOException | KeyManagementException | CertificateException | KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    public  static void main(String[] a){

        checkPhone("60166271551");
    }
}
