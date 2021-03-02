package com.weking.cache;

import com.weking.core.C;
import com.weking.game.GameUtil;
import com.weking.model.game.GameNpc;
import com.weking.redis.LibRedis;
import com.wekingframework.core.LibSysUtils;

import java.util.*;

/**
 * Created by Administrator on 2016/12/28.
 * GameCache
 */
public class GameCache {

    private static String _room_game_tag = C.projectName + "_weking_room_game_";
    private static String _room_game_user_tag = C.projectName + "_weking_game_user_";

    private static final String list_npc = C.projectName + "_npc_list_";          //NPC信息
    private static final String list_npc_id = C.projectName + "_npc_list_id";     //NPC id列表
    private static final String npc_field_id = "npc_field_id";
    public static final String npc_field_name = "npc_field_name";
    public static final String npc_field_radix = "npc_field_radix";
    private static final String game_time = "game_time";

    private static final String GAME_WIN_ID_LIST = C.projectName + "GameWinIdList";     //记录牌局输赢


    public static final String game_state = "game_state";
    public static final String game_id = "game_id";
    private static final String switch_type = "switch_type";


    //竞猜
    private static String _room_guessing_tag = C.projectName + "_weking_room_guessing_";

    public static final String guessing_state = "guessing_state";
    public static final String guessing_id = "game_id";
    public static final String guessing_diff= "guessing_diff";
    public static final String guessing_time= "guessing_time";
    public static final String guessing_one_num= "guessing_one_num";
    public static final String guessing_two_num= "guessing_two_num";
    public static final String guessing_title= "guessing_title";
    public static final String guessing_option_one= "guessing_option_one";
    public static final String guessing_option_two= "guessing_option_two";
    public static final String guessing_price= "guessing_price";
    public static final String guessing_right_option= "guessing_right_option";



