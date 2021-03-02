package com.weking.core.requestFilter;

import com.gexin.fastjson.JSONObject;
import com.weking.core.WkUtil;
import com.wekingframework.core.LibProperties;
import org.apache.commons.lang.StringUtils;
import wk.rtc.comm.WkUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Xujm
 * 2018/12/26
 */
public class ResolveDataHandle {

    public static Map<String,Object> resolveData(HttpServletRequest request){
        Map<String, Object> extraParams = new HashMap<>(0);
        String data = request.getParameter("data");
        String requestURI = request.getRequestURI();
        String ignoreUrl = LibProperties.getConfig("weking.ignore.url");
        if(StringUtils.isNotEmpty(ignoreUrl)){
            String[] urls = ignoreUrl.split(",");
            if (!Arrays.asList(urls).contains(requestURI)){
                if(StringUtils.isNotEmpty(data)){
                    data = WkUtils.decryptString(data);
                }
            }else {
                data = WkUtil.urlDecode(data);
            }

        }

        if(StringUtils.isNotEmpty(data)&&data.contains("{")){
            extraParams = JSONObject.parseObject(data);
        }else {
            extraParams.put("data",data);
        }
        return extraParams;
    }

    public static Object getParamValue(HttpServletRequest request,String param){
        String paramValue = request.getParameter(param);
        if(StringUtils.isEmpty(paramValue)){
            Map<String, Object> extraParams = resolveData(request);
            return extraParams.get(param);
        }
        return paramValue;
    }

}
