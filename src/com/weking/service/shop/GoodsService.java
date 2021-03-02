package com.weking.service.shop;

import com.weking.cache.WKCache;
import com.weking.core.IMCode;
import com.weking.core.ResultCode;
import com.weking.core.WkImClient;
import com.weking.core.WkUtil;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.mapper.shop.goods.*;
import com.weking.model.shop.goods.*;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service("goodsService")
public class GoodsService extends LibServiceBase {

    @Resource
    private GoodsCommonMapper goodsCommonMapper;
    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private GoodsClassMapper goodsClassMapper;
    @Resource
    private StoreService storeService;
    @Resource
    private GoodsImagesMapper goodsImagesMapper;

    /**
     * 商品引导
     */
    public JSONObject index(String lang_code,Boolean flg) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", LibProperties.getLanguage(lang_code, "weking.lang.app.goods.index.tag0"));
        jsonObject.put("data", getGoodsClass(0).getJSONArray("list"));
        object.put("goods_class", jsonObject);
        JSONArray jsonArray = new JSONArray();
        jsonObject.put("title", LibProperties.getLanguage(lang_code, "weking.lang.app.goods.index.tag1"));
        jsonObject.put("data", getGoodsCommendList(0, 10,flg).getJSONArray("list"));
        jsonObject.put("type", 1);
        jsonArray.add(jsonObject);
        object.put("goods_type", jsonArray);
        return object;
    }

    /**
     * 查询用户名下商品
     */
    public JSONObject getUserGoodsCommonList(int userId, String query, int index, int count) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        int storeId = storeService.findStoreIdByUserId(userId);
        List<Integer> roomIdList = goodsCommonMapper.selectRoomGoodsCommonIdList(storeId);
        List<GoodsCommon> list = goodsCommonMapper.selectSearchGoodsCommonList(storeId, query, null, index, count);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (GoodsCommon info : list) {
            jsonObject = getGoodsCommonInfo(info);
            if (roomIdList == null) {
                jsonObject.put("is_live", false);
            } else {
                jsonObject.put("is_live", roomIdList.contains(info.getId()));
            }
            jsonArray.add(jsonObject);
        }
        object.put("list", jsonArray);
        return object;
    }

    /**
     * 查询店铺商品列表
     */
    public JSONObject getStoreGoodsCommonList(int storeId, int index, int count) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", getStoreGoodsCommon(storeId, index, count));
        return object;
    }

    /**
     * 查询店铺商品列表
     */
    private JSONArray getStoreGoodsCommon(int storeId, int index, int count) {
        if (storeId == 0) {
            return new JSONArray();
        }
        List<GoodsCommon> list = goodsCommonMapper.selectGoodsCommonByStoreId(storeId, index, count);
        return handleGoodsCommonListData(list);
    }

    /**
     * 获得用户直播间直播商品数量
     */
    public JSONObject getRoomGoodsCommonCount(int userId) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        int storeId = storeService.findStoreIdByUserId(userId);
        object.put("goods_count", goodsCommonMapper.selectRoomGoodsCommonCount(storeId));
        return object;
    }

    /**
     * 直播间公共商品列表
     */
    public JSONObject getRoomGoodsCommonList(int storeId) {
        JSONObject object;
        String goodsCommon = WKCache.get_room_goods_list(storeId);
        if (goodsCommon == null) {
            object = LibSysUtils.getResultJSON(ResultCode.success);
            JSONArray jsonArray = new JSONArray();
            int size = 0;
            if (storeId != 0) {
                List<GoodsCommon> list = goodsCommonMapper.selectRoomGoodsCommonList(storeId);
                size = list.size();
                if (size > 0) {
                    JSONObject jsonObject;
                    JSONObject goodsObject = getStoreSpecGoodsId(storeId, getGoodsCommonIdList(list));
                    for (GoodsCommon info : list) {
                        jsonObject = new JSONObject();
                        JSONArray specArray = analysisGoodsSpecList(info.getSpecName(),
                                info.getSpecValue(), goodsObject.getJSONObject("goods_color").optString(LibSysUtils.toString(info.getId()), "0"));
                        jsonObject.put("goods_commonid", info.getId());
                        jsonObject.put("spec_list", specArray);
                        jsonObject.put("goods_name", info.getGoodsName());
                        jsonObject.put("goods_price", info.getGoodsPrice());
                        jsonObject.put("goods_marketprice", info.getGoodsMarketprice());
                        jsonObject.put("goods_list", goodsObject.getJSONObject("goods_list").getJSONObject(LibSysUtils.toString(info.getId())));
                        jsonObject.put("images", goodsObject.getJSONObject("goods_image").getJSONObject(LibSysUtils.toString(info.getId())));
                        jsonObject.put("goods_image", WkUtil.combineUrl(info.getGoodsImage(), UploadTypeEnum.SHOP, true));
                        jsonObject.put("coin_price", info.getCoin_price());
                        jsonArray.add(jsonObject);
                    }
                }
            }
            object.put("list", jsonArray);
            object.put("goods_count", size);
            WKCache.add_room_goods_list(storeId, object.toString());
        } else {
            object = JSONObject.fromObject(goodsCommon);
        }
        return object;
    }

    /**
     * 获得直播列表展示商品
     */
    public JSONObject getLiveListGoods(int userId) {
        int storeId = storeService.findStoreIdByUserId(userId);
        JSONObject object = new JSONObject();
        JSONObject goodsObj = getRoomGoodsCommonList(storeId);
        JSONArray goodsArr = goodsObj.getJSONArray("list");
        Iterator it = goodsArr.iterator();
        JSONArray imgArr = new JSONArray();
        int i = 0;
        while (it.hasNext() && i < 3) {
            JSONObject ob = (JSONObject) it.next();
            imgArr.add(ob.getString("goods_image"));
            i++;
        }
        object.put("goods_count", goodsObj.getInt("goods_count"));
        object.put("goods_img", imgArr);
        return object;
    }

    /**
     * 获取公共商品ID列表
     */
    private List<Integer> getGoodsCommonIdList(List<GoodsCommon> list) {
        List<Integer> goodsCommonIdList = new ArrayList<>();
        for (GoodsCommon info : list) {
            goodsCommonIdList.add(info.getId());
        }
        return goodsCommonIdList;
    }

    /**
     * 店铺以规格ID为key商品ID为value列表
     */
    private JSONObject getStoreSpecGoodsId(int storeId, List<Integer> GoodsCommonIdList) {
        List<Goods> list = goodsMapper.selectGoodsListByGoodsCommonIds(storeId, GoodsCommonIdList);
        JSONObject object = new JSONObject();
        JSONObject imgObj = new JSONObject();
        JSONObject goodsObject = new JSONObject();
        JSONObject imageObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONObject colorObject = new JSONObject();
        int curGoodsCommonId = 0;
        int color_id = 0;
        for (Goods info : list) {
            String spec = assembleSpec(info.getGoodsSpec());
            if (curGoodsCommonId == info.getGoodsCommonid()) {
                jsonObject.put(spec, info.getId());
                imgObj.put(info.getColorId(), WkUtil.combineUrl(info.getGoodsImage(), UploadTypeEnum.SHOP, true));
            } else {
                if (curGoodsCommonId != 0) {
                    goodsObject.put(curGoodsCommonId, jsonObject);
                    imageObject.put(curGoodsCommonId, imgObj);
                    colorObject.put(curGoodsCommonId, color_id);
                }
                jsonObject = new JSONObject();
                imgObj = new JSONObject();
                jsonObject.put(spec, info.getId());
                imgObj.put(info.getColorId(), WkUtil.combineUrl(info.getGoodsImage(), UploadTypeEnum.SHOP, true));
                curGoodsCommonId = info.getGoodsCommonid();
            }
            color_id = info.getColorId();
        }
        goodsObject.put(curGoodsCommonId, jsonObject);
        imageObject.put(curGoodsCommonId, imgObj);
        colorObject.put(curGoodsCommonId, color_id);
        object.put("goods_list", goodsObject);
        object.put("goods_image", imageObject);
        object.put("goods_color", colorObject);
        return object;
    }

    /**
     * 解析商品规格返回列表
     */
    private JSONArray analysisGoodsSpecList(String spec_name, String spec_value, String color_id) {
        JSONArray specArray = new JSONArray();
        if (!LibSysUtils.isNullOrEmpty(spec_name)) {
            JSONObject specName = JSONObject.fromObject(spec_name);
            JSONObject specValue = JSONObject.fromObject(spec_value);
            Iterator iterator = specName.keys();
            JSONObject valueObject;
            while (iterator.hasNext()) {
                JSONObject specObject = new JSONObject();
                String key = LibSysUtils.toString(iterator.next());
                specObject.put("name", specName.getString(key));
                valueObject = specValue.getJSONObject(key);
                specObject.put("value", valueObject);
                if (valueObject.size() > 0 && valueObject.has(color_id)) {
                    specObject.put("is_color", true);
                }
                specArray.add(specObject);
            }
        }
        return specArray;
    }

    /**
     * 组装商品规格ID 以|分割
     */
    private String assembleSpec(String goodsSpec) {
        if (LibSysUtils.isNullOrEmpty(goodsSpec)) {
            return "0";
        }
        JSONObject specObject = JSONObject.fromObject(goodsSpec);
        return StringUtils.join(specObject.keySet().toArray(), "|");
    }

    /**
     * 获取商品详情通过子商品ID
     */
    public JSONObject getGoodsCommonInfoByGoodsId(int goodsId, String lang_code) throws Exception {
        Goods goodsInfo = goodsMapper.findGoodsInfoByGoodsId(goodsId);
        if (goodsInfo == null) {
            return LibSysUtils.getResultJSON(ResultCode.goods_no_exist, LibProperties.getLanguage(lang_code, "weking.lang.app.goods.no.exist"));
        }
        return getGoodsCommonDetail(goodsInfo.getGoodsCommonid(), lang_code);
    }

    /**
     * 商品详情
     *
     * @param goodsCommonId 商品公共ID
     */
    public JSONObject getGoodsCommonDetail(int goodsCommonId, String lang_code) throws Exception {
        GoodsCommonExtend goodsCommonInfo = goodsCommonMapper.findGoodsInfoByGoodsCommonId(goodsCommonId);
        if (goodsCommonInfo == null) {
            return LibSysUtils.getResultJSON(ResultCode.goods_no_exist, LibProperties.getLanguage(lang_code, "weking.lang.app.goods.no.exist"));
        }
        JSONObject goodsInfo = mosaicGoodsInfo(goodsCommonId, lang_code);
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("goods_name", goodsCommonInfo.getGoodsName());
        object.put("store_id", goodsCommonInfo.getStoreId());
        object.put("goods_price", goodsCommonInfo.getGoodsPrice());
        object.put("goods_marketprice", goodsCommonInfo.getGoodsMarketprice());
        object.put("spec_list", analysisGoodsSpecList(goodsCommonInfo.getSpecName(), goodsCommonInfo.getSpecValue(), goodsInfo.optString("color_id","0")));
        object.put("goods_body", getGoodsBody(goodsCommonInfo.getGoodsBody()));
        object.put("goods_jingle", goodsCommonInfo.getGoodsJingle());
        object.put("goods_freight", getGoodsFreight(goodsCommonInfo.getStoreId(), goodsCommonInfo.getGoodsPrice(), goodsCommonInfo.getGoodsFreight()));
        object.put("goods_image", WkUtil.combineUrl(goodsCommonInfo.getGoodsImage(), UploadTypeEnum.SHOP, true));
        object.put("goods_vat", goodsCommonInfo.getGoodsVat());
        if (goodsCommonInfo.getGoodsParentId() > 0) {
            object.put("goods_images", getGoodsCommonImages(goodsCommonInfo.getGoodsParentId()));
        } else {
            object.put("goods_images", getGoodsCommonImages(goodsCommonId));
        }
        object.put("goods_state", goodsCommonInfo.getGoodsState());
        object.put("coin_price", goodsCommonInfo.getCoin_price());
        //object.put("goods_attr", getGoodsAttr(goodsCommonInfo.getGoodsAttr()));
        object.putAll(goodsInfo);
        return object;
    }

    /**
     * 处理商品属性
     */
    private JSONArray getGoodsAttr(String goods_attr) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonObject = JSONObject.fromObject(goods_attr);
        Iterator keys = jsonObject.keys();
        while (keys.hasNext()) {
            JSONObject obj = jsonObject.getJSONObject(LibSysUtils.toString(keys.next()));
            jsonObj.put("name", obj.getString("name"));
            obj.remove("name");
            Collection attrColl = obj.values();
            for (Object info : attrColl) {
                jsonObj.put("value", LibSysUtils.toString(info));
            }
            jsonArray.add(jsonObj);
        }
        return jsonArray;
    }

    /**
     * 获得商品运费
     */
    private BigDecimal getGoodsFreight(int storeId, BigDecimal goodsPrice, BigDecimal goodsFreight) {
        String storeFreePrice = storeService.getStoreFieldInfo(storeId, "store_free_price");
        if (storeFreePrice != null) {
            BigDecimal freePrice = new BigDecimal(storeFreePrice);
            if (goodsPrice.compareTo(freePrice) >= 0) {
                return new BigDecimal("0");
            }
        }
        return goodsFreight;
    }

    /**
     * 获得商品详情
     */
    private JSONObject getGoodsBody(String goodsBody) {
        if (LibSysUtils.isNullOrEmpty(goodsBody)) {
            return null;
        }
        return JSONObject.fromObject(goodsBody);
    }

    /**
     * 组合子商品
     */
    private JSONObject mosaicGoodsInfo(int goodsCommonId, String lang_code) throws Exception {
        JSONObject object = new JSONObject();
        JSONObject specObject = new JSONObject();
        JSONObject imgObj = new JSONObject();
        int color_id = 0;
        List<Goods> list = goodsMapper.selectListByGoodsCommonId(goodsCommonId);
        if (list != null && list.size() > 0) {
            int goodsSalenum = 0;
            for (Goods info : list) {
                String spec = assembleSpec(info.getGoodsSpec());
                specObject.put(spec, info.getId());
                imgObj.put(info.getColorId(), WkUtil.combineUrl(info.getGoodsImage(), UploadTypeEnum.SHOP, true));
                color_id = info.getColorId();
                goodsSalenum = goodsSalenum + info.getGoodsSalenum();
            }
            object.put("goods_list", specObject);
            object.put("images", imgObj);
            object.put("color_id", color_id);
            object.put("store_id", list.get(0).getStoreId());
            object.put("store_name", list.get(0).getStoreName());
            object.put("goods_salenum", goodsSalenum);
        } else {
            return LibSysUtils.getResultJSON(ResultCode.data_error,LibProperties.getLanguage(lang_code, "weking.lang.app.goods.no.exist"));
        }
        return object;
    }

    /**
     * 获取商品轮播图片
     */
    private JSONArray getGoodsCommonImages(int goodsCommonId) {
        JSONArray jsonArray = new JSONArray();
        List<String> list = goodsImagesMapper.selectListByGoodsCommonId(goodsCommonId);
        for (String info : list) {
            jsonArray.add(WkUtil.combineUrl(info, UploadTypeEnum.SHOP, false));
        }
        return jsonArray;
    }

    /**
     * 加入购物车
     */
    public JSONObject addCart(int userId, int goodsId, int goodsNum, String lang_code) {
        Goods goods = goodsMapper.findGoodsInfoByGoodsId(goodsId);
        if (goods == null) {
            return LibSysUtils.getResultJSON(ResultCode.goods_no_exist, LibProperties.getLanguage(lang_code, "weking.lang.app.goods.no.exist"));
        }
        int re = cartMapper.updateGoodsNumByGoodsId(goodsId, userId, goodsNum);
        if (re == 0) {
            Cart record = new Cart();
            record.setBuyerId(userId);
            record.setGoodsId(goodsId);
            record.setGoodsImage(goods.getGoodsImage());
            record.setGoodsNum((short) goodsNum);
            record.setGoodsPrice(goods.getGoodsPrice());
            record.setStoreId(goods.getStoreId());
            record.setStoreName(goods.getStoreName());
            record.setGoodsName(goods.getGoodsName());
            record.setCoin_price(goods.getCoin_price());
            cartMapper.insert(record);
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 更新购物车商品数量
     */
    public JSONObject updateCartGoodsNum(int userId, int goodsId, int goodsNum) {
        cartMapper.updateGoodsNumByGoodsId(goodsId, userId, goodsNum);
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 移除购物车
     */
    public JSONObject removeCart(int userId, String goodsIds) {
        if (!LibSysUtils.isNullOrEmpty(goodsIds)) {
            deleteByUserIdAndGoodsId(userId, goodsIds.split(","));
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 删除购物车商品
     */
    public int deleteByUserIdAndGoodsId(int userId, String[] goodsIds) {
        return cartMapper.deleteByUserIdAndGoodsId(userId, goodsIds);
    }

    /**
     * 用户购物车列表
     */
    public JSONObject getCartList(int userId) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray jsonArray = new JSONArray();
        Map<Integer, Short> map = getCartGoodsEqGoodsNum(userId);
        if (map != null) {
            List<Goods> list = goodsMapper.selectGoodsListByGoodsIds(map);
            JSONObject jsonObject = new JSONObject();
            JSONObject goodsObject;
            JSONArray goodsArray = new JSONArray();
            int curStoreId = 0;
            for (Goods info : list) {
                if (curStoreId != info.getStoreId()) {
                    if (curStoreId != 0) {
                        jsonObject.put("goods_list", goodsArray);
                        jsonArray.add(jsonObject);
                    }
                    jsonObject = new JSONObject();
                    goodsArray = new JSONArray();
                    curStoreId = info.getStoreId();
                    jsonObject.put("store_id", curStoreId);
                    jsonObject.put("store_name", info.getStoreName());
                }
                goodsObject = new JSONObject();
                goodsObject.put("goods_name", info.getGoodsName());
                goodsObject.put("goods_price", getGoodsPrice(info));
                goodsObject.put("goods_num", map.get(info.getId()));
                goodsObject.put("goods_id", info.getId());
                goodsObject.put("goods_image", WkUtil.combineUrl(info.getGoodsImage(), UploadTypeEnum.SHOP, true));
                goodsObject.put("goods_spec", assembleSpecValue(info.getGoodsSpec()));
                goodsObject.put("goods_marketprice", info.getGoodsMarketprice());
                goodsObject.put("coin_price", info.getCoin_price());
                goodsArray.add(goodsObject);
            }
            jsonObject.put("goods_list", goodsArray);
            jsonArray.add(jsonObject);
        }
        object.put("list", jsonArray);
        return object;
    }

    /**
     * 获得用户购物车商品对应数量
     */
    private Map<Integer, Short> getCartGoodsEqGoodsNum(int userId) {
        List<Cart> cartList = cartMapper.selectListByUserId(userId);
        if (cartList == null || cartList.size() == 0) {
            return null;
        }
        Map<Integer, Short> map = new HashMap<>();
        for (Cart info : cartList) {
            map.put(info.getGoodsId(), info.getGoodsNum());
        }
        return map;
    }

    /**
     * 获取分类列表
     *
     * @param gc_pid 类别父ID
     */
    public JSONObject getGoodsClass(int gc_pid) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        List<GoodsClass> list = goodsClassMapper.selectChildListByGcParentId(gc_pid);
        for (GoodsClass info : list) {
            jsonObject = new JSONObject();
            jsonObject.put("gc_id", info.getId());
            jsonObject.put("gc_name", info.getGcName());
            jsonObject.put("gc_image", WkUtil.combineUrl(info.getGcThumb(), UploadTypeEnum.SHOP, true));
            jsonArray.add(jsonObject);
        }
        object.put("list", jsonArray);
        return object;
    }

    /**
     * 获取推荐商品列表
     * flg 为true  则为游客  不显示SCA
     */
    public JSONObject getGoodsCommendList(int index, int count,Boolean flg) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        List<GoodsCommon> list = goodsCommonMapper.selectGoodsCommendList(index, count);
        object.put("list", handleGoodsCommonListData(list));
        if(flg){
            for(GoodsCommon info:list){
                info.setCoin_price(new BigDecimal(0));
            }
        }
        return object;
    }

    /**
     * 获取店铺推荐商品列表
     */
    public JSONArray getStoreGoodsCommendList(int storeId, int index, int count) {
        List<GoodsCommon> list = goodsCommonMapper.selectStoreGoodsCommendList(storeId, index, count);
        return handleGoodsCommonListData(list);
    }

    /**
     * 获取分类商品列表
     *  flg为true  则为游客
     */
    public JSONObject getClassGoodsCommonList(int gc_id, int index, int count,Boolean flg) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        List<GoodsCommon> list = goodsCommonMapper.selectGoodsCommonListByGcId1(gc_id, index, count);
        if(flg){
            for(GoodsCommon info:list){
                info.setCoin_price(new BigDecimal(0));
            }
        }
        object.put("list", handleGoodsCommonListData(list));
        return object;
    }

    /**
     * 处理商品列表数据
     */
    private JSONArray handleGoodsCommonListData(List<GoodsCommon> list) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (GoodsCommon info : list) {
            jsonObject = getGoodsCommonInfo(info);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /**
     * 返回商品公共信息
     */
    private JSONObject getGoodsCommonInfo(GoodsCommon info) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("goods_name", info.getGoodsName());
        jsonObject.put("goods_price", info.getGoodsPrice());
        jsonObject.put("goods_parentid", info.getGoodsParentId());
        jsonObject.put("goods_marketprice", info.getGoodsMarketprice());
        jsonObject.put("goods_jingle", info.getGoodsJingle());
        jsonObject.put("goods_commonid", info.getId());
        jsonObject.put("goods_image", WkUtil.combineUrl(info.getGoodsImage(), UploadTypeEnum.SHOP, false));
        jsonObject.put("coin_price", info.getCoin_price());
        return jsonObject;
    }

    /**
     * 订单商品按照店铺整理
     */
    public JSONObject getOrderGoodsByShop(String goods_info, int country_id, String lang_code) {
        JSONObject goodsObj = JSONObject.fromObject(goods_info);
        Map<Integer, Integer> goodsInfo = jsonObjectToMap(goodsObj);
        List<Goods> list = goodsMapper.selectGoodsListByGoodsIds(goodsInfo);
        JSONObject goodsParentList = getGoodsStorageList(list);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject goodsObject;
        JSONArray goodsArray = new JSONArray();
        int curStoreId = 0;
        BigDecimal goodsPrice; //商品价格
        BigDecimal goodsTotalPrice; //商品总价格
        BigDecimal goodsCoinPrice ; //商品虚拟币价格
        BigDecimal goodsTotalCoinPrice; //商品总虚拟币价格
        BigDecimal storeFreight = new BigDecimal("0"); //店铺运费
        BigDecimal goodsFreight; //商品运费
        BigDecimal storeTotalAmount = new BigDecimal("0"); //店铺总金额
        BigDecimal orderTotalAmount = new BigDecimal("0"); //订单总金额
        BigDecimal coinTotalAmount = new BigDecimal("0"); //店铺虚拟币总金额
        BigDecimal orderCoinTotalAmount = new BigDecimal("0"); //订单虚拟币总金额
        boolean isEffective = true;
        int goodsStorage;
        JSONObject goodsJsonInfo = new JSONObject();
        for (Goods info : list) {
            goodsObject = new JSONObject();
            Integer GoodsNum = goodsInfo.get(info.getId());
            if (curStoreId != info.getStoreId()) {
                if (curStoreId != 0) {
                    storeFreight = getGoodsFreight(curStoreId, storeTotalAmount, storeFreight);
                    storeTotalAmount = storeTotalAmount.add(storeFreight);
                    orderTotalAmount = orderTotalAmount.add(storeTotalAmount);
                    orderCoinTotalAmount = orderCoinTotalAmount.add(coinTotalAmount);
                    jsonObject.put("store_freight", storeFreight);
                    jsonObject.put("store_amount", storeTotalAmount);
                    jsonObject.put("total_coin_amount", coinTotalAmount);
                    jsonObject.put("goods_list", goodsArray);
                    jsonArray.add(jsonObject);
                }
                jsonObject = new JSONObject();
                goodsArray = new JSONArray();
                curStoreId = info.getStoreId();
                storeTotalAmount = new BigDecimal("0");
                coinTotalAmount = new BigDecimal("0");
                jsonObject.put("store_id", curStoreId);
                jsonObject.put("store_name", info.getStoreName());
            }
            goodsPrice = getGoodsPrice(info);
            goodsCoinPrice = info.getCoin_price();
            goodsTotalPrice = goodsPrice.multiply(new BigDecimal(GoodsNum));
            goodsTotalCoinPrice = goodsCoinPrice.multiply(new BigDecimal(GoodsNum));
            storeTotalAmount = storeTotalAmount.add(goodsTotalPrice);
            coinTotalAmount = coinTotalAmount.add(goodsTotalCoinPrice);
            goodsFreight = getGoodsFreight(curStoreId, goodsPrice, info.getGoodsFreight());
            if (goodsFreight.compareTo(storeFreight) == 1) {
                storeFreight = goodsFreight;
            }
            if (info.getGoodsParentid() > 0) {
                goodsJsonInfo.put(info.getGoodsParentid(), GoodsNum);
                goodsStorage = goodsParentList.getInt(LibSysUtils.toString(info.getGoodsParentid()));
            } else {
                goodsJsonInfo.put(info.getId(), GoodsNum);
                goodsStorage = info.getGoodsStorage();
            }
            if (goodsStorage < GoodsNum) { //检测库存
                isEffective = false;
                goodsObject.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.insufficient.stock"));
            }
            if (info.getSaleId() != 0 && country_id != info.getSaleId()) { //检测出售区域
                isEffective = false;
                goodsObject.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.not.sale.area"));
            }
            goodsObject.put("goods_id", info.getId());
            goodsObject.put("goods_num", GoodsNum);
            goodsObject.put("goods_parentid", info.getGoodsParentid());
            goodsObject.put("goods_name", info.getGoodsName());
            goodsObject.put("goods_price", goodsPrice);
            BigDecimal pay_price = goodsPrice.multiply(new BigDecimal(GoodsNum));
            goodsObject.put("pay_price", pay_price);
            goodsObject.put("sca_price", goodsCoinPrice);
            goodsObject.put("goods_marketprice", info.getGoodsMarketprice());
            goodsObject.put("goods_spec", assembleSpecValue(info.getGoodsSpec()));
            goodsObject.put("goods_image", WkUtil.combineUrl(info.getGoodsImage(), UploadTypeEnum.SHOP, true));
            goodsObject.put("goods_storage", goodsStorage);
            goodsObject.put("coin_price", goodsCoinPrice);
            goodsObject.put("total_coin_price", goodsTotalCoinPrice);
            goodsArray.add(goodsObject);
        }
        storeFreight = getGoodsFreight(curStoreId, storeTotalAmount, storeFreight);
        storeTotalAmount = storeTotalAmount.add(storeFreight);
        orderTotalAmount = orderTotalAmount.add(storeTotalAmount);
        orderCoinTotalAmount = orderCoinTotalAmount.add(coinTotalAmount);
        jsonObject.put("store_freight", storeFreight);
        jsonObject.put("store_amount", storeTotalAmount);
        jsonObject.put("total_coin_amount", coinTotalAmount);
        jsonObject.put("goods_list", goodsArray);
        jsonArray.add(jsonObject);
        JSONObject object = new JSONObject();
        object.put("list", jsonArray);
        object.put("order_amount", orderTotalAmount);
        object.put("order_coin_amount", orderCoinTotalAmount);
        object.put("is_effective", isEffective);
        object.put("goods_info", goodsJsonInfo.toString());
        return object;
    }

    /**
     * 获取商品库存
     * 以ID 为键，库存为值
     */
    public JSONObject getGoodsStorageList(List<Goods> list) {
        JSONObject object = new JSONObject();
        List<Integer> goodsIds = new ArrayList<>();
        for (Goods info : list) {
            if (info.getGoodsParentid() > 0) {
                goodsIds.add(info.getGoodsParentid());
            }
        }
        if (goodsIds.size() > 0) {
            List<Goods> goodsParentList = goodsMapper.selectParentGoodsList(goodsIds);
            for (Goods info : goodsParentList) {
                object.put(info.getId(), info.getGoodsStorage());
            }
        }
        return object;
    }

    /**
     * 获得商品价格
     */
    private BigDecimal getGoodsPrice(Goods info) {
        if (info.getGoodsPromotionType() == 0) {
            return info.getGoodsPrice();
        }
        return info.getGoodsPromotionPrice();
    }


    /**
     * 处理商品规格
     */
    private String assembleSpecValue(String goods_spec) {
        if (LibSysUtils.isNullOrEmpty(goods_spec)) {
            return "";
        }
        JSONObject specObject = JSONObject.fromObject(goods_spec);
        return StringUtils.join(specObject.values().toArray(), " ");
    }

    /**
     * 获取商品详情
     * flg为true  则为游客
     */
    public JSONObject getGoodsDetail(int goodsId, String lang_code,Boolean flg) {
        Map<String, String> goodsInfoMap = getGoodsAllCacheInfo(goodsId);
        //Goods goodsInfo = goodsMapper.findGoodsInfoByGoodsId(goodsId);
        if (goodsInfoMap == null) {
            return LibSysUtils.getResultJSON(ResultCode.goods_no_exist, LibProperties.getLanguage(lang_code, "weking.lang.app.goods.no.exist"));
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("goods_id", LibSysUtils.toInt(goodsInfoMap.get("goods_id")));
        object.put("goods_name", goodsInfoMap.get("goods_name"));
        object.put("goods_price", goodsInfoMap.get("goods_price"));
        object.put("goods_image", WkUtil.combineUrl(goodsInfoMap.get("goods_image"), UploadTypeEnum.SHOP, true));
        object.put("goods_storage", goodsInfoMap.get("goods_storage"));
        object.put("coin_price", goodsInfoMap.get("coin_price"));
        if(flg){
            object.put("coin_price", "0");
        }
        return object;
    }

    /**
     * 批量减少商品库存数量
     *
     * @param goodsInfo goods_id=>goods_num的键值对
     */
    public Boolean updateGoodsNumBatch(String goodsInfo) {
        JSONObject goodsObj = JSONObject.fromObject(goodsInfo);
        Map<Integer, Integer> goodsMap = jsonObjectToMap(goodsObj);
        int re = goodsMapper.updateGoodsNumBatch(goodsMap);
        if (re == goodsMap.size()) {
            deductionGoodsCacheStorage(goodsMap, false);
            return true;
        }
        return false;
    }

    /**
     * 扣减缓存商品库存
     *
     * @param isAdd 是否增加
     */
    private void deductionGoodsCacheStorage(Map<Integer, Integer> goodsMap, Boolean isAdd) {
        if (goodsMap.size() > 0) {
            int goods_storage;
            for (Integer goodsId : goodsMap.keySet()) {
                Integer goodsNum = goodsMap.get(goodsId);//得到每个key多对用value的值
                if (!isAdd) {
                    goods_storage = -goodsNum;
                } else {
                    goods_storage = goodsNum;
                }
                WKCache.deduction_goods_info(goodsId, "goods_storage", goods_storage);
            }
        }
    }


    /**
     * 批量更新商品销售数量
     *
     * @param goodsMap goods_id=>goods_num的键值对
     */
    public int updateGoodsSaleNumBatch(Map<Integer, Integer> goodsMap) {
        return goodsMapper.updateGoodsSaleNumBatch(goodsMap);
    }

    /**
     * 选择直播商品
     */
    public JSONObject choiceLiveGoodsCommon(int userId, String goods_commonids) {
        int storeId = storeService.findStoreIdByUserId(userId);
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        if (storeId != 0) {
            String[] goodsCommonArray = goods_commonids.split(",");
            goodsCommonMapper.clearIsLiveGoodsCommon(storeId); //清除原先直播商品
            WKCache.del_room_goods_list(storeId);
            goodsCommonMapper.updateIsLiveByGoodsCommonIds(storeId, goodsCommonArray);
        }
        return object;
    }

    /**
     * json对象转map
     */
    public Map<Integer, Integer> jsonObjectToMap(JSONObject jsonObject) {
        Map<Integer, Integer> map = new HashMap<>();
        Iterator it = jsonObject.keys();
        // 遍历jsonObject数据，添加到Map对象
        while (it.hasNext()) {
            String key = LibSysUtils.toString(it.next());
            Integer value = jsonObject.getInt(key);
            map.put(LibSysUtils.toInt(key), value);
        }
        return map;
    }

    /**
     * 设置当前直播商品
     */
    public JSONObject setCurLiveGoodsCommon(int userId, int liveId, int goodsCommonId) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        int anchor_id = LibSysUtils.toInt(WKCache.get_room(liveId, "user_id"));
        if (anchor_id == userId) {
            GoodsCommon goodsCommon = goodsCommonMapper.findGoodsCommonById(goodsCommonId);
            if (goodsCommon != null) {
                String live_stream_id = WKCache.get_room(liveId, "live_stream_id");
                Map<String, String> map = new HashMap<>();
                map.put("goods_commonid", LibSysUtils.toString(goodsCommon.getId()));
                map.put("goods_image", goodsCommon.getGoodsImage());
                map.put("goods_price", LibSysUtils.toString(goodsCommon.getGoodsPrice()));
                map.put("coin_price", LibSysUtils.toString(goodsCommon.getCoin_price()));
                WKCache.add_live_goods(liveId, map);
                JSONObject imObject = new JSONObject();
                imObject.put("im_code", IMCode.live_goods);
                imObject.put("goods_commonid", goodsCommon.getId());
                imObject.put("goods_image", WkUtil.combineUrl(goodsCommon.getGoodsImage(), UploadTypeEnum.SHOP, true));
                imObject.put("goods_price", LibSysUtils.toString(goodsCommon.getGoodsPrice()));
                imObject.put("coin_price", LibSysUtils.toString(goodsCommon.getCoin_price()));
                System.out.println("live_goods:" + imObject.toString());
                WkImClient.sendRoomMsg(live_stream_id, imObject.toString(), 1);
                imObject.remove("im_code");
                object.putAll(imObject);
            }
        }
        return object;
    }

    /**
     * 查询店铺商品总数量
     */
    public int selectStoreGoodsCommonNum(int storeId) {
        return goodsCommonMapper.selectStoreGoodsCommonNum(storeId);
    }

    /**
     * 搜索商品列表
     * flg  为true  则为游客模式  不显示sca数
     */
    public JSONObject getSearchGoodsCommonList(int storeId, String query, String price, int index, int count,Boolean flg) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        String goods_name = null;
        String[] priceArr = null;
        if (!LibSysUtils.isNullOrEmpty(query)) {
            goods_name = query;
        }
        if (!LibSysUtils.isNullOrEmpty(price)) {
            priceArr = price.split("-");
        }
        List<GoodsCommon> list = goodsCommonMapper.selectSearchGoodsCommonList(storeId, goods_name, priceArr, index, count);
        if(flg){
            for(GoodsCommon info:list){
                info.setCoin_price(new BigDecimal(0));
            }
        }
        object.put("list", handleGoodsCommonListData(list));
        return object;
    }

    /**
     * 批量增加商品库存
     */
    public int updateIncGoodsStorageBatch(Map<Integer, Integer> goodsMap) {
        int re = goodsMapper.updateIncGoodsStorageBatch(goodsMap);
        if (re > 0) {
            deductionGoodsCacheStorage(goodsMap, true);
        }
        return re;
    }

    /**
     * 代销商品列表
     */
    public JSONObject getProprietaryGoodsCommonList(int userId, String goodsName, int index, int count) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        int storeId = storeService.getOwnStoreId();
        List<GoodsCommon> list = goodsCommonMapper.selectSearchGoodsCommonList(storeId, goodsName, null, index, count);
        int userStoreId = storeService.findStoreIdByUserId(userId);
        List<Integer> parentIdList = goodsCommonMapper.selectGoodsParentIdList(userStoreId);
        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();
        for (GoodsCommon info : list) {
            jsonObject = getGoodsCommonInfo(info);
            if (parentIdList == null) {
                jsonObject.put("consignment", false);
            } else {
                jsonObject.put("consignment", parentIdList.contains(info.getId()));
            }
            jsonArray.add(jsonObject);
        }
        object.put("list", jsonArray);
        return object;
    }

    /**
     * 代售
     */
    @Transactional
    public JSONObject addConsignee(int storeId, int goodsCommonId) {
        GoodsCommon goodsCommon = new GoodsCommon();
        goodsCommon.setStoreId(storeId);
        goodsCommon.setGoodsCommonId(goodsCommonId);
        goodsCommon.setGoodsAddTime(LibDateUtils.getLibDateTime());
        int re = goodsCommonMapper.insertCopy(goodsCommon);
        if (re > 0) {
            int commonId = goodsCommon.getId();
            String store_name = storeService.getStoreFieldInfo(storeId, "store_name");
            goodsMapper.insertCopy(storeId, store_name, goodsCommonId, commonId, LibDateUtils.getLibDateTime());
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 取消代售
     */
    @Transactional
    public JSONObject cacheConsignee(int storeId, int goodsParentId) {
        Integer goodsCommonId = goodsCommonMapper.findIdByGoodsParentId(storeId, goodsParentId);
        if (goodsCommonId != null) {
            int re = goodsCommonMapper.delGoodsCommon(goodsCommonId, storeId);
            if (re > 0) {
                goodsMapper.delGoodsByGoodsCommonId(goodsCommonId);
            }
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 验证该商品是否可代销
     */
    public Boolean checkIsConsignee(int storeId, int goodsCommonId) {
        int ownStoreId = storeService.getOwnStoreId();
        if (ownStoreId == storeId) {
            return false;
        }
//        Integer id = goodsCommonMapper.findIdByGoodsParentId(storeId,goodsCommonId);
//        if(id != null){
//            return false;
//        }
        //验证商品是否符合代销
        GoodsCommon info = goodsCommonMapper.findGoodsCommonById(goodsCommonId);
        if (info.getStoreId() != ownStoreId) {
            return false;
        }
        return true;
    }

    /**
     * 获得商品某个信息
     */
    public String getGoodsFieldInfo(int goodsId, String field) {
        String val = WKCache.get_goods_info(goodsId, field);
        if (val == null) {
            Map<String, String> map = setGoodsCacheInfo(goodsId);
            if (map != null) {
                val = map.get(field);
            }
        }
        return val;
    }

    /**
     * 获取商品所有缓存信息
     */
    public Map<String, String> getGoodsAllCacheInfo(int goodsId) {
        Map<String, String> goodsInfoMap = WKCache.get_goods_allInfo(goodsId);
        if (goodsInfoMap.size() == 0 || goodsInfoMap.get("goods_id") == null) {
            goodsInfoMap = setGoodsCacheInfo(goodsId);
        }
        return goodsInfoMap;
    }

    /**
     * 获得商品缓存信息
     */
    public Map<String, String> getGoodsCacheInfo(int goodsId, String... fields) {
        List<String> list = WKCache.get_goods_info(goodsId, fields);
        Map<String, String> map;
        if (list.get(0) == null) {
            map = setGoodsCacheInfo(goodsId);
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
     * 设置商品缓存信息
     */
    private Map<String, String> setGoodsCacheInfo(int goodsId) {
        Map<String, String> map = new HashMap<>();
        Goods goodsInfo = goodsMapper.findGoodsInfoByGoodsId(goodsId);
        if (goodsInfo == null) {
            return null;
        }
        map.put("goods_id", LibSysUtils.toString(goodsInfo.getId()));
        map.put("goods_parentid", LibSysUtils.toString(goodsInfo.getGoodsParentid()));
        map.put("goods_name", LibSysUtils.toString(goodsInfo.getGoodsName()));
        map.put("goods_price", LibSysUtils.toString(getGoodsPrice(goodsInfo)));
        map.put("goods_marketprice", LibSysUtils.toString(goodsInfo.getGoodsMarketprice()));
        map.put("goods_image", LibSysUtils.toString(goodsInfo.getGoodsImage()));
        map.put("coin_price", LibSysUtils.toString(goodsInfo.getCoin_price()));
        if (goodsInfo.getGoodsParentid() > 0) {
            map.put("goods_storage", getGoodsFieldInfo(goodsInfo.getGoodsParentid(), "goods_storage"));
        } else {
            map.put("goods_storage", LibSysUtils.toString(goodsInfo.getGoodsStorage()));
        }

        WKCache.add_goods_info(goodsId, map);
        return map;
    }


    public static void main(String[] args) {
//        BigDecimal a = new BigDecimal("5");
//        BigDecimal storeTotalAmount = new BigDecimal("0");
//        System.out.println(storeTotalAmount.add(a));
//        JSONObject jsonOne = new JSONObject();
//        JSONObject jsonTwo = new JSONObject();
//        jsonOne.put("222","蓝色");
//        jsonOne.put("224","绿色");
//        jsonOne.put("227","橙色");
//        jsonTwo.put("1",jsonOne);
//        System.out.println(jsonTwo);
    }
}
