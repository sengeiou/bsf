package com.weking.service.digital;

import com.weking.cache.GameCache;
import com.weking.cache.WKCache;
import com.weking.core.C;
import com.weking.core.DateUtils;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.core.digital.HelpUtils;
import com.weking.core.digital.ReqInnerDeposit;
import com.weking.core.digital.Resp;
import com.weking.core.digital.RespObj;
import com.weking.core.enums.CoinTypeEnum;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.commission.CommissionMapper;
import com.weking.mapper.digital.*;
import com.weking.mapper.game.GameFireMapper;
import com.weking.mapper.log.MiningLogMapper;
import com.weking.mapper.log.ScaGoldLogMapper;
import com.weking.mapper.pocket.ExchangeItemMapper;
import com.weking.mapper.pocket.PlatformIncomeMapper;
import com.weking.mapper.pocket.PocketInfoMapper;
import com.weking.mapper.withdrawlog.WithDrawMapper;
import com.weking.model.account.AccountInfo;
import com.weking.model.commission.Commission;
import com.weking.model.digital.*;
import com.weking.model.game.GameFire;
import com.weking.model.log.MiningLog;
import com.weking.model.log.ScaGoldLog;
import com.weking.model.pocket.ExchangeItem;
import com.weking.model.pocket.PocketInfo;
import com.weking.model.withdrawlog.WithDraw;
import com.weking.service.game.GameService;
import com.weking.service.system.MsgService;
import com.weking.service.user.UserService;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/5/17.
 */

