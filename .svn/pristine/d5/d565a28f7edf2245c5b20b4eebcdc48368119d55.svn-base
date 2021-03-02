package com.weking.service.live;

import com.weking.cache.GameCache;
import com.weking.cache.RoomCacheInfo;
import com.weking.cache.UserCacheInfo;
import com.weking.cache.WKCache;
import com.weking.core.*;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.core.sensitive.WordFilter;
import com.weking.game.GameUtil;
import com.weking.mapper.PlayAddress.PlayAddressMapper;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.activity.ActivityListMapper;
import com.weking.mapper.blacklog.BlackLogMapper;
import com.weking.mapper.certification.CertificationMapper;
import com.weking.mapper.follow.FollowInfoMapper;
import com.weking.mapper.level.LevelEffectMapper;
import com.weking.mapper.live.*;
import com.weking.mapper.log.ConsumeInfoMapper;
import com.weking.mapper.log.GuessingLogMapper;
import com.weking.mapper.log.LiveLogInfoMapper;
import com.weking.mapper.pocket.ContributionInfoMapper;
import com.weking.mapper.pocket.GiftInfoMapper;
import com.weking.mapper.pocket.PocketInfoMapper;
import com.weking.mapper.pocket.UserGainMapper;
import com.weking.model.PlayAddress.PlayAddress;
import com.weking.model.account.AccountInfo;
import com.weking.model.activity.ActivityList;
import com.weking.model.certification.Certification;
import com.weking.model.level.LevelEffect;
import com.weking.model.live.*;
import com.weking.model.log.ConsumeInfo;
import com.weking.model.log.GuessingLog;
import com.weking.model.log.LiveLogInfo;
import com.weking.model.pocket.ContributionInfo;
import com.weking.model.pocket.GiftInfo;
import com.weking.model.pocket.PocketInfo;
import com.weking.model.pocket.UserGain;
import com.weking.service.game.GameService;
import com.weking.service.pay.PayService;
import com.weking.service.pay.PocketService;
import com.weking.service.shop.GoodsService;
import com.weking.service.shop.StoreService;
import com.weking.service.system.MsgService;
import com.weking.service.system.RobotService;
import com.weking.service.system.SystemService;
import com.weking.service.user.FollowService;
import com.weking.service.user.LevelService;
import com.weking.service.user.TaskService;
import com.weking.service.user.UserService;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("liveService")
public class LiveService extends LibServiceBase {
    static Logger loger = Logger.getLogger(LiveService.class);

    @Resource
    private TaskService taskService;
    @Resource
    private LiveLogInfoMapper liveLogInfoMapper;
    @Resource
    private PocketInfoMapper pocketInfoMapper;
    @Resource
    private CertificationMapper certificationMapper;
    @Resource
    private AccountInfoMapper accountMapper;
    @Resource
    private FollowInfoMapper followInfoMapper;
    @Resource
    private LiveTagMapper liveTagMapper;
    @Resource
    private GiftInfoMapper giftInfoMapper;
    @Resource
    private ConsumeInfoMapper consumeInfoMapper;
    @Resource
    private ContributionInfoMapper contributionInfoMapper;
    @Resource
    private RobotService robotService;
    @Resource
    private UserService userService;
    @Resource
    private PlayAddressMapper playAddressMapper;
    @Resource
    private LevelService levelService;
    @Resource
    private StoreService storeService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private GameService gameService;
    @Resource
    private VideoChatMapper videoChatMapper;
    @Resource
    private BlackLogMapper blackLogMapper;
    @Resource
    private LiveGuardService liveGuardService;
    @Resource
    private PocketService pocketService;
    @Resource
    private GameCategoryMapper gameCategoryMapper;
    @Resource
    private AccountInfoMapper accountInfoMapper;
    @Resource
    private SystemService systemService;
    @Resource
    private MsgService msgService;
    @Resource
    private LiveGuardMapper liveGuardMapper;
    @Resource
    private LiveAdvanceNoticeMapper liveAdvanceNoticeMapper;
    @Resource
    private LiveGuessingMapper liveGuessingMapper;
    @Resource
    private GuessingLogMapper guessingLogMapper;
    @Resource
    private ActivityListMapper activityListMapper;
    @Resource
    private UserGainMapper userGainMapper;
    @Resource
    private LevelEffectMapper levelEffectMapper;
    @Resource
    private FollowService followService;

    @Resource
    private LiveShowMapper liveShowMapper;

    @Resource
    private PayService payService;

    private volatile Boolean _serversendmsg = null;//是否后台推送IM消息
    private static volatile String _projectName = LibProperties.getConfig("weking.config.project.name");

    public boolean get_sendmsg_state() {
        if (_serversendmsg == null)
            _serversendmsg = LibSysUtils.toBoolean(WKCache.get_system_cache("server.sendmsg"));
        return _serversendmsg;
    }

    public void clar_sendmsg_state() {
        _serversendmsg = null;
    }

    /**
     * 检查是否有直播权限
     *
     * @param user_id   主播的user_id
     * @param lang_code
     * @return
     */
    public JSONObject checkLivePrivilege(int user_id, String project_name, String lang_code, String channel, double version) {
        JSONObject result = LibSysUtils.getResultJSON(0);
        AccountInfo accountInfo = accountMapper.selectByPrimaryKey(user_id);
        double appVersion = LibSysUtils.toDouble(WKCache.get_system_cache("is_ios_version"), 0.0);
        //账号被禁用
        if (accountInfo.getIsblack() == 2) {
            result.put("code", ResultCode.account_isblack);
            result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.liveing.forbidden"));
            return result;
        }
        //是否启用实名认证
        String isRealName = WKCache.get_system_cache("IS_REAL_NAME");
        if (LibSysUtils.toBoolean(isRealName)) {
            Certification certificationInfo = certificationMapper.selectByuserId(user_id);
            if (certificationInfo == null) {
                result.put("code", ResultCode.live_uncertification);
                result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.liveing.autonym"));
                return result;
            }
        }
        int getAnchor_level = accountInfo.getAnchor_level();
        int public_live_ticket = LibSysUtils.toInt(WKCache.get_system_cache("weking.public_live_diamond"));//开通直播权限所需要的钻石数
        result.put("public_live_diamond", public_live_ticket);
        if (appVersion != version) {
            //如果主播等级为0，并且需要钻石购买开通直播权限
            if (getAnchor_level == 0 && public_live_ticket > 0) {
                result.put("public_live", false);
                if (!LibSysUtils.isNullOrEmpty(channel)) {
                    result.put("public_live_msg", LibProperties.getLanguage(lang_code, "weking.lang." + channel + "app.public_live_msg").replace("%diamond%", LibSysUtils.toString(public_live_ticket)));
                } else {
                    result.put("public_live_msg", LibProperties.getLanguage(lang_code, "weking.lang." + project_name + "app.public_live_msg").replace("%diamond%", LibSysUtils.toString(public_live_ticket)));
                }
            } else {
                result.put("public_live", true);
                result.put("public_live_msg", "");
            }
        } else {
            result.put("public_live", true);
            result.put("public_live_msg", "");
        }


        //主播等级大于1的才有开启付费播的权限
        if (getAnchor_level > 1) {
            result.put("pay_live", true); // 付费播
            result.put("privacy_live", true);  // 私密播
            result.put("program_live", true);   // 节目播
            result.put("pay_live_msg", "");
            result.put("privacy_live_msg", "");
        } else {
            result.put("pay_live", false);
            result.put("privacy_live", false);
            result.put("program_live", false);
            result.put("pay_live_msg", LibProperties.getLanguage(lang_code, "weking.lang.app.pay_live_msg"));
            result.put("privacy_live_msg", LibProperties.getLanguage(lang_code, "weking.lang.app.privacy_live_msg"));
        }
        String[] tickets = WKCache.get_system_cache("weking.pay_live_ticket").split(",");
        JSONArray array = new JSONArray();
        for (int i = 0; i < tickets.length; i++) {
            array.add(LibSysUtils.toInt(tickets[i]));
        }
        result.put("tickets", array);
        //是否可以开始电商直播
        int storeId = storeService.findStoreIdByUserId(user_id);
        if (storeId == 0) {
            result.put("shop_live", false);
            result.put("shop_live_msg", LibProperties.getLanguage(lang_code, "weking.lang.app.shop.store.error"));
        } else {
            result.put("shop_live", true);
            result.put("shop_live_msg", "");
        }
        result.put("push_flag", WKCache.get_system_cache("zego.push_flag"));


        String users = WKCache.get_system_cache(C.WKSystemCacheField.live_official_room_users);
        Set<String> liveOfficialListUsers = new HashSet<>();
        if (!LibSysUtils.isNullOrEmpty(users)) {
            String[] userArray = users.split(",");
            if (userArray != null && userArray.length > 0) {
                for (String temp : userArray) {
                    liveOfficialListUsers.add(temp);
                }
            }
        }
        if (liveOfficialListUsers.contains(accountInfo.getAccount())) {
            result.put("official_live", true);
        } else {
            result.put("official_live", false);
        }

        return result;
    }

    /**
     * 开始直播
     *
     * @param user_id        主播的user_id
     * @param account        主播的account
     * @param live_stream_id 直播流id
     * @param live_type      直播类型，0：公开播，10：付费播；11：私密播；20:电商；30：游戏  40：节目 50：摄像头
     * @param live_ticket    为付费播时的门票价格
     * @param live_pwd       私密播时的密码
     * @param live_title     直播标题
     * @param live_cover     直播封面
     * @param longitude      经度
     * @param latitude       纬度
     * @param live_tag_name  直播标签
     * @param program_slogan 节目直播口号
     * @return
     */
    @Transactional
    public JSONObject startLive(int user_id, String account, String avatar, String nickname, String live_stream_id, int live_type, int live_ticket, String live_pwd, String live_title,
                                String live_cover, double longitude, double latitude, String city, String live_tag_name, String project_name,
                                boolean is_horizontal, int game_category_id, String program_slogan, String lang_code, double api_version, String channel, Boolean is_official_live,String activity_ids) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        long startTime = LibDateUtils.getLibDateTime();
        LiveLogInfo logInfo = liveLogInfoMapper.selectLivingByUserId(user_id);

        if (logInfo != null) {//如果存在正在直播的记录则删除
            long stime = logInfo.getLiveStart();
            long diff = LibDateUtils.getDateTimeTick(stime, startTime);
            liveLogInfoMapper.updateLiveEndByUserId(startTime, user_id, diff);//如果当前存在未结束的直播列表，则更新结束时间
            WKCache.del_room(logInfo.getId());


            //关播查询是否为官方直播间  如果是  就清官方直播缓存
            int live_official_room = WKCache.get_live_official_room();
            if (live_official_room == logInfo.getId()) {
                WKCache.del_live_official_room();
            }

            loger.info(String.format("----------startLive_end:account=%s,live_id=%d,live_stream_id=%s", account, logInfo.getId(), logInfo.getLive_stream_id()));
        }

        if (is_official_live) {
            int live_official_room = WKCache.get_live_official_room();
            if (live_official_room > 0) {
                result.put("code", ResultCode.live_official);
                result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.official.live"));
                return result;
            }
        }

        if (live_type == C.LiveType.SHOP) { //电商直播
            int storeId = storeService.findStoreIdByUserId(user_id);
            if (storeId == 0) {
                return LibSysUtils.getResultJSON(ResultCode.shop_store_error, LibProperties.getLanguage(lang_code, "weking.lang.app.shop.store.error"));
            }
            result.put("store_id", storeId);
            WKCache.add_user(user_id, "store_id", LibSysUtils.toString(storeId));
        }

        LiveLogInfo liveRecord = new LiveLogInfo();

        Map<String, String> userMap = userService.getUserInfoByUserId(user_id, "is_official", "hots", "anchor_level");
        if (LibSysUtils.toInt(userMap.get("is_official"), 0) == 0) {
            LiveTag liveTag = liveTagMapper.selectByTagType(1);
            if (liveTag != null) {
                live_tag_name = liveTag.getTagName();
            }
        }
        if (LibSysUtils.toInt(userMap.get("hots"), 0) > 0) {
            liveRecord.setRecommend(1);
        }
        /*if (LibSysUtils.toInt(userMap.get("anchor_level"), 0) < 1) {
            return LibSysUtils.getResultJSON(ResultCode.operation_again_later, "暫無開播權限,如有疑問聯系客服~");

        }*/

        liveRecord.setUserId(user_id);
        liveRecord.setLiveStart(startTime);

        liveRecord.setLive_stream_id(live_stream_id);
        liveRecord.setLive_cover(live_cover);
        liveRecord.setLatitude(latitude);
        liveRecord.setLongitude(longitude);
        liveRecord.setTag_name(live_tag_name);
        liveRecord.setLive_type(live_type);
        liveRecord.setProject_name(project_name);
        if (LibSysUtils.isNullOrEmpty(live_title)){
            liveRecord.setLive_title("AppsMe");
        }else {
            liveRecord.setLive_title(live_title);
        }

        if (live_type == C.LiveType.PAY)//付费播
            liveRecord.setLive_extend(LibSysUtils.toString(live_ticket));
        if (live_type == C.LiveType.PRIVATE) {//私密播
            liveRecord.setLive_extend(LibSysUtils.toString(live_pwd));
            live_ticket = LibSysUtils.toInt(WKCache.get_system_cache("live.pwd_ticket"));//购买密码的价格
        }
        if (live_type == C.LiveType.PROGRAM) { // 节目直播
            liveRecord.setProgram_slogan(program_slogan); //节目直播口号
        }
        liveRecord.setHorizontal(is_horizontal);
        liveRecord.setGame_category_id(game_category_id);
        if (live_type == C.LiveType.PRIVATE) //live_type == C.LiveType.PAY ||
            liveRecord.setStatus(2); //VVIP直播把直播记录设置为隐藏
        liveLogInfoMapper.insertSelective(liveRecord);
        int live_id = liveRecord.getId();
        if (live_id > 0) {//直播成功
            SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMddHHmmss");
            String date = dfs.format(new Date());
            Map<String, String> room_info = new HashMap();
            room_info.put("attendance", "0");
            room_info.put("real_audience", "0");
            room_info.put("online_audience", "0");
            room_info.put("city", city);
            room_info.put("live_stream_id", live_stream_id);
            room_info.put("heart_time", date);
            room_info.put("live_id", LibSysUtils.toString(live_id));
            room_info.put("user_id", LibSysUtils.toString(user_id));
            room_info.put("link_live_stream_id", "");
            room_info.put("account", account);
            room_info.put("live_type", LibSysUtils.toString(live_type));
            room_info.put("live_ticket", LibSysUtils.toString(live_ticket));
            room_info.put("live_pwd", live_pwd);


            room_info.put("avatar", avatar);
            room_info.put("nickname", nickname);
            room_info.put("live_start", LibSysUtils.toString(startTime));
            room_info.put("longitude", LibSysUtils.toString(longitude));
            room_info.put("latitude", LibSysUtils.toString(latitude));
            room_info.put("live_cover", live_cover);
            room_info.put("live_title", live_title);
            room_info.put("tag_name", live_tag_name);
            room_info.put("is_horizontal", LibSysUtils.toString(is_horizontal));
            room_info.put("announcement", "");
            room_info.put("program_slogan", program_slogan);
            room_info.put("activity_ids", activity_ids);

            result.put("live_id", live_id);//直播记录id
            //result.put("total_ticket", pocketInfoMapper.getAnchorTicketbyid(user_id));//总收入
            long month = LibDateUtils.getLibDateTime("yyyyMM");
            result.put("total_ticket", WKCache.get_income_num(month, account));//主播收到的币
            PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(user_id);
            int is_official = LibSysUtils.toInt(userService.getUserInfoByUserId(user_id, "is_official"));
            if (is_official != 1) {
                if (pocketInfo != null) {
                    if (pocketInfo.getSca_gold().compareTo(new BigDecimal("0")) > 0) {
                        BigDecimal anchorAll = pocketInfo.getSca_gold();
                        result.put("sca_gold_anchor", anchorAll);//主播sca  gold
                    }
                }
            }


            result.put("reminder", LibProperties.getLanguage(lang_code, "weking.lang.app.live_reminder"));
            result.put("announcement", "");//直播公告
            if (api_version > 3.1) {
                JSONArray banner_array = new JSONArray();
                JSONArray banner_result = systemService.getAdvertisement(4, project_name, lang_code).optJSONArray("adv_list");
                for (int i = 0; i < banner_result.size(); i++) {
                    JSONObject o = banner_result.getJSONObject(i);
                    JSONObject banner_object = new JSONObject();
                    banner_object.put("banner_image_url", o.optString("img_url"));//广告图片地址
                    banner_object.put("banner_url", o.optString("link_url"));//广告跳转地址
                    banner_object.put("banner_title", o.optString("title"));//广告跳标题
                    banner_array.add(banner_object);
                }
                result.put("banner_list", banner_array);
            } else {
                //api_version为3.2以上以下三个参数改成列表
                result.put("banner_image_url", getLiveBanner(account));//广告图片地址
                result.put("banner_url", WKCache.get_system_cache("live.banner.url"));//广告跳转地址
                result.put("banner_title", WKCache.get_system_cache("live.banner.title"));//广告跳标题
            }
            if (WkImClient.USEWKINGIM) {//wekingIM
                WkImClient.createRoomAndJoin(live_stream_id, account, 5);
            }
            Thread notifyFans = new Thread(new Runnable() {//通知粉丝
                public void run() {
                    notifyFans(user_id, live_id, live_stream_id, live_type, false, "", live_title, channel);
                }
            }, "notifyFans");
            notifyFans.start();

            boolean robot=LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.weking_config_robot), false);

            if (live_type == C.LiveType.PAY || live_type == C.LiveType.PRIVATE)
                robot = LibSysUtils.toBoolean(WKCache.get_system_cache("weking.config.vvip.robot"));
            if (live_type == C.LiveType.GAME)//游戏直播不加机器人
                robot = false;
            if (robot) {
                Thread test1 = new Thread(new Runnable() {//机器人
                    public void run() {
                        robotService.start(live_id, live_stream_id);
                    }
                }, "robotService");
                test1.start();
            }
            WKCache.add_room(live_id, room_info);
            WKCache.add_user(user_id, "live_id", LibSysUtils.toString(live_id));
            //WKCache.incr_room_sort(live_id, live_type, LibSysUtils.toInt(WKCache.get_user(user_id, "sorts")) * 10000);
            result.put("guard_info", liveGuardService.getLiveGuardInfo(live_id));
            loger.info(String.format("----------startLive:account=%s,live_id=%d,live_stream_id=%s", account, live_id, live_stream_id));
        }
