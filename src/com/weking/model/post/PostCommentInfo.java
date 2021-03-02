package com.weking.model.post;

/**
 * Created by bing on 24/1/2017.
 */
public class PostCommentInfo {
    private Integer id;

    private Integer comment_user_id;

    private Integer post_id;

    private Integer reply_id;

    private String content;

    private Integer comment_type;

    private Integer status;

    private Long comment_date;

    private Integer reply_user_id;

    private String account;

    private String avatar;

    private String nickname;

    private Integer level;

    private Integer sex;

    private Integer like_count;

    private Integer is_like;

    private Integer comment_id;

    public Integer getComment_id() {
        return comment_id;
    }

    public void setComment_id(Integer comment_id) {
        this.comment_id = comment_id;
    }

    public Integer getIs_like() {
        return is_like;
    }

    public void setIs_like(Integer is_like) {
        this.is_like = is_like;
    }

    public Integer getLike_count() {
        return like_count;
    }

    public void setLike_count(Integer like_count) {
        this.like_count = like_count;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getReply_nickname() {
        return reply_nickname;
    }

    public void setReply_nickname(String reply_nickname) {
        this.reply_nickname = reply_nickname;
    }

    private String reply_nickname;


    public Integer getReply_user_id() {
        return reply_user_id;
    }

    public void setReply_user_id(Integer reply_user_id) {
        this.reply_user_id = reply_user_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getComment_user_id() {
        return comment_user_id;
    }

    public void setComment_user_id(Integer comment_user_id) {
        this.comment_user_id = comment_user_id;
    }

    public Integer getPost_id() {
        return post_id;
    }

    public void setPost_id(Integer post_id) {
        this.post_id = post_id;
    }

    public Integer getReply_id() {
        return reply_id;
    }

    public void setReply_id(Integer reply_id) {
        this.reply_id = reply_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getComment_type() {
        return comment_type;
    }

    public void setComment_type(Integer comment_type) {
        this.comment_type = comment_type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getComment_date() {
        return comment_date;
    }

    public void setComment_date(Long comment_date) {
        this.comment_date = comment_date;
    }

}
