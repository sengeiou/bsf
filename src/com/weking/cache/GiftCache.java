package com.weking.cache;

import com.weking.core.C;
import com.weking.model.pocket.GiftInfo;
import com.weking.redis.LibRedis;
import com.wekingframework.core.LibSysUtils;

import java.util.*;

/**
 * Created zb Administrator on 2017/6/19.
 * 礼物缓存
 */
public class GiftCache {

    private static final String list_gift = C.projectName + "_gift_list_";          //礼物信息

    private static final String list_gift_id = C.projectName + "_gift_sort_id";     //礼物id列表

    private static final String field_id = "field_id";
    public static final String field_name = "field_name";
    public static final String field_price = "field_price";
    private static final String field_isContinue = "field_isContinue";
    private static final String field_type = "field_type";
    private static final String field_image = "field_image";
    private static final String field_picUrl = "field_picUrl";
    private static final String field_enable = "field_enable";
    private static final String field_download_url = "field_download_url";
    private static final String field_tag = "field_tag";
    public static final String field_count = "field_count";

    /**
     * 加载礼物列表到redis中
     */
    public static void setGiftList(List<GiftInfo> list) {

        if (list != null && list.size() > 0) {
            for (GiftInfo giftInfo : list) {
                HashMap<String, String> map = new HashMap<>();
                map.put(field_id, LibSysUtils.toString(giftInfo.getId()));
                map.put(field_name, giftInfo.getName());
                map.put(field_price, LibSysUtils.toString(giftInfo.getPrice()));
                map.put(field_isContinue, LibSysUtils.toString(giftInfo.getIsContinue()));
                map.put(field_type, LibSysUtils.toString(giftInfo.getType()));
                map.put(field_picUrl, giftInfo.getPicUrl());
                map.put(field_image, giftInfo.getGift_image());
                map.put(field_enable, LibSysUtils.toString(giftInfo.getEnable()));
                map.put(field_download_url, giftInfo.getDownload_url());
                map.put(field_tag, LibSysUtils.toString(giftInfo.getTag()));
                LibRedis.hmset(list_gift + giftInfo.getId(), map);
                LibRedis.zadd(list_gift_id, giftInfo.getSort(), giftInfo.getId() + "");

            }
        }
    }

    /**
     * 删除redis中的礼物
     */
    public static void delGiftCache() {
        Set<String> ids = LibRedis.zrange(list_gift_id, 0, 100);
        for (String id: ids ) {
            LibRedis.del(list_gift + id);
        }
        LibRedis.del(list_gift_id);
    }

    /**
     * 获取礼物列表
     *
     * @return 礼物列表
     */
    public static List<GiftInfo> getGiftList() {

        Set<String> ids = LibRedis.zrange(list_gift_id, 0, 100);
        List<GiftInfo> list = new ArrayList<>();
        for (String id : ids) {
            Map<String, String> map = LibRedis.hgetAll(list_gift + id);
            GiftInfo giftInfo = new GiftInfo();
            giftInfo.setId(LibSysUtils.toInt(id));
            giftInfo.setEnable((short) LibSysUtils.toInt(map.get(field_enable)));
            giftInfo.setGift_image(map.get(field_image));
            giftInfo.setPicUrl(map.get(field_picUrl));
            giftInfo.setIsContinue((short) LibSysUtils.toInt(map.get(field_isContinue)));
            giftInfo.setName(map.get(field_name));
            giftInfo.setDownload_url(map.get(field_download_url));
            giftInfo.setTag(map.get(field_tag));
            giftInfo.setPrice(LibSysUtils.toInt(map.get(field_price)));
            giftInfo.setType((short) LibSysUtils.toInt(map.get(field_type)));
            list.add(giftInfo);
        }
        return list;
    }

    public static String getGift(int id, String field) {
        Map<String, String> map = LibRedis.hgetAll(list_gift + id);
        return map.get(field);
    }

}
