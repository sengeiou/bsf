package com.weking.core;

/**
 * Created by Administrator on 2017/2/15.
 */
public class ResultCode {
    // ************************************************************************
    // ********* 请保持appsme、u8等直播项目的result code一致，后面做同步 *************
    // ************************************************************************
    public static int success = 0;//成功
    public static int system_error = -1;    //系统错误
    public static int token_invalidity = 1;    //token无效
    public static int must_upgrade = 2;//强制升级
    //0-999为系统预留
    public static int account_pwd_error = 1001;//密码有误
    public static int account_phone_error = 1002; //手机号有误
    public static int account_login_error = 1003; //账号或密码错误
    public static int account_login_type_error = 1004; //登录方式有误
    public static int account_isblack = 1005; //账号被封
    public static int account_pocket_error = 1006; //注册钱包信息错误
    public static int account_register_error = 1007; //注册失败
    public static int weixin_unionid_error = 1008; //微信unionid错误
    public static int account_email_error = 1009; //邮箱格式错误
    public static int account_login_lose = 1010; //登录失效
    public static int invite_code_error = 1011; //邀请码有误
    public static int account_passwords_differ = 1012; //密码不一致
    public static int operation_again_later = 1013; //稍后再试
    public static int send_captcha_over = 1014; //验证码发送超限
    public static int send_error = 1015; //发送失败
    public static int captcha_error = 1016; //验证码错误
    public static int captcha_expired = 1017; //验证码过期
    public static int account_email_exist = 1018; //邮箱已注册
    public static int modify_info_error = 1019; //修改失败
    public static int upload_image_error = 1020; //上传图片失败
    public static int account_not_exist = 1021; //账号不存在
    public static int account_black_exist = 1022; //已经拉黑
    public static int account_black_un = 1023; //未拉黑
    public static int recharge_not_exist = 1024; //充值种类不存在
    public static int payment_not_exist = 1025; //充值方式不存在
    public static int recharge_buy_error = 1026; //购买错误
    public static int order_not_exist = 1027; //支付号不存在
    public static int delete_error = 1028; //删除失败
    public static int bing_error = 1029; //绑定失败
    public static int account_black_error = 1030; //被对方拉黑
    public static int account_exist = 1031; //账号已经存在
    public static int report_error = 1032; //举报失败
    public static int report_exist = 1033; //已举报
    public static int feedback_error = 1034; //反馈失败
    public static int withdraw_exist = 1035; //存在一笔提现
    public static int cancel_bing_error = 1036; //解绑失败
    public static int account_login_same_user = 1037; //同一账号不同设备登录
    public static int account_phone_exist = 1038; //手机号已存在
    public static int userinfo_edit_often = 1039; //用户信息修改频繁
    public static int nickname_exist = 1040; //昵称已存在
    public static int login_over = 1044; //登录账号数量超限
    public static int register_over = 1041; //注册超限
    public static int login_redirect = 1042; //登录重定向
    public static int nickname_too_long = 1043; //昵称过长
    public static int nickname_edit_fee = 1044; //昵称修改扣费提醒
    public static int account_un_auth = 1045; //账号未审核
    public static int nickname_is_update = 1046; //昵称已存在

    public static int apple_token_error = 1047; //苹果token错误
    public static int out_room = 1048; //已被踢出直播间，无法再次进入

    public static int apple_pay_error = 1049; //支付号不存在



    // ************************************************************************
    // ********* 请保持appsme、u8等直播项目的result code一致，后面做同步 *************
    // ************************************************************************

    public static int live_uncertification = 2001;//未实名认证
    public static int live_sensitive = 2002;//敏感词
    public static int live_not_sufficient_funds = 2003;//余额不足
    public static int live_send_gift_error = 2004;//发送礼物失败
    public static int live_end = 2005;//直播已经结束
    public static int live_islinking = 2006;//正在连麦
    public static int live_pay = 2007;//提示购买门票


    public static int live_official = 2008;//官方直播间已存在
    public static int live_advance_error = 2009;//设置时长有误
    public static int live_time_error = 2010;//设置时间不可重复

    public static int fllow_unfllowme = 3001;//不可关注自己
    public static int black_unblackme = 3002;//不可拉黑自己
    public static int report_unreportme = 3003;//不可举报自己

    public static int game_bet_error = 4000; //下注失败

    public static int invite_code_is_myself = 4003; //邀请码不能是自己的
    public static int invite_code_set_error = 4004; //邀请人设置失败
    public static int game_bet_online = 4005; //下注到达上线
    public static int data_error = 4006; //数据有误
    public static int certification_error = 4007; //认证失败
    public static int card_num_error = 4008; //身份证有误
    public static int live_task_not_exist = 4009; //任务不存在
    public static int reward_coin_receive = 4010; //奖励已领取
    public static int receive_not_achieve = 4011; //未达到领取条件
    public static int receive_error = 4012; //领取失败
    public static int live_game_error_type = 4020;   //游戏类型错误
    public static int game_fire_error = 4021;   //抓娃娃失败

    public static int shop_store_error = 5000; //你还未开通店铺
    public static int goods_no_exist = 5001; //商品不存在或已下架
    public static int delivery_address_no_exist = 5002; //收货地址不存在
    public static int order_failure = 5003; //下单失败
    public static int pay_sn_no_exist = 5004; //支付单号已取消或不存在
    public static int order_no_exist = 5005; //订单不存在或已删除
    public static int insufficient_stock=5006; //库存不足
    public static int store_no_exist = 5007; //店铺不存在或已关闭

    public static int user_offline = 6000; //用户离线
    public static int user_video_chatting = 6001; //用户正在视频聊天
    public static int video_chat_error = 6002; //视频聊天已取消
    public static int user_is_living=6003; //用户正在直播
    public static int user_is_disturb=6004; //用户设置勿扰

    public static int invite_not_exist=7000; //邀约不存在
    public static int invite_appoint_exist=7001; //应约中
    public static int invite_appoint_refuse=7002; //拒绝邀约
    public static int invite_not_start=7003; //邀约还没有开始
    public static int invite_end=7004; //邀约结束
    public static int invite_edit_price=7005;//修改邀约价格存在待确认约会
    public static int invite_payment=7006; //邀约付款
    public static int invite_sex_non=7007; //性别不符合邀约

    public static int guard_price_low = 7100;//购买守护出价太低
    public static int guard_exist = 7101; //已经是守护

    public static int post_gift_off = 7200; //动态礼物盒子已经关闭
    public static int post_reward_num_over = 7201; //动态礼物盒子已经达到上限
    public static int post_frequently = 7202; //发文间隔时间太短
    public static int post_comment_repeated = 7203; //评论重复
    public static int post_lockout = 7204; //封锁发文
    public static int lockout_no_withdraw = 7205; //封锁期间不得提现
    public static int withdraw_over_times = 7206; //超过今日最大提现次数
    public static int withdraw_over_money = 7207; //超过今日最大提现金额
    public static int withdraw_sca_num = 7208; //小于最少提现数量
    public static int withdraw_close = 7209; //提现关闭

    public static int digital_wallet_insufficient = 7210; //余额不足
    public static int expiryTime_anchor_post = 7211; //仅主播可发限时动态

}
