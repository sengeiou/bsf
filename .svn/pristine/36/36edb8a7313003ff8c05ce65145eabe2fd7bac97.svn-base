package com.weking.service.user;

import com.weking.core.AliyunSMS;
import com.weking.core.CheckUtil;
import com.weking.core.ResultCode;
import com.weking.mapper.log.SmsLogMapper;
import com.weking.model.log.SmsLog;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/2/15.
 */
@Service("SmsService")
public class SmsService extends LibServiceBase {
    private static Logger loger = Logger.getLogger(SmsService.class);

    @Resource
    private SmsLogMapper smsLogMapper;

    /**
     * 发送验证码
     *
     * @param phone     手机号码
     * @param area_code 手机区号
     * @param type      验证码类型 (1登录2绑定)
     * @param lang_code 语言
     * @return 发送结果
     */
    public JSONObject sendCaptcha(String phone, String area_code, int type, String project_name, String lang_code) {
        JSONObject object = new JSONObject();
        if (!CheckUtil.checkPhone(phone)) {
            object.put("code", ResultCode.account_phone_error);
            object.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.account.phone.error"));
        } else {
           /* if (!ChuanglanSMS.checkPhone(area_code + phone)) {
                object.put("code", ResultCode.account_phone_error);
                object.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.account.phone.error"));
            } else {*/
                String mobile = area_code + phone;
                object = isSendCaptcha(mobile, type, lang_code);
                if (object.getInt("code") == 0) {
                    String captcha = getCaptcha();
                    try {
                        //创蓝
                        //String content = getSmsContent(type, captcha, project_name, lang_code);
                        //boolean flag = ChuanglanSMS.SendMsg(area_code, phone, content);
                        String templateCode = getSmsTemplateCode(area_code,type, lang_code);
                        boolean flag = AliyunSMS.SendMsg(area_code, phone, templateCode, captcha);
                        if (flag) {
                            object.put("code", ResultCode.success);
                            recordSmsLog(mobile, captcha, type);
                        } else {
                            object = LibSysUtils.getResultJSON(ResultCode.send_error, LibProperties.getLanguage(lang_code, "weking.lang.app.send.error"));
                        }
                    } catch (Exception e) {
                        loger.error("发送验证码失败：" + e.getMessage());
                        object = LibSysUtils.getResultJSON(ResultCode.send_error, LibProperties.getLanguage(lang_code, "weking.lang.app.send.error"));
                    }
                //}
            }
        }
        return object;
    }

    //得到短信内容
    private String getSmsContent(int type, String captchat, String project_name, String lang_code) {
        String content = '【' + LibProperties.getLanguage(lang_code, "weking.lang." + project_name + "app.name") + '】';
        switch (type) {
            case 1: //登录验证码
                content += LibProperties.getLanguage(lang_code, "weking.lang.app.login.captcha");
                break;
            case 2: //绑定验证码
                content += LibProperties.getLanguage(lang_code, "weking.lang.app.bing.captcha");
                break;
            case 4: //修改密码验证码
                content += LibProperties.getLanguage(lang_code, "weking.lang.app.password.captcha");
                break;
            case 7: //注册验证码
                content += LibProperties.getLanguage(lang_code, "weking.lang.app.register.captcha");
                break;
            default:
                content = "";
                break;
        }
        return content.replace("%captcha%", captchat);
    }

    //得到短信内容
    private String getSmsTemplateCode(String area_code,int type, String lang_code) {
        String result = "SMS_108430033";
        if(!"86".equals(area_code)) {//国际
            if ("zh_CN".equals(lang_code)) {
                switch (type) {
                    case 1: //登录验证码
                        result = "SMS_137415825";
                        break;
                    case 2: //绑定验证码
                        result = "SMS_137415826";
                        break;
                    case 4: //修改密码验证码
                        result = "SMS_137410837";
                        break;
                    case 7: //注册验证码
                        result = "SMS_137410838";
                        break;
                    default:
                        result = "";
                        break;
                }
            }
            if ("zh_TW".equals(lang_code)) {
                switch (type) {
                    case 1: //登录验证码
                        result = "SMS_137420779";
                        break;
                    case 2: //绑定验证码
                        result = "SMS_137425819";
                        break;
                    case 4: //修改密码验证码
                        result = "SMS_137425822";
                        break;
                    case 7: //注册验证码
                        result = "SMS_137415784";
                        break;
                    default:
                        result = "";
                        break;
                }
            }
            if ("en_US".equals(lang_code)) {
                switch (type) {
                    case 1: //登录验证码
                        result = "SMS_137415791";
                        break;
                    case 2: //绑定验证码
                        result = "SMS_137425833";
                        break;
                    case 4: //修改密码验证码
                        result = "SMS_137415793";
                        break;
                    case 7: //注册验证码
                        result = "SMS_137410859";
                        break;
                    default:
                        result = "";
                        break;
                }
            }
            if ("ms".equals(lang_code)) {
                switch (type) {
                    case 1: //登录验证码
                        result = "SMS_137415791";
                        break;
                    case 2: //绑定验证码
                        result = "SMS_137425833";
                        break;
                    case 4: //修改密码验证码
                        result = "SMS_137415793";
                        break;
                    case 7: //注册验证码
                        result = "SMS_137410859";
                        break;
                    default:
                        result = "";
                        break;
                }
            }
        }else {//国内
            switch (type) {
                case 1: //登录验证码
                    result = "SMS_108430033";
                    break;
                case 2: //绑定验证码
                    result = "SMS_137415807";
                    break;
                case 4: //修改密码验证码
                    result = "SMS_108430030";
                    break;
                case 7: //注册验证码
                    result = "SMS_108430031";
                    break;
                default:
                    result = "";
                    break;
            }
        }
        return result;
    }

