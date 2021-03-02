package com.weking.model.certification;

public class Certification {
    private Integer id;

    private Integer userId;

    private String realName;

    private String idpicUrl;

    private String idNum;

    private String phone;

    private Short ispass;

    private String unReason;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName == null ? null : realName.trim();
    }

    public String getIdpicUrl() {
        return idpicUrl;
    }

    public void setIdpicUrl(String idpicUrl) {
        this.idpicUrl = idpicUrl == null ? null : idpicUrl.trim();
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum == null ? null : idNum.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Short getIspass() {
        return ispass;
    }

    public void setIspass(Short ispass) {
        this.ispass = ispass;
    }

    public String getUnReason() {
        return unReason;
    }

    public void setUnReason(String unReason) {
        this.unReason = unReason == null ? null : unReason.trim();
    }
}