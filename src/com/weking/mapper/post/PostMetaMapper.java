package com.weking.mapper.post;

import com.weking.model.post.PostMeta;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by bing on 1/26/2017.
 */
public interface PostMetaMapper {
    List<PostMeta> selectByPostId(@Param("postId") int postId);

    int insertSelective(PostMeta postMeta);

    int deleteByPostId(Integer postId);
}
