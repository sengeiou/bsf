package com.weking.service.user;

import com.weking.cache.WKCache;
import com.weking.core.C;
import com.weking.core.ResultCode;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.level.ExpLogMapper;
import com.weking.mapper.level.LevelMapper;
import com.weking.mapper.vip.VipExpLogMapper;
import com.weking.mapper.vip.VipLevelMapper;
import com.weking.model.level.ExpLog;
import com.weking.model.level.Level;
import com.weking.model.vip.VipExpLog;
import com.weking.model.vip.VipLevel;
import com.wekingframework.comm.LibServiceBase;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 等级
 */
@Service("levelService")
@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class LevelService extends LibServiceBase {

    @Resource
    private LevelMapper levelMapper;
    @Resource
    private AccountInfoMapper accountInfoMapper;
    @Resource
    private ExpLogMapper expLogMapper;
    @Resource
    private VipExpLogMapper vipExpLogMapper;
    @Resource
    private VipLevelMapper vipLevelMapper;

    private static volatile List<Integer> _list;
    private static volatile List<Integer> _list_vip;

    /**
     * 增加经验
     *
     * @param userId 用户userId
     * @param num    经验转换数（货币）
     * @param type   经验类型
     */
    public void putExp(int userId, int num, int type,int anchorId) {
        ExpLog record = new ExpLog();
        Double anchorRate = LibSysUtils.toDouble(WKCache.get_system_cache(C.WKSystemCacheField.guard_anchor_rate),0.5);
        int exp_num = 0;
        int vip_exp= 0;
        int anchor_exp= 0;
        switch (type) {
            case 1: //送礼物
                exp_num = num;
                vip_exp = num;
                anchor_exp = num;
                break;
            case 2://购买付费观看门票
                exp_num = num;
                vip_exp = num;
                break;
            case 3:
                break;
            case 4: //签到
                exp_num = num;
                break;
            case 5: //购买守护
                exp_num = (int)Math.floor(num * anchorRate);
                vip_exp = (int)Math.floor(num * anchorRate);
                anchor_exp = (int)Math.floor(num * anchorRate);
                break;
            default:
                break;
        }
        //用户经验
        if (exp_num > 0) {
            JSONObject object = calculateLevel(userId, exp_num);
            if (object.getInt("code") == 0) {
                record.setExpType((byte) type);
                record.setUserId(userId);
                record.setAddTime(LibDateUtils.getLibDateTime());
                record.setExpNum(exp_num);
                record.setUserType((byte) 0);//0 是用户经验
                int re = expLogMapper.insert(record);
                if (re > 0) {
                    int level = object.getInt("level");
                    exp_num = object.getInt("cur_exp");
                    accountInfoMapper.updateExperience(userId, exp_num, level);
                }
            }
        }

        //主播经验
        if (anchor_exp > 0&&anchorId>0) {
            JSONObject object = calculateAnchorLevel(anchorId, anchor_exp);
            if (object.getInt("code") == 0) {
                record.setExpType((byte) type);
                record.setUserId(anchorId);
                record.setAddTime(LibDateUtils.getLibDateTime());
                record.setExpNum(anchor_exp);
                record.setUserType((byte) 1);//0 是用户经验 1是用户经验
                int re = expLogMapper.insert(record);
                if (re > 0) {
                    int level = object.getInt("anchor_level");
                    anchor_exp = object.getInt("cur_exp");
                    accountInfoMapper.updateAnchorExperience(anchorId, anchor_exp, level);
                }
            }
        }

        if(vip_exp>0&&LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.user_vip_exp_switch), false)){
            if (type!=4) {
                JSONObject obj = calculateLevelVip(userId, vip_exp);
                if (obj.getInt("code") == 0) {
                    VipExpLog vipExpLog = new VipExpLog();
                    vipExpLog.setExpType((byte) type);
                    vipExpLog.setUserId(userId);
                    vipExpLog.setAddTime(LibDateUtils.getLibDateTime());
                    vipExpLog.setExpNum(vip_exp);
                    int re = vipExpLogMapper.insert(vipExpLog);
                    if (re > 0) {
                        int level = obj.getInt("level");
                        vip_exp = obj.getInt("cur_exp");
                        accountInfoMapper.updateVipExperience(userId, vip_exp, level);
                    }

                }
            }
        }
    }

    //我的等级
    public JSONObject myLevel(int userId) {
        return calculateLevel(userId, 0);
    }
    //我的vip等级
    public JSONObject myVipLevel(int userId) {
        return calculateLevelVip(userId, 0);
    }

    /**
     * 计算等级
     *
     * @param experience 增加经验
     */
    private JSONObject calculateLevel(int userId, int experience) {
        getLevelList(); //初始化等级列表
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        List<String> userLevel = WKCache.get_user(userId, "level", "experience");
        int level = LibSysUtils.toInt(userLevel.get(0));
        int curExp = LibSysUtils.toInt(userLevel.get(1));
        int downExp; //下一级所需经验
        int size = _list.size();
        if (size > level) { //当前等级小于最高等级
            int[] exp = upgrade(userId, level, curExp, experience);
            curExp = exp[0];
            downExp = exp[1];
            level = exp[2];
        } else {
            downExp = 0;
            curExp = curExp + experience;
        }
        WKCache.add_user(userId, "experience", LibSysUtils.toString(curExp));
        object.put("level", level);
        object.put("cur_exp", curExp);
        object.put("down_exp", downExp);
        return object;
    }

    //升级
    private int[] upgrade(int userId, int level, int curExp, int experience) {
        int[] exp = new int[3];
        int nextExp = _list.get(level);//下一级所需经验
        curExp = curExp + experience; //当前经验
        int downExp = nextExp - curExp; //距离下一级升级经验
        if (downExp <= 0) { //经验足够升级,等级增加
            level++;
            if (_list.size() == level) { //正好升到最高级
                exp[0] = curExp;
                exp[1] = 0;
                exp[2] = level;
            } else {
                exp = upgrade(userId, level, curExp, 0);
            }
            WKCache.add_user(userId, "level", LibSysUtils.toString(level));
        } else {
            exp[0] = curExp;
            exp[1] = downExp;
            exp[2] = level;
        }
        return exp;
    }

    //等级列表
    private void getLevelList() {
        if (_list == null) {
            List<Level> levelList = levelMapper.selectByAllLevel();
            _list = new ArrayList<>();
            for (Level info : levelList) {
                System.out.println(info.getExperience());
                _list.add(info.getExperience());
            }
        }
    }

    public void clearList() {
        _list = null;
    }

    /**
     * 计算等级  vip
     *
     * @param experience 增加经验
     */
    private JSONObject calculateLevelVip(int userId, int experience) {
        getLevelListVip(); //初始化vip等级列表
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        List<String> userLevel = WKCache.get_user(userId, "vip_level", "vip_experience");
        int level = LibSysUtils.toInt(userLevel.get(0));
        int curExp = LibSysUtils.toInt(userLevel.get(1));
        int downExp; //下一级所需经验
        int size = _list_vip.size();
        if (size > level) { //当前等级小于最高等级
            int[] exp = upgradeVip(userId, level, curExp, experience);
            curExp = exp[0];
            downExp = exp[1];
            level = exp[2];
        } else {
            downExp = 0;
            curExp = curExp + experience;
        }
        WKCache.add_user(userId, "vip_experience", LibSysUtils.toString(curExp));
        object.put("level", level);
        object.put("cur_exp", curExp);
        object.put("down_exp", downExp);
        return object;
    }

    //升级
    private int[] upgradeVip(int userId, int level, int curExp, int experience) {
        int[] exp = new int[3];
        int nextExp = _list_vip.get(level);//下一级所需经验
        curExp = curExp + experience; //当前经验
        int downExp = nextExp - curExp; //距离下一级升级经验
        if (downExp <= 0) { //经验足够升级,等级增加
            level++;
            if (_list_vip.size() == level) { //正好升到最高级
                exp[0] = curExp;
                exp[1] = 0;
                exp[2] = level;
            } else {
                exp = upgradeVip(userId, level, curExp, 0);
            }
            WKCache.add_user(userId, "vip_level", LibSysUtils.toString(level));
        } else {
            exp[0] = curExp;
            exp[1] = downExp;
            exp[2] = level;
        }
        return exp;
    }

    //等级列表
    private void getLevelListVip() {
        if (_list_vip == null) {
            List<VipLevel> levelList = vipLevelMapper.selectByAllVipLevel();
            _list_vip = new ArrayList<>();
            for (VipLevel info : levelList) {
                System.out.println(info.getExperience());
                _list_vip.add(info.getExperience());
            }
        }
    }

    public void clearListVip() {
        _list_vip = null;
    }



    /**
     * 计算等级 主播
     *
     * @param anchor_experience 增加经验
     */
    private JSONObject calculateAnchorLevel(int anchorId, int anchor_experience) {
        getLevelList(); //初始化等级列表
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        List<String> anchorLevel = WKCache.get_user(anchorId, "anchor_level", "anchor_experience");
        int level = LibSysUtils.toInt(anchorLevel.get(0));
        int curExp = LibSysUtils.toInt(anchorLevel.get(1));
        int downExp; //下一级所需经验
        int size = _list.size();
        if (size > level) { //当前等级小于最高等级
            int[] exp = upgradeAnchor(anchorId, level, curExp, anchor_experience);
            curExp = exp[0];
            downExp = exp[1];
            level = exp[2];
        } else {
            downExp = 0;
            curExp = curExp + anchor_experience;
        }
        WKCache.add_user(anchorId, "anchor_experience", LibSysUtils.toString(curExp));
        object.put("anchor_level", level);
        object.put("cur_exp", curExp);
        object.put("down_exp", downExp);
        return object;
    }

    //升级
    private int[] upgradeAnchor(int anchorId, int level, int curExp, int anchor_experience) {
        int[] exp = new int[3];
        int nextExp = _list.get(level);//下一级所需经验
        curExp = curExp + anchor_experience; //当前经验
        int downExp = nextExp - curExp; //距离下一级升级经验
        if (downExp <= 0) { //经验足够升级,等级增加
            level++;
            if (_list.size() == level) { //正好升到最高级
                exp[0] = curExp;
                exp[1] = 0;
                exp[2] = level;
            } else {
                exp = upgradeAnchor(anchorId, level, curExp, 0);
            }
            WKCache.add_user(anchorId, "anchor_level", LibSysUtils.toString(level));
        } else {
            exp[0] = curExp;
            exp[1] = downExp;
            exp[2] = level;
        }
        return exp;
    }

}
