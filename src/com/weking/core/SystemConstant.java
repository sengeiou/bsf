package com.weking.core;

import com.weking.cache.WKCache;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;

public interface SystemConstant {

    boolean enable_robot = LibSysUtils.toBoolean(WKCache.get_system_cache("weking.config.robot"));

    int effect_level = LibSysUtils.toInt(WKCache.get_system_cache("EFFECT_LEVEL"));

    JSONObject coin_proportion = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.coin_proportion));

    JSONObject reward_scagold_rate = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.reward_scagold_rate));

    boolean gift_box_push = LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.live_gift_box_push_config), false);

    double api_version = LibSysUtils.toDouble(WKCache.get_system_cache(C.WKSystemCacheField.s_api_version));

    boolean post_adv_switch = LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.s_post_adv_switch), false);

    int frequency = LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.post_ad_insert_frequency), 10);

    int msg_level = LibSysUtils.toInt(WKCache.get_system_cache("weking.config.msg.level"));
}
