package com.weking.service.user;

import com.weking.cache.UserCacheInfo;
import com.weking.cache.WKCache;
import com.weking.core.*;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.core.google.Firebase;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.level.LevelMapper;
import com.weking.mapper.log.LoginLogInfoMapper;
import com.weking.mapper.pocket.PocketInfoMapper;
import com.weking.mapper.room.RoomInfoMapper;
import com.weking.mapper.version.VersionMapper;
import com.weking.model.account.AccountInfo;
import com.weking.model.level.Level;
import com.weking.model.log.LoginLogInfo;
import com.weking.model.pocket.PocketInfo;
import com.weking.model.room.RoomInfo;
import com.weking.model.version.Version;
import com.weking.service.digital.DigitalService;
import com.weking.service.live.LiveService;
import com.weking.service.system.MsgService;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账户
 */
@Service("accountService")
public class AccountService extends LibServiceBase {
    static Logger loger = Logger.getLogger(AccountService.class);
    @Resource
    private AccountInfoMapper accountMapper;
    @Resource
    private PocketInfoMapper pocketInfoMapper;
    @Resource
    private LoginLogInfoMapper loginLogInfoMapper;
    @Resource
    private RoomInfoMapper roomInfoMapper;
    @Resource
    private SmsService smsService;
    @Resource
    private MsgService msgService;
    @Resource
    private LiveService liveService;

    @Resource
    private VersionMapper versionMapper;

    @Resource
    private LevelMapper levelMapper;

    @Resource
    private DigitalService digitalService;

    /**
     * @param type         登录方式    0：手机，1：微信，2：邮箱，3：Facebook，4：kakao, 5:google  13是apple  14是line
     * @param user_name    登录账号，手机登录时为手机号，邮箱登录时为邮箱账号，第三方登录时为第三方id
     * @param pwd          登录密码，没有时为空
     * @param nickname     昵称，没有时为空
     * @param avatar       头像地址，没有时为空
     * @param device_model 设备型号
     * @param device_type  设备类型 0：iphone，1：ipad，2：androidphone，3androidPad
     * @param imei         设备唯一标识码
     * @param client_id    个推cid
     * @param deviceToken  个推deviceToken
     * @param client_ip    客户端IP
     * @param lang_code    语言
     * @return 登录成功返回账号信息和相关配置信息
     */
    public JSONObject login(int type, String user_name, String pwd, String nickname, String avatar, String device_model, int device_type, String imei,
                            boolean force, String client_id, String deviceToken, String client_ip, String area_code, String version, String project_name,
                            String lang_code,String channel,String code ,String identityToken) {
        JSONObject object;
        JSONObject upgrade = checkVersion(device_type, version, project_name,channel);
        if (!user_name.equalsIgnoreCase("franco@sampras.hk")||!user_name.equalsIgnoreCase("91198465")) {
            if (upgrade.optInt("code") == ResultCode.must_upgrade)//强制升级不做登录，直接返回
                return upgrade;
        }
        if (!LibSysUtils.isNullOrEmpty(user_name)) {
            switch (type) {
                case C.LoginType.PHONE://手机
                    object = phoneLogin(user_name, pwd, area_code, lang_code, client_ip);
                    break;
                case C.LoginType.WECHAT://微信
                    object = wxLogin(user_name, nickname, avatar, lang_code, client_ip);
                    break;
                case C.LoginType.EMAIL://邮箱
                    object = emailLogin(user_name, pwd, lang_code);
                    break;
                case C.LoginType.FACEBOOK://facebook
                    object = fbLogin(user_name, nickname, avatar, lang_code, client_ip);
                    break;
                case C.LoginType.KAKAO://kaoko
                    object = kaLogin(user_name, nickname, avatar, lang_code, client_ip);
                    break;
                case C.LoginType.GOOGLE: //谷歌
                    object = googleLogin(user_name, nickname, avatar, lang_code, client_ip);
                    break;
                case C.LoginType.TWITTER: //推特
                    object = twitterLogin(user_name, nickname, avatar, lang_code, client_ip);
                    break;
                case C.LoginType.APPLE://APPLE
                    object = appleLogin(user_name, identityToken, code, lang_code, client_ip);
                    break;
                case C.LoginType.LINE://LINE
                    object = lineLogin(user_name, identityToken, code, lang_code, client_ip);
                    break;
                default://登录方式有误
                    object = new JSONObject();
                    object.put("code", ResultCode.account_login_type_error);
                    object.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.account.login.type.error"));
                    break;
            }
            if (object.getInt("code") == 0) {
               /* if (WKCache.get_account_num(imei) >= 3 && !WKCache.exist_device_account(imei, user_name)) {
                    return LibSysUtils.getResultJSON(ResultCode.login_over, LibProperties.getLanguage(lang_code, "weking.lang.app.login.over"));
                } else {*/
                    if (!WKCache.exist_device_account(imei, user_name)) {
                        WKCache.add_device_account(imei, user_name);
                    }
               // }
                int userId = object.getInt("userId");
                /////// 单点登录 ///////
                if (LibSysUtils.toBoolean(WKCache.get_system_cache("is.sso"))) {
                    if (!force && !checkImei(userId, imei)) {
                        return LibSysUtils.getResultJSON(ResultCode.account_login_same_user);
                    }
                    if (force) {
                        String account = WKCache.get_user(userId, C.WKCacheUserField.account);
                        sendForceLogoutMsg(userId, account);
                    }
                }
                boolean is_register = object.optBoolean("is_register");
                int award_num = object.optInt("award_num");
                /*if (LibSysUtils.isNullOrEmpty(client_id)) {
                    client_id = LibSysUtils.toString(WKCache.get_user(userId, "c_id"));
                }*/

                //更新推送信息
                updatePushInfo(userId, client_id, deviceToken, lang_code);

                //得到登录信息
                object = getLoginResult(userId, type, device_type, imei, project_name, lang_code);
                if (object.optInt("code") == 0) {//登录成功，没有被拉黑
                    if (is_register && award_num > 0) {//如果是第一次登录（注册）则判断是否有奖励钻石,如果有奖励钻石则推送消息
                        String msg = LibProperties.getLanguage(lang_code, "weking.lang." + project_name + "app.register.award");
                        msgService.sendSysMsg(object.getString("account"), msg.replace("%award%", LibSysUtils.toString(award_num)), lang_code);
                    }
                    //发送离线消息
                    msgService.againSendMsg(userId, object.getString("account"), client_id);
                    //记录登陆日志
                    recordLoginLog(userId, object.getString("access_token"), client_ip, device_type, type, device_model, imei, version);

                    if (!user_name.equalsIgnoreCase("franco@sampras.hk")) {
                        object.put("update_msg", upgrade.optString("update_msg"));
                        object.put("version_url", upgrade.optString("version_url"));
                        object.put("yingyongbao_url", "http://a.app.qq.com/o/simple.jsp?pkgname=com.kepchat.androidv4");
                        object.put("google_url", "https://play.google.com/store/apps/details?id=com.kepchat.androidv4");
                    }
                }

            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.account_login_error, LibProperties.getLanguage(lang_code, "weking.lang.account.login.error"));
        }
        return object;
    }

