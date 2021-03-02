package com.weking.service.user;

import com.weking.cache.UserCacheInfo;
import com.weking.cache.WKCache;
import com.weking.core.DateUtils;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.follow.FollowInfoMapper;
import com.weking.mapper.log.ConsumeInfoMapper;
import com.weking.mapper.log.LiveLogInfoMapper;
import com.weking.mapper.pocket.ContributionInfoMapper;
import com.weking.mapper.pocket.PocketInfoMapper;
import com.weking.model.account.AccountInfo;
import com.weking.model.log.ConsumeInfo;
import com.weking.model.pocket.ContributionInfo;
import com.weking.model.pocket.PocketInfo;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service("randService")
public class RankService extends LibServiceBase {

    @Resource
    private AccountInfoMapper accountInfoMapper;
    @Resource
    private ContributionInfoMapper contributionInfoMapper;
    @Resource
    private ConsumeInfoMapper consumeInfoMapper;
    @Resource
    private LiveLogInfoMapper liveLogInfoMapper;
    @Resource
    private FollowInfoMapper followInfoMapper;
    @Resource
    private UserService userService;
    @Resource
    private PocketInfoMapper pocketInfoMapper;
    @Resource
    private FollowService followService;

    /**
     * @param ranking_type 排行榜类型，1=活跃榜、2=消费榜、3=粉丝榜
     * @param sort         1=月榜，2=总榜, 4=日榜 5是周榜
     */
    public JSONObject get_ranking(int userId,int ranking_type, int sort, int index, int count, String project_name, String lang_code) {
        JSONObject object;
        List<Map<String, Object>> list;
        JSONArray array = new JSONArray();
        Long month = null;
        if (sort == 1) {
            month = LibDateUtils.getLibDateTime("yyyyMM");
        }else if(sort==4){
            month = LibDateUtils.getLibDateTime("yyyyMMdd");
        }else if(sort==5){//周榜
            month = DateUtils.getMondayOfThisWeek("yyyyMMdd");
        }
        JSONObject userObj = new JSONObject();
        switch (ranking_type) {
            case 1:
                list = liveLogInfoMapper.getMyLiveTimeOrder(month, index, count);
                array = allOrderInfo(userId,list, ranking_type, lang_code);
                break;
            case 2: //消费排行
                if (month != null) {
                    if(sort==1) {
                        // list = consumeInfoMapper.getConsumeOrder(month, index, count);
                        Set<String> set = WKCache.get_consume_rank(month, index, count);
                        array = orderCacheInfo(userId, set, month, ranking_type, lang_code);
                        userObj = getUserMonthCache(userId, month, ranking_type, array);
                    }else if(sort==4){
                        Set<String> set = WKCache.get_consume_rank_day(month, index, count);
                        array = orderDayCacheInfo(userId, set, month, ranking_type, lang_code);
                        userObj = getUserDayCache(userId, month, ranking_type, array);
                    }else if(sort==5){
                        Set<String> set = WKCache.get_consume_rank_week(month, index, count);
                        array = orderWeekCacheInfo(userId, set, month, ranking_type, lang_code);
                        userObj = getUserWeekCache(userId, month, ranking_type, array);
                    }
                } else {
                    list = pocketInfoMapper.getContributionOrder(index, count);
                    array = allOrderInfo(userId,list, ranking_type, lang_code);
                    userObj = getUserAllOrder(userId,ranking_type,array);
                }
                break;
            case 3://粉丝榜
                if (month != null) {
                    if(sort==1) {
                        Set<String> set = WKCache.get_fans_rank(month, index, count);
                        array = orderCacheInfo(userId, set, month, ranking_type, lang_code);
                        userObj = getUserMonthCache(userId, month, ranking_type, array);
                    }else if(sort==4){
                        Set<String> set = WKCache.get_fans_rank_day(month, index, count);
                        array = orderDayCacheInfo(userId, set, month, ranking_type, lang_code);
                        userObj = getUserDayCache(userId, month, ranking_type, array);
                    }else if(sort==5){
                        Set<String> set = WKCache.get_fans_rank_week(month, index, count);
                        array = orderWeekCacheInfo(userId, set, month, ranking_type, lang_code);
                        userObj = getUserWeekCache(userId, month, ranking_type, array);
                    }
                } else {
                   //list = followInfoMapper.getFansOrder(null, index, count);
                   /* array = allOrderInfo(userId,null, ranking_type, lang_code);
                    userObj = getUserAllOrder(userId,ranking_type,array);*/
                }
                break;
            case 4: //收入排行
                if (month != null) {
                    if(sort==1) {
                        Set<String> set = WKCache.get_income_rank(month, index, count);
                        array = orderCacheInfo(userId, set, month, ranking_type, lang_code);
                        userObj = getUserMonthCache(userId, month, ranking_type, array);
                    }else if(sort==4){
                        Set<String> set = WKCache.get_income_rank_day(month, index, count);
                        array = orderDayCacheInfo(userId, set, month, ranking_type, lang_code);
                        userObj = getUserDayCache(userId, month, ranking_type, array);
                    }else if(sort==5){
                        Set<String> set = WKCache.get_income_rank_week(month, index, count);
                        array = orderWeekCacheInfo(userId, set, month, ranking_type, lang_code);
                        userObj = getUserWeekCache(userId, month, ranking_type, array);
                    }
                } else {
                    list = pocketInfoMapper.getTotalTicketOrder(index, count);
                    array = allOrderInfo(userId,list, ranking_type, lang_code);
                    userObj = getUserAllOrder(userId,ranking_type,array);
                }
                break;
            default:
                break;
        }
        object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", array);
        object.put("self",userObj);
        return object;
    }

