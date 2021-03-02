package com.weking.controller.live;

import com.weking.cache.WKCache;
import com.weking.controller.out.OutControllerBase;
import com.weking.core.C;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.service.live.LiveService;
import com.weking.service.system.RobotService;
import com.weking.service.user.TaskService;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/2/21.
 */
@Controller
@RequestMapping("/live")
public class LiveController extends OutControllerBase {

    static Logger loger = Logger.getLogger(LiveController.class);
    @Resource
    private LiveService liveService;
    @Resource
    private TaskService taskService;
    @Resource
    private RobotService robotService;

    @RequestMapping("/checkLivePrivilege")
    public void checkLivePrivilege(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String lang_code = result.optString("lang_code");
            String project_name = getParameter(request, "project_name", "");
            String channel = getParameter(request, "channel", "");
            double version = LibSysUtils.toDouble(getParameter(request, "version"),1.0);
            result = liveService.checkLivePrivilege(user_id, project_name, lang_code,channel,version);
        }
        out(response, result,api_version);
    }

    @RequestMapping("/startLive")
    public void startLive(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String account = result.optString("account");
            String nickname = result.optString("nickname", "");
            String avatar = result.optString("avatar", "");
            String lang_code = result.optString("lang_code");
            long startTime = LibDateUtils.getLibDateTime();
            String live_stream_id = getParameter(request, "live_stream_id", "");
            loger.info("----------startLive:account="+account+" live_stream_id=:"+live_stream_id+" startTime=:"+startTime);
            String live_title = getParameter(request, "live_title", "");
            String live_cover = getParameter(request, "live_cover", "");
            int game_category_id = getParameter(request, "game_category_id", 0);
            double longitude = LibSysUtils.toDouble(getParameter(request, "longitude"), 0);
            double latitude = LibSysUtils.toDouble(getParameter(request, "latitude"), 0);
            String live_tag_name = getParameter(request, "live_tag_name", "");
            int live_type = getParameter(request, "live_type", 0);//直播类型，0：公开播，10：付费播；11：私密播 20电商 30游戏 40节目 50 攝像頭直播
            int live_ticket = getParameter(request, "live_ticket", 0);//直播类型为付费播时的门票价格
            String live_pwd = getParameter(request, "live_pwd", "");
            String city = getParameter(request, "city", "");
            int game_type = getParameter(request, "game_type", 0);
            String project_name = getParameter(request, "project_name", "");
            String channel = getParameter(request, "channel", "");
            Boolean is_horizontal = getParameter(request, "is_horizontal", false);
            Boolean is_official_live = getParameter(request, "is_official_live", false);
            //double api_version = getParameter(request, "api_version", 1.0);
            String program_slogan = getParameter(request,"program_slogan","");  // 节目直播口号
         /*   live_stream_id="123456789"+"_"+System.currentTimeMillis();*/
            if (live_stream_id.contains(account)){
                String activity_ids = getParameter(request,"activity_ids","");  //开始直播选择的活动
                if (live_type!=12) {
                    live_stream_id = getLive_stream_id(account);
                }
//            System.out.println("program_slogan : "+program_slogan);
                result = liveService.startLive(user_id, account, avatar, nickname, live_stream_id, live_type, live_ticket, live_pwd,
                        live_title, live_cover, longitude, latitude, city, live_tag_name, project_name, is_horizontal, game_category_id,
                        program_slogan,lang_code, api_version,channel,is_official_live,activity_ids);
            }

        }
        out(response, result,api_version);
    }

    @RequestMapping("/endLive")
    public void endLive(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            int live_id = getParameter(request, "live_id", 0);
            int end_type = getParameter(request, "end_type", 0);
            String account = result.optString("account");
            result = liveService.endLive(user_id, account, live_id, end_type);
        }
        out(response, result,api_version);
    }

    @RequestMapping("/forceeEndLive")
    public void forceeEndLive(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (access_token.equals("weking2016")) {
            String account = getParameter(request, "account");
            result = liveService.forceeEndLive(account);
        }
        out(response, result,api_version);
    }

    @RequestMapping("/heart")
    public void heart(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        int live_id = getParameter(request, "live_id", 0);
        live_id=0;
        if (live_id > 0) {
            SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMddHHmmss");
            String time = dfs.format(Calendar.getInstance().getTime());
            WKCache.add_room(live_id, "heart_time", time);
            loger.info("heart_time:live_id:" + live_id + ",time：" + time);
        }
        out(response, LibSysUtils.getResultJSON(0),api_version);
    }

    @RequestMapping("/getLiveList")
    public void getLiveList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result;
        int type = getParameter(request, "type", 0);
        int index = getParameter(request, "index", 0);
        int count = getParameter(request, "count", 10);
        String project_name = getParameter(request, "project_name", "");
        String type_value = getParameter(request, "type_value", "");
        String channel = getParameter(request, "channel", "");
        //double api_version = getParameter(request, C.RequestParam.api_version, 1.0);
        if(access_token!=null) {
            result = WkUtil.checkToken(access_token);
            if (result.optInt("code") == 0) {
                int user_id = result.optInt("user_id");
                String lang_code = result.optString("lang_code");
                result = liveService.getLiveList(user_id, type, type_value, project_name, index, count,
                        lang_code, false, false, api_version,channel);
            }
        }else {
            result = liveService.getLiveList(0, type, type_value, project_name, index, count,
                    "zh_TW", false, false, api_version,channel);
        }
        out(response, result,api_version);
    }

    @RequestMapping("/robotEnter")
    public void robotinRoom(HttpServletRequest request, HttpServletResponse response) {
        String anchor_account = getParameter(request, "anchor_account");//主播的account
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        int live_id = getParameter(request, "live_id", 0);
        String live_stream_id = getParameter(request, "live_stream_id");
        robotService.in_room(live_id, live_stream_id, 0, "", "", "", 0);
        out(response, new JSONObject(),api_version);
    }

    @RequestMapping("/switchLive")
    public void switchLive(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String account = result.optString("account");
            int level = result.optInt("level", 0);
            int vip_level = result.optInt("vip_level", 0);
            String nickname = result.optString("nickname");
            String avatar = result.optString("avatar");
            String lang_code = result.optString("lang_code");

            int exit_live_id = getParameter(request, "exit_live_id", 0);
            String exit_live_stream_id = getParameter(request, "exit_live_stream_id");
            int enter_live_id = getParameter(request, "enter_live_id", 0);
            //double api_version = getParameter(request, "api_version", 1.0);
            result = liveService.switchLive(user_id, account, nickname, level, exit_live_id, exit_live_stream_id,
                    enter_live_id, avatar, lang_code, api_version,vip_level);
        }
        out(response, result,api_version);
    }

    @RequestMapping("/enter")
    public void inRoom(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        String account = "";
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String anchor_account = getParameter(request, "anchor_account");//主播的account
            int live_id = getParameter(request, "live_id", 0);
            String live_stream_id = getParameter(request, "live_stream_id");
            String lang_code = result.optString("lang_code");
            account = result.optString("account");
            String nickname = result.optString("nickname");
            String avatar = result.optString("avatar");
            int level = result.optInt("level", 0);
            int vip_level = result.optInt("vip_level",0);
           // double api_version = getParameter(request, "api_version", 1.0);
            String project_name = getParameter(request, "project_name", "");
            result = liveService.enter(user_id, account, avatar, nickname, level, live_id, live_stream_id, lang_code, false, api_version, project_name,vip_level);
        }
        out(response, result,api_version);
    }

    @RequestMapping("/exit")
    public void outRoom(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String nickname = result.optString("nickname");
            int live_id = getParameter(request, "live_id", 0);
            String live_stream_id = getParameter(request, "live_stream_id");
            String account = result.optString("account");
            int level = result.optInt("level", 0);
            result = liveService.exit(user_id, account, nickname, level, live_id, live_stream_id);
        }
        out(response, result,api_version);
    }

    //获得礼物列表
    @RequestMapping("/getGiftList")
    public void getGiftList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int live_id = getParameter(request, "live_id", 0);
            int liveType = getParameter(request, "live_type", 1);
            String lang_code = result.optString("lang_code");
            result = liveService.getGiftList(live_id, liveType, lang_code,api_version);
