package com.weking.core;

import com.wekingframework.core.LibProperties;

/**
 * Created by on 2017/4/27.
 * 统一定义，避免分散
 * 常量
 */
public class C {
    public static final String projectName = LibProperties.getConfig("weking.config.project.name"); //  系统名称

    public static final int SystemUserId = 0;       //系统userID
    public static final String ManagerSysAccessToken = "weking2016";       //系统userID


    public static class WKCacheKey {
        //红包
        public static String RPList = C.projectName +"RPList"; //红包
        public static String RPConsuList = C.projectName +"RPConsuList"; //消费了的红包
        public static String RPConsuMap = C.projectName +"RPConsuMap";   //消费了的用户
        public static String RPMoney = C.projectName +"RPMoney";         //红包总金额
    }

    /**
     * WKCacheUser的field
     */
    public class WKCacheUserField {
        public static final String user_id = "user_id";
        public static final String account = "account";
        public static final String avatar = "avatar";           //头像
        public static final String c_id = "c_id";               //个推id
        public static final String device_token = "device_token";
        public static final String login_time = "login_time";
        public static final String nickname = "nickname";
        public static final String login_type = "login_type";
        public static final String access_token = "access_token";
        public static final String lang_code = "lang_code";
        public static final String level = "level";
        public static final String lat = "lat";
        public static final String lng = "lng";
        public static final String experience = "experience";   //经验
        public static final String sorts = "sorts";             //主播排序
        public static final String live_id = "live_id";             //live_id
        public static final String anchor_type = "anchor_type";             //anchor_type
        public static final String live_type = "live_type";             //直播类型（0才艺 1游戏 2游戏加才艺）
        public static final String imei = "imei";             //登录设备号
        public static final String project_type = "project_type";             //

        public static final String vip_level = "vip_level";
        public static final String vip_experience = "vip_experience";
    }