@Service("digitalService")
@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class DigitalService extends LibServiceBase {
    static Logger logger = Logger.getLogger(DigitalService.class);
    @Resource
    private UserService userService;
    @Resource
    DigitalWalletLogMapper digitalWalletLogMapper;
    @Resource
    DigitalWalletMapper digitalWalletMapper;
    @Resource
    DigitalWalletAddressMapper digitalWalletAddressMapper;
    @Resource
    DigitalCurrencyMapper digitalCurrencyMapper;
    @Resource
    DigitalTokenMapper digitalTokenMapper;
    @Resource
    private CommissionMapper commissionMapper;
    @Resource
    private MsgService msgService;
    @Resource
    private GameService gameService;
    @Resource
    private AccountInfoMapper accountMapper;
    @Resource
    private WithDrawMapper withDrawMapper;
    @Resource
    private PocketInfoMapper pocketInfoMapper;
    @Resource
    private ExchangeItemMapper exchangeItemMapper;
    @Resource
    private ScaGoldLogMapper scaGoldLogMapper;
    @Resource
    private PlatformIncomeMapper platformIncomeMapper;
    @Resource
    private MiningLogMapper miningLogMapper;
    @Resource
    private GameFireMapper gameFireMapper;
    @Resource
    private SCAWalletLogMapper scaWalletLogMapper;
    @Resource
    private AccountInfoMapper accountInfoMapper;

    /*
    充值
     */
    @Transactional
    public RespObj Charge(ReqInnerDeposit reqInnerDeposit) {
        logger.info(reqInnerDeposit.toString());
        if (null == reqInnerDeposit.getCurrency()) {
            return new RespObj(Resp.FAIL, Resp.ILLEGAL_CURRENCY, null);
        }
        if (null == reqInnerDeposit.getR_amount()
                || reqInnerDeposit.getR_amount().compareTo(new BigDecimal(0)) < 0) {
            return new RespObj(Resp.FAIL, Resp.ILLEGAL_DEPOSIT_AMOUNT, null);
        }

        // 签名认证
        String validateRet = HelpUtils.preValidateBaseSecret(reqInnerDeposit);
        if (!"".equals(validateRet)) {
            return new RespObj(Resp.FAIL, validateRet, null);
        }
        String apisecret = WKCache.get_system_cache("digital.wallet.apisecret");
        validateRet = HelpUtils.validateBaseSecret(HelpUtils.objToMap(reqInnerDeposit), apisecret);
        if (!"".equals(validateRet)) {
            return new RespObj(Resp.FAIL, validateRet, null);
        }

        DigitalWallet digitalWallet = digitalWalletMapper.selectByAddress(reqInnerDeposit.getR_address(), reqInnerDeposit.getCurrency());
        if (digitalWallet == null) {
            return new RespObj(Resp.FAIL, Resp.ACCOUNT_NOT_EXISTS, null);
        }
        if (!reqInnerDeposit.getCurrency().equalsIgnoreCase("SCA")) {
            DigitalWalletLog digitalWalletLog = new DigitalWalletLog();
            digitalWalletLog.setUserId(digitalWallet.getUserId());
            digitalWalletLog.setSymbol(reqInnerDeposit.getCurrency());
            digitalWalletLog.setInAddress(reqInnerDeposit.getR_address());
            digitalWalletLog.setAmount(reqInnerDeposit.getR_amount());
            digitalWalletLog.setCreateTime(LibDateUtils.getLibDateTime());
            digitalWalletLog.setTxid(reqInnerDeposit.getR_txid());
            digitalWalletLog.setConfirmations(LibSysUtils.toInt(reqInnerDeposit.getR_confirmations()));
            digitalWalletLog.setOriginalId(LibSysUtils.toLong(reqInnerDeposit.getId()));
            digitalWalletLog.setTimestamp(reqInnerDeposit.getTimestamp());
            digitalWalletLogMapper.insertSelective(digitalWalletLog);
        } else {
            SCAWalletLog scaWalletLog = new SCAWalletLog();
            scaWalletLog.setUserId(digitalWallet.getUserId());
            scaWalletLog.setSymbol(reqInnerDeposit.getCurrency());
            scaWalletLog.setInAddress(reqInnerDeposit.getR_address());
            scaWalletLog.setAmount(reqInnerDeposit.getR_amount());
            scaWalletLog.setCreateTime(LibDateUtils.getLibDateTime());
            scaWalletLog.setTxid(reqInnerDeposit.getR_txid());
            scaWalletLog.setConfirmations(LibSysUtils.toInt(reqInnerDeposit.getR_confirmations()));
            scaWalletLog.setOriginalId(LibSysUtils.toLong(reqInnerDeposit.getId()));
            scaWalletLog.setTimestamp(reqInnerDeposit.getTimestamp());
            scaWalletLogMapper.insertSelective(scaWalletLog);
        }
        digitalWallet.setInAmount(digitalWallet.getInAmount().add(reqInnerDeposit.getR_amount()));
        digitalWallet.setCurrAmount(digitalWallet.getCurrAmount().add(reqInnerDeposit.getR_amount()));
        digitalWalletMapper.updateByPrimaryKeySelective(digitalWallet);
        //return new RespObj(Resp.FAIL, Resp.STOP_EX, null);
        return new RespObj(Resp.SUCCESS, Resp.SUCCESS_MSG, reqInnerDeposit.getId()); // 原样返回
    }

    /**
     * 增加或减少钱包数据，并增加日志
     */
    public JSONObject OptWallect(int user_id, String lang_code, int originnal_id, String symbol, BigDecimal amount, short opt, String txid, String remark, String sendMsg, double api_version) {
        DigitalWallet digitalWallet = digitalWalletMapper.selectByUserIdSymbol(user_id, symbol);
        short status = 0;
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        String logid = "";
        if (digitalWallet != null) {
            //0充值，1提现，2转账,3消费,4挖矿奖励  6 充值sca兑换SCA GOLD,7填邀请码奖励,
            // 8分红奖励 ,29 分红sca兑换sca gold ,30 sca gold  兑换sca  40:购买商品
            switch (opt) {
                case 0:
                    break;
                case 1:
                    if (api_version < 3.7) {
                        if (digitalWallet.getWithdrawAmount().compareTo(amount) < 0) {
                            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
                        }
                        digitalWallet.setWithdrawAmount(digitalWallet.getWithdrawAmount().subtract(amount));
                        digitalWallet.setOutAmount(digitalWallet.getOutAmount().add(amount));
                        amount = amount.negate();
                        status = 2;
                        break;
                    } else {
                        if (digitalWallet.getWithdrawAmount().add(digitalWallet.getCurrAmount()).compareTo(amount) < 0) {
                            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
                        }
                        if (digitalWallet.getWithdrawAmount().compareTo(amount) < 0) {
                            BigDecimal residueAmount = amount.subtract(digitalWallet.getWithdrawAmount());
                            digitalWallet.setWithdrawAmount(new BigDecimal(0));
                            digitalWallet.setCurrAmount(digitalWallet.getCurrAmount().subtract(residueAmount));
                        } else {
                            digitalWallet.setWithdrawAmount(digitalWallet.getWithdrawAmount().subtract(amount));
                        }
                        digitalWallet.setOutAmount(digitalWallet.getOutAmount().add(amount));
                        amount = amount.negate();
                        status = 2;
                        break;
                    }
                case 2:
                    break;
                case 3:
                    if (digitalWallet.getCurrAmount().compareTo(amount) < 0) {
                        return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
                    }
                    digitalWallet.setCurrAmount(digitalWallet.getCurrAmount().subtract(amount));
                    digitalWallet.setOutAmount(digitalWallet.getOutAmount().add(amount));
                    break;
                case 4:
                    digitalWallet.setCurrAmount(digitalWallet.getCurrAmount().add(amount));
                    digitalWallet.setInAmount(digitalWallet.getInAmount().add(amount));
                    break;
                case 6:
                    //充值sca 兑换sca gold

                    if (api_version < 3.7) {
                        if (digitalWallet.getCurrAmount().compareTo(amount) < 0) {
                            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
                        } else {
                            digitalWallet.setCurrAmount(digitalWallet.getCurrAmount().subtract(amount));
                        }
                        break;
                    } else {
                        if (digitalWallet.getWithdrawAmount().add(digitalWallet.getCurrAmount()).compareTo(amount) < 0) {
                            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
                        }

                        if (digitalWallet.getWithdrawAmount().compareTo(amount) < 0) {
                            BigDecimal residueAmount = amount.subtract(digitalWallet.getWithdrawAmount());
                            digitalWallet.setWithdrawAmount(new BigDecimal(0));
                            digitalWallet.setCurrAmount(digitalWallet.getCurrAmount().subtract(residueAmount));
                        } else {
                            digitalWallet.setWithdrawAmount(digitalWallet.getWithdrawAmount().subtract(amount));
                        }
                        break;
                    }
                case 7:
                    //分红奖励
                    if (api_version < 3.7) {
                        digitalWallet.setCurrAmount(digitalWallet.getCurrAmount().add(amount));
                        digitalWallet.setInAmount(digitalWallet.getInAmount().add(amount));
                    } else {
                        digitalWallet.setWithdrawAmount(digitalWallet.getWithdrawAmount().add(amount));
                        digitalWallet.setAllWithdrawAmount(digitalWallet.getAllWithdrawAmount().add(amount));
                    }
                    break;
                case 8:
                    // 分红sca兑换sca gold
                    digitalWallet.setWithdrawAmount(digitalWallet.getWithdrawAmount().add(amount));
                    digitalWallet.setAllWithdrawAmount(digitalWallet.getAllWithdrawAmount().add(amount));
                    break;
                case 29:

                    if (api_version < 3.7) {
                        if (digitalWallet.getWithdrawAmount().compareTo(amount) < 0) {
                            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
                        }
                        digitalWallet.setWithdrawAmount(digitalWallet.getWithdrawAmount().subtract(amount));
                        break;
                    } else {
                        if (digitalWallet.getWithdrawAmount().add(digitalWallet.getCurrAmount()).compareTo(amount) < 0) {
                            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
                        }

                        if (digitalWallet.getWithdrawAmount().compareTo(amount) < 0) {
                            BigDecimal residueAmount = amount.subtract(digitalWallet.getWithdrawAmount());
                            digitalWallet.setWithdrawAmount(new BigDecimal(0));
                            digitalWallet.setCurrAmount(digitalWallet.getCurrAmount().subtract(residueAmount));
                        } else {
                            digitalWallet.setWithdrawAmount(digitalWallet.getWithdrawAmount().subtract(amount));
                        }
                        break;
                    }
                    // sca gold  兑换 充值sca
                case 30:  //下注
                    if (api_version < 3.7) {
                        digitalWallet.setCurrAmount(digitalWallet.getCurrAmount().add(amount));
                        digitalWallet.setInAmount(digitalWallet.getInAmount().add(amount));
                        break;
                    } else {
                        digitalWallet.setWithdrawAmount(digitalWallet.getWithdrawAmount().add(amount));
                        digitalWallet.setAllWithdrawAmount(digitalWallet.getAllWithdrawAmount().add(amount));
                        break;
                    }
                case 31:
                    break;
                //击中 抓住  赢得
                case 32:
                    break;
                //主播分红
                case 33:
                    break;
                case 40:
                    if (digitalWallet.getWithdrawAmount().compareTo(amount) < 0) {
                        logger.error("虚拟币余额不足，扣除失败");
                        return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
                    } else {
                        digitalWallet.setWithdrawAmount(digitalWallet.getWithdrawAmount().subtract(amount));
                    }
                    break;
                default:
                    digitalWallet.setCurrAmount(digitalWallet.getCurrAmount().add(amount));
                    digitalWallet.setInAmount(digitalWallet.getInAmount().add(amount));
                    break;
            }
            digitalWalletMapper.updateByPrimaryKeySelective(digitalWallet);
            //SCA记录  存入新的记录表里面

            if (!symbol.equalsIgnoreCase("SCA")) {
                DigitalWalletLog digitalWalletLog = new DigitalWalletLog();
                digitalWalletLog.setUserId(LibSysUtils.toLong(user_id));
                digitalWalletLog.setSymbol(symbol);
                digitalWalletLog.setOptType(opt);
                digitalWalletLog.setAmount(amount);
                digitalWalletLog.setCreateTime(LibDateUtils.getLibDateTime());
                digitalWalletLog.setTxid(txid);
                digitalWalletLog.setStatus(status);
                digitalWalletLog.setRemark(remark);
                digitalWalletLog.setOriginalId((long) originnal_id);
                digitalWalletLogMapper.insertSelective(digitalWalletLog);//数字货币日志
                logid = digitalWalletLog.getId().toString();
            } else {
                SCAWalletLog scaWalletLog = new SCAWalletLog();
                scaWalletLog.setUserId(LibSysUtils.toLong(user_id));
                scaWalletLog.setSymbol(symbol);
                scaWalletLog.setOptType(opt);
                scaWalletLog.setAmount(amount);
                scaWalletLog.setCreateTime(LibDateUtils.getLibDateTime());
                scaWalletLog.setTxid(txid);
                scaWalletLog.setStatus(status);
                scaWalletLog.setRemark(remark);
                scaWalletLog.setOriginalId((long) originnal_id);
                scaWalletLogMapper.insertSelective(scaWalletLog);
                logid = scaWalletLog.getId().toString();

            }
            if (!LibSysUtils.isNullOrEmpty(sendMsg)) { //发送系统推送
                AccountInfo accountInfo = accountMapper.selectByPrimaryKey(user_id);
                msgService.sendSysMsg(accountInfo.getAccount(), sendMsg, accountInfo.getLangCode());
            }

        } else {
            result.put(ResultCode.data_error, LibProperties.getLanguage(lang_code, "weking.lang.app.data.error"));
        }
        result.put("logid", logid);
        result.put("txid", txid);
        System.out.println(result);
        return result;
    }

    public boolean RegisterWallect(long user_id) {
        List<DigitalToken> digitalTokenList = digitalTokenMapper.selectTokens();
        if (digitalTokenList.size() > 0) {
            String previous_group = "";
            String address = "";
            for (DigitalToken digitalToken : digitalTokenList) {
                if (digitalToken != null) {
                    if (!previous_group.equals(digitalToken.getGroups())) {
                        previous_group = "";
                        DigitalWalletAddress digitalWalletAddress = digitalWalletAddressMapper.selectUnuseAddress(digitalToken.getGroups());
                        if (digitalWalletAddress != null) {
                            digitalWalletAddress.setUserId(user_id);
                            //更新地址表中的user_id，代币地址已经被用，因为是先查询后使用，可能会存在多人同时注册时候并发问题
                            digitalWalletAddressMapper.updateByPrimaryKeySelective(digitalWalletAddress);
                            address = digitalWalletAddress.getAddress();
                            previous_group = digitalToken.getGroups();

                        }
                    }
                    if (!"".equals(previous_group)) {
                        DigitalWallet digitalWallet = new DigitalWallet();
                        digitalWallet.setUserId(user_id);
                        digitalWallet.setAddress(address);
                        digitalWallet.setSymbol(digitalToken.getSymbol());
                        digitalWalletMapper.insertSelective(digitalWallet);//注册钱包
                    }
                }
            }
        }
        return true;
    }

    /**
     * 获取数字货币钱包信息
     */
    public JSONObject WallectInfo(long user_id, String currency, double api_version) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        currency = "".equals(currency) ? "USD" : currency;
        double d_rate = digitalCurrencyMapper.selectByCurrency(currency);
        BigDecimal rate = new BigDecimal(d_rate);
        BigDecimal price;
        BigDecimal total_amount = new BigDecimal(0);
        BigDecimal amount;
        BigDecimal qry;
        BigDecimal withdrawQry;
        List<DigitalWallet> wallets = digitalWalletMapper.selectByUserId(user_id);
        JSONArray array = new JSONArray();
        JSONObject object;
        for (DigitalWallet wallet : wallets) {
            object = new JSONObject();
            object.put("wallet_id", wallet.getId());
            object.put("token_symbol", wallet.getSymbol());
            object.put("logo", WkUtil.combineUrl(wallet.getToken_logo(), UploadTypeEnum.OTHER, false));
            qry = wallet.getCurrAmount().setScale(5, BigDecimal.ROUND_HALF_UP);//不可提现数量
            withdrawQry = wallet.getWithdrawAmount().setScale(5, BigDecimal.ROUND_HALF_UP);//剩余可提现数量
            price = rate.multiply(new BigDecimal(wallet.getPrice())).setScale(2, BigDecimal.ROUND_HALF_UP);
            amount = qry.multiply(price).setScale(2, BigDecimal.ROUND_HALF_UP);
            //总sca
            //BigDecimal qrySum=wallet.getCurrAmount().add(wallet.getNotWithdrawAmount()).setScale(5, BigDecimal.ROUND_HALF_UP);

            object.put("qty", qry);
            if (api_version < 3.7) {
                object.put("withdrawQry", withdrawQry);
            } else {
                //新版本 把两个SCA和传递过去
                object.put("withdrawQry", withdrawQry.add(qry));
            }
            object.put("price", price);
            object.put("amount", amount);
            total_amount = total_amount.add(amount);
            array.add(object);
        }
        result.put("currency", currency);
        result.put("total_amount", total_amount);

        result.put("list", array);
//        System.out.println(result.toString());
        return result;
    }

    /**
     * 修改钱包的货币
     *
     * @param user_id  用户id
     * @param currency 币别
     * @return
     */
    public JSONObject ChangeCurrency(long user_id, String currency) {
        JSONObject jsonObject = LibSysUtils.getResultJSON(ResultCode.success);
        jsonObject.put("wallet_currency", currency);
        JSONObject result = userService.modify(LibSysUtils.toInt(user_id), jsonObject);
        return result;
    }

    /**
     * 获取币别列表
     *
     * @return
     */
    public JSONObject CurrencyInfo() {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        List<DigitalCurrency> currencyList = digitalCurrencyMapper.selectCurrency();
        JSONObject object;
        JSONArray array = new JSONArray();
        for (DigitalCurrency currency : currencyList) {
            object = new JSONObject();
            object.put("key", currency.getCurrency());
            array.add(currency.getCurrency());
        }
        result.put("list", array);
        return result;
    }

    /**
     * 获取日志详情
     *
     * @param userId
     * @param log_id
     * @param type   0sca  1sca
     * @return
     */
    public JSONObject LogInfo(long userId, long log_id, int type) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        if (type == 0) {
            //DigitalWalletLog digitalWalletLog = digitalWalletLogMapper.selectByPrimaryKey(log_id);
            SCAWalletLog scaWalletLog = scaWalletLogMapper.selectByPrimaryKey(log_id);
            if (scaWalletLog != null) {
                //result.put("token_symbol", scaWalletLog.getSymbol());
                result.put("token_symbol", "搜秀链");
                result.put("opt_type", scaWalletLog.getOptType());
                BigDecimal qty = scaWalletLog.getAmount().setScale(5, BigDecimal.ROUND_HALF_UP);
                result.put("qty", qty.compareTo(new BigDecimal("0")) < 0 ? qty.negate() : qty);
                result.put("in_address", scaWalletLog.getInAddress());
                result.put("out_address", scaWalletLog.getOutAddress());
                result.put("tx_id", scaWalletLog.getTxid());
                result.put("create_time", scaWalletLog.getCreateTime());
                result.put("status", scaWalletLog.getStatus());
                result.put("fee", scaWalletLog.getFee().setScale(3, BigDecimal.ROUND_HALF_UP)); //手续费
                result.put("remark", scaWalletLog.getRemark());//备注
                WithDraw withDraw = withDrawMapper.selectByPaymentSn(scaWalletLog.getTxid());
                if (withDraw != null) {
                    result.put("fee", withDraw.getFee() + withDraw.getCurrency());//手续费
                    if (withDraw.getPay_type() == 3) { //银行卡
                        result.put("in_address", withDraw.getBank_account());
                    } else {
                        result.put("in_address", withDraw.getPay_account());
                    }
//                result.put("pay_account",withDraw.getPay_account());
//                result.put("pay_type",withDraw.getPay_type());
//                result.put("pay_name",withDraw.getPay_name());
//                result.put("bank_account",withDraw.getBank_account());
//                result.put("bank_name",withDraw.getBank_name());
                }

            }
        }
        if (type == 1) {
            ScaGoldLog scaGoldLog = scaGoldLogMapper.selectByPrimaryKey(log_id);
            if (scaGoldLog != null) {
                result.put("token_symbol", "搜秀金");
                result.put("opt_type", exchangeNewType(scaGoldLog.getType()));
                BigDecimal qty = scaGoldLog.getConvNum().setScale(5, BigDecimal.ROUND_HALF_UP);
                result.put("qty", qty.compareTo(new BigDecimal("0")) < 0 ? qty.negate() : qty);
                result.put("create_time", scaGoldLog.getAddTime());
                result.put("tx_id", String.format("SCAG%016d", scaGoldLog.getId()));
                String remark = "";
                switch (scaGoldLog.getType()) {
                    case 0:
                        remark = "打赏奖励";
                        break;
                    case 1:
                        remark = "亿魔兑换";
                        break;
                    case 2:
                        remark = "搜秀链兑换";
                        break;
                    case 3:
                        remark = "兑换搜秀链";
                        break;
                    default:
                        break;
                }
                result.put("remark", remark);  //备注
            }
        }
