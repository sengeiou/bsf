package com.weking.service.pay;

import com.weking.cache.GameCache;
import com.weking.cache.WKCache;
import com.weking.core.*;
import com.weking.core.gash.gash;
import com.weking.core.newebpay.H5NewebPay;
import com.weking.core.newebpay.NewebPay;
import com.weking.core.newebpay.NewebPayNew;
import com.weking.core.pay.PayssionUtil;
import com.weking.core.payNow.HongYangUtil;
import com.weking.core.payNow.PayNowUtil;
import com.weking.core.payNow.YiPayUtil;
import com.weking.mapper.account.UserBillMapper;
import com.weking.mapper.commission.CommissionMapper;
import com.weking.mapper.digital.DigitalWalletMapper;
import com.weking.mapper.game.GameFireMapper;
import com.weking.mapper.log.ConsumeInfoMapper;
import com.weking.mapper.log.FrozenLogMapper;
import com.weking.mapper.log.ScaGoldLogMapper;
import com.weking.mapper.pocket.*;
import com.weking.mapper.statistics.StatisticsMapper;
import com.weking.mapper.withdrawlog.WithDrawMapper;
import com.weking.model.commission.Commission;
import com.weking.model.digital.DigitalWallet;
import com.weking.model.game.GameFire;
import com.weking.model.log.ConsumeInfo;
import com.weking.model.log.FrozenLog;
import com.weking.model.log.ScaGoldLog;
import com.weking.model.pocket.*;
import com.weking.model.withdrawlog.WithDraw;
import com.weking.service.digital.DigitalService;
import com.weking.service.game.GameService;
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
import java.text.DecimalFormat;
import java.util.*;

@Service("pocketService")
public class PocketService extends LibServiceBase {

    private static Logger logger = Logger.getLogger(PocketService.class);
    @Resource
    private RechargeListMapper rechargeListMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private PocketInfoMapper pocketInfoMapper;
    @Resource
    private WithDrawMapper withDrawMapper;
    @Resource
    private UserService userService;
    @Resource
    private GameFireMapper gameFireMapper;
    @Resource
    private ConsumeInfoMapper consumeInfoMapper;
    @Resource
    private GameService gameService;
    @Resource
    private CommissionMapper commissionMapper;
    @Resource
    private StatisticsMapper statisticsMapper;
    @Resource
    private FrozenLogMapper frozenLogMapper;
    @Resource
    private DigitalWalletMapper digitalWalletMapper;
    @Resource
    private ScaGoldLogMapper scaGoldLogMapper;

    @Resource
    private DigitalService digitalService;

    @Resource
    private UserBillMapper userBillMapper;

    @Resource
    private UserGainMapper userGainMapper;
    @Resource
    private GiftInfoMapper giftInfoMapper;

    //可用充值方式
    public JSONObject getRecharge(double version) {
        double appVersion = LibSysUtils.toDouble(WKCache.get_system_cache("WX_RECHARGE_PAY"));
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("wx_pay", version != appVersion);
        return object;
    }

    /**
     * @param type        0微信、2苹果、1支付宝、3谷歌、5GASH,6SCA,7ETH,9后台 10ipay88
     * @param device_type ios,android
     */
    public JSONObject getRechargeList(int userId, int type, String device_type, double version, String project_name,String channel) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);

        if (LibSysUtils.isNullOrEmpty(device_type)) {//兼容旧接口
            object.put("list", internal_getRechargeList(userId, type, project_name, version));
        } else {
            JSONArray array = new JSONArray();
            JSONObject obj = new JSONObject();
            if (device_type.equals("ios")) {
                double appVersion = LibSysUtils.toDouble(WKCache.get_system_cache("WX_RECHARGE_PAY"));
                JSONArray array1;
                if ("yuanMi".equalsIgnoreCase(channel)) {
                    array1 = internal_getRechargeList(userId, C.RechargeType.apple, channel, version);
                }else {
                    array1= internal_getRechargeList(userId, C.RechargeType.apple, project_name, version);
                }
                if (array1.size() > 0) {
                    obj.put("type", C.RechargeType.apple);
                    obj.put("list", array1);
                    array.add(obj);//苹果支付
                }

                if (appVersion != version) {
                    JSONArray tmp = internal_getRechargeList(userId, C.RechargeType.wx, project_name, version);//微信支付
                    if (tmp.size() > 0) {
                        obj.put("type", C.RechargeType.wx);
                        obj.put("list", tmp);
                        array.add(obj);
                    }
                    JSONArray gashTmp = internal_getRechargeList(userId, C.RechargeType.gashpay, project_name, version);//gash支付
                    if (gashTmp.size() > 0) {
                        obj.put("type", C.RechargeType.gashpay);
                        obj.put("list", gashTmp);
                        array.add(obj);
                    }

                    JSONArray scaTmp = internal_getRechargeList(userId, C.RechargeType.sca, project_name, version);//scaTmp
                    if (scaTmp.size() > 0) {
                        obj.put("type", C.RechargeType.sca);
                        obj.put("list", scaTmp);
                        array.add(obj);
                    }

                    JSONArray ethTmp = internal_getRechargeList(userId, C.RechargeType.eth, project_name, version);//eth
                    if (ethTmp.size() > 0) {
                        obj.put("type", C.RechargeType.eth);
                        obj.put("list", ethTmp);
                        array.add(obj);
                    }

//                    JSONArray ipay88Tmp = internal_getRechargeList(userId, C.RechargeType.ipay88, project_name, version);//ipay88
//                    if (gashTmp.size() > 0) {
//                        obj.put("type", C.RechargeType.ipay88);
//                        obj.put("list", ipay88Tmp);
//                        array.add(obj);
//                    }
                }
            } else {
                JSONArray gtem = internal_getRechargeList(userId, C.RechargeType.google, project_name, version);
                if (gtem.size() > 0) {
                    obj.put("type", C.RechargeType.google);
                    obj.put("list", internal_getRechargeList(userId, C.RechargeType.google, project_name, version));
                    array.add(obj);//谷歌
                }
                JSONArray tmp = internal_getRechargeList(userId, C.RechargeType.wx, project_name, version);//微信支付
                if (tmp.size() > 0) {
                    obj.put("type", C.RechargeType.wx);
                    obj.put("list", tmp);
                    array.add(obj);
                }
                JSONArray gashTmp = internal_getRechargeList(userId, C.RechargeType.gashpay, project_name, version);//gash支付
                if (gashTmp.size() > 0) {
                    obj.put("type", C.RechargeType.gashpay);
                    obj.put("list", gashTmp);
                    array.add(obj);
                }

                JSONArray scaTmp = internal_getRechargeList(userId, C.RechargeType.sca, project_name, version);//scaTmp
                if (scaTmp.size() > 0) {
                    obj.put("type", C.RechargeType.sca);
                    obj.put("list", scaTmp);
                    array.add(obj);
                }

                JSONArray ethTmp = internal_getRechargeList(userId, C.RechargeType.eth, project_name, version);//eth
                if (ethTmp.size() > 0) {
                    obj.put("type", C.RechargeType.eth);
                    obj.put("list", ethTmp);
                    array.add(obj);
                }

//                JSONArray ipay88Tmp = internal_getRechargeList(userId, C.RechargeType.ipay88, project_name, version);//ipay88
//                if (gashTmp.size() > 0) {
//                    obj.put("type", C.RechargeType.ipay88);
//                    obj.put("list", ipay88Tmp);
//                    array.add(obj);
//                }
            }
            object.put("list", array);
        }