    /**
     * 设置NPC 列表到缓存
     *
     * @param list list
     */
    public static void setNpcToCache(List<GameNpc> list) {

        String[] npcIdArray = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            GameNpc npc = list.get(i);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(npc_field_id, LibSysUtils.toString(npc.getId()));
            map.put(npc_field_name, npc.getNpc_name());
            map.put(npc_field_radix, LibSysUtils.toString(npc.getRadix()));

            LibRedis.hmset(list_npc + npc.getId(), map);

            npcIdArray[i] = LibSysUtils.toString(npc.getId());
        }
        if(npcIdArray.length > 0){
            LibRedis.sadd(list_npc_id, npcIdArray);
        }
    }
    /**
     * 删除redis中的Npc
     */
    public static void delNpcCache() {
        Set<String> ids = LibRedis.smembers(list_npc_id);
        if(ids != null){
            for (String id: ids ) {
                LibRedis.del(list_npc + id);
            }
        }
        LibRedis.del(list_npc_id);
    }
    /**
     * 获取npc列表
     *
     * @return npc列表
     */
    public static List<GameNpc> getNpcList() {

        Set<String> ids = LibRedis.smembers(list_npc_id);
        List<GameNpc> list = new ArrayList<GameNpc>();
        for (String id : ids) {
            Map<String, String> map = LibRedis.hgetAll(list_npc + id);
            GameNpc gameNpc = new GameNpc();
            gameNpc.setId(LibSysUtils.toInt(id));
            gameNpc.setNpc_name(map.get(npc_field_name));
            gameNpc.setRadix(Float.valueOf(map.get(npc_field_radix)));

            list.add(gameNpc);
        }
        return list;
    }
    public static String getNpc(int id, String field) {
        Map<String, String> map = LibRedis.hgetAll(list_npc + id);
        return map.get(field);
    }
    /**
     * 设置房间类型
     */
    public static void set_game_type(int live_id, int game_type) {
        String slive_id = LibSysUtils.toString(live_id);

        LibRedis.hset(_room_game_tag + slive_id, "game_type", LibSysUtils.toString(game_type));

    }

    /**
     * 获得房间类型
     */
    public static int get_game_type(int live_id) {
        String slive_id = LibSysUtils.toString(live_id);
        int result = 0;
        if (LibRedis.hexists(_room_game_tag + slive_id, "game_type")) {
            result = LibSysUtils.toInt(LibRedis.hget(_room_game_tag + slive_id, "game_type"));
        }
        return result;
    }

    /**
     * 设置游戏状态
     */
    public static void set_game_state(int live_id, int state) {
        String slive_id = LibSysUtils.toString(live_id);
        LibRedis.hset(_room_game_tag + slive_id, GameCache.game_state, LibSysUtils.toString(state));
    }

    /**
     * 获得游戏状态
     */
    public static int get_game_state(int live_id) {
        String slive_id = LibSysUtils.toString(live_id);
        int result = 0;
        if (LibRedis.hexists(_room_game_tag + slive_id, GameCache.game_state)) {
            result = LibSysUtils.toInt(LibRedis.hget(_room_game_tag + slive_id, GameCache.game_state));
        }
        return result;
    }

    /**
     * 用户下注金额
     */
    public static int get_game_user(int live_id, String account, String position) {
        String slive_id = LibSysUtils.toString(live_id);
        int result = 0;
        if (LibRedis.hexists(_room_game_user_tag + position + "_" + slive_id, account)) { //记录第一次下注数量
            result = LibSysUtils.toInt(LibRedis.hget(_room_game_user_tag + position + "_" + slive_id, account));
        }
        return result;
    }

    /**
     * 增加游戏人数
     */
    public static void add_game_user(int live_id, String account, int num, String position) {
        String slive_id = LibSysUtils.toString(live_id);

        if (!LibRedis.hexists(_room_game_user_tag + position + "_" + slive_id, account)) { //记录第一次下注数量
            LibRedis.hset(_room_game_user_tag + position + "_" + slive_id, account, LibSysUtils.toString(num));
        } else { //之后下注增加
            LibRedis.hincrBy(_room_game_user_tag + position + "_" + slive_id, account, num);
        }
        //记录该位置下注总数
        if (!LibRedis.hexists(_room_game_tag + slive_id, position)) {
            LibRedis.hset(_room_game_tag + slive_id, position, LibSysUtils.toString(num));
        } else {
            LibRedis.hincrBy(_room_game_tag + slive_id, position, num);
        }
    }

    /**
     * 获得房间游戏牌局ID
     */
    public static int get_game_id(int live_id) {
        String slive_id = LibSysUtils.toString(live_id);
        int result = 0;

        if (LibRedis.hexists(_room_game_tag + slive_id, GameCache.game_id)) {
            result = LibSysUtils.toInt(LibRedis.hget(_room_game_tag + slive_id, GameCache.game_id));
        }

        return result;
    }

    /**
     * 获得游戏位置下注总金额
     */
    public static int get_position_bet(int live_id, String position) {
        String slive_id = LibSysUtils.toString(live_id);
        int result = 0;

        if (LibRedis.hexists(_room_game_tag + slive_id, position)) {
            result = LibSysUtils.toInt(LibRedis.hget(_room_game_tag + slive_id, position));
        }

        return result;
    }

    //清除游戏房间
    public static void del_all_game_info(int liveId) {
        delUserBet(liveId);
        LibRedis.del(_room_game_tag + liveId);
    }

    public static void delUserBet(int liveId) {
        LibRedis.del(_room_game_user_tag + "left_" + liveId);
        LibRedis.del(_room_game_user_tag + "center_" + liveId);
        LibRedis.del(_room_game_user_tag + "right_" + liveId);
        LibRedis.hset(_room_game_tag + liveId, GameUtil.leftCard, LibSysUtils.toString(0));
        LibRedis.hset(_room_game_tag + liveId, GameUtil.centerCard, LibSysUtils.toString(0));
        LibRedis.hset(_room_game_tag + liveId, GameUtil.rightCard, LibSysUtils.toString(0));
    }

    /**
     * 游戏时间
     */
    public static void set_game_time(int live_id, long game_time) {
        String slive_id = LibSysUtils.toString(live_id);
        LibRedis.hset(_room_game_tag + slive_id, GameCache.game_time, LibSysUtils.toString(game_time));
    }
    /**
     * 游戏时间
     */
    public static long get_game_time(int live_id) {
        String slive_id = LibSysUtils.toString(live_id);
        long result = 0;

        if (LibRedis.hexists(_room_game_tag + slive_id, GameCache.game_time)) {
            result = LibSysUtils.toLong(LibRedis.hget(_room_game_tag + slive_id, GameCache.game_time));
        }

        return result;
    }
    /**
     * 设置房间游戏牌局ID
     */
    public static void set_game_id(int live_id, long gameId) {
        LibRedis.hset(_room_game_tag + live_id, GameCache.game_id, LibSysUtils.toString(gameId));
    }
    /**
     * 设置游戏数据
     */
    public static void set_game_data(int live_id, String key, String data) {
        String slive_id = LibSysUtils.toString(live_id);

        LibRedis.hset(_room_game_tag + slive_id, key, data);
    }

    /**
     * 获得游戏数据
     */
    public static String get_game_data(int live_id, String key) {
        String slive_id = LibSysUtils.toString(live_id);
        String result = null;

        if (LibRedis.hexists(_room_game_tag + slive_id, key)) {
            result = LibRedis.hget(_room_game_tag + slive_id, key);
        }

        return result;
    }

    /**
     * 记录牌局输赢
     *
     * @param liveId liveId
     * @param winId  winId
     */
    public static void setWinId(int liveId, int winId) {
        LibRedis.lpush(GAME_WIN_ID_LIST + liveId, LibSysUtils.toString(winId));
    }

    /**
     * 获取游戏人员
     */
    public static Map<String, String> get_game_users(int live_id, String position) {
        Map<String, String> result;
        result = LibRedis.hgetAll(_room_game_user_tag + position + "_" + LibSysUtils.toString(live_id));//房间人员列表
        return result;
    }

    /**
     * 下一句的游戏类型
     *
     * @param live_id
     * @return
     */
    public static int get_switch_type(int live_id) {
        String gameType = LibRedis.hget(_room_game_tag + live_id, GameCache.switch_type);

        if (LibSysUtils.isNullOrEmpty(gameType)) {
            return -1;
        } else {
            return LibSysUtils.toInt(gameType);
        }
    }

    /**
     * 设置房间类型
     */
    public static void set_switch_type(int live_id, int switch_type) {
        LibRedis.hset(_room_game_tag + live_id, GameCache.switch_type, LibSysUtils.toString(switch_type));
    }


    /**
     * 设置竞猜状态  2是竞猜中 3是已结束
     */
    public static void set_guessing_state(int user_id, int state) {
        String suser_id = LibSysUtils.toString(user_id);
        LibRedis.hset(_room_guessing_tag + suser_id, GameCache.guessing_state, LibSysUtils.toString(state));
    }

    /**
     * 获得竞猜状态
     */
    public static int get_guessing_state(int user_id) {
        String suser_id = LibSysUtils.toString(user_id);
        int result = 0;
        if (LibRedis.hexists(_room_guessing_tag + suser_id, GameCache.guessing_state)) {
            result = LibSysUtils.toInt(LibRedis.hget(_room_guessing_tag + suser_id, GameCache.guessing_state));
        }
        return result;
    }


    //清除 竞猜
    public static void del_guessing_info(int user_id) {
        LibRedis.del(_room_guessing_tag + user_id);
    }

    /**
     * 竞猜时间
     */
    public static void set_guessing_time(int user_id, long guessing_time) {
        String suser_id = LibSysUtils.toString(user_id);
        LibRedis.hset(_room_guessing_tag + suser_id, GameCache.guessing_time, LibSysUtils.toString(guessing_time));
    }
    /**
     * 竞猜时间
     */
    public static long get_guessing_time(int user_id) {
        String suser_id = LibSysUtils.toString(user_id);
        long result = 0;
        if (LibRedis.hexists(_room_guessing_tag + suser_id, GameCache.guessing_time)) {
            result = LibSysUtils.toLong(LibRedis.hget(_room_guessing_tag + suser_id, GameCache.guessing_time));
        }

        return result;
    }
    /**
     * 设置竞猜ID
     */
    public static void set_guessing_id(int user_id, int guessingId) {
        LibRedis.hset(_room_guessing_tag + user_id, GameCache.guessing_id, LibSysUtils.toString(guessingId));
    }

    public static int get_guessing_id(int user_id) {
        String suser_id = LibSysUtils.toString(user_id);
        int result = 0;
        if (LibRedis.hexists(_room_guessing_tag + suser_id, GameCache.guessing_id)) {
            result = LibSysUtils.toInt(LibRedis.hget(_room_guessing_tag + suser_id, GameCache.guessing_id));
        }

        return result;
    }

    /**
     * 设置竞猜有效时长
     */
    public static void set_guessing_diff(int user_id, int diff) {
        LibRedis.hset(_room_guessing_tag + user_id, GameCache.guessing_diff, LibSysUtils.toString(diff));
    }

    public static int get_guessing_diff(int user_id) {
        String suser_id = LibSysUtils.toString(user_id);
        int result = 0;
        if (LibRedis.hexists(_room_guessing_tag + suser_id, GameCache.guessing_diff)) {
            result = LibSysUtils.toInt(LibRedis.hget(_room_guessing_tag + suser_id, GameCache.guessing_diff));
        }

        return result;
    }

    /**
     * 设置竞猜 选项 数
     */
    public static void set_guessing_num(int user_id, int num,String key) {
        LibRedis.hset(_room_guessing_tag + user_id, key, LibSysUtils.toString(num));
    }

    public static int get_guessing_num(int user_id,String key) {
        String suser_id = LibSysUtils.toString(user_id);
        int result = 0;
        if (LibRedis.hexists(_room_guessing_tag + suser_id, key)) {
            result = LibSysUtils.toInt(LibRedis.hget(_room_guessing_tag + suser_id, key));
        }
        return result;
    }

    //增加竞猜数
    public static void add_guessing_num(int user_id,String key) {
        LibRedis.hincrBy(_room_guessing_tag + user_id, key, 1);
    }


    /**
     * 设置竞猜数据
     */
    public static void set_guessing_data(int user_id, String key, String data) {
        String suser_id = LibSysUtils.toString(user_id);

        LibRedis.hset(_room_guessing_tag + suser_id, key, data);
    }

    /**
     * 获得竞猜数据
     */
    public static String get_guessing_data(int user_id, String key) {
        String suser_id = LibSysUtils.toString(user_id);
        String result = null;

        if (LibRedis.hexists(_room_guessing_tag + suser_id, key)) {
            result = LibRedis.hget(_room_guessing_tag + suser_id, key);
        }

        return result;
    }


}

