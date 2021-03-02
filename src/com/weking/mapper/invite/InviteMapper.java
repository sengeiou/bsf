package com.weking.mapper.invite;

import com.weking.model.invite.Invite;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InviteMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Invite record);

    int insertSelective(Invite record);

    Invite findInviteInfoById(Integer id);

    int updateLikeNumByInviteId(@Param("inviteId")Integer inviteId,@Param("likeNum")Integer likeNum);

    int increaseAppointNumById(Integer inviteId);

    List<Invite> selectInviteListByClassId(@Param("classId")Integer classId,@Param("index")Integer index,@Param("count")Integer count);

    List<Invite> selectInviteListByInviteIds(@Param("list")List<String> list);

    List<Invite> selectInviteListByUserId(@Param("userId")int userId,@Param("index")int index,@Param("count")Integer count);

    List<Invite> selectNewInviteList(@Param("index")Integer index,@Param("count")Integer count);

    int deleteInviteById(@Param("id")Integer id,@Param("userId")Integer userId);

    int updateByPrimaryKeySelective(Invite record);
}