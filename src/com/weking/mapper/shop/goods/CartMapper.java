package com.weking.mapper.shop.goods;

import com.weking.model.shop.goods.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {

    int insert(Cart record);

    int selectCountByGoodsIdAndUserId(@Param("goodsId")int goodsId, @Param("userId")int userId); //查询商品是否用户已经添加购物车

    int deleteByUserIdAndGoodsId(@Param("userId") int userId, @Param("array") String[] goodsIds); //移除购物车商品

    List<Cart> selectListByUserId(int userId);

    List<Cart> selectListByUserIdAndGoodsIds(@Param("userId") int userId, @Param("array") String[] goodsIds);

    int updateGoodsNumByGoodsId(@Param("goodsId")int goodsId, @Param("userId")int userId,@Param("goodsNum")int goodsNum);

}