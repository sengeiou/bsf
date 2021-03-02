package com.weking.mapper.chathistory;

import com.weking.model.chathistory.ChatHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatHistoryMapper {

    int insert(ChatHistory record);

    List<ChatHistory> selectOfficeChatList(int userId);

    int updateStatic(long chatId);

    ChatHistory findSystemMsg(int userId);

    int updateSystemState(@Param("recUserId")int recUserId,@Param("messageId")String messageId);
}