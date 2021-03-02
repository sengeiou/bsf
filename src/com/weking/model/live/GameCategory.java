package com.weking.model.live;

/**
 * Created by Administrator on 2018/5/17.
 */
public class GameCategory {

    private Integer category_id;

    private String category_name;

    private String category_url;

    private Short is_vertical;

    private Short enable;

    private Short sort;

    public String getCategory_url() {
        return category_url;
    }

    public void setCategory_url(String category_url) {
        this.category_url = category_url;
    }


    public Short getIs_vertical() {
        return is_vertical;
    }

    public void setIs_vertical(Short is_vertical) {
        this.is_vertical = is_vertical;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name == null ? null : category_name.trim();
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
}
