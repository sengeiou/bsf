package com.weking.mapper.log;

import com.weking.model.log.SmsLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmsLogMapper {

    int insertSelective(SmsLog record);
    
    SmsLog findByParam(SmsLog record); //验证发送账号记录

    int selectCountByTime(@Param("sendAccount")String sendAccount,@Param("sendTime")long sendTime); //查询今天发送数量

    List<SmsLog> selectByToDay(@Param("sendAccount")String sendAccount, @Param("sendTime")long sendTime); //查询今天发送列表
}