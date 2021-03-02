package com.weking.service.system;

import com.weking.cache.UserCacheInfo;
import com.weking.cache.WKCache;
import com.weking.core.*;
import com.weking.core.enums.OperateTypeEnum;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.core.sensitive.WordFilter;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.advertisement.AdvertisementMapper;
import com.weking.mapper.keyword.KeyWordMapper;
import com.weking.mapper.log.ConsumeInfoMapper;
import com.weking.mapper.log.LiveLogInfoMapper;
import com.weking.mapper.log.OperationLogMapper;
import com.weking.mapper.pocket.GiftInfoMapper;
import com.weking.mapper.pocket.OrderMapper;
import com.weking.mapper.report.ReportInfoMapper;
import com.weking.mapper.suggestion.SuggestionMapper;
import com.weking.mapper.version.VersionMapper;
import com.weking.model.account.AccountInfo;
import com.weking.model.advertisement.Advertisement;
import com.weking.model.log.LiveLogInfo;
import com.weking.model.log.OperationLog;
import com.weking.model.pocket.GiftInfo;
import com.weking.model.report.ReportInfo;
import com.weking.model.suggestion.Suggestion;
import com.weking.model.version.Version;
import com.weking.service.post.PostService;
import com.weking.service.user.UserService;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.weking.core.C.ImField.type;

/**
 * 系统系统
 */
@Service("systemService")
public class SystemService extends LibServiceBase {

    private static Logger log = Logger.getLogger(SystemService.class);

    @Resource
    private AdvertisementMapper advertisementMapper;
    @Resource
    private AccountInfoMapper accountInfoMapper;
    @Resource
    private ReportInfoMapper reportInfoMapper;
    @Resource
    private SuggestionMapper suggestionMapper;
    @Resource
    private VersionMapper versionMapper;
    @Resource
    private PostService postService;
    @Resource
    private OperationLogMapper operationLogMapper;
    @Resource
    private KeyWordMapper keyWordMapper;
    @Resource
    private UserService userService;
    @Resource
    private MsgService msgService;
    @Resource
    private LiveLogInfoMapper liveLogInfoMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private ConsumeInfoMapper consumeInfoMapper;
    @Resource
    private GiftInfoMapper giftInfoMapper;

    /**
     * 获取广告内容
     *
     * @param type 获取广告类型   0：登录页广告，1：启动广告，2：首页广告,3:游戏首页,4直播间内的广告,5动态广告  6直播间领取奖励观看广告
     */
    public JSONObject getAdvertisement(int type, String project_name, String lang_code) {
        List<Advertisement> list = advertisementMapper.selectByType(type, project_name);
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        for (Advertisement advertisement : list) {
            JSONObject tmp = new JSONObject();
            tmp.put("adv_type", advertisement.getAdv_type());
            tmp.put("height", advertisement.getHeight());
            tmp.put("width", advertisement.getWidth());
            switch (lang_code) {
                case "zh_CN":
                    tmp.put("img_url", WkUtil.combineUrl(advertisement.getImgUrl(), UploadTypeEnum.ADV, false));
                    break;
                case "zh_TW":
                    tmp.put("img_url", WkUtil.combineUrl(advertisement.getImgUrl(), UploadTypeEnum.ADV, false));
                    break;
                case "en_US":
                    tmp.put("img_url", WkUtil.combineUrl(advertisement.getEn_img_url(), UploadTypeEnum.ADV, false));
                    break;
                case "ms":
                    tmp.put("img_url", WkUtil.combineUrl(advertisement.getMy_img_url(), UploadTypeEnum.ADV, false));
                    break;
                default:
                    tmp.put("img_url", WkUtil.combineUrl(advertisement.getImgUrl(), UploadTypeEnum.ADV, false));
                    break;
            }
            tmp.put("link_url", advertisement.getLinkUrl());
            tmp.put("title", advertisement.getTitle());
            if (!LibSysUtils.isNullOrEmpty(advertisement.getAd_unit_id())) {
                tmp.put("ad_unit_id", advertisement.getAd_unit_id());
            }
            array.add(tmp);
        }
        result.put("adv_list", array);
        return result;
    }