//        System.out.println(result);
        if (LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.live_vip_user_switch), false)) {
            String vipAccount = WKCache.get_system_cache(C.WKSystemCacheField.live_vip_user_account);
            if (vipAccount != null && vipAccount != "") {
                String vipAvatar = userService.getUserFieldByAccount(vipAccount, "avatar");
                vipAvatar = WkUtil.combineUrl(vipAvatar, UploadTypeEnum.AVATAR, true);
                if (vipAvatar != null && vipAvatar != "") {
                    result.put("vipAccount", vipAccount);
                    result.put("vipAvatar", vipAvatar);
                }
            }

        }

        AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(user_id);
        if (accountInfo!=null&&accountInfo.getCdn_option()==1){
            //台湾Cdn
            result.put("push_url", TencentUtil.getPushTWUrl(live_stream_id));
        }else {
            result.put("push_url", TencentUtil.getPushFlowUrl(live_stream_id));
        }

        result.put("live_stream_id", live_stream_id);
        if (is_official_live) {
            WKCache.set_live_official_room(live_id);
        }
        //查询是否开启游戏
        Boolean flg=LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.is_game_switch), false);
        result.put("is_game_switch",flg);
        return result;
    }

    /**
     * 强制结束直播
     *
     * @param account 主播的account
     * @return
     */
    public JSONObject forceeEndLive(String account) {
        int user_id = LibSysUtils.toInt(WKCache.getUserByAccount(account, "user_id"));
        LiveLogInfo liveLogInfo = liveLogInfoMapper.selectLivingByUserId(user_id);
        int live_id = liveLogInfo.getId();
        return endLive(user_id, account, live_id, 1);
    }

    /**
     * 结束直播
     *
     * @param user_id  主播的user_id
     * @param account  主播的 account
     * @param live_id  直播记录id
     * @param end_type 结束类型，0:app结束，1：强制结束,2:定时器,10网络不稳定结束直播，20闪退结束直播
     * @return
     */
    @Transactional
    public JSONObject endLive(int user_id, String account, int live_id, int end_type) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.system_error);
        RoomCacheInfo roomCacheInfo = WKCache.get_room(live_id);
        long endTime = LibDateUtils.getLibDateTime();
        loger.info(String.format("----------begin_endLive:account=%s,live_id=%d,endtime:%d,end_type:%d", account, live_id, endTime, end_type));
        if (roomCacheInfo != null && user_id == roomCacheInfo.getUser_id()) {
            LiveLogInfo liveInfo = liveLogInfoMapper.selectByPrimaryKey(live_id);
            if (liveInfo != null) {
                int live_type = roomCacheInfo.getLive_type();
                int audience_num = roomCacheInfo.getAttendance();
                int real_audience = roomCacheInfo.getReal_attendance();
                LiveLogInfo liveRecord = new LiveLogInfo();
                liveRecord.setId(live_id);
                liveRecord.setLiveEnd(endTime);
                liveRecord.setAudienceNum(audience_num);
                liveRecord.setReal_audience(real_audience);
                liveRecord.setDiff(LibDateUtils.getDateTimeTick(liveInfo.getLiveStart(), endTime));
                boolean is_end = liveLogInfoMapper.updateByPrimaryKeySelective(liveRecord) > 0;//更新直播记录表
                if (is_end) {
                    long total_tiecket = consumeInfoMapper.getThisTimeTotalTiecket(user_id, live_id);
                    //获得本次直播贡献榜
                    List top_three = consumeInfoMapper.getTopThreeSender(user_id, live_id);
                    JSONArray topThreeList = new JSONArray();
                    for (int i = 0; i < top_three.size(); i++) {
                        JSONObject topThreeResutlt = new JSONObject();
                        Map aa = (Map) top_three.get(i);
                        String pichigh = LibSysUtils.toString(aa.get("pic_url"));
                        int send_tickets = LibSysUtils.toInt(aa.get("totalSend"));
                        topThreeResutlt.put("pic_head_low", WkUtil.combineUrl(pichigh, UploadTypeEnum.AVATAR, true));
                        topThreeResutlt.put("send_tickets", send_tickets);
                        topThreeList.add(topThreeResutlt);
                    }
                    double getMoney = changeTicketToMoney(total_tiecket);
                    String total_time = LibDateUtils.diffDatetime(liveInfo.getLiveStart(), endTime);
                    result.remove("code");
                    result.put("live_id", live_id);
                    result.put("audience_num", audience_num);
                    result.put("tickets", total_tiecket);
                    result.put("total_time", total_time);
                    result.put("contribution_top3", topThreeList);
                    result.put("get_money", getMoney);
                    result.put("nickname", WKCache.get_user(user_id, "nickname"));
                    result.put("account", WKCache.get_user(user_id, "account"));
                    result.put("pic_head_low", WkUtil.combineUrl(WKCache.get_user(user_id, "avatar"), UploadTypeEnum.AVATAR, true));
                    int gameState = GameCache.get_game_state(live_id);
                    if (gameState == GameUtil.BET_STATE || gameState == GameUtil.BEGIN_CARD_STATE) {
                        if (GameCache.get_game_type(live_id) == GameUtil.CRAZY_RACING) {
                            result.put("keep_game", true);
                        }
                    }
                    if (end_type == 0 || end_type == 10 || end_type == 20)
                        result.put("im_code", IMCode.end_live);
                    else
                        result.put("im_code", IMCode.force_end_live);
                    WkImClient.sendRoomMsg(liveInfo.getLive_stream_id(), result.toString(), 1);//推送给观众告诉直播结束
                    if (result.optBoolean("keep_game", false)) {
                        Thread thread = new Thread(new Runnable() {//处理游戏
                            @Override
                            public void run() {
                                gameService.refundBet(user_id, live_id, roomCacheInfo);
                            }
                        }, "gameService");
                        thread.start();
                    } else {
                        if (WkImClient.USEWKINGIM) {
                            WkImClient.delRoom(liveInfo.getLive_stream_id(), account, 5);
                        }
                    }
                    WKCache.del_room(live_id);

                    //关播查询是否为官方直播间  如果是  就清官方直播缓存
                    int live_official_room = WKCache.get_live_official_room();
                    if (live_official_room == live_id) {
                        WKCache.del_live_official_room();
                    }

                    //先查询有没有未结束的竞猜
                    int guessingId = GameCache.get_guessing_id(user_id);
                    if (guessingId > 0) {
                        GameCache.del_guessing_info(user_id); // 清除上局竞猜数据
                        liveGuessingMapper.updateByIdAndEndTime(guessingId, LibDateUtils.getLibDateTime());
                    }

                    result.put("code", 0);
                    result.remove("im_code");//返回的结果中不需要此参数
                    WKCache.add_user(user_id, C.WKCacheUserField.live_id, "0");
                    if (live_type == C.LiveType.GAME) {//游戏直播要关闭直播流
                        close_live(account);
                    }
                    loger.info(String.format("----------endLive:account=%s,live_id=%d,endtime:%d,end_type:%d", account, live_id, endTime, end_type));
                }
            }
        }
       /* loger.info("end_live:"+result.toString());*/
        return result;
    }

    //赤票转可提现的钱

    private double changeTicketToMoney(long ticket) {
        double money = 0.00;
        //根据规则转化
        money = ticket * LibSysUtils.toDouble(WKCache.get_system_cache("CASH_RATE"));
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        String s = df.format(money);//返回的是String类型
        return LibSysUtils.toDouble(s);
    }

    /**
     * 退出房间
     *
     * @param user_id        观众的id
     * @param live_id        直播记录id
     * @param account        观众的account
     * @param live_stream_id 视频流id
     * @return
     */
    public JSONObject exit(int user_id, String account, String nickname, int level, int live_id, String live_stream_id) {
        JSONObject result = new JSONObject();
        int effect_level = LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.EFFECT_LEVEL), 50);

        //int attendance = LibSysUtils.getRandom(-2, 15);
        WKCache.del_room_user(live_id, account, -1, false);
        if (level >= effect_level) {//大于进场动效的等级推送退出通知
            result.put("audience_num", get_cache_attendance(live_id));//当前人数
            result.put("people_list", getRoomAccounts(live_id));
            result.put("account", account);
            result.put("live_id", live_id);
            result.put("im_code", IMCode.exit_room);//退出房间
            if (WkImClient.USEWKINGIM)
                WkImClient.forceLeaveRoom(live_stream_id, account);
            WkImClient.sendRoomMsg(live_stream_id, result.toString(), 1);//通知直播间内的人
        }
        String link_live_account = WKCache.get_room(live_id, "link_live_account");
        if (account.equals(link_live_account)) {//观众退出房间时判断是否为连麦人，如果是,则推送结束连麦
            String link_live_stream_id = WKCache.get_room(live_id, "link_live_stream_id");
            endLink(live_id, live_stream_id, link_live_stream_id, user_id, account, nickname);
        }
        //WKCache.incr_room_sort(live_id, -1);
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * web端进入房间
     *
     * @param user_id 用户user_id
     * @param account 主播的account
     * @param live_id
     * @return
     */
    public JSONObject webEnter(int user_id, String account, int live_id) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        LiveLogInfo liveLogInfo = liveLogInfoMapper.selectByPrimaryKey(live_id);
        if (liveLogInfo != null) {
            boolean is_follow = followInfoMapper.verifyIsFollowed(user_id, liveLogInfo.getUserId()) > 0; //判断我是否已经关注他
            result.put("follow_state", is_follow ? 1 : 0);
            result.put("live_id", liveLogInfo.getId());
            result.put("audience_num", liveLogInfo.getAudienceNum());
            result.put("city", liveLogInfo.getCity());
            result.put("live_cover", WkUtil.combineUrl(liveLogInfo.getLive_cover(), UploadTypeEnum.COVER, false));
            result.put("live_title", liveLogInfo.getLive_title());
            AccountInfo accountInfo = accountMapper.selectByPrimaryKey(liveLogInfo.getUserId());
            result.put("account", accountInfo.getAccount());
            result.put("nickname", accountInfo.getNickname());
            result.put("sex", accountInfo.getSex());
            result.put("pic_head_high", WkUtil.combineUrl(accountInfo.getPicheadUrl(), UploadTypeEnum.AVATAR, false));
            result.put("pic_head_low", WkUtil.combineUrl(accountInfo.getPicheadUrl(), UploadTypeEnum.AVATAR, true));
            int live_type = liveLogInfo.getLive_type();
            if (live_type == C.LiveType.HOT || live_type == C.LiveType.SHOP || live_type == C.LiveType.GAME)//公开直播才返回回放地址
                result.put("replay_url", liveLogInfo.getReplay_url());
            else
                result.put("replay_url", "");
            if (liveLogInfo.getLiveEnd() == 0) {//直播中
                result.put("audience_num", get_cache_attendance(live_id));
                result.put("live_stream_id", liveLogInfo.getLive_stream_id());
                result.put("live_type", liveLogInfo.getLive_type());  //直播类型，0：公开播，10：付费播；11：私密播
                result.put("diff", LibDateUtils.diffDatetime(liveLogInfo.getLiveStart(), LibDateUtils.getLibDateTime()));
                PlayAddress playAddress = playAddressMapper.selectByAccount(accountInfo.getAccount());
               /* if ((live_type == C.LiveType.HOT || live_type == C.LiveType.SHOP || live_type == C.LiveType.GAME) && playAddress != null) {//公开直播才返回地址
                    result.put("play_rtmp_url", playAddress.getRtmpurl());
                    result.put("play_hdl_url", playAddress.getHdlurl());
                    result.put("play_hls_url", playAddress.getHlsurl());
                } else {
                    result.put("play_rtmp_url", "");
                    result.put("play_hdl_url", "");
                    result.put("play_hls_url", "");
                }*/
                result.put("play_rtmp_url", TencentUtil.getRtmpPlayUrl(liveLogInfo.getLive_stream_id()));
                result.put("play_hdl_url", TencentUtil.getFlvPlayUrl(liveLogInfo.getLive_stream_id()));

                if (accountInfo.getCdn_option()==1){
                    //台湾Cdn
                    result.put("play_hls_url", TencentUtil.getHlsTWPlayUrl(liveLogInfo.getLive_stream_id()));
                }else {
                    result.put("play_hls_url", TencentUtil.getHlsPlayUrl(liveLogInfo.getLive_stream_id()));

                }



            } else {//回放
                result.put("live_stream_id", "");
                result.put("diff", LibDateUtils.diffDatetime(liveLogInfo.getDiff()));
                result.put("live_type", C.LiveType.RESULT_RECORDING);  //录播
            }
        }
        return result;
    }

    private int get_cache_attendance(int live_id) {
        int attendance = LibSysUtils.toInt(WKCache.get_room(live_id, "attendance"));
        if (attendance < 0)
            attendance = 0;
        return attendance;
    }

    //真实人数
    private int get_cache_real_audience(int live_id) {
        int realAudience = LibSysUtils.toInt(WKCache.get_room(live_id, "real_audience"));
        if (realAudience < 0)
            realAudience = 0;
        return realAudience;
    }

    /**
     * 获得直播间内的奖励信息
     *
     * @param user_id 用户ID
     * @param live_id 直播ID
     * @return
     */
    public JSONObject getLiveRoomAward(int user_id, int live_id, String lang_code) {
        JSONObject result = taskService.getLiveRoomAward(user_id, live_id, lang_code);
        return result;
    }

    /**
     * 领取直播间内的奖励
     *
     * @param user_id 用户ID
     * @param live_id 直播ID
     * @return
     */
    public JSONObject checkLiveRoomAward(int user_id, int live_id, int task_id, String lang_code) {
        JSONObject result = taskService.dailyReward(user_id, live_id, task_id, lang_code);
        return result;
    }

    public JSONObject enter(int user_id, String account, String avatar, String nickname, int level, int live_id,
                            String live_stream_id, String lang_code, boolean is_robot, double api_version, String project_name, int vip_level) {
        JSONObject result = new JSONObject();
        RoomCacheInfo roomCacheInfo = WKCache.get_room(live_id);
        JSONObject robotConfig = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.robot_config));
        int addNum = robotConfig.optInt("addNum", 3);
        if (roomCacheInfo != null) {
            if (blackLogMapper.selectUserRelation(roomCacheInfo.getUser_id(), user_id) > 0) {
                //System.out.println("aaaaa===="+LibSysUtils.getResultJSON(ResultCode.account_black_error, LibProperties.getLanguage(lang_code, "weking.lang.account.black.error")));
                return LibSysUtils.getResultJSON(ResultCode.account_black_error, LibProperties.getLanguage(lang_code, "weking.lang.account.black.error"));
            }
            //判断用户之前有没有被T出过
            if (WKCache.get_out_room_id(account,live_id)){
                return LibSysUtils.getResultJSON(ResultCode.out_room, LibProperties.getLanguage(lang_code, "weking.lang.live.out.room"));
            }


            int attendance = 1;
            //是否启用机器人
            boolean robot=LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.weking_config_robot), false);
            if (roomCacheInfo.getLive_type() == C.LiveType.PAY)//付费播
                robot = LibSysUtils.toBoolean(WKCache.get_system_cache("weking.config.vvip.robot"));
            if (roomCacheInfo.getLive_type() == C.LiveType.GAME)//游戏直播不加机器人
                robot = false;
            if (robot) {
                if (is_robot) {
                    attendance = LibSysUtils.getRandom(1, addNum);
                } else {
                    attendance = LibSysUtils.getRandom(1, addNum);
                }
            }
            int anchor_user_id = LibSysUtils.toInt(roomCacheInfo.getUser_id());//主播的user_id
            int is_official = LibSysUtils.toInt(userService.getUserInfoByUserId(anchor_user_id, "is_official"));

            //int total_ticket = pocketInfoMapper.getAnchorTicketbyid(anchor_user_id);
            boolean is_fllow = followInfoMapper.verifyIsFollowed(user_id, anchor_user_id) > 0; //判断我是否已经关注他
            int send_total_ticket = 0;
            if (!is_robot) {
                send_total_ticket = LibSysUtils.toInt(contributionInfoMapper.getSendTotalTicket(user_id, anchor_user_id));
            }
            WKCache.add_room_user(live_id, account, avatar, attendance, is_robot, send_total_ticket);
            result.put("audience_num", roomCacheInfo.getAttendance() + attendance);//当前人数
            int effect_level = LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.EFFECT_LEVEL), 50);
            JSONArray people_list = getRoomAccounts(live_id);
            if (is_robot || level >= effect_level) {//机器人和达到进场动效的等级才推送人员列表
                result.put("people_list", people_list);
            }
            result.put("account", account);
            result.put("nickname", nickname);
            result.put("pic_head_low", WkUtil.combineUrl(avatar, UploadTypeEnum.AVATAR, true));
            result.put("level", level);
            result.put("vip_level", vip_level);
           // result.put("effect", level >= effect_level ? 1 : 0);//进场特效
            result.put("effect", 0);//进场特效 新版改为无进场特效
            result.put("im_code", IMCode.enter_room);//进入房间
            LevelEffect levelEffect = levelEffectMapper.selectByLevel(level);
            if (levelEffect!=null){
                result.put("url", levelEffect.getUrl());//获得特效图片地址
            }else {
                result.put("url", "");//获得特效图片地址
            }
            //用户进入直播间，返回用户VIP等级
            JSONObject object = userService.getVipPrivilege(user_id);
            if (object!=null){
                result.put("vip_level", LibSysUtils.toInt(object.get("vip_level")));
                result.put("privilege", LibSysUtils.toString(object.get("privilege")));
            }else {
                result.put("vip_level", 0);
                result.put("privilege", "");
            }

            result.put("live_id", live_id);
            if (WkImClient.USEWKINGIM && !is_robot)//加入房间
                WkImClient.forceJoinRoom(roomCacheInfo.getLive_stream_id(), account);
            result.put("is_guard", user_id == liveGuardService.getLiveGuardId(live_id));
            int num = LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.is_push_room_num), 500);
            int userLevel = LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.is_push_user_level), 1);
            if (roomCacheInfo.getAttendance() >= num && level >= userLevel) {
                WkImClient.sendRoomMsg(roomCacheInfo.getLive_stream_id(), result.toString(), 1);//通知直播间内的人
            } else if (roomCacheInfo.getAttendance() < num) {
                WkImClient.sendRoomMsg(roomCacheInfo.getLive_stream_id(), result.toString(), 1);//通知直播间内的人
            }

            result.remove("im_code");
            result.remove("account");
            result.remove("nickname");
            result.remove("pic_head_low");
            result.put("code", ResultCode.success);
            result.put("people_list", people_list);
            result.put("follow_state", is_fllow ? 1 : 0);
            long month = LibDateUtils.getLibDateTime("yyyyMM");
            //result.put("total_ticket", total_ticket);//主播收到的币
            if (WKCache.get_income_num(month, roomCacheInfo.getAccount()) != null) {
                result.put("total_ticket", WKCache.get_income_num(month, roomCacheInfo.getAccount()).intValue());//主播收到的币
            } else {
                result.put("total_ticket", 0);//主播收到的币
            }
            boolean is_manager = !LibSysUtils.isNullOrEmpty(WKCache.get_room_managers(anchor_user_id, account));
            boolean is_banned = !LibSysUtils.isNullOrEmpty(WKCache.get_room_banned(anchor_user_id, account));
            if (roomCacheInfo.getLive_type() == C.LiveType.SHOP) { //电商直播返回store_id
                result.put("store_id", WKCache.get_user(anchor_user_id, "store_id"));
                List<String> goods_info = WKCache.get_live_goods(live_id, "goods_commonid", "goods_image", "goods_price", "coin_price");
                if (goods_info != null && goods_info.get(0) != null) {
                    JSONObject goodsObject = new JSONObject();
                    goodsObject.put("goods_commonid", LibSysUtils.toInt(goods_info.get(0)));
                    goodsObject.put("goods_price", new BigDecimal(goods_info.get(2)));
                    goodsObject.put("goods_image", WkUtil.combineUrl(goods_info.get(1), UploadTypeEnum.SHOP, true));
                    goodsObject.put("coin_price", new BigDecimal(goods_info.get(3)));
                    result.put("goods_info", goodsObject);
                }
            }
            result.put("link_stream_id", roomCacheInfo.getLink_live_stream_id());//连麦id
            result.put("anchor_account", roomCacheInfo.getAccount());
            result.put("anchor_nickname", roomCacheInfo.getNickname());
            result.put("live_type", roomCacheInfo.getLive_type());
            result.put("pic_head_low", WkUtil.combineUrl(roomCacheInfo.getAvatar(), UploadTypeEnum.AVATAR, true));
            result.put("link_account", roomCacheInfo.getLink_live_account());//连麦人的account
            result.put("is_manager", is_manager);//是否为管理员
            result.put("is_banned", is_banned);//是否被禁言
            result.put("announcement", WKCache.get_room(live_id, "announcement"));//直播公告
            if (roomCacheInfo.getLive_type() == C.LiveType.PROGRAM) { //节目直播
                result.put("program_slogan", roomCacheInfo.getProgram_slogan()); //节目直播口号
            }
            result.put("link_url", LibSysUtils.toString(roomCacheInfo.getLink_url())); // 商城/推广链接
            if (api_version > 3.1) {
                JSONArray banner_array = new JSONArray();
                JSONArray banner_result = systemService.getAdvertisement(4, project_name, lang_code).optJSONArray("adv_list");
                for (int i = 0; i < banner_result.size(); i++) {
                    JSONObject o = banner_result.getJSONObject(i);
                    JSONObject banner_object = new JSONObject();
                    if (!LibSysUtils.isNullOrEmpty(o.optString("ad_unit_id"))) {
                        banner_object.put("ad_unit_id", o.optString("ad_unit_id"));   //admob 广告id
                    }
                    banner_object.put("banner_image_url", o.optString("img_url"));//广告图片地址
                    banner_object.put("banner_url", o.optString("link_url"));//广告跳转地址
                    banner_object.put("banner_title", o.optString("title"));//广告跳标题
                    banner_array.add(banner_object);
                }
                result.put("banner_list", banner_array);
            } else {
                //api_version为3.2以上以下三个参数改成列表
                result.put("banner_image_url", getLiveBanner(account));//广告图片地址
                result.put("banner_url", WKCache.get_system_cache("live.banner.url"));//广告跳转地址
                result.put("banner_title", WKCache.get_system_cache("live.banner.title"));//广告跳标题
            }
            if (roomCacheInfo.isPause_live()) {//主播退到后台
                result.put("reminder", LibProperties.getLanguage(lang_code, "weking.lang.app.anchor.leave"));
            } else {
                result.put("reminder", LibProperties.getLanguage(lang_code, "weking.lang.app.live_reminder"));
            }
            /*if (!is_robot) {
                WKCache.incr_room_sort(live_id, roomCacheInfo.getLive_type(), 1);
            }*/
            //奖金池=================================
           /* long sca_gold_pool = WKCache.getSCAGoldPoolValue();
            result.put("sca_gold_pool", sca_gold_pool);*/
            PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(anchor_user_id);
            //不是签约主播  返回sca gold
            if (is_official != 1) {
                if (pocketInfo.getSca_gold().compareTo(new BigDecimal("0")) > 0) {
                    BigDecimal anchorAll = pocketInfo.getSca_gold();
                    result.put("sca_gold_anchor", anchorAll);//主播sca  gold
                }
            }
            int guessing_id = GameCache.get_guessing_id(roomCacheInfo.getUser_id());
            if (guessing_id > 0) {
                result.put("is_guessing_live", true);
            } else {
                result.put("is_guessing_live", false);
            }

            result.put("guard_info", liveGuardService.getLiveGuardInfo(live_id));
        } else {
            //直播已结束
            LiveLogInfo liveLogInfo = liveLogInfoMapper.selectByPrimaryKey(live_id);
            if (liveLogInfo == null) {
                loger.error("enter===live_id:" + live_id + "---userId:" + user_id);
                return LibSysUtils.getResultJSON(ResultCode.live_end);
            }
            int anchor_user_id = liveLogInfo.getUserId();
            boolean is_fllow = followInfoMapper.verifyIsFollowed(user_id, anchor_user_id) > 0; //判断我是否已经关注他
            long total_tiecket = consumeInfoMapper.getThisTimeTotalTiecket(anchor_user_id, live_id);
            //获得本次直播贡献榜
            List top_three = consumeInfoMapper.getTopThreeSender(anchor_user_id, live_id);
            JSONArray topThreeList = new JSONArray();
            for (int i = 0; i < top_three.size(); i++) {
                JSONObject topThree = new JSONObject();
                Map aa = (Map) top_three.get(i);
                String pichigh = LibSysUtils.toString(aa.get("pic_url"));
                int send_tickets = LibSysUtils.toInt(aa.get("totalSend"));
                topThree.put("pic_head_low", WkUtil.combineUrl(pichigh, UploadTypeEnum.AVATAR, true));
                topThree.put("send_tickets", send_tickets);
                topThreeList.add(topThree);
            }
            double getMoney = changeTicketToMoney(total_tiecket);
            String total_time = LibDateUtils.diffDatetime(liveLogInfo.getDiff());
            result.put("live_id", live_id);
            result.put("nickname", WKCache.get_user(anchor_user_id, "nickname"));
            result.put("pic_head_low", WkUtil.combineUrl(WKCache.get_user(anchor_user_id, "avatar"), UploadTypeEnum.AVATAR, true));
            result.put("account", WKCache.get_user(anchor_user_id, "account"));
            result.put("audience_num", liveLogInfo.getAudienceNum());
            result.put("tickets", total_tiecket);
            result.put("total_time", total_time);
            result.put("contribution_top3", topThreeList);
            result.put("get_money", getMoney);
            result.put("follow_state", is_fllow ? 1 : 0);
            result.put("code", ResultCode.live_end);
        }

