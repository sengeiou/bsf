package com.weking.mapper.statistics;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StatisticsMapper {

    int insert(int userId);

    int increaseFansTotalByUserId(@Param("userId") int userId, @Param("fansTotal") int fansTotal);

    int reduceFansTotalByUserId(@Param("userId") int userId, @Param("fansTotal") int fansTotal);

    int batchIncreaseWinTotal(@Param("map") Map map);

    List<Map<String,Object>> getWinTotalOrder(@Param("offset") int offset, @Param("limit") int limit);

    List<Map<String,Object>> getFansTotalOrder(@Param("offset") int offset, @Param("limit") int limit);

    List<Map<String,Object>> selectFansTotalList(@Param("offset") int offset, @Param("limit") int limit);

    List<Map<String,Object>> selectWinTotalList(@Param("offset") int offset, @Param("limit") int limit);

    int batchUpdateWinTotal(@Param("map") Map map);

    int batchUpdateFansTotal(@Param("map") Map map);


}