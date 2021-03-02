package com.weking.model.shop.goods;

import java.math.BigDecimal;

public class Goods {
    private Integer id;

    private Integer goodsCommonid;

    private Integer goodsParentid;

    private String goodsName;

    private String goodsJingle;

    private Integer storeId;

    private String storeName;

    private Integer gcId;

    private Integer brandId;

    private BigDecimal goodsPrice;

    private BigDecimal goodsPromotionPrice;

    private Byte goodsPromotionType;

    private BigDecimal goodsMarketprice;

    private Byte goodsStorageAlarm;

    private Integer goodsClick;

    private Integer goodsSalenum;

    private Integer goodsCollect;

    private Integer goodsStorage;

    private String goodsImage;

    private Byte goodsState;

    private Byte goodsVerify;

    private Long goodsAddtime;

    private Long goodsEdittime;

    private Integer colorId;

    private Integer saleId;

    private BigDecimal goodsFreight;

    private Byte goodsVat;

    private Byte goodsCommend;

    private Byte evaluationGoodStar;

    private Integer evaluationCount;

    private String goodsSpec;

    private BigDecimal coin_price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGoodsCommonid() {
        return goodsCommonid;
    }

    public void setGoodsCommonid(Integer goodsCommonid) {
        this.goodsCommonid = goodsCommonid;
    }

    public Integer getGoodsParentid() {
        return goodsParentid;
    }

    public void setGoodsParentid(Integer goodsParentid) {
        this.goodsParentid = goodsParentid;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public String getGoodsJingle() {
        return goodsJingle;
    }

    public void setGoodsJingle(String goodsJingle) {
        this.goodsJingle = goodsJingle == null ? null : goodsJingle.trim();
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getGcId() {
        return gcId;
    }

    public void setGcId(Integer gcId) {
        this.gcId = gcId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public BigDecimal getGoodsPromotionPrice() {
        return goodsPromotionPrice;
    }

    public void setGoodsPromotionPrice(BigDecimal goodsPromotionPrice) {
        this.goodsPromotionPrice = goodsPromotionPrice;
    }

    public Byte getGoodsPromotionType() {
        return goodsPromotionType;
    }

    public void setGoodsPromotionType(Byte goodsPromotionType) {
        this.goodsPromotionType = goodsPromotionType;
    }

    public BigDecimal getGoodsMarketprice() {
        return goodsMarketprice;
    }

    public void setGoodsMarketprice(BigDecimal goodsMarketprice) {
        this.goodsMarketprice = goodsMarketprice;
    }

    public Byte getGoodsStorageAlarm() {
        return goodsStorageAlarm;
    }

    public void setGoodsStorageAlarm(Byte goodsStorageAlarm) {
        this.goodsStorageAlarm = goodsStorageAlarm;
    }

    public Integer getGoodsClick() {
        return goodsClick;
    }

    public void setGoodsClick(Integer goodsClick) {
        this.goodsClick = goodsClick;
    }

    public Integer getGoodsSalenum() {
        return goodsSalenum;
    }

    public void setGoodsSalenum(Integer goodsSalenum) {
        this.goodsSalenum = goodsSalenum;
    }

    public Integer getGoodsCollect() {
        return goodsCollect;
    }

    public void setGoodsCollect(Integer goodsCollect) {
        this.goodsCollect = goodsCollect;
    }

    public Integer getGoodsStorage() {
        return goodsStorage;
    }

    public void setGoodsStorage(Integer goodsStorage) {
        this.goodsStorage = goodsStorage;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage == null ? null : goodsImage.trim();
    }

    public Byte getGoodsState() {
        return goodsState;
    }

    public void setGoodsState(Byte goodsState) {
        this.goodsState = goodsState;
    }

    public Byte getGoodsVerify() {
        return goodsVerify;
    }

    public void setGoodsVerify(Byte goodsVerify) {
        this.goodsVerify = goodsVerify;
    }

    public Long getGoodsAddtime() {
        return goodsAddtime;
    }

    public void setGoodsAddtime(Long goodsAddtime) {
        this.goodsAddtime = goodsAddtime;
    }

    public Long getGoodsEdittime() {
        return goodsEdittime;
    }

    public void setGoodsEdittime(Long goodsEdittime) {
        this.goodsEdittime = goodsEdittime;
    }

    public Integer getColorId() {
        return colorId;
    }

    public void setColorId(Integer colorId) {
        this.colorId = colorId;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public BigDecimal getGoodsFreight() {
        return goodsFreight;
    }

    public void setGoodsFreight(BigDecimal goodsFreight) {
        this.goodsFreight = goodsFreight;
    }

    public Byte getGoodsVat() {
        return goodsVat;
    }

    public void setGoodsVat(Byte goodsVat) {
        this.goodsVat = goodsVat;
    }

    public Byte getGoodsCommend() {
        return goodsCommend;
    }

    public void setGoodsCommend(Byte goodsCommend) {
        this.goodsCommend = goodsCommend;
    }

    public Byte getEvaluationGoodStar() {
        return evaluationGoodStar;
    }

    public void setEvaluationGoodStar(Byte evaluationGoodStar) {
        this.evaluationGoodStar = evaluationGoodStar;
    }

    public Integer getEvaluationCount() {
        return evaluationCount;
    }

    public void setEvaluationCount(Integer evaluationCount) {
        this.evaluationCount = evaluationCount;
    }

    public String getGoodsSpec() {
        return goodsSpec;
    }

    public void setGoodsSpec(String goodsSpec) {
        this.goodsSpec = goodsSpec == null ? null : goodsSpec.trim();
    }

    public BigDecimal getCoin_price() {
        return coin_price;
    }

    public void setCoin_price(BigDecimal coin_price) {
        this.coin_price = coin_price;
    }
}