    //月榜
    private JSONObject getUserMonthCache(int userId,long time,int rankingType,JSONArray array){
        int size = array.size();
        //获取最后一个对象，不为空代表该用户在榜上
        if(size > 0){
            JSONObject jsonObject = (JSONObject) array.get(size - 1);
            array.remove(size - 1);
            if(jsonObject.size() > 0){
                //jsonObject.put("order",jsonObject.optString("order","").replace("NO.",""))
                return jsonObject;
            }
        }
        String account = userService.getUserInfoByUserId(userId,"account");
        JSONObject jsonObj = getUserCache(userId,account,time,rankingType);
        if(jsonObj != null){
            jsonObj.put("order",0);
        }
        return jsonObj;
    }

    //日榜
    private JSONObject getUserDayCache(int userId,long time,int rankingType,JSONArray array){
        int size = array.size();
        //获取最后一个对象，不为空代表该用户在榜上
        if(size > 0){
            JSONObject jsonObject = (JSONObject) array.get(size - 1);
            array.remove(size - 1);
            if(jsonObject.size() > 0){
                //jsonObject.put("order",jsonObject.optString("order","").replace("NO.",""))
                return jsonObject;
            }
        }
        String account = userService.getUserInfoByUserId(userId,"account");
        JSONObject jsonObj = getUserDayCache(userId,account,time,rankingType);
        if(jsonObj != null){
            jsonObj.put("order",0);
        }
        return jsonObj;
    }


    //周榜
    private JSONObject getUserWeekCache(int userId,long time,int rankingType,JSONArray array){
        int size = array.size();
        //获取最后一个对象，不为空代表该用户在榜上
        if(size > 0){
            JSONObject jsonObject = (JSONObject) array.get(size - 1);
            array.remove(size - 1);
            if(jsonObject.size() > 0){
                //jsonObject.put("order",jsonObject.optString("order","").replace("NO.",""))
                return jsonObject;
            }
        }
        String account = userService.getUserInfoByUserId(userId,"account");
        JSONObject jsonObj = getUserWeekCache(userId,account,time,rankingType);
        if(jsonObj != null){
            jsonObj.put("order",0);
        }
        return jsonObj;
    }


