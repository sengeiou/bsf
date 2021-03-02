package com.weking.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Xujm
 */
public class ResourceUtil {


    public static Map<String,String> PlatformLangMap = new HashMap<>();

    public static Map<Integer,String> InviteClassMap = new HashMap<>();

    public static Map<Integer,Long> NotConfirmAppointmentMap = new ConcurrentHashMap<>();

    public static Map<Integer,Long> LiveGuardMap = new ConcurrentHashMap<>();

    public static Map<String,Integer> userStateMap = new HashMap<>();

}
