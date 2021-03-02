package com.weking.service.post;

import com.weking.cache.WKCache;
import com.weking.core.*;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.core.sensitive.WordFilter;
import com.weking.mapper.account.AccountInfoMapper;
import com.weking.mapper.account.UserBillMapper;
import com.weking.mapper.advertisement.AdvertisementMapper;
import com.weking.mapper.follow.FollowInfoMapper;
import com.weking.mapper.pocket.PocketInfoMapper;
import com.weking.mapper.post.*;
import com.weking.model.account.AccountInfo;
import com.weking.model.account.UserBill;
import com.weking.model.advertisement.Advertisement;
import com.weking.model.pocket.PocketInfo;
import com.weking.model.post.PostCommentInfo;
import com.weking.model.post.PostInfo;
import com.weking.model.post.PostOperation;
import com.weking.model.post.PostViewLog;
import com.weking.service.digital.DigitalService;
import com.weking.service.system.MsgService;
import com.weking.service.user.UserService;
import com.wekingframework.comm.LibServiceBase;
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
import java.util.*;

/**
 * 动态
 * Created by Administrator on 2017/10/22.
 */
@Service("postService")
public class PostService extends LibServiceBase {
    private static Logger log = Logger.getLogger(PostService.class);
    @Resource
    private PostMapper postMapper;
    @Resource
    private PostCommentMapper postCommentMapper;
    @Resource
    private PostMetaMapper postMetaMapper;
    @Resource
    private AccountInfoMapper accountMapper;
    @Resource
    private DigitalService digitalService;
    @Resource
    private UserService userService;
    @Resource
    private MsgService msgService;
    @Resource
    private FollowInfoMapper followInfoMapper;
    @Resource
    private PostViewLogMapper postViewLogMapper;
    @Resource
    private UserBillMapper userBillMapper;
    @Resource
    private PocketInfoMapper pocketInfoMapper;
    @Resource
    private AccountInfoMapper accountInfoMapper;
    @Resource
    private AdvertisementMapper advertisementMapper;
    @Resource
    private PostOperationMapper postOperationMapper;

    /**
     * 获取动态列表
     *
     * @param user_id   获取者userid
     * @param account   如果要获取自己或固定某人的动态则传自己会对应某人的account，account为空则获取所有关注者的动态
     * @param index
     * @param count
     * @param lang_code
     * @return
     */
    public JSONObject getPostList(int user_id, String account, int index, int count, String lang_code) {
        return getHotPost(user_id, account, 0, index, count, lang_code, 0, 0);
//        List<PostInfo> postInfoList = postMapper.getPostList(user_id, account, LibDateUtils.getLibDateTime(), index, count);//获取关注者的动态
//        List<Integer> userFollowList = null;
//        int type = 0;
//        if (LibSysUtils.isNullOrEmpty(account)) {
//            if (postInfoList.size() > 0) {
//                PostInfo postInfo = postInfoList.get(0);
//                long post_time = postInfo.getPost_date();
//                long diff = LibDateUtils.diff(post_time, LibDateUtils.getLibDateTime(), LibDateUtils.DateDiff.DAY);
//                if (diff > 1) {//如果关注的人的动态是两天前的则取最新的显示
//                    postInfoList = postMapper.getNewPostList(LibDateUtils.getLibDateTime(), 0, index, count);//获取最新动态
//                }
//            } else {//没有关注的人的动态则显示所有最新的
//                postInfoList = postMapper.getNewPostList(LibDateUtils.getLibDateTime(), 0, index, count);//获取最新动态
//            }
//            userFollowList = followInfoMapper.getUserFollowerList(user_id);
//        }else {
//            type = 1;
//        }
//        JSONObject result = internalGetPostList(postInfoList, userFollowList,false, user_id,type, lang_code,index);
//        return result;
    }

    public JSONObject getFollowPost(int user_id, String account, int post_id, int index, int count, String lang_code, double api_version) {
        List<PostInfo> postInfoList;
        int type = 0;
        List<Integer> userFollowList = followInfoMapper.getUserFollowerList(user_id);
        if (LibSysUtils.isNullOrEmpty(account)) {
            type = 1;
//            postInfoList = postMapper.getPostList(user_id, account, LibDateUtils.getLibDateTime(), index, count); //获取关注者的动态
            if (index == 0) {
                String arrayString = WKCache.get_user_follow_post_list(user_id);
                if (!LibSysUtils.isNullOrEmpty(arrayString)) {
                    JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
                    JSONArray array = JSONArray.fromObject(arrayString);
                    result.put("list", array);
                    return result;
                }
            }
            if (userFollowList.size() == 0) {
                userFollowList.add(user_id);
            }
            if (post_id != 0) {
                index = 0;
            }
            postInfoList = postMapper.getFollowPostList(user_id, post_id, userFollowList, LibDateUtils.getLibDateTime(), index, count); //获取关注者的动态
            userFollowList = null;
        } else {
            int other_id = LibSysUtils.toInt(userService.getUserFieldByAccount(account, "user_id"), 0);
            if (post_id != 0) {
                index = 0;
            }
            postInfoList = postMapper.getNewPostList(LibDateUtils.getLibDateTime(), other_id, post_id, index, count); //获取最新动态
            userFollowList = followInfoMapper.getUserFollowerList(user_id);
        }
        JSONObject result = internalGetPostList(postInfoList, userFollowList, false, user_id, type, lang_code, post_id, api_version);
        return result;
    }

    /**
     * 获取最新或热门列表
     *
     * @param user_id
     * @param account
     * @param post_id
     * @param index
     * @param count
     * @param lang_code
     * @param api_version
     * @param type        类型 0最新 1热门
     * @return
     */
    public JSONObject getHotPost(int user_id, String account, int post_id, int index, int count, String lang_code, double api_version, int type) {
        int other_id = 0;
        if (!LibSysUtils.isNullOrEmpty(account)) {
            other_id = LibSysUtils.toInt(userService.getUserFieldByAccount(account, "user_id"), 0);
        }
        if (type == 0) {
            if (post_id != 0) {
                index = 0;
            }
            List<PostInfo> postInfoList = postMapper.getNewPostList(LibDateUtils.getLibDateTime(), other_id, post_id, index, count); //获取最新动态
            List<Integer> userFollowList = followInfoMapper.getUserFollowerList(user_id);
            JSONObject result = internalGetPostList(postInfoList, userFollowList, false, user_id, 0, lang_code, post_id, api_version);
            return result;
        } else {
            JSONObject result = getPopularPost(user_id, post_id, index, count, lang_code, api_version);
            return result;
        }

    }

    public JSONObject getPost(int user_id, int post_id, String lang_code) {
        List<PostInfo> postInfoList = postMapper.getPost(user_id, post_id); //获取最新动态
        List<Integer> userFollowList = followInfoMapper.getUserFollowerList(user_id);
        JSONObject result = internalGetPostList(postInfoList, userFollowList, true, user_id, 0, lang_code, 0, 0);
        return result;
    }

