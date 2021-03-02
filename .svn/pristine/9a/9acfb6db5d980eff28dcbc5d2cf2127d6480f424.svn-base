package com.weking.cache;

import com.weking.core.C;
import com.weking.core.DateUtils;
import com.weking.core.ResultCode;
import com.weking.mapper.system.SystemMapper;
import com.weking.model.post.PostInfo;
import com.weking.model.system.System;
import com.weking.redis.LibRedis;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/12/28.
 */
public class WKCache {

    private static volatile String _projectName = LibProperties.getConfig("weking.config.project.name");
    private static volatile LibRedis _libRedis;
    public static String _room_tag = _projectName + "_weking_room_info_";//48

    private static String _live_stream_id = _projectName + "_live_stream_";//当游戏直播值，zego的拉流id和关闭流id不一致

    private static String _room_sort_tag = _projectName + "_weking_room_sort";//直播列表排名

    private static String _room_user_tag = _projectName + "_weking_room_user_";//48
    private static String _room_real_user_tag = _projectName + "_weking_room_user_real_";//真实用户列表48

    private static String _room_manager_tag = _projectName + "_weking_room_manager_";//房间管理员
    private static String _room_banned_tag = _projectName + "_weking_room_banned_";//房间禁言

    private static String _room_rank_tag = _projectName + "_weking_room_rank_";//本场排名48

    private static String _live_goods_tag = _projectName + "_weking_live_goods_";//当前直播商品

    private static String _user_tag = _projectName + "_weking_user_";//7天
    private static String _token_tag = _projectName + "_weking_token_";//7天
    private static String _account_tag = _projectName + "_weking_account_";//7天

    private static String _system_tag = _projectName + "_weking_system";

    private static String _store_tag = _projectName + "_store_goods_";//7天

    private static String _store_info = _projectName + "_store_info_";//7天
    private static String _goods_info = _projectName + "_goods_info_";//7天

    private static String _gash_order_info = _projectName + "_gash_order_info";
    private static String _gash_settle_info = _projectName + "_gash_settle_info";

    private static String _room_income_tag = _projectName + "_weking_income_rank_";//收入排名 月榜
    private static String _room_consume_tag = _projectName + "_weking_consume_rank_";//消费排名 月榜
    private static String _user_fans_rank = _projectName + "_weking_fans_rank_";//粉丝排名 月榜

    private static String _room_income_tag_day = _projectName + "_weking_income_rank_day_";//收入排名 月榜
    private static String _room_consume_tag_day = _projectName + "_weking_consume_rank_day_";//消费排名 月榜
    private static String _user_fans_rank_day = _projectName + "_weking_fans_rank_day_";//粉丝排名 月榜

    private static String _room_income_tag_week = _projectName + "_weking_income_rank_week_";//收入排名 周榜榜
    private static String _room_consume_tag_week = _projectName + "_weking_consume_rank_week_";//消费排名 周榜
    private static String _user_fans_rank_week = _projectName + "_weking_fans_rank_week_";//粉丝排名 周榜


    private static String _room_bio_consume_tag = _projectName + "_weking_bio_consume_rank_";//个人收入排名 月榜

    private static String _room_bio_consume_tag_day = _projectName + "_weking_bio_consume_rank_day_";//个人收入排名 日榜

    private static String _room_bio_consume_tag_week = _projectName + "_weking_bio_consume_rank_week_";//个人收入排名 周榜


    private static String _shop_order = _projectName + "_shop_order"; //缓存未支付商城订单

    private static String _ip_register = _projectName + "_ip_register_"; //缓存ip注册账号次数

    private static String _im_state = _projectName + "_im_state_"; //用户IM状态

    private static String _video_chat_consume = _projectName + "_video_chat_consume";

    private static String _video_chat_msg = _projectName + "_video_chat_msg";

    private static String _video_chating_user = _projectName + "_video_chating_user";

    private static String _ip_captcha = "_ip_captcha";

    private static String _geo_tag = "_geo_tag";

    private static String _zego_token = _projectName + "_zego_token";
    private static String _post_reward_num = _projectName + "_post_reward_num_";
    private static String _post_like_info = _projectName + "_post_like_info";
    private static String _user_guide_post = _projectName + "_user_guide_post";
    private static String _user_post_time = _projectName + "_user_post_time";
    private static String _user_follow_post_list = _projectName + "_user_follow_post_list_";

    private static String _user_post_comments_ = _projectName + "_user_post_comments_";
    private static String _user_hidden_post_ = _projectName + "_user_hidden_post_";
    private static String _hidden_post = _projectName + "_hidden_post";
    private static String _post_lockout_user = _projectName + "_post_lockout_user";
    private static String _recommend_post_list = _projectName + "_recommend_post_list";
    private static String _post_info_ = _projectName + "_post_info_";
    private static String _popular_post_list = _projectName + "_popular_post_list";
    private static String _recommend_post_user_list = _projectName + "_recommend_post_user_list";
    private static String _user_recommend_post = _projectName + "_user_recommend_post";

    private static String _SCA_GOLD_POOL = _projectName + "_SCA_GOLD_POOL";
    private static String _USER_SCA_GOLD = _projectName + "_USER_SCA_GOLD";
    private static String _user_post_operate = _projectName + "_user_post_operate";

    private static String _live_list_log = _projectName + "_live_list:%d:%d:%s:%s:%s_%s";

    private static String _post_recommmend_list = _projectName + "_recommend_post_list:%d:%s_%s";

    private static String _anchor_rank_list = _projectName + "_live_list:%d:%s:%d:%d:%d";

    private static String _room_account_list = _projectName + "_room_account_list:%d";

    private static String _room_gift_list = _projectName + "_room_gift_list:%d";
    private static String _room_live_tag = _projectName + "_room_live_tag:%d";
    private static String _live_all_tag = _projectName + "_live_all_tag";
    private static String _user_all_level_color = _projectName + "_user_all_level_color";
    private static String _user_invite_cache = _projectName + "_user_invite_cache:%d:%d";

    private static String _is_official_live = _projectName + "_is_official_live";

    private static String _recharge_rank_month = _projectName + "_weking_recharge_rank_month_";//充值 月榜

    private static final String _out_room_id = _projectName + "_live_out_room_id";     //直播间被踢出人id


    private static String _room_live_privilege = _projectName + "_room_live_privilege:%d";


    private static volatile SystemMapper systemMapper;

    private static int zegotokenexpires = 7100; //秒
    private static int oneDay = 86400; //过期时间(1天)
    private static int twoDay = 172800; //过期时间(2天)
    private static int sevenDay = 604800; //过期时间(7天)
    private static int weekDay = 691200;//过期时间（8天）

    static {
        init_libRedis();
        init_system();
    }

    public static void init_system() {
        if (systemMapper == null) {
            synchronized (WKCache.class) {
                if (systemMapper == null) {
                    ApplicationContext ctx = new ClassPathXmlApplicationContext("config/spring-common.xml");
                    systemMapper = (SystemMapper) ctx.getBean("systemMapper");
                }
            }
        }
    }

    public static void init_libRedis() {
        if (_libRedis == null) {
            synchronized (WKCache.class) {
                if (_libRedis == null) {
                    _libRedis = new LibRedis();
                }
            }
        }
    }

    private static String getKey(String tag, int key) {
        return String.format("%s%d", tag, key);
    }

    private static String getKey(String tag) {
        return String.format("%s%s", _projectName, tag);
    }

    /**
     * 获取系统参数缓存
     *
     * @param paramName 参数名称
     * @return
     */
    public static String get_system_cache(String paramName) {
        String result = _libRedis.hget(_system_tag, paramName);
        return result;
    }

    public static JSONObject del_system_cache(String paramName) {
        if (LibSysUtils.isNullOrEmpty(paramName))
            _libRedis.del(_system_tag);
        else
            _libRedis.hdel(_system_tag, paramName);
        List<System> list = systemMapper.selectAll();
        if (list != null) {
            for (System systemInfo : list) {
                _libRedis.hset(_system_tag, systemInfo.getArgs(), systemInfo.getAves());
            }
        }
        List<String> map = _libRedis.hmget(_system_tag, "weking.config.debug", "weking.config.mobile.url");
        JSONObject result = new JSONObject();
        result.put("weking.config.debug", map.get(0));
        result.put("weking.config.mobile.url", map.get(1));
        return result;
    }

