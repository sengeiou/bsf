package com.weking.controller.system;

import com.wekingframework.comm.LibControllerBase;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping({"/error","/system/error"})
public class ErrorController extends LibControllerBase {

    protected static Logger log = Logger.getLogger("error");

    /**
     * 404错误
     */
    @RequestMapping("/error_404")
    public void error_404(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result;
        result = LibSysUtils.getResultJSON(404,"not found");
        String url = request.getScheme()+"://"+ request.getServerName()+request.getRequestURI()+"?"+request.getQueryString();
        log.error("not found:"+url);
        out(response, result);
    }

    /**
     * 500错误
     */
    @RequestMapping("/error_500")
    public void error_500(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result;
        result = LibSysUtils.getResultJSON(500, "internal error");
        //String currentURL = request.getRequestURI(); // 取得根目录所对应的绝对路径:
        Exception exception = (Exception) request.getAttribute("exception");
        log.error(exception.getMessage(), exception);
        out(response, result);
    }

    @RequestMapping("/appError")
    public void appError(HttpServletRequest request, HttpServletResponse response){
        String account = getParameter(request,"account");
        String error_info = getParameter(request,"error_info");
        int device_type = LibSysUtils.toInt(getParameter(request,"device_type"));
        String str = "";
        switch (device_type){
            case 0:
                str = "iphone";
                break;
            case 1:
                str = "ipad";
                break;
            case 2:
                str = "android";
                break;
            case 3:
                str = "androidPad";
                break;

        }
        String device_model = getParameter(request,"device_model");
        log.error("appError：---account:"+account+"---device_type:"+str+"---device_model:"+device_model+"---error_info:"+error_info);
    }
}