    //排行榜 月榜
    private JSONObject getUserCache(int userId,String account,long time,int rankingType){
        JSONObject singleInfo = getUserOrderInfo(userId,account);
        if(singleInfo == null){
            return null;
        }
        int qty = 0;
        switch (rankingType) {
            case 2://消费
                Double consumeNum = WKCache.get_consume_num(time, account);
                qty = consumeNum == null?0:consumeNum.intValue();
                break;
            case 3://粉丝
                Double fansNum = WKCache.get_fans_num(time,account);
                qty = fansNum == null || fansNum < 0?0:fansNum.intValue();
                break;
            case 4://收入
                Double incomeNum = WKCache.get_income_num(time, account);
                qty = incomeNum == null?0:incomeNum.intValue();
                break;
            default:
                break;
        }
        singleInfo.put("qty",qty);
        return singleInfo;
    }

    //排行榜 日榜榜
    private JSONObject getUserDayCache(int userId,String account,long time,int rankingType){
        JSONObject singleInfo = getUserOrderInfo(userId,account);
        if(singleInfo == null){
            return null;
        }
        int qty = 0;
        switch (rankingType) {
            case 2://消费
                Double consumeNum = WKCache.get_consume_num_day(time, account);
                qty = consumeNum == null?0:consumeNum.intValue();
                break;
            case 3://粉丝
                Double fansNum = WKCache.get_fans_num_day(time,account);
                qty = fansNum == null || fansNum < 0?0:fansNum.intValue();
                break;
            case 4://收入
                Double incomeNum = WKCache.get_income_num_day(time, account);
                qty = incomeNum == null?0:incomeNum.intValue();
                break;
            default:
                break;
        }
        singleInfo.put("qty",qty);
        return singleInfo;
    }

    //排行榜 周榜
    private JSONObject getUserWeekCache(int userId,String account,long time,int rankingType){
        JSONObject singleInfo = getUserOrderInfo(userId,account);
        if(singleInfo == null){
            return null;
        }
        int qty = 0;
        switch (rankingType) {
            case 2://消费
                Double consumeNum = WKCache.get_consume_num_week(time, account);
                qty = consumeNum == null?0:consumeNum.intValue();
                break;
            case 3://粉丝
                Double fansNum = WKCache.get_fans_num_week(time,account);
                qty = fansNum == null || fansNum < 0?0:fansNum.intValue();
                break;
            case 4://收入
                Double incomeNum = WKCache.get_income_num_week(time, account);
                qty = incomeNum == null?0:incomeNum.intValue();
                break;
            default:
                break;
        }
        singleInfo.put("qty",qty);
        return singleInfo;
    }


    private JSONObject getUserAllOrder(int userId,int rankType,JSONArray array){
        JSONObject jsonObj = getUserMonthCache(userId,0,0,array);
        if(jsonObj.optInt("order",0) == 0){
            int qty = 0;
            PocketInfo pocketInfo;
            switch (rankType) {
                case 2://消费
                    pocketInfo = pocketInfoMapper.selectByUserid(userId);
                    if(pocketInfo != null){
                        qty = LibSysUtils.toInt(pocketInfo.getAll_diamond() - pocketInfo.getTotalDiamond());
                    }
                    break;
                case 3://粉丝
                    qty = followInfoMapper.getFansNum(userId);
                    break;
                case 4://收入
                    pocketInfo = pocketInfoMapper.selectByUserid(userId);
                    if(pocketInfo != null){
                        qty = LibSysUtils.toInt(pocketInfo.getTotalTicket());
                    }
                    break;
                default:
                    break;
            }
            jsonObj.put("qty",qty);
        }
        return jsonObj;
    }

