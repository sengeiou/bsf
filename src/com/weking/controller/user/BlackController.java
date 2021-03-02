package com.weking.controller.user;

import com.weking.controller.out.OutControllerBase;
import com.weking.core.WkUtil;
import com.weking.service.user.BlackLogService;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping({"/black","/user/black"})
public class BlackController extends OutControllerBase {

    @Resource
    private BlackLogService blackLogService;

    //拉黑与取消拉黑
    @RequestMapping("/opeBlack")
    public void opeBlack(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request,"access_token");
        String account = getParameter(request,"account");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        int type = getParameter(request,"type",1);
        int live_id = getParameter(request, "live_id", 0);
        String live_stream_id = getParameter(request, "live_stream_id",null);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.getInt("code")==0){
            int userId = object.optInt("user_id");
            switch (type){
                case 1: //拉黑
                    object = blackLogService.pullBlack(userId, account,live_stream_id,live_id,api_version);
                    break;
                case 2: //取消拉黑
                    object = blackLogService.cacelBlack(userId, account);
                    break;
                default:
                    break;
            }
        }

        this.out(response, object,api_version);
    }

    //黑名单列表
    @RequestMapping("/blackList")
    public void blackList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        int index = getParameter(request,"index",0);  //分页从哪里开始
        int count = getParameter(request,"count",10);  //取多少条数据
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code")==0) {
            int userId = object.optInt("user_id");
            object = blackLogService.getList(userId, index, count);
        }
        this.out(response,object,api_version);
    }
    @RequestMapping("/isBlack")
    public void isBlack(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.getInt("code")==0) {
            int userId = object.optInt("user_id");
            String account = getParameter(request,"account");
            object = blackLogService.isBlack(userId, account);
        }
        this.out(response,object,api_version);
    }
}
