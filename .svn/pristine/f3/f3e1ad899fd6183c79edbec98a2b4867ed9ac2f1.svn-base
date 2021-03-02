package com.weking.controller.pay;

import com.weking.core.WkUtil;
import com.weking.service.pay.PocketService;
import com.wekingframework.comm.LibControllerBase;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 钱包模块
 */
@Controller
@RequestMapping({"/pocket","/pay/pocket"})
public class PocketController extends LibControllerBase {

    protected static Logger log = Logger.getLogger("error");
    private static Logger logger = Logger.getLogger(PocketController.class);

    @Resource
    private PocketService pocketService;


    //获取充值列表
    @RequestMapping("/getRechargeList")
    public void ranking(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.getInt("user_id");
            int type = LibSysUtils.toInt(getParameter(request, "type"));
            String device_type = getParameter(request, "device_type");
            String project_name = getParameter(request,"project_name","");
            String channel = getParameter(request,"channel","");
            double version = LibSysUtils.toDouble(getParameter(request, "version"));
            object = pocketService.getRechargeList(userId,type, device_type,version,project_name,channel);
        }
        out(response, object);
    }

    //获取可用充值方式
    @RequestMapping("/getRecharge")
    public void getRecharge(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            double version = LibSysUtils.toDouble(getParameter(request, "version"));
            object = pocketService.getRecharge(version);
        }
        out(response, object);
    }

    //获取用户钱包信息
    @RequestMapping("/getPocketInfo")
    public void getPocketInfo(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            //double api_version = getParameter(request, C.RequestParam.api_version, 1.0);

            int index = LibSysUtils.toInt(request.getParameter("index"), 0);
            int count = LibSysUtils.toInt(request.getParameter("count"), 20);
            object = pocketService.getPocketInfo(userId,api_version,index,count);
        }
        out(response, object);
    }

    //购买货币
    @RequestMapping("/buy")
    public void buy(HttpServletRequest request, HttpServletResponse response) {
        logger.info("支付-------------app");
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            String ip = request.getRemoteAddr();
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            int rechargeId = LibSysUtils.toInt(getParameter(request, "recharge_id"));
            String project_name = getParameter(request, "project_name", "");
            object = pocketService.buy(userId, rechargeId, ip,project_name, lang_code,"","","","");
        }
        out(response, object);
    }

    //收益
    @RequestMapping("/income")
    public void myIncome(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String callback = getParameter(request, "callback");
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            object = pocketService.income(userId);
        }
        String result = callback + "(" + object.toString() + ")";
        out(response, result);
    }

    //提现
    @RequestMapping("/withdraw")
    public void withdraw(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String callback = getParameter(request, "callback");
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String draw_money = getParameter(request, "draw_money");
            String lang_code = object.optString("lang_code");
            object = pocketService.withdraw(userId, draw_money, lang_code);
        }
        String result = callback + "(" + object.toString() + ")";
        out(response, result);
    }

    //提现列表
    @RequestMapping("/withdrawList")
    public void withdrawList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        String callback = getParameter(request, "callback");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 10);
            object = pocketService.withdrawList(userId, index, count);
        }
        String result = callback + "(" + object.toString() + ")";
        out(response, result);
    }

    //充值列表
    @RequestMapping("/payList")
    public void payList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        String callback = getParameter(request, "callback");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 10);
            object = pocketService.payList(userId, index, count);
        }
        String result = callback + "(" + object.toString() + ")";
        out(response, result);
    }

    /**
     * 用户充值记录
     */
    @RequestMapping("/getRechargeLog")
    public void getRechargeLog(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int index = object.optInt("index");
            int count = object.optInt("count");
            object = pocketService.getRechargeLog(userId,index,count);
        }
        out(response, object);
    }


    //使用记录
    @RequestMapping("/billLog")
    public void billLog(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 20);
            object = pocketService.billLog(userId, index, count);
        }
        out(response, object);
    }

    //购买记录
    @RequestMapping("/buyLog")
    public void buyLog(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 20);
            object = pocketService.buyLog(userId, index, count);
        }
        out(response, object);
    }


}
