package com.weking.mapper.invite;

import com.weking.model.invite.InviteLike;
import org.apache.ibatis.annotations.Param;

public interface InviteLikeMapper {

    int deleteByUserIdAndInviteId(@Param("userId") Integer id,@Param("inviteId")Integer inviteId);

    int insert(InviteLike record);

    Integer findByUserIdAndInviteId(@Param("userId") Integer id,@Param("inviteId")Integer inviteId);

}