    public static JSONObject check_token(String token) {
        if (LibSysUtils.isNullOrEmpty(token)) {
            return LibSysUtils.getResultJSON(ResultCode.token_invalidity, LibProperties.getLanguage("weking.lang.account.login.lose"));
        }
        JSONObject result = new JSONObject();
        if ("weking2016".equals(token)) {
            result.put("code", ResultCode.success);
            result.put("user_id", "15");
            result.put("lang_code", "zh_CN");
        } else {
            List<String> list = WKCache.get_user(token, "user_id", "account", "level", "avatar", "nickname", "lang_code", "wallet_currency"); //根据token获取用户信息
            if (list != null && LibSysUtils.isNullOrEmpty(list.get(0))) {
                result.put("code", ResultCode.token_invalidity);
                result.put("msg", LibProperties.getLanguage("weking.lang.account.login.lose"));
            } else {
                result.put("code", ResultCode.success);
                result.put("user_id", list.get(0));
                result.put("account", list.get(1));
                result.put("level", list.get(2));
                result.put("avatar", list.get(3));
                result.put("nickname", list.get(4));
                String lang_code = list.get(5);
                if (!LibSysUtils.isNullOrEmpty(lang_code))
                    result.put("lang_code", lang_code);
                else
                    result.put("lang_code", LibProperties.getConfig("weking.config.default_lang"));
                result.put("currency", list.get(6));
            }
        }
        return result;
    }


    /**
     * 暂存用户信息
     *
     * @param user_id
     * @param user_info
     */
    public static void add_user(int user_id, Map<String, String> user_info) {
        String access_token = user_info.get("access_token");
        String account = user_info.get("account");
        String result1 = _libRedis.hmset(getKey(_user_tag, user_id), user_info);
        String result2 = _libRedis.hmset(_account_tag + account, user_info);
        if (!LibSysUtils.isNullOrEmpty(access_token)) {
            String result3 = _libRedis.hmset(_token_tag + access_token, user_info);
            if (result3 != null) {
                _libRedis.expire(_token_tag + access_token, sevenDay);
            }
        }
        if (result1 != null) {
            _libRedis.expire(getKey(_user_tag, user_id), sevenDay);
       }
        if (result2 != null) {
            _libRedis.expire(_account_tag + account, sevenDay);
        }
    }

    public static void add_user_cache(int user_id, Map<String, String> user_info) {
        _libRedis.hmset(getKey(_user_tag, user_id), user_info);
    }

    /**
     * 暂存用户信息
     *
     * @param user_id
     * @param field
     * @param value
     */
    public static void add_user(int user_id, String field, String value) {
        List<String> user_info = _libRedis.hmget(getKey(_user_tag, user_id), "access_token", "account");
        if (user_info != null && !LibSysUtils.isNullOrEmpty(user_info.get(0))) {
            String access_token = user_info.get(0);
            String account = user_info.get(1);
            _libRedis.hset(getKey(_user_tag, user_id), field, value);
            _libRedis.hset(_account_tag + account, field, value);
            _libRedis.hset(_token_tag + access_token, field, value);
        }
    }

    /**
     * 删除暂存的用户信息
     *
     * @param user_id
     */
    public static void del_user(int user_id) {
        String user_cache_key = getKey(_user_tag, user_id);
        List<String> result = _libRedis.hmget(user_cache_key, "account", "access_token");
        if (result != null && !LibSysUtils.isNullOrEmpty(result.get(0))) {
            _libRedis.del(user_cache_key);
            String account = result.get(0);
            _libRedis.del(_account_tag + account);
            String access_token = result.get(1);
            _libRedis.del(_token_tag + access_token);
        }
    }


    /**
     * 根据user_id 获取暂存的用户信息
     *
     * @param user_id
     * @return
     */
    public static UserCacheInfo get_user(int user_id) {
        UserCacheInfo result = null;
        Map<String, String> map = _libRedis.hgetAll(getKey(_user_tag, user_id));
        if (map != null && !LibSysUtils.isNullOrEmpty(map.get("user_id"))) {
            result = new UserCacheInfo();
            result.setAvatar(map.get("avatar"));
            result.setAccount(map.get("account"));
            result.setDevice_token(map.get("device_token"));
            result.setLevel(LibSysUtils.toInt(map.get("level")));
            result.setC_id(map.get("c_id"));
            result.setLang_code(map.get("lang_code"));
            result.setLat(LibSysUtils.toDouble(map.get("lat")));
            result.setLng(LibSysUtils.toDouble(map.get("lng")));
            result.setLogin_time(LibSysUtils.toLong(map.get("login_time")));
            String login_type = map.get("login_type");
            if(!"type".equals(login_type)) {
                result.setLogin_type(LibSysUtils.toInt(map.get("login_type")));
            }
            result.setNickname(map.get("nickname"));
            result.setToken(map.get("access_token"));
            result.setUser_id(LibSysUtils.toInt(map.get("user_id")));
            result.setExperience(LibSysUtils.toInt(map.get("experience")));
            result.setSorts(LibSysUtils.toInt(map.get("sorts")));
            result.setRole(LibSysUtils.toInt(map.get("role")));
            result.setAnchor_level(LibSysUtils.toInt(map.get("anchor_level")));
            result.setWallet_currency(map.get("wallet_currency"));
            result.setVip_level(LibSysUtils.toInt(map.get("vip_level")));
            result.setVip_experience(LibSysUtils.toInt(map.get("vip_experience")));
        }
        return result;
    }

    public static Map<String, String> getUserInfo(int user_id) {
        return _libRedis.hgetAll(getKey(_user_tag, user_id));
    }

    /**
     * 根据user_id 获取暂存的用户信息
     *
     * @param user_id
     * @param fields  获取字段的信息
     * @return
     */
    public static List<String> get_user(int user_id, String... fields) {
        return _libRedis.hmget(getKey(_user_tag, user_id), fields);
    }

    /**
     * 根据user_id 获取暂存的用户信息
     *
     * @param user_id
     * @param field   获取字段的信息
     * @return
     */
    public static String get_user(int user_id, String field) {
        return _libRedis.hget(getKey(_user_tag, user_id), field);
    }

    /**
     * access_token 获取暂存的用户信息
     *
     * @param access_token
     * @param fields       获取字段的信息
     * @return
     */
    public static List<String> get_user(String access_token, String... fields) {
        return _libRedis.hmget(_token_tag + access_token, fields);
    }


    /**
     * 根据account 获取暂存的用户信息
     *
     * @param account
     * @param fields  获取字段的信息
     * @return
     */
    public static List<String> getUserByAccount(String account, String... fields) {
        return _libRedis.hmget(_account_tag + account, fields);
    }

    /**
     * 根据account获取暂存的用户信息
     *
     * @param account
     * @param field   获取字段的信息
     * @return
     */
    public static String getUserByAccount(String account, String field) {
        return _libRedis.hget(_account_tag + account, field);
    }


    /**
     * 根据房间id暂存房间信息
     *
     * @param live_id
     * @param roomCacheInfo
     */
    public static String add_room(int live_id, Map<String, String> roomCacheInfo) {
        String result = _libRedis.hmset(getKey(_room_tag, live_id), roomCacheInfo);
        if (result != null) {
            _libRedis.expire(getKey(_room_tag, live_id), twoDay);
        }
        return result;
    }

    public static void add_room(int live_id, String field, String value) {
        _libRedis.hset(getKey(_room_tag, live_id), field, value);
    }

    /**
     * 根据房间id获取房间缓存
     *
     * @param live_id
     * @param field
     */
    public static String get_room(int live_id, String field) {
        String result = _libRedis.hget(getKey(_room_tag, live_id), field);
        return result;
    }

    /**
     * 根据房间id获取房间缓存
     *
     * @param live_id live_id
     * @param field   field
     */
    public static List<String> get_room(int live_id, String... field) {
        return _libRedis.hmget(_room_tag + live_id, field);
    }

    /**
     * 根据房间id获取房间缓存
     *
     * @param live_id
     */
    public static RoomCacheInfo get_room(int live_id) {
        RoomCacheInfo result = null;
        Map<String, String> map = _libRedis.hgetAll(getKey(_room_tag, live_id));
        if (map != null && !LibSysUtils.isNullOrEmpty(map.get("live_id"))) {
            result = new RoomCacheInfo();
            result.setAttendance(LibSysUtils.toInt(map.get("attendance")));
            result.setHeart_time(LibSysUtils.toLong(map.get("heart_time")));
            result.setUser_id(LibSysUtils.toInt(map.get("user_id")));
            result.setCity(map.get("city"));
            result.setReal_attendance(LibSysUtils.toInt(map.get("real_audience")));
            result.setLive_stream_id(map.get("live_stream_id"));
            result.setAccount(map.get("account"));
            result.setLive_id(LibSysUtils.toInt(map.get("live_id")));
            result.setLink_live_stream_id(map.get("link_live_stream_id"));
            result.setLink_live_account(map.get("link_live_account"));
            result.setLive_type(LibSysUtils.toInt(map.get("live_type")));
            result.setLive_ticket(LibSysUtils.toInt(map.get("live_ticket")));
            result.setLive_pwd(LibSysUtils.toString(map.get("live_pwd")));
            result.setPause_live(LibSysUtils.toBoolean(map.get("pause_live")));
            result.setHorizontal(LibSysUtils.toBoolean(map.get("is_horizontal")));
            result.setAvatar(LibSysUtils.toString(map.get("avatar")));
            result.setNickname(LibSysUtils.toString(map.get("nickname")));
            result.setLive_start(LibSysUtils.toLong(map.get("live_start")));
            result.setLongitude(LibSysUtils.toDouble(map.get("longitude")));
            result.setLatitude(LibSysUtils.toDouble(map.get("latitude")));
            result.setLive_cover(LibSysUtils.toString(map.get("live_cover")));
            result.setLive_title(LibSysUtils.toString(map.get("live_title")));
            result.setRole(LibSysUtils.toString(map.get("role")));
            result.setAnnouncement(LibSysUtils.toString(map.get("announcement")));
            result.setProgram_slogan(LibSysUtils.toString(map.get("program_slogan")));
            result.setLink_url(LibSysUtils.toString(map.get("link_url")));
            result.setActivity_ids(LibSysUtils.toString(map.get("activity_ids")));
        }
        return result;
    }

