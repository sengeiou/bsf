package com.weking.mapper.shop.store;

import com.weking.model.shop.store.ShopStore;
import org.apache.ibatis.annotations.Param;

public interface ShopStoreMapper {

    int insert(ShopStore record);

    Integer findStoreIdByUserId(Integer userId);

    ShopStore findStoreInfoByUserId(Integer userId);

    Integer findOwnStoreId();  //查询平台自营店铺ID

    ShopStore findStoreInfoByStoreId(Integer storeId);

    int updateStoreSalesByStoreId(@Param("storeId")int storeId,@Param("storeSales")int storeSales);
}