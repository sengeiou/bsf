package com.weking.controller.live;

import com.weking.cache.WKCache;
import com.weking.controller.out.OutControllerBase;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.service.live.VideoChatService;
import com.weking.service.user.UserService;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 视频聊天对外接收类
 * @author xujm
 */
@Controller
@RequestMapping({"/videoChat","/live/videoChat"})
public class VideoChatController extends OutControllerBase {

    @Resource
    private VideoChatService videoChatService;
    @Resource
    private UserService userService;

    @RequestMapping("/getChatList")
    public void getAnchorList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 10);
            int userId = object.optInt("user_id");
            object = userService.getAnchorList(userId, index, count);
        }
        out(response, object,api_version);
    }

    /**
     * 申请视频聊天
     */
    @RequestMapping("/apply")
    public void apply(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(accessToken);
        if(object.getInt("code")== ResultCode.success){
            int userId = object.getInt("user_id");
            String account = getParameter(request, "account");
            int type = getParameter(request,"type",2);
            String streamId = getParameter(request,"stream_id","");
            String langCode = object.optString("lang_code","");
            object = videoChatService.apply(userId,account,type,streamId,langCode);
        }
        this.out(response, object,api_version);
    }

    /**
     * 同意视频聊天
     */
    @RequestMapping("/accept")
    public void accept(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(accessToken);
        if(object.getInt("code")== ResultCode.success){
            int userId = object.getInt("user_id");
            int roomId = getParameter(request, "room_id",0);
            boolean accept = getParameter(request,"accept",false);
            String streamId = getParameter(request,"stream_id","");
            String langCode = object.optString("lang_code","");
            object = videoChatService.accept(userId,roomId,accept,streamId,langCode);
        }
        this.out(response, object,api_version);
    }

    /**
     * 结束视频聊天
     */
    @RequestMapping("/end")
    public void end(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(accessToken);
        if(object.getInt("code")== ResultCode.success){
            int userId = object.getInt("user_id");
            int roomId = getParameter(request, "room_id",0);
            object = videoChatService.end(userId,roomId,1);
        }
        this.out(response, object,api_version);
    }
    /**
     * 心跳
     */
    @RequestMapping("/heart")
    public void heart(HttpServletRequest request, HttpServletResponse response) {
        String account = getParameter(request, "account");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String otherAccount = getParameter(request, "other_account");
        //WKCache.set_room_heart(WkUtil.getUserIdByAccount(account), WkUtil.getUserIdByAccount(otherAccount));
        out(response, LibSysUtils.getResultJSON(0),api_version);
    }

    /**
     * 获得聊天时间
     */
    @RequestMapping("/getChatTime")
    public void getChatTime(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(accessToken);
        if(object.getInt("code")== ResultCode.success){
            int userId = object.optInt("user_id",0);
            int roomId = getParameter(request,"room_id",0);
            String langCode = object.optString("lang_code");
            object = videoChatService.getChatTime(userId,roomId,langCode);
        }
        this.out(response, object,api_version);
    }

    /**
     * 发送视频聊天礼物
     */
    @RequestMapping("/sendVideoChatGift")
    public void sendVideoChatGift(HttpServletRequest request, HttpServletResponse response){
        String accessToken = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(accessToken);
        if(object.getInt("code")== ResultCode.success){
            int userId = object.optInt("user_id",0);
            int roomId = getParameter(request,"room_id",0);
            int giftId = getParameter(request,"gift_id",0);
            String langCode = object.optString("lang_code");
            object = videoChatService.sendVideoChatGift(userId,roomId,giftId,langCode);
        }
        this.out(response, object,api_version);
    }

    /**
     * 更新聊天配置
     */
    @RequestMapping("/editChatSystem")
    public void editChatSystem(HttpServletRequest request, HttpServletResponse response){
        String accessToken = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(accessToken);
        if(object.getInt("code")== ResultCode.success){
            int userId = object.optInt("user_id",0);
            int chatPrice = getParameter(request,"chat_price",0);
            String disturb = getParameter(request,"is_disturb");
            Boolean isDisturb = null;
            if(!LibSysUtils.isNullOrEmpty(disturb)){
                isDisturb = LibSysUtils.toBoolean(disturb);
            }
            String langCode = object.optString("lang_code");
            object = videoChatService.editChatSystem(userId,chatPrice,isDisturb,langCode);
        }
        this.out(response, object,api_version);
    }


}
