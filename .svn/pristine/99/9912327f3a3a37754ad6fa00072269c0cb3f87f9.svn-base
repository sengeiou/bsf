package com.weking.controller.shop;

import com.weking.controller.out.OutControllerBase;
import com.weking.core.WkUtil;
import com.weking.service.shop.StoreService;
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
@RequestMapping({"/store","/mall/store"})
public class StoreController extends OutControllerBase {

    @Resource
    private StoreService storeService;

    /**
     * 获取店主信息
     */
    @RequestMapping("/getStoreUserInfo")
    public void getStoreUserInfo(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.getInt("user_id");
            int storeId = getParameter(request,"store_id",0);
            String lang_code = object.optString("lang_code");
            object = storeService.getStoreUserInfoByStoreId(userId,storeId, lang_code);
        }
        out(response,object,api_version);
    }

    /**
     * 获取店铺信息
     */
    @RequestMapping("/getStoreInfo")
    public void getStoreInfo(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int storeId = getParameter(request,"store_id",0);
            int index = getParameter(request,"index",0);
            int count = getParameter(request,"count",10);
            object = storeService.getStoreInfo(storeId,index,count);
        }
        out(response,object,api_version);
    }

}
