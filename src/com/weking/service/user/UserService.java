package com.weking.service.user;

import com.weking.cache.RoomCacheInfo;
import com.weking.cache.UserCacheInfo;
import com.weking.cache.WKCache;
import com.weking.core.*;
import com.weking.core.enums.EditTypeEnum;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.core.enums.UserDataTypeEnum;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.account.UserDataMapper;
import com.weking.mapper.blacklog.BlackLogMapper;
import com.weking.mapper.certification.CertificationMapper;
import com.weking.mapper.follow.FollowInfoMapper;
import com.weking.mapper.log.EditLogMapper;
import com.weking.mapper.pocket.ContributionInfoMapper;
import com.weking.mapper.pocket.PocketInfoMapper;
import com.weking.mapper.vip.VipPrivilegeMapper;
import com.weking.model.account.AccountInfo;
import com.weking.model.account.UserData;
import com.weking.model.certification.Certification;
import com.weking.model.log.EditLog;
import com.weking.model.pocket.ContributionInfo;
import com.weking.model.pocket.PocketInfo;
import com.weking.model.vip.VipPrivilege;
import com.weking.service.digital.DigitalService;
import com.weking.service.live.LiveGuardService;
import com.weking.service.live.VideoChatService;
import com.weking.service.pay.PocketService;
import com.weking.service.shop.StoreService;
import com.weking.service.system.MsgService;
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
import java.util.*;

/**
 * 用户
 */
@Service("userService")
public class UserService extends LibServiceBase {
    private static Logger log = Logger.getLogger(UserService.class);
    @Resource
    private AccountInfoMapper accountMapper;
    @Resource
    private PocketInfoMapper pocketInfoMapper;
    @Resource
    private FollowInfoMapper followInfoMapper;
    @Resource
    private FollowService followService;
    @Resource
    private BlackLogMapper blackLogMapper;
    @Resource
    private ContributionInfoMapper contributionInfoMapper;
    @Resource
    private CertificationMapper certificationMapper;
    @Resource
    private MsgService msgService;
    @Resource
    private StoreService storeService;
    @Resource
    private EditLogMapper editLogMapper;
    @Resource
    private VideoChatService videoChatService;
    @Resource
    private PocketService pocketService;
    @Resource
    private UserDataMapper userDataMapper;
    @Resource
    private LiveGuardService liveGuardService;
    @Resource
    private DigitalService digitalService;
    @Resource
    private VipPrivilegeMapper vipPrivilegeMapper;




