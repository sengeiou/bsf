package com.weking.core.google;

import com.wedevol.xmpp.bean.CcsOutMessage;
import com.wedevol.xmpp.server.CcsClient;
import com.wedevol.xmpp.server.MessageHelper;
import com.wedevol.xmpp.util.Util;
import com.weking.cache.WKCache;
import com.weking.core.IMCode;
import com.weking.core.WkUtil;
import com.weking.core.enums.UploadTypeEnum;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.jivesoftware.smack.XMPPException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/8/17.
 */
public class FCM {

    private static volatile String fcmProjectSenderId;
    private static volatile String fcmServerKey;
    private static volatile Boolean debuggable = false;
    private static volatile CcsClient ccsClient;

    static {
        if(fcmProjectSenderId == null && fcmServerKey == null){
            String fcmData = WKCache.get_system_cache("google.fcm");
            if(fcmData != null){
                JSONObject fcmObj = JSONObject.fromObject(fcmData);
                fcmProjectSenderId = fcmObj.optString("sender_id");
                fcmServerKey = fcmObj.optString("server_key");
            }
        }
    }

    public static void init(){
        if(ccsClient == null){
            ccsClient = connect();
        }
    }

    /**
     * 推送消息(通知栏)
     */
    public static void pushMsg(String toRegId,Map<String, String> dataPayload){
        init();
        String messageId = Util.getUniqueMessageId();
        CcsOutMessage message = new CcsOutMessage(toRegId, messageId, dataPayload);
        String jsonRequest = MessageHelper.createJsonOutMessage(message);
        ccsClient.send(jsonRequest);
    }

    /**
     * 推送系统主题消息(通知栏)
     */
    public static void pushSystemTopicsMsg(Map<String, String> dataPayload){
        init();
        String messageId = Util.getUniqueMessageId();
        CcsOutMessage message = new CcsOutMessage("/topics/system", messageId, dataPayload);
        String jsonRequest = MessageHelper.createJsonOutMessage(message);
        ccsClient.send(jsonRequest);

    }

    /**
     * 推送列表消息(通知栏)
     */
    public static void pushListMsg(Map<String, String> dataPayload, Set<String> recipients){
        init();
        CcsOutMessage message = new CcsOutMessage("aa", "aa", dataPayload);
        ccsClient.sendBroadcast(message,recipients);
    }

    private static CcsClient connect(){
        CcsClient ccsClient = CcsClient.prepareClient(fcmProjectSenderId, fcmServerKey, debuggable);
        try {
            ccsClient.connect();
        } catch (XMPPException e) {
            ccsClient = null;
            e.printStackTrace();
        }
        return ccsClient;
    }



    public static void main(String[] args){
//        String fcmProjectSenderId = "812535728488";
//        String fcmServerKey = "AAAAvS7nSWg:APA91bHbVG1XUUzb1t4B6mqJAqozSaX2OZtISWoCJcROoyn-EMEniebPSTmWbWqWJB2j9bRacYvnw-XS9gmSJUNEEXRr7mZBN_J87xuNxeSYMuCRU0HwPaEavoE8rdiMJHdzjrCLV0tc";
//        CcsClient ccsClient = CcsClient.prepareClient(fcmProjectSenderId, fcmServerKey, debuggable);
//        try {
//            ccsClient.connect();
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        }        CcsClient ccsClient = connect();
//        String toRegId = "f_R0O2QI18o:APA91bFFfPPfsHqelB54LpEiyY1rAF4etXzRQ3_i0seq6SvE0S4bjf7mOmbUFy0_unQbFxI4i4Jr6p4eJFkNjEFph-AbI6v_CkkHyAnPnV2MbD3JHbqUCmMRKsQtIbhvuDIaSpO4mQCq";
//        String messageId = Util.getUniqueMessageId();
//        Map<String, String> dataPayload = new HashMap<String, String>();
//        dataPayload.put("store_id", "1");
//        dataPayload.put("account", "9852634");
//        dataPayload.put("pic_head_low", "http://baidu.com");
//        dataPayload.put("nickname", "李唐");
//        dataPayload.put("live_id", "12345");
//        dataPayload.put("live_stream_id", "1235_565656");
//        dataPayload.put("live_type", "1");
//        dataPayload.put("im_code", "1000");
//        CcsOutMessage message = new CcsOutMessage(toRegId, messageId, dataPayload);
//        String jsonRequest = MessageHelper.createJsonOutMessage(message);
//        ccsClient.send(jsonRequest);

        Map<String, String> dataPayload = new HashMap<>();
        dataPayload.put("account", "0");
        dataPayload.put("age", "0");
        dataPayload.put("auth_state", "1");
        dataPayload.put("business_type", "0");
        dataPayload.put("chatMsgStatus", "70");
        dataPayload.put("head_url", WkUtil.combineUrl("system_avatar.png", UploadTypeEnum.AVATAR,true));
        dataPayload.put("im_code", LibSysUtils.toString(IMCode.send_chat));
        dataPayload.put("message", "系统消息测试");
        dataPayload.put("message_id", java.util.UUID.randomUUID().toString());
        dataPayload.put("nickname", "系统消息");
        dataPayload.put("receive_account", "all");
        dataPayload.put("sex", "0");
        dataPayload.put("source_type", "1");
        dataPayload.put("time", LibSysUtils.toString(LibDateUtils.getLibDateTime()));
        pushSystemTopicsMsg(dataPayload);

    }


}
