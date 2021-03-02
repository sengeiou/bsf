package com.weking.controller.user;

import com.weking.cache.WKCache;
import com.weking.controller.out.OutControllerBase;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.service.user.AccountService;
import com.weking.service.user.SmsService;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO 2017/2/14.
 */
@Controller
@RequestMapping({"/account", "/user/account"})
public class AccountController extends OutControllerBase {

    @Resource
    private AccountService accountService;
    @Resource
    private SmsService smsService;

    @RequestMapping("/login")
    public void userLogin(HttpServletRequest request, HttpServletResponse response) {
        String client_ip = WkUtil.getIpAddr(request);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        int type = LibSysUtils.toInt(request.getParameter("type"));
        String user_name = getParameter(request, "user_name");
        String pwd = getParameter(request, "pwd");
        String nickname = getParameter(request, "nickname","");
        if(!LibSysUtils.isNullOrEmpty(nickname)) {
            nickname = WkUtil.urlDecode(nickname);
        }
        String avatar = getParameter(request, "avatar");
        String device_model = getParameter(request, "device_model");
        int device_type = LibSysUtils.toInt(getParameter(request, "device_type"));
        String imei = getParameter(request, "imei");
        String client_id = getParameter(request, "client_id");
        client_id = WkUtil.urlDecode(client_id);
        String deviceToken = getParameter(request, "deviceToken");
        String area_code = getParameter(request, "area_code");
        String lang_code = getParameter(request, "lang_code");
        String project_name = getParameter(request, "project_name", "");
        String channel = getParameter(request, "channel", "");
        boolean force = getParameter(request, "force", false);
        String version = getParameter(request, "version");
        String code = getParameter(request, "code", "");
        String identityToken = getParameter(request, "identityToken", "");

        JSONObject result = accountService.login(type, user_name, pwd, nickname, avatar, device_model, device_type, imei, force,
                client_id, deviceToken, client_ip, area_code, version, project_name, lang_code,channel,code,identityToken);
        this.out(response, result,api_version);
    }

    /**
     * 邮箱注册
     */
    @RequestMapping("/emailRegister")
    public void emailRegister(HttpServletRequest request, HttpServletResponse response) {
        String email = getParameter(request, "email");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String nickname = getParameter(request, "nickname");
        String avatar = getParameter(request, "avatar");
        String pwd = getParameter(request, "pwd", "");
        String confirm_pwd = getParameter(request, "confirm_pwd", "");
        String invite_code = getParameter(request, "invite_code");
        String lang_code = getParameter(request, "lang_code");
        JSONObject object;
        if (!pwd.equals(confirm_pwd)) {
            object = new JSONObject();
            object.put("code", ResultCode.account_passwords_differ);
            object.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.account.passwords.differ"));
        } else {
            object = accountService.emailRegister(email, nickname, avatar, pwd, invite_code, lang_code);
        }
        this.out(response, object,api_version);
    }

    /**
     * 手机号注册
     */
    @RequestMapping("/phoneRegister")
    public void phoneRegister(HttpServletRequest request, HttpServletResponse response) {
        String phone = getParameter(request, "phone");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String pwd = getParameter(request, "pwd", "");
        String captcha = getParameter(request, "captcha", "");
        String langCode = getParameter(request, "lang_code");
        String areaCode = getParameter(request, "area_code", "86");
        String clientIp = WkUtil.getIpAddr(request);
        JSONObject object = accountService.phoneRegister(phone, pwd, captcha, areaCode, clientIp, langCode);
        this.out(response, object,api_version);
    }

    /**
     * 发送验证码
     */
    @RequestMapping("/sendCaptcha")
    public void sendCaptcha(HttpServletRequest request, HttpServletResponse response) {
        JSONObject object;
        String client_ip = WkUtil.getIpAddr(request);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String phone = request.getParameter("phone");
        String area_code = getParameter(request, "area_code", "86");
        int type = getParameter(request, "type", 1);
        String project_name = getParameter(request, "project_name", "");
        String lang_code = request.getParameter("lang_code");
        if (WKCache.get_ip_captcha(client_ip) >= 10) {
            object = LibSysUtils.getResultJSON(ResultCode.send_error, LibProperties.getLanguage(lang_code, "weking.lang.app.send.error"));
        } else {
            object = smsService.sendCaptcha(phone, area_code, type, project_name, lang_code);
        }
        if (object.optInt("code") == ResultCode.success) {
            WKCache.add_ip_captcha(client_ip, 1);
        }
        this.out(response, object,api_version);
    }

    /**
     * 退出登录
     */
    @RequestMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String access_token = request.getParameter("access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.getInt("user_id");
            object = accountService.logout(userId);
        }
        this.out(response, object,api_version);
    }

    /**
     * 绑定第三方账号
     */
    @RequestMapping("/bing")
    public void bing(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            int type = LibSysUtils.toInt(getParameter(request, "type"));
            String bing_num = getParameter(request, "bing_num");
            object = accountService.bing(userId, bing_num, "", type);
        }
        out(response, object,api_version);
    }

    //取消绑定
    @RequestMapping("/cancelBing")
    public void cancelBing(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            int type = getParameter(request, "type", 0);
            String lang_code = object.optString("lang_code");
            object = accountService.cancelBing(userId, type, lang_code);
        }
        out(response, object,api_version);
    }

    /**
     * 绑定手机
     */
    @RequestMapping("/bingPhone")
    public void bingPhone(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            String bing_num = getParameter(request, "phone");
            String code = getParameter(request, "code");
            String area_code = getParameter(request, "area_code");
            object = accountService.bingPhone(userId, bing_num, code, area_code);
        }
        out(response, object,api_version);
    }

    //绑定详情
    @RequestMapping("/bingInfo")
    public void bingInfo(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            object = accountService.bingInfo(userId, lang_code);
        }
        out(response, object,api_version);
    }

    /**
     * 修改密码
     */
    @RequestMapping("/editPwdByOld")
    public void editPwdByOld(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            String oldPwd = getParameter(request, "old_pwd");
            String newPwd = getParameter(request, "new_pwd");
            String langCode = getParameter(request, "lang_code");
            object = accountService.editPwdByOld(userId, oldPwd, newPwd, langCode);
        }
        out(response, object,api_version);
    }

    /**
     * 修改密码
     */
    @RequestMapping("/editPwdByPhone")
    public void editPwdByPhone(HttpServletRequest request, HttpServletResponse response) {
        String phone = getParameter(request, "phone");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String pwd = getParameter(request, "pwd");
        String langCode = getParameter(request, "lang_code");
        String captcha = getParameter(request, "captcha");
        String areaCode = getParameter(request, "area_code");
        JSONObject object = accountService.editPwdByCaptcha(phone, pwd, captcha, areaCode, langCode);
        out(response, object,api_version);
    }

    //用户删除
    @RequestMapping("/delUser")
    public void delUser(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            object = accountService.delUser(userId);
        }
        out(response, object,api_version);
    }

    @RequestMapping("/recharge")
    public void recharge(HttpServletRequest request, HttpServletResponse response) {

        String account = getParameter(request, "account");
        int diamond = getParameter(request, "diamond", 0);
        accountService.recharge(account, diamond);

    }

    @RequestMapping("/setPocket")
    public void setPocket(HttpServletRequest request, HttpServletResponse response) {
        JSONObject object = new JSONObject();
        int i = accountService.setPocket();
        object.put("count",i);
        out(response, object,1.1);
    }


}