    private JSONObject getUserOrderInfo(int userId,String account){
        JSONObject singleInfo = new JSONObject();
        Map<String, String> map = userService.getUserInfoByAccount(account, "user_id","account", "avatar", "nickname", "level");
        String pichigh = WkUtil.combineUrl(map.get("avatar"), UploadTypeEnum.AVATAR, true);
        int otherId = LibSysUtils.toInt(map.get("user_id"));
        singleInfo.put("account", map.get("account"));
        singleInfo.put("avatar", pichigh);
        singleInfo.put("nickname", map.get("nickname"));
        singleInfo.put("sex", 0);
        singleInfo.put("level", map.get("level"));
        if(userId != otherId){
            singleInfo.put("follow_state", followService.getFollowStatus(userId,otherId));
        }
        return singleInfo;
    }
    //缓存取月排行榜
    private JSONArray orderCacheInfo(int userId,Set<String> set, long time, int ranking_type, String lang_code) {
        JSONObject singleInfo;
        JSONArray array = new JSONArray();
        JSONObject userObj = new JSONObject();
        if (set.size() > 0) {
            int i = 1;
            for (String account : set) {
                singleInfo = getUserCache(userId,account,time,ranking_type);
                if (singleInfo != null) {
                    singleInfo.put("order", LibSysUtils.toString(i));
                    array.add(singleInfo);
                }
                if(userId == LibSysUtils.toInt(userService.getUserFieldByAccount(account,"user_id"))){
                    if(singleInfo != null){
                        userObj = singleInfo;
                    }else{
                        userObj = new JSONObject();
                    }
                }
                i++;
            }
        }
        array.add(userObj);
        return array;
    }

    //缓存取日排行榜
    private JSONArray orderDayCacheInfo(int userId,Set<String> set, long time, int ranking_type, String lang_code) {
        JSONObject singleInfo;
        JSONArray array = new JSONArray();
        JSONObject userObj = new JSONObject();
        if (set.size() > 0) {
            int i = 1;
            for (String account : set) {
                singleInfo = getUserDayCache(userId,account,time,ranking_type);
                if (singleInfo != null) {
                    singleInfo.put("order", LibSysUtils.toString(i));
                    array.add(singleInfo);
                }
                if(userId == LibSysUtils.toInt(userService.getUserFieldByAccount(account,"user_id"))){
                    if(singleInfo != null){
                        userObj = singleInfo;
                    }else{
                        userObj = new JSONObject();
                    }
                }
                i++;
            }
        }
        array.add(userObj);
        return array;
    }


    //缓存取周排行榜
    private JSONArray orderWeekCacheInfo(int userId,Set<String> set, long time, int ranking_type, String lang_code) {
        JSONObject singleInfo;
        JSONArray array = new JSONArray();
        JSONObject userObj = new JSONObject();
        if (set.size() > 0) {
            int i = 1;
            for (String account : set) {
                singleInfo = getUserWeekCache(userId,account,time,ranking_type);
                if (singleInfo != null) {
                    singleInfo.put("order", LibSysUtils.toString(i));
                    array.add(singleInfo);
                }
                if(userId == LibSysUtils.toInt(userService.getUserFieldByAccount(account,"user_id"))){
                    if(singleInfo != null){
                        userObj = singleInfo;
                    }else{
                        userObj = new JSONObject();
                    }
                }
                i++;
            }
        }
        array.add(userObj);
        return array;
    }



    //总排行榜
    private JSONArray allOrderInfo(int userId,List<Map<String, Object>> list, int ranking_type, String lang_code) {
        int size = list.size();
        JSONArray array = new JSONArray();
        JSONObject userObj = new JSONObject();
        if (size > 0) {
            JSONObject temp;
            for (int i = 0; i < size; i++) {
                temp = new JSONObject();
                Map<String, Object> map = list.get(i);
                int otherId = LibSysUtils.toInt(map.get("id"));
                temp.put("account", LibSysUtils.toString(map.get("account")));
                temp.put("avatar", WkUtil.combineUrl(LibSysUtils.toString(map.get("avatar")), UploadTypeEnum.AVATAR, true));
                temp.put("nickname", LibSysUtils.toString(map.get("nickname")));
                temp.put("level",LibSysUtils.toString(map.get("level")));
                temp.put("follow_state", followService.getFollowStatus(userId,otherId));
                if (ranking_type == 1) {
                    temp.put("qty", LibDateUtils.diffDatetime(LibSysUtils.toLong(map.get("diff"))));
                }else if (ranking_type == 2) {
                    temp.put("qty", LibSysUtils.toString(map.get("diff")));
                }else {
                    temp.put("qty", LibSysUtils.toString(map.get("diff")));
                }
                temp.put("order", LibSysUtils.toString(i + 1));
                if(userId == otherId){
                    userObj = temp;
                }
                array.add(temp);
            }
        }
        array.add(userObj);
        return array;
    }