    /**
     * @param postInfoList   动态list
     * @param userFollowList 关注用户list
     * @param isDetail
     * @param user_id        用户 id
     * @param type           0 最新  1关注
     * @param lang_code
     * @return
     */
    private JSONObject internalGetPostList(List<PostInfo> postInfoList, List<Integer> userFollowList, boolean isDetail, int user_id, int type,
                                           String lang_code, int post_id, double api_version) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        Set<String> userHiddenPost = WKCache.getUserHiddenPost(user_id);
        for (PostInfo postInfo : postInfoList) {
         /*   boolean ifAdd = true;
            if (userHiddenPost != null && userHiddenPost.size() > 0 && userHiddenPost.contains(LibSysUtils.toString(postInfo.getId()))) {
                ifAdd = false;
            }
            if (ifAdd) {*/
                JSONObject temp = new JSONObject();
                temp.put("post_id", postInfo.getId());

                Map<String, String> userMap = userService.getUserInfoByUserId(postInfo.getUser_id(), "account", "avatar", "level", "nickname", "sex");
                if (userMap != null) {
                    temp.put("account", LibSysUtils.toString(userMap.get("account")));
                    temp.put("avatar", WkUtil.combineUrl(userMap.get("avatar"), UploadTypeEnum.AVATAR, true));
                    temp.put("level", LibSysUtils.toInt(userMap.get("level")));
                    temp.put("nickname", LibSysUtils.toString(userMap.get("nickname")));
                    temp.put("sex", LibSysUtils.toInt(userMap.get("sex")));
                }

                temp.put("post_content", postInfo.getPost_content());
                temp.put("type", postInfo.getType());
                temp.put("is_like", LibSysUtils.toBoolean(postOperationMapper.isLike(postInfo.getId(), user_id)));
                temp.put("dislike", LibSysUtils.toBoolean(postOperationMapper.dislike(postInfo.getId(), user_id)));
                temp.put("sca_reward", postInfo.getSca_reward().setScale(3, BigDecimal.ROUND_HALF_UP));//sca奖励的数字
                String Ogimage = "";
                if (postInfo.getType() == 2) {//视频
                    temp.put("post_video", getPostVideoInfo(postInfo.getMeta_value()));
                } else {
                    JSONArray imageInfo = getPostImageInfo(postInfo.getMeta_value());
                    temp.put("post_images", imageInfo);
                    if (imageInfo.size() > 0) {
                        Ogimage = imageInfo.optJSONObject(0).optString("low_url");
                    }
                }
                temp.put("post_time", WkUtil.format(LibDateUtils.getDateTimeTick(postInfo.getPost_date(), LibDateUtils.getLibDateTime()), lang_code));
                temp.put("comment_count", postInfo.getComment_count());
                temp.put("like_count", postInfo.getLike_count());
                temp.put("dislike_count", postInfo.getDislike_count());
                temp.put("share_count", postInfo.getShare_count());
                temp.put("balance_date", postInfo.getBalance_date());
                temp.put("balance_status", LibSysUtils.toBoolean(postInfo.getBalance_status()));//是否结算SCA奖励
                temp.put("city", postInfo.getCity());
                temp.put("location", postInfo.getLocation());
                int follow_state = 0;
                if (userFollowList == null || type == 1) {
                    follow_state = 1;
                } else {
                    if (userFollowList.contains(postInfo.getUser_id())) {
                        follow_state = 1;
                    }
                }
                temp.put("follow_state", follow_state);
                if (isDetail) {
                    if (LibSysUtils.isNullOrEmpty(Ogimage)) {
                        Ogimage = WkUtil.combineUrl(userMap.get("avatar"), UploadTypeEnum.AVATAR, true);
                    }
                    temp.put("Ogtitle", String.format(LibProperties.getLanguage(lang_code, "weking.lang.post.sharetitle"), LibSysUtils.toString(userMap.get("nickname"))));
                    temp.put("Ogimage", Ogimage);
                    temp.put("OgsiteName", "愛絲蜜AppsMe");
                    temp.put("Ogdescription", postInfo.getPost_content());
                }
                // 动态列表展示3条评论
                JSONArray jsonArray = new JSONArray();
                if (postInfo.getComment_count() >= 1) {
                    List<PostCommentInfo> postCommentInfo = postCommentMapper.selectPostComments(postInfo.getUser_id(), postInfo.getId());
                    for (PostCommentInfo info : postCommentInfo) {
                        JSONObject jsonObject = new JSONObject();
                        Map<String, String> commenterMap = userService.getUserInfoByUserId(info.getComment_user_id(), "account", "avatar", "nickname");
                        if (commenterMap != null) {
                            jsonObject.put("comment_id", info.getId());
                            jsonObject.put("account", commenterMap.get("account"));
                            jsonObject.put("avatar", WkUtil.combineUrl(commenterMap.get("avatar"), UploadTypeEnum.AVATAR, true));
                            jsonObject.put("nickname", commenterMap.get("nickname"));
                            jsonObject.put("post_content", info.getContent());
                            jsonArray.add(jsonObject);
                        }
                    }
                }
                temp.put("comment_list", jsonArray);
                array.add(temp);
            //}
        }
        // 插入admob广告
        if (api_version >= api_version && SystemConstant.post_adv_switch) {
            List<Advertisement> adv_list = advertisementMapper.selectByType(7, "");
            if (adv_list.size() > 0) {
                int countNum = array.size();
                int frequency = SystemConstant.frequency;
                int times = countNum / frequency;
                for (int i = 1; i <= times; i++) {
                    Advertisement advertisement;
                    if (adv_list.size() == 1) {
                        advertisement = adv_list.get(0);
                    } else {
                        advertisement = adv_list.get(new Random().nextInt(adv_list.size() - 1));
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", advertisement.getType());
                    jsonObject.put("ad_unit_id", LibSysUtils.isNullOrEmpty(advertisement.getAd_unit_id()) ? "ca-app-pub-3940256099942544/2247696110" : advertisement.getAd_unit_id());
                    if (countNum >= frequency && countNum > 3 && frequency > 3) {
                        int location = new Random().nextInt(frequency / 4) + (frequency / 4 * 3) + frequency * (i - 1);
                        if (location > 3) {
                            array.add(location, jsonObject);
                        }
                    }
                }
            }
        }

        result.put("list", array);
        if (type == 1 && post_id == 0 && array.size() > 0) {
            WKCache.add_user_follow_post_list(user_id, array.toString());
        }
//        System.out.println(result);
        return result;
    }


