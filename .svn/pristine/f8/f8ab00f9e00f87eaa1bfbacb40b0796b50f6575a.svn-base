package com.weking.controller.user;

import com.weking.controller.out.OutControllerBase;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.service.user.RankService;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 排行榜模块
 */
@Controller
@RequestMapping({"/rank","/user/rank"})
public class RankController extends OutControllerBase {

    protected static Logger log = Logger.getLogger("error");

    @Resource
    private RankService rankService;


    //获取排行榜
    @RequestMapping("/ranking")
    public void ranking(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        int ranking_type = LibSysUtils.toInt(getParameter(request, "ranking_type"));
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        int sort = LibSysUtils.toInt(getParameter(request, "sort"));
        String callback = getParameter(request, "callback");
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            String lang_code = object.optString("lang_code");
            int userId = object.getInt("user_id");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 50);
            String project_name = getParameter(request, "project_name", "");
            object = rankService.get_ranking(userId,ranking_type, sort, index, count,project_name,lang_code);
        }
        String result;
        if(!LibSysUtils.isNullOrEmpty(callback)){
            result = callback + "(" + object.toString() + ")";
        }else{
            result = object.toString();
        }
        out(response, result,api_version);
    }

    //获得贡献榜列表
    @RequestMapping("/getContributionList")
    public void getContributionList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String callback = getParameter(request, "callback");
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            String account = getParameter(request, "account");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 50);
            int type = getParameter(request, "type", 3);//  0日榜 1周榜 2月榜 3总榜
            int userId = object.optInt("user_id");
            if(index<300) {
                if(index>285){
                    count=10;
                }
           // count=300;
                object = rankService.getContributionList(userId, account, index, count,type);
            }else {
                JSONArray jsonArray = new JSONArray();
                object = LibSysUtils.getResultJSON(ResultCode.success);
                object.put("list", jsonArray);
            }
        }
        String result;
        if(!LibSysUtils.isNullOrEmpty(callback)){
            result = callback + "(" + object.toString() + ")";
        }else{
            result = object.toString();
        }
        out(response, result,api_version);
    }

    //获得当前直播贡献榜排名
    @RequestMapping("/getCurrentConsumptionList")
    public void getCurrentContributionList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String callback = getParameter(request, "callback");
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int live_id = getParameter(request, "live_id", 0);
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 50);
            int userId = object.optInt("user_id");
            if(index<150) {
                if(index>135){
                    count=10;
                }
                object = rankService.getCurrentConsumptionList(userId, live_id, index, count);
            }else {
                JSONArray jsonArray = new JSONArray();
                object = LibSysUtils.getResultJSON(ResultCode.success);
                object.put("list", jsonArray);
            }
        }
        String result;
        if(!LibSysUtils.isNullOrEmpty(callback)){
            result = callback + "(" + object.toString() + ")";
        }else{
            result = object.toString();
        }
        out(response, result,api_version);
    }

    //获得当前直播贡献榜排名
    @RequestMapping("/getGameList")
    public void getGameList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        String callback = getParameter(request, "callback");
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int live_id = getParameter(request, "live_id", 0);
            String tagName = getParameter(request, "tagName", "");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 50);
            String project_name = getParameter(request, "project_name", "");
            object = rankService.getGameList(tagName, live_id,project_name,index, count);
        }
        String result = object.toString();
        if (!LibSysUtils.isNullOrEmpty(callback))
            result = callback + "(" + result + ")";
        out(response, result,api_version);
    }

    /**
     * 排行榜缓存
     */
    @RequestMapping("/rankCache")
    public void rankCache(HttpServletRequest request, HttpServletResponse response) {
        int type = getParameter(request,"type",1);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        int index = getParameter(request,"index",0);
        int count = getParameter(request,"count",100);
        JSONObject object = rankService.rankCache(type,index,count);
        out(response,object,api_version);
    }

}