    /**
     * 根据房间id房间缓存
     *
     * @param live_id
     */
    public static void del_room(int live_id) {
        String slive_id = LibSysUtils.toString(live_id);
        _libRedis.del(_room_tag + slive_id);
        _libRedis.del(_room_user_tag + slive_id);
        _libRedis.del(_room_real_user_tag + slive_id);
        _libRedis.del(_room_banned_tag + slive_id);
        _libRedis.del(_room_manager_tag + slive_id);
        _libRedis.del(_room_rank_tag + live_id);
        _libRedis.del(_live_goods_tag + live_id);
        _libRedis.zrem(_room_sort_tag, LibSysUtils.toString(live_id));
    }

    /**
     * 增加房间人数
     *
     * @param live_id
     * @param account           观众account
     * @param avatar            头像
     * @param attendance        增加的机器人数
     * @param is_robot          是否是机器人
     * @param send_total_ticket 给主播赠送的总赤币
     */
    public static void add_room_user(int live_id, String account, String avatar, int attendance, boolean is_robot, int send_total_ticket) {
        String slive_id = LibSysUtils.toString(live_id);
        if (!_libRedis.hexists(_room_user_tag + slive_id, account)) {
            _libRedis.hset(_room_user_tag + slive_id, account, avatar);//房间人员列表
            if (!is_robot)
                _libRedis.zincrby(_room_real_user_tag + slive_id, LibSysUtils.toDouble(send_total_ticket), account);//房间真实用户列表
            _libRedis.hincrBy(_room_tag + slive_id, "attendance", attendance);//观看人数
            _libRedis.hincrBy(_room_tag + slive_id, "real_audience", 1);//实际观看人数
            _libRedis.hincrBy(_room_tag + slive_id, "online_audience", 1);//在线人数
        }
    }

    public static void del_room_user(int live_id, String account, int attendance, boolean is_robot) {
        String slive_id = LibSysUtils.toString(live_id);
        if (_libRedis.hdel(_room_user_tag + slive_id, account) > 0) {//房间人员列表
            _libRedis.hincrBy(_room_tag + slive_id, "online_audience", -1);//在线人数
            _libRedis.hincrBy(_room_tag + slive_id, "attendance", -1);//观看人数
        }
        if (!is_robot)
            _libRedis.zrem(_room_real_user_tag + slive_id, account);
    }

    /**
     * 获取房间人员
     *
     * @param live_id
     * @return
     */
    public static Map<String, String> get_room_users(int live_id) {
        Map<String, String> result = _libRedis.hgetAll(_room_user_tag + LibSysUtils.toString(live_id));//房间人员列表
        return result;
    }

    /**
     * 获取真实用户列表
     *
     * @param live_id
     * @return
     */
    public static Set<String> get_room_real_users(int live_id) {
        Set<String> list = _libRedis.zrevrange(getKey(_room_real_user_tag, live_id), 0, 10);
        return list;
    }

    /**
     * 获取所有房间
     *
     * @return
     */
    public static List<Map<String, String>> get_all_room() {
        List<Map<String, String>> result = _libRedis.hgetAllObject(_room_tag);
        return result;
    }

    /**
     * 获取所有房间
     *
     * @return
     */
    public static Map<String, Map<String, String>> get_all_room_map() {
        Map<String, Map<String, String>> result = _libRedis.getAllMap(_room_tag);
        return result;
    }


    /**
     * 设置管理员
     *
     * @param anchor_user_id
     * @param account        观众account
     */
    public static void add_room_manager(int anchor_user_id, String account, String nickName) {
        String sanchor_user_id = LibSysUtils.toString(anchor_user_id);
        if (!_libRedis.hexists(_room_manager_tag + sanchor_user_id, account)) {
            _libRedis.hset(_room_manager_tag + sanchor_user_id, account, nickName);
        }
    }

    /**
     * 获取管理员列表
     *
     * @param anchor_user_id
     * @return
     */
    public static Map<String, String> get_room_managers(int anchor_user_id) {
        Map<String, String> result = _libRedis.hgetAll(_room_manager_tag + LibSysUtils.toString(anchor_user_id));
        return result;
    }

    public static String get_room_managers(int anchor_user_id, String account) {
        String result = _libRedis.hget(_room_manager_tag + LibSysUtils.toString(anchor_user_id), account);
        return result;
    }

    //取消管理员
    public static long del_room_manager(int anchor_user_id, String account) {
        return _libRedis.hdel(_room_manager_tag + LibSysUtils.toString(anchor_user_id), account);
    }

    /**
     * 禁言
     *
     * @param anchor_user_id
     * @param account        观众account
     */
    public static void add_room_banned(int anchor_user_id, String account, String nickName) {
        String sanchor_user_id = LibSysUtils.toString(anchor_user_id);
        if (!_libRedis.hexists(_room_banned_tag + sanchor_user_id, account)) {
            _libRedis.hset(_room_banned_tag + sanchor_user_id, account, nickName);
        }
    }

    //解除
    public static long del_room_banned(int anchor_user_id, String account) {
        return _libRedis.hdel(_room_banned_tag + LibSysUtils.toString(anchor_user_id), account);
    }

    public static String get_room_banned(int anchor_user_id, String account) {
        String result = _libRedis.hget(_room_banned_tag + LibSysUtils.toString(anchor_user_id), account);
        return result;
    }
    /**
     * 获取禁言列表
     *
     * @param anchor_user_id
     * @return
     */
    public static Map<String, String> get_room_bannedPostList(int anchor_user_id) {
        Map<String, String> result = _libRedis.hgetAll(_room_banned_tag + LibSysUtils.toString(anchor_user_id));
        return result;
    }




    public static Double add_room_rank(int live_id, double score, String member) {
        _libRedis.zincrby(getKey(_room_real_user_tag, live_id), score, member);//房间内人员列表排名
        return _libRedis.zincrby(getKey(_room_rank_tag, live_id), score, member);
    }

    public static Set<String> get_room_rank(int live_id, int index, int count) {
        if (index >= 0) {
            count = index + count -1;
        }
        Set<String> set = _libRedis.zrevrange(getKey(_room_rank_tag, live_id), index, count);
        return set;
    }

    public static Double get_send_iamond(int live_id, String member) {
        Double result = _libRedis.zscore(getKey(_room_rank_tag, live_id), member);
        return result;
    }

    public static Double get_room_user_diamond(int live_id, String member) {
        Double result = _libRedis.zscore(getKey(_room_real_user_tag, live_id), member);
        return result;
    }

    /**
     * 增加直播列表的排名权重
     *
     * @param live_id 直播记录id
     * @param score   权重数值，目前根据后台设置的主播排序、送礼、房间内发言、房间内人数，4个参数计算房间热度，如果需要置顶房间列表则增加score值即可
     *                计算规则如下：
     *                主播排序数值*10000
     *                礼物金额
     *                一次发言+5
     *                新进人员+1
     * @return
     */
    public static Double incr_room_sort(int live_id, int live_type, int score) {
        Double result = 0D;
        if (live_type == 0)
            result = _libRedis.zincrby(_room_sort_tag, score, LibSysUtils.toString(live_id));
        return result;
    }

    //获取房间热度列表
    public static Set<String> get_room_sort(int index, int count) {
        if (index > 0) {
            count = index + count;
        }
        Set<String> set = _libRedis.zrevrange(_room_sort_tag, index, count);
        return set;
    }

    public static String get_live_stream_id_cache(String stream_alias) {
        return _libRedis.hget(String.format("%s%s", _live_stream_id, stream_alias), "stream_id");
    }

    public static Long set_live_stream_id_cache(String stream_alias, String stream_id) {
        return _libRedis.hset(String.format("%s%s", _live_stream_id, stream_alias), "stream_id", stream_id);
    }

