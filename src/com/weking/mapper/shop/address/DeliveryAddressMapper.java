package com.weking.mapper.shop.address;

import com.weking.model.shop.address.DeliveryAddress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DeliveryAddressMapper {
    int deleteByPrimaryKey(@Param("id") Integer id,@Param("userId")int userId);

    int insert(DeliveryAddress record);

    List<DeliveryAddress> selectListByUserId(Integer userId);

    DeliveryAddress findDeliveryAddressByUserId(Integer userId);

    DeliveryAddress findDeliveryAddressByAddressId(@Param("userId")Integer userId,@Param("id")Integer addressId);

    int cancelDefaultAddressByUserId(int userId); //取消用户默认收货地址

    int setDefaultAddressById(@Param("id")int id,@Param("userId") int userId); //设置默认收货地址

    int updateAddressByIdAndUserId(DeliveryAddress record);//更新收货地址

}