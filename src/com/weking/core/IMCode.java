package com.weking.core;

/**
 * Created by Administrator on 2017/2/22.
 */
public class IMCode {
    public static int start_live = 1000;//开始直播
    public static int end_live = 1001;//结束直播
    public static int enter_room = 1002;//进入房间
    public static int exit_room = 1003;//退出房间
    public static int sys_msg = 1004;//系统消息
    public static int like = 1005;//点赞
    public static int send_msg = 1006;//发言
    public static int send_tip = 1007;//弹幕
    public static int send_gif = 1008;//送礼物
    public static int send_gif_win = 10081;//送礼物中奖
    public static int apply_link = 1009;//连麦申请
    public static int argee_link = 1010;//同意连麦
    public static int repulse_link = 1011;//拒绝连麦
    public static int start_link = 1012;//开始连麦
    public static int end_link = 1013;//结束连麦
    public static int to_background = 1014;//主播退到后台
    public static int to_front = 1015;//主播从后台回来

    public static int banned_post = 1016;//禁言
    public static int set_manager = 1017;//设置管理
    public static int force_end_live = 1018;//强制结束直播
    public static int start_live_pay = 1019;//付费播开始直播
    public static int black_user = 1021;//拉黑
    public static int out_room = 1022;//被踢出直播间
    public static int logout_force = 1300; // 强制退出
    public static int send_chat = 1100; //聊天消息
    public static int update_state = 1200; //更改消息状态

    public static int live_goods = 1300; //当前直播商品

    public static int live_announcement = 1400;//直播间内的公告

    public static int sys_notice_push = 1500;//（管理后台）发通知推送

    public static int global_pd = 2004;// 全服-跑道
    public static int global_tt = 2005;// 全服-头条

    public static final int game_bet = 4000; //下注
    public static final int bet_send = 4002; //下注推送
    public static final int game_data = 4003; //开牌数据
    public static final int bet_result = 4004; //下注结果
    public static final int game_switch = 4005; //切换游戏
    public static final int game_end = 4006; //结束游戏

    public static final int guessing_bet = 5007; //竞猜
    public static final int guessing_bet_send = 5008; //观众竞猜推送
    public static final int guessing_end = 5009; //竞猜结束



    public static int video_chat_apply = 6000; //视频聊天请求
    public static int video_chat_agree = 6001; //同意视频聊天
    public static int video_chat_un_agree = 6002; //拒绝视频聊天
    public static int video_chat_end = 6003; //视频聊天结束
    public static int video_chat_time = 6004; //视频聊天加时
    public static int video_chat_gift = 6005; //视频聊天发送礼物

    public static int buy_guard = 7000; //购买守护通知
    public static int shop_good_push = 7001; //电商商品推送
    public static int post_push = 7100; //发文推送


}