//        System.out.println(result.toString());

        if (LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.live_vip_user_switch), false)) {
            String vipAccount = WKCache.get_system_cache(C.WKSystemCacheField.live_vip_user_account);
            if (vipAccount != null && vipAccount != "") {
                String vipAvatar = userService.getUserFieldByAccount(vipAccount, "avatar");
                vipAvatar = WkUtil.combineUrl(vipAvatar, UploadTypeEnum.AVATAR, true);
                if (vipAvatar != null && vipAvatar != "") {
                    result.put("vipAccount", vipAccount);
                    result.put("vipAvatar", vipAvatar);
                }
            }

        }
        return result;
    }

    public String getLiveBanner(String account) {
        String liveBanner = WKCache.get_system_cache("live.banner.image.url");
        if (LibSysUtils.isNullOrEmpty(liveBanner) && "appsme".equals(_projectName.toLowerCase())) {
            if (LibDateUtils.getLibDateTime() <= 20180501000000L) {
                liveBanner = "http://p.uc1.me/advertisement/4/default.png";
                long month = LibDateUtils.getLibDateTime("yyyyMM");
                Double anchorIncome = WKCache.get_income_num(month, account);
                if (anchorIncome != null) {
                    int incomeNum = anchorIncome.intValue();
                    //loger.info("DBadge:"+roomCacheInfo.getAccount()+"="+incomeNum);
                    if (incomeNum > 50000 && incomeNum <= 70000) {
                        liveBanner = "http://p.uc1.me/advertisement/4/DBadge.png";
                    } else if (incomeNum > 70000 && incomeNum <= 120000) {
                        liveBanner = "http://p.uc1.me/advertisement/4/CBadge.png";
                    } else if (incomeNum > 120000 && incomeNum <= 240000) {
                        liveBanner = "http://p.uc1.me/advertisement/4/BBadge.png";
                    } else if (incomeNum > 240000 && incomeNum <= 500000) {
                        liveBanner = "http://p.uc1.me/advertisement/4/ABadge.png";
                    } else if (incomeNum > 500000) {
                        liveBanner = "http://p.uc1.me/advertisement/4/SBadge.png";
                    }
                }
            }
        }
        return liveBanner;
    }

    //直播类别页列表接口
    public JSONObject getRecommendLive(int userId, String project_name, String lang_code) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
//        JSONObject new_live = getLiveList(userId, C.LiveType.NEW, "最新", project_name, 0, 10, lang_code, false, false);
//        if (new_live.getJSONArray("live_list").size() == 0) {
//            new_live = new JSONObject();
//        }
//        object.put("new_live", new_live);
//        JSONObject nearby_live = getLiveList(userId, C.LiveType.NEAR, "附近", project_name, 0, 10, lang_code, false, false);
//        if (nearby_live.getJSONArray("live_list").size() == 0) {
//            nearby_live = new JSONObject();
//        }
//        object.put("nearby_live", nearby_live);
//        JSONObject follow_live = getLiveList(userId, C.LiveType.FOLLOW, "关注", project_name, 0, 10, lang_code, false, false);
//        if (follow_live.getJSONArray("live_list").size() == 0) {
//            follow_live = new JSONObject();
//        }
//        object.put("follow_live", follow_live);
        object.put("tags", getLiveTags(userId));
        object.put("live_tags", getLiveTags(userId, lang_code));
