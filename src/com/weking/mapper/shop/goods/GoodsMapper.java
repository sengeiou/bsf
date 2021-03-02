package com.weking.mapper.shop.goods;

import com.weking.model.shop.goods.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GoodsMapper {

    int insert(Goods record);

    int insertCopy(@Param("storeId")int storeId,@Param("storeName")String storeName,@Param("goodsCommonId")int goodsCommonId,@Param("commonId")int commonId,@Param("goodsAddtime")long goodsAddtime);

    Goods findGoodsInfoByGoodsId(Integer goodsId);

    List<Goods> selectGoodsListByGoodsIds(@Param("map") Map goodsInfo);

    List<Goods> selectGoodsListByGoodsCommonIds(@Param("storeId") Integer storeId,@Param("list") List<Integer> goodsCommonIds);

    List<Goods> selectListByGoodsCommonId(Integer goodsCommonId);

    int updateGoodsNumBatch(@Param("map") Map<Integer,Integer> map);//批量减少商品库存

    int updateGoodsSaleNumBatch(@Param("map") Map<Integer,Integer> map); //更新商品销量

    int updateIncGoodsStorageBatch(@Param("map") Map<Integer,Integer> map); //批量增加商品库存

    List<Goods> selectParentGoodsList(@Param("list")List<Integer> goodsIds); //查询父商品列表

    int delGoodsByGoodsCommonId(int goodsCommonId);

}