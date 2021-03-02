package com.weking.service.admin;

import com.weking.cache.WKCache;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.core.payNow.PayNowUtil;
import com.weking.mapper.log.ConsumeInfoMapper;
import com.weking.service.pay.PayService;
import com.weking.service.pay.PocketService;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * Created by zhb on 2017/8/24.
 * 后台管理
 */
@Service("adminService")
public class AdminService extends LibServiceBase {

    private static Logger logger = Logger.getLogger(AdminService.class);


    @Resource
    private PayService payService;
    @Resource
    private ConsumeInfoMapper consumeInfoMapper;

    public JSONObject updateRecPostList(int user_id, int post_id, int sorts) {
        System.out.println(String.format("----- user_id:%s,post_id:%s,sorts:%s", user_id, post_id, sorts));
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        if (post_id != 0 && user_id != 0) {
            WKCache.addRecommendPostList(post_id,sorts);
          /*  WKCache.addRecommendPostUserList(user_id, sorts);
            WKCache.addUserRecommendPost(user_id, post_id);*/
        }
        return result;
    }

    public JSONObject updateVipLevel(int user_id, int buy_emo) {
        System.out.println(String.format("----- user_id:%s,post_id:%s", user_id, buy_emo));
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        if (buy_emo != 0 && user_id != 0) {
            payService.addCache(buy_emo,user_id);
        }
        return result;
    }


    //payNow核销
    public void payNowCancel() {
        long nowTime = LibDateUtils.getLibDateTime();//当前时间
        List<Map<String, Object>> list = consumeInfoMapper.payNowCancel(nowTime);
        int size = list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                String post="F";
                Map<String, Object> map = list.get(i);
                String account = LibSysUtils.toString(map.get("account"));//用户账号
                int send_diamond = LibSysUtils.toInt(map.get("send_diamond"));//送出点数
                int send_id = LibSysUtils.toInt(map.get("send_id"));//送出人id
                String memOrderNo=LibSysUtils.toString(map.get("account"))+"_"+LibSysUtils.toString(map.get("id"));//id
                //先查paynow那边剩余点数
                int number = PayNowUtil.findSurplusNumber(account);
                if (number>0) {
                    if (number <= send_diamond ) {
                        //小于送出点数，就扣除paynow点数
                        post = PayNowUtil.deductNumber(account, number, memOrderNo);
                    } else if (number > send_diamond) {
                        post = PayNowUtil.deductNumber(account, send_diamond, memOrderNo);
                    }
                }
                //更新核销的数据
                if ("S".equalsIgnoreCase(post)) {
                    consumeInfoMapper.updateConsumeIsDeduct(send_id, nowTime);
                }

            }
        }
        logger.error("payNowCancel==核销完成");



    }



}
