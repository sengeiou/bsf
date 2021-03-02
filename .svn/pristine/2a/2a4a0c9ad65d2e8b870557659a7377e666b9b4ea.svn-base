package com.weking.mapper.withdrawlog;

import com.weking.model.withdrawlog.WithDraw;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WithDrawMapper {

    int insert(WithDraw record);

    int findByUserId(Integer userId);

    List<WithDraw> selectListByUserId(@Param("userId") Integer userId,@Param("offset") int index,@Param("limit")int count);

    List<WithDraw> selectTodayListByUserId(@Param("userId")int user_id,@Param("today") long today);

    WithDraw selectByPaymentSn(@Param("paymentSn") String paymentSn);
}