package com.weking.controller.live;

import com.weking.controller.out.OutControllerBase;
import com.weking.core.WkUtil;
import com.weking.service.live.InviteService;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Xujm
 */
@Controller
@RequestMapping({"/invite","/live/invite"})
public class InviteController extends OutControllerBase {

    @Resource
    private InviteService inviteService;

    @RequestMapping("/list")
    public void list(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int type = getParameter(request,"type",1);
            int classId = getParameter(request,"class_id",0);
            int index = getParameter(request,"index",0);
            int count = getParameter(request,"count",10);
            String langCode = object.optString("lang_code");
            object = inviteService.list(userId,classId,type,index,count,langCode);
        }
        out(response, object,api_version);
    }

    @RequestMapping("/myAppointmentList")
    public void myAppointmentList(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int state = getParameter(request,"state",-1);
            String langCode = object.optString("lang_code");
            int index = getParameter(request,"index",0);
            int count = getParameter(request,"count",10);
            object = inviteService.myAppointmentList(userId,state,index,count,langCode);
        }
        out(response, object,api_version);
    }


    @RequestMapping("/myInviteList")
    public void myInviteList(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String account = getParameter(request,"account");
            String langCode = object.optString("lang_code");
            int index = getParameter(request,"index",0);
            int count = getParameter(request,"count",10);
            object = inviteService.myInviteList(userId,account,index,count,langCode);
        }
        out(response, object,api_version);
    }

    @RequestMapping("/getInviteClassList")
    public void getInviteClassList(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String langCode = getParameter(request,"lang_code",object.optString("lang_code"));
            object = inviteService.getInviteClassList(userId, langCode);
        }
        out(response, object,api_version);
    }

    @RequestMapping("/release")
    public void release(HttpServletRequest request, HttpServletResponse response){
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int classId = getParameter(request,"class_id",0);
            int sex = getParameter(request,"sex",0);
            String city = getParameter(request,"city");
            int buyWay = getParameter(request,"buy_way",0);
            int sincerity = getParameter(request,"sincerity",0);
            String remark = getParameter(request,"remark");
            long startTime = getParameter(request,"start_time",0L);
            long endTime = getParameter(request,"end_time",0L);
            double lng = getParameter(request,"lng",0D);
            double lat = getParameter(request,"lat",0D);
            String invitePicture = getParameter(request,"invite_picture");
            String langCode = getParameter(request,"lang_code",object.optString("lang_code"));
            object = inviteService.release(userId,classId,sex,city,buyWay,sincerity
            ,remark,startTime,endTime,lng,lat,invitePicture,langCode);
        }
        out(response, object,api_version);
    }

    @RequestMapping("/detail")
    public void detail(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String langCode = object.optString("lang_code");
            int inviteId = getParameter(request,"invite_id",0);
            object = inviteService.detail(userId,inviteId,langCode);
        }
        out(response, object,api_version);
    }

    /**
     * 应邀
     */
    @RequestMapping("/appoint")
    public void appoint(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String langCode = object.optString("lang_code");
            int inviteId = getParameter(request,"invite_id",0);
            object = inviteService.appoint(userId,inviteId,langCode);
        }
        out(response, object,api_version);
    }

    /**
     * 应邀
     */
    @RequestMapping("/appointUserList")
    public void appointUserList(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int inviteId = getParameter(request,"invite_id",0);
            int index = getParameter(request,"index",0);
            int count = getParameter(request,"count",10);
            object = inviteService.appointUserList(userId,inviteId,index,count);
        }
        out(response, object,api_version);
    }

    /**
     * 更新邀约订单状态
     */
    @RequestMapping("/updateAppointState")
    public void updateAppointState(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int appointId = getParameter(request,"appoint_id",0);
            int state = getParameter(request,"state",0);
            String langCode = object.optString("lang_code");
            boolean isBuy = getParameter(request,"is_buy",false);
            object = inviteService.updateState(userId,appointId,state,isBuy,langCode);
        }
        out(response, object,api_version);
    }

    /**
     * 约会详情
     */
    @RequestMapping("/appointDetail")
    public void appointDetail(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int appointId = getParameter(request,"appoint_id",0);
            String langCode = object.optString("lang_code");
            object = inviteService.getAppointDetail(userId,appointId,langCode);
        }
        out(response, object,api_version);
    }

    /**
     * 删除约会
     */
    @RequestMapping("/delAppoint")
    public void delAppoint(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int appointId = getParameter(request,"appoint_id",0);
            String langCode = object.getString("lang_code");
            object = inviteService.delAppoint(userId,appointId,langCode);
        }
        out(response, object,api_version);
    }

    /**
     * 删除邀约
     */
    @RequestMapping("/delInvite")
    public void delInvite(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int appointId = getParameter(request,"invite_id",0);
            String langCode = object.getString("lang_code");
            object = inviteService.delInvite(userId,appointId,langCode);
        }
        out(response, object,api_version);
    }

    /**
     * 点赞
     */
    @RequestMapping("/like")
    public void like(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int inviteId = getParameter(request,"invite_id",0);
            boolean isLike = getParameter(request,"is_like",false);
            String langCode = object.getString("lang_code");
            object = inviteService.like(userId,inviteId,isLike,langCode);
        }
        out(response, object,api_version);
    }

    @RequestMapping("/editInvite")
    public void editInvite(HttpServletRequest request, HttpServletResponse response){
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int classId = getParameter(request,"class_id",0);
            int sex = getParameter(request,"sex",0);
            String city = getParameter(request,"city");
            int buyWay = getParameter(request,"buy_way",0);
            int sincerity = getParameter(request,"sincerity",0);
            String remark = getParameter(request,"remark");
            long startTime = getParameter(request,"start_time",0L);
            long endTime = getParameter(request,"end_time",0L);
            double lng = getParameter(request,"lng",0D);
            double lat = getParameter(request,"lat",0D);
            int inviteId = getParameter(request,"invite_id",0);
            String invitePicture = getParameter(request,"invite_picture");
            String langCode = getParameter(request,"lang_code",object.optString("lang_code"));
            object = inviteService.editInvite(userId,inviteId,classId,sex,city,buyWay,sincerity
                    ,remark,startTime,endTime,lng,lat,invitePicture,langCode);
        }
        out(response, object,api_version);
    }

}
