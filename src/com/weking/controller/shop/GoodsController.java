package com.weking.controller.shop;

import com.weking.controller.out.OutControllerBase;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.service.shop.GoodsService;
import com.weking.service.shop.StoreService;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
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
@RequestMapping({"/goods","/mall/goods"})
public class GoodsController extends OutControllerBase {

    @Resource
    private GoodsService goodsService;
    @Resource
    private StoreService storeService;

    /**
     * 代销商品列表
     */
    @RequestMapping("/getProprietaryGoodsCommonList")
    public void getProprietaryGoodsCommonList(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.getInt("user_id");
            String query = getParameter(request,"query","");
            int index = getParameter(request,"index",0);
            int count = getParameter(request,"count",10);
            object = goodsService.getProprietaryGoodsCommonList(userId,query,index,count);
        }
        out(response,object,api_version);
    }

    /**
     * 代售平台商品
     */
    @RequestMapping("/consignee")
    public void consignee(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.getInt("user_id");
            int goodsCommonId = getParameter(request,"goods_commonid",0);
            int type = getParameter(request,"type",1);
            int storeId = storeService.findStoreIdByUserId(userId);
            if(type == 1){
                if(goodsService.checkIsConsignee(storeId,goodsCommonId)){
                    object = goodsService.addConsignee(storeId,goodsCommonId);
                }else{
                    object = LibSysUtils.getResultJSON(ResultCode.success);
                }
            }else{
                object = goodsService.cacheConsignee(storeId,goodsCommonId);
            }
        }
        out(response,object,api_version);
    }

    /**
     * 商城首页
     */
    @RequestMapping("/index")
    public void index(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object ;
        if (access_token != null) {
            object = WkUtil.checkToken(access_token);
            if (object.optInt("code") == 0) {
                String lang_code = object.optString("lang_code");
                object = goodsService.index(lang_code,false);
            }
        }else {
            object = goodsService.index("zh_CN",true);
        }
        out(response,object,api_version);
    }

    /**
     * 直播间商品列表
     */
    @RequestMapping("/roomGoodsList")
    public void roomGoodsList(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int storeId = getParameter(request,"store_id",0);
            object = goodsService.getRoomGoodsCommonList(storeId);
        }
        out(response,object,api_version);
    }

    /**
     * 直播间商品数量
     */
    @RequestMapping("/roomGoodsCount")
    public void roomGoodsCount(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.getInt("user_id");
            object = goodsService.getRoomGoodsCommonCount(userId);
        }
        out(response,object,api_version);
    }
    /**
     * 用户名下商品列表
     */
    @RequestMapping("/getUserGoodsCommonList")
    public void getUserGoodsCommonList(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.getInt("user_id");
            String query = getParameter(request,"query","");
            int index = getParameter(request,"index",0);
            int count = getParameter(request,"count",10);
            object = goodsService.getUserGoodsCommonList(userId,query,index,count);
        }
        out(response,object,api_version);
    }
    /**
     * 店铺商品列表
     */
    @RequestMapping("/getStoreGoodsCommonList")
    public void getStoreGoodsCommonList(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int storeId = getParameter(request,"store_id",0);
            int index = getParameter(request,"index",0);
            int count = getParameter(request,"count",10);
            object = goodsService.getStoreGoodsCommonList(storeId,index,count);
        }
        out(response,object,api_version);
    }
    /**
     * 选择直播商品列表
     */
    @RequestMapping("/choiceLiveGoodsCommon")
    public void choiceLiveGoodsCommon(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.getInt("user_id");
            String goods_commonids = getParameter(request,"goods_commonids");
            object = goodsService.choiceLiveGoodsCommon(userId, goods_commonids);
        }
        out(response,object,api_version);
    }

    /**
     * 公共商品详情
     */
    @RequestMapping("/goodsCommonDetail")
    public void goodsCommonDetail(HttpServletRequest request, HttpServletResponse response)  throws Exception {
        String access_token = getParameter(request,"access_token");
        int goodsCommonId = getParameter(request,"goods_commonid",0);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object ;
        if(access_token!=null) {
            object = WkUtil.checkToken(access_token);
            if (object.optInt("code") == 0) {
                String lang_code = object.optString("lang_code");
                if (goodsCommonId == 0) {
                    int goodsId = getParameter(request, "goods_id", 0);
                    object = goodsService.getGoodsCommonInfoByGoodsId(goodsId, lang_code);
                } else {
                    object = goodsService.getGoodsCommonDetail(goodsCommonId, lang_code);
                }
            }
        }else {
            if (goodsCommonId == 0) {
                int goodsId = getParameter(request, "goods_id", 0);
                object = goodsService.getGoodsCommonInfoByGoodsId(goodsId, "zh_CN");
            } else {
                object = goodsService.getGoodsCommonDetail(goodsCommonId, "zh_CN");
            }
            object.put("coin_price", 0);
        }
        out(response,object,api_version);
    }

    /**
     * 添加购物车
     */
    @RequestMapping("/addCart")
    public void addCart(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.optInt("user_id");
            int goodsId = getParameter(request,"goods_id",0);
            String lang_code = object.optString("lang_code");
            int goodsNum = getParameter(request,"goods_num",1);
            object = goodsService.addCart(userId,goodsId,goodsNum,lang_code);
        }
        out(response,object,api_version);
    }
    /**
     * 更新购物车商品数量
     */
    @RequestMapping("/updateCartGoodsNum")
    public void updateCartGoodsNum(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.optInt("user_id");
            int goodsId = getParameter(request,"goods_id",0);
            int goodsNum = getParameter(request,"goods_num",1);
            object = goodsService.updateCartGoodsNum(userId,goodsId,goodsNum);
        }
        out(response,object,api_version);
    }
    /**
     * 移除购物车商品
     */
    @RequestMapping("/removeCart")
    public void removeCart(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        JSONObject object = WkUtil.checkToken(access_token);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        if(object.optInt("code") == 0){
            int userId = object.optInt("user_id");
            String goodsIds = getParameter(request,"goods_ids");
            object = goodsService.removeCart(userId,goodsIds);
        }
        out(response,object,api_version);
    }

    /**
     * 购物车列表
     */
    @RequestMapping("/cartList")
    public void cartList(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int userId = object.optInt("user_id");
            object = goodsService.getCartList(userId);
        }
        out(response,object,api_version);
    }

    /**
     * 商品分类
     */
    @RequestMapping("/goodsClass")
    public void goodsClass(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object ;
        if (access_token!=null) {
            object = WkUtil.checkToken(access_token);
            if (object.optInt("code") == 0) {
                int gc_pid = LibSysUtils.toInt(getParameter(request, "gc_pid"));
                object = goodsService.getGoodsClass(gc_pid);
            }
        }else {
            int gc_pid = LibSysUtils.toInt(getParameter(request,"gc_pid"));
            object = goodsService.getGoodsClass(gc_pid);
        }
        out(response,object,api_version);
    }

    /**
     * 类别商品列表
     */
    @RequestMapping("/classGoodsCommonList")
    public void classGoodsCommonList(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        int gc_id = LibSysUtils.toInt(getParameter(request,"gc_id"));
        int index = getParameter(request,"index",0);
        int count = getParameter(request,"count",10);
        JSONObject object ;
        if(access_token!=null) {
            object = WkUtil.checkToken(access_token);
            if (object.optInt("code") == 0) {
                object = goodsService.getClassGoodsCommonList(gc_id, index, count,false);
            }
        }else {
            object = goodsService.getClassGoodsCommonList(gc_id, index, count,true);
        }
        out(response,object,api_version);
    }

    /**
     * 首页商品类型列表
     */
    @RequestMapping("goodsTypeList")
    public void goodsTypeList(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        int type = getParameter(request, "type", 1);
        int index = getParameter(request, "index", 0);
        int count = getParameter(request, "count", 10);
        JSONObject object;
        if(access_token!=null) {
            object = WkUtil.checkToken(access_token);
            if (object.optInt("code") == 0) {
                switch (type) {
                    case 1:
                        object = goodsService.getGoodsCommendList(index, count,false);
                        break;
                    default:
                        object = LibSysUtils.getResultJSON(ResultCode.success);
                        object.put("list", new JSONArray());
                        break;
                }
            }
        }else {
            switch (type) {
                case 1:
                    object = goodsService.getGoodsCommendList(index, count,true);
                    break;
                default:
                    object = LibSysUtils.getResultJSON(ResultCode.success);
                    object.put("list", new JSONArray());
                    break;
            }
        }
        out(response,object,api_version);
    }

    /**
     * 商品详情
     */
    @RequestMapping("/goodsDetail")
    public void goodsDetail(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object;
        int goodsId = LibSysUtils.toInt(getParameter(request, "goods_id"));
        if(access_token!=null) {
            object = WkUtil.checkToken(access_token);
            if (object.optInt("code") == 0) {
                String lang_code = object.optString("lang_code");
                object = goodsService.getGoodsDetail(goodsId, lang_code, false);
            }
        }else {
            object = goodsService.getGoodsDetail(goodsId,"zh_CN",true);
        }
        out(response,object,api_version);
    }

    /**
     * 设置当前直播商品
     */
    @RequestMapping("/setCurLiveGoodsCommon")
    public void setCurLiveGoodsCommon(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if(object.optInt("code") == 0){
            int goodsCommonId =getParameter(request,"goods_commonid",0);
            int userId = object.getInt("user_id");
            int liveId = getParameter(request,"live_id",0);
            object = goodsService.setCurLiveGoodsCommon(userId,liveId,goodsCommonId);
        }
        out(response,object,api_version);
    }

    /**
     * 搜索商品列表
     */
    @RequestMapping("/getSearchGoodsCommonList")
    public void getSearchGoodsCommonList(HttpServletRequest request, HttpServletResponse response){
        String access_token = getParameter(request,"access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object ;
        String query = getParameter(request, "query");
        String price = getParameter(request, "price");
        int storeId = getParameter(request, "store_id", 0);
        int index = getParameter(request, "index", 0);
        int count = getParameter(request, "count", 10);
        if(access_token!=null) {
            object = WkUtil.checkToken(access_token);
            if (object.optInt("code") == 0) {
                object = goodsService.getSearchGoodsCommonList(storeId, query, price, index, count,false);
            }
        }else {
            object = goodsService.getSearchGoodsCommonList(storeId, query, price, index, count,true);
        }
        out(response,object,api_version);
    }
}
