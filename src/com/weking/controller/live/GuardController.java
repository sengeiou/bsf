package com.weking.controller.live;

import com.weking.controller.out.OutControllerBase;
import com.weking.core.WkUtil;
import com.weking.service.live.LiveGuardService;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Xujm
 */
@Controller
@RequestMapping("/live/guard")
public class GuardController extends OutControllerBase {

    @Resource
    private LiveGuardService liveGuardService;

    @RequestMapping("/buy")
    public void list(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int liveId = getParameter(request,"live_id",0);
            String account = getParameter(request,"account","0");
            int price = getParameter(request,"price",0);
            String langCode = object.optString("lang_code");
            object = liveGuardService.buy(userId,liveId,price,langCode,account);
        }
        out(response, object,api_version);
    }

    @RequestMapping("/info")
    public void info(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int liveId = getParameter(request,"live_id",0);
            String account = getParameter(request,"account","0");
            String langCode = object.optString("lang_code");
            object = liveGuardService.info(liveId,langCode,account);
        }
        out(response, object,api_version);
    }

    @RequestMapping("/rank")
    public void rank(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(accessToken);
        if (object.optInt("code") == 0) {
            int userId = object.getInt("user_id");
            int liveId = getParameter(request,"live_id",0);
            String account = getParameter(request,"account");
            //String langCode = object.optString("lang_code");
            if(liveId != 0){
                object = liveGuardService.getGuardRand(userId,liveId);
            }else{
                object = liveGuardService.getGuardRand(userId,account);
            }

        }
        out(response, object,api_version);
    }


}
