package com.weking.mapper.invite;

import com.weking.model.invite.Invite;
import com.weking.model.invite.InviteAppoint;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InviteAppointMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(InviteAppoint record);

    int insertSelective(InviteAppoint record);

    InviteAppoint findInfoById(@Param("id")Integer id);

    InviteAppoint findInfoByInviteId(@Param("inviteId")Integer inviteId);

    InviteAppoint findByUserIdAndInviteId(@Param("userId") Integer userId,@Param("inviteId")Integer inviteId);

    List<InviteAppoint> selectAppointmentList(@Param("userId")Integer userId,@Param("state")Integer state,@Param("index")Integer index,@Param("count")Integer count);

    int updateByPrimaryKeySelective(InviteAppoint record);

    int updateStateById(@Param("id")Integer id,@Param("frozenId")Integer frozenId,@Param("state")Integer state);

    InviteAppoint findAppointmentInfo(Integer appointId);

    int deleteAppointInfo(@Param("id")Integer appointId,@Param("userId")Integer userId);

    Integer findNotConfirmIdByInviteId(Integer inviteId);

    /**
     * 查询所有的待确认约会
     */
    List<InviteAppoint> selectAllNotConfirmAppointment();

    Integer findByInviteIdAndOtherFrozenId(@Param("inviteId")int inviteId,@Param("otherFrozenId")int otherFrozenId);

    List<InviteAppoint> selectAppointmentUserList(@Param("inviteId")Integer inviteId,@Param("index")Integer index,@Param("count")Integer count);
}