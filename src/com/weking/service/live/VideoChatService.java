package com.weking.service.live;

import com.weking.cache.WKCache;
import com.weking.core.*;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.mapper.blacklog.BlackLogMapper;
import com.weking.mapper.follow.FollowInfoMapper;
import com.weking.mapper.live.UserChatMapper;
import com.weking.mapper.live.VideoChatMapper;
import com.weking.mapper.pocket.GiftInfoMapper;
import com.weking.mapper.pocket.PocketInfoMapper;
import com.weking.model.live.UserChat;
import com.weking.model.live.VideoChat;
import com.weking.model.pocket.GiftInfo;
import com.weking.service.pay.PocketService;
import com.weking.service.user.UserService;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 视频聊天实现类
 *
 * @author Xujm
 */
@Service("videoChatService")
public class VideoChatService {

    private static Logger logger = Logger.getLogger(WkImClient.class);

    @Resource
    private PocketService pocketService;
    @Resource
    private UserService userService;
    @Resource
    private VideoChatMapper videoChatMapper;
    @Resource
    private GiftInfoMapper giftInfoMapper;
    @Resource
    private FollowInfoMapper followInfoMapper;
    @Resource
    private PocketInfoMapper pocketInfoMapper;
    @Resource
    private UserChatMapper userChatMapper;
    @Resource
    private BlackLogMapper blackLogMapper;

    /**
     * 申请聊天
     */
    public JSONObject apply(int userId, String account, int type, String userStream, String langCode) {
        JSONObject object;
        int otherId = LibSysUtils.toInt(userService.getUserFieldByAccount(account, "user_id"));
        if (isDisturb(otherId)) {
            return LibSysUtils.getResultJSON(ResultCode.user_is_disturb, LibProperties.getLanguage(langCode, "user.is.disturb"));
        }
        if (blackLogMapper.selectUserRelation(otherId, userId) > 0) {
            return LibSysUtils.getResultJSON(ResultCode.account_black_error, LibProperties.getLanguage(langCode, "weking.lang.account.black.error"));
        }
        //检测对方用户是否在线
        int userImState = WKCache.getImState(account);
//        if(userImState == 2){
//            return LibSysUtils.getResultJSON(ResultCode.user_offline, LibProperties.getLanguage(langCode,"weking.lang.user.offline"));
//        }
        //检测用户是否正在视频聊天
        if (WKCache.checkVideoChatUser(otherId)) {
            if (userImState == 1) {
                return LibSysUtils.getResultJSON(ResultCode.user_video_chatting, LibProperties.getLanguage(langCode, "weking.lang.user.video.chatting"));
            }
        }
        int liveId = LibSysUtils.toInt(WKCache.get_user(otherId, C.WKCacheUserField.live_id));
        if (liveId > 0) {
            return LibSysUtils.getResultJSON(ResultCode.user_is_living, LibProperties.getLanguage(langCode, "user.is.living"));
        }
        int costPrice = LibSysUtils.toInt(WKCache.get_system_cache("video.spend.price"));
        if (pocketService.getUserBalance(userId) < LibSysUtils.toInt(WKCache.get_system_cache("video.spend.price"))) {
            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(langCode, "weking.lang.app.live_not_sufficient_funds"));
        }
        int chatRoomId = recordVideoChat(userId, otherId, type, costPrice, userStream);
        if (chatRoomId == 0) {
            return LibSysUtils.getResultJSON(ResultCode.user_video_chatting, LibProperties.getLanguage(langCode, "weking.lang.user.video.chatting"));
        }

