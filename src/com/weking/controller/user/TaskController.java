package com.weking.controller.user;

import com.weking.cache.WKCache;
import com.weking.controller.out.OutControllerBase;
import com.weking.service.user.TaskService;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping({"/task","user/task"})
public class TaskController extends OutControllerBase {

    static Logger log = Logger.getLogger(TaskController.class);

    @Resource
    private TaskService taskService;

    //签到任务
    @RequestMapping("/getLoginTaskList")
    public void getLoginTaskList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object=new JSONObject();
        if(access_token!=null) {
            object = WKCache.check_token(access_token);
            if (object.optInt("code") == 0) {
                int userId = object.optInt("user_id");
                String lang_code = object.optString("lang_code");
                object = taskService.getLoginTaskList(userId, lang_code);
            }
        }else {
            object.put("code",0);
        }
        out(response, object,api_version);
    }

    //分享任务
    @RequestMapping("/shareTask")
    public void shareTask(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            int live_id = getParameter(request, "live_id", 0);
            object = taskService.shareTask(userId, live_id,lang_code);
        }
        out(response, object,api_version);
    }

    /**
     *领取任务u币
     */
//	@RequestMapping("/getFreeCoin")
//	public void getFreeCoin(HttpServletRequest request, HttpServletResponse response){
//		String access_token = getParameter(request,"access_token");
//		JSONObject object = WKCache.check_token(access_token);
//		if(object.getInt("code")==0){
//			int userId = object.optInt("user_id");
//			String lang_code = object.optString("lang_code");
//			object= taskService.getFreeCoin(userId,lang_code);
//		}
//		out(response,object);
//	}

    // 获取所有活动
    @RequestMapping("/getActivityList")
    public void getActivityList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            int type=LibSysUtils.toInt(request.getParameter("type"),0);
            object = taskService.getActivityList(userId,lang_code,type);
        }
        out(response, object,api_version);
    }


    //直播间签到天数
    @RequestMapping("/getLiveSinInDay")
    public void getLiveSinInDay(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        int live_id = LibSysUtils.toInt(request.getParameter("live_id"), 0);
        JSONObject object=new JSONObject();
        if(access_token!=null) {
            object = WKCache.check_token(access_token);
            if (object.optInt("code") == 0) {
                int userId = object.optInt("user_id");
                String lang_code = object.optString("lang_code");
                object = taskService.getLiveSinInDay(userId, lang_code,live_id);
            }
        }/*else {
            object.put("code",0);
        }*/
        out(response, object,api_version);
    }

    // 获取每日任务列表  及完成进度
    @RequestMapping("/getEverydayTaskList")
    public void getEverydayTaskList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            object = taskService.getEverydayTaskList(userId,lang_code);
        }
        out(response, object,api_version);
    }

    // 领取每日任务奖励
    @RequestMapping("/getEverydayTaskAward")
    public void getEverydayTaskAward(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        int task_id = LibSysUtils.toInt(request.getParameter("task_id"), 0);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            object = taskService.getEverydayTaskAward(userId,lang_code,task_id);
        }
        out(response, object,api_version);
    }


}
