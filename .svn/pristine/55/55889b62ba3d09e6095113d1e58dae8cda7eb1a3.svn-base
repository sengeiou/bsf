package com.weking.game.doll;

import com.weking.cache.WKCache;
import com.weking.core.C;
import com.wekingframework.core.LibSysUtils;

/**
 * Created by zb on 2017/6/21.
 * 娃娃机游戏
 */
public class DollMachine {

    /**
     * 控制区间的平衡
     * <p>设置负数则系统有利，设置正数则用户有利</>
     */
    private static final int BALANCE_DOLL_MACHINE = 0;
    private static final int BALANCE_STAR_WARS = 0;

    /**
     * 根据本钱与玩具价格计算是否命中
     *
     * @param capital 本钱
     * @param price   玩具价格
     */
    private static boolean getResultInner(float capital, float price, int balance) {
        //  int 范围 四个字节，-2147483648~2147483647

        int m = 10000;
        float n = (capital * m / price) + balance;
//        System.out.println(n);
        // 产生区间[0,m) 内的随机数
        int random = (int) (Math.random() * m);
        // 若随机数落入[0,n)，则命中
        return random < n;
    }


    /**
     * 娃娃机
     *
     * @param capital capital
     * @param price   price
     * @return boolean
     */
    public static boolean getDollMachineResult(float capital, float price) {
        int balance = LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.GAME_DOLL_BALANCE));

        return getResultInner(capital, price, balance);
    }


    /**
     * 星球大战（打飞机）
     *
     * @param capital capital
     * @param price   price
     * @return boolean
     */
    public static boolean getStarWarsResult(float capital, float price) {
        int balance = LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.GAME_STAR_WARS_BALANCE));

        return getResultInner(capital, price, balance);
    }


    public static void main(String[] args) {
        String s = "ﻬ建จุ๊บ哥₂₀₁₇ﻬ";
//        String ss = s.replace("ﻩ","");
        String ss = s.replace("ﻬ","");
        System.out.println(ss);
//        int my = 0;
//        int one = 10;
//        int a = 100;
//        for (int i = 0; i < 10000000; i++) {
//            my -= one;
//            boolean get = getResultInner(one, a, -2);
//            if (get) {
//                my += a;
//            }
//            System.out.println("我的钱：" + my);
//        }

    }
}
