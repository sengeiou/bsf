package com.weking.service.system;

import com.weking.cache.RoomCacheInfo;
import com.weking.cache.WKCache;
import com.weking.core.*;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.core.sensitive.WordFilter;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.blacklog.BlackLogMapper;
import com.weking.mapper.chathistory.ChatHistoryMapper;
import com.weking.mapper.chathistory.ChatUserMapper;
import com.weking.mapper.follow.FollowInfoMapper;
import com.weking.model.account.AccountInfo;
import com.weking.model.chathistory.ChatHistory;
import com.weking.model.chathistory.ChatUser;
import com.weking.service.live.LiveService;
import com.weking.service.user.UserService;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 消息
 */
@Service("msgService")
public class MsgService extends LibServiceBase {

    @Resource
    private AccountInfoMapper accountInfoMapper;
    @Resource
    private BlackLogMapper blackLogMapper;
    @Resource
    private ChatHistoryMapper chatHistoryMapper;
    @Resource
    private ChatUserMapper chatUserMapper;
    @Resource
    private FollowInfoMapper followInfoMapper;
    @Resource
    private UserService userService;
    @Resource
    private LiveService liveService;

    /**
     * 系统发送消息给所有用户
     *
     * @param msg  消息内容
     * @param type 类型 0系统消息 1通知推送
     * @return json
     */
    public JSONObject sendMsgToApp(String msg, String title, String link_url, String pic_url, int type, String project_name) {

        String lang_code = LibProperties.getConfig("weking.config.default_lang");
        JSONObject im_data = getImDataJson(0, lang_code, "all", msg);
        if (type == 1) {
            im_data.put(C.ImField.im_code, IMCode.sys_notice_push);
        }
        if (!LibSysUtils.isNullOrEmpty(title)) {
            im_data.put("title", title);
        }
        if (!LibSysUtils.isNullOrEmpty(link_url)) {
            im_data.put("link_url", link_url);
        }
        if (!LibSysUtils.isNullOrEmpty(pic_url)) {
            im_data.put("pic_url", pic_url);
        }
//        System.out.println(im_data.toString());
        boolean success = PushMsg.sendSystemMsg(im_data, msg, lang_code, project_name, null);
        recordChatLog(C.SystemUserId, C.SystemUserId, msg, C.MessageType.sys2all, success ? 2 : 1, im_data.getString("message_id"), im_data.optLong("time"));
        JSONObject result;
        if (success) {
            result = LibSysUtils.getResultJSON(ResultCode.success);
        } else {
            result = LibSysUtils.getResultJSON(ResultCode.system_error, LibProperties.getLanguage(lang_code, "weking.lang.app.send.error"));
        }
        return result;
    }

    /**
     * 发送消息给所有用户
     */
    public void sendMsgToAllUser(JSONObject msgObject, String msg, String title, String tag) {
        PushMsg.sendMsgToAllUser(msgObject, msg, title, tag);
    }

    /**
     * 系统发送消息给所有用户
     *
     * @param msg
     * @param title
     * @return
     */
    public JSONObject sendMsgToApp(String msg, String title, int im_code) {
        String lang_code = LibProperties.getConfig("weking.config.default_lang");
        JSONObject im_data = getImDataJson(0, lang_code, "all", msg);
        im_data.put("title", title);
        if (im_code != 0) {
            im_data.put("im_code", im_code);
        }
        boolean success = PushMsg.sendSystemMsg(im_data, msg, lang_code, "", null);
        recordChatLog(C.SystemUserId, C.SystemUserId, msg, C.MessageType.sys2all, success ? 2 : 1, im_data.getString("message_id"), im_data.optLong("time"));
        JSONObject result;
        if (success) {
            result = LibSysUtils.getResultJSON(ResultCode.success);
        } else {
            result = LibSysUtils.getResultJSON(ResultCode.system_error, LibProperties.getLanguage(lang_code, "weking.lang.app.send.error"));
        }
        return result;
    }

