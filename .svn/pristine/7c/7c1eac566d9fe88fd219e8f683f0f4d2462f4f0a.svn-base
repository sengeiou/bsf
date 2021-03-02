package com.weking.core;

/**
 * Created by zhb on 2017/9/1.
 * wk推拉流util
 */
public class WKPushUtil {

    // 腾讯
    private static int PUSH_TYPE_TX = 0;
    // 网宿
    public static int PUSH_TYPE_WS = 1;
    // 阿里
    public static int PUSH_TYPE_AIL = 2;


    private static String buildPlayKey(String streamId, String clientID) {
        String date = String.valueOf(System.currentTimeMillis());
        return String.format("%s-%s-%s", clientID, date, streamId);
    }

}
