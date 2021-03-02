package com.weking.core;


import com.weking.core.requestFilter.RequestParameterWrapper;
import com.weking.core.requestFilter.ResolveDataHandle;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2017/3/8.
 */
public class AuthFilter extends CharacterEncodingFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String currentURL = request.getRequestURI(); // 取得根目录所对应的绝对路径:
        //double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
       // boolean decrypt = false;
//        Map<String, Object> extraParams = new HashMap<>(0);
//       // if (decrypt) {
//            String data = request.getParameter("data");
//            if (StringUtils.hasLength(data)) {
//                try {
////                data = URLDecoder.decode(data,"utf-8");
//                    //进行参数解密
//
//                    String string = WkUtils.decryptString(data);
//                    extraParams = JSONObject.parseObject(string);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        //}
        //利用原始的request对象创建自己扩展的request对象并添加自定义参数
        RequestParameterWrapper requestParameterWrapper = new RequestParameterWrapper(request);
        requestParameterWrapper.addParameters(ResolveDataHandle.resolveData(request));
        requestParameterWrapper.setCharacterEncoding("utf-8");
        super.doFilterInternal(requestParameterWrapper, response, filterChain);
    }
}
