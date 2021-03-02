package com.weking.controller.post;

import com.weking.controller.out.OutControllerBase;
import com.weking.core.C;
import com.weking.core.WkUtil;
import com.weking.service.post.PostService;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 动态
 */
@Controller
@RequestMapping("/post")
public class PostController extends OutControllerBase {
    private static Logger log = Logger.getLogger(PostController.class);
    @Resource
    private PostService postService;

    /**
     * 获取动态列表
     */
    @RequestMapping("/getPostList")
    public void getPostList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 10);
            String account = getParameter(request, "account", "");
            String lang_code = object.optString("lang_code");
            object = postService.getPostList(userId, account, index, count, lang_code);
        }
        out(response, object,api_version);
    }

    /**
     * 获得关注者动态
     */
    @RequestMapping("/getFollowPost")
    public void getFollowPost(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 10);
            int post_id = getParameter(request,"post_id",0);
            String account = getParameter(request, "account", "");
            //double api_version = getParameter(request, "api_version", 1.0);
            String lang_code = object.optString("lang_code");
            object = postService.getFollowPost(userId, account,post_id, index, count, lang_code,api_version);
        }
        out(response, object,api_version);
    }

    /**
     * 获取最新的动态
     */
    @RequestMapping("/getHotPost")
    public void getHotPost(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WkUtil.checkToken(access_token);
        int index = getParameter(request, "index", 0);
        int count = getParameter(request, "count", 10);
        int post_id = getParameter(request,"post_id",0);
        String account = getParameter(request, "account", "");
        double api_version = getParameter(request, "api_version", 1.0);
        int type = getParameter(request,"type",0);  // 类型 0最新 1热门
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            object = postService.getHotPost(userId, account, post_id,index, count, lang_code,api_version,type);
        } else {//没有登录时
            object = postService.getHotPost(0, account, post_id,index, count, "zh_CN",api_version,type);
        }
