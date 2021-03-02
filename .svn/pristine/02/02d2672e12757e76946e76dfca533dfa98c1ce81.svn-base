package com.weking.service.live;

import com.weking.cache.WKCache;
import com.weking.core.*;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.live.LiveGuardMapper;
import com.weking.mapper.log.LiveLogInfoMapper;
import com.weking.model.account.AccountInfo;
import com.weking.model.live.LiveGuard;
import com.weking.model.log.LiveLogInfo;
import com.weking.service.pay.PocketService;
import com.weking.service.system.MsgService;
import com.weking.service.user.FollowService;
import com.weking.service.user.LevelService;
import com.weking.service.user.UserService;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Xujm
 */
@Service("liveGuardService")
public class LiveGuardService extends LibServiceBase {

    @Resource
    private LiveGuardMapper liveGuardMapper;
    @Resource
    private PocketService pocketService;
    @Resource
    private UserService userService;
    @Resource
    private MsgService msgService;
    @Resource
    private FollowService followService;
    @Resource
    private LevelService levelService;
    @Resource
    private AccountInfoMapper accountInfoMapper;
    @Resource
    private LiveLogInfoMapper liveLogInfoMapper ;

    private static long time = 7 * 24 * 60 * 60 * 1000;


    /**
     * 购买
     */
    @Transactional
    public JSONObject buy(int userId, int liveId, int price, String langCode, String account) {
        int anchorId=0;
        int curGuardUserId=0;
        int liveIdTemp=0;
        int curPrice=0;
        String live_stream_id="";
        boolean flg=true;//为true  则推送Im
        if (liveId == 0 && !"0".equals(account)) {
            AccountInfo accountInfo = accountInfoMapper.selectByAccountId(account);
            if (accountInfo != null) {
                LiveLogInfo liveInfo = liveLogInfoMapper.findLiveLogInfoByUserId(accountInfo.getId());
                anchorId =accountInfo.getId();
                curGuardUserId = findLiveGuard(anchorId);
                if(liveInfo!=null){
                    if(liveInfo.getLiveEnd()!=0){
                        flg=false;
                    }else {
                        live_stream_id=liveInfo.getLive_stream_id();
                        liveIdTemp=liveInfo.getId();
                    }

                }
            } else {
                return LibSysUtils.getResultJSON(ResultCode.system_error, LibProperties.getLanguage(langCode, "weking.lang.app.data.error"));
            }
        }else if(liveId!=0){
            anchorId = LibSysUtils.toInt(WKCache.get_room(liveId, "user_id"));
            curGuardUserId = getLiveGuardId(liveId);
            live_stream_id=WKCache.get_room(liveId, "live_stream_id");
        }
        if (anchorId <= 0) {
            return LibSysUtils.getResultJSON(ResultCode.live_end, LibProperties.getLanguage(langCode, "weking.lang.app.live_end"));
        }
        if (userId == curGuardUserId) {
            return LibSysUtils.getResultJSON(ResultCode.guard_exist, LibProperties.getLanguage(langCode, "weking.lang.guard.exist"));
        }
        curPrice = getBuyGuardPrice(anchorId);
        //购买价格要大于当前价格
        if (curPrice > 0) {
            if (price <= curPrice) {
                return LibSysUtils.getResultJSON(ResultCode.guard_price_low, LibProperties.getLanguage(langCode, "weking.lang.guard.price.low"));
            }
        }
        //购买价格为0表示还没守护购买，价格必须要大于等于底价
        if (curPrice == 0) {
            if (price < LibSysUtils.toInt(WKCache.get_system_cache("live.guard.price"))) {
                return LibSysUtils.getResultJSON(ResultCode.guard_price_low, LibProperties.getLanguage(langCode, "weking.lang.guard.price.low"));
            }
        }
        JSONObject  object = pocketService.consume(userId, anchorId, price, 6, liveId, langCode);
        if (object.getInt("code") == ResultCode.success) {
            //将之前守护置为失效
            if (curGuardUserId > 0) {
                liveGuardMapper.updateStateByAnchorId(anchorId);
                //发送守护被抢通知
                Map<String, String> userMap = userService.getUserInfoByUserId(curGuardUserId, "account", "lang_code");
                String anchorName = WKCache.get_user(anchorId, "nickname");
                String msg = String.format(LibProperties.getLanguage(userMap.get("lang_code"), "weking.lang.guard.rob"), anchorName);
                msgService.sendSysMsg(userMap.get("account"), msg, userMap.get("lang_code"));
            }
            levelService.putExp(userId, price, 5,anchorId);
            long curTime = LibDateUtils.getLibDateTime();
            LiveGuard record = new LiveGuard();
            record.setAnchorId(anchorId);
            record.setUserId(userId);
            record.setPrice(price);
            record.setAddTime(curTime);
            liveGuardMapper.insert(record);
            Map<String, String> userMap = userService.getUserBaseInfoByUserId(userId);
            if (flg) {
                JSONObject imObject = new JSONObject();
                imObject.put("im_code", IMCode.buy_guard);
                imObject.put("account", userMap.get("account"));
                imObject.put("nickname", userMap.get("nickname"));
                imObject.put("pic_head_low", WkUtil.combineUrl(userMap.get("avatar"), UploadTypeEnum.AVATAR, true));
                imObject.put("price", price);
                imObject.put("send_time", curTime);
                if(liveIdTemp!=0) {
                    imObject.put("live_id", liveIdTemp);
                }else{
                    imObject.put("live_id", liveId);
                }
                WkImClient.sendRoomMsg(live_stream_id, imObject.toString(), 1);
            }
            WKCache.add_room(liveId, "guard_id", LibSysUtils.toString(userId));
            WKCache.add_room_rank(liveId, LibSysUtils.toDouble(price), userMap.get("account"));//加入到本场消费记录中
        }else {
            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(langCode, "weking.lang.info.nomoney"));
        }
        return object;
    }

    /**
     * 守护详情
     */
    public JSONObject info(int liveId, String langCode,String account) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        int anchorId=0;
        if(liveId!=0) {
            anchorId= LibSysUtils.toInt(WKCache.get_room(liveId, "user_id"));
        }else {
            AccountInfo accountInfo = accountInfoMapper.selectByAccountId(account);
            if (accountInfo != null) {
                anchorId=accountInfo.getId();
            }else {
                return LibSysUtils.getResultJSON(ResultCode.system_error, LibProperties.getLanguage(langCode, "weking.lang.app.data.error"));
            }
        }
        if (anchorId <= 0) {
            return LibSysUtils.getResultJSON(ResultCode.live_end, LibProperties.getLanguage(langCode, "weking.lang.app.live_end"));
        }
        LiveGuard liveGuard = getValidLiveGuard(anchorId);
        JSONObject obj = new JSONObject();
        if (liveGuard != null) {
            obj = getLiveGuardInfo(liveGuard.getUserId(), liveGuard.getPrice(), liveGuard.getAddTime());
        }
        List<LiveGuard> list = liveGuardMapper.selectInvalidList(anchorId);
        JSONArray jsonArray = new JSONArray();
        if (list != null) {
            long curTime = LibDateUtils.getLibDateTime();
            for (LiveGuard info : list) {
                JSONObject jsonObject = new JSONObject();
                Map<String, String> userMap = userService.getUserBaseInfoByUserId(info.getUserId());
                jsonObject.put("account", userMap.get("account"));
                jsonObject.put("nickname", userMap.get("nickname"));
                jsonObject.put("sex", LibSysUtils.toInt(userMap.get("sex")));
                jsonObject.put("pic_head_low", WkUtil.combineUrl(userMap.get("avatar"), UploadTypeEnum.AVATAR, true));
                jsonObject.put("history_time", WkUtil.format(LibDateUtils.getDateTimeTick(info.getAddTime(), curTime), langCode));
                jsonArray.add(jsonObject);
            }
        }
        object.put("guard_info", obj);
        object.put("guard_price", getBuyGuardPrice(liveGuard));
        object.put("default_price", getBuyGuardDefaultPrice());
        object.put("interval_price", getBuyGuardIntervalPrice());
        object.put("history_guard", jsonArray);
        return object;
    }

    public void checkCancelLiveGuard() {
        if (ResourceUtil.LiveGuardMap.isEmpty()) {
            List<LiveGuard> liveGuard = liveGuardMapper.selectLiveGuardList();
            for (LiveGuard info : liveGuard) {
                ResourceUtil.LiveGuardMap.put(info.getId(), info.getAddTime());
            }
        }
        Long curTime = LibDateUtils.getLibDateTime();
        Iterator<Map.Entry<Integer, Long>> it = ResourceUtil.LiveGuardMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Long> entry = it.next();
            if (LibDateUtils.getDateTimeTick(entry.getValue(), curTime) >= time) {
                cancelLiveGuard(entry.getKey());
                ResourceUtil.LiveGuardMap.remove(entry.getKey());
            }
        }
    }

    private void cancelLiveGuard(int guardId) {
        LiveGuard liveGuard = liveGuardMapper.findLiveGuardById(guardId);
        if (liveGuard != null) {
            if (LibDateUtils.getDateTimeTick(liveGuard.getAddTime(), LibDateUtils.getLibDateTime()) >= time) {
                cancelLiveGuard(guardId, liveGuard.getUserId(), liveGuard.getAnchorId());
            }
        }
    }

    @Transactional
    private void cancelLiveGuard(int guardId, int guardUserId, int anchorId) {
        int re = liveGuardMapper.updateStateById(guardId);
        if (re > 0) {
            Map<String, String> userMap = userService.getUserInfoByUserId(guardUserId, "account", "lang_code");
            String anchorName = userService.getUserInfoByUserId(anchorId, "nickname");
            String msg = String.format(LibProperties.getLanguage(userMap.get("lang_code"), "weking.lang.guard.expire"), anchorName);
            msgService.sendSysMsg(userMap.get("account"), msg, userMap.get("lang_code"));
            int liveId = LibSysUtils.toInt(WKCache.get_user(anchorId, "live_id"));
            if (liveId > 0) {
                WKCache.add_room(liveId, "guard_id", "0");
                JSONObject imObject = new JSONObject();
                imObject.put("im_code", IMCode.buy_guard);
                imObject.put("account", "");
                imObject.put("nickname", "");
                imObject.put("pic_head_low", "");
                imObject.put("price", 0);
                imObject.put("live_id", liveId);
                WkImClient.sendRoomMsg(WKCache.get_room(liveId, "live_stream_id"), imObject.toString(), 1);
            }
        }
    }

    private JSONObject getLiveGuardInfo(int guardUserId, int guardPrice, long addTime) {
        JSONObject obj = new JSONObject();
        Map<String, String> userMap = userService.getUserBaseInfoByUserId(guardUserId);
        obj.put("account", userMap.get("account"));
        obj.put("nickname", userMap.get("nickname"));
        obj.put("pic_head_low", WkUtil.combineUrl(userMap.get("avatar"), UploadTypeEnum.AVATAR, true));
        obj.put("level", userMap.get("level"));
        obj.put("price", guardPrice);
        obj.put("signature", userMap.get("signature"));
        obj.put("surplus", getSurplusTime(addTime));
        return obj;
    }

    /**
     * 获得当前购买守护价格
     */
    private int getBuyGuardPrice(int anchorId) {
        LiveGuard liveGuard = getValidLiveGuard(anchorId);
        if (liveGuard != null) {
            return liveGuard.getPrice();
        }
        return 0;
    }

    /**
     * 获得当前购买守护价格
     */
    private int getBuyGuardPrice(LiveGuard liveGuard) {
        if (liveGuard != null) {
            return liveGuard.getPrice();
        }
        return getBuyGuardDefaultPrice();
    }

    private int getBuyGuardDefaultPrice() {
        return LibSysUtils.toInt(WKCache.get_system_cache("live.guard.price"));
    }

    private int getBuyGuardIntervalPrice() {
        return LibSysUtils.toInt(WKCache.get_system_cache("guard.interval.price"));
    }

    public JSONObject getLiveGuardInfo(int liveId) {
        return getLiveGuardInfoByGuardUserId(getLiveGuardId(liveId));
    }

    public JSONObject getLiveGuardInfoByAnchorId(int anchorId) {
        return getLiveGuardInfoByGuardUserId(findLiveGuard(anchorId));
    }

    private JSONObject getLiveGuardInfoByGuardUserId(int liveGuardUserId) {
        if (liveGuardUserId > 0) {
            JSONObject obj = new JSONObject();
            Map<String, String> userMap = userService.getUserBaseInfoByUserId(liveGuardUserId);
            obj.put("account", userMap.get("account"));
            obj.put("nickname", userMap.get("nickname"));
            obj.put("pic_head_low", WkUtil.combineUrl(userMap.get("avatar"), UploadTypeEnum.AVATAR, true));
            return obj;
        }
        return null;
    }

    public int getLiveGuardId(int liveId) {
        String guardId = WKCache.get_room(liveId, "guard_id");
        if (guardId == null) {
            int liveGuardUserId = findLiveGuard(LibSysUtils.toInt(WKCache.get_room(liveId, "user_id")));
            WKCache.add_room(liveId, "guard_id", LibSysUtils.toString(liveGuardUserId));
            return liveGuardUserId;
        }
        return LibSysUtils.toInt(guardId);
    }

    /**
     * 获得守护排行
     */
    public JSONObject getGuardRand(int userId, int liveId) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        int anchorId = LibSysUtils.toInt(WKCache.get_room(liveId, "user_id"));
        JSONArray array = guardRandList(userId, anchorId);
        object.put("self", getSelfGuardRank(userId, anchorId, array));
        object.put("list", array);
        int guardUserId = findLiveGuard(anchorId);
        object.put("guard_info", getSelfRandInfo(guardUserId, anchorId));
        return object;
    }

    /**
     * 获得守护排行
     */
    public JSONObject getGuardRand(int userId, String anchorAccount) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        int anchorId = LibSysUtils.toInt(userService.getUserFieldByAccount(anchorAccount, "user_id"));
        JSONArray array = guardRandList(userId, anchorId);
        object.put("self", getSelfGuardRank(userId, anchorId, array));
        object.put("list", array);
        int guardUserId = findLiveGuard(anchorId);
        object.put("guard_info", getRankGuardInfo(userId, guardUserId, anchorId));
        return object;
    }

    public JSONObject getRankGuardInfo(int userId, int guardUserId, int anchorId) {
        JSONObject info = getSelfRandInfo(guardUserId, anchorId);
        if (userId != guardUserId) {
            if (info != null) {
                info.put("follow_state", followService.getFollowStatus(userId, guardUserId));
            }
        }
        return info;
    }

    private JSONObject getSelfGuardRank(int userId, int anchorId, JSONArray array) {
        int size = array.size();
        //获取最后一个对象，不为空代表该用户在榜上
        if (size > 0) {
            JSONObject jsonObject = (JSONObject) array.get(size - 1);
            array.remove(size - 1);
            if (jsonObject.size() > 0) {
                return jsonObject;
            }
        }
        return getSelfRandInfo(userId, anchorId);
    }

    private JSONArray guardRandList(int userId, int anchorId) {
        List<LiveGuard> list = liveGuardMapper.getLiveGuardRand(anchorId);
        int i = 0;
        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();
        JSONObject userObj = new JSONObject();
        for (LiveGuard info : list) {
            jsonObject = new JSONObject();
            jsonObject.put("nickname", info.getNickname());
            jsonObject.put("account", info.getAccount());
            jsonObject.put("avatar", WkUtil.combineUrl(info.getAvatar(), UploadTypeEnum.AVATAR, true));
            jsonObject.put("qty", info.getPrice());
            jsonObject.put("order", LibSysUtils.toString(++i));
            jsonObject.put("level", info.getLevel());
            if (userId == info.getUserId()) {
                userObj = jsonObject;
            }
            if (userId != info.getUserId()) {
                jsonObject.put("follow_state", followService.getFollowStatus(userId, info.getUserId()));
            }
            jsonArray.add(jsonObject);
        }
        jsonArray.add(userObj);
        return jsonArray;
    }

    private JSONObject getSelfRandInfo(int userId, int anchorId) {
        Map<String, String> userMap = userService.getUserInfoByUserId(userId, "account", "nickname", "level", "avatar");
        if (userId == 0) {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("nickname", userMap.get("nickname"));
        jsonObject.put("account", userMap.get("account"));
        jsonObject.put("avatar", WkUtil.combineUrl(userMap.get("avatar"), UploadTypeEnum.AVATAR, true));
        jsonObject.put("level", userMap.get("level"));
        jsonObject.put("qty", getUserAllGuardPrice(userId, anchorId));
        jsonObject.put("order", 0);
        return jsonObject;
    }

    private int getUserAllGuardPrice(int userId, int anchorId) {
        Integer allPrice = liveGuardMapper.getAllLiveGuardPrice(userId, anchorId);
        if (allPrice == null) {
            return 0;
        }
        return allPrice;
    }

    /**
     * 获得直播间守护用户ID
     */
    private int findLiveGuard(int anchorId) {
        LiveGuard liveGuard = getValidLiveGuard(anchorId);
        if (liveGuard != null) {
            return liveGuard.getUserId();
        }
        return 0;
    }

    /**
     * 获得有效直播间守护
     */
    private LiveGuard getValidLiveGuard(int anchorId) {
        LiveGuard liveGuard = liveGuardMapper.findLiveGuardByAnchorId(anchorId);
        if (liveGuard != null) {
            if (LibDateUtils.getDateTimeTick(liveGuard.getAddTime(), LibDateUtils.getLibDateTime()) < time) {
                return liveGuard;
            } else {
                cancelLiveGuard(liveGuard.getId(), liveGuard.getUserId(), liveGuard.getAnchorId());
            }
        }
        return null;
    }

    private long getSurplusTime(long buyTime) {
        return (time - LibDateUtils.getDateTimeTick(buyTime, LibDateUtils.getLibDateTime())) / 1000;
    }

}
