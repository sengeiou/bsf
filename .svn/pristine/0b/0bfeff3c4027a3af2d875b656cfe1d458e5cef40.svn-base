package com.weking.game;

import com.weking.cache.WKCache;
import com.weking.core.C;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Random;

public class GameUtil {

	public static final int FRIED_GOLDEN=1;//炸金花

	public static final int TEXAS_HOLDEM=2;//德州扑克

	public static final int TAURUS_CARD = 3; //牛牛

	public static final int DOLL_MACHINE = 4; //娃娃机

	public static final int STAR_WARS = 5; //星球大战

	public static final int CRAZY_RACING = 7; //疯狂赛车

	public static final int leftCardType=1; //表示左边位置牌

	public static final int centerCardType=2; //表示中间位置牌

	public static final int rightCardType=3; //表示右边位置牌

	public static final String rightCard="right"; //右边位置

	public static final String centerCard="center"; //中间位置

	public static final String leftCard="left"; //左边位置

	public static final String TEXASHOLDEM_PRE_POKER = "pre_poker"; //显示扎金花一张牌

	public static final String TAURUS_PRE_POKER = "TAURUS_PRE_POKER"; //显示的牛牛三张牌

	public static final int EVERY_DAY_FREE_RECEIVE_NUM = 6; //每天免费领取虚拟币次数

	public static final int RECEIVE_FREE_DIAMONDS = 10; //每次免费领取虚拟币数

	public static final int BET_TIME = 30;  //下注时间（棋牌32）

	public static final int BEGIN_CARD_TIME = 18; //开牌时间+倒计时

	public static final int BET_STATE = 2; //下注状态

	public static final int BEGIN_CARD_STATE = 1; //开牌状态

	public static final int GAME_END = 3; //游戏结束

	public static final String RESULT_DATA_START_GAME = "RESULT_DATA_START_GAME"; // 记录开始游戏数据

	public static final String RESULT_DATA_END_GAME = "RESULT_DATA_END_GAME";  // 记录结束游戏数据

	/**
	 * 随机不重复的数列
	 */
	public static int[] getRandomSequence(int total, int need) {
		int[] sequence = new int[total];
		int[] output = new int[need];
		for (int i = 0; i < total; i++) {
			sequence[i] = i;
		}
		Random random = new Random();
		int end = total - 1;
		for (int i = 0; i < need; i++) {
			int num = random.nextInt(end + 1);
			output[i] = sequence[num];
			sequence[num] = sequence[end];
			end--;
		}
		return output;
	}

	//取得两数随机数
	public static int getRandNum(int a,int b){
		int [] arr = {a,b};
		int index=(int)(Math.random() * arr.length);
		return arr[index];
	}
	//json数组转int[]
	public static int[] getJsonToArray(JSONArray array){
		int size = array.size();
		int[] intArray = new int[size];
		for(int i=0;i<size;i++){
			intArray[i] = LibSysUtils.toInt(array.get(i));
		}
		return intArray;
	}


	/**
	 * 根据等级得到倍数计算领取任务的金币
	 * @param level 用户等级
	 * @param diamond 本来的金币数
	 * @return 计算后的金币数
	 */
	public static int getFreeCoin(int level, int diamond) {

		String s_task_diamond_mult = WKCache.get_system_cache(C.WKSystemCacheField.s_task_diamond_mult);
		if(LibSysUtils.isNullOrEmpty(s_task_diamond_mult)) {
			return diamond;
		}
		JSONArray jsonArray = JSONArray.fromObject(s_task_diamond_mult);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject item = jsonArray.optJSONObject(i);
			int itemLevel = item.optInt("level");
			if(level < itemLevel) {
				int mult = item.optInt("mult");
				return diamond * mult;
			}
		}

		return diamond;
	}

	/**
	 * 根据等级得到倍数
	 * @param level 用户等级
	 * @return 倍数
 	 */
	public static int getFreeCoinMult(int level) {
		String s_task_diamond_mult = WKCache.get_system_cache(C.WKSystemCacheField.s_task_diamond_mult);
		if(LibSysUtils.isNullOrEmpty(s_task_diamond_mult)) {
			return 1;
		}
		JSONArray jsonArray = JSONArray.fromObject(s_task_diamond_mult);
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject item = jsonArray.optJSONObject(i);
			int itemLevel = item.optInt("level");
			if(level < itemLevel) {
				return item.optInt("mult");
			}
		}
		return 1;
	}
}
