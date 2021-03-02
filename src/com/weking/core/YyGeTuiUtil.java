package com.weking.core;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.em.EPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.weking.cache.WKCache;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class YyGeTuiUtil {

    private static volatile String appId;
    private static volatile String appKey;
    private static volatile String masterSecret;
    private static volatile String url;
    private static volatile IGtPush pusher;

    static {
        String geTuiInfo = WKCache.get_system_cache("yy.getui.info");
        JSONObject infoObj = JSONObject.fromObject(geTuiInfo);
        appId = infoObj.getString("appId");
        appKey = infoObj.getString("appKey");
        masterSecret = infoObj.getString("masterSecret");
        url = WKCache.get_system_cache("weking.config.getui.url");
        pusher = new IGtPush(url, appKey, masterSecret);
    }

    /**
     * 没有apn的推送模板
     *
     * @param contentJson 透传的消息内容 json格式
     * @return TransmissionTemplate
     */
    private static TransmissionTemplate transmissionTemplateNoApn(String contentJson) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(2);
        template.setTransmissionContent(contentJson);
        // 设置定时展示时间
        //template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");
        return template;
    }

    /**
     * 带有apn的推送模板
     *
     * @param transmissionContent 透传的消息内容 json格式
     * @param apnTitle            apn的title
     * @param apnBody             apn的内容
     * @return 模板
     */
    private static TransmissionTemplate transmissionTemplateWithApn(String transmissionContent, String apnTitle, String apnBody) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(transmissionContent);
        template.setTransmissionType(2);
        APNPayload payload = new APNPayload();
        payload.addCustomMsg("result", transmissionContent);
        //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
        payload.setAutoBadge("1");
        payload.setContentAvailable(1);
        payload.setSound("default");
        payload.setCategory("$由客户端定义");

        //apn高级推送
        APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
        ////通知文本消息标题
        alertMsg.setTitle(apnTitle);
        //通知文本消息字符串
        alertMsg.setBody(apnBody);
        //对于标题指定执行按钮所使用的Localizable.strings,仅支持IOS8.2以上版本
        alertMsg.setTitleLocKey(apnTitle);

        payload.setAlertMsg(alertMsg);

        template.setAPNInfo(payload);
        return template;
    }

    /**
     * 推送给app所有用户
     *
     * @param messageJson 透传的消息内容 json格式
     * @param context     apn 内容
     * @return 是否成功
     */
    public static boolean pushMsgToApp(String messageJson, String context,String title) {
        TransmissionTemplate template = transmissionTemplateWithApn(messageJson, title, context);
        AppMessage message = new AppMessage();
        message.setData(template);

        message.setOffline(true);
        //离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 1000 * 3600); // 24小时
        //推送给App的目标用户需要满足的条件
        AppConditions cdt = new AppConditions();
        List<String> appIdList = new ArrayList<>();
        appIdList.add(appId);
        message.setAppIdList(appIdList);
        //手机类型
        List<String> phoneTypeList = new ArrayList<>();
        //省份
        List<String> provinceList = new ArrayList<>();
        //自定义tag
        List<String> tagList = new ArrayList<>();

        cdt.addCondition(AppConditions.PHONE_TYPE, phoneTypeList);
        cdt.addCondition(AppConditions.REGION, provinceList);
        cdt.addCondition(AppConditions.TAG, tagList);
        message.setConditions(cdt);

        IPushResult ret = pusher.pushMessageToApp(message, "任务别名_toApp");

        System.out.println(ret.getResponse().toString());

        return ret.getResultCode() == EPushResult.RESULT_OK;
    }


    /**
     * 无apn的透传推送
     *
     * @param contextJson 透传的消息内容 json格式
     * @param cid         cid
     * @return 是否成功
     */
    public static boolean pushMessageNoApn(String contextJson, String cid) {
        TransmissionTemplate template = transmissionTemplateNoApn(contextJson);
        return pushMsgToSingle(template, cid);
    }

    /**
     * 有apn的透传推送
     *
     * @param contextJson 透传的消息内容 json格式
     * @param cid         cid
     * @param context     apn的消息字符串
     * @param title       apn的title
     * @return 是否成功
     */
    public static boolean pushMessageWithApn(String contextJson, String cid, String context, String title) {
        TransmissionTemplate template = transmissionTemplateWithApn(contextJson, title, context);
        return pushMsgToSingle(template, cid);
    }

    /**
     * 推送给cid对应的用户
     *
     * @param template 消息模板
     * @param cid      cid
     * @return 是否成功
     */
    private static Boolean pushMsgToSingle(TransmissionTemplate template, String cid) {

        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 3600 * 1000);
        message.setData(template);
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0);
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(cid);//设置客户端唯一编号
        //target.setAlias(Alias);
        IPushResult ret;
        try {
            ret = pusher.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            e.printStackTrace();
            ret = pusher.pushMessageToSingle(message, target, e.getRequestId());
        }
        Boolean flag = false;
        if (ret != null) {
            Map<String, Object> map = ret.getResponse();
            if (map.get("result").equals("ok")) {
                if (map.get("status").equals("successed_online")) {
                    flag = true;
                }
            }else {
                System.out.println(map.get("result"));
            }
        }
        return flag;
    }

    /**
     * 部分用户推送
     *
     * @param Msg     透传的消息内容 json格式
     * @param cIds    cid集合
     * @param context apn 内容
     * @param title   apn 标题
     */
    public static void pushMsgToListWithApn(String Msg, Collection<String> cIds, String context, String title) {
        List<Target> targets = new ArrayList<>();
        // 通知透传模板
        TransmissionTemplate template = transmissionTemplateWithApn(Msg, title, context);
        ListMessage message = new ListMessage();
        message.setData(template);
        // 设置消息离线，并设置离线时间
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 1000 * 3600);
        for (String cId : cIds) {
            Target target = new Target();
            target.setAppId(appId);
            target.setClientId(cId);
            targets.add(target);
        }
        String taskId = pusher.getContentId(message);
        pusher.pushMessageToList(taskId, targets);
    }
}
