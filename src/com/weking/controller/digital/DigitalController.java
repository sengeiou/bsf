package com.weking.controller.digital;

import com.weking.controller.out.OutControllerBase;
import com.weking.controller.pay.PayController;
import com.weking.core.WkUtil;
import com.weking.core.digital.ReqInnerDeposit;
import com.weking.core.digital.RespObj;
import com.weking.service.digital.DigitalService;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

/**
 * 数字货币
 */

@Controller
@RequestMapping({"/digital"})
public class DigitalController extends OutControllerBase {

    private static Logger log = Logger.getLogger(PayController.class);
    @Resource
    private DigitalService digitalService;

    /**
     * 数字货币充值，由区块链服务器调用
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/wallet/charge", method = RequestMethod.POST)
    @ResponseBody
    public synchronized RespObj Charge(HttpServletRequest request, HttpServletResponse response) {
        String id = getParameter(request, "id");//区块链服务器中的id
        String currency = getParameter(request, "currency");//虚拟币类型
        String r_address = getParameter(request, "r_address");//充值地址
        String r_amount = getParameter(request, "r_amount", "0");//充值数量，已处理
        String r_create_time = getParameter(request, "r_create_time");//充值时间
        String r_txid = getParameter(request, "r_txid");//txid
        String r_confirmations = getParameter(request, "r_confirmations");//确认数
        String api_key = getParameter(request, "api_key");
        Long timestamp = getParameter(request, "timestamp", 0L);
        String sign = getParameter(request, "sign");
        String sign_type = getParameter(request, "sign_type");
        System.out.println("currency:" + currency);
        System.out.println("r_address:" + r_address);
        System.out.println("r_amount:" + r_amount);
        ReqInnerDeposit reqInnerDeposit = new ReqInnerDeposit();
        reqInnerDeposit.setId(id);
        reqInnerDeposit.setCurrency(currency);
        reqInnerDeposit.setR_address(r_address);
        reqInnerDeposit.setR_amount(new BigDecimal(r_amount));
        reqInnerDeposit.setR_create_time(r_create_time);
        reqInnerDeposit.setR_txid(r_txid);
        reqInnerDeposit.setR_confirmations(r_confirmations);
        reqInnerDeposit.setApi_key(api_key);
        reqInnerDeposit.setTimestamp(timestamp);
        reqInnerDeposit.setSign(sign);
        reqInnerDeposit.setSign_type(sign_type);
        RespObj result = digitalService.Charge(reqInnerDeposit);
        log.info(String.format("status:%s,msg:%s,data:%s", result.getStatus(), result.getMsg(),
                result.getData() == null ? "" : result.getData()));
        return result;

    }

    /**
     * 获取数字货币钱包信息
     *
     * @param request
     * @param response
     */
    @RequestMapping("/wallet/info")
    public void WallectInfo(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            long user_id = result.optLong("user_id");
            String currency = result.optString("currency");
            result = digitalService.WallectInfo(user_id, currency,api_version);
        }
        out(response, result,api_version);
    }

    //  货币交易记录  (不可提现 sca)
    @RequestMapping("/wallet/tokenInfo")
    public void TokenInfo(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        long wallet_id = getParameter(request, "wallet_id", 0L);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            long user_id = result.optLong("user_id");
            String currency = result.optString("currency");
            int index = getParameter(request, "index", 0);
            int count = LibSysUtils.toInt(getParameter(request, "count"), 0);
            int type = getParameter(request, "type", 0);

            result = digitalService.TokenInfo(user_id, wallet_id, currency, type, index, count,api_version);
        }
        out(response, result,1.2);
    }

    //  货币交易记录  (可提现 sca)
    @RequestMapping("/wallet/scaInfo")
    public void scaInfo(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        long wallet_id = getParameter(request, "wallet_id", 0L);
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            long user_id = result.optLong("user_id");
            String currency = result.optString("currency");
            int index = getParameter(request, "index", 0);
            int count = LibSysUtils.toInt(getParameter(request, "count"), 0);

            result = digitalService.scaInfo(user_id, wallet_id, currency, index, count,api_version);
        }
        out(response, result,1.2);
    }



    /**
     * 修改钱包的币别
     *
     * @param request
     * @param response
     */
    @RequestMapping("/wallet/changeCurrency")
    public void ChangeCurrency(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        String currency = getParameter(request, "currency", "USD");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            long user_id = result.optLong("user_id");
            result = digitalService.ChangeCurrency(user_id, currency);
        }
        out(response, result,api_version);
    }

    @RequestMapping("/wallet/currencyInfo")
    public void CurrencyInfo(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            long user_id = result.optLong("user_id");
            result = digitalService.CurrencyInfo();
        }
        out(response, result,api_version);
    }

    // 记录详细
    @RequestMapping("/wallet/logInfo")
    public void LogInfo(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject result = WkUtil.checkToken(access_token);
        if (result.optInt("code") == 0) {
            long user_id = result.optLong("user_id");
            long log_id = getParameter(request, "log_id", 0L);
            int type = getParameter(request, "type", 0);
            result = digitalService.LogInfo(user_id, log_id, type);
        }
        out(response, result,1.2);
    }


    /**
     * 数字货币提现申请
     */
    @RequestMapping("/applyWithdraw")
    public void ApplyWithdraw(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject result = WkUtil.checkToken(access_token);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        if (result.optInt("code") == 0) {
            int user_id = result.optInt("user_id");
            String lang_code = result.optString("lang_code");
            long wallet_id = getParameter(request, "wallet_id", 0L);
            double draw_num = getParameter(request, "draw_num", 0.0);
            int pay_type = getParameter(request, "pay_type", 0);
            String pay_account = getParameter(request, "pay_account", "");
            String pay_name = getParameter(request, "pay_name", "");
            String bank_account = getParameter(request, "bank_account", "");
            String bank_name = getParameter(request, "bank_name", "");
            String eth_address = getParameter(request, "eth_address", "");

            result = digitalService.ApplyWithdraw(user_id, lang_code, wallet_id, draw_num, pay_type, pay_account,
                    pay_name, bank_account, bank_name,eth_address,api_version);
        }
        out(response, result,api_version);

    }

    /**
     * 数字货币提现审核
     */
    @RequestMapping("/auditWithdraw")
    public void AuditWithdraw(HttpServletRequest request, HttpServletResponse response) {

    }

    /**
     * 数字货币获取提现列表
     */
    @RequestMapping("/withdrawList")
    public void WithdrawList(HttpServletRequest request, HttpServletResponse response) {

    }

    /**
     * 数字货币更新提现状态
     */
    @RequestMapping("/withdrawStatus")
    public void WithdrawStatus(HttpServletRequest request, HttpServletResponse response) {

    }

    /**
     * 用户SCA GOLD
     */
    @RequestMapping("/wallet/SCAGoldInfo")
    public void SCAGoldInfo(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WkUtil.checkToken(access_token);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        if (object.optInt("code") == 0) {
            long userId = object.optLong("user_id");
            String currency = object.optString("currency");
            String lang_code = object.optString("lang_code");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 0);
            object = digitalService.SCAGoldInfo(userId, currency, lang_code, index, count);
        }
        out(response, object,1.2);
    }


    /**
     * 兑换SCA GOLD (emo 不再能兑换sca gold)
     */
    @RequestMapping("/wallet/convScaGold")
    public void convScaGold(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            String currency = object.optString("currency");
            int item_id = getParameter(request, "item_id", 0);
            //增加sca 类型  来兑换sca gold  1是充值sca 2是分红sca 3 兑换sca
            int type = getParameter(request, "type", 0);

            object = digitalService.convScaGold(userId, item_id,type, currency, lang_code,api_version );
            //object = digitalService.convScaGold(userId, item_id, currency, lang_code );
        }
        out(response, object,1.2);
    }





    /**
     * 获取兑换SCA GOLD记录
     */
    @RequestMapping("/wallet/SCAGoldLog")
    public void getSCAGoldLog(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"));
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 10);
            object = digitalService.getSCAGoldLog(userId, lang_code, index, count);
        }
        out(response, object,api_version);
    }


}
