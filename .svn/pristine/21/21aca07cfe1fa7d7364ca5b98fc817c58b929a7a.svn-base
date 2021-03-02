package com.weking.core;

import com.weking.cache.WKCache;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.game.GameUtil;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;

/**
 * Created by zhb on 2017/9/12.
 * 工具类
 */
public class IMPushUtil {
    private static final int BG_GIFT = 0;       // 礼物-粉红
    private static final int BG_ZJH = 1;        // 炸金花-大红
    private static final int BG_NN = 2;         // 牛牛-宝蓝
    private static final int BG_TTDZ = 3;       // 天天德州-咖啡
    private static final int BG_DOLL = 4;       // 夹娃娃-浅紫
    private static final int BG_START_WAR = 5;  // 星际大战-浅黄

   /* private static  int MIN_PD_GIFT ;
    private static  int MIN_TT_GIFT ;*/
    private static int MIN_PD_ZJH = 10000;
    private static int MIN_TT_ZJH = 100000;
    private static int MIN_PD_NN = 10000;
    private static int MIN_TT_NN = 100000;
    private static int MIN_PD_TTDZ = 10000;
    private static int MIN_TT_TTDZ = 100000;
    private static int MIN_PD_DOLL = 1000;
    private static int MIN_TT_DOLL = 50000;
    private static int MIN_PD_START_WAR = 25;  //倍数
    private static int MIN_TT_START_WAR = 50; //倍数




    //{"giftPd":5000,"giftTt":50000,"zjhPd":10000,"zjhTt":100000,"nnPd":10000,"nnTt":100000,"ttdzPd":10000,"ttdzTt":100000,"dollPd":1000,"dollTt":50000,"starwarPd":6,"starwarTt":50}

    private static JSONObject getBaseJson(int liveId, String streamId, String anchorHeadPic, int role) {
        int liveType = LibSysUtils.toInt(WKCache.get_room(liveId, C.WKCacheRoomField.live_type));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(C.ImField.live_id,liveId);

        if(liveType == C.LiveType.RESULT_RECORDING) {
            jsonObject.put(C.ImField.live_flv_url, WKCache.get_room(liveId, C.WKCacheRoomField.replay_url));
        }else{
            jsonObject.put(C.ImField.live_flv_url, streamId);
        }

//        jsonObject.put(C.ImField.live_flv_url, TencentUtil.getRtmpPlayUrl(streamId));
        jsonObject.put(C.ImField.role, role);
        jsonObject.put(C.ImField.pic_head_high, anchorHeadPic);
        return  jsonObject;
    }

    public static void sendGlobalMsgRp(int liveId, String liveStreamId,String anchorHeadPic,
                                       String nickname, String anchorName, int role) {

        JSONObject jsonObject = getBaseJson(liveId, liveStreamId, anchorHeadPic, role);

        jsonObject.put(C.ImField.im_code, IMCode.global_tt);
        //
        String html = String.format(LibProperties.getLanguage("weking.lang.rp.global.msg"),
                WkUtil.getShortName(nickname),
                 WkUtil.getShortName(anchorName));
        jsonObject.put(C.ImField.text, html);

        jsonObject.put(C.ImField.background, BG_GIFT);
        WkImClient.sendGlobalMsg(jsonObject.toString());
    }

    //12 10号  改为可配置
    public static void sendGlobalMsgGift(int liveId, String liveStreamId, String nickname,
                                         String giftName, String anchorName,
                                         String giftPic, int giftPrice, String anchorHeadPic, int role,int MIN_PD_GIFT,int MIN_TT_GIFT) {
        if (giftPrice < MIN_PD_GIFT) {
            return;
        }
        JSONObject jsonObject = getBaseJson(liveId, liveStreamId, anchorHeadPic, role);

        if (giftPrice >= MIN_TT_GIFT) {
            // 头条
            jsonObject.put(C.ImField.im_code, IMCode.global_tt);
            //
            String html = String.format(LibProperties.getLanguage("weking.lang.gift.gb.tt.msg"), WkUtil.getShortName(nickname),
                     WkUtil.combineUrl(giftPic,UploadTypeEnum.AVATAR,false), WkUtil.getShortName(anchorName));
            jsonObject.put(C.ImField.text, html);
        } else {
            // 跑道
            jsonObject.put(C.ImField.im_code, IMCode.global_pd);
            // 手气碉堡啦！XX在XX房间玩敲三家赢了XX！
            String html = String.format(LibProperties.getLanguage("weking.lang.gift.gb.pd.msg"), nickname,
                    WkUtil.combineUrl(giftPic,UploadTypeEnum.AVATAR,false), anchorName);
            jsonObject.put(C.ImField.text, html);
        }

        jsonObject.put(C.ImField.background, BG_GIFT);
        jsonObject.put("account",WKCache.get_room(liveId,"account"));
        WkImClient.sendGlobalMsg(jsonObject.toString());
    }

