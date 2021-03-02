package com.weking.controller.user;

import com.weking.cache.WKCache;
import com.weking.controller.out.OutControllerBase;
import com.weking.core.WkUtil;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.service.user.LevelService;
import com.weking.service.user.UserService;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 用户信息
 */
@Controller
@RequestMapping({"/user", "/user/info"})
public class UserController extends OutControllerBase {

    @Resource
    private UserService userService;

    @Resource
    private LevelService levelService;

    @Resource
    private AccountInfoMapper accountMapper;

    @RequestMapping("/getAnchorList")
    public void getAnchorList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 10);
            object = userService.getAnchorList(userId, index, count);
        }
        out(response, object,api_version);
    }

    /**
     * 推荐主播列表
     */
    @RequestMapping("/getRecommendAnchorList")
    public void getRecommendAnchorList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 10);
            int userId = object.optInt("user_id");
            object = userService.getRecommendAnchorList(userId, index, count);
        }
        out(response, object,api_version);
    }

    //修改用户信息
    @RequestMapping("/modify")
    public void modify(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            Enumeration param = request.getParameterNames();
            JSONObject jsonObject = new JSONObject();
            Object key;
            String val;
            while (param.hasMoreElements()) {
                key = param.nextElement();
                val = getParameter(request, LibSysUtils.toString(key));
                if (!LibSysUtils.isNullOrEmpty(val)) {
                    jsonObject.put(key, val);
                }
            }
            object = userService.modify(userId, jsonObject);
        }
        this.out(response, object,api_version);
    }

    //用户详情
    @RequestMapping("/userInfo")
    public void userInfo(HttpServletRequest request, HttpServletResponse response) {
        JSONObject object;
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String account = getParameter(request, "account");
        object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            object = userService.userInfo(userId, account);
        }
        this.out(response, object,api_version);
    }

    /**
     * 查询用户
     */
    @RequestMapping("/queryUserList")
    public void queryUserList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            String query = getParameter(request, "query");
            int index = LibSysUtils.toInt(getParameter(request, "index"), 0);
            int count = LibSysUtils.toInt(getParameter(request, "count", 10));
            int userId = object.optInt("user_id");
            object = userService.queryUserList(userId, query, index, count);
        }
        out(response, object,api_version);
    }

    //我的等级
    @RequestMapping("/myLevel")
    public void myLevel(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        String callback = getParameter(request, "callback");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            object = levelService.myLevel(userId);
        }
        String result = callback + "(" + object.toString() + ")";
        out(response, result,api_version);
    }
    //我的等级
    @RequestMapping("/myVipLevel")
    public void myVipLevel(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        String callback = getParameter(request, "callback");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            object = levelService.myVipLevel(userId);
        }
        String result = callback + "(" + object.toString() + ")";
        out(response, result,api_version);
    }

    //获取邀请图片
    @RequestMapping("/getInvitePic")
    public void getInvitePic(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            String nickname = object.optString("nickname");
            object = userService.getInvitePic(userId, nickname, lang_code);
        }
        out(response, object,api_version);
    }

    //输入邀请码
    @RequestMapping("/setInviteCode")
    public void setInviteCode(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            String invite_code = getParameter(request, "invite_code");
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            String nickname = object.optString("nickname", "");
            String project_name = getParameter(request, "project_name", "");
            object = userService.setInviteCode(userId, nickname, invite_code, project_name, lang_code,0);
        }
        out(response, object,api_version);
    }

    //用户邀请码
    @RequestMapping("/inviteCode")
    public void inviteCode(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String callback = getParameter(request, "callback");
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            object = userService.inviteCode(userId, lang_code);
        }
        String result = callback + "(" + object.toString() + ")";
        out(response, result,api_version);
    }

    //邀请列表
    @RequestMapping("/inviteList")
    public void inviteList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String callback = getParameter(request, "callback");
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 10);
            object = userService.inviteList(userId, index, count);
        }
        String result = callback + "(" + object.toString() + ")";
        out(response, result,api_version);
    }

    //实名认证
    @RequestMapping("/certification")
    public void certification(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            String real_name = getParameter(request, "real_name");
            String card_num = getParameter(request, "card_num");
            String phone = getParameter(request, "phone");
            String card_img = getParameter(request, "card_img");
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            object = userService.certification(userId, real_name, card_num, phone, card_img, lang_code);
        }
        out(response, object,api_version);
    }

    @RequestMapping("/reset")
    public void reset(HttpServletRequest request, HttpServletResponse response) {
        List<Map<String, Object>> list = this.getLibJdbcTemplate().queryForList("SELECT id from wk_user_info where invite_code=''");
        int size = list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                Map<String, Object> map = list.get(i);
                String id = LibSysUtils.toString(map.get("id"));
                String code = getInveteCode();
                this.getLibJdbcTemplate().update("update wk_user_info set invite_code='" + code + "' where id=" + id);
            }
        }
    }

    //获得邀请码
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

    /**
     * 保存用户资源照片或视频
     */
    @RequestMapping("/saveUserData")
    public void saveUserData(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.getInt("code") == 0) {
            int userId = object.optInt("user_id");
            int type = LibSysUtils.toInt(getParameter(request, "type"), 0);
            String data = getParameter(request, "data");
            object = userService.saveUserData(userId, type, data);
        }
        out(response, object,1.2);
    }

    /**
     * 删除照片及视频
     */
    @RequestMapping("/delUserData")
    public void delUserData(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        JSONObject object = WkUtil.checkToken(accessToken);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        if (object.getInt("code") == 0) {
            int userId = object.getInt("user_id");
            int dataId = getParameter(request, "data_id", 0);
            object = userService.delUserData(userId, dataId);
        }
        out(response, object,api_version);
    }

    /**
     * 更新照片及视频
     */
    @RequestMapping("/updateUserData")
    public void updateUserData(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.getInt("code") == 0) {
            int userId = object.getInt("user_id");
            int dataId = getParameter(request, "data_id", 0);
            String dataValue = getParameter(request, "data_value");
            object = userService.updateUserData(userId, dataId, dataValue);
        }
        out(response, object,api_version);
    }

    //获取用户VIP等级
    @RequestMapping("/getVipPrivilege")
    public void getVipPrivilege(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            object = userService.getVipPrivilege(userId);
        }
        out(response, object,api_version);
    }

}
