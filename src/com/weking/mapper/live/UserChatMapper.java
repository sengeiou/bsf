package com.weking.mapper.live;

import com.weking.model.live.UserChat;

public interface UserChatMapper {

    int insert(UserChat record);

    int insertSelective(UserChat record);

    UserChat findUserChatInfo(Integer id);

    int updateByPrimaryKeySelective(UserChat record);

}