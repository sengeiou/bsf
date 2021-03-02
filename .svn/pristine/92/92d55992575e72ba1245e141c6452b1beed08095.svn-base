package com.weking.mapper.pocket;

import com.weking.model.pocket.RechargeList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RechargeListMapper {

    List<RechargeList> selectByRechargeType(@Param("type") Integer type,@Param("project_name")String project_name,@Param("role")int role,@Param("version")double version);

    RechargeList findByRechargeId(Integer id);

    RechargeList findByThirdId(@Param("thirdId") String thirdId, @Param("rechargeType") int rechargeType);

    List<RechargeList> findByRechargeType(@Param("rechargeType") int rechargeType);

    RechargeList selectByTypeAndMoney(@Param("pay_money")Double pay_money, @Param("recharge_type")Integer recharge_type);

}