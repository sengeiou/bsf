package com.weking.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class PayNowCancerTimerTask extends TimerTask {

    private Logger loggerr = LoggerFactory.getLogger(PayNowCancerTimerTask.class);

    @Override
    public void run() {
        try {
            System.out.println("------------PayNowCancerTimerTask Strat-------------");
            BackgroundProvider.payNowCancel();
        } catch (Exception e) {
            loggerr.error(e.getMessage(),e);
            System.out.println();
        }
    }

}