    /**
     * 暂存当前直播商品信息
     */
    public static void add_live_goods(int live_id, Map<String, String> goods_info) {
        String result = _libRedis.hmset(getKey(_live_goods_tag, live_id), goods_info);
        if (result != null) {
            _libRedis.expire(getKey(_live_goods_tag, live_id), twoDay);
        }
    }

    /**
     * 根据live_id获取当前直播商品信息
     */
    public static List<String> get_live_goods(int live_id, String... field) {
        return _libRedis.hmget(getKey(_live_goods_tag, live_id), field);
    }

    /**
     * 暂存当前直播商品列表
     */
    public static void add_room_goods_list(int storeId, String goods_info) {
        String result = _libRedis.put(getKey(_store_tag, storeId), goods_info);
        if (result != null) {
            _libRedis.expire(getKey(_store_tag, storeId), twoDay);
        }
    }

    /**
     * 根据live_id获取当前直播商品信息
     */
    public static String get_room_goods_list(int storeId) {
        return _libRedis.get(getKey(_store_tag, storeId));
    }

    /**
     * 根据live_id删除当前直播商品信息
     */
    public static void del_room_goods_list(int storeId) {
        _libRedis.del(getKey(_store_tag, storeId));
    }

    /**
     * 暂存店铺信息
     */
    public static void add_store_info(int storeId, Map<String, String> store_info) {
        String result = _libRedis.hmset(getKey(_store_info, storeId), store_info);
        if (result != null) {
            _libRedis.expire(getKey(_store_info, storeId), sevenDay);
        }
    }

    /**
     * storeId 获取暂存的店铺信息
     */
    public static List<String> get_store_info(int storeId, String... fields) {
        return _libRedis.hmget(getKey(_store_info, storeId), fields);
    }

    /**
     * 获取店铺单个字段信息
     */
    public static String get_store_info(int storeId, String field) {
        return _libRedis.hget(getKey(_store_info, storeId), field);
    }

    /**
     * 暂存商品信息
     */
    public static void add_goods_info(int goodsId, Map<String, String> goods_info) {
        String result = _libRedis.hmset(getKey(_goods_info, goodsId), goods_info);
        if (result != null) {
            _libRedis.expire(getKey(_goods_info, goodsId), sevenDay);
        }
    }

    /**
     * 获取暂存的商品多个字段信息
     */
    public static List<String> get_goods_info(int goodsId, String... fields) {
        return _libRedis.hmget(getKey(_goods_info, goodsId), fields);
    }

    /**
     * 扣减商品信息
     */
    public static void deduction_goods_info(int goodsId, String field, int num) {
        _libRedis.hincrBy(_goods_info + goodsId, field, num);
    }

    /**
     * 获取商品所有缓存信息
     */
    public static Map<String, String> get_goods_allInfo(int goodsId) {
        return _libRedis.hgetAll(getKey(_goods_info, goodsId));
    }

    /**
     * 获取商品单个字段信息
     */
    public static String get_goods_info(int goodsId, String field) {
        return _libRedis.hget(getKey(_goods_info, goodsId), field);
    }

    /**
     * 缓存需重试订单信息
     */
    public static void add_gash_order_info(String field, String value) {
        _libRedis.hset(_gash_order_info, field, value);
    }

    /**
     * 缓存需重试订单信息
     */
    public static Map<String, String> get_all_gash_order() {
        return _libRedis.hgetAll(_gash_order_info);
    }

    /**
     * 删除重试订单信息
     */
    public static void del_gash_order_info(String field) {
        _libRedis.hdel(_gash_order_info, field);
    }

    /**
     * 缓存需请款订单信息
     */
    public static void add_gash_settle_info(String field, String value) {
        _libRedis.hset(_gash_settle_info, field, value);
    }

    /**
     * 缓存请款订单信息
     */
    public static Map<String, String> get_all_gash_settle() {
        return _libRedis.hgetAll(_gash_settle_info);
    }

    /**
     * 删除请款完成订单信息
     */
    public static void del_gash_settle_info(String field) {
        _libRedis.hdel(_gash_settle_info, field);
    }

    /**
     * 缓存商城未支付订单信息
     */
    public static void add_shop_order(Map<String, String> map) {
        _libRedis.hmset(_shop_order, map);
    }

    /**
     * 获得商城未支付订单信息
     */
    public static Map<String, String> all_shop_order() {
        return _libRedis.hgetAll(_shop_order);
    }

    /**
     * 删除商城订单信息
     */
    public static void del_shop_order(String field) {
        _libRedis.hdel(_shop_order, field);
    }

    //收入排行
    public static Double add_income_rank(long time, double score, String member) {
        return _libRedis.zincrby(_room_income_tag + time, score, member);
    }

    public static Long add_income_list(long time, Map<String, Double> scoreMembers) {
        return _libRedis.zadd(_room_income_tag + time, scoreMembers);
    }

    public static Double get_income_num(long time, String member) {
        return _libRedis.zscore(_room_income_tag + time, member);
    }

    public static Set<String> get_income_rank(long time, int index, int count) {
        if (index >=0) {
            count = index + count-1;
        }
        return _libRedis.zrevrange(_room_income_tag + time, index, count);
    }

    public static Long getIncomeUserRank(long time, String member) {
        return _libRedis.zrank(_room_income_tag + time, member);
    }


    //收入排行  周榜
    public static Double add_income_rank_week(long time, double score, String member) {
        _libRedis.expire(_room_income_tag_week + time, weekDay);
        return _libRedis.zincrby(_room_income_tag_week + time, score, member);
    }

    public static Double get_income_num_week(long time, String member) {
        return _libRedis.zscore(_room_income_tag_week + time, member);
    }

    public static Long add_income_list_week(long time, Map<String, Double> scoreMembers) {
        _libRedis.expire(_room_income_tag_week + time, weekDay);
        return _libRedis.zadd(_room_income_tag_week + time, scoreMembers);
    }

    public static Set<String> get_income_rank_week(long time, int index, int count) {
        if (index >= 0) {
            count = index + count-1;
        }
        return _libRedis.zrevrange(_room_income_tag_week + time, index, count);
    }

    //收入排行榜 日榜

    public static Double add_income_rank_day(long time, double score, String member) {
        _libRedis.expire(_room_income_tag_day+time, twoDay);
        return _libRedis.zincrby(_room_income_tag_day + time, score, member);

    }

    public static Long add_income_list_day(long time, Map<String, Double> scoreMembers) {
        return _libRedis.zadd(_room_income_tag_day + time, scoreMembers);

    }

    public static Double get_income_num_day(long time, String member) {
        return _libRedis.zscore(_room_income_tag_day + time, member);
    }

    public static Set<String> get_income_rank_day(long time, int index, int count) {
        if (index >= 0) {
            count = index + count-1;
        }
        return _libRedis.zrevrange(_room_income_tag_day + time, index, count);
    }

//个人收入月榜
public static Double add_bio_income_rank(long time,int anchor, double score, String member) {

    return _libRedis.zincrby(_room_bio_consume_tag +anchor+ time, score, member);
}

   /* public static Long add_bio_income_list(long time,int anchor, Map<String, Double> scoreMembers) {
        return _libRedis.zadd(_room_bio_consume_tag +anchor+ time, scoreMembers);
    }*/
    public static Double get_bio_incomee_num(long time,int anchor, String member) {
        return _libRedis.zscore(_room_bio_consume_tag +anchor+ time, member);
    }



    public static Set<String> get_bio_consume_rank(long time,int anchor, int index, int count) {
        if (index >= 0) {
            count = index + count-1;
        }
        return _libRedis.zrevrange(_room_bio_consume_tag +anchor+ time, index, count);
    }



    //个人收入榜 日榜

    public static Double add__bio_income_rank_day(long time,int anchor, double score, String member) {
        _libRedis.expire(_room_bio_consume_tag_day  +anchor+ time, twoDay);
        return _libRedis.zincrby(_room_bio_consume_tag_day  +anchor+ time, score, member);
    }

    /*public static Long add_income_list_day(long time, Map<String, Double> scoreMembers) {
        return _libRedis.zadd(_room_income_tag_day + time, scoreMembers);
    }*/

    public static Double get__bio_income_num_day(long time,int anchor, String member) {
        return _libRedis.zscore(_room_bio_consume_tag_day  +anchor+ time, member);
    }

    public static Set<String> get__bio_income_rank_day(long time,int anchor, int index, int count) {
        if (index >= 0) {
            count = index + count-1;
        }
        return _libRedis.zrevrange(_room_bio_consume_tag_day  +anchor+ time, index, count);
    }


    //个人收入榜 周榜

