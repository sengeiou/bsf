package com.weking.service.user;

import com.weking.cache.UserCacheInfo;
import com.weking.cache.WKCache;
import com.weking.core.*;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.follow.FollowInfoMapper;
import com.weking.model.account.AccountInfo;
import com.weking.model.follow.FollowInfo;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("followService")
public class FollowService extends LibServiceBase {

    @Resource
    private AccountInfoMapper accountInfoMapper;
    @Resource
    private FollowInfoMapper followInfoMapper;

    /**
     * 关注
     *
     * @param user_id        粉丝id
     * @param toAccount      被关注者account
     * @param follow_type    关注状态 0取消关注  1 关注
     * @param live_stream_id 流id
     */
    public JSONObject addCancelFollow(int user_id, String toAccount, int level, int follow_type, String live_stream_id, int live_id, String lang_code) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        //先通过account获取到被关注者的userid ，然后插入数据库
        AccountInfo accountInfo = accountInfoMapper.selectByAccountId(toAccount);
        if (accountInfo != null) {
            int beFollowedId = accountInfo.getId();
            if (beFollowedId == user_id) {//不可以关注自己
                result.put("code", ResultCode.fllow_unfllowme);
                result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.fllow.fllowme"));
                return result;
            }
            if (follow_type == 0) {//取消关注
                int re = followInfoMapper.deleteByUserid(user_id, beFollowedId);
                if(re > 0){
                    WKCache.add_fans_rank(LibDateUtils.getLibDateTime("yyyyMM"),-1,toAccount);
                    WKCache.add_fans_rank_day(LibDateUtils.getLibDateTime("yyyyMMdd"),-1,toAccount);
                    WKCache.add_fans_rank_week(DateUtils.getMondayOfThisWeek("yyyyMMdd"),-1,toAccount);
                }
                result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.cancel.success"));
            } else {//关注
                long followTime = LibDateUtils.getLibDateTime();
                FollowInfo record = new FollowInfo();
                record.setFollowId(user_id);
                record.setBefollowId(beFollowedId);
                record.setFollowTime(followTime);
                boolean is_fllow = followInfoMapper.verifyIsFollowed(user_id, beFollowedId) > 0; //判断我是否已经关注他
                boolean is_fllowme = followInfoMapper.verifyIsFollowed(beFollowedId, user_id) > 0; //判断他是否已经关注我
                if (!is_fllow) {
                    int re = followInfoMapper.insertSelective(record);
                    if(re > 0){
                        WKCache.add_fans_rank(LibDateUtils.getLibDateTime("yyyyMM"),1,toAccount);
                        WKCache.add_fans_rank_day(LibDateUtils.getLibDateTime("yyyyMMdd"),1,toAccount);
                        WKCache.add_fans_rank_week(DateUtils.getMondayOfThisWeek("yyyyMMdd"),1,toAccount);
                    }
                }
                if (is_fllowme)
                    result.put("follow_state", 2);//相互关注
                else
                    result.put("follow_state", 1);//我关注他

                int anchorId = LibSysUtils.toInt(WKCache.get_room(live_id,"user_id"));
                if (!LibSysUtils.isNullOrEmpty(live_stream_id) && accountInfo.getId()==anchorId) {//如果流id不为空则推送一条关注消息到房间
                    UserCacheInfo userCacheInfo = WKCache.get_user(user_id);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("im_code", IMCode.sys_msg);
                    jsonObject.put("account", userCacheInfo.getAccount());
                    jsonObject.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.follow_anchor"));
                    jsonObject.put("pic_head_low", WkUtil.combineUrl(userCacheInfo.getAvatar(), UploadTypeEnum.AVATAR, true));
                    jsonObject.put("nickname", userCacheInfo.getNickname());
                    jsonObject.put("live_id", live_id);

                    jsonObject.put("level", level);
                    WkImClient.sendRoomMsg(live_stream_id,jsonObject.toString(), 1);
                }
                result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.fllow.success"));
            }
        } else {
            result = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.account.exist_error"));
        }

        return result;
    }

    /**
     * 获取粉丝列表
     *
     * @param user_id 用户id
     */
    public JSONObject getFans(int user_id, String account, int index, int count) {
        AccountInfo accountInfo = accountInfoMapper.selectByAccountId(account);
        JSONObject result;
        if (accountInfo != null) {
            result = LibSysUtils.getResultJSON(ResultCode.success);
            List<AccountInfo> list = accountInfoMapper.getFans(accountInfo.getId(), index, count);
            JSONArray array = getFansStartsInfo(list, 0, user_id);
            result.put("list", array);
        } else {
            UserCacheInfo userCacheInfo = WKCache.get_user(user_id);
            result = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.account.exist_error"));
        }

        return result;
    }

    /**
     * 获取我关注的列表
     */
    public JSONObject getStarts(int user_id, String account, int index, int count) {
        AccountInfo accountInfo = accountInfoMapper.selectByAccountId(account);
        JSONObject result;
        if (accountInfo != null) {
            result = LibSysUtils.getResultJSON(ResultCode.success);
            List<AccountInfo> list = accountInfoMapper.getStarts(accountInfo.getId(), index, count);
            JSONArray array = getFansStartsInfo(list, 1, user_id);
            result.put("list", array);
        } else {
            UserCacheInfo userCacheInfo = WKCache.get_user(user_id);
            result = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.account.exist_error"));
        }
        return result;
    }

    /**
     * @param type 0获取粉丝列表，1：获取关注列表
     */
    private JSONArray getFansStartsInfo(List<AccountInfo> list, int type, int user_id) {
        JSONArray result = new JSONArray();
        for (AccountInfo fansInfo : list) {
            JSONObject jinfo = new JSONObject();
            String picurl = fansInfo.getPicheadUrl();
            picurl = WkUtil.combineUrl(picurl, UploadTypeEnum.AVATAR, true);
            int userid = fansInfo.getId();
            int follow_state = followStatus(user_id, userid);
            jinfo.put("account", fansInfo.getAccount() == null ? "" : fansInfo.getAccount());
            jinfo.put("nickname", fansInfo.getNickname() == null ? "" : fansInfo.getNickname());
            jinfo.put("sex", fansInfo.getSex() == null ? 0 : fansInfo.getSex());
            jinfo.put("pic_head_low", picurl);
            jinfo.put("signature", fansInfo.getSigniture() == null ? "" : fansInfo.getSigniture());
            jinfo.put("follow_state", follow_state);
            jinfo.put("level", fansInfo.getLevel());
            result.add(jinfo);
        }
        return result;
    }

    /**
     * 用户双方的关注状态
     *
     * @return 0未关注1已关注2互关注
     */
    public int followStatus(int userId, int other) {
        int follow_state;
        follow_state = followInfoMapper.verifyIsFollowed(userId, other);
        int befollow_state = followInfoMapper.verifyIsFollowed(other, userId); //判断对方是否关注了用户
        if (befollow_state == 1) {
            if (follow_state == 1) {
                follow_state = 2;
            }
        }
        return follow_state;
    }

    /**
     * 用户双方的关注状态
     *
     * @return 0未关注1已关注
     */
    public int getFollowStatus(int userId, int other) {
        return followInfoMapper.verifyIsFollowed(userId, other);
    }


}
