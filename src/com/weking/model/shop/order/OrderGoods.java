package com.weking.model.shop.order;

import java.math.BigDecimal;

public class OrderGoods {
    private Integer id;

    private Integer orderId;

    private Integer goodsId;

    private String goodsName;

    private BigDecimal goodsPrice;

    private BigDecimal goodsMarketprice;

    private Integer goodsNum;

    private String goodsImage;

    private BigDecimal goodsPayPrice;

    private Integer storeId;

    private String storeName;

    private Integer buyerId;

    private String goodsSpec;

    private BigDecimal coin_price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public BigDecimal getGoodsMarketprice() {
        return goodsMarketprice;
    }

    public void setGoodsMarketprice(BigDecimal goodsMarketprice) {
        this.goodsMarketprice = goodsMarketprice;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage == null ? null : goodsImage.trim();
    }

    public BigDecimal getGoodsPayPrice() {
        return goodsPayPrice;
    }

    public void setGoodsPayPrice(BigDecimal goodsPayPrice) {
        this.goodsPayPrice = goodsPayPrice;
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

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
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