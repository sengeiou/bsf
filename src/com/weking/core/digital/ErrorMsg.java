package com.weking.core.digital;

/**
 * Created by Administrator on 2018/5/24.
 */
public class ErrorMsg {
    public final static String M_NAME_NULL = "M_NAME_NULL"; // 用户名不能为空
    public final static String M_NAME_ERROR = "M_NAME_ERROR"; // 用户名不符合规则
    public final static String FROM_NULL = "FROM_NULL"; // 验证码参数错误
    public final static String KAPTCHA_CODE_NULL = "KAPTCHA_CODE_NULL"; // 校验码不能为空
    public final static String KAPTCHA_CODE_ERROR = "KAPTCHA_CODE_ERROR"; // 校验码错误
    public final static String ALREADY_REGISTERED = "ALREADY_REGISTERED"; // 已经注册
    public final static String ACCOUNT_LOCKED = "ACCOUNT_LOCKED"; // 账号被锁定
    public final static String ACCOUNT_NOT_EXIST = "ACCOUNT_NOT_EXIST"; // 账号不存在
    public final static String SMS_CODE_SEND_ERROR = "SMS_CODE_SEND_ERROR"; // 验证码发送失败
    public final static String SMS_CODE_ERR = "SMS_CODE_ERR"; // 验证码错误
    public final static String NEW_SMS_CODE_ERR = "NEW_SMS_CODE_ERR"; // 更换账号时，新验证码错误
    public final static String SMS_CODE_NULL = "SMS_CODE_NULL"; // 验证码不能为空
    public final static String PWD_NULL = "PWD_NULL"; // 密码不能为空
    public final static String OLD_PWD_ERROR = "OLD_PWD_ERROR"; // 原密码错误
    public final static String SEC_PWD_NULL = "SEC_PWD_NULL"; // 资金密码不能为空
    public final static String SEC_PWD_ERROR = "SEC_PWD_ERROR"; // 资金密码错误
    public final static String OLD_SEC_PWD_ERROR = "OLD_SEC_PWD_ERROR"; // 原资金密码错误
    public final static String TWO_PWD_SAME = "TWO_PWD_SAME"; // 登录密码和资金密码不能相同
    public final static String M_NAME_ALREADY_REG = "M_NAME_ALREADY_REG"; // 该账号已注册
    public final static String REGISTRATION_NOT_COMPLETED = "REGISTRATION_NOT_COMPLETED"; // 账号未完成注册
    public final static String INVALID_MNAME_OR_PASSWORD = "INVALID_MNAME_OR_PASSWORD"; // 用户名或密码错误
    public final static String GOOGLE_CODE_NULL = "GOOGLE_CODE_NULL"; // 谷歌验证码不能为空
    public final static String GOOGLE_CODE_ERROR = "GOOGLE_CODE_ERROR"; // 谷歌验证码错误
    public final static String OLD_GOOGLE_CODE_ERROR = "OLD_GOOGLE_CODE_ERROR"; // 原谷歌验证码错误
    public final static String ILLEGAL_TOKEN = "ILLEGAL_TOKEN"; // Token不正确
    public final static String NOT_LOGIN = "NOT_LOGIN"; // 未登录
    public final static String ID_NUM_ALREADY_EXIST = "ID_NUM_ALREADY_EXIST"; // 身份证明证件号重复
    public final static String BASIC_AUTH_NOT_COMPLETED = "BASIC_AUTH_NOT_COMPLETED"; // 请先完成基础认证
    public final static String BASIC_AUTH_ALREADY = "BASIC_AUTH_ALREADY"; // 基础认证已经完成

    public final static String DUPLICATE_M_NAME_MOBILE = "DUPLICATE_M_NAME_MOBILE"; // m_name为手机时，m_back_name重复绑定手机
    public final static String DUPLICATE_M_NAME_EMAIL = "DUPLICATE_M_NAME_EMAIL"; // m_name为邮箱时，m_back_name重复绑定邮箱

    public final static String TMP_M_NAME_CHANGED = "TMP_M_NAME_CHANGED"; // 【绑定手机或用户名】和【改变用户名时】，提交的tmp_m_name与获得验证码的tmp_m_name不一致

    public final static String API_LABEL_ERR = "API_LABEL_ERR"; // API标签不能为空
    public final static String API_COUNT_MAX_3 = "API_COUNT_MAX_3"; // APIKey最多申请3个

    public final static String ILLEGAL_SYMBOL = "ILLEGAL_SYMBOL"; // 交易对不存在
    public final static String STOP_EX = "STOP_EX"; // 暂停服务