    /**
     * 缓存房间的field
     */
    public class WKCacheRoomField {
        public static final String attendance = "attendance";               //人数
        public static final String real_audience = "real_audience";         //实际观看高峰人数，只增不减
        public static final String online_audience = "online_audience";     //在线观众
        public static final String city = "city";
        public static final String live_stream_id = "live_stream_id";
        public static final String heart_time = "heart_time";
        public static final String live_id = "live_id";
        public static final String user_id = "user_id";
        public static final String link_live_stream_id = "link_live_stream_id";
        public static final String link_live_account = "link_live_account";
        public static final String account = "account";
        public static final String live_type = "live_type";
        public static final String live_ticket = "live_ticket";             //付费播时的门票
        public static final String live_pwd = "live_pwd";                   //私密播时的密码
        public static final String pause_live = "pause_live";               //主播是否退到后台
        public static final String nickname = "nickname";
        public static final String live_start = "live_start";
        public static final String longitude = "longitude";
        public static final String latitude = "latitude";
        public static final String live_cover = "live_cover";
        public static final String live_time = "live_time";
        public static final String live_title = "live_title";
        public static final String game_type = "game_type";
        public static final String avatar = "avatar";
        public static final String role = "role";
        public static final String is_horizontal = "is_horizontal";
        public static final String replay_url = "replay_url";
    }
    /**
     * 缓存key
     */
    public class WKSystemCacheField {
        public static final String GLOBAL_MIN_PRICE_GIFT = "s_global_min_gift"; //全服飘屏礼物，最小单价
        public static final String GLOBAL_MIN_VALUE_WINNER = "s_global_min_winner"; //全服飘屏礼物，最小值
        public static final String GAME_DOLL_BALANCE = "s_game_doll_balance";
        public static final String GAME_STAR_WARS_BALANCE = "s_game_star_wars_balance";
        public static final String s_nearby_radius = "s_nearby_radius";
        public static final String s_gt_url = "s_gt_url";
        public static final String s_getui_appkey_copy = "s_getui_appkey_copy";
        public static final String s_getui_appkey_copy2 = "s_getui_appkey_copy2";
        public static final String s_getui_mastersecret_copy = "s_getui_mastersecret_copy";
        public static final String s_getui_mastersecret_copy2 = "s_getui_mastersecret_copy2";
        public static final String s_getui_appid_copy = "s_getui_appid_copy";
        public static final String s_getui_appid_copy2 = "s_getui_appid_copy2";
        public static final String s_tencent_bizid = "s_tencent_bizid";
        public static final String s_tencent_push_key = "s_tencent_push_key";
        public static final String s_tencent_check_key = "s_tencent_check_key";
        public static final String s_game_commission = "s_game_commission";
        public static final String s_game_bet_max = "s_game_bet_max";
        public static final String s_googlepay_key = "s_googlepay_key";
        public static final String s_apple_pay_debug = "s_apple_pay_debug";
        public static final String s_api_url = "s_api_url"; //  微信等第三方回调
        public static final String s_server_ip = "s_server_ip"; // 登录的时候返回
        public static final String s_mobile_url = "s_mobile_url"; // 登录的时候返回
        public static final String s_pic_server = "s_pic_server";
        public static final String s_debug = "s_debug";
        public static final String s_gt_debug_appId = "s_gt_debug_appId";
        public static final String s_gt_appId = "s_gt_appId";
        public static final String s_gt_debug_appKey = "s_gt_debug_appKey";
        public static final String s_gt_appKey = "s_gt_appKey";
        public static final String s_gt_debug_masterSecret = "s_gt_debug_masterSecret";
        public static final String s_gt_masterSecret = "s_gt_masterSecret";
        public static final String s_wx_recharge_pay = "s_wx_recharge_pay";
        public static final String s_apple_recharge_pay = "s_apple_recharge_pay";
        public static final String s_wx_recharge_pay_copy = "s_wx_recharge_pay_copy";
        public static final String s_apple_recharge_pay_copy = "s_apple_recharge_pay_copy";
        public static final String s_cash_rate = "s_cash_rate";
        public static final String s_is_real_name = "s_is_real_name";
        public static final String s_live_public_diamond = "s_live_public_diamond";//开通直播权限所需要的钻石数
        public static final String s_live_pay_ticket = "s_live_pay_ticket";
        public static final String s_game_anchor_subsidy = "s_game_anchor_subsidy";
        public static final String weking_config_robot = "weking.config.robot"; //是否启用机器人
        public static final String s_vvip_robot = "s_vvip_robot";
        public static final String EFFECT_LEVEL = "EFFECT_LEVEL";
        public static final String s_barrage_price = "s_barrage_price";
        public static final String s_link_level = "s_link_level";
        public static final String s_live_pay_duration = "s_live_pay_duration"; // 付费观看时长
        public static final String s_robot_msg = "s_robot_msg";
        public static final String s_game_open_rules = "s_game_open_rules";
        public static final String s_wx_web_appid = "s_wx_web_appid";
        public static final String s_wx_web_secret = "s_wx_web_secret";
        public static final String s_wx_pay_key = "s_wx_pay_key";
        public static final String s_wx_pay_mch_id = "s_wx_pay_mch_id";
        public static final String s_wx_pay_appid = "s_wx_pay_appid";
        public static final String s_account_len = "s_account_len";
        public static final String s_zego_app_id = "s_zego_app_id";
        public static final String s_zego_sign_key = "s_zego_sign_key";
        public static final String s_zego_level = "s_zego_level";
        public static final String s_zego_debug = "s_zego_debug";

        public static final String s_rp_sys_num = "s_rp_sys_num";       //系统发一次红包的红包数
        public static final String s_rp_sys_money = "s_rp_sys_money";   //系统发一次红包的金额
        public static final String s_rp_sys_percent = "s_rp_sys_percent";   //系统发一次红包的金额
        public static final String s_server_inip = "s_server_inip";   //内网服务器ip
        public static final String s_api_version = "s_api_version";   //api请求版本号

        public static final String s_alipay_pri_key = "s_alipay_pri_key";   //支付宝应用私钥
        public static final String s_alipay_pub_key = "s_alipay_pub_key";   //支付宝应用公钥
        public static final String s_alipay_pub_key2 = "s_alipay_pub_key2";   //支付宝公钥
        public static final String s_alipay_appid = "s_alipay_appid";   //支付宝appid

        public static final String s_si_xin_level = "s_si_xin_level";   //不需要互相关注，能直接发私信的等级
        public static final String s_bing_phone_diamond = "s_bing_phone_diamond";   //绑定手机送多少钻石

        public static final String s_send_room_msg_limit = "s_send_room_msg_limit";   //直播间发言限制
        public static final String s_task_diamond_mult = "s_task_diamond_mult";   //完成任务时，根据等级不同钻石的倍数
        public static final String s_enter_room_effect = "s_enter_room_effect";   //进入直播间特效
        public static final String s_banned_level = "s_banned_level";   //直播间禁言等级
        public static final String s_game_commission_rule = "s_game_commission_rule"; // 平台抽水比例规则

