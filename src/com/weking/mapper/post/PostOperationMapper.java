package com.weking.mapper.post;

import com.weking.model.post.PostOperation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostOperationMapper {

    int insert(PostOperation record);

    int insertSelective(PostOperation postOperation);

    PostOperation selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(PostOperation record);

    //是否点赞
    int isLike(@Param("postId") Integer postId, @Param("userId") Integer userId);

    //是否踩
    int dislike(@Param("postId") Integer postId, @Param("userId") Integer userId);

    //取消点赞
    int deleteLikeByPostId(@Param("postId") Integer postId, @Param("userId") Integer userId);

    List<PostOperation> getOperationListByPostId(@Param("postId") Integer postId);

    List<PostOperation> getOperationListByPostIdAndType(@Param("postId") Integer postId,@Param("type") Integer type,@Param("index") int index,
                                                        @Param("count") Integer count);

}