package com.weking.service.web;

import com.weking.cache.WKCache;
import com.weking.core.HttpXmlUtils;
import com.weking.core.ResultCode;
import com.weking.service.user.AccountService;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("webService")
public class WebService {

    @Resource
    private AccountService accountService;

    public JSONObject login(int type,String user_name,String pwd,String client_ip,String area_code,String device_model,String version,String lang_code){
        JSONObject object;
        String nickname = "";
        String avatar = "";
        switch (type){
            case 1://微信
                object = getWxUserInfo(user_name,lang_code);
                if(object.optInt("code")==0){
                    nickname = object.optString("nickname");
                    avatar = object.optString("avatar");
                    user_name = object.getString("user_name");
                }
                break;
            case 3://facebook
                break;
            case 4://kaoko
                break;
            case 5: //谷歌
                break;
        }
        object = accountService.login(type, user_name, pwd, nickname, avatar, device_model, 4, "0000",false, "", "", client_ip, area_code, version,"", lang_code,"","","");
        return object;
    }

    //获取微信用户信息
    private JSONObject getWxUserInfo(String code,String lang_code) {
        JSONObject object;
        String appid = WKCache.get_system_cache("weking.config.weixin.web.appid");
        String secret = WKCache.get_system_cache("weking.config.weixin.web.secret");
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+secret+"&code=" + code + "&grant_type=authorization_code";
        String Post = HttpXmlUtils.httpsRequest(url, "POST", "");
        JSONObject jsonData = JSONObject.fromObject(Post);
        if (!jsonData.has("errcode")) {
            String openid = jsonData.optString("openid");
            String access_token = jsonData.optString("access_token");
            String urls = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN";
            String userInfo = HttpXmlUtils.httpsRequest(urls, "GET", "");
            JSONObject user = JSONObject.fromObject(userInfo);
            if (user.has("errcode")) {
                object = LibSysUtils.getResultJSON(ResultCode.weixin_unionid_error,LibProperties.getLanguage(lang_code,"weking.lang.weixin.unionid.error"));
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.success);
                object.put("nickname",user.optString("nickname"));
                object.put("avatar",user.optString("headimgurl"));
                object.put("user_name",user.getString("unionid"));
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.weixin_unionid_error,LibProperties.getLanguage(lang_code,"weking.lang.weixin.unionid.error"));
        }
        return object;
    }

}
