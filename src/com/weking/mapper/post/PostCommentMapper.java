package com.weking.mapper.post;

import com.weking.model.post.PostCommentInfo;
import com.weking.model.post.PostInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by bing on 1/26/2017.
 */
public interface PostCommentMapper {

    PostCommentInfo selectByPrimaryKey(Integer id);

    int deleteByPrimaryKey(Integer postCommentId);

    int insertSelective(PostCommentInfo postComment);

    //获取评论
    List<PostCommentInfo> getCommentList(@Param("user_id") Integer user_id, @Param("post_id") Integer post_id, @Param("offset") int offset, @Param("limit") int limit);

    List<PostCommentInfo> selectCommentList(@Param("user_id") Integer user_id, @Param("post_id") Integer post_id, @Param("offset") int offset, @Param("limit") int limit);

    //是否点赞
    int isLike(@Param("comment_id") Integer comment_id, @Param("userId") Integer userId);

    //点赞评论
    int increaseLikeCount(@Param("comment_id") Integer comment_id, @Param("qty") Integer qty);

    //获取所有评论
    List<PostCommentInfo> getAllCommentList( @Param("post_id") Integer post_id);

    // 获取动态列表的展示评论 3条
    List<PostCommentInfo> selectPostComments(@Param("user_id")int user_id,@Param("post_id") Integer post_id);
}