    //获得主播贡献榜
    public JSONObject getContributionList(int userId, String account, int index, int count,int type) {
        JSONObject obj = WKCache.get_anchor_rank_list(userId, account, index, count, type);
        if(obj!=null){
            return obj;
        }
        JSONObject object;
        UserCacheInfo userCacheInfo = WKCache.get_user(userId);
        //根据account 获取 userId
        AccountInfo accountInfo = accountInfoMapper.selectByAccountId(account);
        Long month = null;
        if (type == 2) {//月榜
            month = LibDateUtils.getLibDateTime("yyyyMM");
        }else if(type==0){//日榜
            month = LibDateUtils.getLibDateTime("yyyyMMdd");
        }else if(type==1){//周榜
            month = DateUtils.getMondayOfThisWeek("yyyyMMdd");
        }
        if (accountInfo == null) {
            object = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.account.exist_error"));
        } else {
            int anchorId = accountInfo.getId();
            JSONArray jsonArray = new JSONArray();
            JSONObject userObj = new JSONObject();
            switch (type) {
                case 3://总榜
                List<ContributionInfo> list = contributionInfoMapper.selectContributionUserList(anchorId, index, count);
                int size = list.size();
                if (size > 0) {
                    JSONObject singleInfo;
                    for (int i = 0; i < size; i++) {
                        singleInfo = new JSONObject();
                        ContributionInfo info = list.get(i);
                        String pichigh = WkUtil.combineUrl(info.getPicheadUrl(), UploadTypeEnum.AVATAR, true);
                        singleInfo.put("account", info.getAccount());
                        singleInfo.put("pic_head_low", pichigh);
                        singleInfo.put("avatar", pichigh);
                        singleInfo.put("ticket_count", info.getSendTotalTicket());
                        singleInfo.put("nickname", info.getNickname());
                        singleInfo.put("qty", info.getSendTotalTicket());
                        singleInfo.put("sex", info.getSex());
                        singleInfo.put("order", LibSysUtils.toString(i + 1 + index));
                        singleInfo.put("level", info.getLevel());
                        if (userId != info.getSendId()) {
                            singleInfo.put("follow_state", followService.getFollowStatus(userId, info.getSendId()));
                        }
                        if (userId == info.getSendId()) {
                            userObj = singleInfo;
                        }
                        jsonArray.add(singleInfo);
                    }
                }
                    break;
                case 0: //日榜
                    Set<String> sets = WKCache.get__bio_income_rank_day(month,anchorId, index, count);
                    jsonArray = anchorCacheInfo(anchorId, sets, month, type,index);
                    break;
                case 1: //周榜
                    Set<String> setWeek = WKCache.get__bio_income_rank_week(month,anchorId, index, count);
                    jsonArray = anchorCacheInfo(anchorId, setWeek, month, type,index);
                    break;
                case 2: //月榜
                    Set<String> set = WKCache.get_bio_consume_rank(month,anchorId, index, count);
                    jsonArray = anchorCacheInfo(anchorId, set, month, type,index);
                    //userObj = getUserMonthCache(userId, month, ranking_type, array);
                    break;
            }
            if(userObj.size() == 0){
                userObj = getUserOrderInfo(anchorId,userCacheInfo.getAccount());
                userObj.put("order",0);
            }
            object = LibSysUtils.getResultJSON(ResultCode.success);
            object.put("list", jsonArray);
            object.put("self", userObj);

        }
        WKCache.set_anchor_rank_list(userId,account,index, count,type,object);
        return object;
    }