    //手机登录
    private JSONObject phoneLogin(String phone, String password, String area_code, String lang_code, String client_ip) {
        JSONObject object = new JSONObject();
        if (!CheckUtil.checkPhone(phone)) {
            object.put("code", ResultCode.account_phone_error);
            object.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.account.phone.error"));
        } else {
            if (password.length() == 32) {
                Integer userId = accountMapper.loginByPhone(phone, encryptPwd(password));
                if (userId == null) {
                    return LibSysUtils.getResultJSON(ResultCode.account_login_error, LibProperties.getLanguage(lang_code, "weking.lang.account.login.error"));
                }
                object.put("userId", userId);
                object.put("code", ResultCode.success);
            } else {
                object = smsService.checkCaptchat(area_code + phone, password, 1, lang_code);
                if (object.getInt("code") == ResultCode.success) {
                    Integer userId = accountMapper.loginByPhone(phone, null);
                    if (userId == null) {
                        object = register(phone, null, null, null, 0, 0, area_code, lang_code, client_ip);
                    } else {
                        object.put("userId", userId);
                        object.put("code", ResultCode.success);
                    }
                }
            }
        }
        return object;
    }


    //苹果登录
    private JSONObject appleLogin(String user_name, String identityToken, String code, String lang_code, String client_ip) {
        JSONObject object = new JSONObject();
        Jws<Claims> claimsJws = CheckUtil.getPublicKey(identityToken);
        if (claimsJws == null || claimsJws.getBody() == null) {
            System.out.println("apple:false "+code+ " identityToken===" + identityToken);
            object.put("code", ResultCode.apple_token_error);
            object.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.apple.token.error"));
        }else {
            String subject = claimsJws.getBody().getSubject();

            Integer userId = accountMapper.loginByAppleNum(user_name);
            if (userId != null) {
                object.put("code", ResultCode.success);
                object.put("userId", userId);
            } else {
                object = register(user_name, "", "", null, C.LoginType.APPLE, 0, "86", lang_code, client_ip);
            }
        }


        return object;
    }

    //邮箱登录
    private JSONObject emailLogin(String email, String password, String langCode) {
        JSONObject object = new JSONObject();
        if (!CheckUtil.checkEmail(email)) {
            object.put("code", ResultCode.account_email_error);
            object.put("msg", LibProperties.getLanguage(langCode, "weking.lang.account.mail.error"));
        } else if (!checkPassword(password)) { //验证密码
            object.put("code", ResultCode.account_pwd_error);
            object.put("msg", LibProperties.getLanguage(langCode, "weking.lang.account.pwd.error"));
        } else {
            Integer userId = accountMapper.loginByEmail(email, encryptPwd(password));
            if (userId == null) {
                object.put("msg", LibProperties.getLanguage(langCode, "weking.lang.account.login.error"));
                object.put("code", ResultCode.account_login_error);
            } else {
                object.put("code", ResultCode.success);
                object.put("userId", userId);
            }
        }
        return object;
    }

    //微信登录
    private JSONObject wxLogin(String user_name, String nickname, String avatar, String lang_code, String client_ip) {
        JSONObject object = new JSONObject();
        if (!CheckUtil.checkStrLength(user_name, 28, 28)) { //微信unionid长度限定为28位
            object.put("code", ResultCode.weixin_unionid_error);
            object.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.weixin.unionid.error"));
        } else {
            Integer userId = accountMapper.loginByWxNum(user_name);
            if (userId != null) {
                object.put("code", ResultCode.success);
                object.put("userId", userId);
            } else {
                object = register(user_name, nickname, avatar, null, C.LoginType.WECHAT, 0, "86", lang_code, client_ip);
            }
        }
        return object;
    }

    //facebook登录
    private JSONObject fbLogin(String user_name, String nickname, String avatar, String lang_code, String client_ip) {
        JSONObject object = new JSONObject();
        Integer userId = accountMapper.loginByFacebook(user_name);
        if (userId != null) {
            object.put("code", ResultCode.success);
            object.put("userId", userId);
        } else {
            object = register(user_name, nickname, avatar, null, C.LoginType.FACEBOOK, 0, "86", lang_code, client_ip);
        }
        return object;
    }

