package com.weking.controller.shop;

import com.weking.controller.out.OutControllerBase;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.service.shop.AddressService;
import com.wekingframework.core.LibProperties;
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
@RequestMapping({"/address", "/mall/address"})
public class AddressController extends OutControllerBase {

    @Resource
    private AddressService addressService;

    /**
     * 国家列表
     */
    @RequestMapping({"/countryList", "/areaList"})
    public void countryList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        int parentId = getParameter(request, "parentId", 0);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            String lang_code = object.optString("lang_code", "zh_CN");
            object = addressService.getCountryList(lang_code, parentId);
        }
        out(response, object,api_version);
    }

    /**
     * 添加收货地址
     */
    @RequestMapping("/addDeliveryAddress")
    public void addDeliveryAddress(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String true_name = getParameter(request, "true_name");
            String area = getParameter(request, "area");
            String city = getParameter(request, "city");
            String address = getParameter(request, "address");
            int countryId = getParameter(request, "country_id", 0);
            String mobile = getParameter(request, "mobile");
            String is_default = getParameter(request, "is_default", "0");
            if (LibSysUtils.isNullOrEmpty(true_name) || LibSysUtils.isNullOrEmpty(area) || LibSysUtils.isNullOrEmpty(address) || LibSysUtils.isNullOrEmpty(mobile) || countryId == 0) {
                String lang_code = object.optString("lang_code", "zh_CN");
                object = LibSysUtils.getResultJSON(ResultCode.data_error, LibProperties.getLanguage(lang_code, "weking.lang.app.data.error"));
            } else {
                object = addressService.addDeliveryAddress(userId, true_name, countryId, area, city, address, mobile, is_default);
            }
        }
        out(response, object,api_version);
    }

    /**
     * 删除收货地址
     */
    @RequestMapping("/delDeliveryAddress")
    public void delDeliveryAddress(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int address_id = getParameter(request, "address_id", 0);
            object = addressService.delDeliveryAddress(userId, address_id);

        }
        out(response, object,api_version);
    }

    /**
     * 设置默认发货地址
     */
    @RequestMapping("/setDefaultDeliveryAddress")
    public void setDefaultDeliveryAddress(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int addressId = getParameter(request, "address_id", 0);
            int userId = object.optInt("user_id");
            object = addressService.setDefaultDeliveryAddress(userId, addressId);
        }
        out(response, object,api_version);
    }

    /**
     * 获取收货地址列表
     */
    @RequestMapping("/getDeliveryAddressList")
    public void getDeliveryAddressList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code", "zh_CN");
            object = addressService.getDeliveryAddressList(userId, lang_code);
        }
        out(response, object,api_version);
    }

    /**
     * 获取收货地址列表
     */
    @RequestMapping("/getDeliveryAddressInfo")
    public void getDeliveryAddressInfo(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int addressId = getParameter(request, "address_id", 0);
            String lang_code = object.optString("lang_code", "zh_CN");
            object = addressService.getDeliveryAddressInfo(userId, addressId, lang_code);
            if (object == null) {
                object = LibSysUtils.getResultJSON(ResultCode.data_error, LibProperties.getLanguage(lang_code, "weking.lang.app.data.error"));
            } else {
                object.put("code", ResultCode.success);
            }
        }
        out(response, object,api_version);
    }

    /**
     * 更新收货地址
     */
    @RequestMapping("/updateDeliveryAddress")
    public void updateDeliveryAddress(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String true_name = getParameter(request, "true_name");
            String area = getParameter(request, "area");
            String city = getParameter(request, "city");
            String address = getParameter(request, "address");
            int countryId = getParameter(request, "country_id", 0);
            int addressId = getParameter(request, "address_id", 0);
            String mobile = getParameter(request, "mobile");
            String is_default = getParameter(request, "is_default", "0");
            if (addressId == 0) {
                String lang_code = object.optString("lang_code", "zh_CN");
                object = LibSysUtils.getResultJSON(ResultCode.data_error, LibProperties.getLanguage(lang_code, "weking.lang.app.data.error"));
            } else {
                object = addressService.updateDeliveryAddress(userId, addressId, true_name, countryId, area, city, address, mobile, is_default);
            }
        }
        out(response, object,api_version);
    }
}
