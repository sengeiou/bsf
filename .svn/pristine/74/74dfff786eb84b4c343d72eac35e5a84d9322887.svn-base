package com.weking.service.game;

import com.weking.cache.*;
import com.weking.core.*;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.game.GameUtil;
import com.weking.game.doll.DollMachine;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.account.UserBillMapper;
import com.weking.mapper.game.*;
import com.weking.mapper.log.LiveLogInfoMapper;
import com.weking.mapper.pocket.PocketInfoMapper;
import com.weking.mapper.pocket.UserGainMapper;
import com.weking.model.account.AccountInfo;
import com.weking.model.commission.Commission;
import com.weking.model.game.*;
import com.weking.model.pocket.GiftInfo;
import com.weking.model.pocket.PocketInfo;
import com.weking.model.pocket.UserGain;
import com.weking.service.pay.PocketService;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service("gameService")
public class GameService {

    private static Logger log = Logger.getLogger(GameService.class);

    @Resource
    private GameLogMapper gameLogMapper;
    @Resource
    private PocketService pocketService;
    @Resource
    private PocketInfoMapper pocketInfoMapper;
    @Resource
    private BetLogMapper betLogMapper;
    @Resource
    private GameDataMapper gameDataMapper;
    @Resource
    private GameSubsidyMapper gameSubsidyMapper;
    @Resource
    private AccountInfoMapper accountInfoMapper;
    @Resource
    private GameBetMapper gameBetMapper;
    @Resource
    private LiveLogInfoMapper liveLogInfoMapper;
    @Resource
    private UserBillMapper userBillMapper;
    @Resource
    private UserGainMapper userGainMapper;


    /**
     * 获取娃娃机的礼物奖品数组
     *
     * @return
     */
    public JSONArray getTargetList() {

        JSONArray array = new JSONArray();
        List<GiftInfo> giftList = GiftCache.getGiftList();
        if (giftList == null || giftList.size() == 0) {
            return array;
        }
        JSONArray arrayId1 = new JSONArray();
        JSONArray arrayId2 = new JSONArray();
        JSONArray arrayId3 = new JSONArray();
        getGiftList12(giftList, arrayId1, 10);
        getGiftList12(giftList, arrayId2, 100);
        getGiftList12(giftList, arrayId3, 1000);
        array.add(arrayId1);
        array.add(arrayId2);
        array.add(arrayId3);
        return array;
    }

    //获得房间游戏信息
    JSONObject getGameLiveInfo(int liveId, String account) {
        JSONObject object = new JSONObject();
        int game_type = GameCache.get_game_type(liveId);//游戏房间类型
        switch (game_type) {
            case GameUtil.DOLL_MACHINE://娃娃机
                object.put("doll_game", getTargetList());
                object.put("game_type", game_type);
                break;
            case GameUtil.STAR_WARS:
                object.put("star_wars", getStarWars());
                break;
            case GameUtil.CRAZY_RACING:
                int time;
                int game_state = GameCache.get_game_state(liveId);//游戏状态
                object.put(C.ImField.game_info, getBetResult(liveId, account));//得到游戏信息
                if (game_state == GameUtil.BET_STATE) {//如果游戏在开牌中 2为下注 1为开牌
                    time = GameUtil.BET_TIME;
                } else {//开牌中
                    time = GameUtil.BEGIN_CARD_TIME;
                }
                object.put(C.ImField.game_state, game_state);
                object.put(C.ImField.game_id,GameCache.get_game_id(liveId));
                long diff = LibDateUtils.getDateTimeTick(GameCache.get_game_time(liveId), LibDateUtils.getLibDateTime()) / 1000;
                object.put(C.ImField.countdown_time, time - diff < 0 ? 0 : time - diff);//倒计时时间;
                break;
            default:
                break;
        }
        object.put(C.ImField.game_type, game_type);
        return object;
    }

    /**
     * 下注阶段进入房间的数据
     */
    private String getBetResult(int liveId, String account) {
        JSONArray array = new JSONArray();
        JSONObject leftJson = new JSONObject();
        JSONObject centerJson = new JSONObject();
        JSONObject rightJson = new JSONObject();
        leftJson.put(C.ImField.position_id, GameUtil.leftCardType);
        leftJson.put(C.ImField.my_bet_number, GameCache.get_game_user(liveId, account, GameUtil.leftCard));//第一副牌
        leftJson.put(C.ImField.all_bet_number, GameCache.get_position_bet(liveId, GameUtil.leftCard));
        array.add(leftJson);
        centerJson.put(C.ImField.position_id, GameUtil.centerCardType);
        centerJson.put(C.ImField.my_bet_number, GameCache.get_game_user(liveId, account, GameUtil.centerCard));//第二副牌
        centerJson.put(C.ImField.all_bet_number, GameCache.get_position_bet(liveId, GameUtil.centerCard));
        array.add(centerJson);
        rightJson.put(C.ImField.position_id, GameUtil.rightCardType);
        rightJson.put(C.ImField.my_bet_number, GameCache.get_game_user(liveId, account, GameUtil.rightCard));//第三副牌
        rightJson.put(C.ImField.all_bet_number, GameCache.get_position_bet(liveId, GameUtil.rightCard));
        array.add(rightJson);
        return array.toString();
    }

    private void getGiftList12(List<GiftInfo> giftList, JSONArray arrayId, int num) {
        for (GiftInfo giftInfo : giftList) {
            if (giftInfo.getPrice() > num) {
                JSONObject item = new JSONObject();
                item.put(C.ImField.id, giftInfo.getId());
                arrayId.add(item);
            }
            if (arrayId.size() >= 12) {
                return;
            }
        }
        int count = arrayId.size();
        if (count < 12) {
            for (int i = 0; i < 12 - count; i++) {
                arrayId.add(arrayId.opt(i));
            }
        }
    }