        public static final String S_GLOBAL_RULE = "s_global_rule";   //全服飘屏规则

        public static final String post_gift_box_config = "post_gift_box_config";   //动态礼物盒子配置
        public static final String post_like_share_config = "post_like_share_config";   //点赞邀请朋友挖矿配置
        public static final String post_like_share_url = "post_like_share_url";   //动态点赞邀请分享链接
        public static final String post_like_share_pic_url = "post_like_share_pic_url";   //动态点赞邀请分享链接展示图片

        public static final String ipay88_merchant_code = "ipay88_merchant_code";   //商家代码由iPay88提供并用于唯一标识
        public static final String ipay88_merchant_key = "ipay88_merchant_key";   //Ipay88提供的商家密钥
        public static final String ipay88_backend_post_url = "ipay88_backend_post_url";   //Ipay88支付回调地址
        public static final String guide_post_reward = "guide_post_reward";   //引导用户发帖挖矿，po成功获得sca奖励数量
        public static final String post_time_interval = "post_time_interval";   //发布动态时间间隔(秒)
        public static final String follow_post_list_cache_time = "follow_post_list_cache_time";   //关注动态列表缓存时间
        public static final String hide_post_dislike_num = "hide_post_dislike_num";   //dislike文章做隐藏操作次数
        public static final String hide_post_num = "hide_post_num";   //隐藏文章操作次数
        public static final String live_gift_box_push_config = "live_gift_box_push_config";   //通知领取直播间礼物盒子配置
        public static final String cny_to_myr_rate = "cny_to_myr_rate";   //人民币转马来西亚林吉特汇率
        public static final String s_post_adv_switch = "s_post_adv_switch";   //刷动态出现广告开关
        public static final String coin_withdraw_config = "coin_withdraw_config";   //货币提现配置
        public static final String push_post_switch = "push_post_switch";   //定时推送发文配置
        public static final String app_langage_kind = "app_langage_kind";   //APP用户言语种类
        public static final String popular_post_config = "popular_post_config";   //热门动态列表相关配置
        public static final String coin_proportion = "coin_proportion";   //货币对应比例
        public static final String reward_scagold_rate = "reward_scagold_rate";   //打赏emo,分配用户sca gold比例
        public static final String dividend_scagold_rate = "dividend_scagold_rate";   //平台分红比例
        public static final String inner_invite_config = "inner_invite_config";   //邀请配置
        public static final String withdraw_fee_rate = "withdraw_fee_rate";   //提现手续费
        public static final String dividend_switch = "dividend_switch";   //平台分红开关
        public static final String android_hide_version = "android_hide_version";   //安卓隐藏版本号
        public static final String recommend_post_switch = "recommend_post_switch";   //推荐动态tab开关
        public static final String reward_scagold_switch = "reward_scagold_switch";   //打赏礼物奖励SCA GOLD开关
        public static final String post_no_limit_users = "post_no_limit_users";   //发帖没有间隔限制的用户
        public static final String post_mining_emo = "post_mining_emo";   //(emo奖励)动态挖矿参数
        public static final String post_ad_insert_frequency = "post_ad_insert_frequency";   //动态列表插入广告频率（多少条插一个）
        public static final String guard_anchor_rate = "guard.anchor.rate";//主播购买守护分成
        public static final String pay_ezpay_hashKey = "pay.ezpay.hashKey";//hashKey
        public static final String pay_ezpay_iv = "pay.ezpay.iv";//iv
        public static final String pay_ezpay_email = "pay.ezpay.email";//email
        public static final String pay_ezpay_MerchantID = "pay.ezpay.MerchantID";//MerchantID
        public static final String pay_ezpay_url = "pay.ezpay.url";//url
        public static final String pay_ezpay_switch = "pay.ezpay.switch";//发票开关

        public static final String pay_H5NewebPay_key = "pay.H5NewebPay.key";//非官网蓝新支付 key
        public static final String pay_H5NewebPay_iv = "pay.H5NewebPay.iv";//非官网蓝新支付 iv
        public static final String pay_H5NewebPay_h5 = "pay.H5NewebPay.h5";//非官网蓝新支付 h5
        public static final String pay_H5NewebPay_MerchantID = "pay.H5NewebPay.MerchantID";//非官网蓝新支付 商户id
        public static final String pay_H5NewebPay_email = "pay.H5NewebPay.email";//非官网蓝新支付 email

