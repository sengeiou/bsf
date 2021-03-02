package com.weking.model.shop.adv;

public class ShopAdv {
    private Integer id;

    private String advTitle;

    private Byte advSort;

    private String imgUrl;

    private String extend;

    private Boolean isShow;

    private Double width;

    private Double height;

    private Byte advType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdvTitle() {
        return advTitle;
    }

    public void setAdvTitle(String advTitle) {
        this.advTitle = advTitle == null ? null : advTitle.trim();
    }

    public Byte getAdvSort() {
        return advSort;
    }

    public void setAdvSort(Byte advSort) {
        this.advSort = advSort;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend == null ? null : extend.trim();
    }

    public Boolean getIsShow() {
        return isShow;
    }

    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Byte getAdvType() {
        return advType;
    }

    public void setAdvType(Byte advType) {
        this.advType = advType;
    }
}