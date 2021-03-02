package com.weking.service.shop;

import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.mapper.shop.adv.ShopAdvMapper;
import com.weking.model.shop.adv.ShopAdv;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("shopService")
public class ShopService extends LibServiceBase {

    private static Logger logger = Logger.getLogger(ShopService.class);

    @Resource
    private ShopAdvMapper shopAdvMapper;

    /**
     *
     * 广告列表
     */
    public JSONObject getAdvList(){
        List<ShopAdv> list = shopAdvMapper.selectAdvList();
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (ShopAdv info:list){
            jsonObject = new JSONObject();
            jsonObject.put("adv_title",info.getAdvTitle());
            jsonObject.put("img_url", WkUtil.combineUrl(info.getImgUrl(), UploadTypeEnum.SHOP,false));
            jsonObject.put("extend",info.getExtend());
            jsonObject.put("width",info.getWidth());
            jsonObject.put("hight",info.getHeight());
            jsonObject.put("adv_type",info.getAdvType());
            jsonArray.add(jsonObject);
        }
        object.put("list",jsonArray);
        return object;
    }

}