package com.weking.controller.shop;

import com.weking.controller.out.OutControllerBase;
import com.weking.core.WkUtil;
import com.weking.service.shop.ShopService;
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
@RequestMapping({"/shop", "/mall/shop"})
public class ShopController extends OutControllerBase {

    @Resource
    private ShopService shopService;

    /**
     * 广告列表
     */
    @RequestMapping("/getAdvList")
    public void getAdvList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object ;
        if (access_token != null) {
            object = WkUtil.checkToken(access_token);
            if (object.optInt("code") == 0) {
                object = shopService.getAdvList();
            }
        } else {
            object = shopService.getAdvList();
        }
        out(response, object,api_version);
    }

}