//        System.out.println(object.toString());
        return object;

    }

    //用户录播列表
    public JSONObject getLivePlaybackList(int userId, String account, String project_name, int index, int count, String lang_code, boolean webGet, String channel) {
        AccountInfo accountInfo = accountMapper.selectByAccountId(account);
        JSONObject object;
        if (accountInfo != null) {
            object = getLiveList(accountInfo.getId(), C.LiveType.USER_RECORDING, "用户录播", project_name, index, count, lang_code,
                    webGet, userId == accountInfo.getId(), 1.0, channel);
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.account.exist_error"));
        }

        return object;
    }

    /**
     * 删除直播记录
     *
     * @param userId
     * @param liveId
     * @param status    1为删除,2为隐藏，0为显示
     * @param lang_code
     * @return
     */
    public JSONObject delLivePlayBack(int userId, int liveId, int status, String lang_code) {
        JSONObject object;
        int re = liveLogInfoMapper.updateLiveStatus(userId, liveId, status);
        if (re > 0) {
            object = LibSysUtils.getResultJSON(ResultCode.success);
        } else {
            UserCacheInfo userCacheInfo = WKCache.get_user(userId);
            object = LibSysUtils.getResultJSON(ResultCode.delete_error, LibProperties.getLanguage(lang_code, "weking.lang.delete.error"));
        }
        return object;
    }

    /**
     * 获取直播列表
     *
     * @param user_id    用户user_id
     * @param type       0：热门，1：关注，2：最新，3：附近，4：国家，5：Tag，6：推荐录播，7:用户录播记录
     *                   10：付费播；11：私密播;20:电商直播;30:游戏直播 50新人  60热榜
     * @param type_value type为国家时传国家名称，type为Tag时传Tag值，type为30时传game_category_id
     * @param index      分页起始位置
     * @param count      分页显示记录数
     * @param lang_code
     * @param webGet     是否是web端调用
     *                   channel  马甲包
     * @param is_self    是否为获取自己的记录(目前只有在获取回放记录时有用，当为获取自己的回放记录时则要把隐藏的记录返回)
     * @return
     */
    public JSONObject getLiveList(int user_id, int type, String type_value, String project_name, int index, int count,
                                  String lang_code, boolean webGet, boolean is_self, double api_version, String channel) {
        JSONObject object = WKCache.get_live_list_log(user_id, type, type_value, project_name, index, count);
        if (object != null) {
            return object;
        }
        if ("all".equals(type_value.toLowerCase())) {//如果是all则表示获取所有直播，则把type_value替换成空
            type_value = "";
        }
        //先获取官方直播间id
        int live_official_room = WKCache.get_live_official_room();
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        UserCacheInfo userCacheInfo = WKCache.get_user(user_id);
        String title = "";
        double[] latLng = null;
        switch (type) {
            case C.LiveType.HOT://热门
                title = LibProperties.getLanguage(lang_code, "weking.lang.app.live_tag0");

                if (live_official_room > 0) {
                    LiveLogInfo liveLog = liveLogInfoMapper.selectByPrimaryKey(live_official_room);
                    if (liveLog != null) {
                        Map<String, String> userInfoByUserId = userService.getUserInfoByUserId(liveLog.getUserId(), "account", "nickname", "avatar");

                        liveLog.setAccount(userInfoByUserId.get("account"));
                        liveLog.setNickname(userInfoByUserId.get("nickname"));
                        liveLog.setPicheadUrl(userInfoByUserId.get("avatar"));
                        if (liveLog.getHorizontal() == null) {
                            liveLog.setHorizontal(false);
                        }
                        JSONArray liveLogArray = new JSONArray();
                        setLiveListJSONArray(userCacheInfo, webGet, lang_code, liveLog, liveLogArray, channel);
                        result.put("is_official_live", liveLogArray.get(0));
                    }
                }
                break;
            case C.LiveType.FOLLOW://关注
                title = LibProperties.getLanguage(lang_code, "weking.lang.app.live_tag1");
                break;
            case C.LiveType.NEW://最新
                title = LibProperties.getLanguage(lang_code, "weking.lang.app.live_tag2");
                break;
            case C.LiveType.NEAR://附近
                title = LibProperties.getLanguage(lang_code, "weking.lang.app.live_tag3");
                int raidus = LibSysUtils.toInt(WKCache.get_system_cache("weking.nearby.radius"));
                latLng = LatLonUtil.getAround(userCacheInfo.getLat(), userCacheInfo.getLng(), raidus);
                break;
            case C.LiveType.COUNTRY://国家
                break;
            case C.LiveType.TAG:
                title = type_value;
                break;
            case C.LiveType.new_anchor://新人
                title = LibProperties.getLanguage(lang_code, "weking.lang.app.live_tag5");
                break;
            case C.LiveType.PAY://VIP
                title = "VIP";
                break;
            case C.LiveType.hot_rank: //热榜
                title = LibProperties.getLanguage(lang_code, "weking.lang.app.live_tag6");
                break;
            case C.LiveType.RECOMMENDED_RECORDING://推荐录播
                title = LibProperties.getLanguage(lang_code, "weking.lang.app.live_tag4");
                break;
            case C.LiveType.USER_RECORDING://用户录播记录
                long month = LibDateUtils.getLibDateTime("YYYYMM00000000");
                Map map = liveLogInfoMapper.getLiveTime(user_id, month);
                result.put("all_total", LibDateUtils.diffDatetime(LibSysUtils.toLong(map.get("all_total"))));
                result.put("month_total", LibDateUtils.diffDatetime(LibSysUtils.toLong(map.get("month_total"))));
                loger.info(String.format("livePlaybackList:%d,%d,%s", user_id, month, result.toString()));
            default:
                break;
        }
        boolean show_playback = (type == C.LiveType.HOT) && index == 0;  //显示推荐录播
        List<LiveLogInfo> list;
        if(type!=6) {
            list = inner_get_list(type, type_value, project_name, latLng, user_id, index, count, is_self, api_version, live_official_room);
            //获取所有直播
            if (api_version >= 3.9) {
                List<LiveLogInfo> liveList = get_live_list(project_name);
                for (LiveLogInfo live : liveList) {
                    live.setReal_audience(get_cache_real_audience(live.getId()));//真实人数
                }
                if (liveList.size() > 0) {
                    Collections.sort(liveList, new Comparator<LiveLogInfo>() {
                        public int compare(LiveLogInfo arg0, LiveLogInfo arg1) {
                            return arg1.getReal_audience() - arg0.getReal_audience();
                        }
                    });
                    LiveLogInfo liveLogInfo = liveList.get(0);
                    JSONArray liveLogInfoArray = new JSONArray();
                    setLiveListJSONArray(userCacheInfo, webGet, lang_code, liveLogInfo, liveLogInfoArray, channel);
                    if (blackLogMapper.selectUserRelation(user_id, liveList.get(0).getUserId()) < 1) {
                        result.put("liveLogInfoArray", liveLogInfoArray.get(0));
                    }
                    JSONObject obj = (JSONObject) liveLogInfoArray.get(0);
                    result.put("is_popularity_more", LibSysUtils.toString(obj.get("account")));
                }

            /*LiveLogInfo liveLogInfo = liveLogInfoMapper.getRemandLivingIsOfficial();
            if (liveLogInfo != null) {
                JSONArray liveLogInfoArray = new JSONArray();
                setLiveListJSONArray(userCacheInfo, webGet, lang_code, liveLogInfo, liveLogInfoArray,channel);
                if (blackLogMapper.selectUserRelation(user_id, liveLogInfo.getUserId()) < 1) {
                    result.put("liveLogInfoArray", liveLogInfoArray.get(0));
                }
            }*/
            }
        }else {
            show_playback=true;
            list=null;
        }
//        boolean show_playback = (type == 0 && index == 0 && list.size() == 0);//如果是首页，直播记录等于0时增加录播回放
        //if ("mongalaxy".equals(_projectName.toLowerCase()) || "nimpmos".equals(_projectName.toLowerCase())) {

        //show_playback = true;

        //}
        if (show_playback) {
            List<LiveLogInfo> list1 = inner_get_list(C.LiveType.RECOMMENDED_RECORDING, type_value, project_name, latLng,
                    user_id, 0, 30, is_self, api_version, 0);
            if (list1.size() > 0) {
                list = list == null ? new ArrayList<LiveLogInfo>() : list;
                Set<Integer> userIdSet = new HashSet<>();
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        userIdSet.add(list.get(i).getUserId());
                    }
                }
                for (int i = 0; i < list1.size(); i++) {
                    if (!userIdSet.contains(list1.get(i).getUserId())) {
                        list.add(list1.get(i));
                    }
                }
            }
        }
        JSONArray array = new JSONArray();
        JSONArray vipAndProgramLiveArray = new JSONArray();  // VIP和节目直播
        JSONArray otherLiveArray = new JSONArray();  // 其他类型直播
        for (LiveLogInfo liveLogInfo : list) {
            Integer anchorId = liveLogInfo.getUserId();
            //if (blackLogMapper.selectUserRelation(user_id, anchorId) <1) {
            if (liveLogInfo.getLive_type() == C.LiveType.PRIVATE || liveLogInfo.getLive_type() == C.LiveType.PROGRAM) {
                setLiveListJSONArray(userCacheInfo, webGet, lang_code, liveLogInfo, vipAndProgramLiveArray, channel);
            } else {
                setLiveListJSONArray(userCacheInfo, webGet, lang_code, liveLogInfo, otherLiveArray, channel);
            }
            //}
        }
        array.addAll(vipAndProgramLiveArray);  // VIP和节目直播
        array.addAll(otherLiveArray); // 其他类型直播
        result.put("title", title);
        result.put("live_list", array);
        result.put("type", type);
        /*if (type == C.LiveType.PAY && array.size() == 0) {//VVIP的提示
            result.put("vvip_msg", LibProperties.getLanguage(lang_code, "weking.lang.app.vvip.opentime"));
        }*/
        WKCache.set_live_list_log(user_id, type, type_value, project_name, index, count, result);
        return result;
    }

    // 设置直播列表JSON数组
    private void setLiveListJSONArray(UserCacheInfo userCacheInfo, boolean webGet, String lang_code, LiveLogInfo liveLogInfo, JSONArray jsonArray, String channel) {
        if (liveLogInfo == null) {
            return;
        }
        long diff = liveLogInfo.getDiff();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("live_id", liveLogInfo.getId());
        //String url = WKCache.get_system_cache("weking.config.pic.server");
        if (diff > 0) {
            jsonObject.put("audience_num", liveLogInfo.getAudienceNum());
        } else {
            jsonObject.put("audience_num", get_cache_attendance(liveLogInfo.getId()));
        }
        jsonObject.put("city", liveLogInfo.getCity());
        String avatar = LibSysUtils.toString(liveLogInfo.getPicheadUrl());
        String cover = liveLogInfo.getLive_cover();
        if (cover == null || cover.equals("")) {
            cover = avatar;
        }
        int live_type = liveLogInfo.getLive_type();//直播类型,0:正常直播，10：付费直播，11：私密直播，9：录播,20:电商，30：游戏
        if (live_type == C.LiveType.SHOP) { //直播展示商品图片
            jsonObject.put("live_goods", goodsService.getLiveListGoods(liveLogInfo.getUserId()));
            jsonObject.put("store_id", storeService.findStoreIdByUserId(liveLogInfo.getUserId()));
        }
        jsonObject.put("live_cover", WkUtil.combineUrl(cover, UploadTypeEnum.COVER, false));
        //jsonObject.put("live_title", liveLogInfo.getLive_title().equals("") ? LibProperties.getConfig("weking.config.project.name") : liveLogInfo.getLive_title());
        if ("yuanMi".equalsIgnoreCase(channel) && liveLogInfo.getLive_title().equalsIgnoreCase("AppsMe")) {
            jsonObject.put("live_title", "嬡秘");
        } else if ("yuanMi".equalsIgnoreCase(channel) && liveLogInfo.getLive_title().equalsIgnoreCase("yuanMi")) {
            jsonObject.put("live_title", "嬡秘");
        } else if (LibSysUtils.isNullOrEmpty(channel) && liveLogInfo.getLive_title().equalsIgnoreCase("yuanMi")) {
            jsonObject.put("live_title", "AppsMe");
        } else {
            jsonObject.put("live_title", liveLogInfo.getLive_title());
        }
        jsonObject.put("replay_url", liveLogInfo.getReplay_url());
        jsonObject.put("account", liveLogInfo.getAccount());
        jsonObject.put("tag_name", liveLogInfo.getTag_name());
        jsonObject.put("nickname", liveLogInfo.getNickname());
        jsonObject.put("pic_head_high", WkUtil.combineUrl(avatar, UploadTypeEnum.AVATAR, false));
        jsonObject.put("pic_head_low", WkUtil.combineUrl(avatar, UploadTypeEnum.AVATAR, true));
        jsonObject.put("status", liveLogInfo.getStatus());
        jsonObject.put("like_count", liveLogInfo.getLike_count());
        jsonObject.put("is_like", false);//是否点赞
        jsonObject.put("live_ticket", 0);//门票


        AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(liveLogInfo.getUserId());
        if (accountInfo!=null&&accountInfo.getCdn_option()==1){
            //台湾Cdn
            jsonObject.put("live_rtmp_url", "");
            jsonObject.put("live_flv_url", TencentUtil.getFlvTWPlayUrl(liveLogInfo.getLive_stream_id()));
        }else {
            jsonObject.put("live_rtmp_url", TencentUtil.getRtmpPlayUrl(liveLogInfo.getLive_stream_id()));
            jsonObject.put("live_flv_url", TencentUtil.getFlvPlayUrl(liveLogInfo.getLive_stream_id()));
        }



        if (liveLogInfo.getIs_new() == null) {
            jsonObject.put("is_new", false);//不是新人
        } else {
            jsonObject.put("is_new", liveLogInfo.getIs_new());//是新人
        }
        if (live_type == C.LiveType.PAY) { //付费播
            jsonObject.put("live_ticket", LibSysUtils.toInt(liveLogInfo.getLive_extend()));//门票
        }
        if (live_type == C.LiveType.PRIVATE) { //密码播的门票金额
            int live_ticket = LibSysUtils.toInt(WKCache.get_room(liveLogInfo.getId(), "live_ticket"));
            jsonObject.put("live_ticket", live_ticket);//门票
        }
        if (live_type == C.LiveType.PROGRAM) { //节目直播
            jsonObject.put("program_slogan", liveLogInfo.getProgram_slogan()); //节目直播口号
        }
        jsonObject.put("game_type", 0);
        if (live_type == C.LiveType.GAME) {
            jsonObject.put("game_type", GameCache.get_game_type(liveLogInfo.getId()));
        }
        jsonObject.put("is_horizontal", liveLogInfo.getHorizontal());//是否横屏
        jsonObject.put("game_category_name", liveLogInfo.getGame_category_name());//游戏直播的游戏名称
        live_type = diff == 0 ? live_type : C.LiveType.RESULT_RECORDING;//有直播时长的为录播
        jsonObject.put("live_type", live_type);
        if (diff == 0) {
            jsonObject.put("diff", LibDateUtils.diffDatetime(liveLogInfo.getLiveStart(), LibDateUtils.getLibDateTime()));
        } else {
            jsonObject.put("diff", LibDateUtils.diffDatetime(liveLogInfo.getLiveStart(), liveLogInfo.getLiveEnd()));
        }
        jsonObject.put("time_since_end", WkUtil.format(LibDateUtils.getDateTimeTick(liveLogInfo.getLiveStart(), LibDateUtils.getLibDateTime()), lang_code));
        if (!webGet) {
            jsonObject.put("live_stream_id", liveLogInfo.getLive_stream_id());
            jsonObject.put("live_start", liveLogInfo.getLiveStart());
            if (userCacheInfo != null) {
                try {
                    jsonObject.put("distance", LatLonUtil.getDistance(userCacheInfo.getLng(), userCacheInfo.getLat(), liveLogInfo.getLongitude(), liveLogInfo.getLatitude()) + "KM");
                } catch (Exception e) {
                    jsonObject.put("distance", "");
                    e.printStackTrace();
                }
            }
        }
        jsonArray.add(jsonObject);
    }

    private List<LiveLogInfo> inner_get_list(int type, String type_value, String project_name, double[] latLng, int user_id,
                                             int index, int count, boolean is_self, double api_version, int live_official_id) {
        List<LiveLogInfo> list = new ArrayList<>();
        switch (type) {
            case C.LiveType.HOT://热门
                if (api_version >= 3.8) {
                    list = liveLogInfoMapper.getHotLivingIsOfficial(project_name, index, count, live_official_id);
                } else {
                    list = liveLogInfoMapper.getHotLiving(project_name, index, count, live_official_id);
                }
                Integer newHour = LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.live_new_hour), 24);
                for (LiveLogInfo live : list) {
                    Map map = liveLogInfoMapper.getLiveTime(live.getUserId(), 0);
                    long allTime = LibSysUtils.toLong(map.get("all_total"));
                    //如果直播时长小于  指定时长  为新人
                    if (allTime < 3600000 * newHour) {
                        live.setIs_new(true);
                    } else {
                        live.setIs_new(false);
                    }

                }

                break;
            case C.LiveType.FOLLOW://关注
                list = liveLogInfoMapper.getFollowLiving(project_name, user_id, index, count);
                break;
            case C.LiveType.NEW://最新
                list = liveLogInfoMapper.getNewLiving(project_name, index, count);
                break;
            case C.LiveType.NEAR://附近
                list = liveLogInfoMapper.getNearbyLiving(project_name, latLng[1], latLng[3], latLng[0], latLng[2], index, count);
                break;
            case C.LiveType.COUNTRY://国家
                break;
            case C.LiveType.TAG:
                list = liveLogInfoMapper.getTagsLiving(project_name, type_value, index, count);
                break;
            case C.LiveType.RECOMMENDED_RECORDING://推荐录播
                list = liveLogInfoMapper.getRecommendRecord(project_name, index, count);
                break;
            case C.LiveType.USER_RECORDING://用户录播记录
                int status1 = 0;
                int status2 = 0;
                if (is_self) {
                    status2 = 2;//获取自己的直播记录时要把隐藏的也返回
                }
                list = liveLogInfoMapper.getPlayback(project_name, user_id, index, count, status1, status2);
                break;
            case C.LiveType.PAY://付费播
                if (LibSysUtils.isNullOrEmpty(type_value)) {
                    list = liveLogInfoMapper.getVIPLiving(project_name, index, count);
                } else {
                    list = liveLogInfoMapper.getVIPLivingByTag(project_name, type_value, index, count);
                }
                //如果无VIP直播就取录播
                if (list.size()==0){
                    list = liveLogInfoMapper.getVIPRecommendRecord(project_name, index, count);
                }
                break;
            case C.LiveType.PRIVATE://私密播
                if (LibSysUtils.isNullOrEmpty(type_value))
                    list = liveLogInfoMapper.getPrivacyLiving(project_name, index, count);
                else
                    list = liveLogInfoMapper.getPrivacyLivingByTag(project_name, type_value, index, count);
                break;
            case C.LiveType.SHOP:
                list = liveLogInfoMapper.getShopLiveList(project_name, index, count);
                break;
            case C.LiveType.GAME:
                if ("7".equals(type_value)) //7为综合游戏，表示所有
                    type_value = "";
                list = liveLogInfoMapper.getGameLiveList(project_name, type_value, index, count);
                break;

            case C.LiveType.new_anchor://新人
                List<LiveLogInfo> list_old = liveLogInfoMapper.getAllLivingIsOfficial(project_name, live_official_id);
                Integer new_Hour = LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.live_new_hour), 24);
                for (LiveLogInfo live : list_old) {
                    Map map = liveLogInfoMapper.getLiveTime(live.getUserId(), 0);
                    long allTime = LibSysUtils.toLong(map.get("all_total"));
                    //如果直播时长小于  指定时长  为新人
                    if (allTime < 3600000 * new_Hour) {
                        live.setIs_new(true);
                        list.add(live);
                    }
                }
                break;
            case C.LiveType.hot_rank://热榜
                int sorts = LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.live_hot_sorts), 50);
                list = liveLogInfoMapper.getHotRankList(project_name, index, count, sorts, live_official_id);
                break;
        }
        return list;
    }


    private List<LiveLogInfo> get_live_list(String project_name) {
        List<LiveLogInfo> list = null;
        list = liveLogInfoMapper.getAllLivingIsOfficial(project_name, 0);
        return list;
    }


    //获得直播tags列表
    public JSONArray getLiveTags(int userId) {
        String is_official = userService.getUserInfoByUserId(userId, "is_official");
        int tag_type = 0;
        if (LibSysUtils.toInt(is_official) == 0) {
            tag_type = 1;
        }
        JSONArray array = WKCache.get_room_live_tag(tag_type);
        if (array != null) {
            return array;
        }
        List<LiveTag> tags = liveTagMapper.getAll(tag_type);
        JSONArray jsonArray = new JSONArray();
        //jsonArray.add("All");

        for (LiveTag tag : tags) {
            jsonArray.add(tag.getTagName());
        }
        WKCache.set_room_live_tag(tag_type, jsonArray);
        return jsonArray;
    }

    //获得直播tags列表
    public JSONArray getLiveTags(int userId, String lang_code) {
        //String is_official = userService.getUserInfoByUserId(userId,"is_official");
        JSONArray array = WKCache.get_live_all_tag();
        if (array != null) {
            return array;
        }
        List<LiveTag> tags = liveTagMapper.getNewAll();
        JSONArray jsonArray = new JSONArray();
        List<LiveLogInfo> list;
        for (LiveTag tag : tags) {
            switch (tag.getTag_value()) {
                case C.LiveType.PAY://付费播
                    list = liveLogInfoMapper.getVIPLiving("", 0, 99);
                    addTagJsonInArray(list, jsonArray, tag, lang_code);
                    break;
                case C.LiveType.PRIVATE://私密播
                    list = liveLogInfoMapper.getPrivacyLiving("", 0, 99);
                    addTagJsonInArray(list, jsonArray, tag, lang_code);
                    break;
                case C.LiveType.SHOP:
                    list = liveLogInfoMapper.getShopLiveList("", 0, 99);
                    addTagJsonInArray(list, jsonArray, tag, lang_code);
                    break;
                case C.LiveType.GAME:
                    list = liveLogInfoMapper.getGameLiveList("", "", 0, 99);
                    addTagJsonInArray(list, jsonArray, tag, lang_code);
                    break;
                default:
                    list = liveLogInfoMapper.getTagsLiving("", tag.getTagName(), 0, 99);
                    addTagJsonInArray(list, jsonArray, tag, lang_code);
                    break;
            }
        }
        WKCache.set_live_all_tag(jsonArray);
        return jsonArray;
    }

    private void addTagJsonInArray(List<LiveLogInfo> list, JSONArray jsonArray, LiveTag tag, String lang_code) {
        if (list.size() > 0) {
            JSONObject jsonObject;
            switch (lang_code) {
                case "ms":
                    jsonObject = new JSONObject();
                    jsonObject.put("tag_name", tag.getMs_name());
                    break;
                case "en_US":
                    jsonObject = new JSONObject();
                    jsonObject.put("tag_name", tag.getEn_name());
                    break;
                case "zh_TW":
                    jsonObject = new JSONObject();
                    jsonObject.put("tag_name", tag.getTw_name());
                    break;
                default:
                    jsonObject = new JSONObject();
                    jsonObject.put("tag_name", tag.getTagName());
                    break;
            }
            jsonObject.put("tag_type", tag.getTag_value());
            jsonArray.add(jsonObject);
        }
    }

    /**
     * 直播间内修改公告标题和封面
     *
     * @param live_id        直播id
     * @param announcement   直播公告
     * @param live_title     直播标题
     * @param live_cover     直播封面
     * @param program_slogan 节目直播口号
     * @param link_url       商城/推广链接
     * @return
     */
    public JSONObject setLiveAnnouncement(int live_id, String announcement, String live_title, String live_cover, String program_slogan, String link_url, double api_version) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        RoomCacheInfo roomCacheInfo = WKCache.get_room(live_id);
        if (roomCacheInfo != null) {
            WKCache.add_room(live_id, "announcement", announcement);
            WKCache.add_room(live_id, "live_title", live_title);
            WKCache.add_room(live_id, "live_cover", live_cover);
            WKCache.add_room(live_id, "program_slogan", program_slogan);
            WKCache.add_room(live_id, "link_url", link_url);
            JSONObject msg_result = new JSONObject();
            msg_result.put("im_code", IMCode.live_announcement);
            msg_result.put("msg", announcement);
            msg_result.put("program_slogan", program_slogan);
            msg_result.put("link_url", link_url);
            msg_result.put("live_id", live_id);//推送live_id给客户端
            WkImClient.sendRoomMsg(roomCacheInfo.getLive_stream_id(), msg_result.toString(), 1);//推送给房间内的人员
            if (api_version > 3.2) {
                LiveLogInfo info = new LiveLogInfo();
                info.setId(live_id);
                info.setLive_title(live_title);
                info.setLive_cover(live_cover);
                info.setProgram_slogan(program_slogan); // 节目直播口号
                info.setLink_url(link_url); // 商城/推广链接
                liveLogInfoMapper.updateByPrimaryKeySelective(info);
            }
        }
        return result;
    }

    /**
     * 用户直播间内发言
     *
     * @param user_id        用户的id
     * @param live_stream_id 视频流id
     * @param msg            消息内容
     * @param is_barrage     是否为弹幕true为弹幕
     * @return
     */
    public JSONObject sendMsg(int user_id, String account, String nickname, String avatar, int level, int live_id,
                              String live_stream_id, String msg, boolean is_barrage, String lang_code, double api_version, boolean is_robot, int vip_level, String referToAccount) {
        // TODO: 2017/3/1 过滤敏感词
        if (blackLogMapper.selectUserRelation(LibSysUtils.toInt(WKCache.get_room(live_id, "user_id")), user_id) > 0) {
            return LibSysUtils.getResultJSON(ResultCode.account_black_error, LibProperties.getLanguage(lang_code, "weking.lang.account.black.error"));
        }
        RoomCacheInfo roomCacheInfo = WKCache.get_room(live_id);
        if (roomCacheInfo == null) {
            return LibSysUtils.getResultJSON(ResultCode.live_send_gift_error);
        }
        if (is_barrage) {
            int i = pocketInfoMapper.deductDiamondByUserid(1, user_id);//扣钱
            if (i > 0) {//扣钱成功
                pocketInfoMapper.increaseTicketByUserid(1, 0);//弹幕的钱加到id为0的主播身上
                levelService.putExp(user_id, 1, 1, 0);//增加经验值
                //插入消费记录表
                long sendtime = LibDateUtils.getLibDateTime();
                ConsumeInfo consumeLogInfo = new ConsumeInfo();
                consumeLogInfo.setSendId(user_id);
                consumeLogInfo.setReceiveId(0);
                consumeLogInfo.setSendDiamond(1);
                consumeLogInfo.setSendTime(sendtime);
                consumeLogInfo.setGiftId(0);
                consumeLogInfo.setLiveRecordId(live_id);
                String ratio = userService.getUserInfoByUserId(user_id, "ratio");
                consumeLogInfo.setRatio(new BigDecimal(ratio).setScale(8));
                consumeInfoMapper.insertSelective(consumeLogInfo);
            } else {
                return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
            }
        }
        boolean isManager = !LibSysUtils.isNullOrEmpty(WKCache.get_room_managers(roomCacheInfo.getUser_id(), account));//查询  发言人是不是管理员
        if (isManager) {
            is_barrage = true;
        }
        JSONObject result = new JSONObject();
        result.put("im_code", IMCode.send_msg);
        result.put("account", account);
        result.put("referToAccount", referToAccount);
        result.put("msg", WordFilter.doFilter(msg));
        result.put("is_barrage", is_barrage);
        result.put("pic_head_low", WkUtil.combineUrl(avatar, UploadTypeEnum.AVATAR, true));
        result.put("nickname", nickname);
        result.put("level", level);
        result.put("vip_level", vip_level);
        result.put("live_id", live_id);//推送live_id给客户端
        result.put("is_guard", user_id == liveGuardService.getLiveGuardId(live_id));


        //用户进入直播间，返回用户VIP等级
        JSONObject object = userService.getVipPrivilege(user_id);
        if (object!=null){
            result.put("privilege", LibSysUtils.toString(object.get("privilege")));
        }else {
            result.put("privilege", "");
        }

        result.put("is_manager", isManager);
        live_stream_id = roomCacheInfo.getLive_stream_id();
        JSONObject temp = LibSysUtils.getResultJSON(0);
        if (get_sendmsg_state()) {
            WkImClient.sendRoomMsg(live_stream_id, result.toString(), 1);
            //result.put("live_id", 1);//如果是后台推送消息则把live_id设置为1，这样前端推送时其他房间会过滤就会收不到消息
        } else
            temp.put("im_msg", "\"" + result.toString() + "\"");
        int leftDiamond = pocketInfoMapper.getSenderLeftDiamondbyid(user_id);//获取剩余钻石
        temp.put("my_diamonds", leftDiamond);
       /* if (!is_robot) {
            int live_type = LibSysUtils.toInt(WKCache.get_room(live_id, "live_type"));
            WKCache.incr_room_sort(live_id, live_type, 5);
        }*/
        return temp;
    }

    /**
     * 送礼物
     *
     * @param user_id   观众userid
     * @param live_id   直播记录id
     * @param gift_id   礼物id
     * @param count_num 连发数量
     * @return
     */
    @Transactional
    public JSONObject sendGift(int user_id, int level, String account, String nickname, String avatar, int live_id, int roomId, int gift_id, int count_num, String lang_code, double api_version, int vip_level) {
        if (roomId == 0 && live_id == 0) {
            return LibSysUtils.getResultJSON(ResultCode.live_send_gift_error, LibProperties.getLanguage(lang_code, "weking.lang.gift_send_error"));
        }
        int anchor_user_id;
        String live_stream_id = "";
        String anchorNickname = "";
        String anchorAvatar = "";
        int liveType;
        if (roomId == 0) {
            LiveLogInfo liveLogInfo = liveLogInfoMapper.selectByPrimaryKey(live_id);

          /*  RoomCacheInfo roomCacheInfo = WKCache.get_room(live_id);*/
            /*if (roomCacheInfo == null) {
                loger.info("发送礼物失败原因：房间缓存为null：==" + roomId);
                return LibSysUtils.getResultJSON(ResultCode.live_send_gift_error, LibProperties.getLanguage(lang_code, "weking.lang.gift_send_error"));
            }*/
            anchor_user_id = liveLogInfo.getUserId();
            live_stream_id = liveLogInfo.getLive_stream_id();
            AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(anchor_user_id);
            if (accountInfo!=null){
                anchorNickname = accountInfo.getNickname();
                anchorAvatar = accountInfo.getPicheadUrl();
            }

            liveType = liveLogInfo.getLive_type();
        } else {
            VideoChat info = videoChatMapper.findVideoChatById(roomId);
            if (info == null || info.getEndTime() > 0) {
                return LibSysUtils.getResultJSON(ResultCode.live_send_gift_error, LibProperties.getLanguage(lang_code, "weking.lang.gift_send_error"));
            }
            if (info.getUserId() == user_id) {
                anchor_user_id = info.getOtherId();
            } else {
                anchor_user_id = info.getUserId();
            }
            liveType = C.LiveType.NEW;
        }
        GiftInfo giftInfo = giftInfoMapper.selectByPrimaryKey(gift_id);
        if (giftInfo != null) {
            int gift_ticket = giftInfo.getPrice();
            int giftId_temp=gift_id;
            if (gift_ticket != 0) {
                if (giftInfo.getType() == 8) {
                    gift_id = getRandomGiftId(gift_ticket);
                    giftInfo = giftInfoMapper.selectByPrimaryKey(gift_id);
                }
                JSONObject object = pocketService.consume(user_id, anchor_user_id, gift_ticket, giftId_temp, live_id, lang_code);
                if (object.getInt("code") == ResultCode.success) { //扣款成功

                   /* if (giftInfo.getType()==6){
                        //加钱时候  充值金额存入缓存 VIP
                        payService.addCache(giftInfo.getPrice(),user_id);
                    }*/
                    Boolean flg = getActivity(giftInfo.getId());
                    if (flg){
                        //送活动礼物的时候处理
                        taskService.dayTaskHandle(C.TaskId.send,user_id, 1);
                    }

                    levelService.putExp(user_id, gift_ticket, 1, anchor_user_id);//增加经验值
                    //修改或者新增 贡献榜 先去查询是否已经有这条记录
                    int m = contributionInfoMapper.updateContirbution(user_id, anchor_user_id, gift_ticket);
                    if (m == 0) { //没有数据，insert
                        long sendTicket = gift_ticket;
                        ContributionInfo record = new ContributionInfo();
                        record.setSendId(user_id);
                        record.setAnchorId(anchor_user_id);
                        record.setSendTotalTicket(sendTicket);
                        contributionInfoMapper.insert(record);
                    }
                    JSONObject result = new JSONObject();

                    //int anchorTicket = pocketInfoMapper.getAnchorTicketbyid(anchor_user_id);//获取主播的

                    JSONObject coin_proportion = SystemConstant.coin_proportion;
                    JSONObject reward_scagold_rate = SystemConstant.reward_scagold_rate;
                    int emo2scagold = coin_proportion.optInt("emo2scagold", 0) * gift_ticket;  // 总sca gold
                    double userConvNum = emo2scagold * reward_scagold_rate.optDouble("receiver", 0);//用户得到的sca gold
                    double anchorConvNum = emo2scagold * reward_scagold_rate.optDouble("sender", 0);//主播得到的sca gold

                    //主播账号
                    Map<String, String> userMap = userService.getUserInfoByUserId(anchor_user_id, "account", "nickname", "is_official");
                    String anchorAccount = userMap.get("account");
                    String anchorName = userMap.get("nickname");
                    PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(anchor_user_id);
                    int is_official = LibSysUtils.toInt(userMap.get("is_official"));
                    if (is_official != 1) {
                        if (pocketInfo.getSca_gold().compareTo(new BigDecimal("0")) > 0) {
                            BigDecimal anchorAll = pocketInfo.getSca_gold();
                            result.put("anchorAll", anchorAll);//主播sca  gold
                        } else {
                            result.put("anchorAll", new BigDecimal("0"));//主播sca  gold
                        }
                    }

                    //if(giftInfo.getType()==(short)6) {
                    result.put("anchorAccount", anchorAccount);//主播账号
                    result.put("anchorName", anchorName);//主播昵称
                    result.put("userConvNum", userConvNum);//用户送礼获得sca gold
                    result.put("anchorConvNum", anchorConvNum);//主播获得sca gold
                    //}

                    result.put("im_code", IMCode.send_gif);
                    result.put("account", account);
                    result.put("nickname", nickname);
                    result.put("pic_head_low", WkUtil.combineUrl(avatar, UploadTypeEnum.AVATAR, true));
                    // result.put("anchor_tickets", anchorTicket);//总收入
                    long month = LibDateUtils.getLibDateTime("yyyyMM");
                    result.put("anchor_tickets", WKCache.get_income_num(month, anchorAccount));//主播收到的币
                    result.put("gift_id", gift_id);
                    result.put("send_time", LibDateUtils.getLibDateTime());
                    if (api_version <= 4.1 && giftInfo.getType() == 7) {
                        result.put("type", 1);
                    } else {
                        result.put("type", giftInfo.getType());
                    }
                    result.put("count_num", count_num);
                    result.put("gift_img", WkUtil.combineUrl(giftInfo.getGift_image(), UploadTypeEnum.AVATAR, false));
                    result.put("gift_name", giftInfo.getName());
                    result.put("level", level);
                    result.put("vip_level", vip_level);
                    result.put("live_id", live_id);
                    result.put("room_id", roomId);
                    result.put("is_guard", user_id == liveGuardService.getLiveGuardId(live_id));
                    result.put("gift_price", giftInfo.getPrice());


                    //用户进入直播间，返回用户VIP等级
                    JSONObject vipObj = userService.getVipPrivilege(user_id);
                    if (vipObj!=null){
                        result.put("privilege", LibSysUtils.toString(vipObj.get("privilege")));
                    }else {
                        result.put("privilege", "");
                    }


                    if (get_sendmsg_state()) {
                        if (roomId > 0) {
                            live_stream_id = LibSysUtils.toString(roomId);
                        }
                        WkImClient.sendRoomMsg(live_stream_id, result.toString(), 1);
                        result.put("live_id", 1);//如果是后台推送消息则把live_id设置为1，这样前端推送时其他房间会过滤就会收不到消息
                        result.put("room_id", 1);
                    }
                    if (live_id > 0) {
                        int MIN_PD_GIFT = LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.send_gift_pd), 5000);
                        int MIN_TT_GIFT = LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.send_gift_tt), 50000);
                        IMPushUtil.sendGlobalMsgGift(live_id, live_stream_id, nickname,
                                giftInfo.getName(), anchorNickname,
                                giftInfo.getGift_image(), giftInfo.getPrice(), anchorAvatar,
                                0, MIN_PD_GIFT, MIN_TT_GIFT);
                    }
                    result.put("im_msg", "\"" + result.toString() + "\"");//前端判断如果有im_msg则推送
                    result.remove("live_id");
                    result.remove("im_code");
                   /* result.remove("account");
                    result.remove("nickname");*/
                  /*  result.remove("pic_head_low");
                    result.remove("is_continue");*/
                   /* result.remove("count_num");*/
                    result.remove("gift_img");
                    /*result.remove("gift_name");*/

                    //礼物赠送成功  去判断是否中奖。
                    JSONObject is_send_gift_config = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.is_send_gift_config));
                    boolean on_off = is_send_gift_config.optBoolean("on_off", false);
                    if (on_off){
                        int times = is_send_gift_config.optInt("times", 1); //中奖倍数
                        double win_probability = is_send_gift_config.optDouble("win_probability", 0.01);//中奖几率
                        boolean resultInner = getResultInner(win_probability, times);
                        //如果中奖就加钱 且推IM消息
                        if (resultInner){
                            int winPrice = times * gift_ticket;//中奖获得金额
                            int re = pocketInfoMapper.increaseDiamondByUserId(user_id, winPrice);//加钱
                            if (re > 0) {
                                UserGain gain = UserGain.getGain(user_id, C.UserGainType.live_win, winPrice, live_id);
                                userGainMapper.insertSelective(gain);
                            }
                            result.put("is_win",true);
                            result.put("times",times);
                            JSONObject obj = new JSONObject();

                            String msg = String.format(LibProperties.getLanguage("weking.lang.live.send.gift.win"), WkUtil.getShortName(nickname),
                                    WkUtil.getShortName(giftInfo.getName()), times);
                            obj.put("im_code", IMCode.send_gif_win);
                            obj.put("account", account);
                            obj.put("times",times);
                            obj.put(C.ImField.msg, msg);
                            obj.put(C.ImField.live_id, live_id);

                            WkImClient.sendRoomMsg(live_stream_id, obj.toString(), 1);
                        }
                    }else {
                        result.put("is_win",false);
                        result.put("times",0);
                    }

                    int leftDiamond = pocketInfoMapper.getSenderLeftDiamondbyid(user_id);//获取剩余钻石

                    result.put("my_diamonds", leftDiamond);
                    result.put("code", 0);
                    if (live_id > 0) {
                        WKCache.add_room_rank(live_id, LibSysUtils.toDouble(gift_ticket), account);//加入到本场消费记录中
                        //WKCache.incr_room_sort(live_id, liveType, gift_ticket);
                    }
                    return result;
                } else {
                    return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
                }
            }
        }
        return LibSysUtils.getResultJSON(ResultCode.live_send_gift_error, LibProperties.getLanguage(lang_code, "weking.lang.gift_send_error"));
    }


    public JSONObject switchLive(int user_id, String account, String nickname, int level, int exit_live_id,
                                 String exit_live_stream_id, int enter_live_id, String avatar, String lang_code, double api_version, int vip_level) {
        exit(user_id, account, nickname, level, exit_live_id, exit_live_stream_id);

        return enter(user_id, account, avatar, nickname, level, enter_live_id, "", lang_code, false, api_version, "", vip_level);

    }

    /**
     * 主播退到后台
     *
     * @param user_id        主播userid
     * @param live_id        直播记录id
     * @param live_stream_id 视频流id
     * @return
     */
    public JSONObject pauseLive(int user_id, String account, int live_id, String live_stream_id, String lang_code) {
        if (live_id > 0) {//记录主播退到后台
//            SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMddHHmmss");
//            String time = dfs.format(Calendar.getInstance().getTime());
            WKCache.add_room(live_id, "pause_live", "1");
        }
        JSONObject sendMsg = new JSONObject();
        sendMsg.put("im_code", IMCode.to_background);
        sendMsg.put("live_id", live_id);
        WkImClient.sendRoomMsg(live_stream_id, sendMsg.toString(), 1);
        loger.info(String.format("----------pauseLive:account=%s,live_id=%d,live_stream_id=%s", account, live_id, live_stream_id));
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 主播从后台回到前端
     *
     * @param user_id        主播userid
     * @param live_id        直播记录id
     * @param live_stream_id 视频流id
     * @return
     */
    public JSONObject resumeLive(int user_id, String account, int live_id, String live_stream_id, String lang_code) {
        if (live_id > 0) {
            //SimpleDateFormat dfs = new SimpleDateFormat("yyyyMMddHHmmss");
            //String time = dfs.format(Calendar.getInstance().getTime());
            WKCache.add_room(live_id, "pause_live", "0");
        }
        JSONObject sendMsg = new JSONObject();
        sendMsg.put("im_code", IMCode.to_front);
        sendMsg.put("live_id", live_id);
        WkImClient.sendRoomMsg(live_stream_id, sendMsg.toString(), 1);
        loger.info(String.format("----------resumeLive:account=%s,live_id=%d,live_stream_id=%s", account, live_id, live_stream_id));
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 设置管理员
     *
     * @param live_id         直播记录id
     * @param live_stream_id  流id
     * @param manager_account 管理员account
     * @param authorization   true为设置为管理员，false为解除解除管理员
     * @param lang_code       语言code
     * @return
     */
    public JSONObject setManager(int live_id, String live_stream_id, String manager_account, Boolean authorization, String lang_code) {
        List<String> user_info = WKCache.getUserByAccount(manager_account, "nickname", "level");
        int anchor_user_id = LibSysUtils.toInt(WKCache.get_room(live_id, "user_id"));
        String user_nickname = user_info.get(0);
        int level = LibSysUtils.toInt(user_info.get(1));
        JSONObject sender = new JSONObject();
        sender.put("account", manager_account);
        sender.put("nickname", user_nickname);
        sender.put("im_code", IMCode.set_manager);
        sender.put("live_id", live_id);
        sender.put("level", level);
        String msg;
        if (authorization) {
            WKCache.add_room_manager(anchor_user_id, manager_account, user_nickname);
            sender.put("authorization", true);
            msg = LibProperties.getLanguage(lang_code, "weking.lang.app.manager.set.success");
        } else {
            WKCache.del_room_manager(anchor_user_id, manager_account);
            sender.put("authorization", false);
            msg = LibProperties.getLanguage(lang_code, "weking.lang.app.manager.set.cancel");
        }
        WkImClient.sendRoomMsg(live_stream_id, sender.toString(), 1);
        return LibSysUtils.getResultJSON(ResultCode.success, msg);
    }

    /**
     * 获取管理员
     *
     * @param user_id   主播id
     * @param lang_code int类型  type  0是管理员列表  1是禁言列表
     * @return
     */
    public JSONObject getManagerList(int user_id, String lang_code, int type, int live_id) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        Map<String, String> map = null;
        if (type == 0) {
            map = WKCache.get_room_managers(user_id);
        } else if (type == 1) {
            String anchor_account = WKCache.get_room(live_id, "account");
            AccountInfo accountInfo = accountMapper.selectByAccountId(anchor_account);
            user_id = accountInfo.getId();
            map = WKCache.get_room_bannedPostList(user_id);
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            JSONObject dataResult = new JSONObject();
            dataResult.put("account", entry.getKey());
            dataResult.put("nickname", entry.getValue());
            List<String> user_info = WKCache.getUserByAccount(entry.getKey(), "avatar", "level");
            dataResult.put("pic_head_low", WkUtil.combineUrl(user_info.get(0), UploadTypeEnum.AVATAR, true));
            dataResult.put("level", LibSysUtils.toInt(user_info.get(1)));
            array.add(dataResult);
        }
        result.put("list", array);
        return result;
    }


    /**
     * 禁言
     *
     * @param live_id        直播记录id
     * @param live_stream_id 流id
     * @param user_account   用户account
     * @param forbid         true为禁言，false为解除禁言
     * @param lang_code      语言code
     * @return
     */
    public JSONObject bannedPost(int live_id, String live_stream_id, String user_account, Boolean forbid, String lang_code) {
        String anchor_account = WKCache.get_room(live_id, "account");
        live_stream_id = WKCache.get_room(live_id, "live_stream_id");
        if (anchor_account.equals(user_account)) {//如果是禁言主播则直接返回
            return LibSysUtils.getResultJSON(ResultCode.success);
        }
        List<String> user_info = WKCache.getUserByAccount(user_account, "nickname", "level");
        int anchor_user_id = LibSysUtils.toInt(WKCache.get_room(live_id, "user_id"));
        String user_nickname = user_info.get(0);
        int level = LibSysUtils.toInt(user_info.get(1));
        JSONObject sender = new JSONObject();
        sender.put("live_id", live_id);
        sender.put("account", user_account);
        sender.put("nickname", user_nickname);
        sender.put("level", level);
        sender.put("im_code", IMCode.banned_post);
        String msg;
        if (forbid) {
            WKCache.add_room_banned(anchor_user_id, user_account, user_nickname);
            sender.put("forbid", true);
            msg = LibProperties.getLanguage(lang_code, "weking.lang.app.info.banned");
        } else {
            WKCache.del_room_banned(anchor_user_id, user_account);
            sender.put("forbid", false);
            msg = LibProperties.getLanguage(lang_code, "weking.lang.app.info.unbanned");
        }
        WkImClient.sendRoomMsg(live_stream_id, sender.toString(), 1);
        return LibSysUtils.getResultJSON(ResultCode.success, msg);
    }

    /**
     * @param live_id
     * @param user_id
     * @param account
     * @param user_account 被查询者的account
     * @return
     */
    public JSONObject getUserinfo(int live_id, int user_id, String account, String user_account) {
        int anchor_user_id = LibSysUtils.toInt(WKCache.get_room(live_id, "user_id"));
        boolean imManager = !LibSysUtils.isNullOrEmpty(WKCache.get_room_managers(anchor_user_id, account));//查询人是不是管理员
        boolean isManager = !LibSysUtils.isNullOrEmpty(WKCache.get_room_managers(anchor_user_id, user_account));//被查询人是不是管理员
        if (!imManager) {
            //如果不是管理员再判断是不是主播
            String anchor_account = WKCache.get_room(live_id, "account");
            imManager = account.equals(anchor_account);
        }
        JSONObject userinfo = userService.userInfo(user_id, user_account);
        if (userinfo.optInt("code") == 0) {
            userinfo.put("im_manager", imManager);
            userinfo.put("is_manager", isManager);
            userinfo.put("is_bannedPost", !LibSysUtils.isNullOrEmpty(WKCache.get_room_banned(anchor_user_id, user_account)));
            //PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(user_id);
            //userinfo.put("send_diamonds", pocketInfo.getAll_diamond() - pocketInfo.getTotalDiamond());//送出的钻石
            return userinfo;
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    //申请连麦
    public JSONObject applyLink(int live_id, int audience_user_id, String audience_account, String audience_nickname, int level, String lang_code) {
        RoomCacheInfo roomCacheInfo = WKCache.get_room(live_id);
        String live_stream_id = roomCacheInfo.getLive_stream_id();
        String anchor_account = roomCacheInfo.getAccount();
        String link_live_account = WKCache.get_room(live_id, "link_live_account");
        if (blackLogMapper.selectUserRelation(roomCacheInfo.getUser_id(), audience_user_id) > 0) {
            return LibSysUtils.getResultJSON(ResultCode.account_black_error, LibProperties.getLanguage(lang_code, "weking.lang.account.black.error"));
        }
        if (!LibSysUtils.isNullOrEmpty(link_live_account)) {
            return LibSysUtils.getResultJSON(ResultCode.live_islinking, LibProperties.getLanguage(lang_code, "weking.lang.app.anchor.linking"));
        }
        int link_level = LibSysUtils.toInt(WKCache.get_system_cache("LINK_LEVEL"));
        if (level < link_level) {
            String msg = LibProperties.getLanguage(lang_code, "weking.lang.app.anchor.linking_level");
            return LibSysUtils.getResultJSON(ResultCode.live_islinking, msg.replace("%level%", LibSysUtils.toString(link_level)));
        }
        JSONObject msg = new JSONObject();
        msg.put("im_code", IMCode.apply_link);
        msg.put("account", audience_account);
        msg.put("nickname", audience_nickname);
        msg.put("live_id", live_id);
        WkImClient.sendPrivateMsg(anchor_account, msg.toString());
        WKCache.add_room(live_id, "link_live_account", audience_account);
        return LibSysUtils.getResultJSON(ResultCode.success, LibProperties.getLanguage(lang_code, "weking.lang.app.broadcast.apply.ok"));
    }

    /**
     * 接受连麦
     *
     * @param live_id
     * @param anchor_user_id 主播的user_id
     * @param anchor_account 主播的account
     * @param accept         true：同意,false拒绝
     * @return
     */
    public JSONObject acceptLink(int live_id, int anchor_user_id, String anchor_account, boolean accept) {
        RoomCacheInfo roomCacheInfo = WKCache.get_room(live_id);
        String audience_account = roomCacheInfo.getLink_live_account();
        String live_stream_id = roomCacheInfo.getLive_stream_id();
        String lang_code = WKCache.getUserByAccount(audience_account, "lang_code");
        JSONObject msg = new JSONObject();
        if (accept) {
            msg.put("im_code", IMCode.argee_link);
            msg.put("live_id", live_id);
            msg.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.broadcast.ok"));
        } else {
            WKCache.add_room(live_id, "link_live_account", "");
            msg.put("im_code", IMCode.repulse_link);
            msg.put("live_id", live_id);
            msg.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.broadcast.reject"));
        }
        WkImClient.sendPrivateMsg(audience_account, msg.toString());
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 开始连麦
     *
     * @param live_id             直播记录id
     * @param live_stream_id      流id
     * @param link_live_stream_id 连麦流id
     * @param audience_user_id    连麦人id
     * @param audience_account    连麦人account
     * @return
     */
    public JSONObject startLink(int live_id, String live_stream_id, String link_live_stream_id, int audience_user_id, String audience_account, String audience_nickname) {
        JSONObject msg = new JSONObject();
        msg.put("im_code", IMCode.start_link);
        msg.put("account", audience_account);
        msg.put("nickname", audience_nickname);
        msg.put("link_live_stream_id", link_live_stream_id);
        msg.put("live_id", live_id);
        System.out.println("link_live_stream_id:" + live_stream_id);
        WkImClient.sendRoomMsg(live_stream_id, msg.toString(), 1);
        WKCache.add_room(live_id, "link_live_stream_id", link_live_stream_id);
        // TODO: 2017/3/9 记录数据库
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 结束连麦
     *
     * @param live_id
     * @param live_stream_id
     * @param user_id
     * @param account
     * @param nickname
     * @return
     */
    public JSONObject endLink(int live_id, String live_stream_id, String link_live_stream_id, int user_id, String account, String nickname) {
        JSONObject msg = new JSONObject();
        msg.put("im_code", IMCode.end_link);
        msg.put("live_id", live_id);
        msg.put("account", account);
        msg.put("nickname", nickname);
        msg.put("link_live_stream_id", link_live_stream_id);
        WkImClient.sendRoomMsg(live_stream_id, msg.toString(), 1);
        WKCache.add_room(live_id, "link_live_stream_id", "");
        WKCache.add_room(live_id, "link_live_account", "");
        // TODO: 2017/3/9 记录数据库
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 更新点赞数量
     *
     * @param live_id
     * @param like_count
     * @return
     */
    public JSONObject updateLike(int live_id, int like_count) {
        liveLogInfoMapper.updateLike(like_count, live_id);
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    //获取房间内的人数
    private JSONArray getRoomAccounts(int live_id) {
        JSONArray result = new JSONArray();
        //Map<String, String> real_userinfo = WKCache.get_room_users(live_id);
        int liveGuardId = liveGuardService.getLiveGuardId(live_id);//守护Id
        int anchorId = 0;
        LiveLogInfo liveLogInfo = liveLogInfoMapper.selectByPrimaryKey(live_id);
        if (liveLogInfo != null) {
            anchorId = liveLogInfo.getUserId();//主播Id
        }
        Set<String> list = WKCache.get_room_real_users(live_id);//获取真实用户
        if (list != null) {
            if (list.size() > 0) {
                int i = 1;
                JSONObject dataResult;
                for (String account : list) {
                    List<String> info = WKCache.getUserByAccount(account, "account", "avatar", "user_id", "level", "nickname");
                    dataResult = new JSONObject();
                    Integer send_diamond = 0;
                    dataResult.put("account", info.get(0));
                   /* int userId = LibSysUtils.toInt(info.get(2));
                    if(liveGuardId==userId){
                        dataResult.put("is_guard", 0);//0 是守护
                    }else if(liveGuardMapper.findLiveGuardByUserIdAndAnchor(userId,anchorId)!=null){
                        dataResult.put("is_guard", 1);//1 是前守护
                    }else {
                        dataResult.put("is_guard", 2);//什么都不是
                    }*/
                    Double send_iamond = WKCache.get_send_iamond(live_id, account);
                    if (send_iamond != null) {
                        send_diamond = send_iamond.intValue();
                    }
                    // Long allSend = consumeInfoMapper.getAllByUserId(userId, anchorId);
                    dataResult.put("send_diamond", send_diamond);
                    dataResult.put("level", info.get(3));
                    dataResult.put("nickname", info.get(4));

                    dataResult.put("pic_head_low", WkUtil.combineUrl(info.get(1), UploadTypeEnum.AVATAR, true));
                    if (i <= 3) {
                        if (WKCache.get_room_user_diamond(live_id, info.get(0)) > 0)
                            dataResult.put("rank", i);//前三名用1,2,3，其他的用0,前端会根据1,2,3来增加头像特效
                        else
                            dataResult.put("rank", 0);
                    } else {
                        dataResult.put("rank", 0);//前三名用1,2,3，其他的用0,前端会根据1,2,3来增加头像特效
                    }
                    result.add(dataResult);
                    i++;
                    if (i >= 11)
                        break;
                }
            }
        }
        if (result.size() < 10) {
            Map<String, String> userinfo1 = WKCache.get_room_users(live_id);
            if (userinfo1 != null) {
                if (userinfo1.size() > 0) {
                    int i = result.size();
                    JSONObject dataResult;

                    for (Map.Entry<String, String> entry : userinfo1.entrySet()) {
                        if (!list.contains(entry.getKey())) {
                            dataResult = new JSONObject();
                            dataResult.put("account", entry.getKey());
                            Integer userId = accountInfoMapper.findUserIdByAccount(entry.getKey());
                            Integer send_diamond = 0;
                            /*if(liveGuardId==userId){
                                dataResult.put("is_guard", 0);//0 是守护
                            }else if(liveGuardMapper.findLiveGuardByUserIdAndAnchor(userId,anchorId)!=null){
                                dataResult.put("is_guard", 1);//1 是前守护
                            }else {
                                dataResult.put("is_guard", 2);//什么都不是
                            }*/
                            Double send_iamond = WKCache.get_send_iamond(live_id, entry.getKey());
                            if (send_iamond != null) {
                                send_diamond = send_iamond.intValue();
                            }

                            // Long allSend = consumeInfoMapper.getAllByUserId(userId, anchorId);
                            dataResult.put("send_diamond", send_diamond);
                            AccountInfo accountInfo = accountInfoMapper.selectByAccountId(entry.getKey());
                            if (accountInfo != null) {
                                dataResult.put("level", accountInfo.getLevel());
                                dataResult.put("nickname", accountInfo.getNickname());
                            } else {
                                dataResult.put("level", 1);
                                dataResult.put("nickname", "隱藏昵稱");
                            }

                            dataResult.put("pic_head_low", WkUtil.combineUrl(entry.getValue(), UploadTypeEnum.AVATAR, true));
                            int rank = LibSysUtils.getRandom(0, 3);
                            dataResult.put("rank", 0);
                            result.add(dataResult);
                            i++;
                            if (i >= 10)
                                break;
                        }
                    }
                }
            }
        }
        WKCache.set_room_account_list(live_id, result);
        return result;
    }

    //通知粉丝开播
    public void notifyFans(int user_id, int live_id, String live_stream_id, int live_type, boolean is_push_all,
                           String title, String msg, String channel) {
        try {
            String content;
            if (live_type == C.LiveType.PAY) {
                content = LibSysUtils.getLang("weking.lang.app.vipisliving");
            } else {
                content = LibSysUtils.getLang("weking.lang.app.isliving");
            }
            UserCacheInfo cacheInfo = WKCache.get_user(user_id);
//            JSONObject result = new JSONObject();
//            result.put("store_id", WKCache.get_user(user_id,"store_id"));
//            result.put("account", cacheInfo.getAccount());
//            result.put("pic_head_low", WkUtil.combineUrl(cacheInfo.getAvatar(), true));
//            result.put("nickname", cacheInfo.getNickname());
//            result.put("live_id", live_id);
//            result.put("live_stream_id", live_stream_id);
//            result.put("live_type", live_type);
//            result.put("im_code", IMCode.start_live);
//            Map<String, String> result = new HashMap<>();
            JSONObject result = new JSONObject();
            result.put("store_id", WKCache.get_user(user_id, "store_id"));
            result.put("account", cacheInfo.getAccount());
            result.put("pic_head_low", WkUtil.combineUrl(cacheInfo.getAvatar(), UploadTypeEnum.AVATAR, true));
            result.put("nickname", cacheInfo.getNickname());
            result.put("live_id", LibSysUtils.toString(live_id));
            result.put("live_stream_id", live_stream_id);
            result.put("live_type", LibSysUtils.toString(live_type));
            result.put("im_code", LibSysUtils.toString(IMCode.start_live));


            AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(user_id);
            if (accountInfo!=null&&accountInfo.getCdn_option()==1){
                //台湾Cdn
                result.put("live_rtmp_url", "");
                result.put("live_flv_url", TencentUtil.getFlvTWPlayUrl(live_stream_id));
            }else {
                result.put("live_rtmp_url", TencentUtil.getRtmpPlayUrl(live_stream_id));
                result.put("live_flv_url", TencentUtil.getFlvPlayUrl(live_stream_id));
            }

       /*     result.put("live_rtmp_url", TencentUtil.getRtmpPlayUrl(live_stream_id));
            result.put("live_flv_url", TencentUtil.getFlvPlayUrl(live_stream_id));*/

            result.put("is_horizontal", WKCache.get_room(live_id, "is_horizontal"));//是否横屏
            if (LibSysUtils.isNullOrEmpty(title) && LibSysUtils.isNullOrEmpty(channel)) {
                title = "AppsMe";
            } else if (LibSysUtils.isNullOrEmpty(title) && "yuanMi".equalsIgnoreCase(channel)) {
                title = "嬡秘";
            }

            if (!LibSysUtils.isNullOrEmpty(title)) {
                result.put("title", title);
            }
            if (LibSysUtils.isNullOrEmpty(msg)||"AppsMe".equals(msg)) {
                msg = cacheInfo.getNickname() + content;
            }else {
                msg = msg;
            }
            result.put("message", msg);
//            System.out.println("直播通知：" + result.toString());
            if (is_push_all) {
                msgService.sendMsgToAllUser(result, msg, LibSysUtils.getLang("weking.lang.app.looking"), null);
            } else {
               /* if (cacheInfo.getAnchor_level() == 9) {  //通知所有人
                    msgService.sendMsgToAllUser(result, msg, LibSysUtils.getLang("weking.lang.app.looking"), null);
                } else {*/
                    List<Map<String, Object>> followerList = followInfoMapper.findUserFollowerList(user_id);
                    PushMsg.pushListMsg(result, followerList, msg);
                //}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject getGiftList(int live_id, int liveType, String lang_code, Double api_version) {
        JSONObject obj = WKCache.get_room_gift_list(live_id);
        if (obj != null) {
            return obj;
        }
        List<LiveTag> allTagList = liveTagMapper.getAll(2);

        List<Integer> activityIdList=new ArrayList<>();

        List<Integer> activity_giftId_List=new ArrayList<>();
        if(live_id!=0){
            RoomCacheInfo roomCacheInfo = WKCache.get_room(live_id);
            if (roomCacheInfo!=null){
                String activity_ids = roomCacheInfo.getActivity_ids();
                if (!LibSysUtils.isNullOrEmpty(activity_ids)){
                    String[] str = activity_ids.split(",");
                    for (int i = 0; i < str.length; i++) {
                        activityIdList.add(LibSysUtils.toInt(str[i]));
                    }

                    //获取活动礼物id集合
                    List<ActivityList> activityLists = activityListMapper.selectGiftIdByListId(activityIdList);
                    for (ActivityList activity : activityLists){
                        if (!LibSysUtils.isNullOrEmpty(activity.getGift_id())){
                            String[] gift_str = activity.getGift_id().split(",");
                            for (int i = 0; i < gift_str.length; i++) {
                                activity_giftId_List.add(LibSysUtils.toInt(gift_str[i]));
                            }
                        }
                    }
                    allTagList = liveTagMapper.getAllAndActivity(2);
                }else {
                    allTagList = liveTagMapper.getAll(2);
                }
            }
        }

        List<GiftInfo> list = giftInfoMapper.selectGiftListByLiveType(liveType);

        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray jsonArray = new JSONArray();
        JSONArray tagArray = new JSONArray();
        JSONObject jsonObject;
        JSONObject tagObj;
        String url = WKCache.get_system_cache("weking.config.pic.server");
        for (GiftInfo giftInfo : list) {
            jsonObject = new JSONObject();
            jsonObject.put("gift_id", giftInfo.getId());
            //如果查出的活动礼物里包含该礼物id，则是活动礼物
            if(activity_giftId_List.contains(giftInfo.getId())){
                jsonObject.put("tag_id",30);
            }else {
                jsonObject.put("tag_id", giftInfo.getTag_id());
            }
            switch (lang_code) {
                case "ms":
                    jsonObject.put("name", giftInfo.getMsName());
                    break;
                case "en_US":
                    jsonObject.put("name", giftInfo.getEnName());
                    break;
                default:
                    jsonObject.put("name", giftInfo.getName());
                    break;
            }
            //jsonObject.put("type", giftInfo.getType());
            jsonObject.put("pic_url", combineUrl(url, giftInfo.getPicUrl(), UploadTypeEnum.GIFT, false));
            jsonObject.put("gift_image", combineUrl(url, giftInfo.getGift_image(), UploadTypeEnum.GIFT, false));
            if (api_version <= 4.1 && giftInfo.getType() == 7) {
                jsonObject.put("type", 1);
                jsonObject.put("download_url", "");
            } else {
                jsonObject.put("type", giftInfo.getType());
                jsonObject.put("download_url", combineUrl(url, giftInfo.getDownload_url(), UploadTypeEnum.GIFT, false));
            }

            jsonObject.put("price", giftInfo.getPrice());
            jsonObject.put("tag", giftInfo.getTag());

            jsonArray.add(jsonObject);
        }


        for (LiveTag tagInfo : allTagList) {
            tagObj = new JSONObject();
            tagObj.put("tag_id", tagInfo.getId());
            switch (lang_code) {
                case "ms":
                    tagObj.put("name", tagInfo.getMs_name());
                    break;
                case "en_US":
                    tagObj.put("name", tagInfo.getEn_name());
                    break;
                default:
                    tagObj.put("name", tagInfo.getTw_name());
                    break;
            }

            tagArray.add(tagObj);
        }
        object.put("list", jsonArray);
        object.put("tag_list", tagArray);
        WKCache.set_room_gift_list(live_id, object);
        return object;
    }

    public static String combineUrl(String url, String path, UploadTypeEnum typeEnum, boolean getLowPic) {
        String result = LibSysUtils.toString(path);
        if (!result.contains("http")) {
            if (getLowPic && !LibSysUtils.isNullOrEmpty(result)) {
                if (AliyunOSS.useOSS()) {
                    result = result + "!" + typeEnum.getStylename();
                } else {
                    if (result.contains("big")) {
                        result = result.replace("big", "small");
                    }
                }
            }
            if (!LibSysUtils.isNullOrEmpty(result)) {
                result = url + result;
            }
        }
        return result;
    }

    public JSONObject checkEnterPrivilege(int user_id, int live_id, double api_version, String project_name, String live_pwd, String lang_code) {
        JSONObject result = LibSysUtils.getResultJSON(0);
        RoomCacheInfo roomCacheInfo = WKCache.get_room(live_id);
        if (roomCacheInfo != null) {
            if (roomCacheInfo.getLive_type() == C.LiveType.PAY) {//付费观看的直播要检查是否有权限进入
                UserCacheInfo userCacheInfo = WKCache.get_user(user_id);
                long vvip_freeTime = LibSysUtils.toLong(WKCache.get_system_cache("vvip.freetime")) * 1000;//VVIP免费时间
                if (userCacheInfo.getRole() != 2) {
                    ConsumeInfo consumeInfo = consumeInfoMapper.getLastPayTicket(user_id);//获取获取用户最近一次购买门票的记录
                    if (consumeInfo == null) {//没有购买门票
                        if (vvip_freeTime > 0 && api_version > 2.0) {//有免费观看时间则插入一条免费观看记录
                            buyTicket(user_id, live_id, true, lang_code,0);
                            result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.live_residue").replace("%residue%", LibDateUtils.diffDatetime(vvip_freeTime)));
                            result.put("remain_time", vvip_freeTime / 1000);
                        } else {
                            result.put("code", ResultCode.live_pay);
                            result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang." + project_name + "app.live_pay").replace("%diamond%", LibSysUtils.toString(roomCacheInfo.getLive_ticket())));
                        }
                    } else {
                        //检查观看时间是否过期
                        long diff = LibDateUtils.getDateTimeTick(consumeInfo.getSendTime(), LibDateUtils.getLibDateTime());//购买门票到当前的时间（毫秒）
                        long pay_live_time = LibSysUtils.toLong(WKCache.get_system_cache("weking.pay_live_duration")) * 1000;//转换成毫秒比较
                        if (consumeInfo.getSendDiamond() == 0) {//如果上一次购买是免费观看记录，则用免费时长来判断
                            pay_live_time = vvip_freeTime;//转换成毫秒比较
                        }
                        if (diff > pay_live_time) {
                            //超时，提示购买
                            Calendar now = Calendar.getInstance();
                            int day = now.get(Calendar.DAY_OF_MONTH);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd");
                            int last_day = LibSysUtils.toInt(sdf.format(LibDateUtils.libDateTime2DateTime(consumeInfo.getSendTime())));
                            if (last_day != day && api_version > 2.0) {
                                buyTicket(user_id, live_id, true, lang_code,0);
                                result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.live_residue").replace("%residue%", LibDateUtils.diffDatetime(vvip_freeTime)));
                                result.put("remain_time", vvip_freeTime / 1000);
                            } else {
                                result.put("code", ResultCode.live_pay);
                                result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang." + project_name + "app.live_pay").replace("%diamond%", LibSysUtils.toString(roomCacheInfo.getLive_ticket())));
                            }
                        } else {
                            long residue = pay_live_time - diff;
                            result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.live_residue").replace("%residue%", LibDateUtils.diffDatetime(residue)));
                            result.put("remain_time", residue / 1000);
                        }
                    }
                } else {
                    long residue = 7200000;
                    result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.live_residue").replace("%residue%", LibDateUtils.diffDatetime(residue)));
                    result.put("remain_time", residue / 1000);
                }
            }
            if (roomCacheInfo.getLive_type() == 11) {//密码播
                if (!live_pwd.equals(roomCacheInfo.getLive_pwd())) {//密码不正确
                    result.put("code", ResultCode.account_pwd_error);
                    result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.account.pwd.error"));
                }
            }
        } else {
            result.put("code", ResultCode.live_end);
            result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.live_end"));
        }
        return result;
    }

    public JSONObject checkEnterPrivilegeAndVip(int user_id, int live_id, double api_version, String project_name, String live_pwd, String lang_code,int type) {
        JSONObject result = LibSysUtils.getResultJSON(0);
        LiveLogInfo liveLogInfo = liveLogInfoMapper.selectByPrimaryKey(live_id);
        if (liveLogInfo != null) {
          if (user_id!=liveLogInfo.getUserId()) {
                if (liveLogInfo.getLive_type() == C.LiveType.PAY) {//付费观看的直播要检查是否有权限进入
                    ConsumeInfo consumeInfo = consumeInfoMapper.getPayTicket(user_id, live_id, type);//获取购买门票的记录
                    if (consumeInfo == null) {//没有购买门票
                        //如果是直播  需要查该用户有没有买过主播的门票
                        if (type == 0) {
                            //查询上条主播直播记录
                            LiveLogInfo liveInfo = liveLogInfoMapper.findLiveLogInfoByEndAndUserId(liveLogInfo.getUserId());
                            if (liveInfo != null) {
                                ConsumeInfo consume = consumeInfoMapper.getPayTicket(user_id, liveInfo.getId(), type);//获取购买门票的记录
                                if (consume != null) {
                                    long diff = LibDateUtils.getDateTimeTick(liveInfo.getLiveEnd(), LibDateUtils.getLibDateTime());//购买门票到当前的时间（毫秒）
                                    long vip_diff = LibSysUtils.toLong(WKCache.get_system_cache("is.live.vip.diff")) * 60000;//有效期
                                    if (diff > vip_diff) {
                                        result.put("code", ResultCode.live_pay);
                                        result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang." + project_name + "app.live_pay").replace("%diamond%", LibSysUtils.toString(liveLogInfo.getLive_extend())));
                                    } else {
                                        //更改记录
                                        consumeInfoMapper.updateLiveIdById(consume.getId(), live_id);

                                    }

                                } else {
                                    result.put("code", ResultCode.live_pay);
                                    result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang." + project_name + "app.live_pay").replace("%diamond%", LibSysUtils.toString(liveLogInfo.getLive_extend())));
                                }
                            } else {
                                result.put("code", ResultCode.live_pay);
                                result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang." + project_name + "app.live_pay").replace("%diamond%", LibSysUtils.toString(liveLogInfo.getLive_extend())));
                            }

                        } else {
                            result.put("code", ResultCode.live_pay);
                            result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang." + project_name + "app.live_pay").replace("%diamond%", LibSysUtils.toString(liveLogInfo.getLive_extend())));
                        }
                    }
                }
                if (liveLogInfo.getLive_type() == 11) {//密码播
                    RoomCacheInfo roomCacheInfo = WKCache.get_room(live_id);
                    if (roomCacheInfo != null) {
                        if (!live_pwd.equals(roomCacheInfo.getLive_pwd())) {//密码不正确
                            result.put("code", ResultCode.account_pwd_error);
                            result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.account.pwd.error"));
                        }
                    } else {
                        result.put("code", ResultCode.live_end);
                        result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.live_end"));
                    }
                }
            }
            }


        return result;
    }




    /**
     * 获取直播密码
     *
     * @param user_id
     * @param live_id
     * @param lang_code
     * @return
     */
    public JSONObject getLivePwd(int user_id, int live_id, String lang_code) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
//        ConsumeInfo consumeInfo = consumeInfoMapper.getPayTicket(user_id, live_id);//获取获取用户购买门票的记录
//        if (consumeInfo != null) {
//            RoomCacheInfo roomInfo = WKCache.get_room(live_id);
//            result.put("live_pwd", roomInfo.getLive_pwd());
//        } else {
//            result.put("pwd_ticket", LibSysUtils.toInt(WKCache.get_system_cache("live.pwd_ticket")));
//            result.put("code", ResultCode.live_pay);
//        }
        RoomCacheInfo roomInfo = WKCache.get_room(live_id);
        result.put("live_pwd", roomInfo.getLive_pwd());
        return result;
    }

    public JSONObject sharePwdLive(int user_id, int live_id, String share_type, String lang_code) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        taskService.recordTaskLog(user_id, 2, 0, 0, live_id, 7, share_type);
        RoomCacheInfo roomInfo = WKCache.get_room(live_id);
        result.put("live_pwd", roomInfo.getLive_pwd());
        return result;
    }

    /**
     * 购买付费观看的门票
     *
     * @param user_id
     * @param live_id   直播记录id
     * @param is_free   是否为免费观看
     * @param lang_code
     * @return
     */
    @Transactional
    public JSONObject buyTicket(int user_id, int live_id, boolean is_free, String lang_code,int type) {
        JSONObject result = LibSysUtils.getResultJSON(0);
        LiveLogInfo liveLogInfo = liveLogInfoMapper.selectByPrimaryKey(live_id);
        if (liveLogInfo != null) {
            int ticket = LibSysUtils.toInt(liveLogInfo.getLive_extend());

            PocketInfo pinfo = pocketInfoMapper.selectByUserid(user_id);
            if (pinfo != null && (pinfo.getTotalDiamond()+pinfo.getFreeDiamond()) < ticket) {
                return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
            }
            //如果免费的emo够用，直接扣免费emo
            if (pinfo.getFreeDiamond() >= ticket){
                pocketInfoMapper.deductFreeDiamondByUserId(ticket, user_id); //扣钱
            }else {
                //先扣掉免费所有emo
                if (pinfo.getFreeDiamond()>0) {
                    pocketInfoMapper.deductFreeDiamondByUserId(pinfo.getFreeDiamond(), user_id); //扣钱
                }
                //再扣除剩余的部分
                pocketInfoMapper.deductDiamondByUserid(ticket-pinfo.getFreeDiamond(), user_id); //扣钱
            }
            pocketInfoMapper.increaseTicketByUserid(ticket, 0);//购买门票的钱加到id为0的主播身上
            levelService.putExp(user_id, ticket, 2, 0);//增加经验值
            //插入消费记录表
            long sendtime = LibDateUtils.getLibDateTime();
            ConsumeInfo consumeLogInfo = new ConsumeInfo();
            consumeLogInfo.setSendId(user_id);
            consumeLogInfo.setReceiveId(0);//购买门票的钱加到id为0的主播身上
            consumeLogInfo.setSendDiamond(ticket);
            consumeLogInfo.setSendTime(sendtime);
            consumeLogInfo.setGiftId(1);//giftid为1为购买门票
            consumeLogInfo.setLiveRecordId(live_id);
            String ratio = userService.getUserInfoByUserId(user_id, "ratio");
            consumeLogInfo.setRatio(new BigDecimal(ratio).setScale(8));
            consumeLogInfo.setBuy_type(type);
            consumeInfoMapper.insertSelective(consumeLogInfo);
            result.put("my_diamonds", (pinfo.getTotalDiamond()+pinfo.getFreeDiamond()) - ticket);
        }
        return result;
    }


    /**
     * 开通直播权限
     *
     * @param user_id
     * @param lang_code
     * @return
     */
    @Transactional
    public JSONObject openLive(int user_id, String lang_code) {
        JSONObject result = LibSysUtils.getResultJSON(0);
        int ticket = LibSysUtils.toInt(WKCache.get_system_cache("weking.public_live_diamond"));//开通直播权限所需要的钻石
        PocketInfo pinfo = pocketInfoMapper.selectByUserid(user_id);
        if (pinfo != null &&( pinfo.getTotalDiamond()+pinfo.getFreeDiamond()) < ticket) {
            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
        }
        //如果免费的emo够用，直接扣免费emo
        if (pinfo.getFreeDiamond() >= ticket){
            pocketInfoMapper.deductFreeDiamondByUserId(ticket, user_id); //扣钱
        }else {
            //先扣掉免费所有emo
            if (pinfo.getFreeDiamond()>0) {
                pocketInfoMapper.deductFreeDiamondByUserId(pinfo.getFreeDiamond(), user_id); //扣钱
            }
            //再扣除剩余的部分
            pocketInfoMapper.deductDiamondByUserid(ticket-pinfo.getFreeDiamond(), user_id); //扣钱
        }
        pocketInfoMapper.increaseTicketByUserid(ticket, 0);//购买门票的钱加到id为0的主播身上
        levelService.putExp(user_id, ticket, 2, 0);//增加经验值
        //插入消费记录表
        long sendtime = LibDateUtils.getLibDateTime();
        ConsumeInfo consumeLogInfo = new ConsumeInfo();
        consumeLogInfo.setSendId(user_id);
        consumeLogInfo.setReceiveId(0);//购买门票的钱加到id为0的主播身上
        consumeLogInfo.setSendDiamond(ticket);
        consumeLogInfo.setSendTime(sendtime);
        consumeLogInfo.setGiftId(2);//giftid为2为开通直播权限
        consumeLogInfo.setLiveRecordId(0);
        String ratio = userService.getUserInfoByUserId(user_id, "ratio");
        consumeLogInfo.setRatio(new BigDecimal(ratio).setScale(8));
        consumeInfoMapper.insertSelective(consumeLogInfo);
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setId(user_id);
        accountInfo.setAnchor_level((short) 1);
        accountMapper.updateByPrimaryKeySelective(accountInfo);
        result.put("my_diamonds", (pinfo.getTotalDiamond()+pinfo.getFreeDiamond()) - ticket);
        return result;
    }

    public JSONObject getGameLiveInfo(int user_id, String account, String nick_name,String live_stream_id) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);


        AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(user_id);
        if (accountInfo!=null&&accountInfo.getCdn_option()==1){
            //台湾Cdn
            result.put("push_url", TencentUtil.getPushTWUrl(live_stream_id));
            result.put("live_rtmp_url", "");
            result.put("live_flv_url", TencentUtil.getFlvTWPlayUrl(live_stream_id));
        }else {
            result.put("live_rtmp_url", TencentUtil.getRtmpPlayUrl(live_stream_id));
            result.put("live_flv_url", TencentUtil.getFlvPlayUrl(live_stream_id));
            //台湾Cdn
            result.put("push_url", TencentUtil.getPushTWUrl(live_stream_id));
        }


        result.put("live_stream_id", live_stream_id);
        return result;
    }

    private String getZegoToken() {
        String token = WKCache.get_zego_token();
        if (!LibSysUtils.toBoolean(WKCache.get_system_cache("weking.config.debug"))) {
            if (LibSysUtils.isNullOrEmpty(token)) {
                String url = WKCache.get_system_cache("zego.url") + "token";
                String param = String.format("appid=%s&secret=%s", WKCache.get_system_cache("weking.config.zego.app_id"), WKCache.get_system_cache("weking.config.zego.secret"));
                String str = WkUtil.sendGet(url, param);
                JSONObject result = JSONObject.fromObject(str);
                if (result.optInt("code") == 0) {
                    JSONObject data = result.optJSONObject("data");
                    token = data.optString("access_token");
                    WKCache.add_zego_token(token);
                }
            }
        }
        return token;
    }

    private JSONObject create_live(String zego_token, String account, String nick_name, String stream_alias) {
        String url = String.format("%screate-live?access_token=%s", WKCache.get_system_cache("zego.url"), zego_token);
        String param = "title=gamelive&id_name=%s&nick_name=%s&term_type=windows&net_type=无线&stream_id=%s";
        param = String.format(param, account, nick_name, stream_alias);
        String str = WkUtil.sendPost(url, param);
        JSONObject result = JSONObject.fromObject(str);
        loger.info(String.format("zego create_live:account=%s", account));
        return result;
    }

    private JSONObject close_live(String stream_alias) {
        JSONObject result = new JSONObject();
        try {
            String access_token = getZegoToken();
            String stream_id = WKCache.get_live_stream_id_cache(stream_alias);
            loger.info(String.format("zego close_live:stream_alias=%s", stream_alias));
            if (!LibSysUtils.isNullOrEmpty(stream_id)) {
                String url = String.format("%sclose-live?access_token=%s", WKCache.get_system_cache("zego.url"), access_token);
                String param = String.format("stream_id=%s", stream_id);
                String str = WkUtil.sendPost(url, param);
                result = JSONObject.fromObject(str);
                loger.info(String.format("zego close_live end:stream_alias=%s,%s", stream_alias, str));
            }
            WKCache.set_live_stream_id_cache(stream_alias, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject getGameCategory(String lang_code) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        result.put("game_category", internal_getGameCategory(lang_code));
        return result;
    }

    private JSONArray internal_getGameCategory(String lang_code) {
        JSONArray array = new JSONArray();
        JSONObject object;
        List<GameCategory> lists = gameCategoryMapper.getAll();
        for (GameCategory list : lists) {
            object = new JSONObject();
            object.put("category_id", list.getCategory_id());
            object.put("category_name", list.getCategory_name());
            object.put("is_vertical", LibSysUtils.toBoolean(list.getIs_vertical()));
            object.put("category_url", WkUtil.combineUrl(list.getCategory_url(), UploadTypeEnum.OTHER, false));
            array.add(object);
        }

        return array;
    }

    public void liveRoomGiftPush() {
        System.out.println("------直播间礼物推送");
        if (SystemConstant.gift_box_push) {
            int living_count = liveLogInfoMapper.getIsLivingCount();
            JSONObject IMObject = PushMsg.getPushJSONObject(0, "zh_CN", "push.get.live.gift.box.title", "push.get.live.gift.box");
            if (living_count == 0) {
                msgService.sendMsgToApp(IMObject.optString("message"), IMObject.optString("title"), 0);
//                PushMsg.pushSingleMsg("c24d214e2750a1f4710b20be1f7f344b", config.optString("msg"));
            } else {
                List<LiveLogInfo> list = liveLogInfoMapper.getHotLiving("", 0, 1, 0);
                LiveLogInfo liveLogInfo = list.get(0);
//                notifyUserGetGiftBox(liveLogInfo.getUserId(), liveLogInfo.getId(), liveLogInfo.getLive_stream_id(), liveLogInfo.getLive_type());
                notifyFans(liveLogInfo.getUserId(), liveLogInfo.getId(), liveLogInfo.getLive_stream_id(), liveLogInfo.getLive_type(),
                        true, IMObject.optString("title"), IMObject.optString("message"), "");
            }
        }

    }

    public boolean checkLiveIsEnd(int liveId) {
        boolean flg = true;
        LiveLogInfo liveLogInfo = liveLogInfoMapper.selectByPrimaryKey(liveId);
        if (liveLogInfo != null) {
            Long liveEnd = liveLogInfo.getLiveEnd();
            if (liveEnd == 0) {
                flg = false;
            }
        }
        return flg;
    }

    private void notifyUserGetGiftBox(int user_id, int live_id, String live_stream_id, Integer live_type) {
        UserCacheInfo cacheInfo = WKCache.get_user(user_id);
        JSONObject result = new JSONObject();
        result.put("store_id", WKCache.get_user(user_id, "store_id"));
        result.put("account", cacheInfo.getAccount());
        result.put("pic_head_low", WkUtil.combineUrl(cacheInfo.getAvatar(), UploadTypeEnum.AVATAR, true));
        result.put("nickname", cacheInfo.getNickname());
        result.put("live_id", LibSysUtils.toString(live_id));
        result.put("live_stream_id", live_stream_id);
        result.put("live_type", LibSysUtils.toString(live_type));
        result.put("im_code", LibSysUtils.toString(IMCode.start_live));
        result.put("is_horizontal", WKCache.get_room(live_id, "is_horizontal"));    //是否横屏
        result.put("title", "AppsMe");

        Thread notifyUsers = new Thread(new Runnable() {
            public void run() {
                notifyUserByLanguage(result, "ms");
            }
        }, "notifyUsers");
        notifyUsers.start();

//        GeTuiUtil.pushMsgToApp(result.toString(), msg, LibSysUtils.getLang("weking.lang.app.looking"));
//        FCM.pushSystemTopicsMsg(result);

    }


    private void notifyUserByLanguage(JSONObject result, String lang_code) {
        List<Map<String, Object>> userList = new ArrayList<>();
//        for (int i = 0; (i == 0 && userList.size() == 0) || userList.size() != 0; i++) {
//            int index = i * 5;
        userList = accountMapper.selectUserListByLanguage(lang_code, 0, 5);
        String msg = LibProperties.getLanguage(lang_code, "push.get.live.gift.box");
        result.put("message", msg);
        System.out.println("  size: " + userList.size() + " msg:" + msg);
        if (userList.size() > 0) {
            PushMsg.pushSingleMsg("fYpguVe7Rzc:APA91bEucBj4LMSCH_wDEwRPCnHR2iBjIc4yVzX9iVu8dtBQj74NnN_PWIVoLdBQwi-TNNAE9MdXhHS2oo29qSQstrU9eJNz_U0De8dkNkUgYgC0M4HqsHJEFusBPLrDP9wYaEYtIYyE", result);
//                PushMsg.pushListMsg(result, userList, msg);
        }

//        }
    }


    //获取直播收益
    public JSONObject getAnchorLiveIncome(int user_id, String time) {
        List<Map<String, Object>> list;
        List<Map<String, Object>> guardList;
        JSONObject object;
        JSONArray array = new JSONArray();
        Long timeStart = Long.parseLong(time + "01000000");
        Long timeEnd = Long.parseLong(time + "31235959");
        int monthIncome = 0;
        //总收益
        int total_money = pocketInfoMapper.getAnchorMoneyByUserId(user_id);
        //直播收益
        list = liveLogInfoMapper.getMonthIncome(user_id, timeStart, timeEnd);
        int size = list.size();
        if (size > 0) {
            JSONObject temp;
            for (int i = 0; i < size; i++) {
                temp = new JSONObject();
                Map<String, Object> map = list.get(i);
                temp.put("date", LibSysUtils.toString(map.get("live_start")));
                temp.put("diff", LibSysUtils.toString(map.get("diff")));
                temp.put("price", LibSysUtils.toString(map.get("price")));
                temp.put("type", 0);
                monthIncome += LibSysUtils.toInt(map.get("price"));
                array.add(temp);
            }
        }
        //守护收益
        guardList = consumeInfoMapper.getGuardIncome(user_id, timeStart, timeEnd);

        int guardListSize = guardList.size();
        if (guardListSize > 0) {
            JSONObject obj;
            for (int i = 0; i < guardListSize; i++) {
                obj = new JSONObject();
                Map<String, Object> guardMap = guardList.get(i);
                obj.put("date", LibSysUtils.toString(guardMap.get("send_time")));
                obj.put("diff", "");
                obj.put("price", LibSysUtils.toString(guardMap.get("receive_ticket")));
                obj.put("type", 1);
                monthIncome += LibSysUtils.toInt(guardMap.get("receive_ticket"));
                array.add(obj);
            }
        }
        object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("total_money", total_money);
        object.put("month_income", monthIncome);
        object.put("list", array);
        return object;

    }

    /**
     * @param userId  查询者的uerid
     * @param account 被查询者的account
     * @return
     */
    public JSONObject anchorLiveAdvanceNotice(int userId, String account) {
        UserCacheInfo userCacheInfo = WKCache.get_user(userId);
        AccountInfo accountInfo = accountMapper.selectByAccountId(account);
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        if (accountInfo != null) {
            List<LiveAdvanceNotice> list = liveAdvanceNoticeMapper.findAnchorNoticeByUserId(accountInfo.getId(), LibDateUtils.getLibDateTime());
            for (LiveAdvanceNotice notice : list) {
                JSONObject temp = new JSONObject();
                temp.put("id", notice.getId());
                temp.put("live_time", notice.getLiveTime());
                array.add(temp);
            }
            object.put("advanceNotice_list", array);
        } else {
            String langCode = "zh_TW";
            if (userCacheInfo != null) {
                langCode = userCacheInfo.getLang_code();
            }
            object = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(langCode, "weking.lang.account.exist_error"));
        }
        return object;
    }


    /**
     * 新增 删除 直播直播预告
     *
     * @param userId userId
     * @param type   0 新增  1是修改  2是删除
     * @return
     */
    public JSONObject updateLiveAdvanceNotice(int userId, int type, int id, long live_time, String lang_code) {
        // UserCacheInfo userCacheInfo = WKCache.get_user(userId);
        JSONObject object;
        LiveAdvanceNotice advanceNotice = liveAdvanceNoticeMapper.selectByUserIdAndTime(userId, live_time);
        if (advanceNotice == null) {
            long old_time = Long.parseLong(DateUtils.BeforeNowByDay(-11) + "000000");
            switch (type) {
                case 0:
                    if (live_time > old_time) {
                        object = LibSysUtils.getResultJSON(ResultCode.live_advance_error, LibProperties.getLanguage(lang_code, "weking.lang.app.live.advance.error"));
                        return object;
                    } else if (live_time <= LibDateUtils.getLibDateTime()) {
                        object = LibSysUtils.getResultJSON(ResultCode.live_advance_error, LibProperties.getLanguage(lang_code, "weking.lang.app.live.overdue.error"));
                        return object;
                    } else {
                        LiveAdvanceNotice notice = new LiveAdvanceNotice();
                        notice.setLiveTime(live_time);
                        notice.setUserId(userId);
                        liveAdvanceNoticeMapper.insertSelective(notice);
                    }
                    break;
                case 1:
                    if (live_time > old_time) {
                        object = LibSysUtils.getResultJSON(ResultCode.live_advance_error, LibProperties.getLanguage(lang_code, "weking.lang.app.live.advance.error"));
                        return object;
                    } else if (live_time <= LibDateUtils.getLibDateTime()) {
                        object = LibSysUtils.getResultJSON(ResultCode.live_advance_error, LibProperties.getLanguage(lang_code, "weking.lang.app.live.overdue.error"));
                        return object;
                    } else {
                        liveAdvanceNoticeMapper.updateById(id, live_time);
                    }
                    break;
                case 2:
                    liveAdvanceNoticeMapper.deleteByPrimaryKey(id);
                    break;
                default:
                    break;
            }
            String account = userService.getUserInfoByUserId(userId, "account");
            object = anchorLiveAdvanceNotice(userId, account);
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.live_time_error, LibProperties.getLanguage(lang_code, "weking.lang.app.live.time.error"));
        }

        return object;
    }

    //获取开播预告
    public JSONObject getAdvanceNoticeList(int userId, Long start_time, Long end_time) {
        List<LiveAdvanceNotice> list = liveAdvanceNoticeMapper.findAnchorNoticeListByTime(start_time, end_time);
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        for (LiveAdvanceNotice notice : list) {
            JSONObject tmp = new JSONObject();
            tmp.put("live_time", LibSysUtils.toString(notice.getLiveTime()));
            tmp.put("account", LibSysUtils.toString(notice.getAccount()));
            tmp.put("nickname", LibSysUtils.toString(notice.getNickname()));
            tmp.put("pichead_url", WkUtil.combineUrl(notice.getPichead_url(), UploadTypeEnum.ADV, false));
            array.add(tmp);
        }
        result.put("advanceNotice_list", array);
        return result;
    }


    //开始竞猜
    public String startGuessing(int userId, int liveId, int diff, int price, String title, String option_one, String option_two, int right_option) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        //先查询有没有未结束的竞猜
        int guessingId = GameCache.get_guessing_id(userId);
        if (guessingId > 0) {
            GameCache.del_guessing_info(userId); // 清除上局竞猜数据
            liveGuessingMapper.updateByIdAndEndTime(guessingId, LibDateUtils.getLibDateTime());
        }
        int guessing_id = recordLiveGuessing(liveId, userId, diff, price, title, option_one, option_two); // 记录竞猜
        GameCache.set_guessing_time(userId, LibDateUtils.getLibDateTime()); //设置竞猜开始时间
        GameCache.set_guessing_id(userId, guessing_id);
        GameCache.set_guessing_state(userId, GameUtil.BET_STATE); //设置状态
        GameCache.set_guessing_diff(userId, diff * 60);
        GameCache.set_guessing_num(userId, 0, GameCache.guessing_one_num);
        GameCache.set_guessing_num(userId, 0, GameCache.guessing_two_num);
        GameCache.set_guessing_data(userId, GameCache.guessing_option_one, option_one);
        GameCache.set_guessing_data(userId, GameCache.guessing_option_two, option_two);
        GameCache.set_guessing_data(userId, GameCache.guessing_title, title);
        GameCache.set_guessing_data(userId, GameCache.guessing_price, LibSysUtils.toString(price));
        GameCache.set_guessing_data(userId, GameCache.guessing_right_option, LibSysUtils.toString(right_option));

        JSONObject pushJson = new JSONObject();

        pushJson.put(C.ImField.im_code, IMCode.guessing_bet);
        pushJson.put("diff", diff * 60);//秒
        pushJson.put(C.ImField.live_id, liveId);
        pushJson.put("title", title);
        pushJson.put("option_one", option_one);
        pushJson.put("option_two", option_two);
        pushJson.put("price", price);
        pushJson.put("one_num", 0);
        pushJson.put("two_num", 0);
        pushJson.put("right_option", right_option);
        pushJson.put("guessing_state", GameUtil.BET_STATE);
        pushJson.put("guessing_id", guessing_id);
        String roomId = WKCache.get_room(liveId, C.WKCacheRoomField.live_stream_id);
        WkImClient.sendRoomMsg(roomId, pushJson.toString(), 1);
        pushJson.remove(C.ImField.im_code);
        object.putAll(pushJson);
        return object.toString();
    }


    /**
     * 加入竞猜
     *
     * @param live_id live_id
     * @param account account
     * @return String
     */
    public String joinGuessing(int live_id, String account, double api_version) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        String user_id = WKCache.get_room(live_id, "user_id");
        if (user_id != null && user_id != "") {
            int userId = LibSysUtils.toInt(user_id);
            // if (GameCache.get_guessing_state(userId) == GameUtil.BET_STATE) {
            int time = GameCache.get_guessing_diff(userId);//竞猜有效时长
            int price = LibSysUtils.toInt(GameCache.get_guessing_data(userId, GameCache.guessing_price));
            result.put("title", GameCache.get_guessing_data(userId, GameCache.guessing_title));
            result.put("option_one", GameCache.get_guessing_data(userId, GameCache.guessing_option_one));
            result.put("option_two", GameCache.get_guessing_data(userId, GameCache.guessing_option_two));
            result.put("price", price);
            result.put("one_num", GameCache.get_guessing_num(userId, GameCache.guessing_one_num) * price);
            result.put("two_num", GameCache.get_guessing_num(userId, GameCache.guessing_two_num) * price);
            result.put("guessing_state", GameCache.get_guessing_state(userId));
            result.put("guessing_id", GameCache.get_guessing_id(userId));
            result.put("live_id", live_id);
            result.put("right_option", LibSysUtils.toInt(GameCache.get_guessing_data(userId, GameCache.guessing_right_option)));
            long diff = LibDateUtils.getDateTimeTick(GameCache.get_guessing_time(userId), LibDateUtils.getLibDateTime()) / 1000;
            result.put("diff", time - diff < 0 ? 0 : time - diff);//倒计时时间;
            /*if ( (time - diff < 0 ? 0 : time - diff)==0){
                 result.put("guessing_state",3);
                result.put(C.ImField.im_code, IMCode.guessing_end);
                String roomId = WKCache.get_room(live_id, C.WKCacheRoomField.live_stream_id);
                //推送竞猜后 数据
                WkImClient.sendRoomMsg(roomId, result.toString(), 1);
                liveGuessingMapper.updateByIdAndEndTime(GameCache.get_guessing_id(userId), LibDateUtils.getLibDateTime());
                GameCache.del_guessing_info(userId); // 清除竞猜数据
            }*/
            //}
        }

        return result.toString();
    }

    //记录竞猜日志
    private int recordLiveGuessing(int liveId, int userId, int diff, int price, String title, String option_one, String option_two) {
        LiveGuessing record = new LiveGuessing();
        record.setLiveId(liveId);
        record.setUserId(userId);
        record.setAddTime(LibDateUtils.getLibDateTime());
        record.setDiff(diff);
        record.setPrice(price);
        record.setOptionOne(option_one);
        record.setOptionTwo(option_two);
        record.setTitle(title);
        record.setEndTime(0L);
        int re = liveGuessingMapper.insert(record);
        if (re > 0) {
            re = record.getId();
        }
        return re;
    }

    //用户竞猜
    public JSONObject guess(int userId, String account, int liveId, int guessing_id, int option, String lang_code, double api_version) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        String user_id = WKCache.get_room(liveId, "user_id");
        if (user_id != null && user_id != "") {
            int anchorId = LibSysUtils.toInt(user_id);
            int state = GameCache.get_guessing_state(anchorId);
            if (state == GameUtil.BET_STATE) {
                String guessing_price = GameCache.get_guessing_data(anchorId, GameCache.guessing_price);
                if (guessing_price != null && guessing_price != "") {
                    int costPrice = LibSysUtils.toInt(guessing_price);
                    JSONObject object = pocketService.consume(userId, anchorId, costPrice, 7, guessing_id, lang_code);
                    //扣款成功
                    if (object.getInt("code") == ResultCode.success) {
                        levelService.putExp(userId, costPrice, 1, anchorId);//增加经验值
                        //修改或者新增 贡献榜 先去查询是否已经有这条记录
                        int m = contributionInfoMapper.updateContirbution(userId, anchorId, costPrice);
                        if (m == 0) { //没有数据，insert
                            long sendTicket = costPrice;
                            ContributionInfo record = new ContributionInfo();
                            record.setSendId(userId);
                            record.setAnchorId(anchorId);
                            record.setSendTotalTicket(sendTicket);
                            contributionInfoMapper.insert(record);
                        }


                        recordGuessLog(userId, guessing_id, option);//插入用户竞猜日志
                        switch (option) {
                            case 0:
                                GameCache.add_guessing_num(anchorId, GameCache.guessing_one_num);
                                break;
                            case 1:
                                GameCache.add_guessing_num(anchorId, GameCache.guessing_two_num);
                                break;
                        }
                        String guessingData = joinGuessing(liveId, account, api_version);
                        result = JSONObject.fromObject(guessingData);
                        result.put(C.ImField.im_code, IMCode.guessing_bet_send);
                        String roomId = WKCache.get_room(liveId, C.WKCacheRoomField.live_stream_id);

                        //推送竞猜后 数据
                        WkImClient.sendRoomMsg(roomId, result.toString(), 1);
                    } else {
                        return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
                    }
                }
            }
        }
        result.remove(C.ImField.im_code);
        int leftDiamond = pocketInfoMapper.getSenderLeftDiamondbyid(userId);//获取剩余钻石
        result.put("my_diamonds", leftDiamond);
        return result;
    }


    /**
     * 结束竞猜
     *
     * @param live_id live_id
     * @param
     * @return String
     */
    public String endGuessing(int live_id, Double api_version) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        String user_id = WKCache.get_room(live_id, "user_id");
        if (user_id != null && user_id != "") {
            int userId = LibSysUtils.toInt(user_id);
            int guessing_id = GameCache.get_guessing_id(userId);
            GameCache.set_guessing_state(userId, GameUtil.GAME_END);
            String guessingData = joinGuessing(live_id, "", api_version);
            result = JSONObject.fromObject(guessingData);
            result.put(C.ImField.im_code, IMCode.guessing_end);
            String roomId = WKCache.get_room(live_id, C.WKCacheRoomField.live_stream_id);
            //推送竞猜后 数据
            WkImClient.sendRoomMsg(roomId, result.toString(), 1);
            if (guessing_id > 0) {
                GameCache.del_guessing_info(userId); // 清除竞猜数据
                liveGuessingMapper.updateByIdAndEndTime(guessing_id, LibDateUtils.getLibDateTime());
            }
            result.remove(C.ImField.im_code);
        }

        return result.toString();
    }


    //记录用户竞猜日志
    private void recordGuessLog(int userId, int guessing_id, int option) {
        try {
            GuessingLog record = new GuessingLog();
            record.setUserId(userId);
            record.setAddTime(LibDateUtils.getLibDateTime());
            record.setGuessingId(guessing_id);
            record.setOption(option);
            guessingLogMapper.insertSelective(record);
        } catch (Exception e) {
            loger.error("e:" + e);
        }


    }

    public Integer getRandomGiftId(int gift_ticket) {
        int gift_id;
        List<Integer> integers = giftInfoMapper.selectGiftIdByPrice(gift_ticket);
        Random random = new Random();
        int index = random.nextInt(integers.size());
        gift_id = integers.get(index);

        return gift_id;
    }

    /**
     * 根据赠送礼物与后台配置中奖几率计算是否中奖
     *
     * @param times   中奖倍数
     * @param balance   中奖几率
     */
    private static boolean getResultInner( Double balance,int times) {
        //  int 范围 四个字节，-2147483648~2147483647

        Boolean flg=false;
        int m = 10000;
        if (balance>0) {
            double n = ( m/times)*balance;
            // 产生区间[0,m) 内的随机数
            int random = (int) (Math.random() * m);
            // 若随机数落入[0,n)，则中奖
            if (random<n) {
                flg = true;
            }

        }
            return  flg;

    }


    //获取节目单
    public JSONObject getLiveShowList(int userId,String lang_code) {
        List<Map<String, Object>>  liveShowList = liveShowMapper.selectAllLiveShow(LibDateUtils.getLibDateTime());
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        int size = liveShowList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                JSONObject tmp = new JSONObject();
                Map<String, Object> map = liveShowList.get(i);
                tmp.put("url", WkUtil.combineUrl(LibSysUtils.toString(map.get("url")), UploadTypeEnum.SHOWLIVE, false));
                tmp.put("account", LibSysUtils.toString(map.get("account")));
                tmp.put("nickname", LibSysUtils.toString(map.get("nickname")));
                tmp.put("title", LibSysUtils.toString(map.get("title")));
                tmp.put("live_time", LibSysUtils.toLong(map.get("live_time")));
                tmp.put("follow_state", followService.getFollowStatus(userId,LibSysUtils.toInt(map.get("anchor_id"))));
                array.add(tmp);
            }
        }

        result.put("liveShow_list", array);
        return result;
    }


    public Boolean getActivity(int giftId){
        Boolean flg=false;
        List<Integer> activity_giftId_List=new ArrayList<>();
        long time = LibDateUtils.getLibDateTime();
        List<ActivityList> activityLists = activityListMapper.selectUnclosedActivity(time);
        for (ActivityList activity : activityLists){
            if (!LibSysUtils.isNullOrEmpty(activity.getGift_id())){
                String[] gift_str = activity.getGift_id().split(",");
                for (int i = 0; i < gift_str.length; i++) {
                    activity_giftId_List.add(LibSysUtils.toInt(gift_str[i]));
                }
            }
        }
        if (activity_giftId_List.contains(giftId)){
            flg=true;
        }
        return flg;
    }

}
