package com.weking.service.user;

import com.weking.cache.UserCacheInfo;
import com.weking.cache.WKCache;
import com.weking.core.IMCode;
import com.weking.core.ResultCode;
import com.weking.core.WkImClient;
import com.weking.core.WkUtil;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.blacklog.BlackLogMapper;
import com.weking.model.account.AccountInfo;
import com.weking.model.blacklog.BlackLog;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("blackLogService")
public class BlackLogService extends LibServiceBase {

    @Resource
    private BlackLogMapper blackLogMapper;
    @Resource
    private AccountInfoMapper accountInfoMapper;


    //拉黑
    public JSONObject pullBlack(int userId, String account,String live_stream_id,int live_id ,double api_version) {
        if(api_version>=4.0) {
            if (live_id != 0) {
                userId = LibSysUtils.toInt(WKCache.get_room(live_id, "user_id"));
            }
        }
        live_stream_id = WKCache.get_room(live_id, "live_stream_id");
        UserCacheInfo userInfo = WKCache.get_user(userId);
        String lang_code = userInfo.getLang_code();
        JSONObject object = checkBeUser(userId, account,lang_code);
        if (object.getInt("code")==ResultCode.account_black_un) {
            if (live_id==0||live_stream_id==null) {
                int beuser_id = object.getInt("beuser_id");
                BlackLog record = new BlackLog();
                record.setUserId(userId);
                record.setBeuserId(beuser_id);
                Long time = LibDateUtils.getLibDateTime();
                record.setAddtime(time);
                blackLogMapper.insert(record);
            }else {
                object = LibSysUtils.getResultJSON(ResultCode.success,LibProperties.getLanguage(lang_code,"weking.lang.blacklist.success"));
                if (api_version>=4.0&&live_stream_id!=null) {
                    List<String> user_info = WKCache.getUserByAccount(account, "nickname", "level");
                    String user_nickname = user_info.get(0);
                    int level = LibSysUtils.toInt(user_info.get(1));
                    JSONObject sender = new JSONObject();
                    sender.put("live_id", live_id);
                    sender.put("account", account);
                    sender.put("nickname", user_nickname);
                    sender.put("level", level);
                    sender.put("im_code", IMCode.out_room);
                    sender.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.live.out.room"));
                    WkImClient.sendRoomMsg(live_stream_id, sender.toString(), 1);
                    WKCache.add_out_room_id(account,live_id);
                    object.put("msg",LibProperties.getLanguage(lang_code, "weking.lang.live.out.room_manager"));
                }
            }
        }else{
            object.remove("black_id");
        }
        return object;
    }

    //取消拉黑
    public JSONObject cacelBlack(int userId, String account) {
        UserCacheInfo userInfo = WKCache.get_user(userId);
        String lang_code = userInfo.getLang_code();
        JSONObject object = checkBeUser(userId, account,lang_code);
        if(object.getInt("code")==ResultCode.account_black_exist){
            int black_id = object.getInt("black_id");
            int re = blackLogMapper.deleteByPrimaryKey(black_id);
            if(re > 0){
                object = LibSysUtils.getResultJSON(ResultCode.success,LibProperties.getLanguage(lang_code,"weking.lang.app.cancel.success"));
            }
        }else{
            object.remove("beuser_id");
        }
        return object;
    }

    //拉黑列表
    public JSONObject getList(int userId, int index, int count) {
        JSONObject object = new JSONObject();
        JSONObject jsonObject;
        JSONArray array = new JSONArray();
        List<BlackLog> blackList = blackLogMapper.selectBlackList(userId, index, count);
        for(BlackLog blackInfo:blackList) {
            jsonObject = new JSONObject();
            jsonObject.put("nickname",blackInfo.getNickname());
            jsonObject.put("account",blackInfo.getAccount());
            jsonObject.put("sex",blackInfo.getSex());
            jsonObject.put("level",blackInfo.getLevel());
            jsonObject.put("pic_head_low", WkUtil.combineUrl(blackInfo.getPicheadUrl(), UploadTypeEnum.AVATAR,true));
            jsonObject.put("pic_head_high",WkUtil.combineUrl(blackInfo.getPicheadUrl(), UploadTypeEnum.AVATAR,false));
            array.add(jsonObject);
        }
        object.put("code",0);
        object.put("list",array);
        return object;
    }

    /**
     * 验证被拉黑用户
     */
    private JSONObject checkBeUser(int userId, String account,String lang_code) {
        //该用户是否存在
        AccountInfo accountInfo = accountInfoMapper.selectByAccountId(account);
        JSONObject object;
        if (accountInfo == null) {
            object = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(lang_code,"weking.lang.account.exist_error"));
        } else {
            if(userId != accountInfo.getId()){
                int uid = accountInfo.getId();
                BlackLog balckLog = blackLogMapper.verifiUserBlack(userId,uid);
                if (balckLog != null) { //拉黑
                    object = LibSysUtils.getResultJSON(ResultCode.account_black_exist,LibProperties.getLanguage(lang_code,"weking.lang.account.black.exist"));
                    object.put("black_id",balckLog.getId());
                } else {
                    object = LibSysUtils.getResultJSON(ResultCode.account_black_un,LibProperties.getLanguage(lang_code,"weking.lang.account.black.un"));
                    object.put("beuser_id",uid);
                }
            }else{
                object = LibSysUtils.getResultJSON(ResultCode.black_unblackme,LibProperties.getLanguage(lang_code,"weking.lang.black.blackme"));
            }
        }
        return object;
    }
    //是否拉黑
    public JSONObject isBlack(int userId,String account){
        JSONObject jsonObject = checkBeUser(userId,account,"");
        JSONObject object;
        if(jsonObject.optInt("code")==ResultCode.account_black_exist){
            object = LibSysUtils.getResultJSON(ResultCode.success);
        }else{
            object = LibSysUtils.getResultJSON(ResultCode.account_black_un);
        }
        return object;
    }

}
