package com.weking.core;

import com.weking.cache.WKCache;
import com.weking.core.google.FCM;
import com.weking.core.google.Firebase;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 推送消息类
 */
public class PushMsg {

    static Logger logger = Logger.getLogger(PushMsg.class);
    /**
     * 推送单个用户
     */
    public static Boolean pushSingleMsg(String cid, JSONObject imObject){
        Boolean flag = false;
        if(!LibSysUtils.isNullOrEmpty(cid)){
            if(cid.length() == 32){
                flag = GeTuiUtil.pushMessageWithApn(imObject, cid);
            }else{
                try {
                    FCM.pushMsg(cid, imObject);
//                    Firebase.pushMsgToSingle(cid,imObject);
                }catch (Exception e){
                    logger.error(e.getMessage());
                }
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 推送单个用户(无通知栏)
     */
    public static Boolean pushNoApnMsg(String cid, JSONObject imObject){
        Boolean flag = false;
        if(!LibSysUtils.isNullOrEmpty(cid)){
            if(cid.length() == 32){
                flag = GeTuiUtil.pushMessageNoApn(imObject.toString(), cid);
            }else{
                try {
                    FCM.pushMsg(cid, imObject);
//                    Firebase.pushMsgToSingle(cid,imObject);
                }catch (Exception e){
                    logger.error(e.getMessage());
                }
                flag = true;
            }
        }

        return flag;
    }

    /**
     * 推送用户列表
     */
    public static void pushListMsg(Map<String, String> dataPayload,List<Map<String, Object>> list,String content){
        Set<String> cidSet = new HashSet<>();
        Set<String> fcmSet = new HashSet<>();
        for (Map<String, Object> aList : list) {
            String item = LibSysUtils.toString(aList.get("cid"));
            if(!LibSysUtils.isNullOrEmpty(item)){
                if(item.length() == 32){
                    cidSet.add(item);
                }else{
                    fcmSet.add(item);
                }
            }
        }
        if(cidSet.size() > 0){
            JSONObject json = JSONObject.fromObject(dataPayload);
            GeTuiUtil.pushMsgToListWithApn(json.toString(), cidSet, content, LibSysUtils.getLang("weking.lang.app.looking"),LibSysUtils.toString(json.get("pic_head_low")));
        }
        if(fcmSet.size() > 0){
            FCM.pushListMsg(dataPayload,fcmSet);
//            Firebase.pushMsgToList(dataPayload,fcmSet);
        }
    }

    /**
     * 发送系统消息
     */
    public static Boolean sendSystemMsg(JSONObject im_data,String msg,String lang_code,String project_name,String tag){
        boolean success;

        String senderNickname;
        JSONObject system_msg_name = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.system_msg_name));
        if (system_msg_name.size()!=0) {
            senderNickname = system_msg_name.optString(lang_code, "系统消息");
        }else {
            senderNickname = LibProperties.getLanguage(lang_code, "weking.lang.app.system.msg.name");
        }
        if(project_name.equals("yy")){
            //马甲包推送
            success = YyGeTuiUtil.pushMsgToApp(im_data.toString(),msg,senderNickname);
        }else{
            success = GeTuiUtil.pushMsgToApp(im_data.toString(), msg, senderNickname,tag);
            if (LibSysUtils.isNullOrEmpty(tag)){
                FCM.pushSystemTopicsMsg(im_data);
//                Firebase.pushSystemTopicsMsg(im_data);
            }else {
                Firebase.pushTopicMsg(tag,im_data);
            }
        }
        return success;
    }

    /**
     * 发送消息给所有用户
     * @param msgObject
     * @param msg
     * @param title
     * @param tag
     */
    public static void sendMsgToAllUser(JSONObject msgObject, String msg, String title, String tag) {
        GeTuiUtil.pushMsgToApp(msgObject.toString(), msg, title,tag);
//        FCM.pushSystemTopicsMsg(msgObject);
//        FCM.pushMsg("fYpguVe7Rzc:APA91bEucBj4LMSCH_wDEwRPCnHR2iBjIc4yVzX9iVu8dtBQj74NnN_PWIVoLdBQwi-TNNAE9MdXhHS2oo29qSQstrU9eJNz_U0De8dkNkUgYgC0M4HqsHJEFusBPLrDP9wYaEYtIYyE", result);
//        Firebase.pushMsgToSingle("fYpguVe7Rzc:APA91bEucBj4LMSCH_wDEwRPCnHR2iBjIc4yVzX9iVu8dtBQj74NnN_PWIVoLdBQwi-TNNAE9MdXhHS2oo29qSQstrU9eJNz_U0De8dkNkUgYgC0M4HqsHJEFusBPLrDP9wYaEYtIYyE", msgObject);
        Firebase.pushSystemTopicsMsg(msgObject);
    }

    public static JSONObject getPushJSONObject(int im_code,String lang_code,String titleKey,String contentKey){
        JSONObject pushObject = new JSONObject();
        if (im_code != 0){
            pushObject.put("im_code", LibSysUtils.toString(im_code));
            pushObject.put("title",LibProperties.getLanguage(lang_code,titleKey));
            pushObject.put("nickname",LibProperties.getLanguage(lang_code,titleKey));
            pushObject.put("message",LibProperties.getLanguage(lang_code,contentKey));
        }
        return pushObject;
    }

    /**
     * 发送系统消息
     */
    public static void sendSystemMsgWithLang(int im_code,String titleKey,String contentKey){
        String langStr = LibSysUtils.toString(WKCache.get_system_cache(C.WKSystemCacheField.app_langage_kind));
        if (!LibSysUtils.isNullOrEmpty(langStr)){
            String[] langArray = langStr.split(",");
            for (String lang : langArray){
                if (!LibSysUtils.isNullOrEmpty(lang)){
                    sendSystemMsgWithLang(im_code,titleKey,contentKey,lang);
                }
            }
        }
    }

    /**
     * 发送系统消息
     */
    public static Boolean sendSystemMsgWithLang(int im_code,String titleKey,String contentKey,String tag){
        JSONObject im_data = getPushJSONObject(im_code,tag,titleKey,contentKey);
        GeTuiUtil.pushMsgToApp(im_data.toString(), im_data.optString("message"), im_data.optString("title"),tag);
        Firebase.pushTopicMsg(tag,im_data);
        return true;
    }


    public static void main(String[] args) {
        pushSingleMsg("fYpguVe7Rzc:APA91bEucBj4LMSCH_wDEwRPCnHR2iBjIc4yVzX9iVu8dtBQj74NnN_PWIVoLdBQwi-TNNAE9MdXhHS2oo29qSQstrU9eJNz_U0De8dkNkUgYgC0M4HqsHJEFusBPLrDP9wYaEYtIYyE",
                JSONObject.fromObject("{\"message_id\":\"6abcb073-211a-4f76-9b76-2f50ab9f0b19\",\"state\":0,\"message\":\"啦啦哈哈\",\"age\":0,\"business_type\":0,\"sex\":0,\"time\":20181018212027,\"receive_account\":\"711290275\",\"account\":\"674281078\",\"auth_state\":1,\"head_url\":\"http://t.uc1.me/avatar/2018/10/15/15395677561185ukJ.jpeg\",\"nickname\":\"Jc-9527\",\"im_code\":1100,\"pic_head_low\":\"http://t.uc1.me/avatar/2018/10/15/15395677561185ukJ.jpeg!w200\"}"));
    }


}