    /**
     * 获取推荐的动态
     *
     * @param userId
     * @param index
     * @param count
     * @param lang_code
     * @return
     */
    public JSONObject getRecommendPost(int userId, int index, int count, String lang_code, int post_id, double api_version) {
         JSONObject object = WKCache.get_recommend_post_list(userId,index,count);
         if(object != null){
             return object;
         }
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
        // 获取推荐列表缓存
        //Set<String> recIds = WKCache.getRecommendPostList(index, count);
//        Set<String> recUserIds = WKCache.getRecommendPostUserList(index, count);
      Set<String> recIds ;
        if (api_version>=4.4) {
            //recIds = WKCache.getRecommendPostList(index, count);
        }else {
            recIds = WKCache.getRecommendPostUserList(index, count);
            if (recIds.size() > 0) {
                if (api_version<4.4) {
                    for (String user_Id : recIds) {
                        String postId = WKCache.getUserRecommendPost(user_Id);
                        JSONObject postInfoJSON = getPostInfoJSON(null, LibSysUtils.toInt(postId), userId, lang_code);
                        array.add(postInfoJSON);
                    }
                }
            }

        }
        if (array.size()==0) {
            List<PostInfo> postInfoList = postMapper.getRecommendPostList(LibDateUtils.getLibDateTime(), index, count); //获取最新动态
            if (postInfoList.size() > 0) {
                for (PostInfo postInfo : postInfoList) {
                    JSONObject temp = getPostInfoJSON(postInfo, postInfo.getId(), userId, lang_code);
                    array.add(temp);
                }
            }
        }

        // 插入admob广告
        if (api_version >= SystemConstant.api_version && SystemConstant.post_adv_switch) {
            List<Advertisement> adv_list = advertisementMapper.selectByType(7, "");
            if (adv_list.size() > 0) {
                int countNum = array.size();
                int frequency = SystemConstant.frequency;
                int times = countNum / frequency;
                for (int i = 1; i <= times; i++) {
                    Advertisement advertisement;
                    if (adv_list.size() == 1) {
                        advertisement = adv_list.get(0);
                    } else {
                        advertisement = adv_list.get(new Random().nextInt(adv_list.size() - 1));
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", advertisement.getType());
                    jsonObject.put("ad_unit_id", LibSysUtils.isNullOrEmpty(advertisement.getAd_unit_id()) ? "ca-app-pub-3940256099942544/2247696110" : advertisement.getAd_unit_id());
                    if (countNum >= frequency && countNum > 3 && frequency > 3) {
                        int location = new Random().nextInt(frequency / 4) + (frequency / 4 * 3) + frequency * (i - 1);
                        if (location > 3) {
                            array.add(location, jsonObject);
                        }
                    }
                }
            }
        }


        result.put("list", array);
//        System.out.println(result);
        WKCache.set_recommend_post_list(userId,index,count,result);
        return result;

    }


    /**
     * 获取热门的动态
     *
     * @param userId
     * @param index
     * @param count
     * @param lang_code
     * @return
     */
    public JSONObject getPopularPost(int userId, int post_id, int index, int count, String lang_code, double api_version) {
        List<PostInfo> postInfoList = new ArrayList<>();
        // 获取推荐列表缓存
        Set<String> recIds = WKCache.getPopularPostList(index, count);
        if (recIds.size() > 0) {
            for (String postId : recIds) {
                PostInfo postInfo = getPostInfo(LibSysUtils.toInt(postId));
                if (postInfo != null) {
                    postInfoList.add(postInfo);
                }
            }
        }
        List<Integer> userFollowList = followInfoMapper.getUserFollowerList(userId);
        JSONObject result = internalGetPostList(postInfoList, userFollowList, true, userId, 0, lang_code, post_id, api_version);
//        System.out.println(result);
        return result;
    }

    /**
     * 获取动态详情
     *
     * @param userId
     * @param post_id
     * @param lang_code
     * @return
     */
    public JSONObject getPostInfo(int userId, int post_id, String lang_code) {
        return getPostInfoJSON(null, post_id, userId, lang_code);
    }

    public JSONObject getPostInfoJSON(PostInfo postInfo, int post_id, int user_id, String lang_code) {
        JSONObject temp = LibSysUtils.getResultJSON(ResultCode.success);
        temp.put("post_id", post_id);
        if (postInfo == null) {
            postInfo = getPostInfo(post_id);
        }
        if (postInfo != null) {
            if (postInfo.getSorts() > 0) { //缓存推荐的动态信息
                WKCache.addPostInfo(postInfo);
                WKCache.addRecommendPostList(postInfo.getId(), postInfo.getSorts());
               /* WKCache.addRecommendPostUserList(postInfo.getUser_id(), postInfo.getSorts());
                WKCache.addUserRecommendPost(postInfo.getUser_id(), postInfo.getId());*/
            }

            Map<String, String> userMap = userService.getUserInfoByUserId(postInfo.getUser_id(), "account", "avatar", "level", "nickname", "sex");
            if (userMap != null) {
                temp.put("account", LibSysUtils.toString(userMap.get("account")));
                temp.put("nickname", LibSysUtils.toString(userMap.get("nickname")));
                temp.put("avatar", WkUtil.combineUrl(userMap.get("avatar"), UploadTypeEnum.AVATAR, true));
                temp.put("level", LibSysUtils.toInt(userMap.get("level")));
                temp.put("sex", LibSysUtils.toInt(userMap.get("sex")));
            }

            temp.put("post_content", postInfo.getPost_content());
            temp.put("poster_id", postInfo.getUser_id());
            temp.put("type", postInfo.getType());
            if (postInfo.getExpiryTime()<99999999999999L&&postInfo.getExpiryTime()>0){
                temp.put("is_expiry_post", true);
            }else {
                temp.put("is_expiry_post", false);
            }
            temp.put("is_like", LibSysUtils.toBoolean(postOperationMapper.isLike(postInfo.getId(), user_id)));
            temp.put("dislike", LibSysUtils.toBoolean(postOperationMapper.dislike(postInfo.getId(), user_id)));
            temp.put("sca_reward", postInfo.getSca_reward().setScale(3, BigDecimal.ROUND_HALF_UP));//sca奖励的数字
            String Ogimage = "";
            if (postInfo.getType() == 1) {//图片
                JSONArray array = getPostImageInfo(postInfo.getMeta_value());
                temp.put("post_images", array);
                if (array.size() > 0) {
                    Ogimage = array.optJSONObject(0).optString("low_url");
                }
            } else {
                temp.put("post_video", getPostVideoInfo(postInfo.getMeta_value()));
            }
            boolean is_follow = followInfoMapper.verifyIsFollowed(user_id, postInfo.getUser_id()) > 0; //判断我是否已经关注他
            temp.put("follow_state", is_follow ? 1 : 0);
            temp.put("post_time", WkUtil.format(LibDateUtils.getDateTimeTick(postInfo.getPost_date(), LibDateUtils.getLibDateTime()), lang_code));
            temp.put("comment_count", postInfo.getComment_count());
            temp.put("like_count", postInfo.getLike_count());
            temp.put("dislike_count", postInfo.getDislike_count());
            temp.put("share_count", postInfo.getShare_count());
            temp.put("balance_date", postInfo.getBalance_date());
            temp.put("balance_status", LibSysUtils.toBoolean(postInfo.getBalance_status()));//是否结算SCA奖励
            temp.put("city", postInfo.getCity());
            temp.put("location", postInfo.getLocation());
            if (LibSysUtils.isNullOrEmpty(Ogimage)) {
                Ogimage = WkUtil.combineUrl(userMap.get("avatar"), UploadTypeEnum.AVATAR, true);
            }
            temp.put("Ogtitle", String.format(LibProperties.getLanguage(lang_code, "weking.lang.post.sharetitle"), LibSysUtils.toString(userMap.get("nickname"))));
            temp.put("Ogimage", Ogimage);
            temp.put("OgsiteName", "愛絲蜜AppsMe");
            temp.put("Ogdescription", postInfo.getPost_content());
            // 动态列表展示3条评论
            JSONArray jsonArray = new JSONArray();
            if (postInfo.getComment_count() >= 1) {
                List<PostCommentInfo> postCommentInfo = postCommentMapper.selectPostComments(postInfo.getUser_id(), postInfo.getId());
                for (PostCommentInfo info : postCommentInfo) {
                    JSONObject jsonObject = new JSONObject();
                    Map<String, String> commenterMap = userService.getUserInfoByUserId(info.getComment_user_id(), "account", "avatar", "nickname");
                    if (commenterMap != null) {
                        jsonObject.put("comment_id", info.getId());
                        jsonObject.put("account", commenterMap.get("account"));
                        jsonObject.put("avatar", WkUtil.combineUrl(commenterMap.get("avatar"), UploadTypeEnum.AVATAR, true));
                        jsonObject.put("nickname", commenterMap.get("nickname"));
                        jsonObject.put("post_content", info.getContent());
                        jsonArray.add(jsonObject);
                    }
                }
            }
            temp.put("comment_list", jsonArray);
        }

        return temp;
    }

    public PostInfo getPostInfo(int post_id) {
        PostInfo postInfo = WKCache.getPostInfo(post_id);
        if (postInfo == null) {
            postInfo = postMapper.selectByPrimaryKey(post_id);
        }
        return postInfo;
    }

    private JSONArray getPostImageInfo(String postImage) {
        JSONArray images = new JSONArray();
        if (!LibSysUtils.isNullOrEmpty(postImage)) {
            JSONArray t_images = JSONArray.fromObject(postImage);
            if (t_images.size() > 0) {
                JSONObject obj;
                String url;
                for (int i = 0; i < t_images.size(); i++) {
                    obj = t_images.getJSONObject(i);
                    url = obj.optString("url");
                    obj.put("url", WkUtil.combineUrl(url, UploadTypeEnum.POST, false));
                    obj.put("low_url", WkUtil.combineUrl(url, UploadTypeEnum.POST, true));
                    images.add(obj);
                }
            }
        }
        return images;
    }

    private JSONArray getPostVideoInfo(String postImage) {
        JSONArray jsonArray;
        JSONObject obj = new JSONObject();
        if (!LibSysUtils.isNullOrEmpty(postImage)) {
            jsonArray = JSONArray.fromObject(postImage);
            if (jsonArray.size() != 0) {
                obj = jsonArray.getJSONObject(0);
                String url = obj.optString("url");
                obj.put("url", WkUtil.combineUrl(url, UploadTypeEnum.POST, false));
                obj.put("low_url", WkUtil.combineUrl(url, UploadTypeEnum.POST, true));
                obj.put("video_url", WkUtil.combineUrl(obj.optString("video_url"), UploadTypeEnum.VIDEO, false));
            }
        }
        JSONArray arr = new JSONArray();
        arr.add(obj);
        return arr;
    }

    /**
     * 新增动态
     *
     * @param user_id
     * @param content
     * @param images
     * @param city
     * @param longitude
     * @param latitude
     * @param publish_code 邀请发布码
     * @return
     */
    @Transactional
    public JSONObject submit(int user_id, String account, String nickname, String lang_code, int type, String content, String images, String city, String longitude, String latitude, int expiryTime, String publish_code, int is_guide) {
//        System.out.println("发帖！");
        if (WKCache.isLockoutUser(user_id)) {  // 被封锁
            String msg = LibSysUtils.toString(WKCache.getLockoutUserCache(user_id));
            return LibSysUtils.getResultJSON(ResultCode.post_lockout, msg);
        }
        String users = WKCache.get_system_cache(C.WKSystemCacheField.post_no_limit_users);
        Set<String> noLimitUsers = new HashSet<>();
        if (!LibSysUtils.isNullOrEmpty(users)) {
            String[] userArray = users.split(",");
            if (userArray != null && userArray.length > 0) {
                for (String temp : userArray) {
                    noLimitUsers.add(temp);
                }
            }
        }
        if (!noLimitUsers.contains(account)) {
            long last_post_time = WKCache.get_user_last_post_time(user_id);
            if (last_post_time > 0) {
                long post_time_interval = LibSysUtils.toLong(WKCache.get_system_cache(C.WKSystemCacheField.post_time_interval));
                if (LibDateUtils.getDateTimeTick(last_post_time, LibDateUtils.getLibDateTime()) <= (post_time_interval * 1000)) {
                    return LibSysUtils.getResultJSON(ResultCode.post_frequently, LibProperties.getLanguage(lang_code, "weking.lang.post.frequently"));
                }
            }
        }

        AccountInfo user = accountMapper.selectByPrimaryKey(user_id);
        int getAnchor_level = user.getAnchor_level();
        int official = user.getIs_official();
        if (getAnchor_level==0||official==0) {
            if (expiryTime != 0) {
                return LibSysUtils.getResultJSON(ResultCode.expiryTime_anchor_post, LibProperties.getLanguage(lang_code, "weking.lang.expiry.time.anchor.post"));
            }
        }



        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);

        PostInfo postInfo = new PostInfo();
        postInfo.setUser_id(user_id);
        postInfo.setPost_content(content);
        postInfo.setCity(city);
        postInfo.setPost_date(LibDateUtils.getLibDateTime());
        postInfo.setType((byte) type);
        postInfo.setMeta_key(type == 1 ? "post_images" : "post_video");
        postInfo.setMeta_value(images);
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE, LibSysUtils.toInt(WKCache.get_system_cache("weking.balanceDate")));
        postInfo.setBalance_date(LibDateUtils.dateTime2LibDateTime(ca.getTime()));//发帖的7天后做结算
        if (expiryTime != 0)
            postInfo.setExpiryTime(LibSysUtils.toLong(WkUtil.futureTime(expiryTime)));
        else
            postInfo.setExpiryTime(99999999999999L);//如果没有设置动态失效时间，则设置为用不过期
        if (!LibSysUtils.isNullOrEmpty(longitude))
            postInfo.setLocation(latitude + "," + longitude);
        int post_hots = LibSysUtils.toInt(userService.getUserInfoByUserId(user_id, "post_hots"));
        postInfo.setSorts(post_hots);
        postMapper.insertSelective(postInfo);
        if (postInfo.getId() > 0) {
            if (post_hots > 0) { //推荐加入缓存
                WKCache.addPostInfo(postInfo);
               /* if (expiryTime!=0){
                    WKCache.addExpireRecommendPostList(postInfo.getId(), post_hots);
                }else {
                    WKCache.addRecommendPostList(postInfo.getId(), post_hots);
                }*/
                /*WKCache.addRecommendPostUserList(user_id, post_hots);
                WKCache.addUserRecommendPost(user_id, postInfo.getId());*/
            }
            result.put("post_id", postInfo.getId());

            if (is_guide == 1) {
                // 第一次进入引导用户用一张照片挖矿，po成功立即获得EMO
                int guide_post_reward = LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.guide_post_reward), 0);
                if (guide_post_reward > 0) {
                //赠送emo后 获得新的比值
                    Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(LibSysUtils.toInt(user_id), "ratio"));//现有的比值
                    Integer totalDiamond=0;
                    BigDecimal newRatio;
                    if(ratio>0){
                        PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(LibSysUtils.toInt(user_id));
                        if(pocketInfo!=null) {
                            totalDiamond = pocketInfo.getTotalDiamond();//现有的emo
                        }
                        if(totalDiamond>0) {
                            double v = (guide_post_reward + totalDiamond) / (totalDiamond / ratio);
                            newRatio = new BigDecimal(v).setScale(8, BigDecimal.ROUND_HALF_UP);
                        }else {
                            newRatio = new BigDecimal(0).setScale(8, BigDecimal.ROUND_HALF_UP);
                        }
                        accountInfoMapper.updateRatioByUserid(newRatio,LibSysUtils.toInt(user_id));
                    }
                    //增加用户货币
                   /*  pocketInfoMapper.increaseDiamondByUserId(user_id, guide_post_reward);
                   UserBill userBill = UserBill.getBill(user_id, guide_post_reward, 0, postInfo.getId(), C.UserBillType.GUIDE_POST);
                    userBillMapper.insert(userBill);*/
                }
            }

            if (!LibSysUtils.isNullOrEmpty(publish_code)) {
                //  邀请成功一个获得10个sca，朋友也可以得到5个sca
                AccountInfo accountInfo = accountInfoMapper.selectByAccountId(publish_code);
                if (accountInfo == null) {
                    result.put("beinvite_msg", LibProperties.getLanguage(lang_code, "weking.lang.post.beinvite.share.success"));
                    return result;
                }
                JSONObject postLikeJSON = JSONObject.fromObject(WKCache.getUserPostLikeInfoCache(accountInfo.getId()));
                boolean if_reward = postLikeJSON.optBoolean("if_reward");  // 是否奖励
                if (accountInfo.getId() == user_id) {
                    result.put("beinvite_msg", LibProperties.getLanguage(lang_code, "weking.lang.post.invite.own.noreward"));
                } else {
                    JSONObject postLikeConfig = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.post_like_share_config));
                    if (!if_reward && postLikeConfig.optBoolean("on_off")) {
                        postLikeJSON.put("if_reward", true);
                        WKCache.addUserPostLikeInfoCache(accountInfo.getId(), postLikeJSON.toString());

                        double m_reward = postLikeConfig.optDouble("m_reward");
                        double f_reward = postLikeConfig.optDouble("f_reward");
                        if (m_reward > 0) {
//                            BigDecimal m_value = new BigDecimal(m_reward).setScale(3, BigDecimal.ROUND_HALF_UP);
//                            String inviter_msg = String.format(LibProperties.getLanguage(accountInfo.getLangCode(), "weking.lang.post.invite.share.success"), nickname, m_reward);
//                            digitalService.OptWallect(accountInfo.getId(), accountInfo.getLangCode(), postInfo.getId(), "SCA", m_value, (short) 4, LibSysUtils.getRandomNum(16), "邀请朋友发布动态成功奖励", inviter_msg);
//                            UserBill inviterBill = UserBill.getBill(accountInfo.getId(), m_reward, 1, postInfo.getId(), C.UserBillType.INVITE_POST);
//                            userBillMapper.insert(inviterBill);
                            //赠送emo后 获得新的比值
                            Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(LibSysUtils.toInt(user_id), "ratio"));//现有的比值
                            Integer totalDiamond=0;
                            BigDecimal newRatio;
                            if(ratio>0){
                                PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(LibSysUtils.toInt(user_id));
                                if(pocketInfo!=null) {
                                    totalDiamond = pocketInfo.getTotalDiamond();//现有的emo
                                }
                                if(totalDiamond>0) {
                                    double v = (m_reward + totalDiamond) / (totalDiamond / ratio);
                                    newRatio = new BigDecimal(v).setScale(8, BigDecimal.ROUND_HALF_UP);
                                }else {
                                    newRatio = new BigDecimal(0).setScale(8, BigDecimal.ROUND_HALF_UP);
                                }
                                accountInfoMapper.updateRatioByUserid(newRatio,LibSysUtils.toInt(user_id));
                                //WKCache.add_user(LibSysUtils.toInt(user_id), "ratio", LibSysUtils.toString(newRatio));
                            }
                            // 增加用户货币
                           /*  pocketInfoMapper.increaseDiamondByUserId(accountInfo.getId(), (int) m_reward);
                           UserBill userBill = UserBill.getBill(accountInfo.getId(), (int) m_reward, 0, postInfo.getId(), C.UserBillType.INVITE_POST);
                            userBillMapper.insert(userBill);*/
                        }
                        if (f_reward > 0) {
//                            BigDecimal f_value = new BigDecimal(f_reward).setScale(3, BigDecimal.ROUND_HALF_UP);
//                            String beinviter_msg = String.format(LibProperties.getLanguage(accountInfo.getLangCode(), "weking.lang.post.beinvite.share.success"), accountInfo.getNickname(), f_reward);
//                            digitalService.OptWallect(user_id, lang_code, postInfo.getId(), "SCA", f_value, (short) 4, LibSysUtils.getRandomNum(16), "被朋友邀请发布动态成功奖励", beinviter_msg);
//                            UserBill beinviterBill = UserBill.getBill(user_id, f_reward, 1, postInfo.getId(), C.UserBillType.BEINVITE_POST);
//                            userBillMapper.insert(beinviterBill);
                            //赠送emo后 获得新的比值
                            Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(LibSysUtils.toInt(user_id), "ratio"));//现有的比值
                            Integer totalDiamond=0;
                            BigDecimal newRatio;
                            if(ratio>0){
                                PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(LibSysUtils.toInt(user_id));
                                if(pocketInfo!=null) {
                                    totalDiamond = pocketInfo.getTotalDiamond();//现有的emo
                                }
                                if(totalDiamond>0) {
                                    double v = (m_reward + totalDiamond) / (totalDiamond / ratio);
                                    newRatio = new BigDecimal(v).setScale(8, BigDecimal.ROUND_HALF_UP);
                                }else {
                                    newRatio = new BigDecimal(0).setScale(8, BigDecimal.ROUND_HALF_UP);
                                }
                                accountInfoMapper.updateRatioByUserid(newRatio,LibSysUtils.toInt(user_id));
                                //WKCache.add_user(LibSysUtils.toInt(user_id), "ratio", LibSysUtils.toString(newRatio));
                            }
                            // 增加用户货币
                           /* pocketInfoMapper.increaseDiamondByUserId(user_id, (int) f_reward);
                            UserBill userBill = UserBill.getBill(user_id, (int) f_reward, 0, postInfo.getId(), C.UserBillType.BEINVITE_POST);
                            userBillMapper.insert(userBill);*/
                        }

                        result.put("beinvite_msg", String.format(LibProperties.getLanguage(lang_code, "weking.lang.post.beinvite.share.success"), accountInfo.getNickname(), f_reward));
                        result.put("reward_amount", f_reward);
                    }
                }
            }
            // TODO  发文奖励更换为EMO

            // 发布动态时间缓存
            WKCache.add_user_post_submit_time(user_id);
            // 记录当日奖励
            WKCache.addUserTodayPostOperate(user_id, "post");

            return result;
        } else
            return LibSysUtils.getResultJSON(ResultCode.system_error);
    }

    /**
     * 删除动态
     *
     * @param user_id
     * @param post_id
     * @return
     */
    public JSONObject delPost(int user_id, int post_id) {
        postMapper.deleteByPrimaryKey(post_id);
        postMetaMapper.deleteByPostId(post_id);
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 评论
     *
     * @param user_id
     * @param nickname
     * @param post_id
     * @param reply_id
     * @param content
     * @param lang_code
     * @return
     */
    public JSONObject comment(int user_id, String nickname, int post_id, int reply_id, String content, String lang_code) {

        JSONObject result = LibSysUtils.getResultJSON(ResultCode.post_comment_repeated);
        result.put("tip_title", LibProperties.getLanguage(lang_code, "post.comment.repeated.tips.title"));
        if (WordFilter.doFilter(content, content.length())) {
//            System.out.println("垃圾评论");
            result.put("tip_content", LibProperties.getLanguage(lang_code, "post.comment.spam.tips.content"));
            return result;
        }

        Set<String> comments = WKCache.getUserPostComments(user_id);
        if (comments != null && comments.size() > 0) {
            result.put("tip_title", LibProperties.getLanguage(lang_code, "post.comment.repeated.tips.title"));
            result.put("tip_content", LibProperties.getLanguage(lang_code, "post.comment.repeated.tips.content"));
            for (String info : comments) {
                if (WkUtil.getSimilarityRatio(info, content) >= 0.6) {
//                    System.out.println("重复评论");
                    return result;
                }
            }
        }

        PostCommentInfo postCommentInfo = new PostCommentInfo();
        postCommentInfo.setPost_id(post_id);
        if (reply_id > 0) {
            PostCommentInfo postInfo = postCommentMapper.selectByPrimaryKey(reply_id);
            if (postInfo != null)
                postCommentInfo.setReply_user_id(postInfo.getComment_user_id());
            postCommentInfo.setReply_id(reply_id);
        }
        postCommentInfo.setComment_type(C.CommentType.COMMENT);
        postCommentInfo.setComment_user_id(user_id);
        postCommentInfo.setContent(content);
        postCommentInfo.setComment_date(LibDateUtils.getLibDateTime());
        if (postCommentMapper.insertSelective(postCommentInfo) > 0) {
            postMapper.increaseCommentCount(post_id, 1);
            WKCache.updatePostInfo(post_id, "comment_count", 1L);
        }

        // 评论文章推送
        PostInfo postInfo = getPostInfo(post_id);
        if (postInfo != null && postInfo.getUser_id() != user_id) {
            Map<String, String> posterMap = userService.getUserInfoByUserId(postInfo.getUser_id(), "account", "nickname", "lang_code");
            String msg = String.format(LibProperties.getLanguage(lang_code, "weking.lang.post.comment"), nickname);
            msgService.sendSysMsg(C.PushType.POST, post_id, posterMap.get("account"), msg, posterMap.get("lang_code"));

            // 记录当日奖励
            WKCache.addUserTodayPostOperate(user_id, "comment");
        }
        // 更新热门缓存
        saveOrUpdatePopularCache(post_id);

        // 删除评论缓存
        WKCache.delUserPostComments(user_id);
        // 添加评论缓存
        if (content.length() >= 5) {
            WKCache.addUserPostComment(user_id, content);
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 删除评论
     *
     * @param user_id
     * @param comment_id
     * @return
     */
    public JSONObject delComment(int user_id, int post_id, int comment_id) {
        if (postCommentMapper.deleteByPrimaryKey(comment_id) > 0)
            postMapper.increaseCommentCount(post_id, -1);
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 点赞
     *
     * @param user_id
     * @param post_id
     * @param is_like
     * @param poster_account
     * @return
     */
    public JSONObject like(int user_id, String account, String nickname, String avatar, int post_id, boolean is_like, String poster_account, String lang_code) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        if (is_like) {
//            PostCommentInfo postCommentInfo = new PostCommentInfo();
//            postCommentInfo.setPost_id(post_id);
//            postCommentInfo.setComment_type(C.CommentType.POST_LIKE);
//            postCommentInfo.setComment_user_id(user_id);
//            postCommentInfo.setComment_date(LibDateUtils.getLibDateTime());
//            if (postMapper.isLike(post_id, user_id) == 0) {
//                if (postCommentMapper.insertSelective(postCommentInfo) > 0)
//                    postMapper.increaseLikeCount(post_id, 1);
//            }

            // TODO 评论操作记录新表
            PostOperation postOperation = new PostOperation();
            postOperation.setPostId((long) post_id);
            postOperation.setCommentType((byte) C.CommentType.POST_LIKE);
            postOperation.setCommentUserId((long) user_id);
            postOperation.setCommentDate(LibDateUtils.getLibDateTime());
            if (postOperationMapper.isLike(post_id, user_id) == 0) {
                if (postOperationMapper.insertSelective(postOperation) > 0) {
                    postMapper.increaseLikeCount(post_id, 1);
                    WKCache.updatePostInfo(post_id, "like_count", 1L);
                }
            }

            if (!account.equals(poster_account)) {
                // 点赞通知
                Map<String, String> posterMap = userService.getUserInfoByAccount(poster_account, "account", "nickname", "lang_code");
                String msg = String.format(LibProperties.getLanguage(lang_code, "weking.lang.post.liked"), nickname);
                msgService.sendSysMsg(C.PushType.POST, post_id, posterMap.get("account"), msg, posterMap.get("lang_code"));

                // 记录当日奖励
                WKCache.addUserTodayPostOperate(user_id, "like");
            }

            JSONObject postLikeConfig = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.post_like_share_config));
            boolean postLikeSwitch = postLikeConfig.optBoolean("on_off");  // 是否开启点赞分享

            if (postLikeSwitch) {
                int need_num = postLikeConfig.optInt("need_num");  // 点赞需要次数
                int s_interval_day = postLikeConfig.optInt("s_interval_day");
                int f_interval_day = postLikeConfig.optInt("f_interval_day");

                // 获取用户点赞相关缓存
                String postLikeInfo = WKCache.getUserPostLikeInfoCache(user_id);
                JSONObject postLikeJSON = new JSONObject();
                if (!LibSysUtils.isNullOrEmpty(postLikeInfo)) {
                    postLikeJSON = JSONObject.fromObject(postLikeInfo);
                }

                int like_num = postLikeJSON.optInt("like_num");
                long like_time = postLikeJSON.optLong("like_time");
                long last_time = postLikeJSON.optLong("last_time");
                long flag_time = postLikeJSON.optLong("flag_time");
                boolean share_success = postLikeJSON.optBoolean("share_success", false);
                long today_date = LibDateUtils.getLibDateTime("yyyyMMdd");

                if (today_date != like_time) {
                    like_num = 0;
                }
                if (share_success) { //成功状态
                    if (DateUtils.getDiffDays(last_time, today_date) >= s_interval_day) {
                        postLikeJSON.put("share_success", false);
                        postLikeConfig.put("flag_time", DateUtils.getFrontDay(today_date, f_interval_day));
                        postLikeJSON.put("if_reward", false);
                    }
                    postLikeJSON.put("like_num", like_num + 1);
                    postLikeJSON.put("like_time", today_date);

                } else {
                    like_num = like_num + 1;
                    if (flag_time == 0) {
                        flag_time = DateUtils.getFrontDay(today_date, f_interval_day);
                    }
                    JSONObject share_content = new JSONObject();
                    if ((DateUtils.getDiffDays(flag_time, today_date) == f_interval_day) ||
                            (DateUtils.getDiffDays(flag_time, today_date) % f_interval_day == 0)) {
//                        if (like_num == like_num) {
                        if (like_num == need_num) {
                            String link_url_add = String.format("&publish_code=%s&account=%s&lang_code=%s", account, account, lang_code);
                            share_content.put("link_url", WKCache.get_system_cache(C.WKSystemCacheField.post_like_share_url) + link_url_add);
                            share_content.put("content", LibProperties.getLanguage(lang_code, "weking.lang.post.like.share.content"));
                            share_content.put("title", LibProperties.getLanguage(lang_code, "weking.lang.post.like.share.title"));
                            String icon_url = WKCache.get_system_cache(C.WKSystemCacheField.post_like_share_pic_url);
                            if (LibSysUtils.isNullOrEmpty(icon_url)) {
                                icon_url = WkUtil.combineUrl(avatar, UploadTypeEnum.AVATAR, true);
                            }
                            share_content.put("icon_url", icon_url);
//                            share_content.put("publish_code",account);
                            result.put("share_content", share_content);
                            postLikeJSON.put("if_reward", false);
                        }
                    }
                    postLikeJSON.put("share_success", false);
                    postLikeJSON.put("like_num", like_num);
                    postLikeJSON.put("like_time", today_date);
                    postLikeJSON.put("flag_time", flag_time);
                }

                // 增加用户点赞相关缓存
                WKCache.addUserPostLikeInfoCache(user_id, postLikeJSON.toString());
            }
        } else {
            // TODO 评论操作记录新表
//            if (postMapper.deleteLikeByPostId(post_id, user_id) > 0){
//                postMapper.increaseLikeCount(post_id, -1);
//            }

            if (postOperationMapper.deleteLikeByPostId(post_id, user_id) > 0) {
                postMapper.increaseLikeCount(post_id, -1);
            }
        }
        // 更新热门缓存
        saveOrUpdatePopularCache(post_id);
//        System.out.println(result);
        return result;
    }

    /**
     * 点赞评论
     *
     * @param user_id
     * @param post_id
     * @param comment_id
     * @return
     */
    public JSONObject CommentLike(int user_id, int post_id, int comment_id) {
        PostCommentInfo postCommentInfo = new PostCommentInfo();
        postCommentInfo.setPost_id(post_id);
        postCommentInfo.setComment_id(comment_id);
        postCommentInfo.setComment_type(C.CommentType.COMMENT_LIKE);
        postCommentInfo.setComment_user_id(user_id);
        postCommentInfo.setComment_date(LibDateUtils.getLibDateTime());
        if (postCommentMapper.isLike(comment_id, user_id) == 0) {
            if (postCommentMapper.insertSelective(postCommentInfo) > 0)
                postCommentMapper.increaseLikeCount(comment_id, 1);
        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * 不喜欢
     *
     * @param user_id
     * @param post_id
     * @param dislike
     * @param poster_account
     * @return
     */
    public JSONObject dislike(int user_id, String account, String nickname, int post_id, boolean dislike, String poster_account,
                              String lang_code) {
        if (dislike) {
//            PostCommentInfo postCommentInfo = new PostCommentInfo();
//            postCommentInfo.setPost_id(post_id);
//            postCommentInfo.setComment_type(C.CommentType.POST_DISLIKE);
//            postCommentInfo.setComment_user_id(user_id);
//            postCommentInfo.setComment_date(LibDateUtils.getLibDateTime());
//            if (postMapper.dislike(post_id, user_id) == 0) {
//                if (postCommentMapper.insertSelective(postCommentInfo) > 0) {
//                    postMapper.increaseDislikeCount(post_id, 1);
//
//                    PostInfo postInfo = postMapper.selectByPrimaryKey(post_id);
//                    if (postInfo != null && postInfo.getDislike_count() >= LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.hide_post_dislike_num))) {
//                        postInfo.setPost_status(2);
//                        postMapper.updateByPrimaryKeySelective(postInfo);
//                    }
//                }
//            }

            // TODO 评论操作记录新表
            PostOperation postOperation = new PostOperation();
            postOperation.setPostId((long) post_id);
            postOperation.setCommentType((byte) C.CommentType.POST_DISLIKE);
            postOperation.setCommentUserId((long) user_id);
            postOperation.setCommentDate(LibDateUtils.getLibDateTime());
            if (postOperationMapper.dislike(post_id, user_id) == 0) {
                if (postOperationMapper.insertSelective(postOperation) > 0) {
                    postMapper.increaseDislikeCount(post_id, 1);
                    WKCache.updatePostInfo(post_id, "dislike_count", 1L);

                    PostInfo postInfo = postMapper.selectByPrimaryKey(post_id);
                    if (postInfo != null && postInfo.getDislike_count() >= LibSysUtils.toInt(WKCache.get_system_cache(C.WKSystemCacheField.hide_post_dislike_num))) {
                        postInfo.setPost_status(2);
                        postMapper.updateByPrimaryKeySelective(postInfo);
                        if (postInfo.getSorts() > 0) {
                            WKCache.addPostInfo(postInfo);
                        }
                    }
                }
            }


            if (!account.equals(poster_account)) {
                // 踩贴通知
                Map<String, String> posterMap = userService.getUserInfoByAccount(poster_account, "account", "nickname", "lang_code");
                String msg = String.format(LibProperties.getLanguage(lang_code, "weking.lang.post.disliked"), nickname);
                msgService.sendSysMsg(posterMap.get("account"), msg, posterMap.get("lang_code"));
            }

        }
        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    /**
     * @param user_id
     * @param post_id
     * @param share_type 分享类型，WeiXin,WX_Circle,FaceBook,Line
     * @param if_like    是否点赞动态弹出的分享  0否 1是
     * @return
     */
    public JSONObject share(int user_id, int post_id, String share_type, int if_like, String lang_code) {
//        PostCommentInfo postCommentInfo = new PostCommentInfo();
//        postCommentInfo.setPost_id(post_id);
//        postCommentInfo.setContent(share_type);
//        postCommentInfo.setComment_user_id(user_id);
//        postCommentInfo.setComment_date(LibDateUtils.getLibDateTime());

        // TODO 评论操作记录新表
        PostOperation postOperation = new PostOperation();
        postOperation.setPostId((long) post_id);
        postOperation.setContent(share_type);
        postOperation.setCommentUserId((long) user_id);
        postOperation.setCommentDate(LibDateUtils.getLibDateTime());

        if (if_like == 1) {
//            postCommentInfo.setComment_type(C.CommentType.POST_LIKE_SHARE);
            postOperation.setCommentType((byte) C.CommentType.POST_LIKE_SHARE);
            JSONObject postLikeJSON = JSONObject.fromObject(WKCache.getUserPostLikeInfoCache(user_id));
            postLikeJSON.put("share_success", true);
            long todayDate = LibDateUtils.getLibDateTime("yyyyMMdd");
            postLikeJSON.put("last_time", todayDate);
            postLikeJSON.put("flag_time", todayDate);
            WKCache.addUserPostLikeInfoCache(user_id, postLikeJSON.toString());
            JSONObject postLikeConfig = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.post_like_share_config));
            UserBill userBill = userBillMapper.selectBillByUserIdAndTypeToday(user_id, C.UserBillType.INVITE_SHARE, LibDateUtils.getLibDateTime("yyyyMMdd000000"));
            if (postLikeConfig.optBoolean("on_off") && postLikeConfig.optInt("invite_reward") > 0 && userBill == null) { // 奖励
                int invite_reward = postLikeConfig.optInt("invite_reward");
//                BigDecimal reward = new BigDecimal(invite_reward).setScale(3, BigDecimal.ROUND_HALF_UP);
//                String inviter_msg = String.format(LibProperties.getLanguage(lang_code, "weking.lang.post.invite.share.reward"), invite_reward);
//                digitalService.OptWallect(user_id, lang_code, post_id, "SCA", reward, (short) 4, LibSysUtils.getRandomNum(16), "邀请分享奖励", inviter_msg);
//
//                UserBill bill = UserBill.getBill(user_id, invite_reward, 1, post_id, C.UserBillType.INVITE_SHARE);
//                userBillMapper.insert(bill);
                //赠送emo后 获得新的比值
                Double ratio = LibSysUtils.toDouble(userService.getUserInfoByUserId(LibSysUtils.toInt(user_id), "ratio"));//现有的比值
                Integer totalDiamond=0;
                BigDecimal newRatio;
                if(ratio>0){
                    PocketInfo pocketInfo = pocketInfoMapper.selectByUserid(LibSysUtils.toInt(user_id));
                    if(pocketInfo!=null) {
                        totalDiamond = pocketInfo.getTotalDiamond();//现有的emo
                    }
                    if(totalDiamond>0) {
                        double v = (invite_reward + totalDiamond) / (totalDiamond / ratio);
                        newRatio = new BigDecimal(v).setScale(8, BigDecimal.ROUND_HALF_UP);
                    }else {
                        newRatio = new BigDecimal(0).setScale(8, BigDecimal.ROUND_HALF_UP);
                    }
                    accountInfoMapper.updateRatioByUserid(newRatio,LibSysUtils.toInt(user_id));
                   // WKCache.add_user(LibSysUtils.toInt(user_id), "ratio", LibSysUtils.toString(newRatio));
                }
                // 增加用户货币
                /*pocketInfoMapper.increaseDiamondByUserId(user_id, invite_reward);
                UserBill bill = UserBill.getBill(user_id, invite_reward, 0, post_id, C.UserBillType.INVITE_SHARE);
                userBillMapper.insert(bill);*/
            }

        } else {
//            postCommentInfo.setComment_type(C.CommentType.POST_SHARE);
            postOperation.setCommentType((byte) C.CommentType.POST_SHARE);
        }

//        if (postCommentMapper.insertSelective(postCommentInfo) > 0) {
//            postMapper.increaseShareCount(post_id, 1);
//        }

        WKCache.updatePostInfo(post_id, "share_count", 1L);
        if (postOperationMapper.insertSelective(postOperation) > 0) {
            postMapper.increaseShareCount(post_id, 1);
        }
        // 更新热门缓存
        saveOrUpdatePopularCache(post_id);
        // 记录当日奖励
        WKCache.addUserTodayPostOperate(user_id, "share");

        return LibSysUtils.getResultJSON(ResultCode.success);
    }

    //更新热门列表缓存
    private void saveOrUpdatePopularCache(int post_id) {
        PostInfo postInfo = getPostInfo(post_id);
        if (postInfo != null) {
            JSONObject config = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.popular_post_config));
            double score = config.optDouble("share", 0) * postInfo.getShare_count() +
                    config.optDouble("comment", 0) * postInfo.getComment_count() +
                    config.optDouble("like", 0) * postInfo.getLike_count();
            int dayNum = config.optInt("dayNum", 7);
            WKCache.delPopularPostList(config.optDouble("base", 50));
            if (score >= config.optDouble("base", 0)) {
                postInfo.setHots(score);
                postMapper.updateByPrimaryKeySelective(postInfo);
                Long post_date = postInfo.getPost_date();
                Date frontDay = DateUtils.getFrontDay(LibDateUtils.getDateTime(), dayNum);//7天前的这个时间
                long dateTime = LibDateUtils.dateTime2LibDateTime(frontDay, "yyyyMMddHHmmss");
                if (post_date >= dateTime) {
                    WKCache.addPopularPostList(post_id, score, config.optInt("day", 1));
                }
                WKCache.addPostInfo(postInfo);
            }
        }
    }

    /**
     * 获取评论列表
     *
     * @param user_id 获取者userid
     * @param post_id 如果要获取自己或固定某人的动态则传自己会对应某人的account，account为空则获取所有关注者的动态
     * @param index
     * @param count
     * @return
     */
    public JSONObject getCommentList(int user_id, int post_id, int index, int count, String lang_code) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        JSONArray array = new JSONArray();