        public static final String is_push_user_level = "is.push.user.level";//进入直播  推送im用户需等级
        public static final String is_push_room_num = "is.push.room.num";//当直播间人数到达多少 限制推送进入直播间

        public static final String payNow_webNo = "payNow.webNo";//payNow 賣家登入帳號
        public static final String payNow_receiverTel = "payNow.receiverTel";//消費者電話
        public static final String payNow_ECPlatform = "payNow.ECPlatform";//payNow 商店名称
        public static final String pay_type_official = "pay.type.official";//官网 支付方式
        public static final String pay_code = "pay.code";//payNow 交易码

        public static final String yiPay_webNo = "yiPay.webNo";//yipay 商店名称
        public static final String yiPay_key = "yiPay.key";//yipay
        public static final String yiPay_iv = "yiPay.iv";//yipay
        public static final String yiPay_notificationEmail = "yiPay.notificationEmail";//yipay 邮箱


        public static final String pay_new_NewebPay_key = "pay.new.NewebPay.key";//蓝新3支付 key
        public static final String pay_new_NewebPay_iv = "pay.new.NewebPay.iv";//蓝新3支付 iv
        public static final String pay_new_NewebPay_h5 = "pay.new.NewebPay.h5";//蓝新3支付 h5
        public static final String pay_new_NewebPay_MerchantID = "pay.new.NewebPay.MerchantID";//蓝新支付3 商户id
        public static final String pay_new_NewebPay_email = "pay.new.NewebPay.email";//蓝新支付3 email


        public static final String live_vip_user_switch = "live.vip.user.switch";//直播间是否开启用户展示
        public static final String live_vip_user_account = "live.vip.user.account";//直播间展示用户账号


        public static final String user_vip_exp_switch = "user.vip.exp.switch";//是否开启vip经验


        public static final String system_msg_name = "system.msg.name";//消息系统名称

        public static final String live_new_hour = "live.new.hour";//是否新人的直播时长
        public static final String send_gift_pd = "send.gift.pd";//送礼跑道飘屏
        public static final String send_gift_tt = "send.gift.tt";//送礼头条飘屏
        public static final String live_hot_sorts = "live.hot.sorts";//热榜 排序值


        public static final String hy_web_card = "hy.web.card";//hy 商店名称
        public static final String hy_web_atm = "hy.web.atm";//hy
        public static final String hy_store_web = "hy.store.web";//hy
        public static final String hy_password = "hy.password";//hy

        public static final String robot_config = "robot.config";   //机器人配置

        public static final String live_official_room_users = "live.official.room.users";   //可以开官方直播间的用户


        public static final String is_game_switch = "is.game.switch";//是否开启直播游戏

        public static final String is_send_gift_config = "is.send.gift.config";//直播间中奖配置


        public static final String payssion_api_key = "payssion.api.key";//payssion  api_key
        public static final String payssion_secret_key = "payssion.secret.key";//payssion  secret_key
        public static final String payssion_url = "payssion.get.data.url";//payssion 支付结果






    }


    /**
     * 公共的请求参数key
     */
    public class RequestParam {
        public static final String api_version = "api_version";
    }

    /**
     * 直播间
     */
    public class Room {
        public static final int show_user_max = 8; // 显示直播间内人员列表数量
    }

    public class AnchorType {
        public static final int normal = 0;
        public static final int hide = 1;
    }

    /**
     * 任务类型
     */
    public class TaskType {
        public static final int SHARE = 0;          // 0分享任务，
        public static final int WIN_GAME = 1;       // 1游戏任务，
        public static final int SEND_GIFT = 2;      // 2打赏任务,
        public static final int SIGN_IN = 3;        // 3每日签到任务
    }

    /**
     * 私信的消息类型，存数据库
     */
    public class MessageType {
        // 0私信(用户-用户)，1系统-全体用户，2系统-单个用户，3用户-系统
        public static final int u2u = 0;
        public static final int sys2all = 1;
        public static final int sys2u = 2;
        public static final int u2sys = 3;
    }

    /**
     * 直播间，消息类型
     */
    public class RoomMsgType {

        public static final int NORMAL = 0;//正常发言
        public static final int WARNING_SYS = 1;//系统警告
        public static final int WARNING_USER = 2;//用户警告
        public static final int ZAN = 3;//点赞
        public static final int FOLLOW = 4;//关注
        public static final int SEND_GIFT = 5;//送礼
        public static final int AT = 6;// @TA
    }