    /**
     * 发送系统消息
     *
     * @param toAccount 接收者account
     * @param msg       信息
     * @param type      类型 0系统消息 1通知推送
     * @return json
     */
    public JSONObject sendSysMsg(String toAccount, String msg, String title, String link_url, String pic_url, int type) {
        String langCode = userService.getUserFieldByAccount(toAccount, "lang_code");
        if (LibSysUtils.isNullOrEmpty(langCode)) {
            langCode = LibProperties.getConfig("weking.config.default_lang");
        }
        JSONObject im_data = getImDataJson(0, langCode, toAccount, msg);
        if (type == 1) {
            im_data.put(C.ImField.im_code, IMCode.sys_notice_push);
        }
        if (!LibSysUtils.isNullOrEmpty(title)) {
            im_data.put("title", title);
        }
        if (!LibSysUtils.isNullOrEmpty(link_url)) {
            im_data.put("link_url", link_url);
        }
        if (!LibSysUtils.isNullOrEmpty(pic_url)) {
            im_data.put("pic_url", pic_url);
        }
//        System.out.println(im_data.toString());
        return sendMsg(C.SystemUserId, im_data.optString("nickname"), 99, toAccount, im_data.toString(), true, langCode);

    }

    public JSONObject sendSysMsg(String toAccount, String msg, String langCode) {
        if (LibSysUtils.isNullOrEmpty(langCode)) {
            langCode = LibProperties.getConfig("weking.config.default_lang");
        }
        JSONObject im_data = getImDataJson(0, langCode, toAccount, msg);
        return sendMsg(C.SystemUserId, im_data.optString("nickname"), 99, toAccount, im_data.toString(), true, langCode);
    }

    public JSONObject sendSysMsg(int push_type, int extend_id, String toAccount, String msg, String langCode) {
        if (LibSysUtils.isNullOrEmpty(langCode)) {
            langCode = LibProperties.getConfig("weking.config.default_lang");
        }
        JSONObject im_data = getImDataJson(0, langCode, toAccount, msg);
        im_data.put("push_type", push_type);
        im_data.put("extend_id", extend_id);
//        System.out.println(im_data.toString());
        return sendMsg(C.SystemUserId, im_data.optString("nickname"), 99, toAccount, im_data.toString(), true, langCode);
    }

    /**
     * 发送系统消息
     *
     * @param userId    发送者
     * @param toAccount 接收者account
     * @param msg       信息
     * @param langCode  接收者语言
     * @return json
     */
    public JSONObject sendMsg(int userId, String toAccount, String msg, String langCode) {
        JSONObject im_data = getImDataJson(userId, langCode, toAccount, msg);
        return sendMsg(userId, im_data.optString("nickname"), 99, toAccount, im_data.toString(), false, langCode);
    }