    //举报
    public JSONObject report(int userId, String account, String report_msg,int type) {
        JSONObject object;
        UserCacheInfo userCacheInfo = WKCache.get_user(userId);
        log.error("userId"+userId+"===account"+account+"====type:"+type);
        if(type==0) {
            AccountInfo accountInfo = accountInfoMapper.selectByAccountId(account);
            if (accountInfo != null) {
                if (userId != accountInfo.getId()) {
                    ReportInfo info = reportInfoMapper.selectByUserId(userId, accountInfo.getId());
                    if (info != null && info.getAddTime() > LibDateUtils.getLibDateTime("yyyyMMdd000000")) {//24小时之内只能举报一次同一用户
                        object = LibSysUtils.getResultJSON(ResultCode.report_exist, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.app.informed"));
                    } else {
                        ReportInfo record = new ReportInfo();
                        record.setUserId(userId);
                        record.setAddTime(LibDateUtils.getLibDateTime());
                        record.setOtherId(accountInfo.getId());
                        record.setType(0);
                        record.setReportMsg(report_msg);
                        int re = reportInfoMapper.insert(record);
                        if (re > 0) {
                            object = LibSysUtils.getResultJSON(ResultCode.success);
                        } else {
                            object = LibSysUtils.getResultJSON(ResultCode.report_error, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.app.inform.error"));
                        }
                    }
                } else {
                    object = LibSysUtils.getResultJSON(ResultCode.report_unreportme, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.report.reportme"));
                }
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.account.exist_error"));
            }
        }else if(type==1){
            ReportInfo info = reportInfoMapper.selectByUserId(userId, LibSysUtils.toInt(account));
            if (info != null && info.getAddTime() > LibDateUtils.getLibDateTime("yyyyMMdd000000")) {//24小时之内只能举报一次同一用户
                object = LibSysUtils.getResultJSON(ResultCode.report_exist, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.app.informed"));
            } else {
                ReportInfo record = new ReportInfo();
                record.setUserId(userId);
                record.setAddTime(LibDateUtils.getLibDateTime());
                record.setOtherId(LibSysUtils.toInt(account));
                record.setReportMsg(report_msg);
                record.setType(1);
                int re = reportInfoMapper.insert(record);
                if (re > 0) {
                    object = LibSysUtils.getResultJSON(ResultCode.success);
                } else {
                    object = LibSysUtils.getResultJSON(ResultCode.report_error, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.app.inform.error"));
                }
            }

        }else {
            ReportInfo info = reportInfoMapper.selectByUserId(userId, LibSysUtils.toInt(account));
            if (info != null && info.getAddTime() > LibDateUtils.getLibDateTime("yyyyMMdd000000")) {//24小时之内只能举报一次同一用户
                object = LibSysUtils.getResultJSON(ResultCode.report_exist, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.app.informed"));
            } else {
                ReportInfo record = new ReportInfo();
                record.setUserId(userId);
                record.setAddTime(LibDateUtils.getLibDateTime());
                record.setOtherId(LibSysUtils.toInt(account));
                record.setReportMsg(report_msg);
                record.setType(2);
                int re = reportInfoMapper.insert(record);
                if (re > 0) {
                    object = LibSysUtils.getResultJSON(ResultCode.success);
                } else {
                    object = LibSysUtils.getResultJSON(ResultCode.report_error, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.app.inform.error"));
                }
            }
        }
        return object;
    }

    //举报信息列表
    public JSONObject reportList(String lang_code) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(LibProperties.getLanguage(lang_code, "weking.lang.app.report.msg01"));
        jsonArray.add(LibProperties.getLanguage(lang_code, "weking.lang.app.report.msg02"));
        jsonArray.add(LibProperties.getLanguage(lang_code, "weking.lang.app.report.msg03"));
        jsonArray.add(LibProperties.getLanguage(lang_code, "weking.lang.app.report.msg04"));
        jsonArray.add(LibProperties.getLanguage(lang_code, "weking.lang.app.report.msg05"));
        jsonArray.add(LibProperties.getLanguage(lang_code, "weking.lang.app.report.msg06"));
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", jsonArray);
        return object;
    }

    //用户反馈
    public JSONObject feedback(int userId, String sugg_msg, String sugg_pic, String contact, String lang_code) {
        JSONObject object;
        if (LibSysUtils.isNullOrEmpty(sugg_msg)) {
            object = LibSysUtils.getResultJSON(ResultCode.feedback_error, LibProperties.getLanguage(lang_code, "weking.lang.app.feedback.error"));
        } else {
            Suggestion record = new Suggestion();
            record.setUserId(userId);
            record.setContactNum(contact);
            record.setSuggesstion(sugg_msg);
            record.setSuggPic(sugg_pic);
            record.setSuggTime(LibDateUtils.getLibDateTime());
            int re = suggestionMapper.insert(record);
            if (re > 0) {
                object = LibSysUtils.getResultJSON(ResultCode.success, LibProperties.getLanguage(lang_code, "weking.lang.app.feedback.success"));
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.feedback_error, LibProperties.getLanguage(lang_code, "weking.lang.app.feedback.error"));
            }
        }
        return object;
    }

    /**
     * 获取当前版本
     *
     * @param type 0:安卓，1：IOS
     */
    public JSONObject getVersion(int type) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        Version v = versionMapper.selectByType(type, "");
        result.put("must", LibSysUtils.toBoolean(v.getMust()));
        result.put("update_msg", v.getUpdateMsg());
        result.put("url", v.getUrl());
        result.put("version_code", v.getVersionCode());
        result.put("version_name", v.getVersionName());
        return result;
    }

    /**
     * 用户操作埋点记录
     */
    @Transactional
    public JSONObject operateRecord(int userId, String account, int type, String params) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        OperationLog operationLog = new OperationLog();
        if (type == OperateTypeEnum.VIEW_POST.getType()) {
            JSONObject paramsJSON = JSONObject.fromObject(params);
            int post_id = paramsJSON.optInt("post_id");
            String poster_account = paramsJSON.optString("poster_account");
            if (post_id != 0 && !LibSysUtils.isNullOrEmpty(poster_account)) {
                postService.viewPost(userId, post_id, poster_account);
            }
            operationLog.setExtend_id(post_id);
        }
        if (type == OperateTypeEnum.GUIDE_POST.getType()) {
            // 设置用户引导发帖状态
            WKCache.addUserGuideState(userId, 0);
            operationLog.setExtend_id(0);
        }
        if (type == OperateTypeEnum.HIDE_POST.getType()) {
            // 添加隐藏帖子缓存
            JSONObject paramsJSON = JSONObject.fromObject(params);
            int post_id = paramsJSON.optInt("post_id");

            double hidden_num = LibSysUtils.toDouble(WKCache.getHiddenPost(post_id), 0);
            if (hidden_num + 1 >= LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.hide_post_num))) {
                // 设置帖子为隐藏状态
                postService.changePostStatus(post_id, 2);
            }
            //WKCache.addHiddenPost(userId, post_id);
            WKCache.del_recommend_post(post_id);
            operationLog.setExtend_id(post_id);
        }
        operationLog.setAddTime(LibDateUtils.getLibDateTime());
        operationLog.setUserId(userId);
        operationLog.setOperateType((short) type);
        operationLog.setOperateName(OperateTypeEnum.getTypeEnum(type).getName());
        operationLogMapper.insert(operationLog);
        return result;
    }


    public JSONObject addUserCache(int index, int count) {
        List<AccountInfo> list = accountInfoMapper.selectNormalUserList(index, count);
        for (AccountInfo accountInfo : list) {
            Map<String, String> user_cache_info = new HashMap<>();
            user_cache_info.put("user_id", LibSysUtils.toString(accountInfo.getId()));
            user_cache_info.put("account", accountInfo.getAccount());
            user_cache_info.put("avatar", accountInfo.getPicheadUrl());
            user_cache_info.put("c_id", LibSysUtils.toString(accountInfo.getClientid()));
            user_cache_info.put("device_token", LibSysUtils.toString(accountInfo.getDevicetoken()));
            user_cache_info.put("login_time", LibSysUtils.toString(LibDateUtils.getLibDateTime("yyyyMMddHHmmss")));
            user_cache_info.put("nickname", accountInfo.getNickname());
            user_cache_info.put("login_type", LibSysUtils.toString(type));
            user_cache_info.put("lang_code", LibSysUtils.toString(accountInfo.getLangCode()));
            user_cache_info.put("level", LibSysUtils.toString(accountInfo.getLevel()));
            user_cache_info.put("lat", LibSysUtils.toString(accountInfo.getLat()));
            user_cache_info.put("lng", LibSysUtils.toString(accountInfo.getLng()));
            user_cache_info.put("experience", LibSysUtils.toString(accountInfo.getExperience()));
            user_cache_info.put("sorts", LibSysUtils.toString(accountInfo.getSorts()));
            user_cache_info.put("role", LibSysUtils.toString(accountInfo.getRole()));
            user_cache_info.put("anchor_level", LibSysUtils.toString(accountInfo.getAnchor_level()));
            user_cache_info.put("signature", accountInfo.getSigniture());
            user_cache_info.put("wallet_currency", LibSysUtils.toString(accountInfo.getWallet_currency()));
            user_cache_info.put("sex", LibSysUtils.toString(accountInfo.getSex()));
            WKCache.add_user_cache(accountInfo.getId(), user_cache_info);
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    // 刷新内存
    public void refreshKeyWord(String accessToken) {
// 分发到各个服务器
        String inIp = WKCache.get_system_cache(C.WKSystemCacheField.s_server_inip);
        if (!LibSysUtils.isNullOrEmpty(inIp)) {
            String[] inIps = inIp.split(",");
            Map<String, String> map = new HashMap<String, String>();
            map.put("access_token", accessToken);
            for (int i = 0; i < inIps.length; i++) {
                String url = inIps[i] + "live/server/refreshInner";
                post2server(url, map);
            }

        } else {
            log.error("刷新keyWords报错，inIp为空");
        }
    }

    private void post2server(String url, Map<String, String> params) {
        HttpClient client = new HttpClient();
        PostMethod method;
        method = new PostMethod(url);
        client.getParams().setContentCharset("UTF-8");
        method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=UTF-8");
        NameValuePair[] nameValuePairs = new NameValuePair[params.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            nameValuePairs[i] = new NameValuePair(entry.getKey(), entry.getValue());
            i++;
        }
        method.setRequestBody(nameValuePairs);
        try {
            client.executeMethod(method);
            String SubmitResult = method.getResponseBodyAsString();
            System.out.println(SubmitResult);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 刷新内存
     */
    public void refreshInner() {
        List<String> list = keyWordMapper.selectKeyWordList();
        WordFilter.init(list);
        System.out.println("刷新。。。。refreshInner");
    }


    /**
     * 封锁用户状态
     * @param user_id    用户id
     * @param type  0封锁  1解封
     * @param reason   原因
     * @param day_num   天数
     */
    public void setLockout(int user_id, int type, String reason, int day_num) {
        Map<String, String> posterMap = userService.getUserInfoByUserId(user_id, "account", "nickname", "lang_code");
        if (type == 0) {
            String msg = String.format(LibProperties.getLanguage(posterMap.get("lang_code"), "post.lockout"), day_num) + reason;
            WKCache.addLockoutUserCache(user_id, msg);
            // 发送系统消息
            msgService.sendSysMsg(posterMap.get("account"), msg, posterMap.get("lang_code"));
        } else {
            WKCache.delLockoutUserCache(user_id);
            // 发送系统消息
            String msg = LibProperties.getLanguage(posterMap.get("lang_code"), "post.lockout.cancel");
            msgService.sendSysMsg(posterMap.get("account"), msg, posterMap.get("lang_code"));
        }
    }

    //录制
    public JSONObject record(String stream_id, String video_url) {

        JSONObject object;
        int re = liveLogInfoMapper.updateLiveReplayUrl(video_url, TencentUtil.getStreamId(stream_id));
        if (re > 0) {
            object = LibSysUtils.getResultJSON(ResultCode.success);
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.system_error);
        }
        return object;
    }

    //验证腾讯云签名
    public Boolean checkSign(String time, String sign) {
        String check_key = WKCache.get_system_cache("weking.config.tencent.check.key");
        if (sign.equals(EncoderHandler.encodeByMD5(check_key + time))) {
            return true;
        } else {
            return false;
        }
    }

    public JSONObject rechargeLog(String account,int paymentCode,int state,Long beginTime,Long endTime,int index,int count) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        List<Map<String, Object>> list;
        if (LibSysUtils.isNullOrEmpty(account)){
            object.put("code", ResultCode.system_error);
            object.put("msg", "请输入账号查询~");
            return object;
        }else if (beginTime==0L||endTime==0L){
            object.put("code", ResultCode.system_error);
            object.put("msg", "请输入查询时间区间~");
        }else {
            Integer userId = accountInfoMapper.findUserIdByAccount(account);
            list = orderMapper.selectPayListByUserIdOrOther(userId, paymentCode, state, beginTime, endTime, index, count);
            int size = list.size();
            if (size > 0) {
                JSONObject temp;
                for (int i = 0; i < size; i++) {
                    temp = new JSONObject();
                    Map<String, Object> map = list.get(i);
                    temp.put("account", LibSysUtils.toString(map.get("account")));
                    temp.put("nickname", LibSysUtils.toString(map.get("nickname")));
                    temp.put("order_sn", LibSysUtils.toString(map.get("order_sn")));
                    temp.put("trade_no", LibSysUtils.toString(map.get("trade_no")));
                    temp.put("payment_code", LibSysUtils.toInt(map.get("payment_code")));
                    temp.put("amount", LibSysUtils.toDouble(map.get("amount")));
                    temp.put("buy_num", LibSysUtils.toInt(map.get("buy_num")));
                    temp.put("currency", LibSysUtils.toString(map.get("currency")));
                    temp.put("state", LibSysUtils.toInt(map.get("state")));
                    temp.put("ratio", LibSysUtils.toDouble(map.get("ratio")));
                    temp.put("add_time", DateUtils.longTimeToString(LibSysUtils.toLong(map.get("add_time"))));
                    /*temp.put("order", LibSysUtils.toString(i + 1));*/
                    array.add(temp);
                }
            }
            object.put("list", array);
        }

        return object;
    }


    public JSONObject consumeLog(String account,Long beginTime,Long endTime,int index,int count) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        if (LibSysUtils.isNullOrEmpty(account)){
            object.put("code", ResultCode.system_error);
            object.put("msg", "请输入账号查询~");
            return object;
        }else if (beginTime==0L||endTime==0L){
            object.put("code", ResultCode.system_error);
            object.put("msg", "请输入查询时间区间~");
        }else {
            Integer userId = accountInfoMapper.findUserIdByAccount(account);
            List<Map<String, Object>> list = consumeInfoMapper.getConsumeByUserIdAndTime(userId, beginTime, endTime,index, count);
            int size = list.size();
            Map<String, String> giftMap=new HashMap<>();
            Map<String, String> accountMap=new HashMap<>();
            Map<String, String> nicknameMap=new HashMap<>();
            if (size > 0) {
                JSONObject temp;
                for (int i = 0; i < size; i++) {
                    temp = new JSONObject();
                    Map<String, Object> map = list.get(i);
                    if ( map.get("nickname")==null){
                        temp.put("nickname", "平台");
                    }else {
                        temp.put("nickname", LibSysUtils.toString(map.get("nickname")));
                    }
                    if ( map.get("account")==null){
                        temp.put("account", "平台");
                    }else {
                        temp.put("account", LibSysUtils.toString(map.get("account")));
                    }

                    temp.put("send_diamond",LibSysUtils.toInt(map.get("send_diamond")));
                    temp.put("ratio", LibSysUtils.toDouble(map.get("ratio")));
                    temp.put("price", LibSysUtils.toDouble(map.get("price")));
                    if (LibSysUtils.toLong(map.get("send_time"))!=0) {
                        temp.put("add_time", DateUtils.longTimeToString(LibSysUtils.toLong(map.get("send_time"))));
                    }else {
                        temp.put("add_time", "0");

                    }

                    int gift_id = LibSysUtils.toInt(map.get("gift_id"));
                    int send_id = LibSysUtils.toInt(map.get("send_id"));
                    String send_account = accountMap.get(LibSysUtils.toString(send_id));
                    if(LibSysUtils.isNullOrEmpty(send_account)){
                        AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(send_id);
                        if (accountInfo!=null){
                            temp.put("send_account",accountInfo.getAccount());
                            accountMap.put(LibSysUtils.toString(send_id),accountInfo.getAccount());
                        }
                    }else {
                        temp.put("send_account",send_account);
                    }


                    String send_nickname = nicknameMap.get(LibSysUtils.toString(send_id));
                    if(LibSysUtils.isNullOrEmpty(send_nickname)){
                        AccountInfo accountInfo = accountInfoMapper.selectByPrimaryKey(send_id);
                        if (accountInfo!=null){
                            temp.put("send_nickname",accountInfo.getNickname());
                            nicknameMap.put(LibSysUtils.toString(send_id),accountInfo.getNickname());
                        }
                    }else {
                        temp.put("send_nickname",send_nickname);
                    }



                    int live_record_id = LibSysUtils.toInt(map.get("live_record_id"));
                    LiveLogInfo liveLogInfo = liveLogInfoMapper.selectByPrimaryKey(live_record_id);
                    if (liveLogInfo!=null){
                        temp.put("title",liveLogInfo.getLive_title());
                    }else {
                        temp.put("title","");
                    }

                    switch (gift_id) {
                        case -1: //下注
                            temp.put("gift_name","直播遊戲");
                            break;
                        case 0: //弹幕
                            temp.put("gift_name","弹幕");
                            break;
                        case 1: //付费观看门票
                            temp.put("gift_name","付費觀看門票");
                            break;
                        case 2: //购买直播权限
                            temp.put("gift_name","购买直播权限");
                            break;
                        case 3: //视频聊天花费
                            temp.put("gift_name","视频聊天花费");
                            break;
                        case 4: //修改昵称
                            temp.put("gift_name","修改昵称");
                            break;
                        case 5: //约单
                            temp.put("gift_name","约单");
                            break;
                        case 6: //购买守护
                            temp.put("gift_name","购买守护");
                            break;
                        case 7: //竞猜
                            temp.put("gift_name","直播竞猜");
                            break;
                        default: //礼物ID
                            String gift_name = giftMap.get(LibSysUtils.toString(gift_id));
                            if(LibSysUtils.isNullOrEmpty(gift_name)){
                                GiftInfo giftInfo = giftInfoMapper.selectByPrimaryKey(gift_id);
                                if (giftInfo!=null){
                                    temp.put("gift_name",giftInfo.getName());
                                    giftMap.put(LibSysUtils.toString(gift_id),giftInfo.getName());
                                }else {
                                    temp.put("gift_name","礼物");
                                }
                            }else {
                                temp.put("gift_name",gift_name);
                            }
                            break;
                    }
                    array.add(temp);
                }
            }
            object.put("list", array);
        }

        return object;
    }



}