    /**
     * 每日任务ID
     */
    public class TaskId {

        public static final int buy_emo = 420;//充值
        public static final int send = 421;//送礼
    }


    /**
     * IM字段对应关系
     */
    public class ImField {
        public static final String win_money_position = "WD";
        public static final String bet_number = "bet_number";
        public static final String account              = "account";
        public static final String age			        = "age";
        public static final String all_bet_number	    = "all_bet_number";
        public static final String anchor_tickets	    = "anchor_tickets";
        public static final String anchor_name		    = "anchor_name";
        public static final String audience_num		    = "audience_num";
        public static final String authorization	    = "authorization";
        public static final String auth_state		    = "auth_state";

        public static final String bet_info		        = "bet_info";
        public static final String business_type	    = "business_type";
        public static final String background	        = "background";

        public static final String chatMsgStatus	    = "chatMsgStatus";
        public static final String color		        = "color";
        public static final String countdown_time	    = "countdown_time";
        public static final String contribution_top3	= "contribution_top";
        public static final String count_num		    = "count_num";


        public static final String download_url		    = "download_url";
        public static final String doll_game		    = "doll_game";

        public static final String effect		        = "effect";

        public static final String forbid		        = "forbid";
        public static final String role	                = "role";
        public static final String game_id		        = "game_id";
        public static final String game_info		    = "game_info";
        public static final String game_type		    = "game_type";
        public static final String get_money		    = "get_money";
        public static final String gift_name		    = "gift_name";
        public static final String gift_image		    = "gift_image";
        public static final String gift_img		        = "gift_img";
        public static final String gift_info		    = "gift_info";
        public static final String gift_id		        = "gift_id";
        public static final String game_state		    = "game_state";

        public static final String head_url		        = "head_url";

        public static final String id			        = "id";
        public static final String im_code		        = "im_code";
        public static final String im_msg		        = "im_msg";
        public static final String is_show		        = "is_show";
        public static final String is_barrage		    = "is_barrage";
        public static final String is_receive		    = "is_receive";
        public static final String is_horizontal		= "is_horizontal";

        public static final String level		        = "level";
        public static final String live_id		        = "live_id";
        public static final String live_type		    = "live_type";
        public static final String live_stream_id	    = "live_stream_id";
        public static final String live_rtmp_url	    = "live_rtmp_url";
        public static final String live_flv_url		    = "live_flv_url";
        public static final String link_live_stream_id	= "link_live_stream";
        public static final String list	                = "list";

        public static final String my_diamonds		    = "my_diamonds";
        public static final String msg			        = "msg";
        public static final String message		        = "message";
        public static final String message_id		    = "message_id";
        public static final String my_bet_number		= "my_bet_number";

        public static final String name			        = "name";
        public static final String nickname		        = "nickname";
        public static final String number		        = "number";

        public static final String people_list		    = "people_list";
        public static final String pic_head_low		    = "pic_head_low";
        public static final String pic_url		        = "pic_url";
        public static final String poker_info		    = "poker_info";
        public static final String poker_type		    = "poker_type";
        public static final String position_id		    = "position_id";
        public static final String pre_pokers		    = "pre_pokers";
        public static final String price		        = "price";
        public static final String pic_head_high        = "pic_head_high";

        public static final String receive_account	    = "receive_account";
        public static final String radix	            = "radix";

        public static final String send_time		    = "send_time";
        public static final String sex			        = "sex";
        public static final String source_type		    = "source_type";
        public static final String state		        = "state";
        public static final String spare		        = "spare";
        public static final String send_tickets		    = "send_tickets";
        public static final String star_wars		    = "star_wars";

        public static final String task_finish_num	    = "task_finish_num";
        public static final String task_all_num		    = "task_all_num";
        public static final String tickets		        = "tickets";
        public static final String time			        = "time";
        public static final String type			        = "type";
        public static final String total_time		    = "total_time";
        public static final String tag		            = "tag";
        public static final String title		        = "title";
        public static final String text		            = "text";
        public static final String to_account		    = "to_account";

        public static final String win_money		    = "win_money";
        public static final String win_id		        = "win_id";

    }

    public class RebPacketType {
        public static final int sys_gift = 0;
        public static final int user_send = 1;
    }

    public class GiftType {
        public static final int normal = 1;     // 小礼物
        public static final int big = 6;        // 大礼物
        public static final int rebPacket = 7;  // 红包礼物

    }

