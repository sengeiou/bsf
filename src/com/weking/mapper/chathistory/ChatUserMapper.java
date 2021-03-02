package com.weking.mapper.chathistory;

import com.weking.model.chathistory.ChatUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatUserMapper {
    int deleteByUserId(@Param("userId") int userId, @Param("otherId") int otherId);

    int insert(ChatUser record);

    int update(ChatUser record);

    int insertUserMsg(ChatUser record);

    int insertOtherMsg(ChatUser record);

    int updateUserMsg(ChatUser record);

    int updateOtherMsg(ChatUser record);

    List<ChatUser> selectById(@Param("userId") int userId, @Param("otherId") int otherId);

    List<ChatUser> selectFollowListByUserId(@Param("userId") int userId);

    List<ChatUser> selectNotFollowListByUserId(@Param("userId") int userId);

    int updateMsgState(@Param("userId")int userId,@Param("otherId")int otherId);
}