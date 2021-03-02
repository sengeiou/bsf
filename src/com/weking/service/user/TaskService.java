package com.weking.service.user;

import com.weking.cache.UserCacheInfo;
import com.weking.cache.WKCache;
import com.weking.core.*;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.activity.ActivityListMapper;
import com.weking.mapper.pocket.PocketInfoMapper;
import com.weking.mapper.pocket.UserGainMapper;
import com.weking.mapper.task.TaskCountsLogMapper;
import com.weking.mapper.task.TaskItemMapper;
import com.weking.mapper.task.TaskLogMapper;
import com.weking.model.activity.ActivityList;
import com.weking.model.pocket.PocketInfo;
import com.weking.model.pocket.UserGain;
import com.weking.model.task.TaskCountsLog;
import com.weking.model.task.TaskItem;
import com.weking.model.task.TaskLog;
import com.weking.service.digital.DigitalService;
import com.weking.service.system.SystemService;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service("taskService")
@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class TaskService {

    static Logger log = Logger.getLogger(TaskService.class);
    @Resource
    private PocketInfoMapper pocketInfoMapper;
    @Resource
    private TaskItemMapper taskItemMapper;
    @Resource
    private TaskLogMapper taskLogMapper;
    @Resource
    private LevelService levelService;
    @Resource
    private DigitalService digitalService;
    @Resource
    private SystemService systemService;
    @Resource
    private UserService userService;
    @Resource
    private AccountInfoMapper accountInfoMapper;
    @Resource
    private ActivityListMapper activityListMapper;
    @Resource
    private TaskCountsLogMapper taskCountsLogMapper;
    @Resource
    private UserGainMapper userGainMapper;


    //登录任务列表
    public JSONObject getLoginTaskList(int userId,String lang_code) {
        List<TaskItem> dailyLoginTask = taskItemMapper.getLoginTaskList();
        JSONArray jsonArray = new JSONArray();
        if (dailyLoginTask != null) {
            JSONObject jsonObject;
            int taskId = 0; //今天登录任务ID
            boolean todayTask = false; //今天
            TaskLog taskLog = taskLogMapper.selectLastLoginTaskLogByUserId(userId);
            long today = LibDateUtils.getLibDateTime("yyyyMMdd000000");
            if (taskLog != null) {
                if (taskLog.getAddTime() >= today) { //表示今天已领取
                    todayTask = true;
                    taskId = taskLog.getTaskId();
                } else if (taskLog.getAddTime() >= DateUtils.getYesterday()) { //表示昨天已领
                    if (taskLog.getTaskId() < 14) {
                        taskId = taskLog.getTaskId() + 1;
                    } else {
                        taskId = 8;
                    }
                    //自动领取登录奖励
                    TaskItem taskItem = taskItemMapper.selectByPrimaryKey(taskId);
                    recordReward(userId,lang_code, taskItem.getId(), taskItem.getRewardCoin(), taskItem.getRewardType(), 0, taskItem.getTaskType(), "");
                }
            }
            int size = dailyLoginTask.size();
            for (int i = 0; i < size; i++) {
                jsonObject = new JSONObject();
                TaskItem item = dailyLoginTask.get(i);
                jsonObject.put("task_id", item.getId());
                jsonObject.put("title", item.getTitle());
                jsonObject.put("describe", item.getDescribeTask());
                jsonObject.put("reward_num", item.getRewardCoin());
                jsonObject.put("task_num", item.getTaskNumber());
                jsonObject.put("pic_url", WkUtil.combineUrl(item.getPicUrl(), UploadTypeEnum.AVATAR, false));
                if (taskId == 0 && i == 0) { //无领取记录可领取第一天
                    recordReward(userId,lang_code, item.getId(), item.getRewardCoin(), item.getRewardType(), 0, item.getTaskType(), "");
                    jsonObject.put("task_state", 3);//当前可领
                } else if (taskId > item.getId()) {
                    jsonObject.put("task_state", 1);//已经领
                } else if (taskId == item.getId()) {
                    if (todayTask) {
                        jsonObject.put("task_state", 1);
                    } else {
                        jsonObject.put("task_state", 3);
                    }
                } else {
                    jsonObject.put("task_state", 2);//未领
                }
                jsonArray.add(jsonObject);
            }
        }
        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);
        object.put("list", jsonArray);
        return object;
    }

    //完成分享直播间
    public JSONObject shareTask(int user_id, int live_id,String lang_code) {
        int count = LibSysUtils.toInt(taskLogMapper.selectShareTaskCountByUserId(user_id, 1, LibDateUtils.getLibDateTime("yyyyMMdd000000")));
        TaskItem taskItem = taskItemMapper.selectByPrimaryKey(1);
        if (taskItem != null) {
            if (taskItem.getTaskNumber() > count) { //今日未记录分享任务则记录
                recordReward(user_id,lang_code, 1, taskItem.getRewardCoin(), taskItem.getRewardType(), live_id, taskItem.getTaskType(), "");
            }
        }
        String live_stream_id = WKCache.get_room(live_id, "live_stream_id");
        UserCacheInfo userCacheInfo = WKCache.get_user(user_id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("im_code", IMCode.sys_msg);
        jsonObject.put("account", userCacheInfo.getAccount());
        jsonObject.put("msg", LibProperties.getLanguage(userCacheInfo.getLang_code(), "weking.lang.app.share_live"));
        jsonObject.put("pic_head_low", WkUtil.combineUrl(userCacheInfo.getAvatar(), UploadTypeEnum.AVATAR, true));
        jsonObject.put("nickname", userCacheInfo.getNickname());
        jsonObject.put("live_id", live_id);
        jsonObject.put("level", userCacheInfo.getLevel());
        WkImClient.sendRoomMsg(live_stream_id, jsonObject.toString(), 1);
        return LibSysUtils.getResultJSON(ResultCode.success);
    }


    //获取直播间内每日奖励
    public JSONObject getLiveRoomAward(int user_id, int live_id, String lang_code) {
        JSONObject result = LibSysUtils.getResultJSON(3);
        List<TaskItem> list = taskItemMapper.getListByTaskType(6);
        if (list.size() > 0) { //有任务
            int r = LibSysUtils.getRandom(1, 100);
            if (r % 2 == 0) { //50%的概率会出现任务
                long bdate = LibDateUtils.getLibDateTime("yyyyMMdd000000");
                long edate = LibDateUtils.getLibDateTime("yyyyMMdd235959");
                List<TaskLog> logList = taskLogMapper.selectLogByType(user_id, 6, bdate, edate);
                if (logList.size() < list.size()) {
                    result.put("code", ResultCode.success);
                    result.put("task_id", list.get(logList.size()).getId());
                    result.put("url", WKCache.get_system_cache("live.room_award_url"));//跳转的url，为空时前端不调整
                    result.put("img_url", WKCache.get_system_cache("live.room_award_imgurl"));
                    result.put("title", list.get(logList.size()).getTitle());
                    JSONArray banner_result = systemService.getAdvertisement(6, "", lang_code).optJSONArray("adv_list");
                    if (banner_result.size() >= 1) {
                        JSONObject jsonObject;
                        if (banner_result.size() == 1){
                           jsonObject = banner_result.getJSONObject(0);
                        }else {
                           jsonObject = banner_result.getJSONObject(new Random().nextInt(banner_result.size() - 1));
                        }
                        if (!LibSysUtils.isNullOrEmpty(jsonObject.optString("ad_unit_id"))) {
                            result.put("ad_unit_id", jsonObject.optString("ad_unit_id"));   //admob 广告id
                        }else {
                            if (!LibSysUtils.isNullOrEmpty(jsonObject.optString("link_url"))){
                                result.put("url", jsonObject.optString("link_url"));//跳转的url，为空时前端不调整
                            }
                        }
                    }
                }
            }
        }
//        System.out.println(result.toString());
        return result;
    }

    //领取直播间内每日奖励
    public JSONObject dailyReward(int user_id, int live_id, int task_id,String lang_code) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        long bdate = LibDateUtils.getLibDateTime("yyyyMMdd000000");
        long edate = LibDateUtils.getLibDateTime("yyyyMMdd235959");
        TaskLog log = taskLogMapper.selectLogByTaskId(user_id, task_id, bdate, edate);
        if (log == null) {
            TaskItem item = taskItemMapper.selectByPrimaryKey(task_id);
            recordReward(user_id,lang_code, task_id, item.getRewardCoin(), item.getRewardType(), live_id, item.getTaskType(), "");
            result.put("reward_emo", 0);
            if (item.getRewardType() == 1) {
                result.put("reward_emo", item.getRewardCoin());
            }
            result.put("describe_task", item.getDescribeTask());
        } else {
            result = LibSysUtils.getResultJSON(3, "您已領取獎勵");

        }
        return result;
    }

    //记录奖励
    private int recordReward(int userId,String lang_code, int taskId, int rewardCoin, int rewardType, int live_id, int task_type, String share_type) {
        int re = recordTaskLog(userId, taskId, rewardCoin, rewardType, live_id, task_type, share_type);
        if (re > 0) {
            if (rewardType == 1) {//奖励EMO
                //赠送emo后 获得新的比值
                Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(LibSysUtils.toInt(userId), "ratio"));//现有的比值
                Integer totalDiamond=0;
                BigDecimal newRatio;
                if(ratio>0){
                    PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(LibSysUtils.toInt(userId));
                    if(pocketInfo!=null) {
                        totalDiamond = pocketInfo.getTotalDiamond();//现有的emo
                    }
                    if(totalDiamond>0) {
                        double v = (rewardCoin + totalDiamond) / (totalDiamond / ratio);
                        newRatio = new BigDecimal(v).setScale(8, BigDecimal.ROUND_HALF_UP);
                    }else {
                        newRatio = new BigDecimal(0).setScale(8, BigDecimal.ROUND_HALF_UP);
                    }
                    accountInfoMapper.updateRatioByUserid(newRatio,LibSysUtils.toInt(userId));
                   // WKCache.add_user(LibSysUtils.toInt(userId), "ratio", LibSysUtils.toString(newRatio));
                }
                pocketInfoMapper.increaseDiamondByUserId(userId, rewardCoin);
            } else if (rewardType == 2) {//奖励经验
                levelService.putExp(userId, rewardCoin, 4,0);
            } else if (rewardType == 3) {//奖励SCA
                digitalService.OptWallect(userId,lang_code,0, "SCA", new BigDecimal(rewardCoin), (short) 4, "", "獎勵", "",0);
            }
        }
        return re;
    }

    /**
     * 记录任务领取日志
     *
     * @param userId     用户
     * @param taskId     任务编号
     * @param rewardCoin 奖励金额
     * @param rewardType 奖励类型 1emo 2经验,3sca
     * @param live_id    直播编号
     * @param task_type  任务类型
     * @param share_type 分享类型
     * @return
     */
    public int recordTaskLog(int userId, int taskId, int rewardCoin, int rewardType, int live_id, int task_type, String share_type) {
        TaskLog record = new TaskLog();
        record.setUserId(userId);
        record.setAddTime(LibDateUtils.getLibDateTime());
        record.setRewardCoin(rewardCoin);
        record.setTaskId((short) taskId);
        record.setRewardType(rewardType);
        record.setLive_id(live_id);
        record.setTask_type(task_type);
        record.setShare_type(share_type);
        return taskLogMapper.insert(record);
    }


    //获取所有活动
    public JSONObject getActivityList(int userId,String lang_code,int type) {
        List<ActivityList> activityList = activityListMapper.selectAllActivity();
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        switch (type){
            case 0:
                activityList = activityListMapper.selectAllActivity();
                break;
            case 1:
                long time = LibDateUtils.getLibDateTime();
                activityList = activityListMapper.selectUnclosedActivity(time);
                break;
            default:
                break;
        }

        for (ActivityList activity : activityList) {
            JSONObject tmp = new JSONObject();
            tmp.put("activity_title", activity.getActivityTitle());
            if(("").equals(activity.getJumpUrl())||activity.getJumpUrl()==null) {
                tmp.put("jump_url", null);
            }else {
                tmp.put("jump_url", activity.getJumpUrl());
            }
            tmp.put("start_time", LibSysUtils.toString(activity.getStartTime()));
            tmp.put("end_time", LibSysUtils.toString(activity.getEndTime()));
            tmp.put("activity_id", activity.getId());
            tmp.put("image_url", WkUtil.combineUrl(activity.getImageUrl(), UploadTypeEnum.ADV, false));
            array.add(tmp);
            }
          result.put("activity_list", array);
        return result;
    }



    //直播间签到天数
    public JSONObject getLiveSinInDay(int userId,String lang_code,int live_id) {

        JSONObject object = LibSysUtils.getResultJSON(ResultCode.success);

        if (live_id>0) {
            int taskId = 0; //今天登录任务ID
            boolean flg = false; //今天
            TaskItem taskItem ;
            TaskLog taskLog = taskLogMapper.selectLastLiveSinInByUserId(userId);
            long today = LibDateUtils.getLibDateTime("yyyyMMdd000000");
            if (taskLog != null) {
                if (taskLog.getAddTime() >= today) { //表示今天已领取
                    taskId = taskLog.getTaskId();
                    taskItem = taskItemMapper.selectByPrimaryKey(taskId);
                    flg = true; //今天
                } else if (taskLog.getAddTime() >= DateUtils.getYesterday()) { //表示昨天已领
                    if (taskLog.getTaskId() < 49) {
                        taskId = taskLog.getTaskId() + 1;
                    } else {
                        taskId = 49;
                    }
                    //自动领取
                    taskItem = taskItemMapper.selectByPrimaryKey(taskId);
                    recordReward(userId, lang_code, taskItem.getId(), taskItem.getRewardCoin(), taskItem.getRewardType(), live_id, taskItem.getTaskType(), "");
                }else {
                    //自动领取
                    taskItem = taskItemMapper.selectByPrimaryKey(19);
                    recordReward(userId, lang_code, taskItem.getId(), taskItem.getRewardCoin(), taskItem.getRewardType(), live_id, taskItem.getTaskType(), "");
                }

            } else {
                //自动领取
                taskItem = taskItemMapper.selectByPrimaryKey(19);
                recordReward(userId, lang_code, taskItem.getId(), taskItem.getRewardCoin(), taskItem.getRewardType(), live_id, taskItem.getTaskType(), "");
            }
            object.put("sinIn_day", LibSysUtils.toInt(taskItem.getTitle()));
            object.put("is_SinIn", flg);
        }
        return object;
    }


    //获取所有每日任务
    public JSONObject getEverydayTaskList(int userId,String lang_code) {
        List<TaskItem> taskList = taskItemMapper.getListByTaskType(9);
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        for (TaskItem taskItem:taskList){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title",taskItem.getTitle());
            jsonObject.put("describe_task",taskItem.getDescribeTask());
            jsonObject.put("task_number",taskItem.getTaskNumber());//需要多少
            jsonObject.put("reward_coin",taskItem.getRewardCoin());//完成后奖励
            if (taskItem.getPicUrl()!=null ) {
                if (taskItem.getPicUrl().contains("http")){
                    jsonObject.put("pic_url", taskItem.getPicUrl());//任務圖標地址
                }
                jsonObject.put("pic_url", WkUtil.combineUrl(taskItem.getPicUrl(), UploadTypeEnum.TASk, false));//任務圖標地址
            }
            Integer taskId = taskItem.getId();//任务Id
            jsonObject.put("task_id",taskId);
            if (taskId==C.TaskId.buy_emo){
                jsonObject.put("jump",0);
            }else if (taskId==C.TaskId.send){
                jsonObject.put("jump",1);//首页
            }else {
                jsonObject.put("jump",1);//首页
            }

            long day_time = LibDateUtils.getLibDateTime("yyyyMMdd050000");
            long libDateTime = LibDateUtils.getLibDateTime();
            if (libDateTime<day_time){
                day_time=LibSysUtils.toLong(DateUtils.BeforeNowByDay(1)+"050000");
            }
            TaskCountsLog taskCountsLog = taskCountsLogMapper.selectTaskCountsLogByIdAndTime(userId, taskId, day_time);
            if (taskCountsLog!=null){
                jsonObject.put("count",taskCountsLog.getCount());//已完成多少
                jsonObject.put("task_state",LibSysUtils.toInt(taskCountsLog.getTaskState()));//状态 0 待完成 1已完成 2已领取
            }else {
                jsonObject.put("count",0);//已完成多少
                jsonObject.put("task_state",0);//状态 0 待完成 1已完成 2已领取
            }
            array.add(jsonObject);
        }
        result.put("content", LibProperties.getLanguage(lang_code, "weking.lang.task.everyday.content"));
        result.put("task_list", array);
        return result;
    }

    //每日任務操作
    public void dayTaskHandle(int taskId, int userId, int count){

        TaskItem taskItem = taskItemMapper.selectByPrimaryKey(taskId);
        if (taskItem==null){
            return;
        }
        long day_time = LibDateUtils.getLibDateTime("yyyyMMdd050000");
        long time = LibDateUtils.getLibDateTime();
        //先判断今天是否已有完成任务
        if (time<day_time){
            day_time=LibSysUtils.toLong(DateUtils.BeforeNowByDay(1)+"050000");
        }
        int state = 0;
        TaskCountsLog taskCountsLog = taskCountsLogMapper.selectTaskCountsLogByIdAndTime(userId, taskId, day_time);

        // 对应的任务完成条数为1
        if (taskItem.getTaskNumber() <= count) {
            state = 1;  // 已完成
        }

        if (taskCountsLog==null){
            //如果无就新增
            addTaskCountsLog(userId,state,taskId,count);
        }else {
            //先判断该任务今天是否已完成
            // 当日这条任务已经完成或等待领取中
            if (taskItem.getTaskNumber() <= taskCountsLog.getCount() || taskCountsLog.getTaskState() != 0) {
                return;
            }
                int completeCount = taskCountsLog.getCount() + count;
                // 还未完成，次数+1后完成
                if (taskItem.getTaskNumber() <= completeCount) {
                    if (completeCount >= taskItem.getTaskNumber()) {
                        completeCount = taskItem.getTaskNumber() - taskCountsLog.getCount();
                    }
                    state = 1;
                    taskCountsLogMapper.increaseCountsByPrimaryKey(taskCountsLog.getId(), time, completeCount);
                    taskCountsLogMapper.updateTaskStateByKey(taskCountsLog.getId(), state);
                } else if (taskCountsLog.getCount() < taskItem.getTaskNumber()) {        // 次数+1后还未完成
                    int j = taskCountsLogMapper.increaseCountsByPrimaryKey(taskCountsLog.getId(), time, count);
                    if (j <= 0) {
                        addTaskCountsLog(userId, state, taskId, count);
                    }
                }

        }

    }

    // 往任务明细日志表中插入一条记录
    private void addTaskCountsLog(int userId, int state, int taskId, int count) {
        long time = LibDateUtils.getLibDateTime();
        // 插入一条记录
        TaskCountsLog taskCountsLog = new TaskCountsLog();
        taskCountsLog.setUserId(userId);
        taskCountsLog.setTaskId(taskId);
        taskCountsLog.setCount(count);
        taskCountsLog.setAddTime(time);
        taskCountsLog.setTaskState((byte) state);
      taskCountsLogMapper.insertSelective(taskCountsLog);
    }

    // 领取每日任务奖励
    public JSONObject getEverydayTaskAward(int userId,String lang_code,int task_id) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        TaskItem taskItem = taskItemMapper.selectByPrimaryKey(task_id);
        long day_time = LibDateUtils.getLibDateTime("yyyyMMdd050000");
        long time = LibDateUtils.getLibDateTime();
        //先判断今天是否已有完成任务
        if (time<day_time){
            day_time=LibSysUtils.toLong(DateUtils.BeforeNowByDay(1)+"050000");
        }
        TaskCountsLog taskCountsLog = taskCountsLogMapper.selectTaskCountsLogByIdAndTime(userId, task_id, day_time);
        if (taskCountsLog!=null&&taskItem!=null&&taskCountsLog.getTaskState()==1){
            //赠送emo后 获得新的比值
            Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(LibSysUtils.toInt(userId), "ratio"));//现有的比值
            Integer totalDiamond=0;
            BigDecimal newRatio;
            if(ratio>0){
                PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(LibSysUtils.toInt(userId));
                if(pocketInfo!=null) {
                    totalDiamond = pocketInfo.getTotalDiamond()+pocketInfo.getFreeDiamond();//现有的emo
                }
                if(totalDiamond>0) {
                    double v = (taskItem.getRewardCoin() + totalDiamond) / (totalDiamond / ratio);
                    newRatio = new BigDecimal(v).setScale(8, BigDecimal.ROUND_HALF_UP);
                }else {
                    newRatio = new BigDecimal(0).setScale(8, BigDecimal.ROUND_HALF_UP);
                }
                accountInfoMapper.updateRatioByUserid(newRatio,LibSysUtils.toInt(userId));
            }
            int i = pocketInfoMapper.increaseFreeDiamondByUserId(userId, taskItem.getRewardCoin());
            if (i>0){
                //更新状态
                taskCountsLogMapper.updateTaskStateByKey(taskCountsLog.getId(), 2);


                //收入存入新的表单中
                UserGain gain = UserGain.getGain(userId,C.UserGainType.task, taskItem.getRewardCoin(), taskItem.getId());
                userGainMapper.insertSelective(gain);
            }

        }else  if (taskCountsLog!=null&&taskItem!=null&&taskCountsLog.getTaskState()==2){
            result = LibSysUtils.getResultJSON(ResultCode.reward_coin_receive, LibProperties.getLanguage(lang_code, "weking.lang.task.award.getError"));
        }else {
            result = LibSysUtils.getResultJSON(ResultCode.receive_error, LibProperties.getLanguage(lang_code, "weking.lang.task.everyday.getError"));

        }
        return result;
    }


}
