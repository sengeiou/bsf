package com.weking.model.shop.store;

import java.math.BigDecimal;

public class ShopStore {
    private Integer id;

    private String storeName;

    private Integer gradeId;

    private Integer userId;

    private Integer scId;

    private Integer provinceId;

    private String areaInfo;

    private String storeAddress;

    private Byte storeState;

    private Integer storeSort;

    private String storeTime;

    private String storeBanner;

    private String storeAvatar;

    private String storeQq;

    private String storePhone;

    private Byte storeRecommend;

    private Integer storeCredit;

    private Float storeDesccredit;

    private Float storeServicecredit;

    private Float storeDeliverycredit;

    private Integer storeCollect;

    private Integer storeSales;

    private BigDecimal storeFreePrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName == null ? null : storeName.trim();
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getScId() {
        return scId;
    }

    public void setScId(Integer scId) {
        this.scId = scId;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public String getAreaInfo() {
        return areaInfo;
    }

    public void setAreaInfo(String areaInfo) {
        this.areaInfo = areaInfo == null ? null : areaInfo.trim();
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress == null ? null : storeAddress.trim();
    }

    public Byte getStoreState() {
        return storeState;
    }

    public void setStoreState(Byte storeState) {
        this.storeState = storeState;
    }

    public Integer getStoreSort() {
        return storeSort;
    }

    public void setStoreSort(Integer storeSort) {
        this.storeSort = storeSort;
    }

    public String getStoreTime() {
        return storeTime;
    }

    public void setStoreTime(String storeTime) {
        this.storeTime = storeTime == null ? null : storeTime.trim();
    }

    public String getStoreBanner() {
        return storeBanner;
    }

    public void setStoreBanner(String storeBanner) {
        this.storeBanner = storeBanner == null ? null : storeBanner.trim();
    }

    public String getStoreAvatar() {
        return storeAvatar;
    }

    public void setStoreAvatar(String storeAvatar) {
        this.storeAvatar = storeAvatar == null ? null : storeAvatar.trim();
    }

    public String getStoreQq() {
        return storeQq;
    }

    public void setStoreQq(String storeQq) {
        this.storeQq = storeQq == null ? null : storeQq.trim();
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone == null ? null : storePhone.trim();
    }

    public Byte getStoreRecommend() {
        return storeRecommend;
    }

    public void setStoreRecommend(Byte storeRecommend) {
        this.storeRecommend = storeRecommend;
    }

    public Integer getStoreCredit() {
        return storeCredit;
    }

    public void setStoreCredit(Integer storeCredit) {
        this.storeCredit = storeCredit;
    }

    public Float getStoreDesccredit() {
        return storeDesccredit;
    }

    public void setStoreDesccredit(Float storeDesccredit) {
        this.storeDesccredit = storeDesccredit;
    }

    public Float getStoreServicecredit() {
        return storeServicecredit;
    }

    public void setStoreServicecredit(Float storeServicecredit) {
        this.storeServicecredit = storeServicecredit;
    }

    public Float getStoreDeliverycredit() {
        return storeDeliverycredit;
    }

    public void setStoreDeliverycredit(Float storeDeliverycredit) {
        this.storeDeliverycredit = storeDeliverycredit;
    }

    public Integer getStoreCollect() {
        return storeCollect;
    }

    public void setStoreCollect(Integer storeCollect) {
        this.storeCollect = storeCollect;
    }

    public Integer getStoreSales() {
        return storeSales;
    }

    public void setStoreSales(Integer storeSales) {
        this.storeSales = storeSales;
    }

    public BigDecimal getStoreFreePrice() {
        return storeFreePrice;
    }

    public void setStoreFreePrice(BigDecimal storeFreePrice) {
        this.storeFreePrice = storeFreePrice;
    }
}