//            System.out.println(result.toString());
        }
        out(response, result,api_version);
    }

    //获得直播类别页的列表数据
    @RequestMapping("/getRecommendLive")
    public void getRecommendLive(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            String project_name = getParameter(request, "project_name", "");
            object = liveService.getRecommendLive(userId, project_name, lang_code);
        }
        out(response, object,api_version);
    }

    //获得录播记录
    @RequestMapping("/getLivePlaybackList")
    public void getLivePlaybackList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            String project_name = getParameter(request, "project_name", "");
            int userId = object.optInt("user_id");
            String account = getParameter(request, "account");
            String channel = getParameter(request, "channel","");
            int index = LibSysUtils.toInt(getParameter(request, "index", 0));
            int count = LibSysUtils.toInt(getParameter(request, "count", 10));

            String lang_code = object.optString("lang_code");
            object = liveService.getLivePlaybackList(userId, account, project_name, index, count, lang_code, false,channel);
        }
        out(response, object,api_version);
    }

    //删除直播记录
    @RequestMapping("/delLivePlayBack")
    public void delLivePlayBack(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int liveId = LibSysUtils.toInt(getParameter(request, "live_id"));
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            int status = getParameter(request, "status", 1);
            object = liveService.delLivePlayBack(userId, liveId, status, lang_code);
        }
        out(response, object,api_version);
    }

    //获得直播标签
    @RequestMapping("/getLiveTags")
    public void getLiveTags(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            object.remove("user_id");
            object.put("list", liveService.getLiveTags(userId));
        }
        out(response, object,api_version);
    }

    //发言
    @RequestMapping("/sendMsg")
    public void sendMsg(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        String referToAccount = getParameter(request,"referToAccount","");//@到账号account
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            int live_id = getParameter(request, "live_id", 0);
            String live_stream_id = getParameter(request, "live_stream_id");
            String msg = getParameter(request, "msg");
            String account = result.optString("account");//发言account
            String nickname = result.optString("nickname");//nickname
            String avatar = result.optString("avatar");//avatar
            boolean is_barrage = getParameter(request, "is_barrage", false);
            String lang_code = result.optString("lang_code");
            int level = result.optInt("level");
            int vip_level = result.optInt("vip_level");
            //double api_version = getParameter(request, "api_version", 1.0);
            result = liveService.sendMsg(user_id, account, nickname, avatar, level, live_id, live_stream_id, msg, is_barrage, lang_code, api_version, false,vip_level,referToAccount);
        }
        out(response, result,api_version);
    }

    //送礼物
    @RequestMapping("/sendGift")
    public void sendGift(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
         double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            int live_id = getParameter(request, "live_id", 0);
            int room_id = getParameter(request, "room_id", 0);
            int gift_id = getParameter(request, "gift_id", 0);
            int count_num = getParameter(request, "count_num", 1);
            int level = result.optInt("level");
            int vip_level = result.optInt("vip_level");
            String lang_code = result.optString("lang_code");
           // double api_version = getParameter(request, "api_version", 1.0);
            String account = result.optString("account");
            String nickname = result.optString("nickname");
            String avatar = result.optString("avatar");
            result = liveService.sendGift(user_id, level, account, nickname, avatar, live_id, room_id, gift_id, count_num, lang_code, api_version,vip_level);
        }
        out(response, result,api_version);
    }

    //主播回到后台
    @RequestMapping("/pauseLive")
    public void pauseLive(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            int live_id = getParameter(request, "live_id", 0);
            String live_stream_id = getParameter(request, "live_stream_id");
            String lang_code = result.optString("lang_code");
            String account = result.optString("account");
            result = liveService.pauseLive(user_id, account, live_id, live_stream_id, lang_code);
        }
        out(response, result,api_version);
    }

    //主播从后台回到前端
    @RequestMapping("/resumeLive")
    public void resumeLive(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            int live_id = getParameter(request, "live_id", 0);
            String live_stream_id = getParameter(request, "live_stream_id");
            String lang_code = result.optString("lang_code");
            String account = result.optString("account");
            result = liveService.resumeLive(user_id, account, live_id, live_stream_id, lang_code);
        }
        out(response, result,api_version);
    }

    //设置管理员
    @RequestMapping("/setManager")
    public void setManager(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            int live_id = getParameter(request, "live_id", 0);
            String live_stream_id = getParameter(request, "live_stream_id");
            String manager_account = getParameter(request, "manager_account");
            boolean authorization = getParameter(request, "authorization", true);
            String lang_code = result.optString("lang_code");
            String account = result.optString("account");
            result = liveService.setManager(live_id, live_stream_id, manager_account, authorization, lang_code);
        }
        out(response, result,api_version);
    }

    //获取管理员列表
    @RequestMapping("/getManagerList")
    public void getManagerList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        int type = getParameter(request, "type",0);
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            int live_id = getParameter(request, "live_id", 0);
            //String live_stream_id = getParameter(request, "live_stream_id");
            String lang_code = result.optString("lang_code");
            result = liveService.getManagerList(user_id, lang_code,type,live_id);
        }
        out(response, result,api_version);
    }


    //设置禁言
    @RequestMapping("/bannedPost")
    public void bannedPost(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            int live_id = getParameter(request, "live_id", 0);
            String live_stream_id = getParameter(request, "live_stream_id");
            String user_account = getParameter(request, "user_account");
            boolean forbid = getParameter(request, "forbid", true);
            String lang_code = result.optString("lang_code");
            String account = result.optString("account");
            result = liveService.bannedPost(live_id, live_stream_id, user_account, forbid, lang_code);
        }
        out(response, result,api_version);
    }

    //申请连麦
    @RequestMapping("/applyLink")
    public void applyLink(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int live_id = getParameter(request, "live_id", 0);
            int user_id = result.optInt("user_id");
            String account = result.optString("account");
            String nickname = result.optString("nickname");
            String lang_code = result.optString("lang_code");
            int level = result.optInt("level");
            result = liveService.applyLink(live_id, user_id, account, nickname, level, lang_code);
        }
        out(response, result,api_version);
    }

    //接受连麦
    @RequestMapping("/acceptLink")
    public void acceptLink(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int live_id = getParameter(request, "live_id", 0);
            Boolean accept = getParameter(request, "accept", false);
            int user_id = result.optInt("user_id");
            String account = result.optString("account");
            result = liveService.acceptLink(live_id, user_id, account, accept);
        }
        out(response, result,api_version);
    }

    //开始连麦
    @RequestMapping("/startLink")
    public void startLink(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int live_id = getParameter(request, "live_id", 0);
            String live_stream_id = getParameter(request, "live_stream_id");
            String link_live_stream_id = getParameter(request, "link_live_stream_id");
            int user_id = result.optInt("user_id");
            String account = result.optString("account");
            String nickname = result.optString("nickname");
            result = liveService.startLink(live_id, live_stream_id, link_live_stream_id, user_id, account, nickname);
        }
        out(response, result,api_version);
    }

    //结束连麦
    @RequestMapping("/endLink")
    public void endLink(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int live_id = getParameter(request, "live_id", 0);
            String live_stream_id = getParameter(request, "live_stream_id");
            String link_live_stream_id = getParameter(request, "live_stream_id");
            int user_id = result.optInt("user_id");
            String account = result.optString("account");
            String nickname = result.optString("nickname");
            result = liveService.endLink(live_id, live_stream_id, link_live_stream_id, user_id, account, nickname);
        }
        out(response, result,api_version);
    }

    //获取房间内的人员信息
    @RequestMapping("/getUserinfo")
    public void getUserinfo(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int live_id = getParameter(request, "live_id", 0);
            int user_id = result.optInt("user_id");
            String user_account = getParameter(request, "account");
            String lang_code = result.optString("lang_code");
            String account = result.optString("account");
            result = liveService.getUserinfo(live_id, user_id, account, user_account);
        }
        out(response, result,api_version);
    }

    //检查是否有权限进入付费观看的直播间
    @RequestMapping("/checkEnterPrivilege")
    public void checkEnterPrivilege(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String lang_code = result.optString("lang_code");
            Integer live_id = getParameter(request, "live_id", -1);
            int type = getParameter(request, "type", 0); //0是直播 1是录播
            //double api_version = getParameter(request, "api_version", 1.0);
            String project_name = getParameter(request, "project_name", "");
            String live_pwd = getParameter(request, "live_pwd", "");
            if (api_version>=4.3) {
                result = liveService.checkEnterPrivilegeAndVip(user_id, live_id, api_version, project_name, live_pwd, lang_code,type);
            }else {
                result = liveService.checkEnterPrivilegeAndVip(user_id, live_id, api_version, project_name, live_pwd, lang_code,type);

/*
                result = liveService.checkEnterPrivilege(user_id, live_id, api_version, project_name, live_pwd, lang_code);
*/
            }
        }
        out(response, result,api_version);
    }

    //购买付费观看的直播间门票
    @RequestMapping("/buyTicket")
    public void buyTicket(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String lang_code = result.optString("lang_code");
            int live_id = getParameter(request, "live_id", 0);
            int type = getParameter(request, "type", 0); //0是直播 1是录播
            result = liveService.buyTicket(user_id, live_id, false, lang_code,type);
        }
        out(response, result,api_version);
    }

    //获取直播的密码
    @RequestMapping("/getLivePwd")
    public void getLivePwd(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String lang_code = result.optString("lang_code");
            int live_id = getParameter(request, "live_id", 0);
            result = liveService.getLivePwd(user_id, live_id, lang_code);
        }
        out(response, result,api_version);
    }

    //获取密码直播的分享信息
    @RequestMapping("/pwdLiveShareInfo")
    public void getPwdLiveShareInfo(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int live_id = getParameter(request, "live_id", 0);
            result.put("go_url", WKCache.get_system_cache("live.pwd_go_url"));
            result.put("share_url", WKCache.get_system_cache("live.pwd_share_url"));
            result.put("share_img", WKCache.get_system_cache("live.pwd_share_img"));
            result.put("share_title", WKCache.get_system_cache("live.pwd_share_title"));
            result.put("share_describe", WKCache.get_system_cache("live.pwd_share_describe"));
        }
        out(response, result,api_version);
    }

    //分享密码直播间
    @RequestMapping("/sharePwdLive")
    public void sharePwdLive(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            int live_id = getParameter(request, "live_id", 0);
            String share_type = getParameter(request, "share_type", "wx");
            String lang_code = result.optString("lang_code");
            result = liveService.sharePwdLive(user_id, live_id, share_type, lang_code);
        }
        out(response, result,api_version);
    }

    //获得直播间内的奖励信息
    @RequestMapping("/getLiveRoomAward")
    public void getLiveRoomAward(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String lang_code = result.optString("lang_code");
            int live_id = getParameter(request, "live_id", 0);
            result = liveService.getLiveRoomAward(user_id, live_id,lang_code);
        }
        out(response, result,api_version);
    }

    //领取直播间内的奖励
    @RequestMapping("/checkLiveRoomAward")
    public void checkLiveRoomAward(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String lang_code = result.optString("lang_code");
            int live_id = getParameter(request, "live_id", 0);
            int task_id = getParameter(request, "task_id", 0);
            result = liveService.checkLiveRoomAward(user_id, live_id, task_id,lang_code);
        }
        out(response, result,api_version);
    }

    //开通直播权限
    @RequestMapping("/openLive")
    public void openLive(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String lang_code = result.optString("lang_code");
            result = liveService.openLive(user_id, lang_code);
        }
        out(response, result,api_version);
    }

    /**
     * 获取直播的门票金额
     *
     * @param request
     * @param response
     */
    @RequestMapping("/getLiveTickets")
    public void getLiveTickets(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            //int user_id = result.optInt("user_id");
            //String lang_code = result.optString("lang_code");
            result = LibSysUtils.getResultJSON(0);
            String[] tickets = WKCache.get_system_cache("weking.pay_live_ticket").split(",");
            JSONArray array = new JSONArray();
            for (int i = 0; i < tickets.length - 1; i++) {
                array.add(LibSysUtils.toInt(tickets[i]));
            }
            result.put("tickets", array);
        }
        out(response, result,api_version);
    }

    //分享
    @RequestMapping("/share")
    public void share(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            int live_id = getParameter(request, "live_id", 0);
            object = taskService.shareTask(userId, live_id,lang_code);
        }
        out(response, object,api_version);
    }

    @RequestMapping("/incroomsort")
    public void incroomsort(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        int live_id = getParameter(request, "live_id", 0);
        int score = getParameter(request, "score", 0);
        JSONObject result = WkUtil.checkToken(access_token);
        if (access_token.equals("weking2016") && live_id != 0) {
            //String account = getParameter(request, "account");
            //int user_id = LibSysUtils.toInt(WKCache.getUserByAccount(account, "user_id"));
            WKCache.incr_room_sort(live_id, 0, score);
        }
        out(response, result,api_version);
    }

    @RequestMapping("/like")
    public void like(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        int live_id = getParameter(request, "live_id", 0);
        //int like_count = getParameter(request, "like_count", 1);
        JSONObject result = WkUtil.checkToken(access_token);
        result = liveService.updateLike(live_id, 1);
        out(response, result,api_version);
    }

    @RequestMapping("/getGameLiveInfo")
    public void getGameLiveInfo(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String account = result.optString("account");
            String nick_name = result.optString("nickname");
            String live_stream_id=getLive_stream_id(account);
            result = liveService.getGameLiveInfo(user_id, account, nick_name,live_stream_id);
        }
        out(response, result,api_version);
    }

    @RequestMapping("/getGameCategory")
    public void getGameCategory(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            String lang_code = result.optString("lang_code");
            result = liveService.getGameCategory(lang_code);
        }
        out(response, result,api_version);
    }

    //直播间内修改公告标题和封面
    @RequestMapping("/setLiveAnnouncement")
    public void setLiveAnnouncement(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            String lang_code = result.optString("lang_code");
            int live_id = getParameter(request, "live_id", 0);
            String announcement = getParameter(request, "announcement");
            String live_title = getParameter(request, "live_title", "");
            String live_cover = getParameter(request, "live_cover", "");
            String program_slogan = getParameter(request,"program_slogan","");  // 节目直播口号
            String link_url = getParameter(request,"link_url","");  // 商城/推广链接
            //double api_version = LibSysUtils.toDouble(request.getParameter("api_version"), 3.2);
            result = liveService.setLiveAnnouncement(live_id, announcement, live_title, live_cover,program_slogan,link_url, api_version);
        }
        out(response, result,api_version);
    }

    @RequestMapping("/startError")
    public void startError(HttpServletRequest request, HttpServletResponse response) {
        String account = getParameter(request, "account","");
        String live_stream_id = getParameter(request, "live_stream_id", "");
        long startTime = LibDateUtils.getLibDateTime();
        loger.info("----------startLive:account="+account+" live_stream_id=:"+live_stream_id+" startTime=:"+startTime);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = new JSONObject();
        result.put("code",0);
        out(response, result,api_version);
    }

    //获取直播收益
    @RequestMapping("/getAnchorLiveIncome")
    public void getAnchorLiveIncome(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String time = getParameter(request, "time", "");
            result = liveService.getAnchorLiveIncome(user_id,time);
        }

        out(response, result,api_version);
    }


    //获取主播直播预告
    @RequestMapping("/anchorLiveAdvanceNotice")
    public void anchorLiveAdvanceNotice(HttpServletRequest request, HttpServletResponse response) {
        JSONObject object;
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String account = getParameter(request, "account");
        object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            object = liveService.anchorLiveAdvanceNotice(userId, account);
        }
        this.out(response, object,api_version);
    }

    //修改 新增 删除 直播直播预告
    @RequestMapping("/updateLiveAdvanceNotice")
    public void updateLiveAdvanceNotice(HttpServletRequest request, HttpServletResponse response) {
        JSONObject object;
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        int type = LibSysUtils.toInt(getParameter(request, "type"));  //0 新增  1是修改  2是删除
        int id = LibSysUtils.toInt(getParameter(request, "id"),0);
        long live_time = LibSysUtils.toLong(getParameter(request, "live_time"), 0);
        object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            object = liveService.updateLiveAdvanceNotice(userId, type,id,live_time,lang_code);
        }
        this.out(response, object,api_version);
    }

    //开播预告
    @RequestMapping("/getAdvanceNoticeList")
    public void getAdvanceNoticeList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject object;
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);

        long start_time = LibSysUtils.toLong(getParameter(request, "start_time"), 0);
        long end_time = LibSysUtils.toLong(getParameter(request, "end_time"), 0);
        object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            object = liveService.getAdvanceNoticeList(userId, start_time,end_time);
        }
        this.out(response, object,api_version);
    }


    /**
     * 开始直播竞猜
     */
    @RequestMapping("/startGuessing")
    public void startGuessing(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        double api_version = getParameter(request, C.RequestParam.api_version, 1.0);
        if (object.optInt("code") == 0) {
            int liveId = LibSysUtils.toInt(getParameter(request, "live_id", 0));
            int userId = object.optInt(C.WKCacheUserField.user_id);
            int diff = LibSysUtils.toInt(getParameter(request, "diff", 0));//分钟
            int price = LibSysUtils.toInt(getParameter(request, "price", 0));//竞猜金额
            String title = getParameter(request, "title");
            String option_one = getParameter(request, "option_one","");
            String option_two = getParameter(request, "option_two","");
            int right_option = LibSysUtils.toInt(getParameter(request, "right_option"),2);//0 是选项1 1 是选项2 2是无
            String objectStr = liveService.startGuessing(userId, liveId, diff, price,title,option_one,option_two,right_option);
            out(response, objectStr,api_version);
        } else {
            out(response, object,api_version);
        }
    }


    /**
     * 加入直播竞猜
     */
    @RequestMapping("/joinGuessing")
    public void joinGuessing(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = getParameter(request, C.RequestParam.api_version, 1.0);
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int liveId = LibSysUtils.toInt(getParameter(request, "live_id", 0));
            String account = object.optString(C.WKCacheUserField.account);
            String result = liveService.joinGuessing(liveId, account,api_version);
            out(response, result,api_version);
        } else {
            out(response, object,api_version);
        }
    }



    /**
     * 竞猜
     */
    @RequestMapping("/guess")
    public void guess(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = getParameter(request, C.RequestParam.api_version, 1.0);
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int liveId = getParameter(request, "live_id", 0);
            int guessing_id = getParameter(request, "guessing_id", 0);
            int option = getParameter(request, "option", 0); // 0是选项1 1是选项2
            int userId = object.optInt(C.WKCacheUserField.user_id);
            String account = object.getString("account");
            String lang_code = object.optString("lang_code");
            object = liveService.guess(userId, account, liveId, guessing_id, option, lang_code, api_version);
        }
        out(response, object,api_version);
    }


    /**
     * 开始竞猜
     */
    @RequestMapping("/endGuessing")
    public void endGuessing(HttpServletRequest request, HttpServletResponse response) {
        double api_version = getParameter(request, C.RequestParam.api_version, 1.0);
        int liveId = getParameter(request, "live_id", 0);

            String objectStr = liveService.endGuessing(liveId, api_version);
            out(response, objectStr,api_version);

    }

    // 获取节目单
    @RequestMapping("/getLiveShowList")
    public void getLiveShowList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            object = liveService.getLiveShowList(userId,lang_code);
        }
        out(response, object,api_version);
    }





    public static  String getLive_stream_id(String account){
        String live_stream_id=account+"_"+System.currentTimeMillis();
        return live_stream_id;
    }
}