//        List<PostCommentInfo> postCommentInfo = postCommentMapper.getCommentList(user_id, post_id, index, count);

        List<PostCommentInfo> postCommentList = postCommentMapper.selectCommentList(user_id, post_id, index, count);

        for (PostCommentInfo info : postCommentList) {
            JSONObject temp = new JSONObject();
            temp.put("post_id", info.getPost_id());
            temp.put("comment_id", info.getId());
            Map<String, String> reviewerMap = userService.getUserInfoByUserId(info.getComment_user_id(), "account", "nickname", "avatar");
            temp.put("account", reviewerMap.get("account"));
            temp.put("nickname", reviewerMap.get("nickname"));
            temp.put("avatar", WkUtil.combineUrl(reviewerMap.get("avatar"), UploadTypeEnum.AVATAR, true));

            temp.put("post_content", info.getContent());
            temp.put("post_time", WkUtil.format(LibDateUtils.getDateTimeTick(info.getComment_date(), LibDateUtils.getLibDateTime()), lang_code));
            String reply_nickname = "";
            if (info.getReply_user_id() != 0) {
                reply_nickname = userService.getUserInfoByUserId(info.getReply_user_id(), "nickname");
            }
            temp.put("reply_nickname", reply_nickname);

            temp.put("like_count", info.getLike_count());
            temp.put("is_Like", LibSysUtils.toInt(info.getIs_like()) > 0);//当前是否点赞
            array.add(temp);
        }
        result.put("list", array);
        return result;
    }

    /**
     * 计算动态挖矿奖励
     */
    public void calculateAward() {
        List<PostInfo> postInfoList = postMapper.getCalculateAwardList(LibDateUtils.getLibDateTime()); //获取符合计算奖励的文章
        if (postInfoList.size() > 0) {
            JSONObject mining_object = JSONObject.fromObject(WKCache.get_system_cache("post.mining"));
            if (mining_object.size() == 0)
                return;
            List<String> temp = new ArrayList<>();
            String lang_code = LibProperties.getConfig("weking.config.default_lang");
            for (PostInfo postInfo : postInfoList) {
                double mining = 0;
                if (mining_object.optDouble("total", 0) != 0) {  // 未分配奖励
                    int post_user_id = postInfo.getUser_id();//发布者id
                    temp.clear();
                    // TODO 评论操作记录新表
                    List<PostCommentInfo> postCommentInfoList = postCommentMapper.getAllCommentList(postInfo.getId()); //评论及评论点赞
                    List<PostOperation> postOperationList = postOperationMapper.getOperationListByPostId(postInfo.getId()); //点赞,不喜欢,分享列表

                    if (postCommentInfoList.size() > 0 || postOperationList.size() > 0) {
                        //统计计算挖矿奖励
                        mining = mining_object.optDouble("post", 0);
                        int comment_count = 0, like_count = 0, dislike_count = 0, share_count = 0;

                        for (PostCommentInfo postCommentInfo : postCommentInfoList) {
                            if (post_user_id != postCommentInfo.getComment_user_id()) {//评论自己的文章不计入挖矿奖励
                                String key = String.format("%s_%d", postCommentInfo.getComment_user_id(), postCommentInfo.getComment_type());
                                if (temp.indexOf(key) < 0) {//过滤重复评论的用户
                                    switch (postCommentInfo.getComment_type()) {//0文字评论，1点赞,2不喜欢,3分享,4：评论点赞
                                        case C.CommentType.COMMENT:
                                            mining = mining + mining_object.optDouble("comment", 0);
                                            temp.add(key);
                                            comment_count++;
                                            break;
                                        case C.CommentType.COMMENT_LIKE:
                                            break;
                                        default:
                                            break;
                                    }
                                }

                            }
                        }
                        for (PostOperation postOperation : postOperationList) {
                            if (post_user_id != postOperation.getCommentUserId()) {
                                String key = String.format("%s_%d", postOperation.getCommentUserId(), postOperation.getCommentType());
                                if (temp.indexOf(key) < 0) {
                                    switch (postOperation.getCommentType()) { // 1点赞,2不喜欢,3分享
                                        case C.CommentType.POST_LIKE:
                                            mining = mining + mining_object.optDouble("like", 0);
                                            temp.add(key);
                                            like_count++;
                                            break;
                                        case C.CommentType.POST_DISLIKE:
                                            mining = mining + mining_object.optDouble("dislike", 0);
                                            temp.add(key);
                                            dislike_count++;
                                            break;
                                        case C.CommentType.POST_SHARE:
                                            mining = mining + mining_object.optDouble("share", 0);
                                            temp.add(key);
                                            share_count++;
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        }

                        if (mining > 0 && temp.size() > 0) {  //分配挖矿奖励
                            mining = mining > mining_object.optDouble("total", 0) ? mining_object.optDouble("total", 0) : mining;
                            JSONObject mining_rate = JSONObject.fromObject(WKCache.get_system_cache("post.mining_rate"));
                            BigDecimal value = new BigDecimal(mining * mining_rate.optDouble("post", 0)).setScale(3, BigDecimal.ROUND_HALF_UP);
                            digitalService.OptWallect(post_user_id, lang_code, postInfo.getId(), "SCA", value, (short) 4, LibSysUtils.getRandomNum(16), "发布动态获得奖励", "", 0);
                            for (String key : temp) {
                                int user_id = LibSysUtils.toInt(key.split("_")[0]);
                                int comment_type = LibSysUtils.toInt(key.split("_")[1]);
                                switch (comment_type) {//0文字评论，1点赞,2不喜欢,3分享,4：评论点赞 5点赞动态弹出挖矿分享
                                    case C.CommentType.COMMENT:
                                        value = new BigDecimal(mining * mining_rate.optDouble("comment", 0) / comment_count).setScale(3, BigDecimal.ROUND_HALF_UP);
                                        digitalService.OptWallect(user_id, lang_code, postInfo.getId(), "SCA", value, (short) 4, LibSysUtils.getRandomNum(16), "评论动态获得奖励", "", 0);
                                        break;
                                    case C.CommentType.POST_LIKE:
                                        value = new BigDecimal(mining * mining_rate.optDouble("like", 0) / like_count).setScale(3, BigDecimal.ROUND_HALF_UP);
                                        digitalService.OptWallect(user_id, lang_code, postInfo.getId(), "SCA", value, (short) 4, LibSysUtils.getRandomNum(16), "喜欢动态获得奖励", "", 0);
                                        break;
                                    case C.CommentType.POST_DISLIKE:
                                        value = new BigDecimal(mining * mining_rate.optDouble("dislike", 0) / dislike_count).setScale(3, BigDecimal.ROUND_HALF_UP);
                                        digitalService.OptWallect(user_id, lang_code, postInfo.getId(), "SCA", value, (short) 4, LibSysUtils.getRandomNum(16), "不喜欢动态获得奖励", "", 0);
                                        break;
                                    case C.CommentType.POST_SHARE:
                                        value = new BigDecimal(mining * mining_rate.optDouble("share", 0) / share_count).setScale(3, BigDecimal.ROUND_HALF_UP);
                                        digitalService.OptWallect(user_id, lang_code, postInfo.getId(), "SCA", value, (short) 4, LibSysUtils.getRandomNum(16), "分享动态获得奖励", "", 0);
                                        break;
                                    case C.CommentType.COMMENT_LIKE:
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                }
                // 更新
                postInfo.setBalance_status(1);
                postInfo.setSca_reward(new BigDecimal(mining).setScale(3, BigDecimal.ROUND_HALF_UP));
                postMapper.updateByPrimaryKeySelective(postInfo);
                if (postInfo.getSorts() > 0) {
                    WKCache.addPostInfo(postInfo);
                }
            }
        }
    }


    /**
     * 查看动态
     *
     * @param userId
     * @param post_id
     * @return
     */
    public JSONObject viewPost(int userId, int post_id, String post_account) {
        int poster_id = LibSysUtils.toInt(userService.getUserFieldByAccount(post_account, "user_id"));
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        PostViewLog postViewLog = new PostViewLog();
        postViewLog.setPostId(post_id);
        postViewLog.setUserId(userId);
        postViewLog.setPosterId(poster_id);
        postViewLog.setViewNum(1);
        postViewLog.setAddTime(LibDateUtils.getLibDateTime());
        postViewLogMapper.insertSelective(postViewLog);

        return result;
    }

    /**
     * 获取礼物盒子
     *
     * @param userId
     * @return
     */
    @Transactional
    public JSONObject getGiftBox(int userId, String account, String lang_code) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        JSONObject postGiftBoxConfig = JSONObject.fromObject(WKCache.get_system_cache(C.WKSystemCacheField.post_gift_box_config));
        boolean postGiftBoxSwitch = postGiftBoxConfig.getBoolean("on_off");
        if (!postGiftBoxSwitch) {  // 礼物盒子已经关闭
            return LibSysUtils.getResultJSON(ResultCode.post_gift_off);
        }
        int total_reward_num = postGiftBoxConfig.getInt("reward_num");  // 每天奖励总数
        int user_reward_num = WKCache.getUserPostGiftBoxRewardNum(userId);
        if (user_reward_num >= total_reward_num) {  // 今日奖励盒子次数到达上限
            return LibSysUtils.getResultJSON(ResultCode.post_reward_num_over);
        }
        JSONArray rewardArray = postGiftBoxConfig.optJSONArray("detail");
        int reward_amount = rewardArray.optInt(user_reward_num);  // 奖励数量

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
                double v = (reward_amount + totalDiamond) / (totalDiamond / ratio);
                newRatio = new BigDecimal(v).setScale(8, BigDecimal.ROUND_HALF_UP);
            }else {
                newRatio = new BigDecimal(0).setScale(8, BigDecimal.ROUND_HALF_UP);
            }
            accountInfoMapper.updateRatioByUserid(newRatio,LibSysUtils.toInt(userId));
           // WKCache.add_user(LibSysUtils.toInt(userId), "ratio", LibSysUtils.toString(newRatio));
        }
        //增加用户货币
      /*  pocketInfoMapper.increaseDiamondByUserId(userId, reward_amount);
        UserBill userBill = UserBill.getBill(userId, reward_amount, 0, 0, C.UserBillType.POST_GIFT);
        userBillMapper.insert(userBill);*/

//        String msg = String.format(LibProperties.getLanguage(lang_code,"weking.lang.post.gift.reward"),reward_amount);
//        msgService.sendSysMsg(account,msg,lang_code);

        int today_remain_second = DateUtils.getRemainSecondsOneDay(new Date());
        WKCache.addUserPostGiftBoxRewardNum(userId, user_reward_num + 1, today_remain_second);
//        WKCache.addUserPostGiftBoxRewardNum(userId, 1, today_remain_second);  // 随时都弹

        result.put("reward_num", user_reward_num + 1);
        result.put("reward_emo", reward_amount);
        result.put("describe_task", "+" + reward_amount + LibSysUtils.getLang("weking.lang.app.mony"));
        result.put("my_diamonds", pocketInfoMapper.getSenderLeftDiamondbyid(userId));
//        System.out.println(result.toString());
        return result;
    }

    public void changePostStatus(int post_id, int status) {
        postMapper.updatePostStatusById(post_id, status);
    }

    public void todayNoPostPush() {
        System.out.println("------发文推送-------");
        if (LibSysUtils.toBoolean(WKCache.get_system_cache(C.WKSystemCacheField.push_post_switch), false)) {
            JSONObject CNObject = PushMsg.getPushJSONObject(IMCode.post_push, "zh_CN", "post.push.title", "post.push.content");
//            GeTuiUtil.pushMsgToApp(CNObject.toString(), CNObject.optString("message"), CNObject.optString("title"),"");
//            PushMsg.pushSingleMsg("462318bdca8e379d683b5e75a5ee92bc",CNObject);
//            PushMsg.pushSingleMsg("eb43796c961dabbe522594f030640c6c",CNObject);
//            PushMsg.pushSingleMsg("4968a4e4e587856cad60e554744e5cd8",CNObject);
//            Firebase.pushMsgToSingle("fYpguVe7Rzc:APA91bEucBj4LMSCH_wDEwRPCnHR2iBjIc4yVzX9iVu8dtBQj74NnN_PWIVoLdBQwi-TNNAE9MdXhHS2oo29qSQstrU9eJNz_U0De8dkNkUgYgC0M4HqsHJEFusBPLrDP9wYaEYtIYyE", CNObject);
            PushMsg.sendSystemMsgWithLang(IMCode.post_push, "post.push.title", "post.push.content");

        }
    }


    /**
     * 点赞人员列表
     *
     * @param userId
     * @param post_id
     * @param index
     * @param count
     * @param lang_code
     * @return
     */
    public JSONObject getLikeList(int userId, int post_id, int index, int count, String lang_code) {
        PostInfo postInfo = getPostInfo(post_id);
        if (postInfo == null) {
            return LibSysUtils.getResultJSON(ResultCode.data_error);
        }
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        List<Integer> userFollowList = followInfoMapper.getUserFollowerList(userId);
        List<PostOperation> likerList = postOperationMapper.getOperationListByPostIdAndType(post_id, 1, index, count);
        JSONArray array = new JSONArray();
        if (likerList.size() > 0) {
            for (PostOperation info : likerList) {
                JSONObject jsonObject = new JSONObject();
                Map<String, String> userMap = userService.getUserInfoByUserId(LibSysUtils.toInt(info.getCommentUserId()),
                        "account", "nickname", "avatar", "sex", "level", "signature");
                jsonObject.put("account", userMap.get("account"));
                jsonObject.put("nickname", userMap.get("nickname"));
                jsonObject.put("pic_head_low", WkUtil.combineUrl(userMap.get("avatar"), UploadTypeEnum.AVATAR, true));
                jsonObject.put("sex", userMap.get("sex") == null ? 0 : LibSysUtils.toInt(userMap.get("sex")));
                jsonObject.put("level", userMap.get("level") == null ? 1 : userMap.get("level"));
                jsonObject.put("signature", userMap.get("signature") == null ? "" : userMap.get("signature"));
                List<Integer> likerFollowList = followInfoMapper.getUserFollowerList(LibSysUtils.toInt(info.getCommentUserId()));
                // 1、几位共同好友
                Collection realA = new ArrayList<>(userFollowList);
                Collection realB = new ArrayList<>(likerFollowList);
                // 求交集
                realA.retainAll(realB);
                jsonObject.put("friends_num", realA.size());

                // 2、是否关注
                int follow_state = 0;
                if (userFollowList != null && userFollowList.contains(LibSysUtils.toInt(info.getCommentUserId()))) {
                    follow_state = 1;
                }
                jsonObject.put("follow_state", follow_state);
//                if (info.getCommentUserId() != userId) {
                    array.add(jsonObject);
//                }
            }
        }
        result.put("list", array);


        return result;
    }


}
