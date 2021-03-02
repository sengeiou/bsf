package com.weking.core;

import com.weking.cache.GameCache;
import com.weking.cache.GiftCache;
import com.weking.core.google.FCM;
import com.weking.core.sensitive.WordFilter;
import com.weking.mapper.game.GameNpcMapper;
import com.weking.mapper.keyword.KeyWordMapper;
import com.weking.mapper.lang.PlatformLangMapper;
import com.weking.mapper.pocket.GiftInfoMapper;
import com.weking.model.lang.PlatformLang;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

public class TomcatListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        System.out.println("tomcat stop....................");
        if (LibSysUtils.toBoolean(LibProperties.getConfig("weking.cofing.im")))
            WkImClient.disConnect();

//        Enumeration<Driver> drivers = DriverManager.getDrivers();
//        Driver driver;
//        while (drivers.hasMoreElements()) {
//            try {
//                driver = drivers.nextElement();
//                DriverManager.deregisterDriver(driver);
//            } catch (SQLException ex) {
//                logger.error(ex.getMessage(),ex);
//            }
//        }
//        try {
//            AbandonedConnectionCleanupThread.shutdown();
//            FastThreadLocal.removeAll();
//            FastThreadLocal.destroy();
//            InternalThreadLocalMap.remove();
//            InternalThreadLocalMap.destroy();
//        } catch (InterruptedException e) {
//            logger.error(e.getMessage(),e);
//        }

    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
        boolean flag = Boolean.parseBoolean(LibProperties.getConfig("weking.config.ifexec.flag"));
        if (flag) { //判断是否执行定时器操作， 控制负载均衡只有一台服务器在执行定时器
            try {
                new TimerManager(); //这里面就是这个监听器要做的事情
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 礼物初始化 （多服务器的时候会初始化多次）
//        ApplicationContext ctx = new ClassPathXmlApplicationContext("config/spring-common.xml");
        GiftInfoMapper giftInfoMapper = (GiftInfoMapper) SpringContextUtil.getBean("giftInfoMapper");
        GiftCache.setGiftList(giftInfoMapper.selectAllGift());

        initPlatformLang();

        // 星球大战游戏初始化数据 （多服务器的时候会初始化多次）
        GameNpcMapper gameNpcMapper = (GameNpcMapper) SpringContextUtil.getBean("gameNpcMapper");
        GameCache.delNpcCache();
        GameCache.setNpcToCache(gameNpcMapper.selectAllNpc());

        FCM.init();
        if (LibSysUtils.toBoolean(LibProperties.getConfig("weking.cofing.im")))
            WkImClient.connect();

        // 敏感字初始化 （多服务器的时候会初始化多次）
        KeyWordMapper keyWordMapper = (KeyWordMapper) SpringContextUtil.getBean("keyWordMapper");
        WordFilter.init(keyWordMapper.selectKeyWordList());

    }

    /**
     * 初始化语言资源内容
     */
    private void initPlatformLang(){
        PlatformLangMapper platformLangMapper = (PlatformLangMapper) SpringContextUtil.getBean("platformLangMapper");
        List<PlatformLang> list = platformLangMapper.selectAllPlatformLang();
        for (PlatformLang info:list){
            ResourceUtil.PlatformLangMap.put(String.format("%s_%s",info.getLangKey(),info.getLangCode()),info.getLangContext());
        }
    }




}
