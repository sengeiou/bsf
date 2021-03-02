package com.weking.service.shop;

import com.weking.cache.WKCache;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.mapper.shop.store.ShopStoreMapper;
import com.weking.model.account.AccountInfo;
import com.weking.model.shop.store.ShopStore;
import com.weking.service.user.AccountService;
import com.weking.service.user.FollowService;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("storeService")
public class StoreService extends LibServiceBase {

    @Resource
    private ShopStoreMapper shopStoreMapper;
    @Resource
    private AccountService accountService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private FollowService followService;

    /**
     * 搜索用户店铺ID
     */
    public int findStoreIdByUserId(int userId) {
        Integer storeId;
        String store_id = WKCache.get_user(userId, "store_id");
        if (store_id == null) {
            storeId = shopStoreMapper.findStoreIdByUserId(userId);
            if (storeId == null) {
                return 0;
            }
            WKCache.add_user(userId, "store_id", LibSysUtils.toString(storeId));
        } else {
            storeId = LibSysUtils.toInt(store_id);
        }
        return storeId;
    }

    /**
     * 查询平台自营店铺ID
     */
    public int getOwnStoreId() {
        Integer storeId = shopStoreMapper.findOwnStoreId();
        if (storeId == null) {
            return 0;
        }
        return storeId;
    }

    /**
     * 获取店主信息
     */
    public JSONObject getStoreUserInfoByStoreId(int userId, int storeId, String lang_code) {
        ShopStore info = shopStoreMapper.findStoreInfoByStoreId(storeId);
        if (info == null) {
            return LibSysUtils.getResultJSON(ResultCode.store_no_exist, LibProperties.getLanguage(lang_code, "weking.lang.app.store.no.exist"));
        }
        AccountInfo accountInfo = accountService.getAccountInfo(info.getUserId());
        if (accountInfo == null) {
            return LibSysUtils.getResultJSON(ResultCode.account_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.account.exist_error"));
        }

        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("account", accountInfo.getAccount());
        object.put("nickname", accountInfo.getNickname());
        object.put("sex", accountInfo.getSex());
        object.put("follow_state", followService.followStatus(userId, accountInfo.getId()));
        object.put("pic_head_high", WkUtil.combineUrl(accountInfo.getPicheadUrl(), UploadTypeEnum.AVATAR, false));
        object.put("pic_head_low", WkUtil.combineUrl(accountInfo.getPicheadUrl(), UploadTypeEnum.AVATAR, true));
        return object;
    }

    /**
     * 获取店铺信息
     */
    public JSONObject getStoreInfo(int storeId, int index, int count) {
        JSONObject object;
        //Map<String, String> map = getStoreCacheInfo(storeId, "store_id", "store_name", "store_avatar", "store_sales","store_banner");
        ShopStore info = shopStoreMapper.findStoreInfoByStoreId(storeId);
        if (info != null) {
            object = LibSysUtils.getResultJSON(ResultCode.success);
            object.put("store_id", info.getId());
            object.put("store_name", info.getStoreName());
            object.put("store_sales", info.getStoreSales());
            object.put("goods_num", getStoreGoodsCommonCount(info.getId()));
            object.put("store_avatar", WkUtil.combineUrl(LibSysUtils.toString(info.getStoreAvatar()), UploadTypeEnum.SHOP, true));
            object.put("store_banner", WkUtil.combineUrl(LibSysUtils.toString(info.getStoreBanner()), UploadTypeEnum.SHOP, false));
            object.put("store_commend", goodsService.getStoreGoodsCommendList(storeId, index, count));
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.store_no_exist, LibProperties.getLanguage("weking.lang.app.store.no.exist"));
        }
        return object;
    }

    /**
     * 更新店铺销量
     */
    public int updateStoreSalesByStoreId(int storeId, int storeSales) {
        return shopStoreMapper.updateStoreSalesByStoreId(storeId, storeSales);
    }

    /**
     * 获得店铺商品数量
     */
    public int getStoreGoodsCommonCount(int storeId) {
        return goodsService.selectStoreGoodsCommonNum(storeId);
    }

    /**
     * 获得店铺某个信息
     */
    public String getStoreFieldInfo(int storeId, String field) {
//        String val = WKCache.get_store_info(storeId, field);
//        if (val == null) {
//            Map<String, String> map = setStoreCacheInfo(storeId);
//            if (map != null) {
//                val = map.get(field);
//            }
//        }
        String val = "";
        ShopStore info = shopStoreMapper.findStoreInfoByStoreId(storeId);
        if (info != null) {
            if ("store_name".equals(field))
                val = info.getStoreName();
            if ("store_free_price".equals(field))
                val = LibSysUtils.toString(info.getStoreFreePrice());
        }
        return val;
    }

    /**
     * 获得店铺缓存信息
     */
    public Map<String, String> getStoreCacheInfo(int storeId, String... fields) {
        List<String> list = WKCache.get_store_info(storeId, fields);
        Map<String, String> map;
        if (list.get(0) == null) {
            map = setStoreCacheInfo(storeId);
        } else {
            map = new HashMap<>();
            int length = fields.length;
            for (int i = 0; i < length; i++) {
                map.put(fields[i], list.get(i));
            }
        }
        return map;
    }

    /**
     * 设置店铺缓存信息
     */
    private Map<String, String> setStoreCacheInfo(int storeId) {
        Map<String, String> map = new HashMap<>();
        ShopStore info = shopStoreMapper.findStoreInfoByStoreId(storeId);
        if (info == null) {
            return null;
        }
        map.put("store_id", LibSysUtils.toString(info.getId()));
        map.put("store_name", info.getStoreName());
        map.put("store_sales", LibSysUtils.toString(info.getStoreSales()));
        map.put("store_collect", LibSysUtils.toString(info.getStoreCollect()));
        map.put("store_avatar", LibSysUtils.toString(info.getStoreAvatar()));
        map.put("store_free_price", LibSysUtils.toString(info.getStoreFreePrice()));
        map.put("store_banner", LibSysUtils.toString(info.getStoreBanner()));
        WKCache.add_store_info(storeId, map);
        return map;
    }


}