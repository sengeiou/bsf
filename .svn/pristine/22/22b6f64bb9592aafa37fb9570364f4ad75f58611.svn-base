package com.weking.controller.admin;

import com.weking.controller.out.OutControllerBase;
import com.weking.core.C;
import com.weking.service.admin.AdminService;
import com.wekingframework.comm.LibControllerBase;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by zhb on 2017/8/24.
 * 后台管理
 */
@Controller
@RequestMapping("/admin")
public class AdminController extends OutControllerBase {

    protected static Logger log = Logger.getLogger("AdminController");

    @Resource
    private AdminService adminService;

    // 更新推荐动态列表缓存
    @RequestMapping("/updateRecPostList")
    public void opeBlack(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        if (C.ManagerSysAccessToken.equals(access_token)) {
            int post_id = getParameter(request, "post_id", 0);
            int user_id = getParameter(request, "user_id", 0);
            int sorts = getParameter(request, "sorts", 0);
            JSONObject object = adminService.updateRecPostList(user_id,post_id, sorts);
            out(response, object,1.1);
        }
    }

    //后台充值增加VIP等级
    @RequestMapping("/updateVipLevel")
    public void updateVipLevel(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        if (C.ManagerSysAccessToken.equals(access_token)) {
            int user_id = getParameter(request, "user_id", 0);
            int buy_emo = getParameter(request, "buy_emo", 0);
            JSONObject object = adminService.updateVipLevel(user_id,buy_emo);
            out(response, object,1.1);
        }
    }


}
