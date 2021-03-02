package com.weking.mapper.account;


import com.weking.model.account.UserData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDataMapper {
    int deleteByPrimaryKey(@Param("userId") int userId, @Param("id") Integer id);

    int batchInsert(List<UserData> list);

    int insertSelective(UserData record);

    List<UserData> selectDataValueListByUserIdAndDataKey(@Param("userId") int userId, @Param("dataKey") String dataKey, @Param("count") int count);

    String findDataValueByUserIdAndDataKey(@Param("userId") int userId, @Param("dataKey") String dataKey);

    int updateByPrimaryKeySelective(UserData record);

    int updateDataValueByIdAndUserId(@Param("id")Integer id,@Param("userId")Integer userId,@Param("dataValue")String dataValue);

    int selectCountByUserIdAndDataKey(@Param("userId") int userId, @Param("dataKey") String dataKey);
}