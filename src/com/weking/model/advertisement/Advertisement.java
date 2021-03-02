package com.weking.model.advertisement;

public class Advertisement {
    private Long id;

    private String imgUrl;

    private String linkUrl;

    private Double width;

    private Double height;
    
    private String title;

    private int type;

    private String en_img_url;

    private String my_img_url;

    private String ad_unit_id;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAdv_type() {
        return adv_type;
    }

    public void setAdv_type(int adv_type) {
        this.adv_type = adv_type;
    }

    private int adv_type;

    public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl == null ? null : linkUrl.trim();
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

    public String getEn_img_url() {
        return en_img_url;
    }

    public void setEn_img_url(String en_img_url) {
        this.en_img_url = en_img_url;
    }

    public String getMy_img_url() {
        return my_img_url;
    }

    public void setMy_img_url(String my_img_url) {
        this.my_img_url = my_img_url;
    }

    public String getAd_unit_id() {
        return ad_unit_id;
    }

    public void setAd_unit_id(String ad_unit_id) {
        this.ad_unit_id = ad_unit_id;
    }
}