//        System.out.println(object.toString());
        return object;
    }

    private JSONArray internal_getRechargeList(int userId, int type, String project_name, double version) {
        int role = LibSysUtils.toInt(WKCache.get_user(userId, "role"));
        List<RechargeList> list = rechargeListMapper.selectByRechargeType(type, project_name, role, version);
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (RechargeList info : list) {
            jsonObject = new JSONObject();
            jsonObject.put("id", info.getId());
            jsonObject.put("buy_num", info.getBuyNum());
            jsonObject.put("give_num", info.getGiveNum());
            jsonObject.put("currency", info.getCurrency());
            jsonObject.put("pay_money", info.getPayMoney());
            jsonObject.put("third_id", info.getThirdPartyId());
            jsonObject.put("type", type);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    //购买
    @Transactional
    public JSONObject buy(int userId, int rechargeId, String ip, String project_name, String lang_code,
                          String user_name,String email,String payType,String phone) {
        logger.info("购买-------------用户账号"+userId+"=="+rechargeId);
        JSONObject object = null;
        RechargeList rechargeInfo = rechargeListMapper.findByRechargeId(rechargeId);
        if (rechargeInfo != null) {
            int paymentCode = rechargeInfo.getRechargeType();
            switch (paymentCode) {
                case 0: //微信支付
                    object = buyByWx(userId, paymentCode, rechargeInfo, ip, project_name, lang_code);
                    break;
                case 1: //支付宝支付

                    break;
                case 2: //苹果支付
                    object = buyByApple(userId, paymentCode, rechargeInfo, lang_code);
                    break;
                case 3: //谷歌支付
                    object = buyByGoogle(userId, paymentCode, rechargeInfo, lang_code);
                    break;
                case 5: //乐点gash
                    object = buyByGash(userId, paymentCode, rechargeInfo, lang_code);
                    break;
                case 6: //sca
                    return LibSysUtils.getResultJSON(ResultCode.data_error, LibProperties.getLanguage(lang_code, "weking.lang.app.data.error"));
                case 7: //eth
                    object = DigitalBuy(userId, paymentCode, rechargeInfo, lang_code,0);
                    break;
                case 10:  // ipay88
                    object = buyByIPay88(userId, paymentCode, rechargeInfo, lang_code);
                    break;
                case 11: //蓝新科技 支付
                    object = buyByNewebPay(userId, paymentCode, rechargeInfo, lang_code,user_name,email);
                    break;
                case 12: //蓝新科技 支付
                    object = buyByNewebPay(userId, paymentCode, rechargeInfo, lang_code,user_name,email);
                    break;
                case 13: //payNow 支付
                    object = buyByPayNow(userId, paymentCode, rechargeInfo, lang_code,user_name,email,payType);
                    break;
                case 14: //payNow 支付
                    object = buyByPayNow(userId, paymentCode, rechargeInfo, lang_code,user_name,email,payType);
                    break;
                case 15: //YiPay 支付
                    object = buyByYiPay(userId, paymentCode, rechargeInfo, lang_code,user_name,email,payType);
                    break;
                case 16: //蓝新 支付
                    object = buyByNewebPay(userId, paymentCode, rechargeInfo, lang_code,user_name,email);
                    break;
                case 17: //红阳科技 支付
                    object = buyByHongYang(userId, paymentCode, rechargeInfo, lang_code,user_name,email,payType,phone);
                    break;
                case 18: //payssion 支付
                    object = buyByPayssion(userId, paymentCode, rechargeInfo, lang_code,user_name,email,payType,phone);
                    break;
                default:
                    object = LibSysUtils.getResultJSON(ResultCode.payment_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.payment.not.exist"));
                    break;
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.recharge_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.recharge.not.exist"));
        }
        return object;
    }

    /**
     * ipay88 支付
     *
     * @param userId
     * @param paymentCode
     * @param rechargeInfo
     * @param lang_code
     * @return
     */
    private JSONObject buyByIPay88(int userId, int paymentCode, RechargeList rechargeInfo, String lang_code) {
        JSONObject object;
        Map<String, String> userMap = userService.getUserInfoByUserId(userId, "nickname", "email", "phone");
        if (rechargeInfo.getRechargeType() == 10) {  //是否ipay88 支付类型
            String orderSn = getOrderSn();
            int buy_num = rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum();
            int re = recordOrder(userId, orderSn, paymentCode, rechargeInfo.getId(), rechargeInfo.getPayMoney(), buy_num, rechargeInfo.getCurrency(),"","");
            if (re > 0) {
                object = LibSysUtils.getResultJSON(ResultCode.success);
                object.put("merchantCode", WKCache.get_system_cache(C.WKSystemCacheField.ipay88_merchant_code));
                object.put("merchantKey", WKCache.get_system_cache(C.WKSystemCacheField.ipay88_merchant_key));
                BigDecimal bd = new BigDecimal(rechargeInfo.getPayMoney());
                DecimalFormat df = new DecimalFormat(",###,###.00");
                object.put("amount", df.format(bd));
                object.put("currency", rechargeInfo.getCurrency());
                object.put("prodDesc", rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum() + LibSysUtils.getLang("weking.lang.app.mony"));
                object.put("refNo", orderSn);
                object.put("refID", orderSn);
                object.put("remark", userId);
                object.put("country", "MY");
                object.put("paymentId", "");
                object.put("userName", LibSysUtils.isNullOrEmpty(userMap.get("nickname")) ? "Appsme" : userMap.get("nickname"));
                object.put("userContact", LibSysUtils.isNullOrEmpty(userMap.get("phone")) ? "60123456789" : userMap.get("phone"));
                object.put("userEmail", LibSysUtils.isNullOrEmpty(userMap.get("email")) ? "test@appsme.tv" : userMap.get("email"));
                object.put("backendPostURL", WKCache.get_system_cache(C.WKSystemCacheField.ipay88_backend_post_url));
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.recharge_buy_error, LibProperties.getLanguage(lang_code, "weking.lang.recharge.buy.error"));
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.recharge_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.recharge.not.exist"));
        }
        System.out.println(object);
        return object;
    }

    /**
     * 数字货币购买
     */
    private JSONObject DigitalBuy(int userId, int payment_code, RechargeList rechargeInfo, String lang_code,double api_version) {
        JSONObject object;
        BigDecimal payQty = new BigDecimal(rechargeInfo.getPayMoney());
        DigitalWallet digitalWallet = digitalWalletMapper.selectByUserIdSymbol(userId, rechargeInfo.getCurrency());
        if (digitalWallet != null && digitalWallet.getCurrAmount().compareTo(payQty) >= 0) {
            String orderSn = getOrderSn();
            int buy_num = rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum();
            int re = recordOrder(userId, orderSn, payment_code, rechargeInfo.getId(), rechargeInfo.getPayMoney(), buy_num, rechargeInfo.getCurrency(),"","");
            if (re > 0) {
                Order order = orderMapper.selectByOrderSn(userId, orderSn);
                if (order != null && order.getState() == 1) { //是否存在该订单号并且为未付款状态
                    re = orderMapper.updateByOrderId(order.getId(), rechargeInfo.getCurrency(), buy_num);
                    if (re > 0) {
                        //增加用户货币
                        pocketInfoMapper.increaseDiamondByUserId(userId, buy_num);
                        digitalService.OptWallect(userId, lang_code, 0, rechargeInfo.getCurrency(), payQty, (short) 3, orderSn, String.format("buy %d EMO", rechargeInfo.getBuyNum()), "",api_version);
                    }
                }
                object = LibSysUtils.getResultJSON(ResultCode.success);
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.recharge_buy_error, LibProperties.getLanguage(lang_code, "weking.lang.recharge.buy.error"));
            }
        } else
            object = LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.info.nomoney"));
        return object;
    }

    /**
     * 乐点gash支付
     */
    private JSONObject buyByGash(int userId, int payment_code, RechargeList rechargeInfo, String lang_code) {
        JSONObject object;
        if (rechargeInfo.getRechargeType() == 5) {
            String orderSn = getOrderSn();
            int buy_num = rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum();
            int re = recordOrder(userId, orderSn, payment_code, rechargeInfo.getId(), rechargeInfo.getPayMoney(), buy_num, rechargeInfo.getCurrency(),"","");
            logger.info("gash订单号：======="+orderSn);
            if (re > 0) {
                object = LibSysUtils.getResultJSON(ResultCode.success);
                object.put("order_sn", orderSn);
                object.put("data", gash.execute(userId, orderSn, LibSysUtils.toString(rechargeInfo.getPayMoney())));
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.recharge_buy_error, LibProperties.getLanguage(lang_code, "weking.lang.recharge.buy.error"));
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.recharge_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.recharge.not.exist"));
        }
        return object;
    }

    /**
     * 蓝新 支付
     */
    private JSONObject buyByNewebPay(int userId, int payment_code, RechargeList rechargeInfo, String lang_code,String user_name,String email) {
        JSONObject object;
        logger.info("type==============="+rechargeInfo.getRechargeType());
        if (rechargeInfo.getRechargeType() == 11||rechargeInfo.getRechargeType() == 12||rechargeInfo.getRechargeType() == 16) {
            String orderSn = getOrderSn();
            int buy_num = rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum();
            int re = recordOrder(userId, orderSn, payment_code, rechargeInfo.getId(), rechargeInfo.getPayMoney(), buy_num, rechargeInfo.getCurrency(),user_name,email);
            logger.info("蓝新订单号：======="+orderSn);
            if (re > 0) {
                object = LibSysUtils.getResultJSON(ResultCode.success);
                object.put("order_sn", orderSn);
                if(rechargeInfo.getRechargeType() == 11) {
                    //调用接口  官网蓝新
                    object.put("data", NewebPay.getPostData(orderSn, rechargeInfo.getPayMoney().intValue()));
                    logger.info("蓝新1  data：=======" + NewebPay.getPostData(orderSn, rechargeInfo.getPayMoney().intValue()));
                }else if(rechargeInfo.getRechargeType() == 12){
                    //调用接口  非官网蓝新支付
                    object.put("data", H5NewebPay.getPostData(orderSn, rechargeInfo.getPayMoney().intValue()));
                    logger.info("蓝新2  data：=======" + NewebPay.getPostData(orderSn, rechargeInfo.getPayMoney().intValue()));
                }else if(rechargeInfo.getRechargeType() == 16){
                    //调用接口  非官网蓝新支付
                    object.put("data", NewebPayNew.getPostData(orderSn, rechargeInfo.getPayMoney().intValue()));
                    logger.info("蓝新3  data：=======" + NewebPayNew.getPostData(orderSn, rechargeInfo.getPayMoney().intValue()));
                }
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.recharge_buy_error, LibProperties.getLanguage(lang_code, "weking.lang.recharge.buy.error"));
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.recharge_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.recharge.not.exist"));
        }
        return object;

    }

    /**
     * PayNow 支付
     */
    private JSONObject buyByPayNow(int userId, int payment_code, RechargeList rechargeInfo, String lang_code,String user_name,String email,String payType) {
        JSONObject object;
            String orderSn = getOrderSn();
            logger.error("获取数据开始====");
            int buy_num = rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum();
            int re = recordOrder(userId, orderSn, payment_code, rechargeInfo.getId(), rechargeInfo.getPayMoney(), buy_num, rechargeInfo.getCurrency(),user_name,email);
            if (re > 0) {
                //object = LibSysUtils.getResultJSON(ResultCode.success);
                object = PayNowUtil.getPayNowData(orderSn, rechargeInfo.getPayMoney().intValue(), user_name, email,payType,buy_num);
                logger.error("获取数据结束===="+object);
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.recharge_buy_error, LibProperties.getLanguage(lang_code, "weking.lang.recharge.buy.error"));
            }

        return object;

    }

    /**
     * yiPay 支付
     */
    private JSONObject buyByYiPay(int userId, int payment_code, RechargeList rechargeInfo, String lang_code,String user_name,String email,String payType) {
        JSONObject object;
        String orderSn = getOrderSn();
        logger.error("buyByYiPay:获取数据开始====");
        int buy_num = rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum();
        int re = recordOrder(userId, orderSn, payment_code, rechargeInfo.getId(), rechargeInfo.getPayMoney(), buy_num, rechargeInfo.getCurrency(),user_name,email);
        if (re > 0) {
            //object = LibSysUtils.getResultJSON(ResultCode.success);
            object = YiPayUtil.getYiPayData(orderSn, rechargeInfo.getPayMoney().intValue(), user_name, email,payType);
            logger.error("buyByYiPay:获取数据结束===="+object);
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.recharge_buy_error, LibProperties.getLanguage(lang_code, "weking.lang.recharge.buy.error"));
        }

        return object;

    }


    /**
     * 红阳科技 支付
     */
    private JSONObject buyByHongYang(int userId, int payment_code, RechargeList rechargeInfo, String lang_code,
                                     String user_name,String email,String payType,String phone) {
        JSONObject object;
        String orderSn = getOrderSn();
        logger.error("buyByHongYang:获取数据开始====");
        int buy_num = rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum();
        int re = recordOrder(userId, orderSn, payment_code, rechargeInfo.getId(), rechargeInfo.getPayMoney(), buy_num, rechargeInfo.getCurrency(),user_name,email);
        if (re > 0) {
            //object = LibSysUtils.getResultJSON(ResultCode.success);
            object = HongYangUtil.getHongYangData(orderSn, rechargeInfo.getPayMoney().intValue(), user_name, email,payType,phone);
            logger.error("buyByHongYang:获取数据结束===="+object);
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.recharge_buy_error, LibProperties.getLanguage(lang_code, "weking.lang.recharge.buy.error"));
        }

        return object;

    }


    //微信支付
    private JSONObject buyByWx(int userId, int payment_code, RechargeList rechargeInfo, String ip, String project_name, String lang_code) {
        JSONObject object;
        String body = LibProperties.getLanguage(lang_code, "weking.lang." + project_name + "app.mony");
        String orderSn = getOrderSn();
        int payMoney = (int) (rechargeInfo.getPayMoney() * 100);
        String result = WeixinPay.setParam(userId, body, orderSn, payMoney, ip);
        Map map = ParseXMLUtils.getjdomParseXml(result); //解析xml文件
        if (map.size() > 0) {
            String appid = map.get("appid").toString();
            String partnerid = map.get("mch_id").toString();
            String prepayid = map.get("prepay_id").toString();
            String packages = "Sign=WXPay";
            String noncestr = LibSysUtils.getRandomString(16);
            int timestamp = LibSysUtils.toInt(System.currentTimeMillis() / 1000);
            //参数：开始生成签名
            SortedMap<Object, Object> parameters = new TreeMap<>();
            parameters.put("appid", appid);
            parameters.put("partnerid", partnerid);
            parameters.put("prepayid", prepayid);
            parameters.put("package", packages);
            parameters.put("noncestr", noncestr);
            parameters.put("timestamp", timestamp);
            String sign = WeixinPay.createSign(userId, parameters);
            int buy_num = rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum();
            int re = recordOrder(userId, orderSn, payment_code, rechargeInfo.getId(), rechargeInfo.getPayMoney(), buy_num, rechargeInfo.getCurrency(),"","");
            if (re > 0) {
                object = LibSysUtils.getResultJSON(ResultCode.success);
                object.put("appid", appid);
                object.put("partnerid", partnerid);
                object.put("prepayid", prepayid);
                object.put("wx_package", packages);
                object.put("noncestr", noncestr);
                object.put("timestamp", timestamp);
                object.put("sign", sign);
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.recharge_buy_error, LibProperties.getLanguage(lang_code, "weking.lang.recharge.buy.error"));
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.recharge_buy_error, LibProperties.getLanguage(lang_code, "weking.lang.recharge.buy.error"));
        }
        return object;
    }

    /**
     * 苹果支付
     */
    private JSONObject buyByApple(int userId, int payment_code, RechargeList rechargeInfo, String lang_code) {
        JSONObject object;
        if (rechargeInfo.getRechargeType() == 2) {
            String orderSn = getOrderSn();
            int buy_num = rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum();
            int re = recordOrder(userId, orderSn, payment_code, rechargeInfo.getId(), rechargeInfo.getPayMoney(), buy_num, rechargeInfo.getCurrency(),"","");
            logger.info(String.format("buyByApple:re=%d", re));
            if (re > 0) {
                object = LibSysUtils.getResultJSON(ResultCode.success);
                object.put("order_sn", orderSn);
                object.put("product_id", rechargeInfo.getThirdPartyId());
                //log.info(String.format("buyByApple:order_sn=%s,product_id=%s", orderSn, rechargeInfo.getThirdPartyId()));
            } else {
                //log.info("buyByApple:error");
                object = LibSysUtils.getResultJSON(ResultCode.recharge_buy_error, LibProperties.getLanguage(lang_code, "weking.lang.recharge.buy.error"));
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.recharge_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.recharge.not.exist"));
        }
        return object;
    }

    /**
     * 谷歌支付
     */
    private JSONObject buyByGoogle(int userId, int payment_code, RechargeList rechargeInfo, String lang_code) {
        JSONObject object;
        if (rechargeInfo.getRechargeType() == 3) {  //是否谷歌支付类型
            String orderSn = getOrderSn();
            int buy_num = rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum();
            int re = recordOrder(userId, orderSn, payment_code, rechargeInfo.getId(), rechargeInfo.getPayMoney(), buy_num, rechargeInfo.getCurrency(),"","");
            if (re > 0) {
                object = LibSysUtils.getResultJSON(ResultCode.success);
                object.put("product_id", rechargeInfo.getThirdPartyId());
                object.put("order_sn", orderSn);
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.recharge_buy_error, LibProperties.getLanguage(lang_code, "weking.lang.recharge.buy.error"));
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.recharge_not_exist, LibProperties.getLanguage(lang_code, "weking.lang.recharge.not.exist"));
        }
        return object;
    }

    //记录订单
    private int recordOrder(int userId, String orderSn, int paymentCode, int rechargeId, double amount, int num, String currency,String user_name,String email) {
        Order order = new Order();
        long add_time = LibDateUtils.getLibDateTime();
        order.setAddTime(add_time);
        order.setUserId(userId);
        order.setAmount(amount);
        order.setBuyNum(num);
        order.setOrderSn(orderSn);
        order.setCurrency(currency);
        order.setRechargeId(rechargeId);
        order.setPaymentCode((byte) paymentCode);
        order.setUser_name(user_name);
        order.setEmail(email);
        //log.info(String.format("recordOrder：userId:%d,orderSn:%s,paymentCode:%d,rechargeId:%d,amount:%f,num:%d,currency:%s,add_time:%d", userId, orderSn, paymentCode, rechargeId, amount, num, currency, add_time));
        return orderMapper.insert(order);
    }

    /**
     * 获取购买订单号
     */
    private String getOrderSn() {
        long now = System.currentTimeMillis(); //一个13位的时间戳
        String sn = "A"+LibSysUtils.getRandomNum(4) + String.valueOf(now);
        return sn;
    }

    /**
     * 获取付款订单号
     */
    public String getPaymentSn() {
        long now = System.currentTimeMillis(); //一个13位的时间戳
        return String.valueOf(now) + LibSysUtils.getRandomNum(4);
    }

    //收益
    public JSONObject income(int userId) {
        int total_money = pocketInfoMapper.getAnchorMoneyByUserId(userId);
        Double cashRate = LibSysUtils.toDouble(WKCache.get_system_cache("CASH_RATE"));
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("total_money", total_money);
        object.put("withdraw_money", total_money * cashRate);
        return object;
    }

    //用户钱包信息
    public JSONObject getPocketInfo(int userId,double api_version,int index,int count) {
        JSONObject object;
        PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(userId);
        object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("total_diamond", pocketInfo.getTotalDiamond()+pocketInfo.getFreeDiamond());
        object.put("total_money", pocketInfo.getTotalMoney());
        object.put("total_ticket", pocketInfo.getTotalTicket());
        object.put("user_sca_wallet", pocketInfo.getTotalDiamond());
        object.put("pay_diamond", pocketInfo.getTotalDiamond());//付费
        object.put("free_diamond", pocketInfo.getFreeDiamond());//免费

        return object;
    }

    //提现
    @Transactional
    public JSONObject withdraw(int userId, String withDraw, String lang_code) {
        JSONObject object;
        Double draw_money = LibSysUtils.toDouble(withDraw);
        PocketInfo info = pocketInfoMapper.selectByUserid(userId);
        if (info.getTotalMoney() >= draw_money) {
            if (withDrawMapper.findByUserId(userId) > 0) { //只能包含一次提现记录
                object = LibSysUtils.getResultJSON(ResultCode.withdraw_exist, LibProperties.getLanguage(lang_code, "weking.lang.app.withdraw.exist"));
            } else {
                WithDraw record = new WithDraw();
                record.setUserId(userId);
                record.setDrawMoney(LibSysUtils.toDouble(withDraw));
                record.setDrawTime(LibDateUtils.getLibDateTime());
                record.setPaymentSn(getPaymentSn());
                withDrawMapper.insert(record);
                object = LibSysUtils.getResultJSON(ResultCode.success);
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(lang_code, "weking.lang.info.nomoney"));
        }
        return object;
    }

    // 提现记录、
        public JSONObject withdrawList(int userId, int index, int count) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        List<WithDraw> list = withDrawMapper.selectListByUserId(userId, index, count);
        for (WithDraw info : list) {
            jsonObject = new JSONObject();
            jsonObject.put("payment_sn", info.getPaymentSn());
            jsonObject.put("draw_money", info.getDrawMoney());
            jsonObject.put("state", info.getApproveState());
            jsonObject.put("draw_time", info.getDrawTime());
            jsonArray.add(jsonObject);
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", jsonArray);
        return object;
    }

    //充值记录
    public JSONObject payList(int userId, int index, int count) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        List<Order> list = orderMapper.selectPayListByUserId(userId, index, count);
        for (Order info : list) {
            jsonObject = new JSONObject();
            jsonObject.put("order_sn", info.getOrderSn());
            jsonObject.put("amount", info.getAmount());
            jsonObject.put("buy_num", info.getBuyNum());
            jsonObject.put("currency", info.getCurrency());
            jsonObject.put("add_time", info.getAddTime());
            jsonArray.add(jsonObject);
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", jsonArray);
        return object;
    }

    @Transactional
    public boolean deductGameFire(GameFire gameFire, int win, int userId) {

        boolean isDeductOK;// 是否扣、加钱成功

        if (win != 0) { // 等于0不需要更新数据库

            int re = pocketInfoMapper.increaseDiamondByUserId(userId, win);
            isDeductOK = re > 0;

        } else {
            isDeductOK = true;
        }

        if (isDeductOK) { // 更新钱包成功才插入游戏记录
            gameFireMapper.insert(gameFire);//插入游戏记录
        }
        return isDeductOK;
    }

    /**
     * 冻结虚拟币
     */
    @Transactional
    public JSONObject frozen(int userId, int costPrice, int frozenType, String langCode) {
        boolean flag = pocketInfoMapper.frozenDiamondByUserId(costPrice, userId) > 0;
        if (!flag) {
            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(langCode, "weking.lang.info.nomoney"));
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        int frozenId = 0;
        if (costPrice > 0) {
            frozenId = recordFrozenLog(userId, costPrice, frozenType, 2);
        }
        object.put("frozen_id", frozenId);
        return object;
    }

    /**
     * 扣除冻结
     *
     * @param userId   扣费者ID
     * @param otherId  获得相应佣金者
     * @param isDeduct true扣费 false返还
     */
    @Transactional
    public JSONObject deductFrozen(int userId, int otherId, int frozenId, boolean isDeduct) {
        int diamond = getFrozenDiamond(frozenId);
        if (diamond > 0) {
            int state = isDeduct ? 1 : 3;
            int re = frozenLogMapper.updateFrozenState(frozenId, state);
            if (re > 0) {
                if (isDeduct) {
                    //扣除,成功
                    int r = pocketInfoMapper.deductFrozenByUserId(diamond, userId);
                    if (r > 0) {
                        recordConsume(userId, otherId, diamond, diamond, 5, 0);
                        if (otherId > 0) {
                            pocketInfoMapper.increaseTicketByUserid(diamond, otherId);
                        }
                        return LibSysUtils.getResultJSON(ResultCode.success);
                    }
                } else {
                    //返还
                    pocketInfoMapper.backFrozenByUserId(diamond, userId);
                    return LibSysUtils.getResultJSON(ResultCode.success);
                }
            }
        }
        return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds);
    }

    /**
     * 该冻结ID有效钱数(及还处于冻结状态没有扣除或返还)
     */
    public int getFrozenDiamond(int frozenId) {
        Integer diamond = frozenLogMapper.findDiamondById(frozenId);
        if (diamond == null) {
            return 0;
        }
        return diamond;
    }

    /**
     * 获得对方冻结佣金
     *
     * @param userId 佣金获得者
     */
    public void getFrozenCommission(int userId, int otherId, int costPrice, int roomId, int type) {
        if (pocketInfoMapper.deductFrozenByUserId(costPrice, otherId) > 0) {
            pocketInfoMapper.increaseTicketByUserid(costPrice, userId);
            recordConsume(otherId, userId, costPrice, costPrice, type, roomId);
        }
    }

    public void backFrozenDiamond(int userId, int costPrice) {
        pocketInfoMapper.backFrozenByUserId(costPrice, userId);
    }

    public int recordConsume(int userId, int otherId, int costPrice, int ticket, int giftId, int liveId) {
        //插入消费记录表
        long sendTime = LibDateUtils.getLibDateTime();
        ConsumeInfo consumeLogInfo = new ConsumeInfo();
        consumeLogInfo.setSendId(userId);
        consumeLogInfo.setReceiveId(otherId);
        consumeLogInfo.setSendDiamond(costPrice);
        consumeLogInfo.setSendTime(sendTime);
        consumeLogInfo.setGiftId(giftId);
        consumeLogInfo.setLiveRecordId(liveId);
        consumeLogInfo.setReceiveTicket(ticket);
        String ratio = userService.getUserInfoByUserId(userId, "ratio");
        consumeLogInfo.setRatio(new BigDecimal(ratio).setScale(8));
        if(LibSysUtils.toDouble(ratio)>0) {
            double v = costPrice / LibSysUtils.toDouble(ratio);
            consumeLogInfo.setPrice(new BigDecimal(v).setScale(1, BigDecimal.ROUND_DOWN));
        }else {
            consumeLogInfo.setPrice(new BigDecimal(0).setScale(1, BigDecimal.ROUND_DOWN ));
        }
        int re = consumeInfoMapper.insertSelective(consumeLogInfo);
        if (re > 0) {
            re = consumeLogInfo.getId();
        }
        return re;
    }

    /**
     * 用户消费
     *
     * @param userId    消费者Id
     * @param otherId   对方ID
     * @param costPrice 消费金额
     * @param extendId  直播间ID或聊天房间ID(根据消费类型区分)
     */
    @Transactional(rollbackFor = Exception.class)
    public JSONObject consume(int userId, int otherId, int costPrice, int giftId, int extendId, String langCode) {

        Double anchorRate = LibSysUtils.toDouble(WKCache.get_system_cache(C.WKSystemCacheField.guard_anchor_rate),0.5);
        //对方可提取的佣金数
        int ticket = 0;
        switch (giftId) {
            case 0: //弹幕
                break;
            case 1: //付费观看门票
                break;
            case 2: //购买直播权限
                break;
            case 3: //视频聊天花费
                costPrice = LibSysUtils.toInt(WKCache.get_system_cache("video.spend.price"));
                ticket = (int) Math.floor(costPrice * LibSysUtils.toDouble(WKCache.get_system_cache("video.anchor.ticket")));
                break;
            case 4: //修改昵称
                break;
            case 5: //约单
                break;
            case 6: //购买守护
                ticket = (int) Math.floor(costPrice * anchorRate);
                break;
            case 7: //竞猜
                break;
            default: //礼物ID
                ticket = costPrice;
//                // 送礼物分配SCA GOLD
//                if(LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.reward_scagold_switch),false)){
//                    recordSCAGoldLog(userId,otherId,costPrice);
//                }
                break;
        }
        long monthTime = LibDateUtils.getLibDateTime("yyyyMM");
        long dayTime = LibDateUtils.getLibDateTime("yyyyMMdd");
        long weekTime= DateUtils.getMondayOfThisWeek("yyyyMMdd");

        PocketInfo pinfo = pocketInfoMapper.selectByUserid(userId);
        if (pinfo != null && (pinfo.getTotalDiamond()+pinfo.getFreeDiamond()) < costPrice) {
            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(langCode, "weking.lang.app.live_not_sufficient_funds"));
        }

        int re=0;
        //如果免费的emo够用，直接扣免费emo
        if (pinfo.getFreeDiamond() >= costPrice){
            re= pocketInfoMapper.deductFreeDiamondByUserId(costPrice, userId); //扣钱
        }else {
            //先扣掉免费所有emo
            if (pinfo.getFreeDiamond()>0) {
                pocketInfoMapper.deductFreeDiamondByUserId(pinfo.getFreeDiamond(), userId); //扣钱
            }
            //再扣除剩余的部分
            re= pocketInfoMapper.deductDiamondByUserid(costPrice-pinfo.getFreeDiamond(), userId); //扣钱
        }

