package com.weking.mapper.shop.goods;

import com.weking.model.shop.goods.GoodsCommon;
import com.weking.model.shop.goods.GoodsCommonExtend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsCommonMapper {

    int insert(GoodsCommon record);

    int insertCopy(GoodsCommon record);

    List<GoodsCommon> selectGoodsCommonByStoreId(@Param("storeId") Integer storeId,@Param("index")int index,@Param("count")int count);

    GoodsCommonExtend findGoodsInfoByGoodsCommonId(Integer goodsCommonId);

    List<GoodsCommon> selectGoodsCommendList(@Param("index")int index,@Param("count")int count);

    List<GoodsCommon> selectGoodsCommonListByGcId1(@Param("gcId") Integer gcId1,@Param("index")Integer index,@Param("count")Integer count); //查询一级分类商品列表

    int clearIsLiveGoodsCommon(Integer storeId); //清除直播商品

    int updateIsLiveByGoodsCommonIds(@Param("storeId")int storeId,@Param("array") String[] array); //设置商品直播

    List<GoodsCommon> selectRoomGoodsCommonList(Integer storeId);

    GoodsCommon findGoodsCommonById(Integer goodsCommonId);

    List<GoodsCommon> selectStoreGoodsCommendList(@Param("storeId")int storeId,@Param("index")int index,@Param("count")int count);

    List<GoodsCommon> selectSearchGoodsCommonList(@Param("storeId")int storeId, @Param("goodsName")String goodsName,@Param("price")String[] price,@Param("index")int index,@Param("count")int count);

    int selectStoreGoodsCommonNum(int storeId); //店铺商品数量

    List<Integer> selectGoodsParentIdList(int storeId); //店铺代销商品父ID列表

    Integer findIdByGoodsParentId(@Param("storeId")int storeId, @Param("goodsParentId") int goodsParentId);

    int delGoodsCommon(@Param("id")int goodsCommonId,@Param("storeId")int storeId);

    int selectRoomGoodsCommonCount(int storeId);

    List<Integer> selectRoomGoodsCommonIdList(int storeId);
}