    //kakao登录
    private JSONObject kaLogin(String user_name, String nickname, String avatar, String lang_code, String client_ip) {
        JSONObject object = new JSONObject();
        Integer userId = accountMapper.loginByKakao(user_name);
        if (userId != null) {
            object.put("code", ResultCode.success);
            object.put("userId", userId);
        } else {
            object = register(user_name, nickname, avatar, null, C.LoginType.KAKAO, 0, "86", lang_code, client_ip);
        }
        return object;
    }

    //谷歌登录
    private JSONObject googleLogin(String user_name, String nickname, String avatar, String lang_code, String client_ip) {
        JSONObject object = new JSONObject();
        Integer userId = accountMapper.loginByGoogle(user_name);
        if (userId != null) {
            object.put("code", ResultCode.success);
            object.put("userId", userId);
        } else {
            object = register(user_name, nickname, avatar, null, C.LoginType.GOOGLE, 0, "86", lang_code, client_ip);
        }
        return object;
    }

    //line登录
    private JSONObject lineLogin(String user_name, String nickname, String avatar, String lang_code, String client_ip) {
        JSONObject object = new JSONObject();
        Integer userId = accountMapper.loginByLineNum(user_name);
        if (userId != null) {
            object.put("code", ResultCode.success);
            object.put("userId", userId);
        } else {
            object = register(user_name, nickname, avatar, null, C.LoginType.LINE, 0, "86", lang_code, client_ip);
        }
        return object;
    }

    //推特登录
    private JSONObject twitterLogin(String user_name, String nickname, String avatar, String lang_code, String client_ip) {
        JSONObject object = new JSONObject();
        Integer userId = accountMapper.loginByTwitter(user_name);
        if (userId != null) {
            object.put("code", ResultCode.success);
            object.put("userId", userId);
        } else {
            object = register(user_name, nickname, avatar, null, C.LoginType.TWITTER, 0, "86", lang_code, client_ip);
        }
        return object;
    }