    /**
     * 获取主播列表
     */
    public JSONObject getAnchorList(int userId, int index, int count) {
        List<AccountInfo> list = accountMapper.selectAnchorList(userId, index, count);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        int costPrice = LibSysUtils.toInt(WKCache.get_system_cache("video.spend.price"));
        Integer state;
        for (AccountInfo accountInfo : list) {
            jsonObject = new JSONObject();
            jsonObject.put("nickname", accountInfo.getNickname());
            jsonObject.put("account", accountInfo.getAccount());
            jsonObject.put("pic_head_high", WkUtil.combineUrl(accountInfo.getPicheadUrl(), UploadTypeEnum.AVATAR, false));
            jsonObject.put("pic_head_low", WkUtil.combineUrl(accountInfo.getPicheadUrl(), UploadTypeEnum.AVATAR, true));
            jsonObject.put("chat_price", costPrice);
            String key = String.format("%d_%s",accountInfo.getId(),accountInfo.getAccount());
            state = ResourceUtil.userStateMap.get(key);
            if(state == null){
                state = getUserState(accountInfo.getId(), accountInfo.getAccount());
                ResourceUtil.userStateMap.put(key,state);
            }
            jsonObject.put("state", state);
            jsonObject.put("signature", accountInfo.getSigniture());
            jsonArray.add(jsonObject);
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", jsonArray);
        return object;
    }

    public int getUserState(int userId, String account) {
        return WKCache.getUserState(userId,account);
    }

    /**
     * 获取主播列表
     */
    public JSONObject getRecommendAnchorList(int userId, int index, int count) {
        List<AccountInfo> list = accountMapper.selectRecommendAnchorList(userId, index, count);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (AccountInfo accountInfo : list) {
            jsonObject = new JSONObject();
            jsonObject.put("account", accountInfo.getAccount());
            jsonObject.put("nickname", accountInfo.getNickname());
            jsonObject.put("sex", accountInfo.getSex());
            jsonObject.put("pic_head_high", WkUtil.combineUrl(accountInfo.getPicheadUrl(), UploadTypeEnum.AVATAR, false));
            jsonObject.put("pic_head_low", WkUtil.combineUrl(accountInfo.getPicheadUrl(), UploadTypeEnum.AVATAR, true));
            jsonObject.put("signature", accountInfo.getSigniture());
            int follow_state = followInfoMapper.verifyIsFollowed(userId, accountInfo.getId());
            jsonObject.put("follow_state", follow_state);
            jsonObject.put("level", accountInfo.getLevel());
            jsonArray.add(jsonObject);
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", jsonArray);
        return object;
    }

    /**
     * 修改用户信息
     *
     * @param userId     用户ID
     * @param jsonObject 用户修改信息数据
     */
    @Transactional
    public JSONObject  modify(int userId, JSONObject jsonObject) {
        Map<String, String> cacheInfoMap = new HashMap<>();
        String langCode = WKCache.get_user(userId, "lang_code");
        JSONObject object;
        AccountInfo accountInfo = new AccountInfo();
        String sex = jsonObject.optString("sex");
        if (!LibSysUtils.isNullOrEmpty(sex)) {
            accountInfo.setSex((short) LibSysUtils.toInt(sex));
            cacheInfoMap.put("sex", sex);
        }
        String nickname = jsonObject.optString("nickname");
        if (!LibSysUtils.isNullOrEmpty(nickname)) {
            if (accountMapper.findUserIdByNickname(nickname) != null) {
                return LibSysUtils.getResultJSON(ResultCode.nickname_exist, LibProperties.getLanguage("weking.lang.app.nickname.exist"));
            }
            AccountInfo info = accountMapper.selectByPrimaryKey(userId);
            if(info!=null){
                if(info.getIs_update_nickname()==1){
                    return LibSysUtils.getResultJSON(ResultCode.nickname_is_update, LibProperties.getLanguage("weking.lang.app.nickname.update"));
                }
            }

            boolean isBuy = LibSysUtils.toBoolean(jsonObject.optString("is_buy"));
            JSONObject obj = isCanEdit(userId, EditTypeEnum.NICKNAME, isBuy, langCode);
            if (obj.getInt("code") != ResultCode.success) {
                return obj;
            }
            accountInfo.setNickname(nickname);
            accountInfo.setIs_update_nickname((byte)1);//改成功后  设置成不可再次修改
            cacheInfoMap.put("nickname", nickname);
        }
        String address = jsonObject.optString("address");
        if (!LibSysUtils.isNullOrEmpty(address)) {
            accountInfo.setAddress(address);
            cacheInfoMap.put("address", address);
        }
        String birthday = jsonObject.optString("birthday");
        if (!LibSysUtils.isNullOrEmpty(birthday)) {
            accountInfo.setBirthday(LibSysUtils.toInt(birthday));
            cacheInfoMap.put("birthday", birthday);
        }
        String signature = jsonObject.optString("signature");
        if (!LibSysUtils.isNullOrEmpty(signature)) {
            accountInfo.setSigniture(signature);
            cacheInfoMap.put("signature", signature);
        }
        String lng = jsonObject.optString("lng");
        String lat = jsonObject.optString("lat");
        if (!LibSysUtils.isNullOrEmpty(lng) && !LibSysUtils.isNullOrEmpty(lat)) {
            accountInfo.setLng(LibSysUtils.toDouble(lng));
            accountInfo.setLat(LibSysUtils.toDouble(lat));
            cacheInfoMap.put("lng", lng);
            cacheInfoMap.put("lat", lat);
        }
        String lang_code = jsonObject.optString("lang_code");
        if (!LibSysUtils.isNullOrEmpty(lang_code)) {
            accountInfo.setLangCode(lang_code);
            cacheInfoMap.put("lang_code", lang_code);
            // TODO 取消订阅主题和标签
//            Map<String, String> userMap = getUserInfoByUserId(userId,"c_id","lang_code");
//            if (!LibSysUtils.isNullOrEmpty(userMap.get("c_id")) && userMap.get("c_id").length() != 32){ //安卓
//                Firebase.unsubscribeFromTopic(userMap.get("c_id"),userMap.get("lang_code"));
//                Firebase.subscribeToTopic(userMap.get("c_id"),lang_code);
//            }
//            if (!LibSysUtils.isNullOrEmpty(userMap.get("c_id")) && userMap.get("c_id").length() == 32){ // iOS 个推
//
//            }
        }
        String wallet_currency = jsonObject.optString("wallet_currency");
        if (!LibSysUtils.isNullOrEmpty(wallet_currency)) {
            accountInfo.setWallet_currency(wallet_currency);
            cacheInfoMap.put("wallet_currency", wallet_currency);
        }

        String user_name = jsonObject.optString("user_name");
        if (!LibSysUtils.isNullOrEmpty(user_name)) {
            accountInfo.setUser_name(user_name);
            cacheInfoMap.put("user_name", user_name);
        }
        String user_email = jsonObject.optString("user_email");
        if (!LibSysUtils.isNullOrEmpty(user_email)) {
            accountInfo.setUser_email(user_email);
            cacheInfoMap.put("user_email", user_email);
        }

        String phone = jsonObject.optString("phone");
        if (!LibSysUtils.isNullOrEmpty(phone)) {
            accountInfo.setPhone(phone);
            cacheInfoMap.put("phone", phone);
        }


        String picheadUrl = jsonObject.optString("pichead_url");
        if (!LibSysUtils.isNullOrEmpty(picheadUrl)) {
            boolean isBuy = jsonObject.optBoolean("is_buy", false);
            JSONObject obj = isCanEdit(userId, EditTypeEnum.AVATAR, isBuy, langCode);
            if (obj.getInt("code") != ResultCode.success) {
                return obj;
            }
            accountInfo.setPicheadUrl(picheadUrl);
            cacheInfoMap.put("avatar", picheadUrl);
        }
        accountInfo.setId(userId);
        boolean flag = modifyUserInfo(accountInfo);
        if (flag) {
            modifyUserCache(userId, cacheInfoMap);
            object = LibSysUtils.getResultJSON(ResultCode.success);
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.modify_info_error, LibProperties.getLanguage(langCode, "weking.lang.account.edit.error"));
        }
        return object;
    }

    /**
     * 是否可以修改该字段
     */
    private JSONObject isCanEdit(int userId, EditTypeEnum editTypeEnum, boolean isBuy, String langCode) {
        switch (editTypeEnum.getType()) {
            case 0:
                break;
            case 1:
                int costPrice = getEditNicknamePrice(userId);
                if (costPrice > 0) {
                    if (!isBuy) {
                        return LibSysUtils.getResultJSON(ResultCode.nickname_edit_fee,
                                String.format(LibProperties.getLanguage(langCode, "nickname.edit.fee"), costPrice));
                    }
                    JSONObject object = pocketService.consume(userId, 0, getEditNicknamePrice(userId), 4, 0, langCode);
                    if (object.getInt("code") != 0) {
                        return object;
                    }
                }
                break;
            case 2:
                int n = editTypeEnum.getEditTime();
                if (n > 0) {
                    Integer editId = editLogMapper.findIdByUserId(userId, editTypeEnum.getEditKey(), WkUtil.getPastTime(Calendar.DATE, -n));
                    if (editId != null) {
                        return LibSysUtils.getResultJSON(ResultCode.userinfo_edit_often,
                                String.format(LibProperties.getLanguage(langCode, "weking.lang.app.userinfo.edit"), n));
                    }
                }
                break;
            default:
                break;
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 更新用户缓存
     */
    private void modifyUserCache(int userId, Map<String, String> cacheInfoMap) {
        for (Map.Entry<String, String> entry : cacheInfoMap.entrySet()) {
            if (EditTypeEnum.getEnum(entry.getKey()) != null) {
                recordEditLog(userId, entry.getKey(), entry.getValue());
            }
            WKCache.add_user(userId, entry.getKey(), entry.getValue());
        }
    }

    private void recordEditLog(int userId, String editKey, String editValue) {
        EditLog record = new EditLog();
        record.setUserId(userId);
        record.setEditKey(editKey);
        record.setEditValue(editValue);
        record.setAddTime(LibDateUtils.getLibDateTime());
        editLogMapper.insert(record);
    }

    /**
     * 修改头像
     */
    public JSONObject saveAvatar(int userid, String fileName, String lang_code) {
        JSONObject object;
        AccountInfo record = new AccountInfo();
        record.setId(userid);
        record.setPicheadUrl(fileName); //存放的时候只存文件名称
        if (modifyUserInfo(record)) {
            object = LibSysUtils.getResultJSON(ResultCode.success);
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.upload_image_error, LibProperties.getLanguage(lang_code, "weking.lang.app.upload.error"));
        }
        return object;
    }


    /**
     * @param userId  查询者的uerid
     * @param account 被查询者的account
     * @return
     */
    public JSONObject userInfo(int userId, String account) {
        UserCacheInfo userCacheInfo = WKCache.get_user(userId);
        AccountInfo accountInfo = accountMapper.selectByAccountId(account);
        JSONObject object;
        if (accountInfo != null) {
            //获取贡献榜前三名头像
            int beUserId = accountInfo.getId();
            List<ContributionInfo> ainfos = contributionInfoMapper.selectContributionUserList(beUserId, 0, 3);
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject;
            for (ContributionInfo user : ainfos) {
                jsonObject = new JSONObject();
                jsonObject.put("nickname", user.getNickname());
                jsonObject.put("pic_head_low", WkUtil.combineUrl(user.getPicheadUrl(), UploadTypeEnum.AVATAR, false));
                jsonObject.put("pic_head_high", WkUtil.combineUrl(user.getPicheadUrl(), UploadTypeEnum.AVATAR, false));
                jsonArray.add(jsonObject);
            }
            int follow_state = 0;
            boolean black_state = false;
            int myDiamonds = 0; //余额
            long allDiamonds = 0; //总
            long get_diamonds = 0; //得到货币
            long send_diamonds = 0;
            int editNicknamePrice = 0; //改名费用
            if (accountInfo.getId() != userId) { //如果不是自己
                follow_state = followService.followStatus(userId, beUserId);
                black_state = blackLogMapper.selectUserRelation(userId, beUserId) > 0;
            } else {
                editNicknamePrice = getEditNicknamePrice(userId);
            }
            PocketInfo pinfo = pocketInfoMapper.selectByUserid(beUserId); //查询钱包信息
            if (pinfo != null) {
                myDiamonds = pinfo.getTotalDiamond();
                get_diamonds = pinfo.getTotalTicket();
                send_diamonds = pinfo.getAll_diamond() - pinfo.getTotalDiamond();
                allDiamonds=pinfo.getAll_diamond();
            }
            Map<String, Integer> map = followInfoMapper.getStarsFansNum(beUserId); //通过userid获取 关注和粉丝人数
            object = LibSysUtils.getResultJSON(ResultCode.success);
            object.put("account", accountInfo.getAccount());
            object.put("edit_nickname_price", editNicknamePrice);
            object.put("nickname", accountInfo.getNickname());
            object.put("anchor_level", accountInfo.getAnchor_level());
            object.put("sex", accountInfo.getSex());
            object.put("signature", accountInfo.getSigniture());
            object.put("pic_head_high", WkUtil.combineUrl(accountInfo.getPicheadUrl(), UploadTypeEnum.AVATAR, false));
            object.put("pic_head_low", WkUtil.combineUrl(accountInfo.getPicheadUrl(), UploadTypeEnum.AVATAR, true));
            object.put("address", accountInfo.getAddress());
            object.put("birthday", accountInfo.getBirthday());
            object.put("follow", map.get("stars"));
            object.put("fans", map.get("fans"));
            object.put("contribution_top3", jsonArray);
            object.put("follow_state", follow_state);
            object.put("black_state", black_state);
            object.put("my_diamonds", myDiamonds);//余额
            object.put("all_diamonds", allDiamonds);//总额
            object.put("get_diamonds", get_diamonds);
            object.put("send_diamonds", send_diamonds);//送出的钻石
            object.put("level", accountInfo.getLevel());
            if(userCacheInfo != null){
                object.put("login_type", userCacheInfo.getLogin_type());
            }else{
                object.put("login_type", 0);
            }
            object.put("parent_id", accountInfo.getParentId());
            object.put("chat_price", videoChatService.getUserChatPrice(beUserId));
            object.put("photo_list", getUserDataArray(beUserId, UserDataTypeEnum.PHOTO));
            object.put("guard_info", liveGuardService.getLiveGuardInfoByAnchorId(beUserId));
            int userImState = getUserState(accountInfo.getId(), accountInfo.getAccount());
            if (userImState == 4) {
                int liveId = LibSysUtils.toInt(WKCache.get_user(accountInfo.getId(), C.WKCacheUserField.live_id));
                RoomCacheInfo roomCacheInfo = WKCache.get_room(liveId);
                if (roomCacheInfo != null) {
                    object.put("live_id", liveId);
                    object.put("live_stream_id", roomCacheInfo.getLive_stream_id());



                    if (accountInfo!=null&&accountInfo.getCdn_option()==1){
                        //台湾Cdn
                        object.put("live_rtmp_url", "");
                        object.put("live_flv_url", TencentUtil.getFlvTWPlayUrl(roomCacheInfo.getLive_stream_id()));
                    }else {
                        object.put("live_rtmp_url", TencentUtil.getRtmpPlayUrl(roomCacheInfo.getLive_stream_id()));
                        object.put("live_flv_url", TencentUtil.getFlvPlayUrl(roomCacheInfo.getLive_stream_id()));
                    }

                  /*  object.put("live_rtmp_url", TencentUtil.getRtmpPlayUrl(roomCacheInfo.getLive_stream_id()));
                    object.put("live_flv_url", TencentUtil.getFlvPlayUrl(roomCacheInfo.getLive_stream_id()));*/

                    object.put("live_type", roomCacheInfo.getLive_type());
                    object.put("is_horizontal", roomCacheInfo.isHorizontal());
                }
            }
            object.put("state", userImState);
            object.put("store_id", storeService.findStoreIdByUserId(beUserId));
            object.put("is_disturb", videoChatService.isDisturb(beUserId));
        } else {
            String langCode = null;
            if(userCacheInfo != null){
                langCode = userCacheInfo.getLang_code();
            }
            object = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(langCode, "weking.lang.account.exist_error"));
        }
        return object;
    }


    /**
     * 获得改名费
     */
    public int getEditNicknamePrice(int userId) {
        int editNicknamePrice = 0;
        Integer editId = editLogMapper.findIdByUserId(userId, EditTypeEnum.NICKNAME.getEditKey(), null);
        if (editId == null) {
            return editNicknamePrice;
        }
        int anchorLevel = LibSysUtils.toInt(getUserInfoByUserId(userId, "anchor_level"));
        if (anchorLevel <= 0) {
            editNicknamePrice = LibSysUtils.toInt(WKCache.get_system_cache("edit.nickname.price"));
        }
        return editNicknamePrice;
    }

    //查询用户列表
    public JSONObject queryUserList(int userId, String query, int index, int count) {
        query = LibSysUtils.quoteString(query);
        JSONArray jsonArray = new JSONArray();
        if (!LibSysUtils.isNullOrEmpty(query) && query.length() < 11) {
            AccountInfo accountInfo = new AccountInfo();
            accountInfo.setAccount(query);
            accountInfo.setIndex(index);
            accountInfo.setCount(count);
            List<AccountInfo> list = accountMapper.searchAcount(accountInfo);
            JSONObject jsonObject;
            for (AccountInfo info : list) {
                jsonObject = new JSONObject();
                jsonObject.put("account", info.getAccount());
                jsonObject.put("nickname", info.getNickname());
                jsonObject.put("sex", info.getSex());
                jsonObject.put("pic_head_high", WkUtil.combineUrl(info.getPicheadUrl(), UploadTypeEnum.AVATAR, false));
                jsonObject.put("pic_head_low", WkUtil.combineUrl(info.getPicheadUrl(), UploadTypeEnum.AVATAR, true));
                jsonObject.put("signature", info.getSigniture());
                int follow_state = followInfoMapper.verifyIsFollowed(userId, info.getId());
                jsonObject.put("follow_state", follow_state);
                jsonObject.put("level", info.getLevel());
                jsonArray.add(jsonObject);
            }
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", jsonArray);
        return object;
    }

    public JSONObject getInvitePic(int userId,String nickname, String lang_code) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        AccountInfo info = accountMapper.selectByPrimaryKey(userId);
        String srcImgPath = String.format("%s%s%s%s%s%s", LibProperties.getConfig("weking.config.pic.url"), "invite", System.getProperty("file.separator"), "card", System.getProperty("file.separator"), "bg.jpg"); //源图片地址
        String tarImgPath = String.format("%s%s%s%s%s%s", LibProperties.getConfig("weking.config.pic.url"), "invite", System.getProperty("file.separator"), "card", System.getProperty("file.separator"), info.getAccount() + ".jpg"); //源图片地址
        WkUtil.addWaterMark(srcImgPath, tarImgPath, info.getInviteCode());
        result.put("pic_url", "http://pic.appsme.tv/invite/card/" + info.getAccount() + ".jpg");
        result.put("share_context", String.format(LibProperties.getLanguage(lang_code, "weking.lang.post.sharetitle"), nickname));
        return result;
    }

    //设置邀请码
    @Transactional
    public JSONObject setInviteCode(int userId, String nickname, String invite_code, String project_name, String lang_code,double api_version) {
        Integer parentId = accountMapper.selectByInviteCode(invite_code);
        JSONObject object;
        if (parentId != null) {
            if (userId != parentId) {
                int re = accountMapper.updateParentIdByUserId(parentId, userId);
                if (re > 0) {
                   /* if (invite_code.toUpperCase().equals("APPSME")){
                        JSONObject inviteConfig = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.inner_invite_config));
                        long date = LibDateUtils.getLibDateTime("yyyyMMddHHmmss");
                        int invite_num = accountMapper.selectInviteCount(parentId);
                        if (invite_num <= inviteConfig.optInt("reward_times") && date >= inviteConfig.optLong("start_time") && date <= inviteConfig.optLong("end_time")){
                            if (inviteConfig.optDouble("reward_amout") > 0){
                                BigDecimal reward = new BigDecimal(inviteConfig.optString("reward_amout"));
                                digitalService.OptWallect(userId, lang_code, 0, "SCA", reward, (short)7, LibSysUtils.getRandomNum(16), "填邀请码奖励", "");
                            }
                            object = LibSysUtils.getResultJSON(ResultCode.success, LibProperties.getLanguage(lang_code, "weking.lang.app.set.invite_code.success"));
                        }else {
                            object = LibSysUtils.getResultJSON(ResultCode.invite_code_error, LibProperties.getLanguage(lang_code, "weking.lang.app.invite_code.error"));
                        }
                    }else{
                        int award = LibSysUtils.toInt(WKCache.get_system_cache("weking.invite.award"));
                        pocketInfoMapper.increaseDiamondByUserId(parentId, award);//邀请成功奖励100EMO
                        AccountInfo accountInfo = accountMapper.selectByPrimaryKey(parentId);
                        if (accountInfo != null && award > 0) {
                            String msg = LibProperties.getLanguage(lang_code, "weking.lang." + project_name + "app.inviteuser");
                            msg = msg.replace("%nickname%", nickname).replace("%award%", LibSysUtils.toString(award));
                            msgService.sendSysMsg(accountInfo.getAccount(), msg,accountInfo.getLangCode());
                        }
                        //pocketInfoMapper.increaseDiamondByUserId(userId, 50);   //设置邀请码获得50EMO
                        object = LibSysUtils.getResultJSON(ResultCode.success, LibProperties.getLanguage(lang_code, "weking.lang.app.set.invite_code.success"));
                    }*/
                    JSONObject inviteConfig = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.inner_invite_config));
                    BigDecimal reward = new BigDecimal(inviteConfig.optString("reward_amout"));
                    BigDecimal inviteAmout = new BigDecimal(inviteConfig.optString("invite_amout"));

                    digitalService.OptWallect(userId, lang_code, 0, "SCA", reward, (short)7, LibSysUtils.getRandomNum(16), "填邀请码奖励", "邀请成功，获得"+reward+"搜秀链奖励~",api_version);
                    digitalService.OptWallect(parentId, lang_code, 0, "SCA", inviteAmout, (short)7, LibSysUtils.getRandomNum(16), "填邀请码奖励", "邀请成功，获得"+inviteAmout+"搜秀链奖励~",api_version);

                    object = LibSysUtils.getResultJSON(ResultCode.success, LibProperties.getLanguage(lang_code, "weking.lang.app.set.invite_code.success"));


                } else {
                    object = LibSysUtils.getResultJSON(ResultCode.invite_code_set_error, LibProperties.getLanguage(lang_code, "weking.lang.app.set.invite_code.error"));
                }
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.invite_code_is_myself, LibProperties.getLanguage(lang_code, "weking.lang.app.myself.invite_code"));
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.invite_code_error, LibProperties.getLanguage(lang_code, "weking.lang.app.invite_code.error"));
        }
        return object;
    }

    //用户邀请码
    public JSONObject inviteCode(int userId, String lang_code) {
        AccountInfo accountInfo = accountMapper.selectByPrimaryKey(userId);
        JSONObject object;
        if (accountInfo != null) {
            object = LibSysUtils.getResultJSON(ResultCode.success);
            object.put("invite_code", accountInfo.getInviteCode());
            object.put("invite_num", accountMapper.selectInviteCount(accountInfo.getId()));
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.account.exist_error"));
        }
        return object;
    }

    //邀请列表
    public JSONObject inviteList(int userId, int index, int count) {
        List<AccountInfo> list = accountMapper.selectInviteList(userId, index, count);
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        JSONObject obj;
        for (AccountInfo info : list) {
            obj = new JSONObject();
            obj.put("account", info.getAccount());
            obj.put("nickname", info.getNickname());
            obj.put("pic_head_high", WkUtil.combineUrl(info.getPicheadUrl(), UploadTypeEnum.AVATAR, false));
            obj.put("pic_head_low", WkUtil.combineUrl(info.getPicheadUrl(), UploadTypeEnum.AVATAR, true));
            obj.put("sex", info.getSex());
            obj.put("level", info.getLevel());
            array.add(obj);
        }
        object.put("list", array);
        return object;
    }

    //实名认证
    public JSONObject certification(int userId, String real_name, String card_num, String phone, String card_img, String lang_code) {
        JSONObject object;
        if (LibSysUtils.isNullOrEmpty(real_name)) {
            object = LibSysUtils.getResultJSON(ResultCode.data_error, LibProperties.getLanguage(lang_code, "weking.lang.app.data.error"));
        } else if (!IdcardUtils.validateCard(card_num)) {
            object = LibSysUtils.getResultJSON(ResultCode.card_num_error, LibProperties.getLanguage(lang_code, "weking.lang.app.card.num.error"));
        } else if (LibSysUtils.isNullOrEmpty(phone)) {
            object = LibSysUtils.getResultJSON(ResultCode.data_error, LibProperties.getLanguage(lang_code, "weking.lang.app.data.error"));
        } else if (LibSysUtils.isNullOrEmpty(card_img)) {
            object = LibSysUtils.getResultJSON(ResultCode.data_error, LibProperties.getLanguage(lang_code, "weking.lang.app.data.error"));
        } else {
            Certification record = new Certification();
            record.setUserId(userId);
            record.setIdNum(card_num);
            record.setPhone(phone);
            record.setRealName(real_name);
            record.setIdpicUrl(card_img);
            int re = certificationMapper.insert(record);
            if (re > 0) {
                object = LibSysUtils.getResultJSON(ResultCode.success);
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.certification_error, LibProperties.getLanguage(lang_code, "weking.lang.app.certification.error"));
            }
        }
        return object;
    }

    //通过account获取用户信息
    public Map<String, String> getUserInfoByAccount(String account, String... fields) {
        Map<String, String> userMap = new HashMap<>();
        List<String> result = WKCache.getUserByAccount(account, fields);
        if (result == null || result.get(0) == null) {
            AccountInfo accountInfo = accountMapper.findUserMapByAccount(account);
            if (accountInfo != null) {
                int userId = LibSysUtils.toInt(accountInfo.getId());
                userMap.put("user_id", LibSysUtils.toString(accountInfo.getId()));
                userMap.put("avatar", LibSysUtils.toString(accountInfo.getPicheadUrl()));
                userMap.put("account", accountInfo.getAccount());
                userMap.put("c_id", LibSysUtils.toString(accountInfo.getClientid()));
                userMap.put("device_token", LibSysUtils.toString(accountInfo.getDevicetoken()));
                userMap.put("login_time", LibSysUtils.toString(LibDateUtils.getLibDateTime("yyyyMMddHHmmss")));
                userMap.put("nickname", accountInfo.getNickname());
                userMap.put("lang_code", LibSysUtils.toString(accountInfo.getLangCode()));
                userMap.put("level", LibSysUtils.toString(accountInfo.getLevel()));
                userMap.put("lat", LibSysUtils.toString(accountInfo.getLat()));
                userMap.put("lng", LibSysUtils.toString(accountInfo.getLng()));
                userMap.put("experience", LibSysUtils.toString(accountInfo.getExperience()));
                userMap.put("sorts", LibSysUtils.toString(accountInfo.getSorts()));
                userMap.put("role", LibSysUtils.toString(accountInfo.getRole()));
                userMap.put("signature", LibSysUtils.toString(accountInfo.getSigniture()));
                userMap.put("anchor_level", LibSysUtils.toString(accountInfo.getAnchor_level()));
                userMap.put("wallet_currency", LibSysUtils.toString(accountInfo.getWallet_currency()));
                userMap.put("user_name", LibSysUtils.toString(accountInfo.getUser_name()));
                userMap.put("user_email", LibSysUtils.toString(accountInfo.getUser_email()));
                userMap.put("phone", LibSysUtils.toString(accountInfo.getPhone()));
                WKCache.add_user(userId, userMap);
            }
        } else {
            int len = fields.length;
            for (int i = 0; i < len; i++) {
                userMap.put(fields[i], result.get(i));
            }
        }
        return userMap;
    }

    public String getUserFieldByAccount(String account, String field) {
        String result = WKCache.getUserByAccount(account, field);
        if (result == null) {
            Map<String, String> res = getUserInfoByAccount(account, field);
            result = res.get(field);
        }
        return result;
    }

    private Boolean modifyUserInfo(AccountInfo accountInfo) {
        return accountMapper.updateByPrimaryKeySelective(accountInfo) > 0;
    }


    /**
     * 获得用户单个信息
     *
     * @param userId 用户ID
     * @param field  用户信息
     */
    public String getUserInfoByUserId(int userId, String field) {
        if("ratio".equals(field)){
            Map<String, String> userMap = findUserInfoByUserId(userId);
            if (userMap == null) {
                return null;
            }
            userMap.put("c_id", userMap.get("cid"));
            return LibSysUtils.toString(userMap.get(field));
        }
        String result = WKCache.get_user(userId, field);
        if (result == null) {
            Map<String, String> userMap = findUserInfoByUserId(userId);
            if (userMap == null) {
                return null;
            }
            userMap.put("c_id", userMap.get("cid"));
            result = LibSysUtils.toString(userMap.get(field));
        }
        return result;
    }

    /**
     * 通过用户ID获得用户信息
     *
     * @param userId 用户ID
     * @param fields 用户信息字段
     */
    public Map<String, String> getUserInfoByUserId(int userId, String... fields) {
        Map<String, String> userMap = new HashMap<>();
        List<String> result = WKCache.get_user(userId, fields);
        if (result == null || result.get(0) == null) {
            userMap = findUserInfoByUserId(userId);
        } else {
            int len = fields.length;
            for (int i = 0; i < len; i++) {
                userMap.put(fields[i], result.get(i));
            }
        }
        return userMap;
    }

    /**
     * 通过用户ID获得用户信息
     *
     * @param userId 用户ID
     */
    public Map<String, String> getUserInfoByUserId(int userId) {
        Map<String, String> userMap = WKCache.getUserInfo(userId);
        if (userMap == null || userMap.get("id") == null) {
            userMap = findUserInfoByUserId(userId);
        }
        return userMap;
    }

    /**
     * 获得用户基本信息
     */
    public Map<String, String> getUserBaseInfoByUserId(int userId) {
        return getUserInfoByUserId(userId, "account", "nickname", "avatar", "signature", "sex", "level");
    }

    /**
     * 通过用户ID获取用户信息
     *
     * @param userId 用户ID
     */
    private Map<String, String> findUserInfoByUserId(int userId) {
        Map<String, String> map = new HashMap<>();
        Map<String, String> userMap = accountMapper.findUserInfoByUserId(userId);
        if (userMap != null) {
            for (Map.Entry<String, String> entry : userMap.entrySet()) {
                map.put(entry.getKey(), LibSysUtils.toString(entry.getValue()));
            }
            WKCache.add_user(userId, map);
        }
        return userMap;
    }

    /**
     * 获得用户间的距离
     */
    public String getUserDistance(int userId, int otherId, Double dist, String langCode) {
        String distanceStr;
        Double distance;
        if (dist == null) {
            distance = 0D;
            if (userId != otherId) {
                distance = WKCache.get_user_dist(userId, LibSysUtils.toString(otherId));
            }
        } else {
            distance = dist;
        }
        if (distance >= 0) {
            distanceStr = String.format("%skm", String.format("%.2f", distance));
        } else {
            distanceStr = LibProperties.getLanguage(langCode, "weking.lang.app.unknown.location");
        }
        return distanceStr;
    }

    /**
     * 保存用户资源
     */
    public JSONObject saveUserData(int userId, int type, String data) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        if (LibSysUtils.isNullOrEmpty(data)) {
            return LibSysUtils.getResultJSON(ResultCode.data_error, LibSysUtils.getLang("weking.lang.app.data.error"));
        }
        UserDataTypeEnum userDataTypeEnum = UserDataTypeEnum.getEnum(type);
        if (userDataTypeEnum == null) {
            return LibSysUtils.getResultJSON(ResultCode.data_error, LibSysUtils.getLang("weking.lang.app.data.error"));
        }
        int count = userDataMapper.selectCountByUserIdAndDataKey(userId, userDataTypeEnum.getDataKey());
        if (count >= userDataTypeEnum.getNum()) {
            return LibSysUtils.getResultJSON(ResultCode.data_error, LibSysUtils.getLang("user.data.quota"));
        }
        List<UserData> list = new ArrayList<>();
        List<String> dataList = WkUtil.strToList(data);
        long addTime = LibDateUtils.getLibDateTime();
        if (dataList != null) {
            for (String dataValue : dataList) {
                UserData userData = new UserData();
                userData.setUserId(userId);
                userData.setDataKey(userDataTypeEnum.getDataKey());
                userData.setDataValue(dataValue);
                userData.setAddTime(addTime);
                list.add(userData);
            }

            int re = userDataMapper.batchInsert(list);
            if (re > 0) {
                List<UserData> phoneList = userDataMapper.selectDataValueListByUserIdAndDataKey(userId, userDataTypeEnum.getDataKey(), 0);
                JSONArray dataIdsArr = new JSONArray();
                JSONObject dataIdsObj;
                for (UserData userData : phoneList) {
                    if (dataList.contains(userData.getDataValue())) {
                        dataIdsObj = new JSONObject();
                        dataIdsObj.put("id", userData.getId());
                        //dataIdsObj.put("url",userData.getDataValue());
                        dataIdsObj.put("url", WkUtil.combineUrl(userData.getDataValue(), UploadTypeEnum.ALBUM, false));
                        dataIdsArr.add(dataIdsObj);
                    }
                }
                object.put("list", dataIdsArr);
            }
        }
        return object;
    }

    /**
     * 更新用户资源
     */
    public JSONObject updateUserData(int userId, int dataId, String dataValue) {
        if (LibSysUtils.isNullOrEmpty(dataValue)) {
            return LibSysUtils.getResultJSON(ResultCode.data_error, LibSysUtils.getLang("weking.lang.app.data.error"));
        }
        int re = userDataMapper.updateDataValueByIdAndUserId(dataId, userId, dataValue);
        if (re > 0) {
            JSONArray jsonArray = new JSONArray();
            JSONObject dataIdsObj = new JSONObject();
            dataIdsObj.put("id", dataId);
            dataIdsObj.put("url", WkUtil.combineUrl(dataValue, UploadTypeEnum.ALBUM, false));
            jsonArray.add(0, dataIdsObj);
            JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
            object.put("list", jsonArray);
            return object;
        }
        return LibSysUtils.getResultJSON(ResultCode.operation_again_later, LibProperties.getLanguage("weking.lang.operation.later"));
    }

    public JSONObject delUserData(int userId, int dataId) {
        int re = userDataMapper.deleteByPrimaryKey(userId, dataId);
        if (re > 0) {
            return LibSysUtils.getResultJSON(ResultCode.success);
        }
        return LibSysUtils.getResultJSON(ResultCode.operation_again_later, LibProperties.getLanguage("weking.lang.operation.later"));
    }

    private JSONArray getUserDataArray(List<UserData> list) {
        JSONArray dataIdsArr = new JSONArray();
        JSONObject dataIdsObj;
        for (UserData userData : list) {
            dataIdsObj = new JSONObject();
            dataIdsObj.put("id", userData.getId());
            //dataIdsObj.put("url",userData.getDataValue());
            dataIdsObj.put("url", WkUtil.combineUrl(userData.getDataValue(), UploadTypeEnum.ALBUM, false));
            dataIdsArr.add(dataIdsObj);
        }
        return dataIdsArr;
    }

    private JSONArray getUserDataArray(int userId, UserDataTypeEnum userDataTypeEnum) {
        List<UserData> phoneList = userDataMapper.selectDataValueListByUserIdAndDataKey(userId, userDataTypeEnum.getDataKey(), 0);
        JSONArray jsonArray = getUserDataArray(phoneList);
        //追加用户头像到相册列表
        JSONObject dataIdsObj = new JSONObject();
        dataIdsObj.put("id", 0);
        dataIdsObj.put("url", WkUtil.combineUrl(getUserInfoByUserId(userId, "avatar"), UploadTypeEnum.AVATAR, false));
        jsonArray.add(0, dataIdsObj);
        return jsonArray;
    }


    /**
     * 获取用户VIP等级
     */
    public JSONObject getVipPrivilege(int userId) {

        JSONObject obj = WKCache.get__room_live_privilege(userId);
        if (obj!=null){
            return obj;
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        List<VipPrivilege> vipPrivileges = vipPrivilegeMapper.selectAllVipPrivilege();
        Integer vip_level=0;
        Integer next_level_emo=0;
        String privilege="";
        //查询缓存用户当月充值emo
        Double buyEmo = WKCache.get_recharge_rank_month(LibDateUtils.getLibDateTime("yyyyMM"), LibSysUtils.toString(userId));
        if (buyEmo==null) {
            object.put("buy_emo", 0);
            next_level_emo = vipPrivileges.get(0).getBuyNum();
        }else {
            if (vipPrivileges.size()>0) {
                if (buyEmo >= vipPrivileges.get(0).getBuyNum()) {
                    for (int i = 0; i < vipPrivileges.size(); i++) {
                        if (buyEmo < vipPrivileges.get(i).getBuyNum()) {
                            vip_level = vipPrivileges.get(i - 1).getVipLevel();
                            privilege = vipPrivileges.get(i - 1).getPrivilege();
                            next_level_emo = vipPrivileges.get(i).getBuyNum();
                            break;
                        }
                    }
                }else {
                    next_level_emo = vipPrivileges.get(0).getBuyNum();
                }
                if (buyEmo>=vipPrivileges.get(vipPrivileges.size()-1).getBuyNum()){
                    vip_level = vipPrivileges.get(vipPrivileges.size()-1).getVipLevel();
                    privilege = vipPrivileges.get(vipPrivileges.size()-1).getPrivilege();
                    next_level_emo = vipPrivileges.get(vipPrivileges.size()-1).getBuyNum();
                }

            }
            object.put("buy_emo", buyEmo.intValue());
        }
        object.put("vip_level",vip_level);
        object.put("privilege",privilege);
        object.put("next_level_emo",next_level_emo);
        object.put("list", JSONArray.fromObject(vipPrivileges));
        WKCache.set_room_live_privilege(userId,object);
        return object;
    }


}
