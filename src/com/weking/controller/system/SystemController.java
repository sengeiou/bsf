package com.weking.controller.system;

import com.weking.cache.WKCache;
import com.weking.controller.out.OutControllerBase;
import com.weking.core.*;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.mapper.PlayAddress.PlayAddressMapper;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.game.AppGameLogMapper;
import com.weking.mapper.log.LiveLogInfoMapper;
import com.weking.mapper.pocket.PocketInfoMapper;
import com.weking.mapper.pocket.UserGainMapper;
import com.weking.model.PlayAddress.PlayAddress;
import com.weking.model.account.AccountInfo;
import com.weking.model.game.AppGameLog;
import com.weking.model.log.ConsumeInfo;
import com.weking.model.log.LiveLogInfo;
import com.weking.model.pocket.PocketInfo;
import com.weking.model.pocket.UserGain;
import com.weking.redis.LibRedis;
import com.weking.service.live.LiveService;
import com.weking.service.pay.PocketService;
import com.weking.service.system.SystemService;
import com.weking.service.user.LevelService;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created by Administrator on 2017/2/20.
 */
@Controller
@RequestMapping({"/server","/system/server","/live/server"})
public class SystemController extends OutControllerBase {
    static Logger log = Logger.getLogger(SystemController.class);
    @Resource
    private SystemService systemService;
    @Resource
    private LevelService levelService;
    @Resource
    private PlayAddressMapper playaddressMapper;
    @Resource
    private LiveLogInfoMapper liveLogInfoMapper;
    @Resource
    private AccountInfoMapper accountInfoMapper;

    @Resource
    private LiveService liveService;

    @Resource
    private PocketInfoMapper pocketInfoMapper;

    @Resource
    private PocketService pocketService;
    @Resource
    private UserGainMapper userGainMapper;
    @Resource
    private AppGameLogMapper appGameLogMapper;

    private static String key="appsmegame";

    //获取系统广告
    @RequestMapping("/getAdvertisement")
    public void getAdvertisement(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject result = new JSONObject();
        if(LibSysUtils.isNullOrEmpty(access_token)){
            int type = getParameter(request, "type", 0);
            String project_name = getParameter(request, "project_name", "");
            result = systemService.getAdvertisement(type, project_name,"zh_CN");
        }else {
            result = WkUtil.checkToken(access_token);
            if (result.optInt("code") == 0) {
                String lang_code = result.optString("lang_code");
                int type = getParameter(request, "type", 0);
                String project_name = getParameter(request, "project_name", "");
                result = systemService.getAdvertisement(type, project_name,lang_code);
            }else {
                int type = getParameter(request, "type", 0);
                String project_name = getParameter(request, "project_name", "");
                result = systemService.getAdvertisement(type, project_name,"zh_CN");
            }
        }
        out(response, result,api_version);
    }

    //清除系统缓存
    @RequestMapping("/clearCache")
    public void clearCache(HttpServletRequest request, HttpServletResponse response) {
        String args = getParameter(request, "args");
        JSONObject obj = WKCache.del_system_cache(args);
        out(response, obj,1.2);
    }

    //清除数据缓存
    @RequestMapping("/clearDBCache")
    public void clearDBCache(HttpServletRequest request, HttpServletResponse response) {
        levelService.clearList();//清除等级缓存
        liveService.clar_sendmsg_state();//清楚是否后台推送消息
        out(response, LibSysUtils.getResultJSON(ResultCode.success),1.2);
    }

    //初始化im
    @RequestMapping("/startIM")
    public void startIM(HttpServletRequest request, HttpServletResponse response) {
        WkImClient.connect();
        out(response, LibSysUtils.getResultJSON(ResultCode.success),1.2);
    }

    //系统状态
    @RequestMapping("/state")
    public void wkstate(HttpServletRequest request, HttpServletResponse response) {
        JSONObject obj = new JSONObject();
        obj.put("IM.CONNECT.STATE", WkImClient._IMCONNECT);
        obj.put("IM.SEND.STATE", liveService.get_sendmsg_state());
        out(response, obj,1.2);
    }

    //系统状态
    @RequestMapping("/test")
    public void test(HttpServletRequest request, HttpServletResponse response) {
        /*JSONObject obj = new JSONObject();
        obj.put("code",0);
        out(response, obj,1.2);*/
        /*for (int i = 0;i<= 20;i++)   {*/
            JSONObject json = testLogin(1);
            this.out(response, json,1.1);
        // }
    }