    /**
     * @param liveId  直播id
     * @param hit     手机端是否击中
     * @param capital 本钱
     * @param id      命中目标的id
     * @return json
     */
    public JSONObject fire(int liveId, int userId,String nickname, String avatar, boolean hit, int capital, int id, String lang_code) {

        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        int my_diamond = pocketInfoMapper.getSenderLeftDiamondbyid(userId);

        if (my_diamond >= capital) {
            float price = 0;
            float radix = 0;
            GameFire gameFire = new GameFire();
            boolean isGet = false; //服务端判断是否命中
            int gameType = LibSysUtils.toInt(GameCache.get_game_type(liveId));
            switch (gameType) {
                case GameUtil.DOLL_MACHINE:
                    gameFire.setTargetId(id);

                    if (hit) {
                        price = LibSysUtils.toInt(GiftCache.getGift(id, GiftCache.field_price));
                        isGet = DollMachine.getDollMachineResult(capital, price); // 返回是否命中。
                    } else {
                        gameFire.setTargetId(-1);
                    }

                    break;
                case GameUtil.STAR_WARS:
                    if (hit) {
                        gameFire.setTargetId(id);
                        radix = Float.valueOf(GameCache.getNpc(id, GameCache.npc_field_radix));
                        price = radix * capital;
                        isGet = DollMachine.getStarWarsResult(capital, price); // 返回是否命中。
                    } else {
                        gameFire.setTargetId(-1);
                    }
                    break;
                default:
                    log.error("fire 接口的游戏类型错误gameType:" + gameType);

                    return LibSysUtils.getResultJSON(ResultCode.live_game_error_type,
                            LibProperties.getLanguage(lang_code, "weking.lang.parameter.error"));
            }

            List<String> roomCache = WKCache.get_room(liveId,
                    C.WKCacheRoomField.user_id,
                    C.WKCacheRoomField.live_stream_id,
                    C.WKCacheRoomField.nickname,
                    C.WKCacheRoomField.avatar);
            int anchorId = LibSysUtils.toInt(roomCache.get(0));
            gameFire.setGameType(gameType);
            gameFire.setBetNum(capital);
            gameFire.setLiveId(liveId);
            gameFire.setTime(LibDateUtils.getLibDateTime());
            gameFire.setUserId(userId);
            gameFire.setHit(isGet ? 1 : 0);
            gameFire.setIncomeCoin(isGet ? (int) price : 0);
            gameFire.setAnchorId(anchorId);
            object.put("hit", isGet);


            // 计算输赢多少钱
            int win;
            if (isGet) {
                win = (int) (price - capital);
            } else {
                win = -capital;
            }

            // 进行扣加钱操作，
            boolean success = pocketService.deductGameFire(gameFire, win, userId);
            if (success) {
                //插入消费日志表
                pocketService.recordConsume(userId, 0, capital, 0, -1, liveId);//玩游戏设置为-1
                if(isGet){ //如果赢了 就插入收入日志
                   /* UserBill userBill = UserBill.getBill(userId, price, 0,liveId, C.UserBillType.live_game);
                    userBillMapper.insert(userBill);*/
                   //游戏收入存入新的表单中
                    int amount= (int) price;
                    UserGain gain = UserGain.getGain(userId, C.UserGainType.game_pay, amount, liveId);
                    userGainMapper.insertSelective(gain);
                }
                object.put("my_diamonds", my_diamond + win);
                if (isGet) { // 抓取成功则发送房间消息
                    if (gameType == GameUtil.DOLL_MACHINE) {
                        String giftName = GiftCache.getGift(id, GiftCache.field_name);
                        sendRoomMsgFireSuccess(liveId, userId, lang_code, giftName);

                        IMPushUtil.sendGlobalMsgDoll(capital, liveId, roomCache.get(1), (int) price, giftName,
                                nickname, roomCache.get(2), roomCache.get(3), 0);
                    } else {
                        String npcName = GameCache.getNpc(id, GameCache.npc_field_name);
                        sendRoomMsgStarWarsSuccess(liveId, userId, lang_code, npcName);
                        IMPushUtil.sendGlobalMsgStarWar(liveId, roomCache.get(1), radix, nickname, roomCache.get(2),
                                npcName, roomCache.get(3), 0);
                    }
                }
            } else {
                object = LibSysUtils.getResultJSON(ResultCode.game_fire_error,
                        LibProperties.getLanguage(lang_code, "weking.lang.app.game.fire.error"));
                object.put("my_diamonds", my_diamond);
            }

        } else { //余额不足

            object = LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds,
                    LibProperties.getLanguage(lang_code, "weking.lang.info.nomoney"));
            object.put("my_diamonds", my_diamond);
        }


