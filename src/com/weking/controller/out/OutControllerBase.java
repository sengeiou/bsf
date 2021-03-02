package com.weking.controller.out;

import com.wekingframework.comm.LibJdbcTemplate;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import wk.rtc.comm.WkUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Enumeration;

public class OutControllerBase {

    private static Logger log = Logger.getLogger(OutControllerBase.class);
        @Resource
        private LibJdbcTemplate libJdbcTemplate;

        public OutControllerBase() {
        }

        protected LibJdbcTemplate getLibJdbcTemplate() {
            return this.libJdbcTemplate;
        }

        protected String getLangCode() {
            return LibProperties.getConfig("weking.config.default_lang");
        }

        protected final void out(HttpServletResponse response, JSONObject result,Double apiVersion) {
            this.out(response, result.toString(),apiVersion);
        }

        protected final void out(HttpServletResponse response, String result,Double apiVersion) {
            try {
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setContentType("application/json;charset=utf-8");
                response.setCharacterEncoding("UTF-8");
                PrintWriter printWriter = response.getWriter();
                //加密
                if(apiVersion>=4.1) {
                    //System.out.println(Charset.defaultCharset());
                    result = WkUtils.encryString(result);
                    //log.info("encry:"+result);
                    JSONObject obj = new JSONObject();
                    obj.put("resultData",result);//.replaceAll("\r|\n","")
                    result=LibSysUtils.toString(obj);
                }
                response.getWriter().print(result);
                printWriter.flush();
                printWriter.close();
            } catch (Exception var4) {
                var4.printStackTrace();
            }

        }

        protected final String getParameter(HttpServletRequest request, String paramName) {
            return request.getParameter(paramName);
        }

        protected final String getParameter(HttpServletRequest request, String paramName, String defVal) {
            String str = this.getParameter(request, paramName);
            if (LibSysUtils.isNullOrEmpty(str)) {
                str = defVal;
            }

            return str;
        }

        protected final double getParameter(HttpServletRequest request, String paramName, double defVal) {
            String str = this.getParameter(request, paramName);
            defVal = LibSysUtils.toDouble(str, defVal);
            return defVal;
        }

        protected final int getParameter(HttpServletRequest request, String paramName, int defVal) {
            String str = this.getParameter(request, paramName);
            defVal = LibSysUtils.toInt(str, defVal);
            return defVal;
        }

        protected final long getParameter(HttpServletRequest request, String paramName, long defVal) {
            String str = this.getParameter(request, paramName);
            defVal = LibSysUtils.toLong(str, defVal);
            return defVal;
        }

        protected final boolean getParameter(HttpServletRequest request, String paramName, boolean defVal) {
            String str = this.getParameter(request, paramName);
            defVal = LibSysUtils.toBoolean(str, defVal);
            return defVal;
        }

        protected final void logParamsValue(HttpServletRequest request, HttpServletResponse response, String tag, boolean onlyPrint) {
            String params = tag;

            String paraName;
            for(Enumeration enu = request.getParameterNames(); enu.hasMoreElements(); params = params + "," + paraName + ": " + request.getParameter(paraName)) {
                paraName = (String)enu.nextElement();
            }

            System.out.println(params);
        }
    }
