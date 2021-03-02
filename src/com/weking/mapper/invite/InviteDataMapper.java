package com.weking.mapper.invite;

import com.weking.model.invite.InviteData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InviteDataMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(InviteData record);

    int insertSelective(InviteData record);

    String selectByDataValueListByDataKeyAndInviteId(@Param("inviteId")int inviteId,@Param("dataKey")String dataKey);

    int updateByPrimaryKeySelective(InviteData record);

    int updateDataValueByInviteId(InviteData record);

    int updateByPrimaryKey(InviteData record);
}