    public final static String ILLEGAL_CURRENCY = "ILLEGAL_CURRENCY"; // 虚拟币不存在
    public final static String ILLEGAL_VOLUME = "ILLEGAL_VOLUME"; // 订单数量不正确
    public final static String ILLEGAL_PRICE = "ILLEGAL_PRICE"; // 订单价格不正确
    public final static String ILLEGAL_PRICE_TYPE = "ILLEGAL_PRICE_TYPE"; // 订单价格类型不正确（limit market）
    public final static String ILLEGAL_O_TYPE = "ILLEGAL_O_TYPE"; // 订单类型不正确（buy sell）
    public final static String ILLEGAL_SOURCE = "ILLEGAL_SOURCE"; // 订单来源不正确（api web app）
    public final static String ILLEGAL_TRADE_PAIR = "ILLEGAL_TRADE_PAIR"; // 交易对不存在或未启用
    public final static String LITTLE_THAN_MIN_BUY_VOLUME = "LITTLE_THAN_MIN_BUY_VOLUME"; // 数量低于最低买入数量
    public final static String LITTLE_THAN_MIN_SELL_VOLUME = "LITTLE_THAN_MIN_SELL_VOLUME"; // 数量低于最低卖出数量
    public final static String STOP_BUY_EX = "STOP_BUY_EX"; // 暂停买入
    public final static String STOP_SELL_EX = "STOP_SELL_EX"; // 暂停卖出
    public final static String ILLEGAL_PRICE_PRECISION = "ILLEGAL_PRICE_PRECISION"; // 价格小数位不准确
    public final static String ILLEGAL_VOLUME_PRECISION = "ILLEGAL_VOLUME_PRECISION"; // 数量小数位不准确
    public final static String ILLEGAL_O_ID = "ILLEGAL_O_ID"; // 订单ID错误
    public final static String ORDER_DOES_NOT_EXIST = "ORDER_DOES_NOT_EXIST"; // 订单不存在或订单号错误
    public final static String MARKET_ORDER_CANNOT_BE_CANCELLED = "MARKET_ORDER_CANNOT_BE_CANCELLED"; // 市价单不需要取消
    public final static String ORDER_IS_DONE_OR_CANCELED = "ORDER_IS_DONE_OR_CANCELED"; // 订单已取消或已完成
    public final static String CANNOT_WITHDRAW = "CANNOT_WITHDRAW"; // 当前币种暂停提现

    public final static String ILLEGAL_ADDRESS = "ILLEGAL_ADDRESS"; // 提现地址错误
    public final static String ILLEGAL_ADDRESS_LABEL = "ILLEGAL_ADDRESS_LABEL"; // 提现地址标签错误
    public final static String ILLEGAL_WITHDRAW_AMOUNT = "ILLEGAL_WITHDRAW_AMOUNT"; // 提现数量有误
    public final static String WITHDRAW_AMOUNT_MIN_THAN_MIN = "WITHDRAW_AMOUNT_MIN_THAN_MIN"; // 提现数量低于最小提现额度
    public final static String NOT_AUTH_IDENTITY = "NOT_AUTH_IDENTITY"; // 未实名认证
    public final static String WITH_DRAW_THAN_MAX_V2 = "WITH_DRAW_THAN_MAX_V2"; // 超过24小时V2认证级别最大提现额度
    public final static String WITH_DRAW_THAN_MAX_V1 = "WITH_DRAW_THAN_MAX_V1"; // 超过24小时V1认证级别最大提现额度

    public final static String ILLEGAL_DEPOSIT_AMOUNT = "ILLEGAL_DEPOSIT_AMOUNT"; // 充值数量有误

    public final static String ADDRESS_ALREADY_EXISTS = "ADDRESS_ALREADY_EXISTS"; // 充值地址已经存在
    public final static String POOL_NO_ADDRESS_EXISTS = "POOL_NO_ADDRESS_EXISTS"; // 地址池暂无地址

    public final static String ILLEGAL_DEPOSIT_ADDRESS = "ILLEGAL_DEPOSIT_ADDRESS"; // 充值地址错误
    public final static String ILLEGAL_DEPOSIT_VOLUME = "ILLEGAL_DEPOSIT_VOLUME"; // 充值数量不正确
    public final static String LITTLE_THAN_MIN_DEPOSIT_VOLUME = "LITTLE_THAN_MIN_DEPOSIT_VOLUME"; // 数量低于最小充值数量

    public final static String NO_SUFFICIENT_FUNDS = "NO_SUFFICIENT_FUNDS"; // 余额不足
    public final static String ACCOUNT_NOT_EXISTS = "ACCOUNT_NOT_EXISTS"; // 账户不存在
    public final static String ACCOUNT_EXCEPTION = "ACCOUNT_EXCEPTION"; // 账户异常

    public final static String ILLEGAL_ID = "ILLEGAL_ID"; // ID不存在

    public final static String REDIS_ERROR = "REDIS_ERROR"; // 缓存服务器异常

    public final static String ILLEGAL_TIMESTAMP_FORMAT = "ILLEGAL_TIMESTAMP_FORMAT"; // 时间戳格式错误
    public final static String ILLEGAL_TIMESTAMP = "ILLEGAL_TIMESTAMP"; // 时间戳不正确
    public final static String ILLEGAL_SIGN = "ILLEGAL_SIGN"; // 签名不正确
    public final static String ILLEGAL_SIGN_TYPE = "ILLEGAL_SIGN_TYPE"; // 签名方法不正确
    public final static String ILLEGAL_API_KEY = "ILLEGAL_API_KEY"; // APIKey不正确
    public final static String ILLEGAL_IP = "ILLEGAL_IP"; // IP地址不正确
}