    public static void sendGlobalMsgCard(int liveId, String liveStreamId,int winMoney, String nickname,
                                         String anchorName, int gameType, String anchorHeadPic,int role) {
        int minPDValue; // 跑道最小值
        int minTTValue; // 头条最小值
        int bt;
        if (gameType == GameUtil.FRIED_GOLDEN) {
            //炸金花
            minPDValue = MIN_PD_ZJH;
            minTTValue = MIN_TT_ZJH;
            bt = BG_ZJH;
        } else if (gameType == GameUtil.TEXAS_HOLDEM) {
            //德州扑克
            minPDValue = MIN_PD_TTDZ;
            minTTValue = MIN_TT_TTDZ;
            bt = BG_TTDZ;
        } else {
            //牛牛
            minPDValue = MIN_PD_NN;
            minTTValue = MIN_TT_NN;
            bt = BG_NN;
        }

        if (winMoney < minPDValue) {
            return;
        }

        JSONObject jsonObject = getBaseJson(liveId, liveStreamId, anchorHeadPic, role);
        if (winMoney >= minTTValue) {
            // 头条
            jsonObject.put(C.ImField.im_code, IMCode.global_tt);
            // XX（玩家昵称）人品大爆炸！在XX（主播昵称）房间玩敲三家赢了XX万U钻！
            String html = String.format(LibProperties.getLanguage("weking.lang.win.gb.tt.msg"), WkUtil.getShortName(nickname),
                    WkUtil.getShortName(anchorName), winMoney);
            jsonObject.put(C.ImField.text, html);
        } else {
            // 跑道
            jsonObject.put(C.ImField.im_code, IMCode.global_pd);
            // 手气碉堡啦！XX在XX房间玩敲三家赢了XX！
            String html = String.format(LibProperties.getLanguage("weking.lang.win.gb.pd.msg"), nickname,
                    anchorName, winMoney);
            jsonObject.put(C.ImField.text, html);
        }

        jsonObject.put(C.ImField.background, bt);
        TimerUtil.sendGlobalMsgDelay(jsonObject.toString(), 1);
    }


    /**
     * 跑道：
     * 1、100U钻场夹中XX礼物（1000以上）
     * 2、1000U钻场夹中XX礼物10000-50000（不含）
     * 头条：
     * 1、1000U钻场夹中XX礼物（50000以上）
     * @param liveId
     * @param liveStreamId
     * @param price
     * @param dollName
     * @param nickname
     * @param anchorName
     * @param anchorHeadPic
     * @param role
     */
    public static void sendGlobalMsgDoll(int capital, int liveId, String liveStreamId,int price, String dollName,
                                         String nickname, String anchorName, String anchorHeadPic, int role) {
        if(capital < 100) {
            // 100场以下不飘
            return;
        }
        if (price < MIN_PD_DOLL) {
            return;
        }

        // 100场
        if(capital == 100 && price >= MIN_PD_DOLL) {
            // 1、100U钻场夹中XX礼物（1000以上）
            // 跑道
            dollPD(liveId,liveStreamId,dollName,nickname,anchorName,anchorHeadPic,role);

            return;
        }

        // 1000场
        if(capital == 1000 ) {
            if( price>=10000 && price< 50000) {
               // 2、1000U钻场夹中XX礼物10000-50000（不含）
                // 跑道
                dollPD(liveId,liveStreamId,dollName,nickname,anchorName,anchorHeadPic,role);

                return;
            }
            if(price >= 50000) {
                JSONObject jsonObject = getBaseJson(liveId, liveStreamId, anchorHeadPic, role);
                // 头条
                jsonObject.put(C.ImField.im_code, IMCode.global_tt);

                String html = String.format(LibProperties.getLanguage("weking.lang.doll.gb.tt.msg"), WkUtil.getShortName(nickname),
                        WkUtil.getShortName(anchorName), dollName);
                jsonObject.put(C.ImField.text, html);

                jsonObject.put(C.ImField.background, BG_DOLL);

                TimerUtil.sendGlobalMsgDelay(jsonObject.toString(), 4);
            }
        }

    }


    private static void dollPD(int liveId, String liveStreamId,String dollName,
                        String nickname, String anchorName, String anchorHeadPic, int role) {

        JSONObject jsonObject = getBaseJson(liveId, liveStreamId, anchorHeadPic, role);

        jsonObject.put(C.ImField.im_code, IMCode.global_pd);

        String html = String.format(LibProperties.getLanguage("weking.lang.doll.gb.pd.msg"), nickname,
                anchorName, dollName);
        jsonObject.put(C.ImField.text, html);

        jsonObject.put(C.ImField.background, BG_DOLL);

        TimerUtil.sendGlobalMsgDelay(jsonObject.toString(), 4);
    }

    public static void sendGlobalMsgStarWar(int liveId, String liveStreamId,float beiShu,
                                            String nickname, String anchorName, String npcName,
                                            String anchorHeadPic, int role) {
        if (beiShu < MIN_PD_START_WAR) {
            return;
        }

        JSONObject jsonObject = getBaseJson(liveId, liveStreamId, anchorHeadPic, role);
        if (beiShu >= MIN_TT_START_WAR) {
            // 头条
            jsonObject.put(C.ImField.im_code, IMCode.global_tt);

            String html = String.format(LibProperties.getLanguage("weking.lang.starwar.gb.tt.msg"), WkUtil.getShortName(nickname),
                    WkUtil.getShortName(anchorName), beiShu, npcName);
            jsonObject.put(C.ImField.text, html);
        } else {
            // 跑道
            jsonObject.put(C.ImField.im_code, IMCode.global_pd);

            String html = String.format(LibProperties.getLanguage("weking.lang.starwar.gb.pd.msg"), nickname,
                    anchorName, beiShu, npcName);
            jsonObject.put(C.ImField.text, html);
        }

        jsonObject.put(C.ImField.background, BG_START_WAR);

        WkImClient.sendGlobalMsg(jsonObject.toString());
    }

}
