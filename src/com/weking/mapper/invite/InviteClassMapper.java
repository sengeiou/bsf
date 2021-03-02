package com.weking.mapper.invite;

import com.weking.model.invite.InviteClass;

import java.util.List;

public interface InviteClassMapper {

    List<InviteClass> selectAllInviteClassList();

    List<InviteClass> selectValidInviteClassList();

    String findClassNameByClassId(Integer id);

}