        return object;
    }

    /**
     * 下注
     *
     * @param userId      userId
     * @param account     account
     * @param liveId      liveId
     * @param betNum      betNum
     * @param positionId  押注的位置 1左，2中，3右
     * @param lang_code   lang_code
     * @param api_version api_version
     * @return json
     */
    public JSONObject bet(int userId, String account, int liveId, int betNum, int positionId, String lang_code, double api_version) {
        return betOther(userId, account, liveId, betNum, positionId, lang_code, api_version);
    }

    public JSONObject betOther(int userId, String account, int liveId, int betNum, int positionId, String lang_code, double api_version) {
        JSONObject checkJson = doBetCheck(account, liveId, betNum, lang_code);
        if (checkJson.optInt("code") != ResultCode.success) {
            return checkJson;
        }

        int my_diamond = pocketInfoMapper.getSenderLeftDiamondbyid(userId);
        //余额不足
        if (my_diamond < betNum) {
            return LibSysUtils.getResultJSON(ResultCode.live_not_sufficient_funds,
                    LibProperties.getLanguage(lang_code, "weking.lang.info.nomoney"));
        }
        return doBet(userId, account, liveId, betNum, positionId, lang_code,
                my_diamond, GameUtil.TAURUS_CARD, api_version);
    }

    private JSONObject doBet(int userId, String account, int liveId, int betNum, int positionId, String lang_code,
                             int my_diamond, int gameType, double api_version) {
        String position = getPosition(positionId);
        JSONObject object = pocketService.deductDiamond(userId, account, liveId, positionId, position, betNum,
                my_diamond, gameType, lang_code);
        if (object.optInt("code") == ResultCode.success) {
            int myBet = GameCache.get_game_user(liveId, account, position);//下注业务处理，得到自己下注金额
            int allBet = getCurPositionBet(liveId, position);//得到该牌下注总金额
            object.put("position_id", positionId);
            object.put("my_bet_number", myBet);
            object.put("all_bet_number", allBet);
            pushBetReuslt(liveId, account, betNum, positionId, api_version);//下注,推送所有下注情况
        }
        return object;
    }

    /**
     * 推送下注所有结果
     *
     * @param liveId      liveId
     * @param betNum      betNum
     * @param positionId  押注的位置 1左，2中，3右
     * @param api_version api_version
     */
    private void pushBetReuslt(int liveId, String account,
                               int betNum, int positionId, double api_version) {
        JSONObject finallyResult = new JSONObject();
        JSONObject first = new JSONObject();
        JSONObject second = new JSONObject();
        JSONObject Third = new JSONObject();
        JSONArray bet_info = new JSONArray();

        finallyResult.put(C.ImField.im_code, IMCode.bet_send);
        finallyResult.put(C.ImField.account, account);
        finallyResult.put(C.ImField.position_id, positionId);
        finallyResult.put(C.ImField.bet_number, betNum);
        first.put(C.ImField.id, GameUtil.leftCardType);
        first.put(C.ImField.all_bet_number, GameCache.get_position_bet(liveId, GameUtil.leftCard));
        bet_info.add(first);
        second.put(C.ImField.id, GameUtil.centerCardType);
        second.put(C.ImField.all_bet_number, GameCache.get_position_bet(liveId, GameUtil.centerCard));
        bet_info.add(second);
        Third.put(C.ImField.id, GameUtil.rightCardType);
        Third.put(C.ImField.all_bet_number, GameCache.get_position_bet(liveId, GameUtil.rightCard));
        bet_info.add(Third);
        finallyResult.put(C.ImField.bet_info, bet_info);
        finallyResult.put(C.ImField.live_id, liveId);
        String stream_id = getLiveStreamId(liveId);
        if (!LibSysUtils.isNullOrEmpty(stream_id)) {
            WkImClient.sendRoomMsg(stream_id, finallyResult.toString(),null);
        }
    }

    //获得当前位置下注总金额
    private int getCurPositionBet(int liveId, String position) {
        return GameCache.get_position_bet(liveId, position);
    }

    //记录用户下注日志
    public int recordBetLog(int user_id, long game_id, int position_id, int bet_num) {
        BetLog record = new BetLog();
        record.setAddTime(LibDateUtils.getLibDateTime());
        record.setGameId(game_id);
        record.setBetNum(bet_num);
        record.setUserId(user_id);
        record.setPositionId((byte) position_id);
        return betLogMapper.insert(record);
    }

    //根据位置ID获取位置标识
    private String getPosition(int positionId) {
        String position = "";
        switch (positionId) {
            case GameUtil.leftCardType:
                position = GameUtil.leftCard;
                break;
            case GameUtil.centerCardType:
                position = GameUtil.centerCard;
                break;
            case GameUtil.rightCardType:
                position = GameUtil.rightCard;
                break;
            default:
                break;
        }
        return position;
    }

    private JSONObject doBetCheck(String account, int liveId, int betNum, String lang_code) {
        int game_state = GameCache.get_game_state(liveId);
        //下注阶段才能下注
        if (game_state != GameUtil.BET_STATE) {
            return LibSysUtils.getResultJSON(ResultCode.game_bet_error, LibProperties.getLanguage(lang_code, "weking.lang.game.bet.error.moment"));
        }
        // 超过最大下注限制
        if (getUserAllBet(account, liveId) + betNum >
                LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.s_game_bet_max))) {
            return LibSysUtils.getResultJSON(ResultCode.game_bet_online,
                    LibProperties.getLanguage(lang_code, "weking.lang.app.game.bet.online"));
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    //获取用户所有下注
    private int getUserAllBet(String account, int liveId) {
        int leftBet = GameCache.get_game_user(liveId, account, GameUtil.leftCard);
        int centerBet = GameCache.get_game_user(liveId, account, GameUtil.centerCard);
        int rightBet = GameCache.get_game_user(liveId, account, GameUtil.rightCard);
        return leftBet + centerBet + rightBet;
    }


    public String startGame(int userId, int liveId,int gameType, double api_version) {
        if (gameType == GameUtil.DOLL_MACHINE) {
            // 抓娃娃
            JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
            GameCache.set_game_type(liveId, gameType); //设置游戏类型
            object.put(C.ImField.doll_game, getTargetList());
            return object.toString();
        }
        if(gameType == GameUtil.CRAZY_RACING){
            return startGamePoker(userId,liveId,gameType);
        }
        return LibSysUtils.getResultJSON(ResultCode.success).toString();
    }

    /**
     * 疯狂赛车哪一方赢
     */
    public int racingWinId(int liveId){
        int openRules = LibSysUtils.toInt(WKCache.get_system_cache("s_game_open_rules"));
        return getTargetWinIdWithRule(liveId, WkUtil.getBetweenRandom(1,3), openRules);
    }

    private static int getWinIdByRandom(Map<Integer,Integer> winMap,int[] bet){
        JSONObject probability = JSONObject.fromObject(WKCache.get_system_cache("game.bet.probability"));
        int random = WkUtil.getBetweenRandom(1,100);
        double max = 100 * probability.optDouble("max",0.2);
        double mid = max + 100 * probability.optDouble("mid",0.3);
        int winId;
        if(random <= max){
            winId = winMap.get(bet[2]);
        }else if(random <= mid){
            winId = winMap.get(bet[1]);
        }else{
            winId = winMap.get(bet[0]);
        }
        log.info("========random:"+random+"===="+winId+"===="+max+"===="+mid+"=="+winMap.toString());
        return winId;
    }

    /**
     * 获得位置下注Map<下注量，位置ID>
     * 相同下注量位置随机取一个
     */
    private static Map<Integer,Integer> getPositionBetMap(int leftBet,int centerBet,int rightBet){
        Map<Integer,Integer> winMap = new HashMap<>();
        winMap.put(leftBet,GameUtil.leftCardType);
        winMap.put(centerBet,GameUtil.centerCardType);
        winMap.put(rightBet,GameUtil.rightCardType);
        if(leftBet == centerBet){
            winMap.put(leftBet,GameUtil.getRandNum(GameUtil.leftCardType, GameUtil.centerCardType));
        }
        if(leftBet == rightBet){
            winMap.put(leftBet,GameUtil.getRandNum(GameUtil.leftCardType, GameUtil.rightCardType));
        }
        if(centerBet == rightBet){
            winMap.put(centerBet,GameUtil.getRandNum(GameUtil.centerCardType, GameUtil.rightCardType));
        }
        return winMap;
    }

    /**
     * 根据规则返回我们想要的赢位置
     * @param liveId liveId
     * @param win_position 原来赢的位置
     * @param openRules 规则
     * @return 根据规则算出来的赢的位置
     */
    public static int getTargetWinIdWithRule(int liveId, int win_position, int openRules ) {
        int wid = win_position;
        int leftBet = GameCache.get_position_bet(liveId, GameUtil.leftCard);
        int centerBet = GameCache.get_position_bet(liveId, GameUtil.centerCard);
        int rightBet = GameCache.get_position_bet(liveId, GameUtil.rightCard);
        if (leftBet == centerBet && leftBet == rightBet) { //三数相等随机赢方
            wid = win_position;
        } else {
            int[] bet = {leftBet, centerBet, rightBet};
            Arrays.sort(bet); //数组排序 升序
            switch (openRules) {
                case 1: //最大下注赢
                    if (bet[2] > bet[1]) {
                        if (leftBet == bet[2]) {
                            wid = GameUtil.leftCardType;
                        } else if (centerBet == bet[2]) {
                            wid = GameUtil.centerCardType;
                        } else {
                            wid = GameUtil.rightCardType;
                        }
                    } else { // bet[2] = bet[1]
                        if (leftBet == bet[0]) { // 左边最小，则随机中间、右边
                            wid = GameUtil.getRandNum(GameUtil.centerCardType, GameUtil.rightCardType);
                        } else if (centerBet == bet[0]) {// 中间最小，则随机左边、右边
                            wid = GameUtil.getRandNum(GameUtil.leftCardType, GameUtil.rightCardType);
                        } else {// 右边最小，则随机左边、中间
                            wid = GameUtil.getRandNum(GameUtil.leftCardType, GameUtil.centerCardType);
                        }
                    }
                    break;
                case 2: //最小下注赢
//                    System.out.println("----最小下注赢----随机的结果：" + win_position);
                    if (bet[0] < bet[1]) {
                        if (leftBet == bet[0]) {
                            wid = GameUtil.leftCardType;
//                            System.out.println("----左边的牌押注最小，返回左边 -- " + wid);
                        } else if (centerBet == bet[0]) {
                            wid = GameUtil.centerCardType;
//                            System.out.println("----中间的牌押注最小，返回中间 -- " + wid);
                        } else {
                            wid = GameUtil.rightCardType;
//                            System.out.println("----右边的牌押注最小，返回右边 -- " + wid);
                        }
                    } else {
                        if (leftBet == bet[2]) {
                            wid = GameUtil.getRandNum(GameUtil.centerCardType, GameUtil.rightCardType);
//                            System.out.println("----最小押注有两副牌，左边最大，则随机中间、右边 -- " + wid);
                        } else if (centerBet == bet[2]) {
                            wid = GameUtil.getRandNum(GameUtil.leftCardType, GameUtil.rightCardType);
                        } else {
                            wid = GameUtil.getRandNum(GameUtil.leftCardType, GameUtil.centerCardType);
                        }
                    }
                    break;
                case 3: // 最大不赢
                    if (bet[2] == bet[1]) {
                        // 最大押注有两副牌，选出最小押注的
                        if(leftBet != bet[2]) {
                            wid = GameUtil.leftCardType;
                        } else if(centerBet != bet[2]) {
                            wid = GameUtil.centerCardType;
                        } else {
                            wid = GameUtil.rightCardType;
                        }
                    } else {
                        if(leftBet == bet[2]) { //左边的牌押注最大，则随机中间和右边
                            wid = GameUtil.getRandNum(GameUtil.centerCardType, GameUtil.rightCardType);
                        } else if (centerBet == bet[2]){//中间的牌押注最大，则随机左边和右边
                            wid = GameUtil.getRandNum(GameUtil.leftCardType, GameUtil.rightCardType);
                        } else {//右边的牌押注最大，则随机左边和 中间
                            wid = GameUtil.getRandNum(GameUtil.leftCardType, GameUtil.centerCardType);
                        }
                    }
                    break;
                case 4: // 最小不赢
                    if (bet[0] == bet[1]) {
                        // 最小押注有两副牌，选出最大押注的
                        if(leftBet == bet[2]) {
                            wid = GameUtil.leftCardType;
                        } else if(centerBet == bet[2]) {
                            wid = GameUtil.centerCardType;
                        } else {
                            wid = GameUtil.rightCardType;
                        }
                    } else {
                        if(leftBet == bet[0]) { //左边的牌押注最小，则随机中间和右边
                            wid = GameUtil.getRandNum(GameUtil.centerCardType, GameUtil.rightCardType);
                        } else if (centerBet == bet[0]){//中间的牌押注最小，则随机左边和右边
                            wid = GameUtil.getRandNum(GameUtil.leftCardType, GameUtil.rightCardType);
                        } else {//右边的牌押注最小，则随机左边和中间
                            wid = GameUtil.getRandNum(GameUtil.leftCardType, GameUtil.centerCardType);
                        }
                    }
                    break;
                default:
                    Map<Integer,Integer> winMap = getPositionBetMap(leftBet,centerBet,rightBet);
                    wid = getWinIdByRandom(winMap,bet);
                    break;
            }
        }
        return wid;
    }

    /**
     * 疯狂赛车数据
     */
    public JSONObject getRacingData(int liveId){
        JSONObject object = new JSONObject();
        JSONArray jsonArray;
        int winId = racingWinId(liveId);
        JSONObject object1 = randSpeed();
        JSONObject object2 = randSpeed();
        JSONObject object3 = randSpeed();
        //判断谁大
        int allGrade1 = object1.getInt("all_grade");
        int allGrade2 = object2.getInt("all_grade");
        int allGrade3 = object3.getInt("all_grade");
        if(allGrade1 > allGrade2 && allGrade1 > allGrade3){
            jsonArray = changeSpeed(winId,object1,object2,object3);
        }else if(allGrade2 > allGrade1 && allGrade2 > allGrade3){
            jsonArray = changeSpeed(winId,object2,object1,object3);
        }else if(allGrade3 > allGrade1 && allGrade3 > allGrade2){
            jsonArray = changeSpeed(winId,object3,object1,object2);
        }else if(allGrade3 == allGrade1 && allGrade3 == allGrade2){
            //三个数都相等
            if(allGrade1 == 0){
                //如果三个数都为0，增强一个
                object1 = appendSpeed(object1);
            }else{
                //三个数都相等,削弱两个
                object2 = weakenSpeed(object2);
                object3 = weakenSpeed(object3);
            }
            jsonArray = changeSpeed(winId,object1,object2,object3);
        }else{
            //最大两个数相等
            if(allGrade1 == allGrade2){
                object2 = weakenSpeed(object2);
                jsonArray = changeSpeed(winId,object1,object2,object3);
            }else if(allGrade1 == allGrade3){
                object3 = weakenSpeed(object3);
                jsonArray = changeSpeed(winId,object1,object2,object3);
            }else{
                object3 = weakenSpeed(object3);
                jsonArray = changeSpeed(winId,object2,object1,object3);
            }
        }
        object.put("win_id",winId);
        object.put("speed_data",jsonArray);
        return object;
    }

    /**
     * 削弱加速
     */
    private JSONObject weakenSpeed(JSONObject object){
        JSONArray jsonArray2 = object.getJSONArray("array");
        jsonArray2.remove(0);
        object.put("array",jsonArray2);
        return object;
    }

    /**
     * 添加加速
     */
    private JSONObject appendSpeed(JSONObject object){
        JSONArray jsonArray2 = object.getJSONArray("array");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("speed_grade",WkUtil.getBetweenRandom(1,3));
        jsonObject.put("place_time",WkUtil.getBetweenRandom(2,8));
        jsonArray2.add(jsonObject);
        object.put("array",jsonArray2);
        return object;
    }
    /**
     * 根据谁赢换加速
     */
    private JSONArray changeSpeed(int winId,JSONObject maxObj,JSONObject object1,JSONObject object2){
        JSONArray jsonArray = new JSONArray();
        switch (winId){
            case 1:
                jsonArray = getCarData(jsonArray,maxObj);
                jsonArray = getCarData(jsonArray,object1);
                jsonArray = getCarData(jsonArray,object2);
                break;
            case 2:
                jsonArray = getCarData(jsonArray,object2);
                jsonArray = getCarData(jsonArray,maxObj);
                jsonArray = getCarData(jsonArray,object1);
                break;
            case 3:
                jsonArray = getCarData(jsonArray,object1);
                jsonArray = getCarData(jsonArray,object2);
                jsonArray = getCarData(jsonArray,maxObj);
                break;
            default:
                break;
        }
        return jsonArray;
    }

    private JSONArray getCarData(JSONArray jsonArray,JSONObject jsonObject){
        JSONObject carObj = new JSONObject();
        carObj.put("car",jsonObject.getJSONArray("array"));
        jsonArray.add(carObj);
        return jsonArray;
    }

    /**
     * 随机加速等级
     */
    public JSONObject randSpeed(){
        JSONObject object = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        int allGrade = 0;
        //每辆车有三次获得加速的机会(当加速档次随机为0则在当前次数不获得)
        for (int n=0; n<3; n++){
            jsonObject = new JSONObject();
            int speedGrade = WkUtil.getBetweenRandom(0,3);
            if(speedGrade != 0){
                int placeTime = WkUtil.getBetweenRandom(3*n+1,3*n+2);
                jsonObject.put("speed_grade",speedGrade);
                jsonObject.put("place_time",placeTime);
                jsonArray.add(jsonObject);
            }
            allGrade = allGrade + speedGrade;
        }
        object.put("array",jsonArray);
        object.put("all_grade",allGrade);
        return object;
    }

    /**
     * 加入游戏
     *
     * @param live_id live_id
     * @param account account
     * @return String
     */
    public String join(int live_id, String account) {
        JSONObject result = getGameLiveInfo(live_id, account);
        result.put("code", ResultCode.success);
        return result.toString();
    }

    /**
     * 切换游戏
     *
     * @param liveId   liveId
     * @param switchType gameType
     * @return String
     */
    public String switchGame(int userId,int liveId, int switchType,String lang_code) {
        //当前游戏为疯狂赛车先给用户发送切换游戏提醒，设置切换游戏类型并结束接口推送切换游戏数据
        int gameType = GameCache.get_game_type(liveId);
        if(gameType == GameUtil.CRAZY_RACING){
            GameCache.set_switch_type(liveId, switchType);
            sendRoomMsgSwitchGame(liveId,switchType, lang_code);
            return LibSysUtils.getResultJSON(ResultCode.success).toString();
        }
        // 直接推送通知给直播间的人切换游戏
        String live_stream_id = WKCache.get_room(liveId, C.WKCacheRoomField.live_stream_id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(C.ImField.im_code, IMCode.game_switch);
        jsonObject.put(C.ImField.game_type, switchType);
        jsonObject.put(C.ImField.live_id, liveId);
        if (switchType == GameUtil.DOLL_MACHINE) {
            jsonObject.put(C.ImField.doll_game, getTargetList());
        }else if (switchType == GameUtil.STAR_WARS) {
            // 切换游戏时打飞机
            jsonObject.put(C.ImField.star_wars, getStarWars());
        }else if(switchType == GameUtil.CRAZY_RACING){
            //return //startGame(userId,liveId,switchType,0);
        }
        GameCache.set_game_type(liveId, switchType);
        System.out.println(jsonObject.toString());
        WkImClient.sendRoomMsg(live_stream_id, jsonObject.toString(),1);
        return LibSysUtils.getResultJSON(ResultCode.success).toString();
    }

    private String sendRoomMsgSwitchGame(int liveId, int switchType, String langCode) {
        String msg = LibProperties.getLanguage(langCode,"weking.lang.app.game.switch");
        String game = "";
        switch(switchType){
            case 0:
                game = LibProperties.getLanguage(langCode,"weking.lang.app.game.broadcast.talent");
                break;
            case GameUtil.DOLL_MACHINE:
                game = LibProperties.getLanguage(langCode,"weking.lang.app.game.catch.doll");
                break;
            case GameUtil.STAR_WARS:
                game = LibProperties.getLanguage(langCode,"weking.lang.app.game.star.wars");
                break;
            case GameUtil.CRAZY_RACING:
                game = LibProperties.getLanguage(langCode,"weking.lang.app.game.crazy.racing");
                break;
            default:
                break;
        }
        msg = String.format(msg,game);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(C.ImField.im_code, IMCode.sys_msg);
        jsonObject.put(C.ImField.live_id, liveId);
        jsonObject.put(C.ImField.msg, msg);
        String live_stream_id = WKCache.get_room(liveId, C.WKCacheRoomField.live_stream_id);
        WkImClient.sendRoomMsg(live_stream_id, jsonObject.toString(),1);
        System.out.println(jsonObject.toString());
        return msg;
    }

    public JSONArray getStarWars() {
        JSONArray array = new JSONArray();
        List<GameNpc> list = GameCache.getNpcList();
        for (GameNpc npc : list) {
            JSONObject item = new JSONObject();
            item.put(C.ImField.id, npc.getId());
            item.put(C.ImField.radix, npc.getRadix());
            array.add(item);
        }
        return array;
    }
    /**
     * 抓娃娃成功，发送房间消息
     *
     * @param liveId    liveId
     * @param user_id   user_id
     * @param lang_code lang_code
     */
    private void sendRoomMsgFireSuccess(int liveId, int user_id, String lang_code, String giftName) {
        String msg = LibProperties.getLanguage(lang_code, "weking.lang.app.game.doll.fire.msg") + giftName;
        sendRoomMsg(liveId, user_id, msg, true);
    }

    /**
     * 星球大战成功，发送房间消息
     *
     * @param liveId    liveId
     * @param user_id   user_id
     * @param lang_code lang_code
     */
    private void sendRoomMsgStarWarsSuccess(int liveId, int user_id, String lang_code, String npcName) {
        String msg = LibProperties.getLanguage(lang_code, "weking.lang.app.game.starwars.fire.msg") + npcName;
        sendRoomMsg(liveId, user_id, msg, false);
    }

    /**
     * 发送消息
     *
     * @param liveId  liveId
     * @param user_id user_id
     * @param msg     msg
     */
    private void sendRoomMsg(int liveId, int user_id, String msg, boolean delay) {
        String live_stream_id = WKCache.get_room(liveId, C.WKCacheRoomField.live_stream_id);
        UserCacheInfo userCacheInfo = WKCache.get_user(user_id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(C.ImField.im_code, IMCode.sys_msg);
        jsonObject.put(C.ImField.account, userCacheInfo.getAccount());
        jsonObject.put(C.ImField.msg, msg);
        jsonObject.put(C.ImField.pic_head_low, WkUtil.combineUrl(userCacheInfo.getAvatar(), UploadTypeEnum.AVATAR, true));
        jsonObject.put(C.ImField.nickname, userCacheInfo.getNickname());
        jsonObject.put(C.ImField.live_id, liveId);
        jsonObject.put(C.ImField.level, userCacheInfo.getLevel());
        if (delay) {
            TimerUtil.sendRoomMsgDelay(live_stream_id, jsonObject.toString(), 4);
        } else {
            WkImClient.sendRoomMsg(live_stream_id, jsonObject.toString(),1);
        }
    }

    //房间游戏列表
    public JSONObject getGameList(int liveId, int index, int count) {
        List<Integer> list = gameLogMapper.selectGameByLive(liveId, index, count);
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", list);
        return object;
    }

    //开始游戏
    public String startGamePoker(int userId, int liveId, int gameType) {
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        int anchorId = LibSysUtils.toInt(WKCache.get_room(liveId, C.WKCacheRoomField.user_id));
        if (anchorId != userId) {
            //return object.toString();
        }
        if (GameCache.get_game_state(liveId) != GameUtil.BET_STATE) { //如果游戏还在下注状态则不能开始
            if (gameType > 0) {
                initGame(liveId); // 清除上局游戏数据
                long game_id = recordGameLog(liveId, userId, gameType); // 记录游戏日志
                GameCache.set_game_time(liveId, LibDateUtils.getLibDateTime()); //设置游戏开始时间
                GameCache.set_game_type(liveId, gameType); //设置游戏类型
                GameCache.set_game_id(liveId, game_id);
                JSONObject pushJson = new JSONObject();
                switch (gameType) {
                    case GameUtil.FRIED_GOLDEN: //扎金花
                        break;
                    case GameUtil.TEXAS_HOLDEM:  //天天德州
                        break;
                    case GameUtil.TAURUS_CARD: //牛牛
                        break;
                    case GameUtil.CRAZY_RACING: //疯狂赛车
                        break;
                    default:
                        break;
                }
                pushJson.put(C.ImField.im_code, IMCode.game_bet);
                pushJson.put(C.ImField.countdown_time, GameUtil.BET_TIME);
                pushJson.put(C.ImField.live_id, liveId);
                pushJson.put(C.ImField.game_type, gameType);
                String roomId = WKCache.get_room(liveId, C.WKCacheRoomField.live_stream_id);
                WkImClient.sendRoomMsg(roomId, pushJson.toString(), null);
                pushJson.remove(C.ImField.im_code);
                object.put(C.ImField.game_id, game_id);
                object.putAll(pushJson);
                GameCache.set_game_state(liveId, GameUtil.BET_STATE); //设置游戏状态
            }
            GameCache.set_game_data(liveId, GameUtil.RESULT_DATA_END_GAME, object.toString());//缓存数据，主播重试时返回
        } else {
            object.put(C.ImField.countdown_time, GameUtil.BET_TIME);
            object.put(C.ImField.live_id, liveId);
            object.put(C.ImField.game_type, gameType);
            object.put(C.ImField.game_id, GameCache.get_game_id(liveId));
            //return GameCache.get_game_data(liveId, GameUtil.RESULT_DATA_END_GAME);//  返回缓存数据
        }
        return object.toString();
    }

    /**
     * 获取游戏结果数据
     * @param userId
     * @return
     */
    public JSONObject getGameResultData(int userId,int liveId){
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        JSONObject imObj = getRacingData(liveId);
        object.putAll(imObj);
        if(GameCache.get_game_state(liveId) == GameUtil.BET_STATE){
            GameCache.set_game_state(liveId,GameUtil.BEGIN_CARD_STATE);
            GameCache.set_game_time(liveId,LibDateUtils.getLibDateTime());
            String roomId = getLiveStreamId(liveId);
            GameCache.set_game_data(liveId,GameUtil.RESULT_DATA_START_GAME,imObj.toString());
            imObj.put(C.ImField.im_code, IMCode.game_data);
            imObj.put(C.ImField.game_id,GameCache.get_game_id(liveId));
            imObj.put(C.ImField.live_id,liveId);
            WkImClient.sendRoomMsg(roomId,imObj.toString(),null);
            System.out.println(imObj.toString());
        }
        return object;
    }

    public String getLiveStreamId(int liveId){
        String roomId = WKCache.get_room(liveId, C.WKCacheRoomField.live_stream_id);
        if(roomId == null){
            roomId = liveLogInfoMapper.findLiveStreamIdById(liveId);
        }
        return roomId;
    }



    //返还下注
    public void refundBet(int userId,int liveId,RoomCacheInfo liveInfo) {
        int gameType = GameCache.get_game_type(liveId);
        if (gameType > 0 && GameCache.get_game_state(liveId) == GameUtil.BET_STATE) {
            long curTime = LibDateUtils.getLibDateTime();
            long gameTime = GameCache.get_game_time(liveId);
            long diffTime =  LibDateUtils.getDateTimeTick(gameTime,curTime);
            long sleepTime = GameUtil.BET_TIME * 1000 - diffTime;
            log.info("game_thread1:"+diffTime+"="+sleepTime+"="+liveId);
            sleepGame(liveId,sleepTime);
            getGameResultData(userId, liveId);
        }
        if(gameType > 0 && GameCache.get_game_state(liveId) == GameUtil.BEGIN_CARD_STATE){
            long curTime = LibDateUtils.getLibDateTime();
            long gameTime = GameCache.get_game_time(liveId);
            long diffTime =  LibDateUtils.getDateTimeTick(gameTime,curTime);
            long sleepTime = GameUtil.BEGIN_CARD_TIME * 1000 - diffTime;
            log.info("game_thread2:"+diffTime+"="+sleepTime+"="+liveId);
            sleepGame(liveId,sleepTime);
            endGamePoker(userId,liveId,gameType,0,liveInfo);
        }
        if (WkImClient.USEWKINGIM) {
            WkImClient.delRoom(liveInfo.getLive_stream_id(), liveInfo.getAccount(), 5);
        }
    }

    private void sleepGame(int liveId,long sleepTime){
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    //记录游戏日志
    private long recordGameLog(int liveId, int userId, int gameType) {
        GameLog record = new GameLog();
        record.setLiveId(liveId);
        record.setUserId(userId);
        record.setGameTime(LibDateUtils.getLibDateTime());
        record.setGameType((byte) gameType);
        long re = gameLogMapper.insert(record);
        if (re > 0) {
            re = record.getId();
        }
        return re;
    }

    /**
     * 初始化房间游戏信息
     */
    private void initGame(int liveId) {
        GameCache.del_all_game_info(liveId);
    }

    /**
     * 结束游戏
     * type 0:app结束，2:定时器
     */
    public String endGamePoker(int userId, int liveId, int gameType, double api_version,RoomCacheInfo roomCacheInfo) {
        if(roomCacheInfo == null){
            log.error("endGamePoker:"+liveId);
            if(liveId == 0){
                liveId = LibSysUtils.toInt(WKCache.get_user(userId,"live_id"));
            }
            roomCacheInfo = WKCache.get_room(liveId);
        }
//        if (api_version < 4) {// 旧版接口没有gameType
//            gameType = LibSysUtils.toInt(roomCache.get(0));
//        }

        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        int anchorId = LibSysUtils.toInt(roomCacheInfo.getUser_id());
        //检测操作是主播发起
        if (anchorId != userId) {
            //return object.toString();
        }
        int gameId = GameCache.get_game_id(liveId);
        int gameState = GameCache.get_game_state(liveId);
        if (gameState == GameUtil.BEGIN_CARD_STATE || gameState == GameUtil.GAME_END) {//游戏处于开牌阶段才能结束
            GameCache.set_game_state(liveId, GameUtil.GAME_END); //该局游戏结束
            // 设置游戏时间
            GameCache.set_game_time(liveId, LibDateUtils.getLibDateTime());

            //获取游戏数据（牌数据）
            JSONObject gameData = getGameCardData(gameType, liveId);
            // 记录WinId到缓存
            int win_id = gameData.optInt(C.ImField.win_id);
            GameCache.setWinId(liveId, win_id);
            //更新游戏牌局信息 wk_gameLog
            int re = updateWinId(gameId, win_id);
            if (re > 0) {
                String live_stream_id = roomCacheInfo.getLive_stream_id();
                // 记录游戏数据到db wk_gameData
                recordGameData(gameId, gameData.toString());
                //计算主播提成
                double gameAllBet = getGameAllBet(liveId);
                pocketService.anchorSubsidy(userId, gameId, gameAllBet);
                // 结束游戏指令，IM推送
                sendEndGameRoomMsg(liveId, live_stream_id, userId, gameId, gameData);

                Set<String> userAccountSet = getBetUser(liveId);
                Map<String, Integer> winUserIdMoney = new HashMap<String, Integer>(); //记录赢的用户
                Map<String, Integer> betUserIdBet = new HashMap<String, Integer>(); //记录用户下注
                Long my_diamonds;
                int betCoin;

                // DB批量查用户信息
                Map<String, Integer> accountUserIdMap = getBetUserId(userAccountSet);
                // DB批量查钱包表信息
                Map<String, Long> userIdDiamondMap = getBetUserIdDiamond(accountUserIdMap);

                List<Commission> commissionList = new ArrayList<>();
                // 平台抽水比例规则 {"type":1,"licence":0.2, "minBet":100, "minWin":300}
                String commissionRule = WKCache.get_system_cache(C.WKSystemCacheField.s_game_commission_rule);
                JSONObject commissionRuleJson = JSONObject.fromObject(commissionRule);
                // 抽水比例licence，
                Double licenceRule = commissionRuleJson.optDouble("licence");
                // 押注超过minBet
                int minBetRule = commissionRuleJson.optInt("minBet");
                // 赢超过minWin
                int minWinRule = commissionRuleJson.optInt("minWin");
                int commissionType = commissionRuleJson.optInt("type");

                for (String betAccount : userAccountSet) {
                    Commission commission = new Commission();
                    commission.setUserId(userId);
                    commission.setRule(commissionRule);
                    String betUserId = LibSysUtils.toString(accountUserIdMap.get(betAccount));
                    // 计算下注用户的输赢情况，并抽水
                    JSONObject betData = countBunko(commissionType, liveId, commission, betAccount, win_id,
                            gameType, licenceRule, minBetRule, minWinRule);

                    // 赢的金币 = 赢的金币 - 抽水值
                    int winMoney = betData.optInt("win_money") - commission.getCommission();

                    if (winMoney > 0) {
                        // 赢的情况
                        winUserIdMoney.put(betUserId, winMoney);
                        //WKCache.add_game_rank(LibDateUtils.getLibDateTime("yyyyMMdd"), winMoney, betAccount);
                        //taskService.finishTaskNotice(LibSysUtils.toInt(betUserId), C.TaskType.WIN_GAME);
                        my_diamonds = userIdDiamondMap.get(betUserId) + winMoney;
                        betCoin = betData.optInt("win_bet") - betData.optInt("lose_bet");

                    } else {
                        // 输的情况,从钱包中扣除抽水
                        winUserIdMoney.put(betUserId, -commission.getCommission());
                        my_diamonds = userIdDiamondMap.get(betUserId) - commission.getCommission();
                        if (my_diamonds < 0) {// 加入my_diamonds减去抽水为负数，则需要处理
                            commission.setCommission((int) (commission.getCommission() + my_diamonds));
                        }
                        betCoin = Math.abs(betData.optInt("win_bet") - betData.optInt("lose_bet"));
                    }
                    commissionList.add(commission);
                    betUserIdBet.put(betUserId, betCoin);
                    //  发送输赢信息给下注的每个人/ 全服飘屏
                    sendPrivateGameMsg(null, winMoney, my_diamonds < 0 ? 0 : my_diamonds, betAccount, gameId,
                            liveId,roomCacheInfo.getRole(), live_stream_id, userId, gameType, roomCacheInfo.getAvatar(), api_version);
                }
                // 更新winner钱包表，统计表（排行榜），下注统计表
                pocketService.gameIncome(userId, commissionList, accountUserIdMap, winUserIdMoney, betUserIdBet, gameId, gameType,liveId);

            }
            endGameNext(object, gameData, liveId);
        } else {
            //  返回缓存数据
            return GameCache.get_game_data(liveId, GameUtil.RESULT_DATA_START_GAME);
        }
        int switchType = GameCache.get_switch_type(liveId);
        if(switchType >= 0){
            //返回后清除切换
            GameCache.set_switch_type(liveId,-1);
            GameCache.set_game_type(liveId,switchType);
        }
        object.put("switch_type",switchType);
        return object.toString();
    }

    //记录输赢日志
    public void recordGameBetLog(int anchorId, Map<String, Integer> users, Map<String, Integer> winUser,
                                 Map<String, Integer> betUser, long gameId, int gameType) {
        List<GameBet> list = new ArrayList<>();
        GameBet record;
        long time = LibDateUtils.getLibDateTime();
        String user_id;
        for (Integer userId : users.values()) {
            user_id = LibSysUtils.toString(userId);
            record = new GameBet();
            record.setUserId(userId);
            record.setGameTime(time);
            record.setGameId(gameId);
            record.setBetCoin(betUser.get(user_id));
            record.setIncomeCoin(winUser.get(user_id));
            record.setAnchorId(anchorId);
            record.setGameType((byte) gameType);
            list.add(record);
        }
        if (list.size() > 0) {
            gameBetMapper.insertBatch(list);
        }
    }

    private void endGameNext(JSONObject object, JSONObject gameData, int liveId) {
        object.put(C.ImField.countdown_time, GameUtil.BEGIN_CARD_TIME);
        object.putAll(gameData);
        //设置游戏状态开牌
        GameCache.set_game_state(liveId, GameUtil.BEGIN_CARD_STATE);
        //缓存数据，主播重试时返回
        GameCache.set_game_data(liveId, GameUtil.RESULT_DATA_START_GAME, object.toString());
        // 判断是否切换游戏
//        int nextType = GameCache.get_switch_type(liveId);
//        if (nextType >= 0) {
//            sendSwitchGameMsg(liveId, nextType, 18);
//        }
    }

    //记录主播抽成日志
    public int recordSubsidy(int userId, int anchorSubsidy, long gameId) {
        GameSubsidy record = new GameSubsidy();
        record.setUserId(userId);
        record.setSubsidy(anchorSubsidy);
        record.setGameId(gameId);
        record.setAddTime(LibDateUtils.getLibDateTime());
        return gameSubsidyMapper.insert(record);
    }

    /**
     * 获取牌数据
     *
     * @param gameType 游戏类型
     * @param liveId   直播id
     * @return 牌数据
     */
    private JSONObject getGameCardData(int gameType, int liveId) {
        JSONObject gameData = new JSONObject();
        switch (gameType) {
            case GameUtil.FRIED_GOLDEN: //结束扎金花游戏
                break;
            case GameUtil.TEXAS_HOLDEM:  //结束天天德州游戏
                break;
            case GameUtil.TAURUS_CARD: //牛牛
                break;
            case GameUtil.CRAZY_RACING: //疯狂赛车
                gameData = JSONObject.fromObject(GameCache.get_game_data(liveId, GameUtil.RESULT_DATA_START_GAME));
                break;
            default:
                break;
        }
        return gameData;//{"WB":2,"GB":[{"PD":[{"NC":14,"CB":3},{"NC":9,"CB":1},{"NC":5,"CB":1},{"NC":7,"CB":2},{"NC":13,"CB":3}],"PF":1,"PE":2},{"PD":[{"NC":9,"CB":2},{"NC":8,"CB":3},{"NC":4,"CB":2},{"NC":12,"CB":2},{"NC":8,"CB":1}],"PF":2,"PE":9},{"PD":[{"NC":4,"CB":4},{"NC":2,"CB":2},{"NC":3,"CB":4},{"NC":7,"CB":1},{"NC":13,"CB":1}],"PF":3,"PE":6}]}
    }

    private void sendSwitchGameMsg(int liveId, int switchType, int delay) {
        String live_stream_id = WKCache.get_room(liveId, C.WKCacheRoomField.live_stream_id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(C.ImField.im_code, IMCode.game_switch);
        jsonObject.put(C.ImField.game_type, switchType);
        jsonObject.put(C.ImField.live_id, liveId);

        // 切换游戏时抓娃娃
        if (switchType == GameUtil.DOLL_MACHINE) {
            jsonObject.put(C.ImField.doll_game, getTargetList());
        }

        // 切换游戏时打飞机
        else if (switchType == GameUtil.STAR_WARS) {
            jsonObject.put(C.ImField.star_wars, getStarWars());
        }
        // 延迟切换
//        TimerUtil.sendRoomMsgDelay(live_stream_id, jsonObject.toString(), delay, new Action().new Void() {
//            @Override
//            public void invoke() {
//                GameCache.cleanRoomGame(liveId);
//                GameCache.set_game_type(liveId, switchType);
//                GameCache.set_switch_type(liveId, -1);
//            }
//        });
    }

    /**
     * 修改游戏记录赢家
     */
    private int updateWinId(long gameId, int winId) {
        GameLog record = new GameLog();
        record.setId(gameId);
        record.setWinId((byte) winId);
        return gameLogMapper.updateByPrimaryKeySelective(record);
    }

    //记录游戏数据
    private void recordGameData(long gameId, String gameData) {
        GameData record = new GameData();
        record.setGameData(gameData);
        record.setGameId(gameId);
        gameDataMapper.insert(record);
    }

    //获取当前游戏所有下注
    private int getGameAllBet(int liveId) {
        int leftBet = GameCache.get_position_bet(liveId, GameUtil.leftCard);
        int centerBet = GameCache.get_position_bet(liveId, GameUtil.centerCard);
        int rightBet = GameCache.get_position_bet(liveId, GameUtil.rightCard);
        return leftBet + centerBet + rightBet;
    }

    /**
     * 结束游戏指令，IM推送
     *
     * @param liveId   直播id
     * @param userId   用户id
     * @param gameId   游戏id
     * @param gameData 游戏数据
     */
    private void sendEndGameRoomMsg(int liveId, String live_stream_id, int userId, int gameId, JSONObject gameData) {
        //获取主播的Ticket
        int anchorTicket = pocketInfoMapper.getAnchorTicketbyid(userId);
//
        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(C.ImField.countdown_time, GameUtil.BEGIN_CARD_TIME);
        jsonObject.put(C.ImField.game_id, gameId);
        jsonObject.put(C.ImField.live_id, liveId);
        jsonObject.put(C.ImField.anchor_tickets, anchorTicket);
        jsonObject.putAll(gameData);
        jsonObject.put(C.ImField.im_code, IMCode.game_end);
//        log.info("tag:" + "endGame_" + gameId + "_" + live_stream_id);
        WkImClient.sendRoomMsg(live_stream_id, jsonObject.toString(),1);
    }


    //获得下注用户
    private Set<String> getBetUser(int liveId) {
        Set<String> userSet = new HashSet<>();
        Map<String, String> leftUsers = GameCache.get_game_users(liveId, GameUtil.leftCard);
        for (String account : leftUsers.keySet()) {
            userSet.add(account);
        }
        Map<String, String> centerUsers = GameCache.get_game_users(liveId, GameUtil.centerCard);
        for (String account : centerUsers.keySet()) {
            userSet.add(account);
        }
        Map<String, String> rightUsers = GameCache.get_game_users(liveId, GameUtil.rightCard);
        for (String account : rightUsers.keySet()) {
            userSet.add(account);
        }
        return userSet;
    }

    //获取下注用户user_id
    private Map<String, Integer> getBetUserId(Set userAccountSet) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        if (userAccountSet.size() > 0) {
            List<AccountInfo> users = accountInfoMapper.batchSelectUsers(userAccountSet);
            for (AccountInfo info : users) {
                map.put(info.getAccount(), info.getId());
            }
        }
        return map;
    }


    //获取下注用户余额
    private Map<String, Long> getBetUserIdDiamond(Map userMap) {
        Map<String, Long> map = new HashMap<String, Long>();
        if (userMap.size() > 0) {
            List<PocketInfo> users = pocketInfoMapper.batchSelectUsersDiamond(userMap);
            for (PocketInfo info : users) {
                map.put(LibSysUtils.toString(info.getUserId()), LibSysUtils.toLong(info.getTotalDiamond()));
            }
        }
        return map;
    }


    /**
     * 取缓存的数据，计算account用户输赢
     *
     * @param liveId             liveId
     * @param account            account
     * @param wid                wid
     * @param gameType           gameType
     * @param commissionTypeRule 0抽->押中的下注；1抽-> 全部的押注
     * @param licenceRule        抽水比例
     * @param minBetRule         最小下注数
     * @param minWinRule         最小赢钱数
     * @return JSONObject
     */
    private JSONObject countBunko(int commissionTypeRule, int liveId, Commission commission, String account,
                                  int wid, int gameType, Double licenceRule, int minBetRule,
                                  int minWinRule) {
        JSONObject object = getBetAndWinNum(liveId, account, wid, gameType);

        commission(object, liveId, commission, gameType, licenceRule, minBetRule, minWinRule, commissionTypeRule);

        return object;
    }

    // 设置commission的值
    private void commission(JSONObject object, int liveId, Commission commission, int gameType,
                            Double licenceRule, int minBetRule, int minWinRule, int commissionTypeRule) {
        int myWinBet = object.optInt("win_bet");
        int myLoseBet = Math.abs(object.optInt("lose_bet"));
        int winMoney = object.optInt("win_money");
        int myBet = myWinBet + myLoseBet;
        commission.setWinNum(winMoney);
        commission.setAddTime(LibDateUtils.getLibDateTime());
        commission.setBetNum(myBet);
        commission.setGameType((byte) gameType);
        commission.setLiveId(liveId);

        // 计算抽水值
        int commissionValue = doCommission(commissionTypeRule, minBetRule, minWinRule, licenceRule,
                winMoney, myBet, myWinBet);

        commission.setCommission(commissionValue);

    }

    /**
     * 计算抽水
     *
     * @param commissionTypeRule 1 全部的押注都抽 else 抽中了的押注
     * @param minBetRule         抽水条件：最新押注
     * @param minWinRule         抽水条件：最新赢
     * @param licenceRule        抽水比例
     * @param winMoney           赢钱（负数为输）
     * @param myBet              总下注额
     * @param myWinBet           赢钱的下注额
     * @return
     */
    private int doCommission(int commissionTypeRule, int minBetRule, int minWinRule, Double licenceRule,
                             int winMoney, int myBet, int myWinBet) {
        int commissionValue = 0;
        // 赢钱大于设定值 并且 下注大于设定值 才抽水
        if (licenceRule > 0 && winMoney > minWinRule && myBet > minBetRule) {
            //规则说明：type等于0 抽->押中的下注，type等于1 抽->全部的押注; licence：抽水比例； minBet:下注大于minBet才抽水 minWin:赢钱大于minWin才抽水
            if (commissionTypeRule == 1) {
                // Type == 1 抽-> 全部的押注
                commissionValue += (new Double(myBet * licenceRule)).intValue();
            } else {
                // 抽->押中的下注
                commissionValue += (new Double(myWinBet * licenceRule)).intValue();
            }
        } else {
            // 不抽水
            commissionValue = 0;
        }

        return commissionValue;
    }

    // 给每个下注的用户发送游戏输赢信息
    private void sendPrivateGameMsg(JSONArray winPosition, int money, long my_diamonds, String betAccount, int gameId,
                                    int liveId, String anchorRole, String live_stream_id, int anchorUserId, int gameType,
                                    String anchorHeadPic, double api_version) {
        JSONObject myBetResult = new JSONObject();
        myBetResult.put(C.ImField.im_code, IMCode.bet_result);
        myBetResult.put(C.ImField.win_money, money);//输赢的钱
        myBetResult.put(C.ImField.win_money_position, winPosition);//输赢的钱
        myBetResult.put(C.ImField.my_diamonds, my_diamonds);//剩下的钱
        myBetResult.put(C.ImField.account, betAccount);
        myBetResult.put(C.ImField.game_id, gameId);
        myBetResult.put(C.ImField.live_id, liveId);
        //myBetResult.put(C.ImField.gift_info, getGiftInfo(money));
        WkImClient.sendPrivateMsg(betAccount, myBetResult.toString());

        IMPushUtil.sendGlobalMsgCard(liveId, live_stream_id, money, WKCache.getUserByAccount(betAccount, C.WKCacheUserField.nickname),
                WKCache.get_user(anchorUserId, C.WKCacheUserField.nickname), gameType, anchorHeadPic, LibSysUtils.toInt(anchorRole));
    }


    private JSONObject getBetAndWinNum(int liveId, String account, int wid, int gameType) {
        int myWinBet = 0;
        int myLoseBet = 0;
        int winMoney = 0;

        int leftBet = GameCache.get_game_user(liveId, account, GameUtil.leftCard); //下注
        if (wid == GameUtil.leftCardType) {
            myWinBet = myWinBet + leftBet;//计算赢的钱
        } else {
            myLoseBet = myLoseBet - leftBet;//计算输的下注
        }
        int centerBet = GameCache.get_game_user(liveId, account, GameUtil.centerCard); //下注
        if (wid == GameUtil.centerCardType) {
            myWinBet = myWinBet + centerBet;//计算赢的钱
        } else {
            myLoseBet = myLoseBet - centerBet;//计算输的下注
        }
        int rightBet = GameCache.get_game_user(liveId, account, GameUtil.rightCard);
        if (wid == GameUtil.rightCardType) {
            myWinBet = myWinBet + rightBet;//计算赢的钱
        } else {
            myLoseBet = myLoseBet - rightBet;//计算输的下注
        }
        switch (gameType) {
            case GameUtil.FRIED_GOLDEN: //扎金花
                if (myWinBet > 0) {
                    winMoney = myWinBet * 3;
                }
                if (myWinBet == 0 && myLoseBet < 0) {
                    winMoney = myLoseBet;
                }
                break;
            case GameUtil.TEXAS_HOLDEM:  //天天德州
                if (myWinBet > 0) {//赢得钱
                    if (wid == 2) {
                        winMoney = myWinBet * 22;
                    } else {
                        winMoney = myWinBet * 2;
                    }
                }
                if (myWinBet == 0 && myLoseBet < 0) {//输掉的钱
                    winMoney = myLoseBet;
                }
                break;
            case GameUtil.TAURUS_CARD: //牛牛
                if (myWinBet > 0) {
                    winMoney = myWinBet * 3;
                }
                if (myWinBet == 0 && myLoseBet < 0) {
                    winMoney = myLoseBet;
                }
                break;
            case GameUtil.CRAZY_RACING: //疯狂赛车
                if (myWinBet > 0) {
                    winMoney = myWinBet * 2;
                }
                if (myWinBet == 0 && myLoseBet < 0) {
                    winMoney = myLoseBet;
                }
                break;
            default:
                break;
        }

        JSONObject object = new JSONObject();
        object.put("win_bet", myWinBet); // 押注中了的betNum
        object.put("lose_bet", myLoseBet);// 押注不中的betNum
        object.put("win_money", winMoney);//输赢的钱 正表示赢，负数表示输
        return object;
    }

    /**
     * 竞猜榜
     *
     * @param liveId liveId
     * @return JSONObject
     */
    public JSONObject getBetList(int liveId) {
        Set<String> userSet = getBetUser(liveId);// 拿到下注者的account

        Map<String, Integer> map = new HashMap<String, Integer>();
        for (String account : userSet) {
            int total = getUserAllBet(account, liveId);
            map.put(account, total);
        }
        TreeMap<String, Integer> treeMap = SortUtil.sortMap(map);
        Iterator<Map.Entry<String, Integer>> it = treeMap.entrySet().iterator();
        Map.Entry<String, Integer> entry;
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        JSONObject item;
        while (it.hasNext()) {
            entry = it.next();
            item = new JSONObject();
            item.put("nickname", WKCache.getUserByAccount(entry.getKey(), C.WKCacheUserField.nickname));
            item.put("bet_num", entry.getValue());
            item.put("pic_head_low", WkUtil.combineUrl(WKCache.getUserByAccount(entry.getKey(), C.WKCacheUserField.avatar),UploadTypeEnum.AVATAR,true));
            array.add(item);
        }
        result.put("list", array);
        return result;
    }

}