/*
        int re = pocketInfoMapper.deductDiamondByUserid(costPrice, userId);
*/
        if (re == 0) {
            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds, LibProperties.getLanguage(langCode, "weking.lang.app.live_not_sufficient_funds"));
        }
        String account = WKCache.get_user(userId, "account");
        String otherAccount = WKCache.get_user(otherId, "account");
        if (LibSysUtils.toInt(WKCache.get_user(userId, "role")) != 2) { //内部用户不参加排行
            WKCache.add_consume_rank(monthTime, costPrice, account);
            WKCache.add_consume_rank_day(dayTime, costPrice, account);
            WKCache.add_consume_rank_week(weekTime, costPrice, account);//周榜

        }
        int consumeId = recordConsume(userId, otherId, costPrice, ticket, giftId, extendId);
            if (giftId > 8) {
                // 送礼物分配SCA GOLD
                if (LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.reward_scagold_switch), false)) {
                    recordSCAGoldLog(userId, otherId, costPrice);
                }
            }
            if (ticket > 0 && otherId > 0) {
                int r = pocketInfoMapper.increaseTicketByUserid(ticket, otherId);
                if (r > 0 && LibSysUtils.toInt(WKCache.get_user(otherId, "role")) != 2 && LibSysUtils.toInt(WKCache.get_user(userId, "role")) != 2) {
                    //分别加入 日榜 月榜的缓存
                    WKCache.add_income_rank(monthTime, ticket, otherAccount);
                    WKCache.add_income_rank_day(dayTime, ticket, otherAccount);
                    WKCache.add_income_rank_week(weekTime, ticket, otherAccount);//周榜


                    //个人收入榜 月榜
                    WKCache.add_bio_income_rank(monthTime, otherId, costPrice, account);
                    //日榜
                    WKCache.add__bio_income_rank_day(dayTime, otherId, costPrice, account);
                    //周榜
                    WKCache.add__bio_income_rank_week(weekTime, otherId, costPrice, account);
                }
            }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("consume_id", consumeId);
        return object;
    }

    // 记录sca gold打赏收入
    private void recordSCAGoldLog(int userId, int otherId, int costPrice) {
        JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));
        JSONObject reward_scagold_rate = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.reward_scagold_rate));
        int emo2scagold = coin_proportion.optInt("emo2scagold",0) * costPrice;  // 总sca gold
        // SCA GOLD总池加入
        WKCache.increaseToSCAGoldPool(emo2scagold);

        long date = LibDateUtils.getLibDateTime();
        if (reward_scagold_rate.optDouble("receiver",0) != 0){
            double convNum = emo2scagold * reward_scagold_rate.optDouble("receiver",0);
            ScaGoldLog receiverLog = ScaGoldLog.getScaGoldLog(otherId,new BigDecimal(costPrice),new BigDecimal(convNum),0,date);
            scaGoldLogMapper.insertSelective(receiverLog);
            pocketInfoMapper.updateScaGoldByUserId(otherId, (int) convNum);

            WKCache.addUserSCAGold(otherId,convNum);
        }
        if (reward_scagold_rate.optDouble("sender",0) != 0){
            double convNum = emo2scagold * reward_scagold_rate.optDouble("sender",0);
            ScaGoldLog senderLog = ScaGoldLog.getScaGoldLog(userId,new BigDecimal(costPrice),new BigDecimal(convNum),0,date);
            scaGoldLogMapper.insertSelective(senderLog);
            pocketInfoMapper.updateScaGoldByUserId(userId, (int) convNum);

            WKCache.addUserSCAGold(userId,convNum);
        }

    }

    public int getUserBalance(int userId) {
        if (userId == 0) {
            return 0;
        }
        PocketInfo pinfo = pocketInfoMapper.selectByUserid(userId);
        if (pinfo == null) {
            return 0;
        }
        return pinfo.getTotalDiamond()+pinfo.getFreeDiamond();
    }

    //主播抽成
    @Transactional
    public void anchorSubsidy(int userId, long gameId, double gameAllBet) {
        if (gameAllBet > 0) {
            double subsidy = LibSysUtils.toDouble(WKCache.get_system_cache(C.WKSystemCacheField.s_game_anchor_subsidy));
//            int anchorSubsidy = (int) Math.ceil(gameAllBet * subsidy);
            int anchorSubsidy = new BigDecimal(gameAllBet * subsidy).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();// 四舍五入 取整

            if (anchorSubsidy > 0) {
                int re = gameService.recordSubsidy(userId, anchorSubsidy, gameId);
                if (re > 0) {
                    pocketInfoMapper.increaseTicketByUserid(anchorSubsidy, userId);
                }
            }
        }

    }

    //扣減游戏下注鑽石
    @Transactional
    public JSONObject deductDiamond(int userId, String account, int liveId, int positionId, String position,
                                    int betNum, int my_diamond, int gameType, String lang_code) {
        JSONObject object;
        int re = pocketInfoMapper.deductAllDiamondByUserId(betNum, userId);
        if (re > 0) {
            //插入消费日志表
            recordConsume(userId, 0, betNum, 0, -1, liveId);//玩游戏设置为-1
            int rest_diamond = my_diamond - betNum; //剩下钻石
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
                           Map<String, Integer> winUserIdMoney, Map<String, Integer> betUser, long gameId, int gameType,int liveId) {

        if (winUserIdMoney.size() > 0) {
            pocketInfoMapper.batchIncreaseDiamond(winUserIdMoney);
            //statistics(winUserIdMoney);
        }
        if (betUser.size() > 0) {
            gameService.recordGameBetLog(userId, accountUserIdMap, winUserIdMoney, betUser, gameId, gameType);
        }
        for (Map.Entry<String, Integer> map : winUserIdMoney.entrySet()) {
          /*  UserBill bill = UserBill.getBill(LibSysUtils.toInt(map.getKey()), LibSysUtils.toDouble(map.getValue()), 0, liveId, 6);//存赛车赢的日志记录
            userBillMapper.insert(bill);*/
            //游戏收入存入新的表单中
            UserGain gain = UserGain.getGain(LibSysUtils.toInt(map.getKey()), C.UserGainType.game_pay, LibSysUtils.toInt(map.getValue()), liveId);
            userGainMapper.insertSelective(gain);
        }

        if (commissionList.size() > 0) {
            // 当用户的钱包表中不够扣抽水时，会扣完钱包，钻石为0，
            // 但是commission表中还是记录了抽水值不变，会导致两边账不一致
            commissionMapper.insertByBatch(commissionList);
        }
    }

    private void statistics(Map<String, Integer> winUserIdMoney) {
        // 赢钱排行榜，只记录赢的数据
        Map<String, Integer> statisticsMap = new HashMap<>();
        for (String next : winUserIdMoney.keySet()) {
            Integer integer = winUserIdMoney.get(next);
            if (integer > 0) {
                statisticsMap.put(next, integer);
            }
        }
        if (statisticsMap.size() > 0) {
            statisticsMapper.batchIncreaseWinTotal(statisticsMap);
        }
    }

    private int recordFrozenLog(int userId, int diamond, int frozenType, int state) {
        int frozenId = 0;
        FrozenLog record = new FrozenLog();
        record.setUserId(userId);
        record.setDiamond(diamond);
        record.setFrozenType((byte) frozenType);
        record.setState((byte) state);
        record.setAddTime(LibDateUtils.getLibDateTime());
        int re = frozenLogMapper.insert(record);
        if (re > 0) {
            frozenId = record.getId();
        }
        return frozenId;
    }

    /**
     * 充值日志
     */
    public JSONObject getRechargeLog(int userId, int index, int count) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray jsonArray = new JSONArray();
        List<Order> list = orderMapper.selectRechargeListByUserId(userId, index, count);
        JSONObject jsonObject;
        for (Order info : list) {
            jsonObject = new JSONObject();
            jsonObject.put("order_sn", info.getOrderSn());
            jsonObject.put("buy_num", info.getBuyNum());
            jsonObject.put("amount", info.getAmount());
            jsonObject.put("add_time", info.getAddTime());
            jsonObject.put("state", info.getState());
            jsonObject.put("payment_code", info.getPaymentCode());
            jsonObject.put("currency", info.getCurrency());
            jsonArray.add(jsonObject);
        }
        object.put("list", jsonArray);
        return object;
    }


    public JSONObject billLog(int userId,int index,int count) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        List<Map<String, Object>> list = consumeInfoMapper.getConsumeByUserId(userId,index, count);
                int size = list.size();
                Map<String, String> giftMap=new HashMap<>();
                if (size > 0) {
                    JSONObject temp;
                    for (int i = 0; i < size; i++) {
                        temp = new JSONObject();
                        Map<String, Object> map = list.get(i);
                        if ( map.get("nickname")==null){
                            temp.put("nickname", "平台");
                        }else {
                            temp.put("nickname", LibSysUtils.toString(map.get("nickname")));
                        }
                        temp.put("send_diamond",LibSysUtils.toInt(map.get("send_diamond")));
                        temp.put("add_time",LibSysUtils.toLong(map.get("send_time")));

                        int gift_id = LibSysUtils.toInt(map.get("gift_id"));
                        switch (gift_id) {
                            case -1: //下注
                                temp.put("gift_name","直播遊戲");
                                break;
                            case 0: //弹幕
                                temp.put("gift_name","弹幕");
                                break;
                            case 1: //付费观看门票
                                temp.put("gift_name","付費觀看門票");
                                break;
                            case 2: //购买直播权限
                                temp.put("gift_name","购买直播权限");
                                break;
                            case 3: //视频聊天花费
                                temp.put("gift_name","视频聊天花费");
                                break;
                            case 4: //修改昵称
                                temp.put("gift_name","修改昵称");
                                break;
                            case 5: //约单
                                temp.put("gift_name","约单");
                                break;
                            case 6: //购买守护
                                temp.put("gift_name","购买守护");
                                break;
                            case 7: //竞猜
                                temp.put("gift_name","直播竞猜");
                                break;
                            default: //礼物ID
                                String gift_name = giftMap.get(LibSysUtils.toString(gift_id));
                                if(LibSysUtils.isNullOrEmpty(gift_name)){
                                    GiftInfo giftInfo = giftInfoMapper.selectByPrimaryKey(gift_id);
                                    if (giftInfo!=null){
                                        temp.put("gift_name",giftInfo.getName());
                                        giftMap.put(LibSysUtils.toString(gift_id),giftInfo.getName());
                                    }else {
                                        temp.put("gift_name","礼物");
                                    }
                                }else {
                                    temp.put("gift_name",gift_name);
                                }
                                break;
                        }
                        array.add(temp);
                    }
                }
        object.put("list", array);
        return object;
    }


    public JSONObject buyLog(int userId,int index,int count) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        List<UserGain> userGains = userGainMapper.selectUserGainListByUserId(userId, index, count);
        JSONObject jsonObject;
        for (UserGain info : userGains) {
            jsonObject = new JSONObject();
            jsonObject.put("buy_num", info.getBuyNum());
            jsonObject.put("add_time", info.getAddTime());
            jsonObject.put("type", info.getType());
            array.add(jsonObject);
        }
        object.put("list", array);
        return object;
    }


    /**
     * payssion 支付
     */
    private JSONObject buyByPayssion(int userId, int payment_code, RechargeList rechargeInfo, String lang_code,
                                     String user_name,String email,String payType,String phone) {
        JSONObject object;
        String orderSn = getOrderSn();
        logger.error("buyByPayssion:获取数据开始====");
        int buy_num = rechargeInfo.getBuyNum() + rechargeInfo.getGiveNum();
        int re = recordOrder(userId, orderSn, payment_code, rechargeInfo.getId(), rechargeInfo.getPayMoney(), buy_num, rechargeInfo.getCurrency(),user_name,email);
        if (re > 0) {
            //object = LibSysUtils.getResultJSON(ResultCode.success);
            object = PayssionUtil.getPayssionData(orderSn, rechargeInfo.getPayMoney(), user_name, email,payType,phone,rechargeInfo.getCurrency());
            logger.error("buyByPayssion:获取数据结束===="+object);
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.recharge_buy_error, LibProperties.getLanguage(lang_code, "weking.lang.recharge.buy.error"));
        }

        return object;

    }


}
