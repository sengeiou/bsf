package com.weking.model.account;

import com.weking.model.Base;

import java.math.BigDecimal;

public class AccountInfo extends Base {
    private Integer id;

    private String phone;

    private String email;

    private String account;

    private String password;

    private String wxNum;

    private String qqNum;

    private String wbNum;

    private String fbNum;

    private String googleNum;

    private String kakao_num;

    private String apple_num;

    private String line_num;

    private String twitterNum;

    private String nickname;

    private Short sex;

    private String address;

    private String picheadUrl;

    private Integer birthday;

    private String signiture;

    private Long regTime;

    private String clientid;

    private String devicetoken;

    private String openid;

    private String inviteCode;

    private Byte emailState;

    private Integer parentId;

    private Integer isblack;

    private Integer sorts;

    private Integer areaCode;

    private Integer experience;

    private int level;

    private String langCode;

    private double lng;

    private double lat;

    private Short enable;

    private Short anchor_level;

    private Integer anchor_experience;

    private Short role;

    private String wallet_pwd;

    private String wallet_currency;

    private Integer hots;

    private Integer post_hots;

    private Integer is_official;

    private Integer guild_id;

    private BigDecimal ratio;

    private Double withdraw_rate;

    private Byte is_update_nickname;

    private String user_name;

    private String user_email;

    private Integer vip_experience;

    private int vip_level;

    private Integer cdn_option;

    public String getWallet_currency() {
        return wallet_currency;
    }

    public void setWallet_currency(String wallet_currency) {
        this.wallet_currency = wallet_currency;
    }

    public String getWallet_pwd() {
        return wallet_pwd;
    }

    public void setWallet_pwd(String wallet_pwd) {
        this.wallet_pwd = wallet_pwd;
    }

    public Short getRole() {
        return role;
    }

    public void setRole(Short role) {
        this.role = role;
    }

    public Short getAnchor_level() {
        return anchor_level;
    }

    public void setAnchor_level(Short anchor_level) {
        this.anchor_level = anchor_level;
    }

    public String getKakao_num() {
        return kakao_num;
    }

    public void setKakao_num(String kakao_num) {
        this.kakao_num = kakao_num;
    }

    public String getGoogleNum() {
        return googleNum;
    }

    public void setGoogleNum(String googleNum) {
        this.googleNum = googleNum;
    }

    public Integer getIsblack() {
        return isblack;
    }

    public void setIsblack(Integer isblack) {
        this.isblack = isblack;
    }

    public Byte getEmailState() {
        return emailState;
    }

    public void setEmailState(Byte emailState) {
        this.emailState = emailState;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getDevicetoken() {
        return devicetoken;
    }

    public void setDevicetoken(String devicetoken) {
        this.devicetoken = devicetoken;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClient_id(String clientid) {
        this.clientid = clientid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getWxNum() {
        return wxNum;
    }

    public void setWxNum(String wxNum) {
        this.wxNum = wxNum == null ? null : wxNum.trim();
    }

    public String getQqNum() {
        return qqNum;
    }

    public void setQqNum(String qqNum) {
        this.qqNum = qqNum == null ? null : qqNum.trim();
    }

    public String getWbNum() {
        return wbNum;
    }

    public void setWbNum(String wbNum) {
        this.wbNum = wbNum == null ? null : wbNum.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public Short getSex() {
        return sex;
    }

    public void setSex(Short sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getPicheadUrl() {
        return picheadUrl;
    }

    public void setPicheadUrl(String picheadUrl) {
        this.picheadUrl = picheadUrl == null ? null : picheadUrl.trim();
    }

    public Integer getBirthday() {
        return birthday;
    }

    public void setBirthday(Integer birthday) {
        this.birthday = birthday;
    }

    public String getSigniture() {
        return signiture;
    }

    public void setSigniture(String signiture) {
        this.signiture = signiture == null ? null : signiture.trim();
    }

    public Long getRegTime() {
        return regTime;
    }

    public void setRegTime(Long regTime) {
        this.regTime = regTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFbNum() {
        return fbNum;
    }

    public void setFbNum(String fbNum) {
        this.fbNum = fbNum;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Integer getSorts() {
        return sorts;
    }

    public void setSorts(Integer sorts) {
        this.sorts = sorts;
    }

    public Integer getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(Integer areaCode) {
        this.areaCode = areaCode;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Short getEnable() {
        return enable;
    }

    public void setEnable(Short enable) {
        this.enable = enable;
    }

    public String getTwitterNum() {
        return twitterNum;
    }

    public void setTwitterNum(String twitterNum) {
        this.twitterNum = twitterNum;
    }

    public Integer getHots() {
        return hots;
    }

    public void setHots(Integer hots) {
        this.hots = hots;
    }

    public Integer getPost_hots() {
        return post_hots;
    }

    public void setPost_hots(Integer post_hots) {
        this.post_hots = post_hots;
    }

    public Integer getIs_official() {
        return is_official;
    }

    public void setIs_official(Integer is_official) {
        this.is_official = is_official;
    }

    public Integer getGuild_id() {
        return guild_id;
    }

    public void setGuild_id(Integer guild_id) {
        this.guild_id = guild_id;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }

    public Double getWithdraw_rate() {
        return withdraw_rate;
    }

    public void setWithdraw_rate(Double withdraw_rate) {
        this.withdraw_rate = withdraw_rate;
    }

    public Byte getIs_update_nickname() {
        return is_update_nickname;
    }

    public void setIs_update_nickname(Byte is_update_nickname) {
        this.is_update_nickname = is_update_nickname;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public Integer getVip_experience() {
        return vip_experience;
    }

    public void setVip_experience(Integer vip_experience) {
        this.vip_experience = vip_experience;
    }

    public int getVip_level() {
        return vip_level;
    }

    public void setVip_level(int vip_level) {
        this.vip_level = vip_level;
    }

    public Integer getAnchor_experience() {
        return anchor_experience;
    }

    public void setAnchor_experience(Integer anchor_experience) {
        this.anchor_experience = anchor_experience;
    }

    public String getApple_num() {
        return apple_num;
    }

    public void setApple_num(String apple_num) {
        this.apple_num = apple_num;
    }

    public String getLine_num() {
        return line_num;
    }

    public void setLine_num(String line_num) {
        this.line_num = line_num;
    }

    public Integer getCdn_option() {
        return cdn_option;
    }

    public void setCdn_option(Integer cdn_option) {
        this.cdn_option = cdn_option;
    }
}