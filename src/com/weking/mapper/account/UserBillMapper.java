package com.weking.mapper.account;

import com.weking.model.account.UserBill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserBillMapper {

    int insert(UserBill record);

    UserBill selectByPrimaryKey(Long id);

    int updateByPrimaryKey(UserBill record);

    UserBill selectBillByUserIdAndType(@Param("userId") int user_id,@Param("type") int type);

    List<UserBill> selectListByUserIdAndType(@Param("userId") int user_id,@Param("type") int type);

    UserBill selectBillByUserIdAndTypeToday(@Param("userId") int user_id,@Param("type") int type,@Param("today") long today);
}