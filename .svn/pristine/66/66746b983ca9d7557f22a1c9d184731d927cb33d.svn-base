package com.weking.mapper.post;

import com.weking.model.post.PostInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by bing on 24/1/2017.
 */
public interface PostMapper {

    PostInfo selectByPrimaryKey(Integer id);

    int deleteByPrimaryKey(Integer postId);

    int insertSelective(PostInfo post);

    int updateByPrimaryKeySelective(PostInfo post);

    int increaseLikeCount(@Param("postId") Integer postId, @Param("qty") Integer qty);

    int increaseDislikeCount(@Param("postId") Integer postId, @Param("qty") Integer qty);

    int increaseCommentCount(@Param("postId") Integer postId, @Param("qty") Integer qty);

    int increaseShareCount(@Param("postId") Integer postId, @Param("qty") Integer qty);

    //获取动态
    List<PostInfo> getPostList(@Param("user_id") Integer user_id, @Param("account") String account, @Param("currentTime") long currentTime, @Param("offset") int offset, @Param("limit") int limit);

    //获取动态
    List<PostInfo> getNewPostList(@Param("currentTime") long currentTime, @Param("userId") int userId,
                                  @Param("postId") int postId,@Param("offset") int offset, @Param("limit") int limit);

    //获取动态详情
    List<PostInfo> getPost(@Param("user_id") Integer user_id,@Param("post_id") Integer post_id);

    //取消点赞
    int deleteLikeByPostId(@Param("postId") Integer postId, @Param("userId") Integer userId);

    //是否点赞
    int isLike(@Param("postId") Integer postId, @Param("userId") Integer userId);

    //是否踩
    int dislike(@Param("postId") Integer postId, @Param("userId") Integer userId);

    //获取计算奖励的记录
    List<PostInfo> getCalculateAwardList(@Param("currentTime") long currentTime);

    // 获取关注的动态列表
    List<PostInfo> getFollowPostList(@Param("user_id") Integer user_id,@Param("postId") Integer post_id, @Param("list") List<Integer> list, @Param("currentTime") long currentTime, @Param("offset") int offset, @Param("limit") int limit);

    int updatePostStatusById(@Param("postId") Integer postId, @Param("status") Integer status);

    // 获取推荐的动态列表
    List<PostInfo> getRecommendPostList(@Param("currentTime") long currentTime,@Param("offset") int offset, @Param("limit") int limit);
}