//        System.out.println(result.toString());
        return result;
    }

    /**
     * 获取Token明细
     *
     * @param user_id   用户id
     * @param wallet_id Token 钱包id
     * @param currency  币别
     * @param type      0sca 1sca gold  不可提现sca
     * @return
     */
    public JSONObject TokenInfo(long user_id, long wallet_id, String currency, int type, int index, int count, double api_version) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        if (type == 0) {
            double d_rate = digitalCurrencyMapper.selectByCurrency(currency);
            BigDecimal rate = new BigDecimal(d_rate);
            DigitalWallet digitalWallet;
            if (wallet_id != 0) {
                digitalWallet = digitalWalletMapper.selectByKey(wallet_id);
            } else {
                digitalWallet = digitalWalletMapper.selectByUserIdSymbol((int) user_id, "SCA");
            }
            if (digitalWallet != null) {
                String token_symbol = digitalWallet.getSymbol();
                if (index == 0) {
                    result.put("wallet_id", digitalWallet.getId());
                    result.put("token_symbol", token_symbol);
                    result.put("address", digitalWallet.getAddress());
                    result.put("currency", currency);
//            result.put("rate", d_rate);
                    result.put("price", digitalWallet.getPrice() * d_rate);
                    result.put("logo", WkUtil.combineUrl(digitalWallet.getToken_logo(), UploadTypeEnum.OTHER, false));
                    if (api_version < 3.7) {
                        BigDecimal qty = digitalWallet.getCurrAmount().setScale(5, BigDecimal.ROUND_HALF_UP);
                        result.put("qty", qty);
                        BigDecimal price = rate.multiply(new BigDecimal(digitalWallet.getPrice())).setScale(2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal amount = qty.multiply(price).setScale(2, BigDecimal.ROUND_HALF_UP);
                        result.put("amount", amount);
                    } else {
                        //新版本  把两个sca和  传过去
                        BigDecimal qty = digitalWallet.getCurrAmount().add(digitalWallet.getWithdrawAmount()).setScale(5, BigDecimal.ROUND_HALF_UP);
                        result.put("qty", qty);
                        BigDecimal price = rate.multiply(new BigDecimal(digitalWallet.getPrice())).setScale(2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal amount = qty.multiply(price).setScale(2, BigDecimal.ROUND_HALF_UP);
                        result.put("amount", amount);
                    }

                    long sca_gold_pool = WKCache.getSCAGoldPoolValue();
                    result.put("sca_gold_pool", sca_gold_pool);
                }
                JSONArray array = new JSONArray();
                JSONObject object;
                if (count == 0) {
                    count = 100;
                }
                //老版本
                if (api_version < 3.7) {
                    String[] optType = {"0", "3", "5", "6", "7", "30", "31", "32", "33"};
                    //List<DigitalWalletLog> list = digitalWalletLogMapper.selectListByUserIdSymbol(LibSysUtils.toInt(user_id), token_symbol, index, count);
                    //List<DigitalWalletLog> list = digitalWalletLogMapper.selectListByUserIdSymbolAndType(LibSysUtils.toInt(user_id), optType, token_symbol, index, count);
                    List<SCAWalletLog> list = scaWalletLogMapper.selectListByUserIdSymbolAndType(LibSysUtils.toInt(user_id), optType, token_symbol, index, count);
                    if (list.size() > 0) {
                        for (SCAWalletLog walletLog : list) {
                            // if(walletLog.getOptType()==(short)5||walletLog.getOptType()==(short)6||walletLog.getOptType()==(short)30) {
                            object = new JSONObject();
                            object.put("log_id", walletLog.getId());
                            object.put("create_time", walletLog.getCreateTime());
                            BigDecimal value = walletLog.getAmount().setScale(5, BigDecimal.ROUND_HALF_UP);
                            value = value.compareTo(new BigDecimal("0")) < 0 ? value.negate() : value;
                            object.put("qty", value);
                            object.put("opt_type", walletLog.getOptType());
                            object.put("coin", "搜秀链");
                            array.add(object);
                            //  }
                        }
                    }

                } else {
                    List<SCAWalletLog> list = scaWalletLogMapper.selectListByUserIdSymbol(LibSysUtils.toInt(user_id), token_symbol, index, count);
                    if (list.size() > 0) {
                        for (SCAWalletLog walletLog : list) {
                            object = new JSONObject();
                            object.put("log_id", walletLog.getId());
                            object.put("create_time", walletLog.getCreateTime());
                            BigDecimal value = walletLog.getAmount().setScale(5, BigDecimal.ROUND_HALF_UP);
                            value = value.compareTo(new BigDecimal("0")) < 0 ? value.negate() : value;
                            object.put("qty", value);
                            object.put("opt_type", walletLog.getOptType());
                            object.put("coin", "搜秀链");
                            array.add(object);
                        }
                    }
                }

                JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));
                double emo2scagold = coin_proportion.optDouble("emo2scagold");
                double emo2rmb = coin_proportion.optDouble("emo2rmb");
                List<ExchangeItem> exchangeItems = exchangeItemMapper.selectAllExchangeItems();
                JSONArray array2 = new JSONArray();
                for (ExchangeItem item : exchangeItems) {
                    JSONObject obj = new JSONObject();
                    obj.put("item_id", item.getId());
                    obj.put("exc_num", item.getExcNum());

                    if (item.getType() == 2) {
                        obj.put("get_num", item.getExcNum() * coin_proportion.optDouble("sca2scagold"));
                        array2.add(obj);
                    }

                }
                result.put("sca_exchange_list", array2);

                result.put("list", array);
            }
        }
        if (type == 1) {
            PocketInfo pocketInfo = pocketInfoMapper.selectByUserid((int) user_id);
            BigDecimal my_sca_gold = pocketInfo.getSca_gold().setScale(2, BigDecimal.ROUND_HALF_UP);
            result.put("my_sca_gold", my_sca_gold);
            long sca_gold_pool = WKCache.getSCAGoldPoolValue();
            result.put("sca_gold_pool", sca_gold_pool);
            List<ScaGoldLog> list = scaGoldLogMapper.selectScaGoldLogsByUserId((int) user_id, index, count);
            JSONArray array = new JSONArray();
            if (list.size() > 0) {
                for (ScaGoldLog info : list) {
                    JSONObject object = new JSONObject();
                    object.put("log_id", info.getId());
                    object.put("create_time", info.getAddTime());
                    BigDecimal value = info.getConvNum().setScale(5, BigDecimal.ROUND_HALF_UP);
                    value = value.compareTo(new BigDecimal("0")) < 0 ? value.negate() : value;
                    object.put("qty", value);
                    object.put("opt_type", exchangeNewType(info.getType()));
                    object.put("coin", "搜秀金");
                    array.add(object);
                }
            }
            result.put("list", array);
        }
        System.out.println(result.toString());
        return result;
    }


    /**
     * 获取Token明细
     *
     * @param user_id   用户id  sca
     * @param wallet_id Token 钱包id
     * @param currency  币别
     * @return
     */
    public JSONObject scaInfo(long user_id, long wallet_id, String currency, int index, int count, double api_version) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);

        double d_rate = digitalCurrencyMapper.selectByCurrency(currency);
        BigDecimal rate = new BigDecimal(d_rate);
        DigitalWallet digitalWallet;
        if (wallet_id != 0) {
            digitalWallet = digitalWalletMapper.selectByKey(wallet_id);
        } else {
            digitalWallet = digitalWalletMapper.selectByUserIdSymbol((int) user_id, "SCA");
        }
        if (digitalWallet != null) {
            String token_symbol = digitalWallet.getSymbol();
            if (index == 0) {
                result.put("wallet_id", digitalWallet.getId());
                result.put("token_symbol", token_symbol);
                result.put("address", digitalWallet.getAddress());
                result.put("currency", currency);
//            result.put("rate", d_rate);
                result.put("price", digitalWallet.getPrice() * d_rate);
                result.put("logo", WkUtil.combineUrl(digitalWallet.getToken_logo(), UploadTypeEnum.OTHER, false));
                BigDecimal qty = digitalWallet.getWithdrawAmount().setScale(5, BigDecimal.ROUND_HALF_UP);//可提现
                if (api_version < 3.7) {
                    result.put("reQty", qty);
                } else {
                    //新版把两个和传递过去
                    BigDecimal scale = digitalWallet.getCurrAmount().setScale(5, BigDecimal.ROUND_HALF_UP);
                    result.put("reQty", qty.add(scale));
                }
                BigDecimal price = rate.multiply(new BigDecimal(digitalWallet.getPrice())).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal amount = qty.multiply(price).setScale(2, BigDecimal.ROUND_HALF_UP);
                result.put("amount", amount);
                long sca_gold_pool = WKCache.getSCAGoldPoolValue();
                result.put("sca_gold_pool", sca_gold_pool);
            }
            JSONArray array = new JSONArray();
            JSONObject object;
            if (count == 0) {
                count = 100;
            }
            if (api_version < 3.7) {
                String[] optType = {"1", "8", "29"};
                //List<DigitalWalletLog> list = digitalWalletLogMapper.selectListByUserIdSymbolAndType(LibSysUtils.toInt(user_id),optType, token_symbol, index, count);
                List<SCAWalletLog> list = scaWalletLogMapper.selectListByUserIdSymbolAndType(LibSysUtils.toInt(user_id), optType, token_symbol, index, count);
                if (list.size() > 0) {
                    for (SCAWalletLog walletLog : list) {
                        // if(walletLog.getOptType()==(short)8||walletLog.getOptType()==(short)29) {
                        object = new JSONObject();
                        object.put("log_id", walletLog.getId());
                        object.put("create_time", walletLog.getCreateTime());
                        BigDecimal value = walletLog.getAmount().setScale(5, BigDecimal.ROUND_HALF_UP);
                        value = value.compareTo(new BigDecimal("0")) < 0 ? value.negate() : value;
                        object.put("qty", value);
                        object.put("opt_type", walletLog.getOptType());
                        object.put("coin", "搜秀链");
                        array.add(object);
                        //}
                    }
                }
            } else {
                List<SCAWalletLog> list = scaWalletLogMapper.selectListByUserIdSymbol(LibSysUtils.toInt(user_id), token_symbol, index, count);
                if (list.size() > 0) {
                    for (SCAWalletLog walletLog : list) {
                        object = new JSONObject();
                        object.put("log_id", walletLog.getId());
                        object.put("create_time", walletLog.getCreateTime());
                        BigDecimal value = walletLog.getAmount().setScale(5, BigDecimal.ROUND_HALF_UP);
                        value = value.compareTo(new BigDecimal("0")) < 0 ? value.negate() : value;
                        object.put("qty", value);
                        object.put("opt_type", walletLog.getOptType());
                        object.put("coin", "搜秀链");
                        array.add(object);
                    }
                }
            }

            JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));
            double emo2scagold = coin_proportion.optDouble("emo2scagold");
            double emo2rmb = coin_proportion.optDouble("emo2rmb");
            List<ExchangeItem> exchangeItems = exchangeItemMapper.selectAllExchangeItems();
            JSONArray array2 = new JSONArray();
            for (ExchangeItem item : exchangeItems) {
                JSONObject obj = new JSONObject();
                obj.put("item_id", item.getId());
                obj.put("exc_num", item.getExcNum());

                if (item.getType() == 2) {
                    obj.put("get_num", item.getExcNum() * coin_proportion.optDouble("sca2scagold"));
                    array2.add(obj);
                }

            }
            result.put("sca_exchange_list", array2);
            result.put("list", array);
        }
        System.out.println(result.toString());
        return result;
    }


    /**
     * SCA 、SCA GOLD记录同接口相同数据格式返回，处理类型冲突
     *
     * @param optType
     * @return
     */
    private int exchangeNewType(int optType) {
        int type;
        switch (optType) {
            case 0:
                type = 10;  //打赏收入SCA GOLD
                break;
            case 1:
                type = 11;  //emo兑换SCA GOLD
                break;
            case 2:
                type = 12;  //sca兑换SCA GOLD
                break;
            case 3:
                type = 13;  //SCA GOLD 兑换 充值sca
                break;
            default:
                type = 10;
                break;
        }
        return type;
    }


    /**
     * 数字货币提现申请
     *
     * @param user_id     用户ID
     * @param lang_code   用户语言
     * @param wallet_id   钱包id
     * @param draw_num    提现数量
     * @param pay_type    收款方式  1支付宝  2paypal  3银行卡 4 Eth
     * @param pay_account 收款账号
     * @return
     */
    @Transactional
    public JSONObject ApplyWithdraw(int user_id, String lang_code, long wallet_id, double draw_num, int pay_type, String pay_account,
                                    String pay_name, String bank_account, String bank_name, String eth_address, double api_version) {
        JSONObject withdrawConfig = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_withdraw_config));
        if (!withdrawConfig.optBoolean("on_off", false)) {
            return LibSysUtils.getResultJSON(ResultCode.withdraw_close, LibProperties.getLanguage(lang_code, "withdraw.close"));
        }

        if (WKCache.isLockoutUser(user_id)) {
            return LibSysUtils.getResultJSON(ResultCode.lockout_no_withdraw, LibProperties.getLanguage(lang_code, "post.lockout.no.withdraw"));
        }

        if (withDrawMapper.findByUserId(user_id) > 0) {
            return LibSysUtils.getResultJSON(ResultCode.withdraw_exist, LibProperties.getLanguage(lang_code, "weking.lang.app.withdraw.exist"));
        }
        //判断提现数量
        if (draw_num < withdrawConfig.optInt("sca_num", 150)) {
            // return LibSysUtils.getResultJSON(ResultCode.withdraw_sca_num, LibProperties.getLanguage(lang_code, "withdraw.over.scaNum"));
            return LibSysUtils.getResultJSON(ResultCode.withdraw_sca_num, String.format(LibProperties.getLanguage(lang_code, "withdraw.over.scaNum"), withdrawConfig.optInt("sca_num", 150)));
        }
       /* //判断ETH地址
        if (!eth_address.startsWith("0x")||eth_address.length()!=42){
            return LibSysUtils.getResultJSON(ResultCode.withdraw_exist, LibProperties.getLanguage(lang_code, "weking.lang.app.withdraw.exist"));
        }*/
        List<WithDraw> withDrawList = withDrawMapper.selectTodayListByUserId(user_id, LibDateUtils.getLibDateTime("yyyyMMdd000000"));
        if (withDrawList.size() >= withdrawConfig.optInt("times", 1)) {
            return LibSysUtils.getResultJSON(ResultCode.withdraw_over_times, LibProperties.getLanguage(lang_code, "withdraw.over.times"));
        }
        DigitalWallet digitalWallet = digitalWalletMapper.selectByKey(wallet_id);
        if (api_version < 3.7) {
            if (draw_num >= digitalWallet.getWithdrawAmount().divide(new BigDecimal(2)).intValue()) {
                return LibSysUtils.getResultJSON(ResultCode.withdraw_over_money, String.format(LibProperties.getLanguage(lang_code, "withdraw.over.money"), withdrawConfig.optString("max_num", "50%")));
            }
        } else {
            if (draw_num >= digitalWallet.getWithdrawAmount().add(digitalWallet.getCurrAmount()).divide(new BigDecimal(2)).intValue()) {
                return LibSysUtils.getResultJSON(ResultCode.withdraw_over_money, String.format(LibProperties.getLanguage(lang_code, "withdraw.over.money"), withdrawConfig.optString("max_num", "50%")));
            }
        }

        String currency = userService.getUserInfoByUserId(user_id, "wallet_currency");
        double d_rate = digitalCurrencyMapper.selectByCurrency(currency);
        double draw_money = digitalWallet.getPrice() * d_rate * draw_num;
        double amount = draw_money;
        if (!currency.equals("USD")) {
            amount = draw_money / d_rate;
        }
        if (amount > withdrawConfig.optDouble("money")) {
            //return LibSysUtils.getResultJSON(ResultCode.withdraw_over_money, LibProperties.getLanguage(lang_code, "withdraw.over.money"));
            return LibSysUtils.getResultJSON(ResultCode.withdraw_over_money, String.format(LibProperties.getLanguage(lang_code, "withdraw.over.money"), withdrawConfig.optString("max_num", "50%")));
        }

        BigDecimal value = new BigDecimal(draw_num);

        JSONObject object = OptWallect(user_id, lang_code, 0, "SCA", value, (short) 1, LibSysUtils.getRandomNum(16), "提现扣除", "", api_version);
        if (object.getInt("code") != 0) {
            return object;
        }

        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        WithDraw withDraw = new WithDraw();
        withDraw.setDrawTime(LibDateUtils.getLibDateTime());
        withDraw.setUserId(user_id);
        withDraw.setDrawMoney(new BigDecimal(draw_money).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        withDraw.setDraw_type((byte) CoinTypeEnum.getEnum(digitalWallet.getSymbol()).getType());
        withDraw.setDraw_num(draw_num);
        withDraw.setPay_type((byte) pay_type);
        withDraw.setPay_account(pay_account);
        withDraw.setPaymentSn(object.getString("txid"));
        withDraw.setExtend_id(object.getLong("logid"));
        withDraw.setPay_name(pay_name);
        withDraw.setBank_account(bank_account);
        withDraw.setBank_name(bank_name);
        withDraw.setCurrency(currency);
        withDraw.setEth_address(eth_address.trim());
        double rate = LibSysUtils.toDouble(WKCache.get_system_cache(C.WKSystemCacheField.withdraw_fee_rate));
        BigDecimal fee = new BigDecimal(draw_money * rate).setScale(2, BigDecimal.ROUND_HALF_UP);
        double temp_fee = fee.doubleValue();
        if (!currency.equals("USD")) {
            temp_fee = temp_fee / d_rate;
        }
        if (temp_fee >= 100) {
            if (currency.equals("USD")) {
                fee = new BigDecimal(100);
            } else {
                fee = new BigDecimal(100 * d_rate);
            }
        }
        withDraw.setFee(fee.doubleValue());
        withDrawMapper.insert(withDraw);

        result.put("msg", LibProperties.getLanguage(lang_code, "withdraw.submit.success"));
        return result;
    }


    /**
     * 用户SCA GOLD钱包信息
     *
     * @param userId
     * @param currency
     * @return
     */
    public JSONObject SCAGoldInfo(long userId, String currency, String lang_code, int index, int count) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        currency = LibSysUtils.isNullOrEmpty(currency) ? "CNY" : currency;
        JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));
        PocketInfo pocketInfo = pocketInfoMapper.selectByUserid((int) userId);
        if (pocketInfo == null) {
            return LibSysUtils.getResultJSON(ResultCode.system_error, LibProperties.getLanguage(lang_code, "weking.lang.app.data.error"));
        }
        BigDecimal my_sca_gold = pocketInfo.getSca_gold().setScale(2, BigDecimal.ROUND_HALF_UP);
        result.put("my_sca_gold", my_sca_gold);
        long sca_gold_pool = WKCache.getSCAGoldPoolValue();
        result.put("sca_gold_pool", sca_gold_pool);
        double emo2scagold = coin_proportion.optDouble("emo2scagold");
