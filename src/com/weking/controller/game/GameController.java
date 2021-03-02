package com.weking.controller.game;

import com.weking.cache.WKCache;
import com.weking.core.C;
import com.weking.service.game.GameService;
import com.wekingframework.comm.LibControllerBase;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/game")
public class GameController extends LibControllerBase {

    //static Logger log = Logger.getLogger(GameController.class);

    @Resource
    private GameService gameService;

    /**
     * 开火，射击,钓鱼;
     *
     * @param request  请求
     * @param response 响应
     */
    @RequestMapping("/getTargetList")
    public void getTargetList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
//            int liveId = getParameter(request, "live_id", 0);
            object.put("ids", gameService.getTargetList());
        }
        out(response, object);
    }

    /**
     * 竞猜榜
     */
    @RequestMapping("/getBetList")
    public void getBetList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        if (object.getInt("code") == 0) {
            int liveId = LibSysUtils.toInt(getParameter(request, "live_id"));

            object = gameService.getBetList(liveId);
        }
        out(response, object);
    }

    /**
     * 开火，射击,钓鱼;
     *
     * @param request  请求
     * @param response 响应
     */
    @RequestMapping("/fire")
    public void fire(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int liveId = getParameter(request, "live_id", 0);
            boolean hit = getParameter(request, "hit", false);
            int id = getParameter(request, "id", 0);
            int capital = getParameter(request, "capital", 0);
            int user_id = object.optInt(C.WKCacheUserField.user_id);
            String nickname = object.optString(C.WKCacheUserField.nickname);
            String avatar = object.optString(C.WKCacheUserField.avatar);
            String lang_code = object.optString("lang_code");
            object = gameService.fire(liveId, user_id,nickname, avatar, hit, capital, id, lang_code);
        }
        out(response, object);
    }

    @RequestMapping("/join")
    public void join(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int liveId = getParameter(request, "live_id", 0);
            String account = object.optString(C.WKCacheUserField.account);
            String result = gameService.join(liveId, account);
            out(response, result);
        } else {
            out(response, object);
        }
    }

    @RequestMapping("/switch")
    public void switchGame(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int liveId = getParameter(request, "live_id", 0);
            int game_type = getParameter(request, "game_type", 0);
            String langCode = object.optString("lang_code");
            String result = gameService.switchGame(userId,liveId, game_type,langCode);
            out(response, result);
        } else {
            out(response, object);
        }
    }

    @RequestMapping("/getGameResultData")
    public void getGameResultData(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int liveId = getParameter(request, "live_id", 0);
            object = gameService.getGameResultData(userId, liveId);
        }
        out(response, object);

    }
    /**
     * 房间游戏列表
     */
    @RequestMapping("/gameList")
    public void gameList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int liveId = getParameter(request, "live_id", 0);
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 20);
            object = gameService.getGameList(liveId, index, count);
        }
        //log.info("游戏结果"+object.toString());
        out(response, object);
    }

    /**
     * 下注
     */
    @RequestMapping("/bet")
    public void bet(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int liveId = getParameter(request, "live_id", 0);
            int positionId = getParameter(request, "position_id", 0);
            int bet_num = getParameter(request, "bet_num", 0);
            double api_version = getParameter(request, C.RequestParam.api_version, 1.0);
            int userId = object.optInt(C.WKCacheUserField.user_id);
            String account = object.getString("account");
            String lang_code = object.optString("lang_code");
            object = gameService.bet(userId, account, liveId, bet_num, positionId, lang_code, api_version);
        }
        //log.info("bet_info:"+object.toString());
        out(response, object);
    }

    /**
     * 开始游戏
     */
    @RequestMapping("/startGame")
    public void startGame(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int liveId = getParameter(request, "live_id", 0);
            int userId = object.optInt(C.WKCacheUserField.user_id);
            int gameType = getParameter(request, "game_type", 0);
            double api_version = getParameter(request, C.RequestParam.api_version, 1.0);
            String objectStr = gameService.startGame(userId, liveId, gameType, api_version);
            out(response, objectStr);
        } else {
            out(response, object);
        }
    }

    /**
     * 开始游戏
     */
    @RequestMapping("/endGame")
    public void endGame(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WKCache.check_token(access_token);
        if (object.optInt("code") == 0) {
            int liveId = getParameter(request, "live_id", 0);
            int gameType = getParameter(request, "game_type", 0);
            double api_version = getParameter(request, C.RequestParam.api_version, 1.0);
            int userId = object.optInt(C.WKCacheUserField.user_id);
            String objectStr = gameService.endGamePoker(userId, liveId,gameType, api_version,null);
            out(response, objectStr);
        } else {
            out(response, object);
        }
    }

}