    private JSONObject getLoginResult(int user_id, int type, int device_type, String imei, String project_name, String lang_code) {
        JSONObject result = new JSONObject();
        AccountInfo accountInfo = getAccountInfo(user_id);
        if (accountInfo.getIsblack() == 2) {
            result.put("code", ResultCode.account_isblack);
            result.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.account.isblack"));
        } else {
            int userId = accountInfo.getId();
            String token = LibSysUtils.getRandomString(16);
            if (LibSysUtils.toBoolean(WKCache.get_system_cache("weking.config.debug"))) {//debug环境下方便测试把token设置为account
                token = accountInfo.getAccount();
            }
            Map<String, String> user_cache_info = new HashMap<>();
            user_cache_info.put("user_id", LibSysUtils.toString(accountInfo.getId()));
            user_cache_info.put("account", accountInfo.getAccount());
            user_cache_info.put("avatar", accountInfo.getPicheadUrl());
            user_cache_info.put("c_id", LibSysUtils.toString(accountInfo.getClientid()));
            user_cache_info.put("device_token", LibSysUtils.toString(accountInfo.getDevicetoken()));
            user_cache_info.put("login_time", LibSysUtils.toString(LibDateUtils.getLibDateTime("yyyyMMddHHmmss")));
            user_cache_info.put("nickname", accountInfo.getNickname());
            user_cache_info.put("login_type", LibSysUtils.toString(type));
            user_cache_info.put("access_token", token);
            user_cache_info.put("lang_code", LibSysUtils.toString(lang_code));
            user_cache_info.put("level", LibSysUtils.toString(accountInfo.getLevel()));
            user_cache_info.put("lat", LibSysUtils.toString(accountInfo.getLat()));
            user_cache_info.put("lng", LibSysUtils.toString(accountInfo.getLng()));
            user_cache_info.put("experience", LibSysUtils.toString(accountInfo.getExperience()));
            user_cache_info.put("sorts", LibSysUtils.toString(accountInfo.getSorts()));
            user_cache_info.put("role", LibSysUtils.toString(accountInfo.getRole()));
            user_cache_info.put(C.WKCacheUserField.imei, imei);
            user_cache_info.put("anchor_level", LibSysUtils.toString(accountInfo.getAnchor_level()));
            user_cache_info.put("anchor_experience", LibSysUtils.toString(accountInfo.getAnchor_experience()));
            user_cache_info.put("project_name", project_name);
            user_cache_info.put("signature", accountInfo.getSigniture());
            user_cache_info.put("wallet_currency", LibSysUtils.toString(accountInfo.getWallet_currency()));
            user_cache_info.put("sex", LibSysUtils.toString(accountInfo.getSex()));
            user_cache_info.put("email", LibSysUtils.toString(accountInfo.getEmail()));
            user_cache_info.put("phone", LibSysUtils.toString(accountInfo.getPhone()));
            user_cache_info.put("post_hots", LibSysUtils.toString(accountInfo.getPost_hots()));
            user_cache_info.put("is_official", LibSysUtils.toString(accountInfo.getIs_official()));
            user_cache_info.put("hots", LibSysUtils.toString(accountInfo.getHots()));
            user_cache_info.put("user_name", LibSysUtils.toString(accountInfo.getUser_name()));
            user_cache_info.put("user_email", LibSysUtils.toString(accountInfo.getUser_email()));
            //user_cache_info.put("ratio", LibSysUtils.toString(accountInfo.getRatio()));

            user_cache_info.put("vip_level", LibSysUtils.toString(accountInfo.getVip_level()));
            user_cache_info.put("vip_experience", LibSysUtils.toString(accountInfo.getVip_experience()));
            WKCache.del_user(user_id);
            WKCache.add_user(user_id, user_cache_info);
            PocketInfo pinfo = pocketInfoMapper.selectByUserid(userId);
            if (pinfo != null) {
                result.put("my_diamonds", pinfo.getTotalDiamond());
            }
            result.put("access_token", token);
            result.put("is_official", LibSysUtils.toString(accountInfo.getIs_official()));//1是签约主播
            result.put("account", accountInfo.getAccount());
            result.put("level", accountInfo.getLevel());
            result.put("vip_level",accountInfo.getVip_level());
            result.put("anchor_level",accountInfo.getAnchor_level());
            String nickname = accountInfo.getNickname();
            String pichigh = accountInfo.getPicheadUrl();
            if (nickname == null) {
                nickname = "";
            }
            if(accountInfo.getAnchor_level()>0){
                result.put("is_anchor", true);
            }else {
                result.put("is_anchor", false);
            }
            result.put("nick_name", nickname);
            result.put("pic_head_high", WkUtil.combineUrl(pichigh, UploadTypeEnum.AVATAR, false));//大图片
            result.put("pic_head_low", WkUtil.combineUrl(pichigh, UploadTypeEnum.AVATAR, true));//小图片
            //result.put("pwd", accountInfo.getPassword());
            result.put("code", ResultCode.success);
            result.put("server_ip", WKCache.get_system_cache("weking.config.server.ip"));
            //result.put("server_ip", "http://appsme1.chidaotv.com/");
            if (device_type != 4) {
                result.put("zego_app_id", LibSysUtils.toLong(WKCache.get_system_cache("weking.config.zego.app_id")));
                result.put("zego_sign_key", WKCache.get_system_cache("weking.config.zego.sign_key"));
                result.put("zego_level", LibSysUtils.toInt(WKCache.get_system_cache("weking.config.zego.level")));
                result.put("zego_debug", LibSysUtils.toBoolean(WKCache.get_system_cache("weking.config.zego.zego_debug")));
                result.put("share_invite_url", WKCache.get_system_cache("weking.config.mobile.url") + "r.html?invitation=" + accountInfo.getInviteCode());
                result.put("share_invite_msg", LibProperties.getLanguage(lang_code, "weking.lang." + project_name + "app.invite.msg").replace("%invite_code%", accountInfo.getInviteCode()));
                result.put("share_post_title", String.format(LibProperties.getLanguage(lang_code, "weking.lang.post.sharetitle"), accountInfo.getNickname()));
                result.put("pic_server", LibSysUtils.toString(WKCache.get_system_cache("weking.config.pic.server")));
                boolean show_shop = LibSysUtils.toBoolean(WKCache.get_system_cache("show.shop"));
                if (!show_shop)
                    show_shop = accountInfo.getRole() == 2;
                result.put("show_shop", show_shop);
                int award = LibSysUtils.toInt(WKCache.get_system_cache("weking.comment.award"));
                if (award > 0) {
                    String msg = LibProperties.getLanguage(lang_code, "weking.lang." + project_name + "app.reputably");
                    msg = msg.replace("%award%", LibSysUtils.toString(award));
                    result.put("good_comment", msg);//好评提醒
                }
                //等级颜色
                JSONArray level_color = new JSONArray();

                level_color.add("9067c7");//从第0级开始
                JSONArray array = WKCache.get_user_all_level_color();
                if(array!=null){
                    result.put("level_color", array);
                }else {
                    List<Level> levelList = levelMapper.selectByAllLevel();
                    for (Level level : levelList) {
                        level_color.add(level.getColor());
                    }
                    result.put("level_color", level_color);
                    WKCache.set_user_all_level_color(level_color);
                }
                result.put("icon_type", WKCache.get_system_cache("app.icon.type"));
                result.put("weking_im", true);//是否使用weking的IM

                result.put("hide_version", WKCache.get_system_cache("WX_RECHARGE_PAY"));//苹果前端根据此版本号隐藏一些东西，比如我的收益、输入邀请码‘邀请奖励等

                if (accountInfo.getEmail().equalsIgnoreCase("franco@sampras.hk")) {
                    result.put("hide_version", 5.0);
                }

                JSONObject api_obj = new JSONObject();
                //api_obj.put("default_url", "http://appsme1.chidaotv.com/");
                api_obj.put("default_url", WKCache.get_system_cache("weking.config.server.ip"));
                api_obj.put("pay_url", WKCache.get_system_cache("weking.config.pay.url"));
                api_obj.put("pic_url", WkUtil.getPicRootUrl());
                api_obj.put("h5_url", WKCache.get_system_cache("weking.config.mobile.url"));
                result.put("api_url", api_obj);
                result.put("active_index", LibSysUtils.toInt(WKCache.get_system_cache("config.active_index")));

                JSONObject postGiftBoxConfig = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.post_gift_box_config));
                result.put("post_gift_box", postGiftBoxConfig.optBoolean("on_off"));
//                JSONObject postLikeConfig = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.post_like_share_config));
//                boolean postLikeSwitch = postLikeConfig.optBoolean("on_off");  // 是否开启点赞分享
//                result.put("post_like_share",postLikeSwitch);
                //获取用户引导发帖状态
                result.put("if_guide", WKCache.getUserGuideState(userId));
                //安卓隐藏版本号
                result.put("android_hide_version", WKCache.get_system_cache(C.WKSystemCacheField.android_hide_version));
                //推荐动态tab开关
                result.put("recommend_post_switch", LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.recommend_post_switch), false));
                WkImClient.subscribe(accountInfo.getAccount());

            }
            result.put("im_appkey", LibSysUtils.toString(LibProperties.getConfig("weking.cofing.im.appkey")));//wekingIM的key
        }
        return result;
    }

    //根据userid获取用户信息
    public AccountInfo getAccountInfo(int user_id) {
        return accountMapper.selectByPrimaryKey(user_id);
    }

    //邮箱注册
    public JSONObject emailRegister(String user_name, String nickname, String avatar, String password, String invide_code, String lang_code) {
        JSONObject object;
        if (!CheckUtil.checkEmail(user_name)) { //验证邮箱格式
            return LibSysUtils.getResultJSON(ResultCode.account_email_error, LibProperties.getLanguage(lang_code, "weking.lang.account.mail.error"));
        }
        if (verifyEmail(user_name)) { //验证邮箱是否注册
            object = LibSysUtils.getResultJSON(ResultCode.account_email_exist, LibProperties.getLanguage(lang_code, "weking.lang.account.mail.exist"));
        } else if (!checkPassword(password)) {
            object = LibSysUtils.getResultJSON(ResultCode.account_pwd_error, LibProperties.getLanguage(lang_code, "weking.lang.account.pwd.error"));
        } else {
            int parentId = 0;
            if (!LibSysUtils.isNullOrEmpty(invide_code)) {
                parentId = getParentId(invide_code);
            }
            if (parentId == -1) {
                object = LibSysUtils.getResultJSON(ResultCode.invite_code_error, LibProperties.getLanguage(lang_code, "weking.lang.invite.code.error"));
            } else {
                object = register(user_name, nickname, avatar, password, 2, parentId, "86", lang_code, "");
            }
        }
        return object;
    }

    /**
     * 手机号注册
     */
    public JSONObject phoneRegister(String phone, String password, String captcha, String areaCode, String clientIp, String langCode) {
        JSONObject object;
        if (!CheckUtil.checkPhone(phone)) {
            return LibSysUtils.getResultJSON(ResultCode.account_phone_error, LibProperties.getLanguage(langCode, "weking.lang.account.phone.error"));
        }
        if (accountMapper.loginByPhone(phone, null) != null) {
            return LibSysUtils.getResultJSON(ResultCode.account_phone_exist, LibProperties.getLanguage(langCode, "weking.lang.account.phone.exist"));
        }
        if (!checkPassword(password)) {
            return LibSysUtils.getResultJSON(ResultCode.account_pwd_error, LibProperties.getLanguage(langCode, "weking.lang.account.pwd.error"));
        }
        object = smsService.checkCaptchat(areaCode + phone, captcha, 7, langCode);
        if (object.getInt("code") == ResultCode.success) {
            object = register(phone, null, null, password, 0, 0, areaCode, langCode, clientIp);
        }
        return object.optInt("code") == ResultCode.success ? LibSysUtils.getResultJSON(ResultCode.success) : object;
    }

    //新用户注册
    @Transactional
    public JSONObject register(String user_name, String nickname, String avatar, String password, int type, int parentId, String area_code, String lang_code, String client_ip) {
        if (WKCache.get_ip_register(client_ip) >= 5) {
            return LibSysUtils.getResultJSON(ResultCode.register_over, LibProperties.getLanguage(lang_code, "weking.lang.app.register.over"));
        }
        JSONObject object = new JSONObject();
        //处理空值,用通用方法来处理不需要每次写
        AccountInfo accountInfo = new AccountInfo();
        String account = getAccount();
        accountInfo.setAccount(account);
        if (!LibSysUtils.isNullOrEmpty(nickname)) { //昵称为空account作为昵称
            accountInfo.setNickname(nickname);
        } else {
            accountInfo.setNickname(account);
        }
        switch (type) {
            case 0: { //手机号码注册
                accountInfo.setPhone(user_name);
                accountInfo.setAreaCode(LibSysUtils.toInt(area_code));
                if (!LibSysUtils.isNullOrEmpty(password)) {
                    accountInfo.setPassword(encryptPwd(password));
                }
                break;
            }
            case 1: { //微信号码注册
                if (!LibSysUtils.isNullOrEmpty(avatar)) {
                    avatar = FileUtil.saveImage(avatar, UploadTypeEnum.AVATAR);
                }
                accountInfo.setWxNum(user_name);
                break;
            }
            case 2: { //邮箱注册
                accountInfo.setEmail(user_name);
                accountInfo.setPassword(encryptPwd(password));
                break;
            }
            case 3: { //facebook注册
                if (!LibSysUtils.isNullOrEmpty(avatar)) {
                    avatar = FileUtil.saveImage(avatar, UploadTypeEnum.AVATAR);
                }
                accountInfo.setFbNum(user_name);
                break;
            }
            case 4: { //kakao
                if (!LibSysUtils.isNullOrEmpty(avatar)) {
                    avatar = FileUtil.saveImage(avatar, UploadTypeEnum.AVATAR);
                }
                accountInfo.setKakao_num(user_name);
                break;
            }
            case 5: {  //谷歌
                if (!LibSysUtils.isNullOrEmpty(avatar)) {
                    avatar = FileUtil.saveImage(avatar, UploadTypeEnum.AVATAR);
                }
                accountInfo.setGoogleNum(user_name);
                break;
            }
            case 6: //推特
                if (!LibSysUtils.isNullOrEmpty(avatar)) {
                    avatar = FileUtil.saveImage(avatar, UploadTypeEnum.AVATAR);
                }
                accountInfo.setTwitterNum(user_name);
                break;
            case 13: { //apple
                if (!LibSysUtils.isNullOrEmpty(avatar)) {
                    avatar = FileUtil.saveImage(avatar, UploadTypeEnum.AVATAR);
                }
                accountInfo.setApple_num(user_name);
                break;
            }
            case 11: { //line
                if (!LibSysUtils.isNullOrEmpty(avatar)) {
                    avatar = FileUtil.saveImage(avatar, UploadTypeEnum.AVATAR);
                }
                accountInfo.setLine_num(user_name);
                break;
            }
            default:
                break;
        }
        accountInfo.setInviteCode(getInveteCode());
        accountInfo.setParentId(parentId);
        accountInfo.setPicheadUrl(avatar);
        accountInfo.setRegTime(LibDateUtils.getLibDateTime());
        int result = accountMapper.insertSelective(accountInfo);
        if (result == 1) {//注册成功后去数据库生成账户信息
            int userId = accountInfo.getId();
            int award = LibSysUtils.toInt(WKCache.get_system_cache("weking.register.award"));//注册奖励
            if (!registerPocket(userId, award)) {
                object.put("code", ResultCode.account_pocket_error);
                object.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.account.phone_pocket_error"));
            } else {//注册成功
                WKCache.add_ip_register(client_ip, 1);
                object.put("code", ResultCode.success);
                object.put("userId", userId);
                object.put("is_register", true);
                object.put("award_num", award);
                digitalService.RegisterWallect(userId);//注册数字钱包
            }
        } else {
            object.put("code", ResultCode.account_register_error);
            object.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.account.register_error"));
        }
        return object;
    }

    //获得account
    public String getAccount() {
        boolean flag = true;
        String account = "";
        while (flag) {
            account = LibSysUtils.getRandomNum(9);
            //检测account是否存在
            int total = accountMapper.verifyAccountId(account);
            if (total == 0) {
                flag = false;
            }
        }
        return account;
    }

    //获得邀请码  数字  加 字符
    private String getInveteCode() {
        boolean flag = true;
        String inviteCode = "";
        while (flag) {
            inviteCode = LibSysUtils.getRandomInviteCode(6);
            //检测account是否存在
            Integer userId = accountMapper.selectByInviteCode(inviteCode);
            if (userId == null) {
                flag = false;
            }
        }
        return inviteCode;
    }

    //获得邀请码  纯数字
   /* private String getInveteCode() {
        boolean flag = true;
        String inviteCode = "";
        while (flag) {
            inviteCode = LibSysUtils.getRandomNum(6);
            //检测account是否存在
            Integer userId = accountMapper.selectByInviteCode(inviteCode);
            if (userId == null) {
                flag = false;
            }
            if(inviteCode.equals("666666")||inviteCode.equals("888888")||inviteCode.equals("999999")){
                flag = true;
            }
        }
        return inviteCode;
    }*/


    //通过邀请码获取邀请人
    private int getParentId(String invote_code) {
        Integer userId = accountMapper.selectByInviteCode(invote_code.toUpperCase());
        if (userId == null) {
            userId = -1;
        }
        return userId;
    }

    //验证版本


    //密码加密
    private String encryptPwd(String password) {
        return EncoderHandler.encode("SHA1", password);
    }

    //验证密码
    private Boolean checkPassword(String password) {
        return CheckUtil.checkStrLength(password, 6, 32);
    }

    //记录登录日志
    private void recordLoginLog(int userId, String access_token, String client_ip, int deviceType, int loginType, String deviceModel, String imei, String version) {
        LoginLogInfo record = new LoginLogInfo();
        record.setUserId(userId);
        record.setAccessToken(access_token);
        record.setClientIp(client_ip);
        record.setDeviceType((short) deviceType);
        record.setLoginType((short) loginType);
        record.setDeviceModel(deviceModel);
        record.setImei(imei);
        record.setLoginTime(LibDateUtils.getLibDateTime());
        record.setVersion(version);
        loginLogInfoMapper.insert(record);
    }

    private boolean verifyEmail(String email) {
        return accountMapper.verifyEmail(email) > 0;
    }

    //更新推送信息
    private void updatePushInfo(int userId, String client_id, String deviceToken, String lang_code) {
        if (!LibSysUtils.isNullOrEmpty(client_id)) {
            String cid = WKCache.get_user(userId,"c_id");
            if(cid != null && cid.equals(client_id)){
                return;
            }
            accountMapper.clearGetuiInfo(client_id);
            accountMapper.updateGetuiInfo(userId, client_id, deviceToken);
            if (!LibSysUtils.isNullOrEmpty(lang_code)) {
                if (client_id.length() == 32) {
                    GeTuiUtil.setTag(client_id, lang_code);
                } else {
                    Firebase.subscribeToTopic(client_id, lang_code);
                }
            }
        } else if (!LibSysUtils.isNullOrEmpty(deviceToken)) {
            String device_token = WKCache.get_user(userId,"device_token");
            if(device_token != null && device_token.equals(client_id)){
                return;
            }
            accountMapper.updateGetuiInfo(userId, client_id, deviceToken);
        }
    }

    //创建房间
    private void createRoom(int userId) {
        RoomInfo roomInfo = new RoomInfo();
        roomInfo.setUserId(userId);
        roomInfoMapper.insert(roomInfo);
    }

    public int setPocket(){
        int count=0;
        for (int i=1463328 ;i<=1463676;i++){
            registerPocket(i,0);
            count++;
        }
    return count;
    }


    /**
     * 注册钱包账户信息
     */
    private boolean registerPocket(int userId, int award) {
        PocketInfo pocket = new PocketInfo();
        pocket.setAll_diamond(LibSysUtils.toLong(award));
        pocket.setTotalDiamond(award);
        pocket.setFreeDiamond(0);
        pocket.setUserId(userId);
        pocket.setTotalMoney(0.0);
        pocket.setTotalTicket(0L);
        pocket.setSca_gold(new BigDecimal(0));
        pocket.setAll_sca_gold(new BigDecimal(0));
        return pocketInfoMapper.insert(pocket) > 0;
    }

    // 绑定
    public JSONObject bing(int userId, String bing_num, String area_code, int type) {
        JSONObject object;
        AccountInfo accountInfo = new AccountInfo();
        switch (type) {
            case 0://手机
                accountInfo.setPhone(bing_num);
                accountInfo.setAreaCode(LibSysUtils.toInt(area_code));
                break;
            case 1://微信
                accountInfo.setWxNum(bing_num);
                break;
            case 2://邮箱
                break;
            case 3://facebook
                accountInfo.setFbNum(bing_num);
                break;
            case 4://kakao
                accountInfo.setKakao_num(bing_num);
                break;
            case 5: //谷歌
                accountInfo.setGoogleNum(bing_num);
                break;
            case 6: //推特
                accountInfo.setTwitterNum(bing_num);
                break;
            default:
                break;
        }
        UserCacheInfo userCacheInfo = WKCache.get_user(userId);
        if (accountMapper.selectByThirdNum(accountInfo) > 0) {
            object = LibSysUtils.getResultJSON(ResultCode.account_exist, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.app.account.exist"));
        } else {
            accountInfo.setId(userId);
            int re = accountMapper.updateUserNumById(accountInfo);
            if (re > 0) {
                object = LibSysUtils.getResultJSON(ResultCode.success);
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.bing_error, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.app.bing.error"));
            }
        }
        return object;
    }

    //取消第三方绑定
    public JSONObject cancelBing(int userId, int type, String lang_code) {
        JSONObject object;
        int loginType = LibSysUtils.toInt(WKCache.get_user(userId, "login_type"));
        if (loginType != type) { //登录类型和解绑账号不能一样
            AccountInfo accountInfo = new AccountInfo();
            switch (type) {
                case 0://手机
                    break;
                case 1://微信
                    accountInfo.setWxNum("");
                    break;
                case 2://邮箱
                    break;
                case 3://facebook
                    accountInfo.setFbNum("");
                    break;
                case 4://kaoko
                    accountInfo.setKakao_num("");
                    break;
                case 5: //谷歌
                    accountInfo.setGoogleNum("");
                    break;
                case 6: //谷歌
                    accountInfo.setTwitterNum("");
                    break;
                default:
                    break;
            }
            accountInfo.setId(userId);
            int re = accountMapper.cancelBingByUserId(accountInfo);
            if (re > 0) {
                object = LibSysUtils.getResultJSON(ResultCode.success);
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.cancel_bing_error, LibProperties.getLanguage(lang_code, "weking.lang.cancel.bing.error"));
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.cancel_bing_error, LibProperties.getLanguage(lang_code, "weking.lang.cancel.bing.error"));
        }
        return object;
    }

    //绑定手机
    public JSONObject bingPhone(int userId, String phone, String code, String area_code) {
        JSONObject object;
        UserCacheInfo userCacheInfo = WKCache.get_user(userId);
        String lang_code = userCacheInfo.getLang_code();
        if (CheckUtil.checkPhone(phone)) {
            object = smsService.checkCaptchat(area_code + phone, code, 2, lang_code);
            if (object.getInt("code") == 0) {
                object = bing(userId, phone, area_code, 0);
                object.put("phone", phone);
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.account_phone_error, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.account.phone.error"));
        }
        return object;
    }

    //绑定详情
    public JSONObject bingInfo(int userId, String lang_code) {
        JSONObject object;
        AccountInfo userInfo = accountMapper.selectByPrimaryKey(userId);
        if (userInfo != null) {
            object = new JSONObject();
            boolean bingPhone = false;
            boolean bingWxNum = false;
            boolean bingFbNum = false;
            boolean bingGoogleNum = false;
            boolean bingKakaoNum = false;
            boolean bingTwitterNum = false;
            String phone = "";
            if (!LibSysUtils.isNullOrEmpty(userInfo.getPhone())) {
                bingPhone = true;
                phone = userInfo.getPhone();
            }
            if (!LibSysUtils.isNullOrEmpty(userInfo.getWxNum())) {
                bingWxNum = true;
            }
            if (!LibSysUtils.isNullOrEmpty(userInfo.getFbNum())) {
                bingFbNum = true;
            }
            if (!LibSysUtils.isNullOrEmpty(userInfo.getGoogleNum())) {
                bingGoogleNum = true;
            }
            if (!LibSysUtils.isNullOrEmpty(userInfo.getKakao_num())) {
                bingKakaoNum = true;
            }
            if (!LibSysUtils.isNullOrEmpty(userInfo.getTwitterNum())) {
                bingTwitterNum = true;
            }
            JSONObject phoneObj = new JSONObject();
            phoneObj.put("bingPhone", bingPhone);
            phoneObj.put("phone", phone);
            object.put("bingPhone", phoneObj);
            object.put("bingWxNum", bingWxNum);
            object.put("bingFbNum", bingFbNum);
            object.put("bingGoogleNum", bingGoogleNum);
            object.put("bingKakaoNum", bingKakaoNum);
            object.put("bingTwitterNum", bingTwitterNum);
            object.put("is_pwd", !LibSysUtils.isNullOrEmpty(userInfo.getPassword()));
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.account.exist_error"));
        }
        return object;
    }

    /**
     * 旧密码修改密码
     */
    public JSONObject editPwdByOld(int userId, String oldPwd, String newPwd, String langCode) {
        Integer id = accountMapper.findByPassword(userId, encryptPwd(oldPwd));
        if (id == null) {
            return LibSysUtils.getResultJSON(ResultCode.account_pwd_error, LibProperties.getLanguage(langCode, "weking.lang.account.pwd.error"));
        }
        AccountInfo record = new AccountInfo();
        record.setId(userId);
        record.setPassword(encryptPwd(newPwd));
        accountMapper.updateByPrimaryKeySelective(record);
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 手机验证码修改密码
     */
    public JSONObject editPwdByCaptcha(String phone, String pwd, String captcha, String areaCode, String langCode) {
        JSONObject object;
        if (CheckUtil.checkPhone(phone)) {
            object = smsService.checkCaptchat(areaCode + phone, captcha, 4, langCode);
            if (object.getInt("code") == 0) {
                int re = accountMapper.updatePwdByPhone(phone, encryptPwd(pwd), areaCode);
                if (re > 0) {
                    object = LibSysUtils.getResultJSON(ResultCode.success);
                }
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.account_phone_error, LibProperties.getLanguage(langCode, "weking.lang.account.phone.error"));
        }
        return object;
    }

    //退出登录
    public JSONObject logout(int userId) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        //int re = accountMapper.updateGetuiInfo(userId, "", "");
        //if (re > 0) {
        //    WKCache.del_user(userId);
        //}
        return object;
    }

    //删除用户
    public JSONObject delUser(int userId) {
        JSONObject object;
        int re = accountMapper.delUser(userId);
        if (re > 0) {
            WKCache.del_user(userId);
            object = LibSysUtils.getResultJSON(ResultCode.success);
        } else {
            UserCacheInfo userCacheInfo = WKCache.get_user(userId);
            object = LibSysUtils.getResultJSON(ResultCode.delete_error, LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.delete.error"));
        }
        return object;
    }

    //验证版本信息
    private JSONObject checkVersion(int device_type, String version, String project_name,String channel) {
        JSONObject upgrade = LibSysUtils.getResultJSON(ResultCode.success);
        if (!LibSysUtils.isNullOrEmpty(version)) {
            if (device_type != 4) {//4为web登录，不需要检查版本
                int device_type_temp = device_type == 0 || device_type == 1 ? 1 : 0;
                Version versionResult;
                if (LibSysUtils.isNullOrEmpty(channel)) {
                    versionResult = versionMapper.selectByType(device_type_temp, project_name);
                }else {
                    versionResult = versionMapper.selectByType(device_type_temp, channel);
                }
                double sys_version;
                if (device_type_temp == 1) { //苹果
                    sys_version = LibSysUtils.toDouble(versionResult.getVersionName());
                } else {
                    sys_version = LibSysUtils.toDouble(versionResult.getVersionCode());
                }
                double curr_version = LibSysUtils.toDouble(version);
                if (sys_version > curr_version) {
                    if (versionResult.getMust() == 1) {
                        upgrade.put("code", ResultCode.must_upgrade);
                    }
                    upgrade.put("update_msg", versionResult.getUpdateMsg());
                    upgrade.put("version_url", versionResult.getUrl());
                    upgrade.put("yingyongbao_url", "http://a.app.qq.com/o/simple.jsp?pkgname=com.kepchat.androidv4");
                    upgrade.put("google_url", "https://play.google.com/store/apps/details?id=com.kepchat.androidv4");
                }
            }
        }
        return upgrade;
    }

    public JSONObject recharge(String account, int diamond) {
        AccountInfo accountInfo = accountMapper.selectByAccountId(account);
        if (accountInfo != null) {
            int user_id = accountInfo.getId();
            String sql = String.format("INSERT INTO\n" +
                    "            wk_order(user_id, order_sn, trade_no, payment_code, recharge_id, amount, buy_num, currency, state, add_time)\n" +
                    "            VALUES(%d, 0, '', 9, 0, 0, %d, '', 3, %s)", user_id, diamond, LibDateUtils.getLibDateTime());
            this.getLibJdbcTemplate().update(sql);
            sql = String.format("UPDATE wk_pocket set total_diamond=total_diamond+%d,all_diamond=all_diamond+%d where user_id=%d", diamond, diamond, user_id);
            this.getLibJdbcTemplate().update(sql);

        }
        return null;
    }

    private void sendForceLogoutMsg(int userId, String account) {
        String liveIdStr = WKCache.get_user(userId, C.WKCacheUserField.live_id);
        if (!LibSysUtils.isNullOrEmpty(liveIdStr)) {
            // 正在直播，则强制退出直播
            liveService.endLive(userId, account, LibSysUtils.toInt(liveIdStr), 2);
        }

        JSONObject object = new JSONObject();
        object.put(C.ImField.im_code, IMCode.logout_force);
        object.put(C.ImField.account, account);
        loger.info("checkImei:" + userId + "," + object.toString());
        WkImClient.sendPrivateMsg(account, object.toString());
    }

    private boolean checkImei(int userId, String imei) {

        String lastImei = WKCache.get_user(userId, C.WKCacheUserField.imei);
        String accessToken = WKCache.get_user(userId, C.WKCacheUserField.access_token);
        if (lastImei == null) {
            return true;
        }
        if (!LibSysUtils.isNullOrEmpty(accessToken) && !LibSysUtils.isNullOrEmpty(imei) && !imei.equals(lastImei)) {
            // accessToken不为空，并且imei不一样，则为同一账号不同设备登录
            loger.info("checkImei:" + userId + "," + imei + "," + accessToken + "," + lastImei);
            return false;
        }
        return true;
    }
}
