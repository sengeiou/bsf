//package com.weking.core.requestFilter;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.util.Map;
//
//public class requestFilter implements Filter{
//    private static Logger logger = LoggerFactory.getLogger(requestFilter.class);
//    @Override
//    public void destroy() {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
//            throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) req;
//        Map<String, Object> extraParams = ResolveDataHandle.resolveData(request);
//
//        //利用原始的request对象创建自己扩展的request对象并添加自定义参数
//        RequestParameterWrapper requestParameterWrapper = new RequestParameterWrapper(request);
//        requestParameterWrapper.addParameters(extraParams);
//        filterChain.doFilter(requestParameterWrapper, res);
//    }
//
//    @Override
//    public void init(FilterConfig arg0) throws ServletException {
//        // TODO Auto-generated method stub
//
//    }
//
//}