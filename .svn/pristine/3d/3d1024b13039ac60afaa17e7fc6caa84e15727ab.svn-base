package com.weking.service.system;

import com.weking.cache.WKCache;
import com.weking.core.C;
import com.weking.service.live.LiveService;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/10.
 */
@Service("robotService")
//@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class RobotService extends LibServiceBase {
    @Resource
    private LiveService liveService;

    private static volatile List<Map<String, Object>> _robots;

    private void get_robots() {
        if (_robots == null)
            _robots = this.getLibJdbcTemplate().queryForList(String.format("SELECT id as user_id,nickname,account,pichead_url,level from wk_user_info WHERE is_robot=1 LIMIT %d,%d", 0, 2000));
    }

    public void start(int live_id, String live_stream_id) {
        try {
            get_robots();
            JSONObject robotConfig = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.robot_config));
            int sleepTime = robotConfig.optInt("sleepTime", 5000);
            Thread.sleep(sleepTime);
            int size = _robots.size();
            if (size > 0) {
                String[] msgs = WKCache.get_system_cache("weking.lang.app.robot_msg").split(",");
                int start = LibSysUtils.getRandom(0, 1968);//获取起始位置
                int count = LibSysUtils.getRandom(19, 31);//获取开播的机器人数
                for (int i = 0; i < count; i++) {
                    Map<String, Object> map = _robots.get(start);
                    String nickname = LibSysUtils.toString(map.get("nickname"));
                    String account = LibSysUtils.toString(map.get("account"));
                    int user_id = LibSysUtils.toInt(map.get("user_id"));
                    int level = LibSysUtils.toInt(map.get("level"));
                    String avatar = LibSysUtils.toString(map.get("pichead_url"));
                    in_room(live_id, live_stream_id, user_id, account, avatar, nickname, level);
                    Thread.sleep(LibSysUtils.getRandom(1000, sleepTime));
                    if (i % 7 == 0) {//逢5发言
                        String msg = "hi";
                        if (msgs.length > 0) {
                            msg = msgs[LibSysUtils.getRandom(0, msgs.length - 1)];
                        }
                        push_message(live_id,live_stream_id, user_id, LibSysUtils.toString(account), nickname, "",level, msg);
                    }
                    start++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //进入房间
    public void in_room(int live_id, String live_stream_id, int user_id, String account, String avatar, String nickname, int level) {
        try {
            get_robots();
            if (LibSysUtils.isNullOrEmpty(account) && _robots != null && _robots.size() > 0) {
                int tick = LibSysUtils.getRandom(1, 11);
                if (tick % 2 == 0)//随机判断是否要进入机器人
                    return;
                Map<String, Object> map = _robots.get(LibSysUtils.getRandom(0, _robots.size() - 1));
                user_id = LibSysUtils.toInt(map.get("user_id"));
                level = LibSysUtils.toInt(map.get("level"));
                nickname = LibSysUtils.toString(map.get("nickname"));
                account = LibSysUtils.toString(map.get("account"));
                avatar = LibSysUtils.toString(map.get("pichead_url"));
            }
            liveService.enter(user_id, account, avatar, nickname, level, live_id, live_stream_id, "", true,1.0,"",0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void push_message(int live_id,String live_stream_id, int user_id, String account, String nickname, String head_url, int level, String msg) {
        try {
            get_robots();
            if (LibSysUtils.isNullOrEmpty(account) && _robots != null && _robots.size() > 0) {
                int tick = LibSysUtils.getRandom(1, 11);
                if (tick % 2 != 0)//随机判断是否要机器人发言
                    return;
                Map<String, Object> map = _robots.get(LibSysUtils.getRandom(0, _robots.size() - 1));
                nickname = LibSysUtils.toString(map.get("nickname"));
                account = LibSysUtils.toString(map.get("account"));
                level = LibSysUtils.toInt(map.get("level"));
            }
            if (LibSysUtils.isNullOrEmpty(msg)) {
                msg = "hi";
                String[] msgs = LibSysUtils.getLang("weking.lang.app.robot_msg").split(",");
                if (msgs.length > 0) {
                    msg = msgs[LibSysUtils.getRandom(0, msgs.length - 1)];
                }
            }
            liveService.sendMsg(user_id, account, nickname, head_url, level,live_id, live_stream_id, msg, false, "",1.0,true,0,"");

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public void push_click(int room_id, String live_stream_id) {

    }
}
