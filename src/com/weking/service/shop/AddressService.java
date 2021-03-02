package com.weking.service.shop;

import com.weking.core.ResultCode;
import com.weking.mapper.comm.CommAreaMapper;
import com.weking.mapper.shop.address.DeliveryAddressMapper;
import com.weking.model.comm.CommArea;
import com.weking.model.shop.address.DeliveryAddress;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Service("addressService")
public class AddressService extends LibServiceBase {

    @Resource
    private DeliveryAddressMapper deliveryAddressMapper;

    @Resource
    private CommAreaMapper commAreaMapper;

    /**
     * 添加收货地址
     */
    @Transactional
    public JSONObject addDeliveryAddress(int userId, String true_name, int countryId, String area, String city, String address, String mobile, String isDefault) {
        if ("1".equals(isDefault)) { //如果设为默认收货地址，更新原有默认
            deliveryAddressMapper.cancelDefaultAddressByUserId(userId);
        }
        DeliveryAddress record = new DeliveryAddress();
        record.setAddress(address);
        record.setUserId(userId);
        record.setArea(area);
        record.setCity(city);
        record.setTrueName(true_name);
        record.setMobile(mobile);
        record.setIsDefault(isDefault);
        record.setCountryId(countryId);
        deliveryAddressMapper.insert(record);
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("address_id", record.getId());
        return object;
    }

    /**
     * 删除收货地址
     */
    public JSONObject delDeliveryAddress(int userId, int address_id) {
        deliveryAddressMapper.deleteByPrimaryKey(address_id, userId);
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 更新收货地址
     */
    @Transactional
    public JSONObject updateDeliveryAddress(int userId, int addressId, String true_name, int countryId, String area, String city, String address, String mobile, String isDefault) {
        if ("1".equals(isDefault)) { //如果设为默认收货地址，更新原有默认
            deliveryAddressMapper.cancelDefaultAddressByUserId(userId);
        }
        DeliveryAddress record = new DeliveryAddress();
        if (!LibSysUtils.isNullOrEmpty(address)) {
            record.setAddress(address);
        }
        if (!LibSysUtils.isNullOrEmpty(area)) {
            record.setArea(area);
        }
        if (!LibSysUtils.isNullOrEmpty(city)) {
            record.setCity(city);
        }
        if (!LibSysUtils.isNullOrEmpty(true_name)) {
            record.setTrueName(true_name);
        }
        if (!LibSysUtils.isNullOrEmpty(mobile)) {
            record.setMobile(mobile);
        }
        if (!LibSysUtils.isNullOrEmpty(isDefault)) {
            record.setIsDefault(isDefault);
        }
        if (countryId > 0) {
            record.setCountryId(countryId);
        }
        record.setUserId(userId);
        record.setId(addressId);
        deliveryAddressMapper.updateAddressByIdAndUserId(record);
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 国家列表
     */
    public JSONObject getCountryList(String lang_code, int parentId) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        List<CommArea> lists = commAreaMapper.selectByParentId(parentId, lang_code);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (CommArea list : lists) {
            jsonObject = new JSONObject();
            jsonObject.put("id", LibSysUtils.toString(list.getId()));
            jsonObject.put("name", list.getZhCn());
            jsonArray.add(jsonObject);
        }
        object.put("list", jsonArray);
        return object;
    }

    /**
     * 设置默认发货地址
     */
    @Transactional
    public JSONObject setDefaultDeliveryAddress(int userId, int addressId) {
        deliveryAddressMapper.cancelDefaultAddressByUserId(userId);
        deliveryAddressMapper.setDefaultAddressById(addressId, userId);
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 获得收货地址列表
     */
    public JSONObject getDeliveryAddressList(int userId, String lang_code) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        List<DeliveryAddress> list = deliveryAddressMapper.selectListByUserId(userId);
        JSONArray jsonArray = new JSONArray();
        for (DeliveryAddress info : list) {
            jsonArray.add(groupDeliveryAddressInfo(info, lang_code));
        }
        object.put("list", jsonArray);
        return object;
    }

    /**
     * 获取收货地址详情
     */
    public JSONObject getDeliveryAddressInfo(int userId, int addressId, String lang_code) {
        DeliveryAddress info = deliveryAddressMapper.findDeliveryAddressByAddressId(userId, addressId);
        if (info == null) {
            return null;
        }
        return groupDeliveryAddressInfo(info, lang_code);
    }

    /**
     * 组合收货信息
     */
    private JSONObject groupDeliveryAddressInfo(DeliveryAddress info, String lang_code) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("true_name", info.getTrueName());
        jsonObject.put("mobile", info.getMobile());
        jsonObject.put("address", info.getAddress());
        jsonObject.put("area", info.getArea());
        jsonObject.put("city", info.getCity());
        jsonObject.put("is_default", info.getIsDefault());
        jsonObject.put("address_id", info.getId());
        jsonObject.put("country_id", info.getCountryId());
        jsonObject.put("country_name", getCountryLang(lang_code, info.getCountryId()));
        return jsonObject;
    }

    /**
     * 获取默认发货地址信息(没有默认取一条)
     */
    public JSONObject getDeliveryAddressInfo(int userId, String lang_code) {
        DeliveryAddress info = deliveryAddressMapper.findDeliveryAddressByUserId(userId);
        if (info == null) {
            return null;
        }
        return groupDeliveryAddressInfo(info, lang_code);
    }

    /**
     * 获取国家名称语言
     */
    private String getCountryLang(String lang_code, int id) {
        CommArea commArea = commAreaMapper.selectNameById(id, lang_code);
        if (commArea != null)
            return commArea.getZhCn();
        else
            return "";
    }

    /**
     * 获取语言
     */
    private String getLang(String lang, String key, String basename) {
        ResourceBundle resource = getResourceBundle(lang, basename);
        return resource.getString(key);
    }

    /**
     * 获取语言资源文件
     */
    private ResourceBundle getResourceBundle(String lang, String basename) {
        if (LibSysUtils.isNullOrEmpty(lang)) {
            lang = LibProperties.getConfig("weking.config.default_lang");
        }
        String[] array = lang.split("_");
        Locale locale = new Locale(array[0], array[1]);
        return ResourceBundle.getBundle(basename, locale);
    }

}