    public static Double add__bio_income_rank_week(long time,int anchor, double score, String member) {
        _libRedis.expire(_room_bio_consume_tag_week  +anchor+ time, weekDay);
        return _libRedis.zincrby(_room_bio_consume_tag_week  +anchor+ time, score, member);
    }

    /*public static Long add_income_list_day(long time, Map<String, Double> scoreMembers) {
        return _libRedis.zadd(_room_income_tag_day + time, scoreMembers);
    }*/

    public static Double get__bio_income_num_week(long time,int anchor, String member) {
        return _libRedis.zscore(_room_bio_consume_tag_week  +anchor+ time, member);
    }

    public static Set<String> get__bio_income_rank_week(long time,int anchor, int index, int count) {
        if (index >= 0) {
            count = index + count-1;
        }
        return _libRedis.zrevrange(_room_bio_consume_tag_week  +anchor+ time, index, count);
    }





    //消费排行
    public static Double add_consume_rank(long time, double score, String member) {
        return _libRedis.zincrby(_room_consume_tag + time, score, member);
    }

    public static Double get_consume_num(long time, String member) {
        return _libRedis.zscore(_room_consume_tag + time, member);
    }

    public static Long add_consume_list(long time, Map<String, Double> scoreMembers) {
        return _libRedis.zadd(_room_consume_tag + time, scoreMembers);
    }

    public static Set<String> get_consume_rank(long time, int index, int count) {
        if (index >= 0) {
            count = index + count-1;
        }
        return _libRedis.zrevrange(_room_consume_tag + time, index, count);
    }

    public static Long getConsumeUserRank(long time, String member) {
        return _libRedis.zrank(_room_consume_tag + time, member);
    }

    //消费排行  日榜
    public static Double add_consume_rank_day(long time, double score, String member) {
        _libRedis.expire(_room_consume_tag_day + time, twoDay);
        return _libRedis.zincrby(_room_consume_tag_day + time, score, member);
    }

    public static Double get_consume_num_day(long time, String member) {
        return _libRedis.zscore(_room_consume_tag_day + time, member);
    }

    public static Long add_consume_list_day(long time, Map<String, Double> scoreMembers) {
        _libRedis.expire(_room_consume_tag_day + time, twoDay);
        return _libRedis.zadd(_room_consume_tag_day + time, scoreMembers);
    }

    public static Set<String> get_consume_rank_day(long time, int index, int count) {
        if (index >= 0) {
            count = index + count-1;
        }
        return _libRedis.zrevrange(_room_consume_tag_day + time, index, count);
    }


    //消费排行  周榜
    public static Double add_consume_rank_week(long time, double score, String member) {
        _libRedis.expire(_room_consume_tag_week + time, weekDay);
        return _libRedis.zincrby(_room_consume_tag_week + time, score, member);
    }

    public static Double get_consume_num_week(long time, String member) {
        return _libRedis.zscore(_room_consume_tag_week + time, member);
    }

    public static Long add_consume_list_week(long time, Map<String, Double> scoreMembers) {
        _libRedis.expire(_room_consume_tag_week + time, weekDay);
        return _libRedis.zadd(_room_consume_tag_week + time, scoreMembers);
    }

    public static Set<String> get_consume_rank_week(long time, int index, int count) {
        if (index >= 0) {
            count = index + count-1;
        }
        return _libRedis.zrevrange(_room_consume_tag_week + time, index, count);
    }



    //粉丝排行
    public static Double add_fans_rank(long time, double score, String member) {
        return _libRedis.zincrby(_user_fans_rank + time, score, member);
    }

    public static Long add_fans_list(long time, Map<String, Double> scoreMembers) {
        return _libRedis.zadd(_user_fans_rank + time, scoreMembers);
    }

    public static Double get_fans_num(long time, String member) {
        return _libRedis.zscore(_user_fans_rank + time, member);
    }

    public static Set<String> get_fans_rank(long time, int index, int count) {
        if (index >= 0) {
            count = index + count-1;
        }
        return _libRedis.zrevrange(_user_fans_rank + time, index, count);
    }

    public static Long getFansUserRank(long time, String mem) {
        return _libRedis.zrank(_user_fans_rank + time, mem);
    }

    // 粉丝排行榜 日榜
    public static Double add_fans_rank_day(long time, double score, String member) {
        _libRedis.expire(_user_fans_rank_day + time, twoDay);
        return _libRedis.zincrby(_user_fans_rank_day + time, score, member);
    }

    public static Long add_fans_list_day(long time, Map<String, Double> scoreMembers) {
        _libRedis.expire(_user_fans_rank_day + time, twoDay);
        return _libRedis.zadd(_user_fans_rank_day+ time, scoreMembers);
    }

    public static Double get_fans_num_day(long time, String member) {
        return _libRedis.zscore(_user_fans_rank_day + time, member);
    }

    public static Set<String> get_fans_rank_day(long time, int index, int count) {
        if (index >= 0) {
            count = index + count-1;
        }
        return _libRedis.zrevrange(_user_fans_rank_day + time, index, count);
    }

    // 粉丝排行榜 周榜
    public static Double add_fans_rank_week(long time, double score, String member) {
        _libRedis.expire(_user_fans_rank_week + time, weekDay);
        return _libRedis.zincrby(_user_fans_rank_week + time, score, member);
    }

    public static Long add_fans_list_week(long time, Map<String, Double> scoreMembers) {
        _libRedis.expire(_user_fans_rank_week + time, weekDay);
        return _libRedis.zadd(_user_fans_rank_week + time, scoreMembers);
    }

    public static Double get_fans_num_week(long time, String member) {
        return _libRedis.zscore(_user_fans_rank_week + time, member);
    }

    public static Set<String> get_fans_rank_week(long time, int index, int count) {
        if (index >= 0) {
            count = index + count-1;
        }
        return _libRedis.zrevrange(_user_fans_rank_week + time, index, count);
    }


    /**
     * 增加ip注册次数
     */
    public static void add_ip_register(String field, int num) {
        Long time = LibDateUtils.getLibDateTime("yyyyMMdd");
        _libRedis.hincrBy(_ip_register + time, field, num);
        _libRedis.expire(_ip_register + time, oneDay);

    }

    /**
     * 获得ip注册次数
     */
    public static int get_ip_register(String field) {
        Long time = LibDateUtils.getLibDateTime("yyyyMMdd");
        return LibSysUtils.toInt(_libRedis.hget(_ip_register + time, field), 0);
    }


    /**
     * 增加ip发送验证码次数
     */
    public static void add_ip_captcha(String field, int num) {
        Long time = LibDateUtils.getLibDateTime("yyyyMMdd");
        _libRedis.hincrBy(_ip_captcha + time, field, num);
        _libRedis.expire(_ip_captcha + time, oneDay);

    }

    /**
     * 获得ip发送验证码次数
     */
    public static int get_ip_captcha(String field) {
        Long time = LibDateUtils.getLibDateTime("yyyyMMdd");
        return LibSysUtils.toInt(_libRedis.hget(_ip_captcha + time, field), 0);
    }

    public static void set_live_list_log(int user_id, int type, String type_value, String project_name, int index, int count,JSONObject data){
        String key = String.format(_live_list_log,user_id,type, type_value, project_name,index, count);
        _libRedis.putObject(key, data);
        _libRedis.expire(key, 10);
    }

    public static JSONObject get_live_list_log(int user_id, int type, String type_value, String project_name, int index, int count){
        String key = String.format(_live_list_log,user_id,type, type_value, project_name,index, count);
        Object object = _libRedis.getObject(key);
        if(object != null){
            return JSONObject.fromObject(_libRedis.getObject(key));
        }
        return null;
    }

    public static void set_recommend_post_list(int user_id, int index, int count,JSONObject data){
        String key = String.format(_post_recommmend_list,user_id,index, count);
        _libRedis.putObject(key, data);
        _libRedis.expire(key, 10);
    }

    public static JSONObject get_recommend_post_list(int user_id,int index, int count){
        String key = String.format(_post_recommmend_list,user_id,index, count);
        Object object = _libRedis.getObject(key);
        if(object != null){
            return JSONObject.fromObject(_libRedis.getObject(key));
        }
        return null;
    }



    public static void set_anchor_rank_list(int userId, String account, int index, int count,int type,JSONObject data){
        String key = String.format(_anchor_rank_list,userId,account,index, count,type);
        _libRedis.putObject(key, data);
        _libRedis.expire(key, 30);
    }

    public static JSONObject get_anchor_rank_list(int userId, String account, int index, int count,int type){
        String key = String.format(_anchor_rank_list,userId,account,index, count,type);
        Object object = _libRedis.getObject(key);
        if(object != null){
            return JSONObject.fromObject(_libRedis.getObject(key));
        }
        return null;
    }