//        double emo2rmb = coin_proportion.optDouble("emo2rmb");
//        BigDecimal amount = my_sca_gold.divide(new BigDecimal(emo2scagold)).multiply(new BigDecimal(emo2rmb));
//        double cny_to_usd_rate = digitalCurrencyMapper.selectByCurrency("CNY");
//        double usd_to_other_rate = digitalCurrencyMapper.selectByCurrency(currency);
//        BigDecimal total_amount = amount.divide(new BigDecimal(cny_to_usd_rate), 20, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(usd_to_other_rate)).setScale(2, BigDecimal.ROUND_HALF_UP);

        result.put("total_amount", 0);
        result.put("currency", currency);

        List<ExchangeItem> exchangeItems = exchangeItemMapper.selectAllExchangeItems();

        JSONArray array1 = new JSONArray();
        JSONArray array2 = new JSONArray();
        JSONArray array3 = new JSONArray();
        for (ExchangeItem item : exchangeItems) {
            JSONObject object = new JSONObject();
            object.put("item_id", item.getId());
            object.put("exc_num", item.getExcNum());
            if (item.getType() == 1) {
                object.put("get_num", item.getExcNum() * emo2scagold);
                array1.add(object);
            }
            if (item.getType() == 2) {
                object.put("get_num", item.getExcNum() * coin_proportion.optDouble("sca2scagold"));
                array2.add(object);
            }

            if (item.getType() == 3) {
                Double rate = 1 / coin_proportion.optDouble("scagold2sca");
                object.put("get_num", item.getExcNum() * rate);
                array3.add(object);
            }
        }
        result.put("emo_exchange_list", array1);
        result.put("sca_exchange_list", array2);
        result.put("scaGold_exchange_list", array3);
        //兑换记录
        if (count == 0) {
            count = 100;
        }
        List<ScaGoldLog> list = scaGoldLogMapper.selectScaGoldLogsByUserId((int) userId, index, count);
        JSONArray array = new JSONArray();
        if (list.size() > 0) {
            for (ScaGoldLog info : list) {
                JSONObject object = new JSONObject();
                object.put("log_id", info.getId());
                object.put("create_time", info.getAddTime());
                BigDecimal value = info.getConvNum().setScale(5, BigDecimal.ROUND_HALF_UP);
                value = value.compareTo(new BigDecimal("0")) < 0 ? value.negate() : value;
                object.put("qty", value);
                object.put("opt_type", exchangeNewType(info.getType()));
                object.put("coin", "搜秀金");
                array.add(object);
            }
        }
        result.put("list", array);
        System.out.println(result.toString());
        return result;
    }

    /**
     * 兑换SCA GOLD
     *
     * @param userId
     * @param item_id
     * @param lang_code
     * @return
     */
    public JSONObject convScaGold(int userId, int item_id, int type, String currency, String lang_code, double api_version) {
        //public JSONObject convScaGold(int userId, int item_id,  String currency, String lang_code) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        ExchangeItem exchangeItem = exchangeItemMapper.selectByPrimaryKey(item_id);
        if (exchangeItem == null) {
            return LibSysUtils.getResultJSON(ResultCode.system_error, LibProperties.getLanguage(lang_code, "weking.lang.app.data.error"));
        }
        JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));
        double rate;

        if (api_version >= 3.7) {
            switch (type) {

                //sca兑换  sca Gold
                case 2:
                    rate = coin_proportion.optDouble("sca2scagold");
                    JSONObject object = OptWallect(userId, lang_code, 0, "SCA", new BigDecimal(exchangeItem.getExcNum()), (short) 29, LibSysUtils.getRandomNum(16), "搜秀链兑换搜秀金", "", api_version);
                    if (object.getInt("code") != 0) {
                        return object;
                    }
                    int conNum = (int) (exchangeItem.getExcNum() * rate);
                    ScaGoldLog SCAGoldLog = ScaGoldLog.getScaGoldLog(userId, new BigDecimal(exchangeItem.getExcNum()), new BigDecimal(conNum),
                            Integer.valueOf(exchangeItem.getType()), LibDateUtils.getLibDateTime());
                    scaGoldLogMapper.insertSelective(SCAGoldLog);
                    pocketInfoMapper.updateScaGoldByUserId(userId, conNum);

                    WKCache.increaseToSCAGoldPool(conNum);
                    WKCache.addUserSCAGold(userId, conNum);
                    break;
                case 3:
                    //判断用户余额sca gold
                    PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(userId);
                    if (pocketInfo != null) {
                        if (pocketInfo.getSca_gold().intValue() < exchangeItem.getExcNum()) {
                            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
                        }
                    }

                    double scaRate = coin_proportion.optDouble("scagold2sca");
                    double r = 1 / scaRate;

                    //能兑换多少sca
                    //int convNum = (int) (exchangeItem.getExcNum() * rate);
                    BigDecimal num = new BigDecimal(exchangeItem.getExcNum() * r);
                    //增加scagold 收支记录
                    ScaGoldLog goldLog = ScaGoldLog.getScaGoldLog(userId, num, new BigDecimal(exchangeItem.getExcNum()),
                            Integer.valueOf(exchangeItem.getType()), LibDateUtils.getLibDateTime());
                    scaGoldLogMapper.insertSelective(goldLog);

                    //减少sca gold  数量
                    pocketInfoMapper.updateUserScaGoldByUserId(userId, -exchangeItem.getExcNum());

                    //增加sca 数量  加入不可提现sca
                    JSONObject objt = OptWallect(userId, lang_code, 0, "SCA", num, (short) 30, LibSysUtils.getRandomNum(16), "搜秀金兑换搜秀链", "", api_version);
                    if (objt.getInt("code") != 0) {
                        return objt;
                    }
                    //减少 奖金池sca gold 数量
                    WKCache.increaseToSCAGoldPool(-exchangeItem.getExcNum());
                    //减少 用户sca gold 数量
                    WKCache.addUserSCAGold(userId, -exchangeItem.getExcNum());
                default:
                    rate = 0;
                    break;
            }

        } else {
       /* //jiuban
            switch (exchangeItem.getType()) {
                case 1:
                    rate = coin_proportion.optDouble("emo2scagold");
                    int i = pocketInfoMapper.deductDiamondByUserid(exchangeItem.getExcNum(), userId); //扣钱
                    if (i <= 0) { //余额不足
                        return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
                    }
                    break;
                case 2:
                    rate = coin_proportion.optDouble("sca2scagold");
                    String msg= api_version+"";
                    JSONObject object = OptWallect(userId, lang_code, 0, "SCA", new BigDecimal(exchangeItem.getExcNum()), (short) 6, LibSysUtils.getRandomNum(16), "兑换SCA GOLD", msg);
                    if (object.getInt("code") != 0) {
                        return object;
                    }
                    break;
                default:
                    rate = 0;
                    break;
            }
            int convNum = (int) (exchangeItem.getExcNum() * rate);
            ScaGoldLog scaGoldLog = ScaGoldLog.getScaGoldLog(userId,new BigDecimal(exchangeItem.getExcNum()),new BigDecimal(convNum),
                    Integer.valueOf(exchangeItem.getType()),LibDateUtils.getLibDateTime());
            scaGoldLogMapper.insertSelective(scaGoldLog);
            pocketInfoMapper.updateScaGoldByUserId(userId, convNum);
            WKCache.increaseToSCAGoldPool(convNum);
            WKCache.addUserSCAGold(userId, convNum);*/

            switch (type) {
                //充值sca 兑换  sca Gold
                case 1:
                    rate = coin_proportion.optDouble("sca2scagold");
                    JSONObject obj = OptWallect(userId, lang_code, 0, "SCA", new BigDecimal(exchangeItem.getExcNum()), (short) 6, LibSysUtils.getRandomNum(16), "搜秀链兑换搜秀金", "", api_version);
                    if (obj.getInt("code") != 0) {
                        return obj;
                    }
                    int convNum = (int) (exchangeItem.getExcNum() * rate);
                    ScaGoldLog scaGoldLog = ScaGoldLog.getScaGoldLog(userId, new BigDecimal(exchangeItem.getExcNum()), new BigDecimal(convNum),
                            Integer.valueOf(exchangeItem.getType()), LibDateUtils.getLibDateTime());
                    scaGoldLogMapper.insertSelective(scaGoldLog);
                    pocketInfoMapper.updateScaGoldByUserId(userId, convNum);

                    WKCache.increaseToSCAGoldPool(convNum);
                    WKCache.addUserSCAGold(userId, convNum);
                    break;
                //分红sca兑换  sca Gold
                case 2:
                    rate = coin_proportion.optDouble("sca2scagold");
                    JSONObject object = OptWallect(userId, lang_code, 0, "SCA", new BigDecimal(exchangeItem.getExcNum()), (short) 29, LibSysUtils.getRandomNum(16), "搜秀链兑换搜秀金", "", api_version);
                    if (object.getInt("code") != 0) {
                        return object;
                    }
                    int conNum = (int) (exchangeItem.getExcNum() * rate);
                    ScaGoldLog SCAGoldLog = ScaGoldLog.getScaGoldLog(userId, new BigDecimal(exchangeItem.getExcNum()), new BigDecimal(conNum),
                            Integer.valueOf(exchangeItem.getType()), LibDateUtils.getLibDateTime());
                    scaGoldLogMapper.insertSelective(SCAGoldLog);
                    pocketInfoMapper.updateScaGoldByUserId(userId, conNum);

                    WKCache.increaseToSCAGoldPool(conNum);
                    WKCache.addUserSCAGold(userId, conNum);
                    break;
                case 3:
                    //判断用户余额sca gold
                    PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(userId);
                    if (pocketInfo != null) {
                        if (pocketInfo.getSca_gold().intValue() < exchangeItem.getExcNum()) {
                            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.app.live_not_sufficient_funds"));
                        }
                    }

                    double scaRate = coin_proportion.optDouble("scagold2sca");
                    double r = 1 / scaRate;

                    //能兑换多少sca
                    //int convNum = (int) (exchangeItem.getExcNum() * rate);
                    BigDecimal num = new BigDecimal(exchangeItem.getExcNum() * r);
                    //增加scagold 收支记录
                    ScaGoldLog goldLog = ScaGoldLog.getScaGoldLog(userId, num, new BigDecimal(exchangeItem.getExcNum()),
                            Integer.valueOf(exchangeItem.getType()), LibDateUtils.getLibDateTime());
                    scaGoldLogMapper.insertSelective(goldLog);

                    //减少sca gold  数量
                    pocketInfoMapper.updateUserScaGoldByUserId(userId, -exchangeItem.getExcNum());

                    //增加sca 数量  加入不可提现sca
                    JSONObject objt = OptWallect(userId, lang_code, 0, "SCA", num, (short) 30, LibSysUtils.getRandomNum(16), "搜秀金兑换搜秀链", "", api_version);
                    if (objt.getInt("code") != 0) {
                        return objt;
                    }
                    //减少 奖金池sca gold 数量
                    WKCache.increaseToSCAGoldPool(-exchangeItem.getExcNum());
                    //减少 用户sca gold 数量
                    WKCache.addUserSCAGold(userId, -exchangeItem.getExcNum());
                default:
                    rate = 0;
                    break;
            }

        }
        result.put("msg", LibProperties.getLanguage(lang_code, "conversion.success"));
        return result;
    }


    /**
     * 获取兑换SCA GOLD记录
     *
     * @param userId
     * @param lang_code
     * @param index
     * @param count
     * @return
     */
    public JSONObject getSCAGoldLog(int userId, String lang_code, int index, int count) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        List<ScaGoldLog> list = scaGoldLogMapper.selectScaGoldLogsByUserId(userId, index, count);
        if (list.size() > 0) {
            JSONArray array = new JSONArray();
            for (ScaGoldLog info : list) {
                JSONObject object = new JSONObject();
                object.put("log_id", info.getId());
                object.put("create_time", info.getAddTime());
                BigDecimal value = info.getConvNum().setScale(5, BigDecimal.ROUND_HALF_UP);
                object.put("qty", value);
                object.put("opt_type", info.getType());
                array.add(object);
            }
            result.put("list", array);
        }