    //当前本场直播消费榜
    public JSONObject getCurrentConsumptionListEx(int userId, int live_id, int index, int count) {
        JSONObject object;
        JSONArray jsonArray = new JSONArray();
        if (live_id > 0) {
            List<ConsumeInfo> list = consumeInfoMapper.getCurrentConsumptionList(live_id, index, count);
            JSONObject singleInfo;
            int size = list.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    singleInfo = new JSONObject();
                    ConsumeInfo info = list.get(i);
                    String pichigh = WkUtil.combineUrl(info.getPicheadUrl(), UploadTypeEnum.AVATAR, true);
                    singleInfo.put("account", info.getAccount());
                    singleInfo.put("pic_head_low", pichigh);
                    singleInfo.put("ticket_count", info.getSendDiamond());
                    singleInfo.put("nickname", info.getNickname());
                    singleInfo.put("sex", info.getSex());
                    singleInfo.put("level", info.getLevel());
                    singleInfo.put("qty", info.getSendDiamond());
                    singleInfo.put("order", LibSysUtils.toString(i + 1));
                    jsonArray.add(singleInfo);
                }
            }
        }
        object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", jsonArray);
        return object;
    }

    //当前本场直播消费榜
    public JSONObject getCurrentConsumptionList(int userId, int live_id, int index, int count) {
        JSONObject object;
        JSONArray jsonArray = new JSONArray();
        JSONObject userObj = new JSONObject();
        if (live_id > 0) {
            Set<String> list = WKCache.get_room_rank(live_id, index, count);
            JSONObject singleInfo;
            int size = list.size();
            if (size > 0) {
                int i = 1;
                for (String account : list) {
                    singleInfo = new JSONObject();
                    Map<String,String> info = userService.getUserInfoByAccount(account, "account", "avatar", "nickname", "level","user_id");
                    int sendDiamond = WKCache.get_send_iamond(live_id, account).intValue();
                    String pichigh = WkUtil.combineUrl(info.get("avatar"), UploadTypeEnum.AVATAR, true);
                    singleInfo.put("account", info.get("account"));
                    singleInfo.put("pic_head_low", pichigh);
                    singleInfo.put("avatar", pichigh);
                    singleInfo.put("ticket_count", sendDiamond);
                    singleInfo.put("nickname", info.get("nickname"));
                    singleInfo.put("sex", 0);
                    singleInfo.put("level", info.get("level"));
                    singleInfo.put("qty", sendDiamond);
                    singleInfo.put("order", LibSysUtils.toString(i+index));
                    if(userId == LibSysUtils.toInt(info.get("user_id"))){
                        userObj = singleInfo;
                    }
                    jsonArray.add(singleInfo);
                    i++;
                }
            }
        }
        if(userObj.size() == 0){
            String account = userService.getUserInfoByUserId(userId,"account");
            userObj = getUserOrderInfo(userId,account);
            Double sendDiamond = WKCache.get_send_iamond(live_id, account);
            userObj.put("qty",sendDiamond == null?0:sendDiamond.intValue());
            userObj.put("order",0);
        }
        object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", jsonArray);
        object.put("self",userObj);
        return object;
    }

    //获取活动排行榜
    public JSONObject getGameList(String tagName, int live_id, String project_name, int index, int count) {
        int ranking_type = 2;
        int sort = 1;
        JSONObject object;
        List<Map<String, Object>> list;
        Long month = null;
        if (sort == 1)
            month = LibDateUtils.getLibDateTime("yyyyMM");
        switch (ranking_type) {
            case 1:
                list = liveLogInfoMapper.getMyLiveTimeOrder(month, index, count);
                break;
            case 2: //消费排行
                if (month != null) {
                    list = consumeInfoMapper.getConsumeOrder(month, index, count);
                } else {
                    list = contributionInfoMapper.getContributionOrder(index, count);
                }
                break;
            case 3://粉丝榜
                list = followInfoMapper.getFansOrder(month, index, count);
                break;
            case 4: //收入排行
                if (month != null) {
                    list = consumeInfoMapper.getIncomeOrder(month, index, count);
                } else {
                    list = contributionInfoMapper.getAllIncomeOrder(index, count);
                }
                break;
            default:
                list = new ArrayList<>();
                break;
        }
        int size = list.size();
        JSONArray array = new JSONArray();
        if (size > 0) {
            JSONObject temp;
            for (int i = 0; i < size; i++) {
                temp = new JSONObject();
                Map<String, Object> map = list.get(i);
                temp.put("account", LibSysUtils.toString(map.get("account")));
                temp.put("pic_head_low", WkUtil.combineUrl(LibSysUtils.toString(map.get("avatar")), UploadTypeEnum.AVATAR, true));
                temp.put("nickname", LibSysUtils.toString(map.get("nickname")));
                if (ranking_type == 1)
                    temp.put("ticket_count", LibDateUtils.diffDatetime(LibSysUtils.toLong(map.get("diff"))));
                else if (ranking_type == 2)
                    temp.put("ticket_count", LibSysUtils.toString(map.get("diff")) + LibProperties.getLanguage("zh_TW", "weking.lang." + project_name + "app.mony"));
                else if (ranking_type == 3 || ranking_type == 4)
                    temp.put("ticket_count", LibSysUtils.toString(map.get("diff")));
                temp.put("order", LibSysUtils.toString(i + 1));
                array.add(temp);
            }
        }
        object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", array);
        return object;
    }

    public JSONObject getDayRank(int ranking_type, long beginTime, long endTime, int index, int count,String giftStr,Integer level_type) {
        List<Map<String, Object>> list;
        List<Integer> giftIdList=new ArrayList<>();
        if(!"0".equals(giftStr)) {
            String[] str = giftStr.split(",");
            for (int i = 0; i < str.length; i++) {
                giftIdList.add(LibSysUtils.toInt(str[i]));
            }
        }
        switch (ranking_type) {
            //收入排行
            case 1:
                list = consumeInfoMapper.getIncomeOrderByDay(beginTime, endTime, index, count);
                break;
            case 2: //消费排行
                list = consumeInfoMapper.getConsumeOrderByDay(beginTime, endTime, index, count);
                break;
            //七夕 收入排行
            case 3:
                if (level_type==0) {
                    list = consumeInfoMapper.getIncomeOrderBySection(beginTime, endTime, index, count, giftIdList);
                }else  if(level_type==1){
                    list = consumeInfoMapper.getIncomeOrderBySectionAndLevel(beginTime, endTime, index, count, giftIdList);
                }else {
                    list = consumeInfoMapper.getIncomeOrderBySectionAndAnchorLevel(beginTime, endTime, index, count, giftIdList);
                }
                break;
            //七夕 消费排行
            case 4:
                list = consumeInfoMapper.getConsumeOrderBySection(beginTime, endTime, index, count,giftIdList);
                break;
            default:
                list = new ArrayList<>();
                break;
        }
        int size = list.size();
        JSONArray array = new JSONArray();
        if (size > 0) {
            JSONObject temp;
            for (int i = 0; i < size; i++) {
                temp = new JSONObject();
                Map<String, Object> map = list.get(i);
                temp.put("account", LibSysUtils.toString(map.get("account")));
                temp.put("pic_head_low", WkUtil.combineUrl(LibSysUtils.toString(map.get("avatar")), UploadTypeEnum.AVATAR, true));
                temp.put("nickname", LibSysUtils.toString(map.get("nickname")));
                temp.put("ticket_count", LibSysUtils.toString(map.get("diff")));
                temp.put("order", LibSysUtils.toString(i + 1));
                array.add(temp);
            }
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", array);
        return object;
    }


    //获取指定时间获得礼物的个数

    public JSONObject getGiftNumRank(int giftId, long beginTime, long endTime, int index, int count,int type) {
        List<Map<String, Object>> list;
        switch (type) {
            //仙女活动
            case 0:
                list = consumeInfoMapper.getGiftNumRank(giftId,beginTime, endTime, index, count);
                break;
            case 1: //端午节活动
                list = consumeInfoMapper.getGiftCountBoat(beginTime, endTime, index, count);
                break;
            //七夕 收入排行
            case 2:
                list = consumeInfoMapper.getIncomeOrderBySectionOne(giftId,beginTime, endTime, index, count);
                break;
            //七夕 消费排行
            case 3:
                list = consumeInfoMapper.getConsumeOrderBySectionOne(giftId,beginTime, endTime, index, count);
                break;
            default:
                list = new ArrayList<>();
                break;
        }

        int size = list.size();
        JSONArray array = new JSONArray();
        if (size > 0) {
            JSONObject temp;
            for (int i = 0; i < size; i++) {
                temp = new JSONObject();
                Map<String, Object> map = list.get(i);
                temp.put("account", LibSysUtils.toString(map.get("account")));
                temp.put("pic_head_low", WkUtil.combineUrl(LibSysUtils.toString(map.get("avatar")), UploadTypeEnum.AVATAR, true));
                temp.put("nickname", LibSysUtils.toString(map.get("nickname")));
                temp.put("ticket_count", LibSysUtils.toString(map.get("diff")));
                temp.put("order", LibSysUtils.toString(i + 1));
                array.add(temp);
            }
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", array);
        return object;
    }


    public JSONObject rankCache(int type, int index, int count) {
        Long month = LibDateUtils.getLibDateTime("yyyyMM");
        Long day = LibDateUtils.getLibDateTime("yyyyMMdd");
        Long weekTime = DateUtils.getMondayOfThisWeek("yyyyMMdd");
        Map<String,Double> scoreMembers;
        List<Map<String, Object>> list;
        switch (type) {
            case 1: //消费
                list = consumeInfoMapper.getConsumeOrder(month, index, count);
                break;
            case 2: //收入
                list = consumeInfoMapper.getIncomeOrder(month, index, count);
                break;
            case 3: //粉丝
                list = followInfoMapper.getFansOrder(month, index, count);
                break;
            default:
                list = new ArrayList<>();
                break;
        }
        scoreMembers = new HashMap<>();
        int size = list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Map<String, Object> map = list.get(i);
                scoreMembers.put(LibSysUtils.toString(map.get("account")),LibSysUtils.toDouble(map.get("diff")));
            }
        }
        if (type == 1) {
            WKCache.add_consume_list(month, scoreMembers);
            WKCache.add_consume_list_day(day, scoreMembers);
            WKCache.add_consume_list_week(weekTime, scoreMembers);
        } else if (type == 2) {
            WKCache.add_income_list(month, scoreMembers);
            WKCache.add_income_list_day(day, scoreMembers);
            WKCache.add_income_list_week(weekTime, scoreMembers);//周榜

        } else if (type == 3) {
            WKCache.add_fans_list(month, scoreMembers);
            WKCache.add_fans_list_day(day, scoreMembers);
            WKCache.add_fans_list_week(weekTime, scoreMembers);//周榜
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    //缓存取 主播收入排行榜 0 日榜 1周榜 2月榜
    private JSONArray anchorCacheInfo(int anchorId,Set<String> set, long time, int type,int index) {
        JSONObject singleInfo;
        JSONArray array = new JSONArray();
        JSONObject userObj = new JSONObject();
        if (set.size() > 0) {
            int i = 1;
            for (String account : set) {
                singleInfo = getAnchorCache(anchorId,account,time,type);
                if (singleInfo != null) {
                    singleInfo.put("order", LibSysUtils.toString(i+index));
                    array.add(singleInfo);
                }
                if(anchorId == LibSysUtils.toInt(userService.getUserFieldByAccount(account,"user_id"))){
                    if(singleInfo != null){
                        userObj = singleInfo;
                    }
                }
                i++;
            }
        }
        if(userObj.size()>0) {
            array.add(userObj);
        }
        return array;
    }


    //排行榜 月榜
    private JSONObject getAnchorCache(int anchorId,String account,long time,int type){
        JSONObject singleInfo = getUserOrderInfo(anchorId,account);
        if(singleInfo == null){
            return null;
        }
        int qty = 0;
        switch (type) {
            case 0://日榜 收入
                Double consumeNum = WKCache.get__bio_income_num_day(time,anchorId, account);
                qty = consumeNum == null?0:consumeNum.intValue();
                break;
            case 1://周榜收入
                Double incomeeWeek = WKCache.get__bio_income_num_week(time,anchorId, account);
                qty = incomeeWeek == null || incomeeWeek < 0?0:incomeeWeek.intValue();
                break;
            case 2://月榜收入
                Double incomee = WKCache.get_bio_incomee_num(time,anchorId, account);
                qty = incomee == null || incomee < 0?0:incomee.intValue();
                break;
            default:
                break;
        }
        singleInfo.put("qty",qty);
        return singleInfo;
    }

}
