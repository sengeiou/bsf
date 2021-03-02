package com.weking.model.comm;

public class CommArea {
    private Integer id;

    private Integer parentId;

    private String internalCode;

    private String postcode;

    private Integer sortId;

    private Integer enable;

    private String zhCn;

    private String zhTw;

    private String enUs;

    private String ms;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(String internalCode) {
        this.internalCode = internalCode == null ? null : internalCode.trim();
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode == null ? null : postcode.trim();
    }

    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public String getZhCn() {
        return zhCn;
    }

    public void setZhCn(String zhCn) {
        this.zhCn = zhCn == null ? null : zhCn.trim();
    }

    public String getZhTw() {
        return zhTw;
    }

    public void setZhTw(String zhTw) {
        this.zhTw = zhTw == null ? null : zhTw.trim();
    }

    public String getEnUs() {
        return enUs;
    }

    public void setEnUs(String enUs) {
        this.enUs = enUs == null ? null : enUs.trim();
    }

    public String getMs() {
        return ms;
    }

    public void setMs(String ms) {
        this.ms = ms == null ? null : ms.trim();
    }
}