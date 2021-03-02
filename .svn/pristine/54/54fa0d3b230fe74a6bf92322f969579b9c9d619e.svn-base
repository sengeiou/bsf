package com.weking.mapper.pocket;

import com.weking.model.pocket.UserGain;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserGainMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserGain record);

    int insertSelective(UserGain record);

    UserGain selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserGain record);

    int updateByPrimaryKey(UserGain record);

    List<UserGain> selectUserGainListByUserId(@Param("userId")int userId, @Param("index")int index, @Param("count")int count);

}