package com.weking.model.live;

public class LiveTag {
    private Integer id;

    private String tagName;

    private Short enable;

    private Short sort;

    private Short tag_value;

    private String en_name;

    private String ms_name;

    private String tw_name;

    private Integer tag_type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName == null ? null : tagName.trim();
    }

    public Short getEnable() {
        return enable;
    }

    public void setEnable(Short enable) {
        this.enable = enable;
    }

    public Short getSort() {
        return sort;
    }

    public void setSort(Short sort) {
        this.sort = sort;
    }

    public Short getTag_value() {
        return tag_value;
    }

    public void setTag_value(Short tag_value) {
        this.tag_value = tag_value;
    }

    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
    }

    public String getMs_name() {
        return ms_name;
    }

    public void setMs_name(String ms_name) {
        this.ms_name = ms_name;
    }

    public String getTw_name() {
        return tw_name;
    }

    public void setTw_name(String tw_name) {
        this.tw_name = tw_name;
    }

    public Integer getTag_type() {
        return tag_type;
    }

    public void setTag_type(Integer tag_type) {
        this.tag_type = tag_type;
    }
}