    private static JSONObject testLogin(int j)  {
        String key = "01:applePay:" + j;
        Jedis jedis = new Jedis();
        RedisTool redisTool = new RedisTool();
        JSONObject object = new JSONObject();
        String requestId = UUID.randomUUID().toString();
        if (!LibRedis.tryGetDistributedLock(key, requestId,2000)) {
            log.error("订单处理中..." + 15);
            object = LibSysUtils.getResultJSON(ResultCode.system_error);
            return object ;
        }
        log.info("==========================CCCCCC==========================");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // log.info("==========================CCCCCC==========================");
        redisTool.releaseDistributedLock(jedis,key,requestId);
        return object;
    }

    //举报
    @RequestMapping("/report")
    public void report(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        String callback = getParameter(request, "callback");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String account = getParameter(request, "account");
            int type = getParameter(request, "type", 0);
            String report_msg = getParameter(request, "report_msg");
            object = systemService.report(userId, account, report_msg,type);
        }
        String result = callback + "(" + object.toString() + ")";
        out(response, result,api_version);
    }

    //APP举报
    @RequestMapping("/liveReport")
    public void liveReport(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            String account = getParameter(request, "account");
            String report_msg = getParameter(request, "report_msg");
            int type = getParameter(request, "type", 0);
            int userId = object.optInt("user_id");
            object = systemService.report(userId, account, report_msg,type);
        }
        out(response, object,api_version);
    }

    @RequestMapping("/reportList")
    public void reportList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String callback = getParameter(request, "callback");
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            String lang_code = object.optString("lang_code");
            object = systemService.reportList(lang_code);
        }
        String result = callback + "(" + object.toString() + ")";
        out(response, result,api_version);
    }

    //反馈
    @RequestMapping("/feedback")
    public void feedback(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String callback = getParameter(request, "callback");
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            String sugg_msg = getParameter(request, "sugg_msg");
            String sugg_pic = getParameter(request, "sugg_pic");
            String contact = getParameter(request, "contact");
            object = systemService.feedback(userId, sugg_msg, sugg_pic, contact, lang_code);
        }
        String result = callback + "(" + object.toString() + ")";
        out(response, result,1.2);
    }

    @RequestMapping("/getVersion")
    public void getVersion(HttpServletRequest request, HttpServletResponse response) {
        int type = getParameter(request, "type", 0);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject result = systemService.getVersion(type);
        out(response, result,api_version);
    }


    /**
     * 即构服务后创建房间
     */
    @RequestMapping("/create")
    public void create(String[] rtmp_url, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=utf-8");
        try {
            BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            JSONObject jsonObject = JSONObject.fromObject(WkUtil.urlDecode(responseStrBuilder.toString()));
            log.error("----------zego_replay new："+jsonObject.toString());
            PrintWriter out = response.getWriter();
            String channelid = LibSysUtils.toString(jsonObject.get("channel_id"));
            String publishid = LibSysUtils.toString(jsonObject.get("publish_id"));
            String hdlur = LibSysUtils.toString(jsonObject.get("hdl_url"));
            String rtmpurl = LibSysUtils.toString(jsonObject.get("rtmp_url"));
            String hlsurl = LibSysUtils.toString(jsonObject.get("hls_url"));
            String pic_url = LibSysUtils.toString(jsonObject.get("pic_url"));
            String stream_alias = LibSysUtils.toString(jsonObject.get("stream_alias"));
            log.error("----------zego_replay create："+hlsurl);
            if (!LibSysUtils.isNullOrEmpty(stream_alias)) {
                String projectName = LibProperties.getConfig("weking.config.project.name");
                PlayAddress pa = new PlayAddress();
                pa.setAccount(publishid);
                PlayAddress updatepa = playaddressMapper.selectByAccount(publishid);
                Enumeration enu = request.getParameterNames();
                if (!LibSysUtils.isNullOrEmpty(rtmpurl)) {
                    rtmpurl = rtmpurl.replace("[\"", "");
                    rtmpurl = rtmpurl.replace("\"]", "");
                    log.info(("rtmpurl==="+rtmpurl));
                    pa.setRtmpurl(rtmpurl);
                }
                if (!LibSysUtils.isNullOrEmpty(hdlur)) {
                    hdlur = hdlur.replace("[\"", "");
                    hdlur = hdlur.replace("\"]", "");
                    pa.setHdlurl(hdlur);
                }
                if (!LibSysUtils.isNullOrEmpty(hlsurl)) {
                    hlsurl = hlsurl.replace("[\"", "");
                    hlsurl = hlsurl.replace("\"]", "");
                    pa.setHlsurl(hlsurl);
                }
                if (!LibSysUtils.isNullOrEmpty(pic_url)) {
                    pic_url = pic_url.replace("[\"", "");
                    pic_url = pic_url.replace("\"]", "");
                    pa.setPicurl(pic_url);
                }

                if (null != updatepa) {
                    pa.setId(updatepa.getId());
                    playaddressMapper.updateByPrimaryKeySelective(pa);
                } else {
                    playaddressMapper.insertSelective(pa);
                }
                log.info("----------zego_create:account=" + channelid + ";live_stream_id=" + stream_alias + ";channelid=" + channelid + ";publishid=" + publishid + ";hlsurl=" + hlsurl + ";rtmpurl=" + rtmpurl);
                out.print(1);

            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }


    /**
     * 即构服务后台重播
     */

    @RequestMapping("/replay")
    public void replay(HttpServletRequest request, HttpServletResponse response) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));

            String str = "";
            String wholeStr = "";
            while((str = reader.readLine()) != null){//一行一行的读取body体里面的内容；
                wholeStr += str;
            }
            log.info("wholeStr："+wholeStr);
            JSONObject json=JSONObject.fromObject(wholeStr);//转化成json对象
            String replay_url = LibSysUtils.toString(json.get("replay_url"));
            String stream_alias = LibSysUtils.toString(json.get("stream_alias"));
            log.info("----------zego_replay：" + "replay_url=" + replay_url + "live_stream_id=" + stream_alias);
            if (!LibSysUtils.isNullOrEmpty(stream_alias)) {
                String projectName = LibProperties.getConfig("weking.config.project.name");
                if (!stream_alias.startsWith("m") || "mongalaxy".equals(projectName.toLowerCase())) {
                    this.getLibJdbcTemplate().update(String.format("update wk_mylive_log set replay_url = '%s' where live_stream_id='%s' and replay_url=''", replay_url, stream_alias));
                    out(response,"1",1.1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 即构服务后台关闭
     */
    @RequestMapping("/close")
    public void close(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=utf-8");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String str = "";
            String wholeStr = "";
            while((str = reader.readLine()) != null){//一行一行的读取body体里面的内容；
                wholeStr += str;
            }
            log.info("wholeStr："+wholeStr);
            JSONObject json=JSONObject.fromObject(wholeStr);//转化成json对象
            String channel_id = LibSysUtils.toString(json.get("channel_id"));
            String stream_alias = LibSysUtils.toString(json.get("stream_alias"));
            String nonce = LibSysUtils.toString(json.get("nonce"));
            if (!LibSysUtils.isNullOrEmpty(stream_alias)) {
                String projectName = LibProperties.getConfig("weking.config.project.name");
                if (!stream_alias.startsWith("m") || "mongalaxy".equals(projectName.toLowerCase())) {
                    log.info("----------zego_close：" + "roomID=" + channel_id.split("_")[0] + ";live_stream_id=" + stream_alias + ";channelid=" + channel_id);
                    out(response,"1",1.1);
                } else {
                    System.out.println("----------------------MonGalaxy Close:" + stream_alias);
                    WkUtil.sendGet("http://mongalaxy.chidaotv.com/server/close",
                            String.format("channel_id=%s&nonce=%s&publish_id=%s&stream_alias=%s", channel_id, nonce, "", stream_alias));
                    out(response,"1",1.1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户操作埋点记录
     */
    @RequestMapping("/operate")
    public void operateRecord(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WkUtil.checkToken(access_token);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String account = object.optString("account");
            int type = getParameter(request,"type",1);
            String params = getParameter(request, "params","{}");
            try {
                params = java.net.URLDecoder.decode(params, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            object = systemService.operateRecord(userId,account,type,params);
        }
        out(response, object,api_version);
    }

    /**
     * 用户操作埋点记录
     */
    @RequestMapping("/addUserCache")
    public void addUserCache(HttpServletRequest request, HttpServletResponse response) {
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        int index = getParameter(request, "index", 0);
        int count = getParameter(request, "count", 10000);
        JSONObject object = systemService.addUserCache(index,count);
        out(response, object,api_version);
    }

    // 刷新关键字缓存
    @RequestMapping("/refreshKeyWord")
    public void refreshKeyWord(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        if (access_token.equals(C.ManagerSysAccessToken)) {
            systemService.refreshKeyWord(access_token);
        }
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        out(response, result,api_version);
    }

    @RequestMapping("/refreshInner")
    public void refreshInner(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        if (access_token.equals(C.ManagerSysAccessToken)) {
            systemService.refreshInner();
        }
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        out(response, result,api_version);
    }

    // 封锁用户状态
    @RequestMapping("/setLockout")
    public void setLockout(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        if (access_token.equals(C.ManagerSysAccessToken)) {
            int user_id = getParameter(request,"user_id",0);
            int type = getParameter(request,"type",0);
            int day_num = getParameter(request,"day_num",0);
            String reason = getParameter(request,"reason");
            systemService.setLockout(user_id,type,reason,day_num);
        }
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        out(response, result,api_version);
    }

    /**
     * 腾讯云回调
     */
    @RequestMapping("/qcloudCallback")
    public void qcloudCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 读取请求内容
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine())!=null){
            sb.append(line);
        }
        // 将资料解码
        String reqBody = sb.toString();
        log.error("requestBody:"+reqBody);
        JSONObject jsonObject = JSONObject.fromObject(reqBody);
        String time = jsonObject.optString("t");
        String sign = jsonObject.optString("sign");
        int eventType = jsonObject.optInt("event_type");
        String stream_id = jsonObject.optString("stream_id");

        log.error("stream_id=="+stream_id);
        if(systemService.checkSign(time,sign)){
            log.error("qcloudCallback success! eventType : "+eventType);
            JSONObject object;
            switch (eventType){
                case 0: //断流
                    object = LibSysUtils.getResultJSON(ResultCode.success);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LiveLogInfo liveLogInfo = liveLogInfoMapper.selectByStreamId(stream_id);
                    if (liveLogInfo!=null&&liveLogInfo.getLiveEnd()==0){
                        Integer userId = liveLogInfo.getUserId();
                        AccountInfo info = accountInfoMapper.selectByPrimaryKey(userId);
                        if (info!=null){
                            liveService.endLive(userId, info.getAccount(), liveLogInfo.getId(), 2);
                        }

                    }
                    break;
                case 1: //推流
                    object = LibSysUtils.getResultJSON(ResultCode.success);
                    break;
                case 100: //录制
                    //int startTime = jsonObject.optInt("start_time",0);
                    //int endTime = jsonObject.optInt("end_time",0);
                    String video_url = jsonObject.optString("video_url");
                    //String video_id = jsonObject.optString("video_id");
                    log.error("video_url : "+video_url);
                    object = systemService.record(stream_id,video_url);
                    break;
                case 200: //截图
                    object = LibSysUtils.getResultJSON(ResultCode.success);
                    break;
                default:
                    object = LibSysUtils.getResultJSON(ResultCode.success);
                    break;
            }
            out(response,object,0.2);
        }else {
            log.error("qcloudCallback failed");
        }
    }


    //获取用户充值账单  提供api对外
    @RequestMapping("/rechargeLog")
    public void rechargeLog(HttpServletRequest request, HttpServletResponse response) {

        String account = getParameter(request, "account", "");
        int paymentCode = getParameter(request, "payment_code",-1);
        int state = getParameter(request, "state", -1);
        int index = getParameter(request, "index", 0);
        int count = getParameter(request, "count", 20);
        long beginTime = getParameter(request, "beginTime", 0L);
        long endTime = getParameter(request, "endTime", 0L);
        JSONObject object  = systemService.rechargeLog(account,paymentCode,state,beginTime,endTime, index, count);
        out(response, object,0.2);
    }


    //获取用户充值账单  提供api对外
    @RequestMapping("/consumeLog")
    public void consumeLog(HttpServletRequest request, HttpServletResponse response) {

        String account = getParameter(request, "account", "");
        int index = getParameter(request, "index", 0);
        int count = getParameter(request, "count", 20);
        long beginTime = getParameter(request, "beginTime", 0L);
        long endTime = getParameter(request, "endTime", 0L);
        JSONObject object  = systemService.consumeLog(account,beginTime,endTime, index, count);
        out(response, object,0.2);
    }

    /**
     * 更新金币
     */

    @RequestMapping("/updateGameCoin")
    public void updateGameCoin(HttpServletRequest request, HttpServletResponse response) {
        JSONObject object = new JSONObject();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));

            String sign = request.getParameter("sign");
            String token = request.getParameter("token");
            String str = "";
            String wholeStr = "";
            while((str = reader.readLine()) != null){//一行一行的读取body体里面的内容；
                wholeStr += str;
            }
            log.info("wholeStr："+wholeStr);
            JSONObject json=JSONObject.fromObject(wholeStr);//转化成json对象
            String orderId = LibSysUtils.toString(json.get("orderId"));
            String gameId = LibSysUtils.toString(json.get("gameId"));
            String uid = LibSysUtils.toString(json.get("uid"));
            long coin = LibSysUtils.toLong(json.get("coin"));
            int type = LibSysUtils.toInt(json.get("type"));
            String overSign=orderId+gameId+uid+coin+type+key;
            String newSign = DigestUtils.md5DigestAsHex(overSign.getBytes());
            String overToken=uid+gameId+key;
            String newToken = DigestUtils.md5DigestAsHex(overToken.getBytes());

            if (newSign.equals(sign)&&newToken.equals(token)){

                AccountInfo accountInfo = accountInfoMapper.selectByAccountId(uid);
                if (accountInfo!=null) {

                    switch (type) {
                        case 1:
                            PocketInfo pinfo = pocketInfoMapper.selectByUserid(accountInfo.getId());
                            if (pinfo != null && (pinfo.getTotalDiamond()) < coin) {
                                object.put("errorCode",-2);
                            }else {
                                //扣除用户EMO
                                pocketInfoMapper.deductDiamondByUserid((int) coin, accountInfo.getId());//扣钱
                                //添加消费日志
                                recordGameLog(accountInfo.getId(),  (int) coin, orderId, gameId, type);//玩游戏设置为-1
                                object.put("errorCode",0);
                            }
                            break;
                        case 2:
                            //加钱
                            pocketInfoMapper.increaseDiamondByUserId( accountInfo.getId(), (int) coin);
                            //游戏收入存入新的表单中
                           // int amount= (int) coin;
                            recordGameLog(accountInfo.getId(),  (int) coin, orderId, gameId, type);
                           /* UserGain gain = UserGain.getGain(accountInfo.getId(), C.UserGainType.game_pay, amount, LibSysUtils.toInt(orderId));
                            userGainMapper.insertSelective(gain);*/
                            object.put("errorCode",0);
                            break;
                    }
                }

            }else {
                object.put("errorCode",-1);
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

        out(response, object,0.2);

    }


    /**
     * //查询玩家基本信息
     */

    @RequestMapping("/getUserInfo")
    public void getUserInfo(HttpServletRequest request, HttpServletResponse response) {
        JSONObject object = new JSONObject();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));

            String sign = request.getParameter("sign");
            String str = "";
            String wholeStr = "";
            while((str = reader.readLine()) != null){//一行一行的读取body体里面的内容；
                wholeStr += str;
            }
            log.info("wholeStr："+wholeStr);
            JSONObject json=JSONObject.fromObject(wholeStr);//转化成json对象
            String gameId = LibSysUtils.toString(json.get("gameId"));
            String uid = LibSysUtils.toString(json.get("uid"));
            String overSign=gameId+uid+key;
            String newSign = DigestUtils.md5DigestAsHex(overSign.getBytes());

            JSONObject obj = new JSONObject();
            if (newSign.equals(sign)){

                AccountInfo accountInfo = accountInfoMapper.selectByAccountId(uid);
                if (accountInfo!=null) {
                    obj.put("uid",accountInfo.getAccount());
                    obj.put("nickname",accountInfo.getNickname());
                    obj.put("avatar", WkUtil.combineUrl(accountInfo.getPicheadUrl(), UploadTypeEnum.AVATAR, false));
                }
                PocketInfo pinfo = pocketInfoMapper.selectByUserid(accountInfo.getId());
                if (pinfo!=null){
                    obj.put("coin",LibSysUtils.toLong(pinfo.getTotalDiamond()));
                }else {
                    obj.put("coin",0L);
                }
                obj.put("gameStatus",0);
                object.put("errorCode",0);
                object.put("data",obj);
            }else {
                object.put("errorCode",-1);
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

        out(response, object,0.2);

    }


    public void recordGameLog(int userId,  int icon, String orderId, String gameId, int type) {
        long addTime = LibDateUtils.getLibDateTime();
        AppGameLog appGameLog = new AppGameLog();
        appGameLog.setUserId(userId);
        appGameLog.setOrderId(orderId);
        appGameLog.setGameId(gameId);
        appGameLog.setCoin(icon);
        appGameLog.setType(type);
        appGameLog.setAddTime(addTime);
        appGameLogMapper.insertSelective(appGameLog);

    }



}
