package com.weking.mapper.vip;

import com.weking.model.vip.VipPrivilege;

import java.util.List;

public interface VipPrivilegeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(VipPrivilege record);

    int insertSelective(VipPrivilege record);

    VipPrivilege selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(VipPrivilege record);

    int updateByPrimaryKey(VipPrivilege record);

    List<VipPrivilege> selectAllVipPrivilege();
}