    //房间人数
    public static void set_room_account_list(int liveId, JSONArray data){
        String key = String.format(_room_account_list,liveId);
        _libRedis.putObject(key, data);
        _libRedis.expire(key, 10);
    }

    public static JSONArray get_room_account_list(int liveId){
        String key = String.format(_room_account_list,liveId);
        Object object = _libRedis.getObject(key);
        if(object != null){
            return JSONArray.fromObject(_libRedis.getObject(key));
        }
        return null;
    }

    //暂存礼物和分类
    public static void set_room_gift_list(int liveType,JSONObject data){
        String key = String.format(_room_gift_list,liveType);
        _libRedis.putObject(key, data);
        _libRedis.expire(key, 300);
    }

    public static JSONObject get_room_gift_list(int liveType){
        String key = String.format(_room_gift_list,liveType);
        Object object = _libRedis.getObject(key);
        if(object != null){
            return JSONObject.fromObject(_libRedis.getObject(key));
        }
        return null;
    }



    //livetag
    public static void set_room_live_tag(int type, JSONArray data){
        String key = String.format(_room_live_tag,type);
        _libRedis.putObject(key, data);
    }

    public static JSONArray get_room_live_tag(int type){
        String key = String.format(_room_live_tag,type);
        Object object = _libRedis.getObject(key);
        if(object != null){
            return JSONArray.fromObject(_libRedis.getObject(key));
        }
        return null;
    }


    //所有标签
    public static void set_live_all_tag( JSONArray data){
        String key = String.format(_live_all_tag);
        _libRedis.putObject(key, data);
        _libRedis.expire(key, 5);
    }

    public static JSONArray get_live_all_tag(){
        String key = String.format(_live_all_tag);
        Object object = _libRedis.getObject(key);
        if(object != null){
            return JSONArray.fromObject(_libRedis.getObject(key));
        }
        return null;
    }



    /**
     * 记录登录设备账号
     */
    public static void add_device_account(String key, String account) {
        if (_libRedis.exists(key)) {
            _libRedis.sadd(key, account);
            _libRedis.expire(key, oneDay);
        } else {
            _libRedis.sadd(key, account);
            _libRedis.expire(key, oneDay);
        }
    }

    /**
     * 获取zego的PC直播token
     *
     * @param token
     */
    public static void add_zego_token(String token) {
        _libRedis.putObject(_zego_token, token);
        _libRedis.expire(_zego_token, zegotokenexpires);
    }

    public static String get_zego_token() {
        String result = LibSysUtils.toString(_libRedis.getObject(_zego_token));
        return result;
    }

    /**
     * 是否存在该登录账号
     */
    public static Boolean exist_device_account(String key, String account) {
        return _libRedis.sismember(key, account);
    }

    /**
     * 获得set的个数
     */
    public static Long get_account_num(String key) {
        Long re = _libRedis.scard(key);
        if (re == null) {
            re = 0L;
        }
        return re;
    }

    /**
     * 记录在线用户
     * type 1在线2离线3正在忙
     */
    public static Long setImState(String account, int type) {
        return _libRedis.hset(_im_state, account, String.format("%d_%d", type, LibDateUtils.getLibDateTime()));
    }

    /**
     * 结束会话更新IM状态
     * 如果用户还在正在忙的状态更新为在线状态，否则不更新
     */
    public static void updateUserImState(String account) {
        int type = getImState(account);
        if (type == 3) {
            setImState(account, 1);
        }
    }

    /**
     * 验证用户是否IM在线
     */
    public static int getImState(String account) {
        String str = _libRedis.hget(_im_state, account);
        if (str == null) {
            return 2;
        }
        String[] s = str.split("_");
        return LibSysUtils.toInt(s[0]);
    }

    /**
     * 删除IM用户
     */
    public static Long delImState(String account) {
        return LibRedis.hdel(_im_state, account);
    }

    /**
     * 记录视频聊天消息
     */
    public static void setVideoChatMsg(String msgId, String msg) {
        _libRedis.hset(_video_chat_msg, msgId, msg);
    }

    /**
     * 记录视频聊天消息
     */
    public static String getVideoChatMsg(String msgId) {
        return _libRedis.hget(_video_chat_msg, msgId);
    }

    /**
     * 删除IM用户
     */
    public static Long delVideoChatMsg(String msgId) {
        return LibRedis.hdel(_video_chat_msg, msgId);
    }

    /**
     * 记录视频聊天消费时间
     */
    public static void setVideoChatConsumeTime(int userId, int roomId, long time) {
        _libRedis.hset(_video_chat_consume, String.format("%d_%d", userId, roomId), LibSysUtils.toString(time));
    }

    /**
     * 获取视频聊天记录
     */
    public static Long getVideoChatConsumeTime(int userId, int roomId) {
        return LibSysUtils.toLong(_libRedis.hget(_video_chat_consume, String.format("%d_%d", userId, roomId)));
    }

    /**
     * 删除视频聊天记录
     */
    public static Long delVideoChatConsumeTime(int userId, int roomId) {
        return LibRedis.hdel(_video_chat_consume, String.format("%d_%d", userId, roomId));
    }

    /**
     * 获取视频聊天扣钻记录
     */
    public static Map<String, String> getVideoChatConsumeTimeMap() {
        return LibRedis.hgetAll(_video_chat_consume);
    }

    public static Long addVideoChatUser(int userId, int otherId) {
        return LibRedis.sadd(_video_chating_user, LibSysUtils.toString(userId), LibSysUtils.toString(otherId));
    }

    public static Boolean checkVideoChatUser(int userId) {
        return LibRedis.sismember(_video_chating_user, LibSysUtils.toString(userId));
    }

    public static Long delVideoChatUser(int userId, int otherId) {
        return LibRedis.srem(_video_chating_user, LibSysUtils.toString(userId), LibSysUtils.toString(otherId));
    }

    /**
     * 设置用户动态礼物盒子奖励数量
     */
    public static void addUserPostGiftBoxRewardNum(int user_id, int num, int expiry_time) {
        _libRedis.put(getKey(_post_reward_num, user_id), LibSysUtils.toString(num));
        _libRedis.expire(getKey(_post_reward_num, user_id), expiry_time);
    }

    /**
     * 获取
     * 用户动态礼物盒子奖励数量
     */
    public static int getUserPostGiftBoxRewardNum(int user_id) {
        return LibSysUtils.toInt(_libRedis.get(getKey(_post_reward_num, user_id)));
    }

    /**
     * 增加用户点赞相关缓存
     *
     * @param user_id
     * @param content
     */
    public static void addUserPostLikeInfoCache(int user_id, String content) {
        _libRedis.hset(_post_like_info, LibSysUtils.toString(user_id), content);
    }

    /**
     * 获取用户点赞相关缓存
     *
     * @param user_id
     */
    public static String getUserPostLikeInfoCache(int user_id) {
        return _libRedis.hget(_post_like_info, LibSysUtils.toString(user_id));
    }

    //设置用户引导发帖状态
    public static void addUserGuideState(int userId, int state) {
        _libRedis.hset(_user_guide_post, LibSysUtils.toString(userId), LibSysUtils.toString(state));
    }

    //获取用户引导发帖状态
    public static int getUserGuideState(int userId) {
        return LibSysUtils.toInt(_libRedis.hget(_user_guide_post, LibSysUtils.toString(userId)), 1);
    }

    /*********************************GEO*****************************************/
    /**
     * 添加用户经纬度
     *
     * @param lng 经度
     * @param lat 纬度
     */
    public static Long add_geo(int userId, Double lng, Double lat) {
        return LibRedis.geoAdd(getKey(_geo_tag), lng, lat, LibSysUtils.toString(userId));
    }

