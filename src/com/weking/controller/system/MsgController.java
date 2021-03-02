package com.weking.controller.system;

import com.weking.cache.WKCache;
import com.weking.core.C;
import com.weking.core.IMCode;
import com.weking.core.WkUtil;
import com.weking.service.system.MsgService;
import com.wekingframework.comm.LibControllerBase;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO 2017/2/20.
 */
@Controller
@RequestMapping({"/msg", "/system/msg"})
public class MsgController extends LibControllerBase {

    @Resource
    private MsgService msgService;


    //发送系统消息
    @RequestMapping("/sendSysMsgToApp")
    public void sendSysMsg2all(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject result = WkUtil.checkToken(access_token);
        String msg = getParameter(request, "msg");
        String project_name = getParameter(request, "project_name", "");
        String title = getParameter(request, "title");
        String link_url = getParameter(request, "link_url");
        String pic_url = getParameter(request, "pic_url");
        int type = getParameter(request, "type",0);
        if(C.ManagerSysAccessToken.equals(access_token)) {
            result = msgService.sendMsgToApp(msg,title,link_url,pic_url,type,project_name);
        }
        out(response, result);
    }


    //发送系统消息
    @RequestMapping("/sendSysMsg")
    public void sendSysMsg(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = new JSONObject();
        if (C.ManagerSysAccessToken.equals(access_token)) {
            String account = getParameter(request, "account");
            String msg = getParameter(request, "msg");
            String title = getParameter(request, "title");
            String link_url = getParameter(request, "link_url");
            String pic_url = getParameter(request, "pic_url");
            int type = getParameter(request, "type",0);
            object = msgService.sendSysMsg(account, msg, title, link_url, pic_url,type);
        }
        out(response, object);
    }

    //发送消息
    @RequestMapping("/sendMsg")
    public void sendMsg(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            String nickname = object.optString("nickname");
            String to_id = getParameter(request, "to_id");
            String im_data = getParameter(request, "im_data");
            int level = object.optInt("level", 1);
            JSONObject imObject = JSONObject.fromObject(im_data);
            imObject.put(C.ImField.im_code, IMCode.send_chat);
            im_data = imObject.toString();
            object = msgService.sendMsg(userId, nickname, level, to_id, im_data, true, lang_code);
        }
        out(response, object);
    }

    //聊天列表
    @RequestMapping("/chatList")
    public void chatList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            int type = getParameter(request, "type", 1);
            object = msgService.chatList(userId, type, lang_code);
        }
        out(response, object);
    }

    //删除聊天用户
    @RequestMapping("/delChatUser")
    public void delChatUser(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            String account = getParameter(request, "account");
            object = msgService.delChatUser(userId, account, lang_code);
        }
        out(response, object);
    }

    /**
     * 更新私信已读
     */
    @RequestMapping("/setChatState")
    public void setChartState(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            String account = getParameter(request, "account");
            String message_id = getParameter(request, "message_id");
            object = msgService.setChatStatic(userId, account, message_id, lang_code);
        }
        out(response, object);
    }

    /**
     * 更新系统消息状态
     */
    @RequestMapping("/setMsgState")
    public void setMsgState(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("userId");
            String message_id = getParameter(request, "message_id");
            object = msgService.updateMsgState(userId, message_id);
        }
        out(response, object);
    }

    /**
     *  直播通知推送
     */
    @RequestMapping("/liveNoticeAll")
    public void liveNoticeAll(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = new JSONObject();
        if (C.ManagerSysAccessToken.equals(access_token)) {
            int live_id = getParameter(request, "live_id",0);
            String title = getParameter(request, "title","");
            String channel = getParameter(request, "channel","");
            String msg = getParameter(request, "msg","");
            object = msgService.liveNoticeAll(live_id,title,msg,channel);
        }
        out(response, object);
    }


    /**
     *  電商商品通知推送
     */
    @RequestMapping("/shopGoodsPush")
    public void shopGoodsPush(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = new JSONObject();
        if (C.ManagerSysAccessToken.equals(access_token)) {
            int goods_id = getParameter(request, "goods_id",0);
            int goods_commonid = getParameter(request, "goods_commonid",0);
            String title = getParameter(request, "title","");
            String msg = getParameter(request, "msg","");
            object = msgService.shopGoodsPush(goods_id,goods_commonid,title,msg);
        }
        out(response, object);
    }

}
