package com.weking.model.account;

/**
 * @author Xujm
 */
public class UserBase {

    private String nickname;

    private String account;

    private String avatar;

    private String signiture;

    private Integer level;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public String getSigniture() {
        return signiture;
    }

    public void setSigniture(String signiture) {
        this.signiture = signiture;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