    //账号是否还可以发送验证码
    private JSONObject isSendCaptcha(String sendAccount, int type, String lang_code) {
        JSONObject object = new JSONObject();
        SmsLog record = new SmsLog();
        record.setSendAccount(sendAccount);
        record.setType((byte) type);
        SmsLog smsInfo = smsLogMapper.findByParam(record);
        object.put("code", ResultCode.success);
        if (smsInfo != null) {
            long intervalTime = LibDateUtils.getDateTimeTick(smsInfo.getSendTime(), LibDateUtils.getLibDateTime());
            if (intervalTime <= 60000) {  //相隔一分钟才能再次发送
                object.put("code", ResultCode.operation_again_later);
                object.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.app.again.later"));
            } else {
                List<SmsLog> smsLog = smsLogMapper.selectByToDay(sendAccount, LibDateUtils.getLibDateTime("yyyyMMdd000000"));
                int size = smsLog.size();
                if (size >= 10) {
                    object.put("code", ResultCode.send_captcha_over);
                    object.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.send.captcha.over"));
                } else if (size >= 5) {
                    long hoursAgoTime = getOneHoursAgoTime();
                    int n = 0;
                    for (SmsLog info : smsLog) {
                        if (info.getSendTime() >= hoursAgoTime) {
                            n++;
                        }
                    }
                    if (n >= 5) {
                        object.put("code", ResultCode.send_captcha_over);
                        object.put("msg", LibProperties.getLanguage(lang_code, "weking.lang.send.captcha.over"));
                    }
                }
            }
        }
        return object;
    }

    //验证验证码
    public JSONObject checkCaptchat(String send_account, String captcha, int type, String lang_code) {
        JSONObject object;
        if (!LibSysUtils.isNullOrEmpty(captcha)) {
            SmsLog smsLog = new SmsLog();
            smsLog.setCaptcha(captcha);
            smsLog.setSendAccount(send_account);
            smsLog.setType((byte) type);
            SmsLog smsInfo = smsLogMapper.findByParam(smsLog);
            if (smsInfo == null) {
                object = LibSysUtils.getResultJSON(ResultCode.captcha_error, LibProperties.getLanguage(lang_code, "weking.lang.app.vcode.error"));
            } else {
//                long intervalTime =  LibDateUtils.getDateTimeTick(smsInfo.getSendTime(),LibDateUtils.getLibDateTime());
//                if(intervalTime > 600000){  //验证码有效期为10分钟
//                    object = LibSysUtils.getResultJSON(ResultCode.captcha_expired,LibProperties.getLanguage(lang_code,"weking.lang.app.captcha.expired"));
//                }else{
                object = new JSONObject();
                object.put("code", ResultCode.success);
//                }
            }
        } else {
            object = LibSysUtils.getResultJSON(ResultCode.captcha_error, LibProperties.getLanguage(lang_code, "weking.lang.app.vcode.error"));
        }
        return object;
    }

    //得到验证码
    public String getCaptcha() {
        return LibSysUtils.getRandomNum(6);
    }

    public int recordSmsLog(String sendAccount, String captcha, int type) {
        SmsLog record = new SmsLog();
        record.setSendAccount(sendAccount);
        record.setCaptcha(captcha);
        record.setSendTime(LibDateUtils.getLibDateTime());
        record.setType((byte) type);
        return smsLogMapper.insertSelective(record);
    }

    //获取过去一小时
    private long getOneHoursAgoTime() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR, -1);
        String oneHoursAgoTime = new SimpleDateFormat("yyyyMMddHHmmss").format(now.getTime());//获取到完整的时间
        return LibSysUtils.toLong(oneHoursAgoTime);
    }

    public static void main(String[] a) {

    }

}
