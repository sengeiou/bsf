package com.weking.service.live;

import com.weking.cache.WKCache;
import com.weking.core.ResourceUtil;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.core.enums.InviteDataTypeEnum;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.mapper.invite.*;
import com.weking.model.invite.*;
import com.weking.service.pay.PocketService;
import com.weking.service.system.MsgService;
import com.weking.service.user.UserService;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.GeoRadiusResponse;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Xujm
 */
@Service("inviteService")
public class InviteService extends LibServiceBase {

    @Resource
    private InviteClassMapper inviteClassMapper;
    @Resource
    private InviteMapper inviteMapper;
    @Resource
    private PocketService pocketService;
    @Resource
    private InviteDataMapper inviteDataMapper;
    @Resource
    private InviteAppointMapper inviteAppointMapper;
    @Resource
    private UserService userService;
    @Resource
    private InviteLikeMapper inviteLikeMapper;
    @Resource
    private MsgService msgService;


    public JSONObject list(int userId,int classId,int type,int index,int count,String langCode){
        JSONObject obj = WKCache.get_user_invite_cache(index,count);
        if(obj!=null){
            return obj;
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray jsonArray;
        switch (type){
            //附近
            case 1:
                jsonArray = nearby(userId,index,count,langCode);
                break;
            case 2:
                List<Invite> list = inviteMapper.selectInviteListByClassId(classId,index,count);
                jsonArray = getInviteArray(userId,list,null,langCode);
                break;
            default:
                jsonArray = new JSONArray();
                break;
        }
        object.put("list",jsonArray);
        WKCache.set_user_invite_cache(index,count,object);
        return object;
    }

    private JSONArray getInviteArray(int userId,List<Invite> list,Double dist,String langCode){
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (Invite info:list){
            jsonObject = new JSONObject();
            jsonObject.put("account", info.getAccount());
            jsonObject.put("avatar", WkUtil.combineUrl(info.getAvatar(), UploadTypeEnum.AVATAR, true));
            jsonObject.put("nickname", info.getNickname());
            jsonObject.put("signature", info.getSigniture());
            jsonObject.put("invite_picture",info.getDataValue());
            jsonObject.put("remark",info.getRemark());
            jsonObject.put("class_id",info.getClassId());
            jsonObject.put("invite_id",info.getId());
            jsonObject.put("class_name",getInviteClassNameByClassId(info.getClassId(),langCode));
            String inviteId = LibSysUtils.toString(info.getId());
            jsonObject = appendDistance(jsonObject,userId,inviteId
                    ,dist,langCode);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /**
     * 我的邀约列表
     */
    public JSONObject myInviteList(int userId,String account,int index,int count,String langCode){
        int otherId = userId;
        if(!LibSysUtils.isNullOrEmpty(account)){
            otherId = LibSysUtils.toInt(userService.getUserFieldByAccount(account,"user_id"));
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        List<Invite> list = inviteMapper.selectInviteListByUserId(otherId,index,count);
        JSONArray jsonArray = new JSONArray();
        Map<String,String> map = userService.getUserBaseInfoByUserId(otherId);
        JSONObject jsonObject;
        for (Invite info:list){
            jsonObject = new JSONObject();
            jsonObject.put("invite_id",info.getId());
            jsonObject.put("appoint_num",info.getAppointNum());
            if(!LibSysUtils.isNullOrEmpty(info.getDataValue())){
                jsonObject.put("invite_picture",JSONArray.fromObject(info.getDataValue()));
            }
            jsonObject.put("class_name",getInviteClassNameByClassId(info.getClassId(),langCode));
            jsonObject.put("remark",info.getRemark());
            jsonObject.put("state",info.getState());
            jsonObject.put("class_id",info.getClassId());
            jsonObject.put("account", map.get("account"));
            jsonObject.put("avatar", WkUtil.combineUrl(map.get("avatar"), UploadTypeEnum.AVATAR, true));
            jsonObject.put("nickname", map.get("nickname"));
            jsonObject.put("signature", map.get("signature"));
            jsonArray.add(jsonObject);
        }
        object.put("list",jsonArray);
        return object;
    }


    /**
     * 附近邀约列表
     */
    public JSONArray nearby(int userId,int index,int count,String langCode){
        JSONArray jsonArray = new JSONArray();
        Double radius = LibSysUtils.toDouble(WKCache.get_system_cache("weking.nearby.radius"));
        Map<String,String> map = userService.getUserInfoByUserId(userId,"lng","lat");
        List<GeoRadiusResponse> geoList = WKCache.georadius(userId,LibSysUtils.toDouble(map.get("lng"))
                ,LibSysUtils.toDouble(map.get("lat")),radius);
        List<Invite> inviteList;
        Map<String,Double> radiusMap = null;
        if(geoList == null){
            inviteList = inviteMapper.selectNewInviteList(index,count);
        }else{
            if(index > 0){
                return jsonArray;
            }
            List<String> inviteIdList = new ArrayList<>();
            radiusMap = new HashMap<>();
            int i = 0;
            for (GeoRadiusResponse info : geoList){
                //附近超过一百只取前一百
                i++;
                if(i > 100){
                    break;
                }
                inviteIdList.add(info.getMemberByString());
                radiusMap.put(info.getMemberByString(),info.getDistance());
            }
            if(inviteIdList.size() <= 0){
                return jsonArray;
            }
            inviteList = inviteMapper.selectInviteListByInviteIds(inviteIdList);
        }
        JSONObject jsonObject;
        for (Invite info:inviteList){
            jsonObject = new JSONObject();
            jsonObject.put("account", info.getAccount());
            jsonObject.put("avatar", WkUtil.combineUrl(info.getAvatar(), UploadTypeEnum.AVATAR, true));
            jsonObject.put("nickname", info.getNickname());
            jsonObject.put("signature", info.getSigniture());
            jsonObject.put("invite_picture",info.getDataValue());
            jsonObject.put("remark",info.getRemark());
            jsonObject.put("class_id",info.getClassId());
            jsonObject.put("class_name",getInviteClassNameByClassId(info.getClassId(),langCode));
            jsonObject.put("invite_id",info.getId());
            String inviteId = LibSysUtils.toString(info.getId());
            if(radiusMap != null) {
                jsonObject = appendDistance(jsonObject, userId, inviteId
                        , radiusMap.get(inviteId), langCode);
            } else{
                jsonObject = appendDistance(jsonObject, userId, inviteId
                        , -1D, langCode);
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /**
     * 我的约会列表
     */
    public JSONObject myAppointmentList(int userId,Integer state,int index,int count,String langCode){
        if(state == -1){
            state = null;
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONObject jsonObject;
        List<InviteAppoint> list = inviteAppointMapper.selectAppointmentList(userId,state,index,count);
        JSONArray jsonArray = new JSONArray();
        for (InviteAppoint info:list){
            jsonObject = new JSONObject();
            jsonObject.put("account", info.getAccount());
            jsonObject.put("appoint_id", info.getId());
            jsonObject.put("avatar", WkUtil.combineUrl(info.getAvatar(), UploadTypeEnum.AVATAR, true));
            jsonObject.put("nickname", info.getNickname());
            jsonObject.put("signature", info.getSigniture());
            JSONObject detailObj = JSONObject.fromObject(info.getDetail());
            jsonObject.put("invite_picture",detailObj.optJSONArray("invite_picture"));
            jsonObject.put("remark",detailObj.optString("remark"));
            jsonObject.put("invite_id",info.getInviteId());
            int classId = detailObj.getInt("class_id");
            jsonObject.put("class_id",classId);
            jsonObject.put("class_name",getInviteClassNameByClassId(classId,langCode));
            jsonObject = appendDistance(jsonObject, userId, LibSysUtils.toString(info.getInviteId())
                    , null, langCode);
            jsonArray.add(jsonObject);
        }
        object.put("list",jsonArray);
        return object;
    }

    /**
     * 添加距离字段
     * @param dist 用户距离 -1直接返回未知，null需要计算距离
     */
    private JSONObject appendDistance(JSONObject jsonObject,int userId,String videoUserId,Double dist,String langCode){
        boolean flag = false;
        if(dist == null){
            Map<String,String> map = userService.getUserInfoByUserId(userId,"lng","lat");
            Double lng = LibSysUtils.toDouble(map.get("lng"));
            Double lat = LibSysUtils.toDouble(map.get("lat"));
            flag = lng != 0 && lat != 0;
            if(flag){
                WKCache.add_geo(userId,lng,lat);
            }

        }
        jsonObject.put("distance",userService.getUserDistance(userId,LibSysUtils.toInt(videoUserId),dist,langCode));
        if(flag){
            WKCache.delGeo(userId);
        }
        return jsonObject;
    }

    /**
     * 邀约类别列表
     */
    public JSONObject getInviteClassList(int userId,String langCode){
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        List<InviteClass> list = inviteClassMapper.selectValidInviteClassList();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (InviteClass info:list){
            jsonObject = new JSONObject();
            jsonObject.put("class_icon",info.getClassIcon());
            jsonObject.put("class_name", WkUtil.getPlatformLang(info.getClassName(),langCode));
            jsonObject.put("class_id",info.getId());
            jsonArray.add(jsonObject);
        }
        object.put("list",jsonArray);
        return object;
    }

    /**
     * 发布邀约
     */
    @Transactional
    public JSONObject release(int userId,int classId,int sex,String city,int buyWay,int sincerity
            ,String remark,long startTime,long endTime,double lng,double lat,String invitePicture,String langCode){
        if(startTime > endTime){
            return LibSysUtils.getResultJSON(ResultCode.data_error, LibProperties.getLanguage(langCode,"weking.lang.app.data.error"));
        }
        int frozenId = 0;
        if(buyWay == 1 || buyWay == 3){
            //如果是自己买单或AA冻结诚意金
            JSONObject object = pocketService.frozen(userId,sincerity,1,langCode);
            if(object.getInt("code")!=ResultCode.success){
                return object;
            }
            frozenId = object.getInt("frozen_id");
        }
        Invite record = new Invite();
        record.setUserId(userId);
        record.setFrozenId(frozenId);
        record.setClassId(classId);
        record.setBuyWay((byte)buyWay);
        record.setCity(city);
        record.setSex((byte)sex);
        record.setRemark(remark);
        record.setSincerity(sincerity);
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        record.setLat(lat);
        record.setLng(lng);
        record.setAddTime(LibDateUtils.getLibDateTime());
        int re = inviteMapper.insert(record);
        if(re > 0){
            int inviteId = record.getId();
            if(!LibSysUtils.isNullOrEmpty(invitePicture)){
                InviteData inviteData = new InviteData();
                inviteData.setDataKey(InviteDataTypeEnum.INVITE_PICTURE.getDataKey());
                inviteData.setInviteId(inviteId);
                inviteData.setDataValue(invitePicture);
                inviteDataMapper.insert(inviteData);
            }
            WKCache.add_geo(inviteId,lng,lat);
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("my_diamonds", pocketService.getUserBalance(userId));
        return object;
    }

    /**
     * 邀约详情
     */
    public JSONObject detail(int userId,int inviteId, String langCode){
        Invite info = inviteMapper.findInviteInfoById(inviteId);
        if(info == null){
            return LibSysUtils.getResultJSON(ResultCode.invite_not_exist,LibProperties.getLanguage(langCode,"weking.lang.app.invite.not.exist"));
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        String className = inviteClassMapper.findClassNameByClassId(info.getClassId());
        Map<String,String> userMap = userService.getUserBaseInfoByUserId(info.getUserId());
        object.put("account",userMap.get("account"));
        object.put("nickname",userMap.get("nickname"));
        object.put("avatar",WkUtil.combineUrl(userMap.get("avatar"), UploadTypeEnum.AVATAR, true));
        object.put("signature",userMap.get("signature"));
        object.put("like_num",info.getLikeNum());
//        int inviteState = 0;
//        InviteAppoint inviteAppoint = inviteAppointMapper.findByUserIdAndInviteId(userId,inviteId);
//        if(inviteAppoint != null){
//            if(inviteAppoint.getState() == 1 || inviteAppoint.getState() == 2){
//                inviteState = 1;
//            }
//            if(inviteAppoint.getState() == 3){
//                inviteState = 3;
//            }
//        }
//        object.put("invite_state",inviteState);
        boolean isAppoint = false;
        InviteAppoint inviteAppoint = inviteAppointMapper.findByUserIdAndInviteId(userId,inviteId);
        if(inviteAppoint != null){
            if(inviteAppoint.getState() == 1 || inviteAppoint.getState() == 2){
                isAppoint = true;
            }
        }
        object.put("is_appoint",isAppoint);
        object.put("is_like",inviteLikeMapper.findByUserIdAndInviteId(userId,info.getId())!=null);
        object.put("class_name",WkUtil.getPlatformLang(className,langCode));
        object.put("class_id",info.getClassId());
        object.put("sex",info.getSex());
        object.put("city",info.getCity());
        object.put("buy_way",info.getBuyWay());
        object.put("sincerity",info.getSincerity());
        object.put("start_time",info.getStartTime());
        object.put("end_time",info.getEndTime());
        object.put("remark",info.getRemark());
        object.put("lng",info.getLng());
        object.put("lat",info.getLat());
        object.put("invite_id",inviteId);
        object.put("invite_picture",JSONArray.fromObject(info.getDataValue()));
        return object;
    }

    /**
     * 应约
     */
    @Transactional
    public JSONObject appoint(int userId,int inviteId,String langCode){
        Invite info = inviteMapper.findInviteInfoById(inviteId);
        if(info == null || info.getState() == 0){
            return LibSysUtils.getResultJSON(ResultCode.invite_not_exist,LibProperties.getLanguage(langCode,"weking.lang.app.invite.not.exist"));
        }
        long currentTime = LibDateUtils.getLibDateTime();
        if(info.getStartTime() > currentTime){
            return LibSysUtils.getResultJSON(ResultCode.invite_not_start,LibSysUtils.getLang("weking.lang.invite.not.start"));
        }
        if(info.getEndTime() < currentTime){
            return LibSysUtils.getResultJSON(ResultCode.invite_end,LibSysUtils.getLang("weking.lang.invite.end"));
        }
        if(info.getSex() == 1 || info.getSex() == 0){
            int sex = LibSysUtils.toInt(userService.getUserInfoByUserId(userId,"sex"));
            if(info.getSex() != sex){
                String msg;
                if(info.getSex() == 0){
                    msg = String.format(LibProperties.getLanguage(langCode,"weking.lang.invite.sex")
                            ,LibProperties.getLanguage(langCode,"weking.lang.invite.sex0"));
                }else{
                    msg = String.format(LibProperties.getLanguage(langCode,"weking.lang.invite.sex")
                            ,LibProperties.getLanguage(langCode,"weking.lang.invite.sex1"));
                }
                return LibSysUtils.getResultJSON(ResultCode.invite_sex_non,msg);
            }
        }
        InviteAppoint inviteAppoint = inviteAppointMapper.findByUserIdAndInviteId(userId,inviteId);
        if(inviteAppoint != null){
            if(inviteAppoint.getState() == 1 || inviteAppoint.getState() == 2){
                return LibSysUtils.getResultJSON(ResultCode.invite_appoint_exist
                        ,LibProperties.getLanguage(langCode,"weking.lang.invite.appoint.exist"));
            }
//            if(inviteAppoint.getState() == 3){
//                return LibSysUtils.getResultJSON(ResultCode.invite_appoint_refuse
//                        ,LibProperties.getLanguage(langCode,"weking.lang.invite.appoint.refuse"));
//            }
        }
        InviteAppoint record = new InviteAppoint();
        int sincerity = getNeedSincerity(info);
        int frozenId = 0;
        if(sincerity > 0){
            JSONObject object = pocketService.frozen(userId,sincerity,1,langCode);
            if(object.getInt("code")!=ResultCode.success){
                return object;
            }
            frozenId = object.getInt("frozen_id");
        }
        record.setFrozenId(frozenId);
        record.setInviteId(inviteId);
        record.setUserId(userId);
        record.setAddTime(currentTime);
        record.setSincerity(sincerity);
        record.setState((byte)2);
        record.setOtherId(info.getUserId());
        record.setDetail(getAppointDetail(info).toString());
        int re = inviteAppointMapper.insert(record);
        if(re > 0){
            inviteMapper.increaseAppointNumById(inviteId);
            String msg = String.format(LibProperties.getLanguage(langCode,"weking.lang.invite.appoint")
                    ,userService.getUserInfoByUserId(userId,"nickname"));
            Map<String,String> userMap = userService.getUserInfoByUserId(info.getUserId(),"account","lang_code");
            msgService.sendMsg(userId,userMap.get("account"),msg,userMap.get("lang_code"));
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("my_diamonds", pocketService.getUserBalance(userId));
        return object;
    }

    /**
     * 应约人列表
     */
    public JSONObject appointUserList(int userId,int inviteId,int index,int count){
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        List<InviteAppoint> list = inviteAppointMapper.selectAppointmentUserList(inviteId,index,count);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (InviteAppoint info:list){
            jsonObject = new JSONObject();
            jsonObject.put("appoint_id",info.getId());
            jsonObject.put("account",info.getAccount());
            jsonObject.put("nickname",info.getNickname());
            jsonObject.put("avatar",WkUtil.combineUrl(info.getAvatar(), UploadTypeEnum.AVATAR, true));
            jsonObject.put("signature",info.getSigniture());
            jsonObject.put("state",info.getState());
            jsonArray.add(jsonObject);
        }
       object.put("list",jsonArray);
        return object;
    }

    /**
     * 获得需要的诚意金
     */
    private int getNeedSincerity(Invite info){
        int sincerity = 0;
        //方式对方买单及AA计算费用
        if(info.getBuyWay() == 2 || info.getBuyWay() == 3){
            sincerity = info.getSincerity();
        }
        return sincerity;
    }

    /**
     * 更新约定状态
     */
    @Transactional
    public JSONObject updateState(int userId,int appointId,int state,boolean isBuy,String langCode){
        JSONObject object;
        InviteAppoint info = inviteAppointMapper.findInfoById(appointId);
        if(info == null){
            return LibSysUtils.getResultJSON(ResultCode.invite_not_exist,LibProperties.getLanguage(langCode,"weking.lang.app.invite.not.exist"));
        }
        switch (state){
            case 1: //同意
                if(info.getState() == 2 && info.getOtherId() == userId){
                    JSONObject inviteObj = JSONObject.fromObject(info.getDetail());
                    int buyWay = inviteObj.getInt("buy_way");
                    Integer otherFrozenId = inviteObj.getInt("frozen_id");
                    if(buyWay == 1 || buyWay == 3){
                        //如果不是他付检测是否要支付
                        Integer id = inviteAppointMapper.findByInviteIdAndOtherFrozenId(info.getInviteId(),otherFrozenId);
                        if(id != null){
                            //冻结诚意金
                            int sincerity = inviteObj.optInt("sincerity",0);
                            if(isBuy){
                                object = pocketService.frozen(userId,sincerity,1,langCode);
                                if(object.getInt("code")!=ResultCode.success){
                                    return object;
                                }
                                otherFrozenId = object.getInt("frozen_id");
                            }else{
                                object = LibSysUtils.getResultJSON(ResultCode.invite_payment,getInviteBuckleMsg(buyWay,sincerity,langCode));
                                return object;
                            }
                        }
                    }
                    int re = inviteAppointMapper.updateStateById(appointId,otherFrozenId,state);
                    if(re > 0){
                        ResourceUtil.NotConfirmAppointmentMap.remove(appointId);
                        Map<String,String> userMap = userService.getUserInfoByUserId(info.getUserId(),"account","lang_code");
                        String msg = String.format(LibProperties.getLanguage(userMap.get("lang_code"),"weking.lang.invite.appoint.agree")
                                ,userService.getUserInfoByUserId(userId,"nickname"));
                        msgService.sendMsg(userId,userMap.get("account"),msg,userMap.get("lang_code"));
                    }
                }
                break;
            case 3://拒绝
                if(info.getState() == 2 && info.getOtherId() == userId){
                    if(info.getFrozenId() > 0){
                        //如果他付返还冻结诚邀金
                        pocketService.deductFrozen(info.getUserId(),0,info.getFrozenId(),false);
                    }
                    int re = inviteAppointMapper.updateStateById(appointId,null,state);
                    if(re > 0){
                        ResourceUtil.NotConfirmAppointmentMap.remove(appointId);
                        Map<String,String> userMap = userService.getUserInfoByUserId(info.getUserId(),"account","lang_code");
                        String msg = String.format(LibProperties.getLanguage(userMap.get("lang_code"),"weking.lang.invite.appoint.refuse")
                                ,userService.getUserInfoByUserId(userId,"nickname"));
                        msgService.sendMsg(userId,userMap.get("account"),msg,userMap.get("lang_code"));
                    }
                }
                break;
            case 0: //取消
                if(info.getState() == 1 || userId == 0){
                    int res = inviteAppointMapper.updateStateById(appointId,null,state);
                    if(res > 0){
                        //返还冻结诚意金
                        if(info.getFrozenId() > 0){
                            pocketService.deductFrozen(info.getUserId(),0,info.getFrozenId(),false);
                        }
                        if(info.getOtherFrozenId() > 0){
                            pocketService.deductFrozen(info.getOtherId(),0,info.getOtherFrozenId(),false);
                        }
                        if(userId == 0){
                            Map<String,String> userMap = userService.getUserInfoByUserId(info.getUserId(),"account","lang_code");
                            String msg = LibProperties.getLanguage(userMap.get("lang_code"),"weking.lang.appoint.system.cecal");
                            msgService.sendSysMsg(userMap.get("account"),msg,userMap.get("lang_code"));
//                            userMap = userService.getUserInfoByUserId(info.getOtherId(),"account","lang_code");
//                            msg = LibProperties.getLanguage(userMap.get("lang_code"),"weking.lang.appoint.system.cecal");
//                            msgService.sendSysMsg(userMap.get("account"),msg,userMap.get("lang_code"));
                        }else{
                            int otherId;
                            if(userId == info.getUserId()){
                                otherId = info.getOtherId();
                            }else{
                                otherId = info.getUserId();
                            }
                            Map<String,String> userMap = userService.getUserInfoByUserId(otherId,"account","lang_code");
                            String msg = String.format(LibProperties.getLanguage(userMap.get("lang_code"),"weking.lang.invite.appoint.cecal")
                                    ,userService.getUserInfoByUserId(userId,"nickname"));
                            msgService.sendMsg(userId,userMap.get("account"),msg,userMap.get("lang_code"));

                        }
                    }
                }
                break;
            case 4: //完成
                if(info.getState() == 1){
                    int re = inviteAppointMapper.updateStateById(appointId,null,state);
                    if(re > 0){
                        String msg = null;
                        int otherId;
                        if(userId == info.getUserId()){
                            otherId = info.getOtherId();
                        }else{
                            otherId = info.getUserId();
                        }
                        String nickname = userService.getUserInfoByUserId(userId,"nickname");
                        Map<String,String> userMap = userService.getUserInfoByUserId(otherId,"account","lang_code");
                        //扣减诚意金
                        if(info.getFrozenId() > 0){
                            JSONObject object1 = pocketService.deductFrozen(info.getUserId(),info.getOtherId(),info.getFrozenId(),true);
                            if(object1.getInt("code")==ResultCode.success){
                                if(otherId == info.getOtherId()){
                                    msg = String.format(LibProperties.getLanguage(userMap.get("lang_code"),"weking.lang.invite.appoint.complete1")
                                            ,nickname,info.getSincerity());
                                }
                            }
                        }
                        if(info.getOtherFrozenId() > 0){
                            JSONObject object2 = pocketService.deductFrozen(info.getOtherId(),info.getUserId(),info.getOtherFrozenId(),true);
                            if(object2.getInt("code")==ResultCode.success){
                                if(otherId == info.getUserId()){
                                    JSONObject detailObj = JSONObject.fromObject(info.getDetail());
                                    msg = String.format(LibProperties.getLanguage(userMap.get("lang_code"),"weking.lang.invite.appoint.complete1")
                                            ,nickname,detailObj.getInt("sincerity"));
                                }
                            }
                        }
                        if(LibSysUtils.isNullOrEmpty(msg)){
                            msg = String.format(LibProperties.getLanguage(userMap.get("lang_code"),"weking.lang.invite.appoint.complete2"),nickname);
                        }
                        msgService.sendMsg(userId,userMap.get("account"),msg,userMap.get("lang_code"));
                    }
                }
                break;
            default:
                break;
        }
        JSONObject object1 = LibSysUtils.getResultJSON(ResultCode.success);
        object1.put("my_diamonds", pocketService.getUserBalance(userId));
        return object1;
    }

    public String getInviteBuckleMsg(int buyWay,int sincerity,String langCode){
        String way = "";
        switch (buyWay){
            case 1:
                way = LibProperties.getLanguage(langCode,"weking.lang.invite.pay1");
                break;
            case 2:
                way = LibProperties.getLanguage(langCode,"weking.lang.invite.pay2");
                break;
            case 3:
                way = LibProperties.getLanguage(langCode,"weking.lang.invite.pay3");
                break;
            default:
                break;
        }
        return String.format(LibProperties.getLanguage(langCode,
                "weking.lang.invite.buckle"),way,sincerity);
    }

    private String getInviteClassNameByClassId(int classId,String langCode){
        return WkUtil.getPlatformLang(getInviteClassNameByClassId(classId),langCode);
    }


    private String getInviteClassNameByClassId(int classId){
        if(ResourceUtil.InviteClassMap.size() == 0){
            List<InviteClass> list = inviteClassMapper.selectAllInviteClassList();
            for (InviteClass info:list){
                ResourceUtil.InviteClassMap.put(info.getId(),info.getClassName());
            }
        }
        return ResourceUtil.InviteClassMap.get(classId);
    }

    private JSONObject getAppointDetail(Invite info){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sex",info.getSex());
        jsonObject.put("class_id",info.getClassId());
        jsonObject.put("remark",info.getRemark());
        jsonObject.put("invite_picture",info.getDataValue());
        jsonObject.put("city",info.getCity());
        jsonObject.put("lat",info.getLat());
        jsonObject.put("lng",info.getLng());
        jsonObject.put("start_time",info.getStartTime());
        jsonObject.put("end_time",info.getEndTime());
        jsonObject.put("buy_way",info.getBuyWay());
        jsonObject.put("frozen_id",info.getFrozenId());
        jsonObject.put("sincerity",info.getSincerity());
        return jsonObject;
    }

    /**
     * 约会详情
     */
    public JSONObject getAppointDetail(int userId,int appointId,String langCode){
        InviteAppoint info = inviteAppointMapper.findAppointmentInfo(appointId);
        if(info == null){
            return LibSysUtils.getResultJSON(ResultCode.invite_not_exist,LibProperties.getLanguage(langCode,"weking.lang.app.invite.not.exist"));
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("appoint_id",info.getId());
        object.put("account",info.getAccount());
        object.put("nickname",info.getNickname());
        object.put("avatar",WkUtil.combineUrl(info.getAvatar(), UploadTypeEnum.AVATAR, true));
        object.put("signature",info.getSigniture());
        object.put("state",info.getState());
        object.put("add_time",info.getAddTime());
        JSONObject jsonObject = JSONObject.fromObject(info.getDetail());
        object.put("class_name",getInviteClassNameByClassId(jsonObject.getInt("class_id"),langCode));
        object.putAll(jsonObject);
        return object;
    }

    /**
     * 删除约会ID
     */
    public JSONObject delAppoint(int userId,int appointId,String langCode){
        InviteAppoint inviteAppoint = inviteAppointMapper.findInfoById(appointId);
        if(inviteAppoint != null){
            if(inviteAppoint.getState() == 1 || inviteAppoint.getState() == 2){
                return LibSysUtils.getResultJSON(ResultCode.invite_appoint_exist
                        ,LibProperties.getLanguage(langCode,"weking.lang.invite.appoint.exist2"));
            }
        }
        inviteAppointMapper.deleteAppointInfo(appointId,userId);
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 删除邀约ID
     */
    public JSONObject delInvite(int userId,int inviteId,String langCode){
        InviteAppoint inviteAppoint = inviteAppointMapper.findInfoByInviteId(inviteId);
        if(inviteAppoint != null){
            return LibSysUtils.getResultJSON(ResultCode.invite_appoint_exist
                    ,LibProperties.getLanguage(langCode,"weking.lang.invite.appoint.exist1"));
        }
        Invite info = inviteMapper.findInviteInfoById(inviteId);
        if(info != null){
            if(userId == info.getUserId()){
                //冻结钱数返还
                pocketService.deductFrozen(userId,0,info.getFrozenId(),false);
                inviteMapper.deleteInviteById(inviteId,userId);
            }
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("my_diamonds", pocketService.getUserBalance(userId));
        return object;
    }


    /**
     * 修改邀约
     */
    @Transactional
    public JSONObject editInvite(int userId,int inviteId,int classId,int sex,String city,int buyWay,int sincerity
            ,String remark,long startTime,long endTime,double lng,double lat,String invitePicture,String langCode){
        if(startTime > endTime){
            return LibSysUtils.getResultJSON(ResultCode.data_error, LibProperties.getLanguage(langCode,"weking.lang.app.data.error"));
        }
        Invite record = new Invite();
        Invite info = inviteMapper.findInviteInfoById(inviteId);
        if(info == null || info.getUserId() != userId){
            return LibSysUtils.getResultJSON(ResultCode.data_error, LibProperties.getLanguage(langCode,"weking.lang.app.data.error"));
        }
        if(sincerity != info.getSincerity() || buyWay != info.getBuyWay()){
            //检测是否有待确认约会，有提示用户先确认
            if(inviteAppointMapper.findNotConfirmIdByInviteId(inviteId) != null){
                return LibSysUtils.getResultJSON(ResultCode.invite_edit_price,LibProperties.getLanguage(langCode,"weking.lang.invite.edit.price"));
            }
            //检测发布时支付的诚邀金是否支付或是否消耗，没消耗的返还
            if(info.getFrozenId() > 0){
                Integer appointId = inviteAppointMapper.findByInviteIdAndOtherFrozenId(inviteId,info.getFrozenId());
                if(appointId == null){
                    pocketService.deductFrozen(userId,0,info.getFrozenId(),false);
                }
            }
            if(buyWay == 1 || buyWay == 3){
                //如果是自己买单则扣钱
                JSONObject object = pocketService.frozen(userId,sincerity,1,langCode);
                if(object.getInt("code")!=ResultCode.success){
                    return object;
                }
                record.setFrozenId(object.getInt("frozen_id"));
            }
        }
        record.setId(inviteId);
        record.setUserId(userId);
        record.setClassId(classId);
        record.setBuyWay((byte)buyWay);
        record.setCity(city);
        record.setSex((byte)sex);
        record.setRemark(remark);
        record.setSincerity(sincerity);
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        record.setLat(lat);
        record.setLng(lng);
        record.setAddTime(LibDateUtils.getLibDateTime());
        int re = inviteMapper.updateByPrimaryKeySelective(record);
        if(re > 0){
            if(!LibSysUtils.isNullOrEmpty(invitePicture)){
                InviteData inviteData = new InviteData();
                inviteData.setDataKey(InviteDataTypeEnum.INVITE_PICTURE.getDataKey());
                inviteData.setInviteId(inviteId);
                inviteData.setDataValue(invitePicture);
                inviteDataMapper.updateDataValueByInviteId(inviteData);
            }
            WKCache.add_geo(inviteId,lng,lat);
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("my_diamonds", pocketService.getUserBalance(userId));
        return object;
    }

    public JSONObject like(int userId,int inviteId,boolean isLike,String langCode){
        Invite info = inviteMapper.findInviteInfoById(inviteId);
        if(info == null){
            return LibSysUtils.getResultJSON(ResultCode.invite_not_exist,LibProperties.getLanguage(langCode,"weking.lang.app.invite.not.exist"));
        }
        //点赞
        if(isLike){
            InviteLike record = new InviteLike();
            record.setInviteId(inviteId);
            record.setUserId(userId);
            record.setAddTime(LibDateUtils.getLibDateTime());
            try{
                int re = inviteLikeMapper.insert(record);
                if(re > 0){
                    inviteMapper.updateLikeNumByInviteId(inviteId,1);
                }
            }catch (Exception e){
                e.getMessage();
            }
        }else{
            int re = inviteLikeMapper.deleteByUserIdAndInviteId(userId,inviteId);
            if(re > 0){
                inviteMapper.updateLikeNumByInviteId(inviteId,-1);
            }
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 暂存当前所有未确认约会
     */
    public void putAllNotConfirmAppoint(){
        List<InviteAppoint> list = inviteAppointMapper.selectAllNotConfirmAppointment();
        for (InviteAppoint info:list){
            ResourceUtil.NotConfirmAppointmentMap.put(info.getId(),info.getAddTime());
        }
    }

}