        WKCache.addVideoChatUser(userId, otherId);
        setVideoChatFeeTime(userId, chatRoomId);
        JSONObject imObject = new JSONObject();
        imObject.put(C.ImField.im_code, IMCode.video_chat_apply);
        imObject.put("msg", String.format(LibProperties.getLanguage(langCode, "user.online.apply"), WKCache.get_user(userId, "nickname")));
        imObject.put("room_id", chatRoomId);
        imObject.put("stream_id", userStream);
        Map<String, String> otherUserMap = userService.getUserBaseInfoByUserId(userId);
        imObject.put("account", otherUserMap.get("account"));
        imObject.put("nickname", otherUserMap.get("nickname"));
        imObject.put("pic_head_high", WkUtil.combineUrl(otherUserMap.get("avatar"), UploadTypeEnum.AVATAR, false));
        boolean isFollow = followInfoMapper.verifyIsFollowed(otherId, userId) > 0;
        imObject.put("follow_state", isFollow ? 1 : 0);
        //imObject.put("pic_head_high",otherUserMap.get("pichead_url"));
        imObject.put("time", LibDateUtils.getLibDateTime());
        imObject.put("receive_account", account);
        if (userImState == 2) {
            imObject.put("message", String.format(LibProperties.getLanguage(langCode, "user.online.apply"), WKCache.get_user(userId, "nickname")));
            PushMsg.pushSingleMsg(userService.getUserInfoByUserId(otherId, "c_id"), imObject);
        } else {
            WkImClient.sendPrivateMsg(account, imObject.toString(), true);
        }
        object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("room_id", chatRoomId);
        return object;
    }

    private void setVideoChatFeeTime(int userId, int roomId) {
        int chatTime = LibSysUtils.toInt(WKCache.get_system_cache("video.chat.time"));
        WKCache.setVideoChatConsumeTime(userId, roomId,
                System.currentTimeMillis() - (chatTime - 5) * 1000);
    }

    /**
     * 接受视频聊天
     */
    public JSONObject accept(int userId, int roomId, boolean accept, String otherStream, String langCode) {
        VideoChat videoChat = videoChatMapper.findVideoChatById(roomId);
        if (videoChat == null || videoChat.getOtherId() != userId || videoChat.getEndTime() != 0) {
            return LibSysUtils.getResultJSON(ResultCode.video_chat_error, LibProperties.getLanguage(langCode, "video.chat.error"));
        }
        int otherId = videoChat.getUserId();
        String otherAccount = userService.getUserInfoByUserId(otherId, "account");
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONObject imObject = new JSONObject();
        if (accept) {
            Map<String, String> userMap = userService.getUserBaseInfoByUserId(userId);
            String account = userMap.get("account");
            imObject.put("im_code", IMCode.video_chat_agree);
            imObject.put("stream_id", otherStream);
            imObject.put("account", account);
            imObject.put("room_id", roomId);
            imObject.put("nickname", userMap.get("nickname"));
            imObject.put("pic_head_high", WkUtil.combineUrl(userMap.get("avatar"), UploadTypeEnum.AVATAR, false));
            boolean isFollow = followInfoMapper.verifyIsFollowed(otherId, userId) > 0;
            imObject.put("follow_state", isFollow ? 1 : 0);
            joinRoom(LibSysUtils.toString(roomId), account, otherAccount);
        } else {
            WKCache.delVideoChatUser(userId, otherId);
            imObject.put("im_code", IMCode.video_chat_un_agree);
            imObject.put("room_id", roomId);
            imObject.put("nickname", userService.getUserInfoByUserId(userId, "nickname"));
            imObject.put("msg", LibProperties.getLanguage(langCode, "video.chat.un.agree"));
        }
        imObject.put("time", LibDateUtils.getLibDateTime());
        imObject.put("receive_account", otherAccount);
        WkImClient.sendPrivateMsg(otherAccount, imObject.toString(), true);
        return object;
    }

    /**
     * 结束视频聊天
     */
    @Transactional(rollbackFor = Exception.class)
    public JSONObject end(int userId, int roomId, int endType) {
        int otherId = 0;
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        if (roomId == 0) {
            return object;
        }
        VideoChat info = videoChatMapper.findVideoChatById(roomId);
        if (info != null) {
            if (userId == info.getUserId()) {
                otherId = info.getOtherId();
            }
            if (userId == info.getOtherId()) {
                otherId = info.getUserId();
            }
            if (info.getEndTime() == 0 && otherId != 0) {
                long endTime = LibDateUtils.getLibDateTime();
                long diffTime = LibDateUtils.getDateTimeTick(info.getStartTime(), endTime);
                int re = videoChatMapper.updateEndTimeById(roomId, endTime, diffTime);
                if (re > 0) {
                    //发送结束信息
                    JSONObject imObject = new JSONObject();
                    imObject.put("im_code", IMCode.video_chat_end);
                    imObject.put("room_id", roomId);
                    String account = userService.getUserInfoByUserId(info.getUserId(), "account");
                    String otherAccount = userService.getUserInfoByUserId(info.getOtherId(), "account");
                    //更新用户聊天状态
                    WKCache.delVideoChatUser(userId, otherId);
                    //发送结束消息
                    WkImClient.sendPrivateMsg(account, imObject.toString(), null);
                    WkImClient.sendPrivateMsg(otherAccount, imObject.toString(), null);
                    WkImClient.delRoom(LibSysUtils.toString(roomId), otherAccount, 3);

                }
            }
        }
        logger.info(String.format("endVideoChat@userId:%d=roomId:%d=endType:%d", userId, roomId, endType));
        WKCache.delVideoChatConsumeTime(userId, roomId);
        return object;
    }

    /**
     * 将两用户创建IM房间
     */
    private void joinRoom(String roomId, String account, String otherAccount) {
        WkImClient.createRoomAndJoin(roomId, account, otherAccount, 5);
    }

    /**
     * 记录视频聊天信息
     */
    private int recordVideoChat(int userId, int otherId, int type, int costPrice, String userStream) {
        VideoChat record = new VideoChat();
        record.setUserId(userId);
        record.setOtherId(otherId);
        record.setType((byte) type);
        record.setCostPrice(costPrice);
        record.setStartTime(LibDateUtils.getLibDateTime());
        record.setUserStream(userStream);
        int re = videoChatMapper.insert(record);
        if (re > 0) {
            re = record.getId();
        }
        return re;
    }

    /**
     * 获得聊天时间
     */
    public JSONObject getChatTime(int userId, int roomId, String langCode) {

        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        VideoChat info = videoChatMapper.findVideoChatById(roomId);
        if (info == null) {
            return object;
        }
        int chatTime = LibSysUtils.toInt(WKCache.get_system_cache("video.chat.time"));
        long topTime = WKCache.getVideoChatConsumeTime(userId, roomId);
        long systemTime = System.currentTimeMillis();
        if (topTime != 0 && systemTime - topTime < (chatTime - 5) * 1000) {
            return object;
        }
        object = pocketService.consume(userId, info.getOtherId(), 0, 3, roomId, langCode);
        if (object.getInt("code") != ResultCode.success) {
            return object;
        }
        JSONObject imObject = new JSONObject();
        imObject.put("im_code", IMCode.video_chat_time);
        imObject.put("room_id", roomId);
        imObject.put("chat_time", chatTime);
        WkImClient.sendPrivateMsg(userService.getUserInfoByUserId(info.getUserId(), "account"), imObject.toString());
        WkImClient.sendPrivateMsg(userService.getUserInfoByUserId(info.getOtherId(), "account"), imObject.toString());
        WKCache.setVideoChatConsumeTime(userId, roomId, systemTime);
        object.put("my_diamonds", pocketInfoMapper.getSenderLeftDiamondbyid(userId));
        return object;
    }

    /**
     * 发送视频聊天礼物
     */
    public JSONObject sendVideoChatGift(int userId, int roomId, int giftId, String langCode) {
        VideoChat info = videoChatMapper.findVideoChatById(roomId);
        if (info == null) {
            return LibSysUtils.getResultJSON(ResultCode.success);
        }
        GiftInfo giftInfo = giftInfoMapper.selectByPrimaryKey(giftId);
        if (giftInfo != null) {
            JSONObject object = pocketService.consume(userId, info.getOtherId(), giftInfo.getPrice(), giftId, roomId, langCode);
            if (object.optInt("code") != ResultCode.success) {
                return object;
            }
            JSONObject imObject = new JSONObject();
            imObject.put("im_code", IMCode.video_chat_gift);
            imObject.put("gift_id", giftId);
            imObject.put("send_time", LibDateUtils.getLibDateTime());
            imObject.put("type", giftInfo.getType());
            imObject.put("gift_img", giftInfo.getGift_image());
            imObject.put("gift_name", giftInfo.getName());
            WkImClient.sendPrivateMsg(userService.getUserInfoByUserId(info.getUserId(), "account"), imObject.toString());
            WkImClient.sendPrivateMsg(userService.getUserInfoByUserId(info.getOtherId(), "account"), imObject.toString());
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("my_diamonds", pocketInfoMapper.getSenderLeftDiamondbyid(userId));
        return object;
    }

    /**
     * 修改聊天设置
     *
     * @param userId
     * @param chatPrice 聊天价格
     * @param isDisturb 是否勿扰
     */
    public JSONObject editChatSystem(int userId, int chatPrice, Boolean isDisturb, String langCode) {
        UserChat record = new UserChat();
        record.setId(userId);
        if (chatPrice > 0) {
            record.setChatPrice(chatPrice);
        }
        if (isDisturb != null) {
            record.setIsDisturb(isDisturb);
        }
        int re = userChatMapper.updateByPrimaryKeySelective(record);
        if (re == 0) {
            try {
                record.setAddTime(LibDateUtils.getLibDateTime());
                userChatMapper.insertSelective(record);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 获得聊天价格
     */
    public int getUserChatPrice(int userId) {
        int costPrice = 0;
        String price = WKCache.get_user(userId, "chat_price");
        if (price != null) {
            UserChat userChat = userChatMapper.findUserChatInfo(userId);
            if (userChat != null) {
                costPrice = userChat.getChatPrice();
                WKCache.add_user(userId, "chat_price", LibSysUtils.toString(costPrice));
            }
        }
        if (costPrice == 0) {
            costPrice = LibSysUtils.toInt(WKCache.get_system_cache("video.spend.price"));
        }
        return costPrice;
    }


    public boolean isDisturb(int userId) {
        UserChat userChat = userChatMapper.findUserChatInfo(userId);
        if (userChat != null && userChat.getIsDisturb()) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("file.separator"));
    }

}
