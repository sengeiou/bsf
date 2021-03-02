package com.weking.core;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhb on 2017/5/17.
 * Timer处理延迟任务
 */
public class TimerUtil {



    private static Timer timer;

    static {
        timer = new Timer("timerGlobalMsgDelay");
    }

    /**
     * 发送全服消息延迟任务
     *
     * @param delay 延迟时长
     * @param msg   消息
     */
    public static void sendGlobalMsgDelay(String msg, int delay) {
        timer.schedule(new TimerTask() {
            public void run() {
                WkImClient.sendGlobalMsg(msg);
            }
        }, delay * 1000);
    }

    public static void sendRoomMsgDelay(String live_stream_id , String msg, int delay) {

        timer.schedule(new TimerTask() {
            public void run() {
                WkImClient.sendRoomMsg(live_stream_id, msg,1);
            }
        }, delay * 1000);
    }



}