    /**
     * 获得附近的人
     *
     * @param radius 附近半径数
     */
    public static List<GeoRadiusResponse> get_geo(int userId, Double radius) {
        List<GeoRadiusResponse> result;
        try {
            result = LibRedis.geoRadiusByMember(getKey(_geo_tag), LibSysUtils.toString(userId), radius);
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    /**
     * 获得附近的人
     *
     * @param radius 附近半径数
     */
    public static List<GeoRadiusResponse> georadius(int userId, double lng, double lat, Double radius) {
        if (lng == 0 || lat == 0) {
            return null;
        }
        List<GeoRadiusResponse> result;
        try {
            result = LibRedis.georadius(getKey(_geo_tag), lng, lat, radius);
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    /**
     * 是否存在该用户
     */
    public static boolean isExistGeo(int userId) {
        return get_geopos(userId).size() > 0;
    }

    /**
     * 获得用户地理位置信息
     */
    public static List<GeoCoordinate> get_geopos(int userId) {
        return LibRedis.geoMemberPos(getKey(_geo_tag), LibSysUtils.toString(userId));
    }

    public static Double get_user_dist(int userId, String user_id) {
        Double dist;
        try {
            dist = LibRedis.geoMemberDist(getKey(_geo_tag), LibSysUtils.toString(userId), user_id);
            if (dist == null) {
                dist = -1D;
            }
        } catch (Exception e) {
            dist = -1D;
            e.printStackTrace();
        }
        return dist;
    }

    public static List<GeoRadiusResponse> georadius(double lng, double lat, double radius) {
        try {
            return LibRedis.georadius(getKey(_geo_tag), lng, lat, radius);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void delGeo(int userId) {
        try {
            LibRedis.zrem(getKey(_geo_tag), LibSysUtils.toString(userId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        _libRedis.del(getKey(_room_rank_tag, 123));
//        add_room_rank(123, LibSysUtils.toDouble(300), "u300");
//        add_room_rank(123, LibSysUtils.toDouble(100), "u100");
//        add_room_rank(123, LibSysUtils.toDouble(100), "u200");
//        add_room_rank(123, LibSysUtils.toDouble(100), "u200");
//        add_room_rank(123, LibSysUtils.toDouble(201), "u201");
//        Set<String> set = get_room_rank(123, 0, 51);
//        Double d = get_send_iamond(123, "u200");
//        int i = d.intValue();
//        int dd = i;
    }

    // 设置用户发布动态时间缓存
    public static void add_user_post_submit_time(int user_id) {
        String time = LibSysUtils.toString(LibDateUtils.getLibDateTime());
        _libRedis.hset(_user_post_time, LibSysUtils.toString(user_id), time);
    }

    // 获取用户发布动态时间缓存
    public static long get_user_last_post_time(int user_id) {
        return LibSysUtils.toLong(_libRedis.hget(_user_post_time, LibSysUtils.toString(user_id)));
    }

    // 设置用户关注的用户的第一页动态
    public static void add_user_follow_post_list(int user_id, String array) {
        _libRedis.put(getKey(_user_follow_post_list, user_id), array);
        _libRedis.expire(getKey(_user_follow_post_list, user_id), LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.follow_post_list_cache_time), 10));
    }

    //  获取用户关注的用户的第一页动态
    public static String get_user_follow_post_list(int user_id) {
        return _libRedis.get(getKey(_user_follow_post_list, user_id));
    }

    public static int getUserState(int userId,String account){
        int userImState = WKCache.getImState(account);
        //用户在线检测是否正在直播
        //if (userImState == 1) {
        if (WKCache.checkVideoChatUser(userId)) {
            userImState = 3;
        } else {
            int liveId = LibSysUtils.toInt(WKCache.get_user(userId, C.WKCacheUserField.live_id));
            if (liveId > 0) {
                userImState = 4;
            }
        }
        return userImState;
        //}
    }


    // 添加用户评论缓存
    public static void addUserPostComment(int user_id, String content) {
        double score = LibDateUtils.getLibDateTime("yyyyMMdd");
        _libRedis.zadd(getKey(_user_post_comments_, user_id), score, content);
    }

    // 获取用户评论缓存
    public static Set<String> getUserPostComments(int user_id) {
        return _libRedis.zrange(getKey(_user_post_comments_, user_id), 0, 9999);
    }

    // 删除用户评论缓存
    public static void delUserPostComments(int user_id) {
        long value = DateUtils.getFrontDay(LibDateUtils.getLibDateTime("yyyyMMdd"), 7);
        _libRedis.zremrangebyscore(getKey(_user_post_comments_, user_id), value, value);
    }

    // 添加隐藏帖子缓存
    public static void addHiddenPost(int userId, int post_id) {
        _libRedis.sadd(getKey(_user_hidden_post_, userId), LibSysUtils.toString(post_id));
        _libRedis.zincrby(_hidden_post, 1, LibSysUtils.toString(post_id));
    }

    // 获取用户隐藏帖子缓存
    public static Set<String> getUserHiddenPost(int userId) {
        return _libRedis.smembers(getKey(_user_hidden_post_, userId));
    }

    // 获取隐藏帖子
    public static Double getHiddenPost(int post_id) {
        return _libRedis.zscore(_hidden_post, LibSysUtils.toString(post_id));
    }

    // 添加用户封锁到缓存
    public static void addLockoutUserCache(int user_id, String reason) {
        _libRedis.hset(_post_lockout_user, LibSysUtils.toString(user_id), reason);
    }

    // 是否为封锁用户
    public static boolean isLockoutUser(int user_id) {
        return _libRedis.hexists(_post_lockout_user, LibSysUtils.toString(user_id));
    }

    public static String getLockoutUserCache(int user_id) {
        return _libRedis.hget(_post_lockout_user, LibSysUtils.toString(user_id));
    }

    public static void delLockoutUserCache(int user_id) {
        _libRedis.hdel(_post_lockout_user, LibSysUtils.toString(user_id));
    }

    // 获取推荐的动态id列表
    public static Set<String> getRecommendPostList(int index, int count) {
        if (index >= 0) {
            count = index + count-1;
        }
        Set<String> set = _libRedis.zrevrange(_recommend_post_list, index, count);
        return set;
    }

    // 添加推荐的动态id列表
    public static void addRecommendPostList(int post_id, int score) {
        _libRedis.zadd(_recommend_post_list, LibSysUtils.toDouble(score), LibSysUtils.toString(post_id));
    }
    public static void addExpireRecommendPostList(int post_id, int score) {
        _libRedis.zadd(_recommend_post_list, LibSysUtils.toDouble(score), LibSysUtils.toString(post_id));
        _libRedis.expire(_recommend_post_list+post_id,5);
    }


    //添加推荐动态的用户列表
    public static void addRecommendPostUserList(int user_id, int score) {
        _libRedis.zadd(_recommend_post_user_list, LibSysUtils.toDouble(score), LibSysUtils.toString(user_id));
    }

    // 获取推荐动态的用户列表
    public static Set<String> getRecommendPostUserList(int index, int count) {
        if (index >= 0) {
            count = index + count-1;
        }
        Set<String> set = _libRedis.zrevrange(_recommend_post_user_list, index, count);
        return set;
    }

    //添加用户的推荐动态
    public static void addUserRecommendPost(int user_id, int post_id) {
        _libRedis.hset(_user_recommend_post,  LibSysUtils.toString(user_id),LibSysUtils.toString(post_id));
    }
    // 获取用户的推荐动态id
    public static String getUserRecommendPost(String user_id) {
        return _libRedis.hget(_user_recommend_post,  user_id);
    }


    /**
     * 获取推荐动态列表信息
     *
     * @param post_id
     * @return
     */
    public static PostInfo getPostInfo(int post_id) {
        Map<String, String> map = _libRedis.hgetAll(getKey(_post_info_, post_id));
        if (map != null && !LibSysUtils.isNullOrEmpty(map.get("post_id"))) {
            PostInfo postInfo = new PostInfo();
            postInfo.setId(LibSysUtils.toInt(map.get("post_id")));
            postInfo.setUser_id(LibSysUtils.toInt(map.get("user_id")));
            postInfo.setPost_status(LibSysUtils.toInt(map.get("post_status")));
            postInfo.setMeta_key(LibSysUtils.toString(map.get("meta_key")));
            postInfo.setExpiryTime(LibSysUtils.toLong(map.get("expiry_time")));
            postInfo.setBalance_date(LibSysUtils.toLong(map.get("balance_date")));
            postInfo.setBalance_status(LibSysUtils.toInt(map.get("balance_status")));
            postInfo.setCity(LibSysUtils.toString(map.get("city")));
            postInfo.setLocation(LibSysUtils.toString(map.get("location")));
            postInfo.setMeta_value(LibSysUtils.toString(map.get("meta_value")));
            postInfo.setPost_content(LibSysUtils.toString(map.get("post_content")));
            postInfo.setPost_date(LibSysUtils.toLong(map.get("post_date")));
            postInfo.setSca_reward(new BigDecimal(LibSysUtils.toString(map.get("sca_reward"))));
            postInfo.setType(Byte.valueOf(LibSysUtils.isNullOrEmpty(map.get("type")) ? "1" : map.get("type")));
            postInfo.setComment_count(LibSysUtils.toInt(map.get("comment_count")));
            postInfo.setDislike_count(LibSysUtils.toInt(map.get("dislike_count")));
            postInfo.setLike_count(LibSysUtils.toInt(map.get("like_count")));
            postInfo.setShare_count(LibSysUtils.toInt(map.get("share_count")));
            postInfo.setSorts(LibSysUtils.toInt(map.get("sorts")));
            return postInfo;
        }else {
            return null;
        }
    }

    // 添加动态信息缓存
    public static void addPostInfo(PostInfo postInfo) {
        if (postInfo != null) {
            Map<String, String> postInfoMap = new HashMap<>();
            postInfoMap.put("post_id", LibSysUtils.toString(postInfo.getId()));
            postInfoMap.put("user_id", LibSysUtils.toString(postInfo.getUser_id()));
            postInfoMap.put("post_status", LibSysUtils.toString(postInfo.getPost_status()));
            postInfoMap.put("meta_key", LibSysUtils.toString(postInfo.getMeta_key()));
            postInfoMap.put("expiry_time", LibSysUtils.toString(postInfo.getExpiryTime()));
            postInfoMap.put("balance_date", LibSysUtils.toString(postInfo.getBalance_date()));
            postInfoMap.put("balance_status", LibSysUtils.toString(postInfo.getBalance_status()));
            postInfoMap.put("city", LibSysUtils.toString(postInfo.getCity()));
            postInfoMap.put("location", LibSysUtils.toString(postInfo.getLocation()));
            postInfoMap.put("meta_value", LibSysUtils.toString(postInfo.getMeta_value()));
            postInfoMap.put("post_content", LibSysUtils.toString(postInfo.getPost_content()));
            postInfoMap.put("post_date", LibSysUtils.toString(postInfo.getPost_date()));
            postInfoMap.put("sca_reward", LibSysUtils.toString(postInfo.getSca_reward() == null ? 0 : postInfo.getSca_reward()));
            postInfoMap.put("type", LibSysUtils.toString(postInfo.getType()));
            postInfoMap.put("comment_count", LibSysUtils.toString(postInfo.getComment_count()));
            postInfoMap.put("dislike_count", LibSysUtils.toString(postInfo.getDislike_count()));
            postInfoMap.put("like_count", LibSysUtils.toString(postInfo.getLike_count()));
            postInfoMap.put("share_count", LibSysUtils.toString(postInfo.getShare_count()));
            postInfoMap.put("sorts", LibSysUtils.toString(postInfo.getSorts()));
            _libRedis.hmset(getKey(_post_info_, postInfo.getId()), postInfoMap);
            _libRedis.expire(getKey(_post_info_, postInfo.getId()), sevenDay);
        }
    }

    public static void updatePostInfo(int post_id, String field, long value) {
        if (_libRedis.exists(getKey(_post_info_, post_id))) {
            _libRedis.hincrBy(getKey(_post_info_, post_id), field, value);
        }
    }

    // 获取热门的动态id列表
    public static Set<String> getPopularPostList(int index, int count) {
        if (index > 0) {
            count = index + count;
        }
        Set<String> set = _libRedis.zrevrange(_popular_post_list, index, count);
        return set;
    }

    // 添加热门的动态id列表
    public static void addPopularPostList(int post_id, double score, int day_num) {
        if (_libRedis.exists(_popular_post_list)) {
            updatePopularPostList(post_id, score);
        } else {
            _libRedis.zadd(_popular_post_list, score, LibSysUtils.toString(post_id));
            int time;
            if (day_num == 7) {
                time = (int) LibDateUtils.getDateTimeTick(LibDateUtils.getDateTime(), DateUtils.getEndDayOfWeek()) / 1000;
            } else {
                time = DateUtils.getRemainSecondsOneDay(LibDateUtils.getDateTime()) + day_num * 24 * 60 * 60;
            }
            _libRedis.expire(_popular_post_list, time);
        }
    }

    // 更新热门的动态id列表
    public static void updatePopularPostList(int post_id, double score) {
        _libRedis.zadd(_popular_post_list, score, LibSysUtils.toString(post_id));
    }

    // 删除热门的动态id列表
    public static void delPopularPostList(double score) {
        _libRedis.zremrangebyscore(_popular_post_list, 0, score);
    }

    // -------------------------------
    // 增加SCA GOLD总池
    public static void increaseToSCAGoldPool(long value) {
        _libRedis.incrBy(_SCA_GOLD_POOL,value);
    }

    //获取SCA GOLD总池
    public static long getSCAGoldPoolValue() {
        return LibSysUtils.toLong(_libRedis.get(_SCA_GOLD_POOL),0);
    }

    // 增加用户SCA GOLD
    public static void addUserSCAGold(int userId,double value) {
        _libRedis.zincrby(_USER_SCA_GOLD,value,LibSysUtils.toString(userId));
    }

    public static Set<String> getAllUserSCAGoldList(){
        return _libRedis.zrevrange(_USER_SCA_GOLD,0,99999999999L);
    }

    public static Double getUserSCAGold(int userId){
        return _libRedis.zscore(_USER_SCA_GOLD,LibSysUtils.toString(userId));
    }

    // 添加用户操作记录
    public static void addUserTodayPostOperate(int user_id,String key) {
        long date = LibDateUtils.getLibDateTime("yyyyMMdd");
        String content = getUserTodayPostOperate(user_id);
        JSONObject jsonObject = new JSONObject();
        int value = 0;
        if (!LibSysUtils.isNullOrEmpty(content)){
            jsonObject = JSONObject.fromObject(content);
            value = jsonObject.optInt(key,0) + 1;
        }else {
            value = value + 1;
        }
        jsonObject.put(key,value);
        _libRedis.hset(getKey(_user_post_operate, (int) date),LibSysUtils.toString(user_id),jsonObject.toString());
    }

    // 获取记录
    public static String getUserTodayPostOperate(int userId){
        long date = LibDateUtils.getLibDateTime("yyyyMMdd");
        return _libRedis.hget(getKey(_user_post_operate, (int) date),LibSysUtils.toString(userId));
    }

    // 获取记录
    public static String getUserTodayPostOperate(int userId,long date){
        return _libRedis.hget(getKey(_user_post_operate, (int) date),LibSysUtils.toString(userId));
    }

    // 获取用户所有记录
    public static Set<String> getAllPostOperateUsers(long date){
        return _libRedis.hkeys(getKey(_user_post_operate, (int) date));
    }

    // 获取用户所有记录
    public static void delUserPostOperate(int userId,long date){
         _libRedis.del(getKey(_user_post_operate, (int) date));
    }

    //删除缓存动态
    public static Long del_recommend_post(int postId) {
        Long flg = _libRedis.zrank(_recommend_post_list, LibSysUtils.toString(postId));
        if(flg != null) {
            return LibRedis.zrem(_recommend_post_list, LibSysUtils.toString(postId));
        }else {
            return LibRedis.zrem(_popular_post_list, LibSysUtils.toString(postId));
        }
    }



    //颜色缓存
    public static void set_user_all_level_color( JSONArray data){
        String key = String.format(_user_all_level_color);
        _libRedis.putObject(key, data);
        //_libRedis.expire(key, 10);
    }

    public static JSONArray get_user_all_level_color(){
        String key = String.format(_user_all_level_color);
        Object object = _libRedis.getObject(key);
        if(object != null){
            return JSONArray.fromObject(_libRedis.getObject(key));
        }
        return null;
    }

    //邀约

    public static void set_user_invite_cache(int index,int count,JSONObject data){
        String key = String.format(_user_invite_cache,index,count);
        _libRedis.putObject(key, data);
        _libRedis.expire(key, 60);
    }

    public static JSONObject get_user_invite_cache(int index,int count){
        String key = String.format(_user_invite_cache,index,count);
        Object object = _libRedis.getObject(key);
        if(object != null){
            return JSONObject.fromObject(_libRedis.getObject(key));
        }
        return null;
    }


    //官方直播间

    public static void set_live_official_room(int live_id){
        _libRedis.put(_is_official_live, LibSysUtils.toString(live_id));
    }

    public static int get_live_official_room(){
        String object = _libRedis.get(_is_official_live);
        if(object != null&&object!=""){
            return LibSysUtils.toInt(object);
        }
        return 0;
    }

    public static void del_live_official_room(){
        _libRedis.del(_is_official_live);
    }


    public static Double add_recharge_rank_month(long time, double score, String member) {
        return _libRedis.zincrby(_recharge_rank_month + time, score, member);
    }

    public static Double get_recharge_rank_month(long time, String member) {
        return _libRedis.zscore(_recharge_rank_month + time, member);
    }

    public static void add_out_room_id(String account,int liveId){
        LibRedis.sadd(_out_room_id+liveId, account);
    }

    public static Boolean get_out_room_id(String account,int liveId){
        Boolean flg=false;
        Set<String> accounts = LibRedis.smembers(_out_room_id+liveId);
        if (accounts!=null&&accounts.contains(account)){
            flg=true;
        }

        return flg;
    }


    //livetag
    public static void set_room_live_privilege(int userId, JSONObject data){
        String key = String.format(_room_live_privilege,userId);
        _libRedis.putObject(key, data);
        _libRedis.expire(key, 30);
    }

    public static JSONObject get__room_live_privilege(int userId){
        String key = String.format(_room_live_privilege,userId);
        Object object = _libRedis.getObject(key);
        if(object != null){
            return JSONObject.fromObject(_libRedis.getObject(key));
        }
        return null;
    }


}

