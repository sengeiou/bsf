package com.weking.controller.system;

import com.wekingframework.comm.LibControllerBase;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExceptionHandlerController extends LibControllerBase implements HandlerExceptionResolver {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView mv = new ModelAndView();
        logger.error("异常:{}，{}，{}",ex.getMessage(), ex, handler);
        JSONObject result = LibSysUtils.getResultJSON(-1, "internal error");
        out(response, result);
        return mv;
    }
}