//        System.out.println(result.toString());
        return result;
    }

    // 用户分红
    public void userDividendSCA() {
//        List<Map<String,Object>> list = pocketInfoMapper.selectScaGoldUser();
        Double platformIncome = platformIncomeMapper.getTodayPlatformIncome(DateUtils.getFrontDay(LibDateUtils.getLibDateTime("yyyyMMdd"), 1));
        if (platformIncome != null) {
            logger.info("-------- 用户分红 --------");
            Set<String> list = WKCache.getAllUserSCAGoldList();
            if (list.size() > 0) {
                long sca_gold_pool = WKCache.getSCAGoldPoolValue();
                if (sca_gold_pool != 0) {
                    double rate = LibSysUtils.toDouble(WKCache.get_system_cache(C.WKSystemCacheField.dividend_scagold_rate));
//            for (Map<String,Object> map : list){
//            int userId = LibSysUtils.toInt(map.get("user_id"));
//            BigDecimal scaGold = new BigDecimal(LibSysUtils.toString(map.get("sca_gold")));
                    JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));
                    double sca2rmb = coin_proportion.optDouble("sca2rmb");
                    for (String temp : list) {
                        int userId = LibSysUtils.toInt(temp);
                        PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(userId);
                        if (pocketInfo.getSca_gold().compareTo(new BigDecimal("0")) > 0) {
                            BigDecimal reward_rmb = pocketInfo.getSca_gold().divide(new BigDecimal(sca_gold_pool), 20, BigDecimal.ROUND_HALF_UP).
                                    multiply(new BigDecimal(platformIncome)).multiply(new BigDecimal(rate));
                            BigDecimal reward = reward_rmb.divide(new BigDecimal(sca2rmb), 20, BigDecimal.ROUND_HALF_UP);
                            OptWallect(userId, "", 0, "SCA", reward, (short) 8, LibSysUtils.getRandomNum(16), "分红奖励", "", 0.0);
                            logger.info(String.format(" user_id:%s reward:%s", userId, reward));
                        }
                    }
                }
            }
        }
    }

    // 挖矿奖励
    public void userMiningReward(String user_id, String post) {
        JSONObject post_mining_emo = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.post_mining_emo));
        int reward_num = 0;
        int type = 0;
        switch (post) {
            case "post":
                reward_num = post_mining_emo.optInt("post", 0);
                type = 1;
                break;
            case "comment":
                reward_num = post_mining_emo.optInt("comment", 0);
                type = 2;
                break;
            case "like":
                reward_num = post_mining_emo.optInt("like", 0);
                type = 3;
                break;
            case "share":
                reward_num = post_mining_emo.optInt("share", 0);
                type = 4;
                break;
            default:
                break;
        }
        if (reward_num > 0) {
            MiningLog miningLog = MiningLog.getMiningLog(LibSysUtils.toInt(user_id), reward_num, LibDateUtils.getLibDateTime(), (byte) type,
                    "", 0);
            miningLogMapper.insertSelective(miningLog);
            //赠送emo后 获得新的比值
            Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(LibSysUtils.toInt(user_id), "ratio"));//现有的比值
            Integer totalDiamond=0;
            BigDecimal newRatio;
            if(ratio>0){
                PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(LibSysUtils.toInt(user_id));
                if(pocketInfo!=null) {
                    totalDiamond = pocketInfo.getTotalDiamond();//现有的emo
                }
                if(totalDiamond>0) {
                    double v = (reward_num + totalDiamond) / (totalDiamond / ratio);
                    newRatio = new BigDecimal(v).setScale(8, BigDecimal.ROUND_HALF_UP);
                }else {
                    newRatio = new BigDecimal(0).setScale(8, BigDecimal.ROUND_HALF_UP);
                }
                accountInfoMapper.updateRatioByUserid(newRatio,LibSysUtils.toInt(user_id));
                //WKCache.add_user(LibSysUtils.toInt(user_id), "ratio", LibSysUtils.toString(newRatio));
            }
            //增加用户货币
            pocketInfoMapper.increaseDiamondByUserId(LibSysUtils.toInt(user_id), reward_num);
            logger.info(String.format(" user_id:%s reward:%s", LibSysUtils.toInt(user_id), reward_num));
        }
    }


    @Transactional
    public boolean deductGameFire(GameFire gameFire, int win, int userId, double api_version) {

        boolean isDeductOK;// 是否扣、加钱成功

        if (win != 0) { // 等于0不需要更新数据库
            int re = 0;
            if (api_version < 3.7) {
                re = digitalWalletMapper.increaseDiamondByUserId(userId, win);
            } else {
                //新版 扣 加分红SCA
                if (win > 0) {
                    re = digitalWalletMapper.increaseWithDrawAmountByUserId(userId, win);
                } else {
                    int userWithdrawAmount = digitalWalletMapper.getUserWithdrawAmount(userId);
                    if (userWithdrawAmount >= (-win)) {
                        re = digitalWalletMapper.increaseWithDrawAmountByUserId(userId, win);

                    } else {
                        int typeNum = win + userWithdrawAmount;
                        re = digitalWalletMapper.deductAllDiamondWithDrawAmountByUserId(userWithdrawAmount, userId);
                        re = digitalWalletMapper.increaseDiamondByUserId(userId, typeNum);
                    }
                }
            }
            isDeductOK = re > 0;

        } else {
            isDeductOK = true;
        }

        if (isDeductOK) { // 更新钱包成功才插入游戏记录
            gameFireMapper.insert(gameFire);//插入游戏记录
        }
        return isDeductOK;
    }

    //主播抽成
    @Transactional
    public void anchorSubsidy(int userId, long gameId, double gameAllBet, double api_version) {
        if (gameAllBet > 0) {
            double subsidy = LibSysUtils.toDouble(WKCache.get_system_cache(C.WKSystemCacheField.s_game_anchor_subsidy));
//            int anchorSubsidy = (int) Math.ceil(gameAllBet * subsidy);
            int anchorSubsidy = new BigDecimal(gameAllBet * subsidy).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();// 四舍五入 取整

            if (anchorSubsidy > 0) {
                int re = gameService.recordSubsidy(userId, anchorSubsidy, gameId);
                if (re > 0) {
                    if (api_version < 3.7) {
                        digitalWalletMapper.increaseTicketByUserid(anchorSubsidy, userId);
                    } else {
                        digitalWalletMapper.increaseTicketWithDrawAmountByUserid(anchorSubsidy, userId);
                    }
                    OptWallect(userId, "", 0, "SCA", new BigDecimal(anchorSubsidy), (short) 33, LibSysUtils.getRandomNum(16), "主播游戏分红获得搜秀链", "", api_version);

                }
            }
        }

    }


    //扣減游戏下注sca
    @Transactional
    public JSONObject deductDiamond(int userId, String account, int liveId, int positionId, String position,
                                    int betNum, int my_diamond, int gameType, String lang_code, double api_version) {
        JSONObject object;
        int re = 0;
        if (api_version < 3.7) {
            re = digitalWalletMapper.deductAllDiamondByUserId(betNum, userId);
        } else {
            int userWithdrawAmount = digitalWalletMapper.getUserWithdrawAmount(userId);
            if (userWithdrawAmount >= betNum) {
                re = digitalWalletMapper.deductAllDiamondWithDrawAmountByUserId(betNum, userId);
            } else {
                int typeNum = betNum - userWithdrawAmount;
                re = digitalWalletMapper.deductAllDiamondWithDrawAmountByUserId(userWithdrawAmount, userId);
                re = digitalWalletMapper.deductAllDiamondByUserId(typeNum, userId);
            }
        }
        OptWallect(userId, "", 0, "SCA", new BigDecimal(betNum), (short) 31, LibSysUtils.getRandomNum(16), "扣減游戏下注搜秀链", "", api_version);

        if (re > 0) {
            int rest_diamond = my_diamond - betNum; //剩下 SCA
            gameService.recordBetLog(userId, GameCache.get_game_id(liveId), positionId, betNum);
            GameCache.add_game_user(liveId, account, betNum, position);
            object = LibSysUtils.getResultJSON(ResultCode.success);
            object.put("my_diamonds", rest_diamond);
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.game_bet_error, LibProperties.getLanguage(lang_code, "weking.lang.app.game.bet.error"));
        }
        return object;
    }

    //游戏收入计算
    @Transactional
    public void gameIncome(int userId, List<Commission> commissionList, Map<String, Integer> accountUserIdMap,
                           Map<String, Integer> winUserIdMoney, Map<String, Integer> betUser, long gameId, int gameType, double api_version) {

        if (winUserIdMoney.size() > 0) {
            if (api_version < 3.7) {
                digitalWalletMapper.batchIncreaseDiamond(winUserIdMoney);
            } else {
                digitalWalletMapper.batchIncreaseDiamondWithdrawAmount(winUserIdMoney);
            }
            for (Map.Entry<String, Integer> map : winUserIdMoney.entrySet()) {
                OptWallect(LibSysUtils.toInt(map.getKey()), "", 0, "SCA", new BigDecimal(map.getValue()), (short) 32, LibSysUtils.getRandomNum(16), "玩游戏获得搜秀链", "", api_version);
            }
            //statistics(winUserIdMoney);
        }
        if (betUser.size() > 0) {
            gameService.recordGameBetLog(userId, accountUserIdMap, winUserIdMoney, betUser, gameId, gameType);
        }

        if (commissionList.size() > 0) {
            // 当用户的钱包表中不够扣抽水时，会扣完钱包，钻石为0，
            // 但是commission表中还是记录了抽水值不变，会导致两边账不一致
            commissionMapper.insertByBatch(commissionList);
        }
    }


}