    private JSONObject getImDataJson(int userId, String lang_code, String toAccount, String msg) {

        String senderNickname;
        JSONObject system_msg_name = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.system_msg_name));
        if (system_msg_name.size()!=0) {
            senderNickname = system_msg_name.optString(lang_code, "系统消息");
        }else {
            senderNickname = LibProperties.getLanguage(lang_code, "weking.lang.app.system.msg.name");
        }
        //String senderNickname = LibProperties.getLanguage(lang_code, "weking.lang.app.system.msg.name");
        JSONObject im_data = new JSONObject();
        if (userId == 0) {
            im_data.put("account", "0");
            im_data.put("head_url", WkUtil.combineUrl("system_avatar.png", UploadTypeEnum.AVATAR, true));
            im_data.put("nickname", senderNickname);
            im_data.put("sex", 0);
        } else {
            Map<String, String> userMap = userService.getUserInfoByUserId(userId, "account", "nickname", "avatar", "sex");
            im_data.put("account", userMap.get("account"));
            im_data.put("head_url", WkUtil.combineUrl(userMap.get("avatar"), UploadTypeEnum.AVATAR, true));
            im_data.put("nickname", userMap.get("nickname"));
            im_data.put("sex", userMap.get("sex"));
        }
        im_data.put("age", 0);
        im_data.put("auth_state", 1);
        im_data.put("business_type", 0);
        im_data.put("chatMsgStatus", 70);
        im_data.put("im_code", IMCode.send_chat);
        im_data.put("message", msg);
        im_data.put("message_id", java.util.UUID.randomUUID().toString());
        im_data.put("receive_account", toAccount);
        im_data.put("source_type", 1);
        im_data.put("time", LibDateUtils.getLibDateTime());
        return im_data;
    }

    /**
     * 发送消息
     */
    public JSONObject sendMsg(int userId, String nickname, int level, String account, String im_data, boolean isCache, String lang_code) {
        JSONObject object;
        if ("0".equals(account)) {  //用户给系统发送消息
            long time = LibDateUtils.getLibDateTime();
            JSONObject imObject = JSONObject.fromObject(im_data);
            String message = imObject.getString("message");
            recordChatLog(userId, C.SystemUserId, message, C.MessageType.u2sys, 1, imObject.getString("message_id"), time);
            object = LibSysUtils.getResultJSON(ResultCode.success);
            object.put("time", time);
            object.put("is_blacker", false);
        } else {
            int msg_level = SystemConstant.msg_level;
            AccountInfo accountInfo = accountInfoMapper.selectByAccountId(account);
            if (accountInfo != null) {
                if (level >= msg_level || accountInfo.getLevel() >= msg_level) {
                    long time = LibDateUtils.getLibDateTime();
                    //验证用户是否被对方拉黑
                    if (blackLogMapper.selectUserRelation(accountInfo.getId(), userId) > 0) {
                        object = LibSysUtils.getResultJSON(ResultCode.account_black_error, LibProperties.getLanguage(lang_code, "weking.lang.account.black.error"));
                        object.put("time", time);
                        object.put("is_blacker", true);
                    } else {
                        int status = 2;
                        int msgType = C.MessageType.u2u;
                        JSONObject imObject = JSONObject.fromObject(im_data);
                        String message = imObject.getString("message");
                        imObject.put("message", WordFilter.doFilter(message));
                        String cid = accountInfo.getClientid();
                        if (!LibSysUtils.isNullOrEmpty(cid)) {
//                            imObject.put("im_code", IMCode.send_chat);
                            imObject.put("time", time);

                            int isFollow;
                            if (userId == 0) { // 系统消息
                                isFollow = 1;
                                msgType = C.MessageType.sys2u;
                            } else {
                                isFollow = followInfoMapper.verifyIsFollowed(accountInfo.getId(), userId) > 0 ? 1 : 0;
                            }
                            imObject.put("auth_state", isFollow);
                            imObject.put("pic_head_low", WkUtil.combineUrl(WKCache.get_user(userId, "avatar"), UploadTypeEnum.AVATAR, true));
                            boolean flag = PushMsg.pushSingleMsg(cid, imObject);
                            if (!flag) {
                                status = 1;
                            }
                        }
                        recordChatUser(userId, accountInfo.getId(), message, time);
                        if (isCache) {
                            recordChatLog(userId, accountInfo.getId(), message, msgType, status, imObject.getString("message_id"), time);
                        }
                        object = LibSysUtils.getResultJSON(ResultCode.success);
                        object.put("time", time);
                        object.put("is_blacker", false);
                    }
                } else {
                    String msg = LibProperties.getLanguage(lang_code, "weking.lang.app.msg_level");
                    object = LibSysUtils.getResultJSON(ResultCode.live_islinking, msg.replace("%level%", LibSysUtils.toString(msg_level)));
                }
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.account.exist_error"));
            }

        }
        return object;
    }

    /**
     * 重发离线私信消息
     */
    public void againSendMsg(int userId, String account, String cid) {
        List<ChatHistory> chatList = chatHistoryMapper.selectOfficeChatList(userId);
        if (chatList.size() > 0) {
            JSONObject jsonObject = new JSONObject();
            for (ChatHistory chatInfo : chatList) {
                jsonObject.put("account", chatInfo.getAccount());
                jsonObject.put("nickname", chatInfo.getNickname());
                jsonObject.put("head_url", WkUtil.combineUrl(chatInfo.getPicheadUrl(), UploadTypeEnum.AVATAR, false));
                jsonObject.put("pic_head_low", WkUtil.combineUrl(chatInfo.getPicheadUrl(), UploadTypeEnum.AVATAR, true));
                jsonObject.put("time", chatInfo.getChatTime());
                jsonObject.put("message_id", chatInfo.getMessageId());
                jsonObject.put("message", chatInfo.getChatContent());
                jsonObject.put("receive_account", Integer.parseInt(account));
                jsonObject.put("age", 0);
                jsonObject.put("business_type", 0);
                jsonObject.put("sex", 0);
                jsonObject.put("state", 0);
                jsonObject.put("spare", 2);
                jsonObject.put("im_code", IMCode.send_chat);
                boolean flag = PushMsg.pushSingleMsg(cid, jsonObject);
                if (flag) {  //发送成功更新状态
                    chatHistoryMapper.updateStatic(chatInfo.getId());
                }
            }
        }
    }

    //用户聊天列表
    public JSONObject chatList(int userId, int type, String lang_code) {
        List<ChatUser> list;
        JSONArray jsonArray = new JSONArray();
        switch (type) {
            case 0:
                list = chatUserMapper.selectNotFollowListByUserId(userId);
                break;
            case 1:
                jsonArray.add(getSystemMsg(userId, lang_code));
                list = chatUserMapper.selectFollowListByUserId(userId);
                break;
            default:
                list = new ArrayList<>();
                break;
        }
        JSONObject jsonObject;
        for (ChatUser chatUser : list) {
            jsonObject = new JSONObject();
            jsonObject.put("nickname", chatUser.getNickname());
            jsonObject.put("account", chatUser.getAccount());
            jsonObject.put("sex", chatUser.getSex());
            jsonObject.put("auth_state", type);
            jsonObject.put("pic_head_low", WkUtil.combineUrl(chatUser.getPicheadUrl(), UploadTypeEnum.AVATAR, true));
            jsonObject.put("read_state", chatUser.getState() == 1 ? 3 : 4);
            jsonObject.put("message", chatUser.getChatContent());
            jsonObject.put("time", chatUser.getChatTime());
            jsonArray.add(jsonObject);
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", jsonArray);
        return object;
    }

    //删除聊天用户
    public JSONObject delChatUser(int userId, String account, String lang_code) {
        JSONObject object;
        AccountInfo accountInfo = accountInfoMapper.selectByAccountId(account);
        if (accountInfo != null) {
            int re = chatUserMapper.deleteByUserId(userId, accountInfo.getId());
            if (re > 0) {
                object = LibSysUtils.getResultJSON(ResultCode.success);
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.delete_error, LibProperties.getLanguage(lang_code, "weking.lang.delete.error"));
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.account.exist_error"));
        }
        return object;
    }

    /**
     * 获取用户一条系统消息
     */
    private JSONObject getSystemMsg(int userId, String lang_code) {
        JSONObject jsonObject = new JSONObject();
        ChatHistory chatInfo = chatHistoryMapper.findSystemMsg(userId);
        String senderNickname;
        JSONObject system_msg_name = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.system_msg_name));
        if (system_msg_name.size()!=0) {
             senderNickname = system_msg_name.optString(lang_code, "系统消息");
        }else {
           senderNickname = LibProperties.getLanguage(lang_code, "weking.lang.app.system.msg.name");
        }
        jsonObject.put("nickname", senderNickname);
        jsonObject.put("account", "0");
        jsonObject.put("sex", 1);
        jsonObject.put("auth_state", 1);
        jsonObject.put("pic_head_low", WkUtil.combineUrl("system.png", UploadTypeEnum.AVATAR, true));
        int read_state = 2;
        if (chatInfo != null) {
            if (chatInfo.getUserId() == 0 && chatInfo.getStatue() == 3) {
                read_state = 1;
            }
            jsonObject.put("read_state", read_state);
            jsonObject.put("message", chatInfo.getChatContent());
            jsonObject.put("time", chatInfo.getChatTime());
        } else {
            jsonObject.put("read_state", read_state);
            jsonObject.put("message", LibProperties.getLanguage(lang_code, "weking.lang.app.system.default.msg"));
            jsonObject.put("time", LibDateUtils.getLibDateTime());
        }
        return jsonObject;
    }

    //更新消息状态
    public JSONObject setChatStatic(int userId, String account, String message_id, String lang_code) {
        JSONObject object;
        AccountInfo accountInfo = accountInfoMapper.selectByAccountId(account);
        if (accountInfo != null) {
            chatUserMapper.updateMsgState(userId, accountInfo.getId());
            JSONObject sendMsg = new JSONObject();
            sendMsg.put("im_code", IMCode.update_state);
            sendMsg.put("message_id", message_id);
            //GeTuiUtil.pushMessageNoApn(sendMsg.toString(), accountInfo.getClientid()); //通知发送者对方已读
            PushMsg.pushNoApnMsg(accountInfo.getClientid(), sendMsg);
            object = LibSysUtils.getResultJSON(ResultCode.success);
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.account.exist_error"));
        }
        return object;
    }

    //更新系统消息状态
    public JSONObject updateMsgState(int userId, String message_id) {
        chatHistoryMapper.updateSystemState(userId, message_id);
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    //记录聊天信息
    private int recordChatLog(int userId, int recUserId, String msg, int msgType, int state, String msg_id, long time) {
        ChatHistory record = new ChatHistory();
        record.setChatContent(msg);
        record.setChatTime(time);
        record.setRecUserId(recUserId);
        record.setStatue(state);
        record.setMessageId(msg_id);
        record.setUserId(userId);
        record.setMsgType(msgType);
        return chatHistoryMapper.insert(record);
    }

    /**
     * 记录聊天用户
     *
     * @param userId  用户userid
     * @param otherId 对方otherid
     */
    private int recordChatUser(int userId, int otherId, String message, long time) {
        int re;
        List<ChatUser> list = chatUserMapper.selectById(userId, otherId);
        ChatUser chatUser = new ChatUser();
        chatUser.setChatContent(message);
        chatUser.setUserId(userId);
        chatUser.setOtherId(otherId);
        chatUser.setChatTime(time);
        int size = list.size();
        if (size == 0) { //两用户初次聊天
            re = chatUserMapper.insert(chatUser);
        } else if (size == 1) { //有用户存在对方聊天列表中
            ChatUser chatInfo = list.get(0);
            if (chatInfo.getUserId() == userId) {
                chatUserMapper.updateUserMsg(chatUser);
                re = chatUserMapper.insertOtherMsg(chatUser);
            } else {
                chatUserMapper.updateOtherMsg(chatUser);
                re = chatUserMapper.insertUserMsg(chatUser);
            }
        } else {
            re = chatUserMapper.update(chatUser);
        }
        return re;
    }

    /**
     * 通知所有人开播
     *
     * @param live_id
     * @return
     */
    public JSONObject liveNoticeAll(int live_id, String title, String msg,String channel) {
        RoomCacheInfo roomCacheInfo = WKCache.get_room(live_id);
        if (roomCacheInfo != null) {
            liveService.notifyFans(roomCacheInfo.getUser_id(), live_id, roomCacheInfo.getLive_stream_id(),
                    roomCacheInfo.getLive_type(), true, title, msg,channel);
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 電商商品通知推送
     *
     * @param goods_id
     * @param goods_commonid
     * @return
     */
    public JSONObject shopGoodsPush(int goods_id, int goods_commonid, String title, String msg) {
        String lang_code = LibProperties.getConfig("weking.config.default_lang");
        JSONObject im_data = getImDataJson(0, lang_code, "all", msg);
        im_data.put("im_code", IMCode.shop_good_push);
        im_data.put("title", title);
        im_data.put("goods_id", goods_id);
        im_data.put("goods_commonid", goods_commonid);
//        System.out.println(im_data.toString());
        boolean success = PushMsg.sendSystemMsg(im_data, msg, lang_code, "", null);

//        boolean success1 = PushMsg.pushSingleMsg("fYpguVe7Rzc:APA91bEucBj4LMSCH_wDEwRPCnHR2iBjIc4yVzX9iVu8dtBQj74NnN_PWIVoLdBQwi-TNNAE9MdXhHS2oo29qSQstrU9eJNz_U0De8dkNkUgYgC0M4HqsHJEFusBPLrDP9wYaEYtIYyE",im_data);
//        boolean success2 = PushMsg.pushSingleMsg("462318bdca8e379d683b5e75a5ee92bc",im_data);

        return LibSysUtils.getResultJSON(ResultCode.success);

    }


}