//        System.out.println(object.toString());
        out(response, object,api_version);
    }


    /**
     * 获取最新的动态(web)
     */
    @RequestMapping("/getPost")
    public void getPost(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WkUtil.checkToken(access_token);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        int post_id = getParameter(request, "post_id", 0);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            object = postService.getPost(userId, post_id, lang_code);
        } else {//没有登录时
            object = postService.getPost(0, post_id, "zh_TW");
        }
        out(response, object,api_version);
    }


    /**
     * 获取推荐的动态
     */
    @RequestMapping("/getRecommendPost")
    public void getRecommendPost(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WkUtil.checkToken(access_token);
        int index = getParameter(request, "index", 0);
        int count = getParameter(request, "count", 10);
        int post_id = getParameter(request, "post_id", 0);
        double api_version = getParameter(request, C.RequestParam.api_version, 1.0);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            object = postService.getRecommendPost(userId, index, count, lang_code,post_id,api_version);
        } else {//没有登录时
            object = postService.getRecommendPost(0, index, count, "zh_CN",post_id,api_version);
        }
        out(response, object,api_version);
    }

    /**
     * 获取热门的动态
     */
    @RequestMapping("/getPopularPost")
    public void getPopularPost(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WkUtil.checkToken(access_token);
        int index = getParameter(request, "index", 0);
        int count = getParameter(request, "count", 20);
        int post_id = getParameter(request, "post_id", 0);
        double api_version = getParameter(request, C.RequestParam.api_version, 1.0);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            object = postService.getPopularPost(userId,post_id, index, count, lang_code,api_version);
        } else {//没有登录时
            object = postService.getPopularPost(0,post_id, index, count, "zh_CN",api_version);
        }
        out(response, object,api_version);
    }

    /**
     * 获取动态详情
     */
    @RequestMapping("/getPostInfo")
    public void getPostInfo(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WkUtil.checkToken(access_token);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            int post_id = getParameter(request, "post_id", 0);
            object = postService.getPostInfo(userId, post_id, lang_code);
        }
        out(response, object,api_version);
    }

    /**
     * 新增动态列表
     */
    @RequestMapping("/submit")
    public void submit(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WkUtil.checkToken(access_token);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String account = object.optString("account");
            String nickname = object.optString("nickname");
            String lang_code = object.optString("lang_code");
            String content = getParameter(request, "content", "");
            String images = getParameter(request, "images", "");
            try {
                images = java.net.URLDecoder.decode(images, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            int type = getParameter(request, "type", 1);
            String city = getParameter(request, "city", "");
            int expiryTime = getParameter(request, "expiry_time", 0);
            String longitude = getParameter(request, "longitude", "");
            String latitude = getParameter(request, "latitude", "");
            String publish_code = getParameter(request, "publish_code", "");
            int is_guide = getParameter(request, "is_guide", 0);
            object = postService.submit(userId,account,nickname, lang_code,type, content, images, city, longitude, latitude, expiryTime,publish_code,is_guide);
        }
        out(response, object,api_version);
    }

    /**
     * 删除动态列表
     */
    @RequestMapping("/delPost")
    public void delPost(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int post_id = getParameter(request, "post_id", 0);
            object = postService.delPost(userId, post_id);
        }
        out(response, object,api_version);
    }

    /**
     * 评论动态
     */
    @RequestMapping("/comment")
    public void comment(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WkUtil.checkToken(access_token);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String nickname = object.optString("nickname");
            String lang_code = object.optString("lang_code");
            int post_id = getParameter(request, "post_id", 0);
            int reply_id = getParameter(request, "reply_id", 0);
            String content = getParameter(request, "content", "");
            object = postService.comment(userId,nickname, post_id, reply_id, content,lang_code);
        }
        out(response, object,api_version);
    }

    /**
     * 删除评论列表
     */
    @RequestMapping("/delComment")
    public void delComment(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int post_id = getParameter(request, "post_id", 0);
            int comment_id = getParameter(request, "comment_id", 0);
            object = postService.delComment(userId, post_id, comment_id);
        }
        out(response, object,api_version);
    }


    /**
     * 点赞动态
     */
    @RequestMapping("/like")
    public void like(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        JSONObject object = WkUtil.checkToken(access_token);
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String account = object.optString("account");
            String nickname = object.optString("nickname");
            String avatar = object.optString("avatar");
            String lang_code = object.optString("lang_code");
            int post_id = getParameter(request, "post_id", 0);
            boolean is_like = getParameter(request, "is_like", true);
            String poster_account = getParameter(request, "poster_account", "");
            object = postService.like(userId, account, nickname,avatar, post_id, is_like, poster_account, lang_code);
        }
        out(response, object,api_version);
    }

    /**
     * 不喜欢
     */
    @RequestMapping("/dislike")
    public void dislike(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String account = object.optString("account");
            String nickname = object.optString("nickname");
            String lang_code = object.optString("lang_code");
            int post_id = getParameter(request, "post_id", 0);
            boolean is_like = getParameter(request, "dislike", true);
            String poster_account = getParameter(request, "poster_account", "");
            object = postService.dislike(userId, account, nickname, post_id, is_like, poster_account,lang_code);
        }
        out(response, object,api_version);
    }

    /**
     * 分享
     */
    @RequestMapping("/share")
    public void share(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            int post_id = getParameter(request, "post_id", 0);
            String share_type = getParameter(request, "share_type", "WeiXin");
            int if_like = getParameter(request, "if_like", 0);
            object = postService.share(userId, post_id, share_type,if_like,lang_code);
        }
        out(response, object,api_version);
    }

    /**
     * 获取评论列表
     */
    @RequestMapping("/getCommentList")
    public void getCommentList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        int index = getParameter(request, "index", 0);
        int count = getParameter(request, "count", 10);
        int post_id = getParameter(request, "post_id", 0);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            object = postService.getCommentList(userId, post_id, index, count, lang_code);
        } else {
            object = postService.getCommentList(0, post_id, index, count, "zh_CN");
        }
        out(response, object,api_version);
    }

    /**
     * 点赞评论
     */
    @RequestMapping("/comment/like")
    public void CommentLike(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int post_id = getParameter(request, "post_id", 0);
            int comment_id = getParameter(request, "comment_id", 0);
            object = postService.CommentLike(userId, post_id, comment_id);
        }
        out(response, object,api_version);
    }

    /**
     * 查看动态
     */
    @RequestMapping("/view")
    public void viewPost(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            int post_id = getParameter(request, "post_id", 0);
            String post_account = getParameter(request, "post_account");
            object = postService.viewPost(userId, post_id,post_account);
        }
        out(response, object,api_version);
    }

    /**
     * 获取礼物盒子
     */
    @RequestMapping("/getGiftBox")
    public void getGiftBox(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String account = object.optString("account");
            String lang_code = object.optString("lang_code");
            int post_id = getParameter(request, "post_id", 0);
            object = postService.getGiftBox(userId,account,lang_code);
        }
        out(response, object,api_version);
    }

    /**
     * 获取点赞人员列表
     */
    @RequestMapping("/getLikeList")
    public void getLikeList(HttpServletRequest request, HttpServletResponse response) {
        String access_token = getParameter(request, "access_token");
        double api_version = LibSysUtils.toDouble(request.getParameter("api_version"),1.2);
        JSONObject object = WkUtil.checkToken(access_token);
        if (object.optInt("code") == 0) {
            int userId = object.optInt("user_id");
            String lang_code = object.optString("lang_code");
            int post_id = getParameter(request, "post_id", 0);
            int index = getParameter(request, "index", 0);
            int count = getParameter(request, "count", 10);
            object = postService.getLikeList(userId, post_id, index, count, lang_code);
        }
        out(response, object,api_version);
    }

}
