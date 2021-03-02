package com.weking.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class PushMessageTimerTask extends TimerTask {

    private Logger loggerr = LoggerFactory.getLogger(PushMessageTimerTask.class);

    @Override
    public void run() {
        try {
            System.out.println("------------BackgroundProvider Strat-------------");
            BackgroundProvider.ServerCheckRoomHeart();
        } catch (Exception e) {
            loggerr.error(e.getMessage(),e);
        }
    }

}
