package com.weking.mapper.comm;

import com.weking.model.comm.CommArea;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommAreaMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CommArea record);

    int insertSelective(CommArea record);

    CommArea selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommArea record);

    int updateByPrimaryKey(CommArea record);

    List<CommArea> selectByParentId(@Param("parentId") int parentId, @Param("lang_code") String lang_code);

    CommArea selectNameById(@Param("id") int id, @Param("lang_code") String lang_code);
}