package com.weking.controller.user;

import com.weking.controller.out.OutControllerBase;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.service.user.FollowService;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping({"/follow","/user/follow"})
public class FollowController extends OutControllerBase {

    @Resource
    private FollowService followService;

    @RequestMapping("/addCancelFollow")
    public void addCancelFollow(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int live_id = getParameter(request, "live_id", 0);
            int follow_type = getParameter(request, "follow_type", 0); //关注类型  0取消关注  1关注
            String to_account = getParameter(request, "to_account");  //关注的账号
            String live_stream_id = getParameter(request, "live_stream_id");  //流id
            String lang_code = result.optString("lang_code");
            int user_id = result.optInt("user_id");
            int level = result.optInt("level");
            result = followService.addCancelFollow(user_id, to_account, level, follow_type, live_stream_id, live_id, lang_code);
        }
        out(response, result,api_version);
    }


    //获取我的粉丝列表
    @RequestMapping("/getRelationList")
    public void getRelationList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 10);
            String account = getParameter(request, "account");
            int type = getParameter(request, "type", 1);
            switch (type) {
                case 1:
                    result = followService.getFans(user_id, account, index, count);
                    break;
                case 2:
                    result = followService.getStarts(user_id, account, index, count);
                    break;
                default:
                    result = LibSysUtils.getResultJSON(ResultCode.system_error);
                    break;
            }
        }
        out(response, result,api_version);
    }
}