    public class RechargeType {
        public static final int wx = 0;         // 微信
        public static final int alipay = 1;     // 支付宝
        public static final int apple = 2;      // 苹果
        public static final int google = 3;     // google
        public static final int other = 4;     // other
        public static final int gashpay = 5;     // gash
        public static final int sca = 6;     // sca
        public static final int eth = 7;     // eth
        public static final int unknown = 8;     // unknown
        public static final int admin = 9;     // admin 后台
        public static final int ipay88 = 10;     // ipay88

    }

    // 登录方式  0：手机，1：微信，2：邮箱，3：Facebook，4：kakao, 5:google, 6：推特 7:QQ  8:微博 9：账号
    public static class LoginType {
        public static final int PHONE = 0;
        public static final int WECHAT = 1;
        public static final int EMAIL = 2;
        public static final int FACEBOOK = 3;
        public static final int KAKAO = 4;
        public static final int GOOGLE = 5;
        public static final int TWITTER = 6;
        public static final int QQ = 7;
        public static final int WEIBO = 8;
        public static final int ACCOUNT = 9;
        public static final int LINE = 11;
        public static final int APPLE = 13;


    }

    public class RoleType {
        public static final int manager = 1;         // 管理员
        public static final int agent = 2;           // 代理充值
        public static final int developer = 3;       // 开发者
        public static final int test = 4;            // 测试
        public static final int sys_banker = 10;      // 系统庄家
    }

    public class LiveType {
        //0：热门，1：关注，2：最新，3：附近，4：国家，5：Tag，6：推荐录播，7:用户录播记录，8：富豪直播, 9返回前端的录播，10：付费播；11：私密播
        public static final int HOT = 0;                        // 热门
        public static final int FOLLOW = 1;                     // 关注
        public static final int NEW = 2;                        // 最新
        public static final int NEAR = 3;                       // 附近
        public static final int COUNTRY = 4;                    // 国家
        public static final int TAG = 5;                        // Tag
        public static final int RECOMMENDED_RECORDING = 6;      // 推荐录播
        public static final int USER_RECORDING = 7;             // 用户录播记录
        public static final int RICH = 8;                       // 富豪直播
        public static final int RESULT_RECORDING = 9;           // 返回前端的录播
        public static final int PAY = 10;                       // 付费播
        public static final int PRIVATE = 11;                   // 私密播
        public static final int bos = 12;                   // 摄像头
        public static final int SHOP = 20;                   // 电商直播
        public static final int GAME = 30;                   // 游戏直播
        public static final int PROGRAM = 40;                   // 节目直播
        public static final int new_anchor = 50;                   // 新人直播
        public static final int hot_rank = 60;                   // 热榜直播

    }

    /**
     * 动态操作类型
     * 0文字评论，1点赞动态,2不喜欢,3分享,4：评论点赞 5点赞动态弹出挖矿分享
     */
    public class CommentType {

        public static final int COMMENT = 0;
        public static final int POST_LIKE = 1;
        public static final int POST_DISLIKE = 2;
        public static final int POST_SHARE = 3;
        public static final int COMMENT_LIKE = 4;
        public static final int POST_LIKE_SHARE = 5;

    }

    /**
     * 用户收支记录类型
     */
    public class UserBillType {

        public static final int COMMON = 0;  //
        public static final int POST_GIFT = 1;//动态礼物盒子奖励
        public static final int INVITE_POST = 2;//邀请他人发布动态
        public static final int BEINVITE_POST = 3;//被邀请发布动态
        public static final int GUIDE_POST = 4;//引导发布动态
        public static final int INVITE_SHARE = 5;//邀请分享奖励
        public static final int live_game = 6;//直播间游戏
    }


    /**
     * 用户收支记录类型
     */
    public class UserGainType {

        public static final int wx_pay = 0;  //微信
        public static final int apple_pay = 2;//苹果
        public static final int google_pay = 3;//谷歌
        public static final int game_pay = 6;//游戏收入
        public static final int live_win = 7;//直播间中奖
        public static final int task = 8;//任务奖励
        public static final int neweb_pay = 11;//蓝新
        public static final int paynow_pay = 13;//paynow
        public static final int YiPay_pay = 15;//YiPay
        public static final int HongYang_pay = 17;//红阳科技
        public static final int payssion_pay = 18;//payssion
    }


    /**
     * 推送通知类型
     */
    public class PushType {

        public static final int COMMON = 0;  //
        public static final int POST = 1;   // 评论点赞动态

    }



}
