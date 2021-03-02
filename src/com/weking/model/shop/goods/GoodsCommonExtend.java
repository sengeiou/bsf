package com.weking.model.shop.goods;

public class GoodsCommonExtend extends GoodsCommon {

    private Byte goodsStorageAlarm;

    private Byte goodsVat;

    private String goodsBody;

    private String goodsAttr;

    public Byte getGoodsStorageAlarm() {
        return goodsStorageAlarm;
    }

    public void setGoodsStorageAlarm(Byte goodsStorageAlarm) {
        this.goodsStorageAlarm = goodsStorageAlarm;
    }

    public Byte getGoodsVat() {
        return goodsVat;
    }

    public void setGoodsVat(Byte goodsVat) {
        this.goodsVat = goodsVat;
    }

    public String getGoodsBody() {
        return goodsBody;
    }

    public void setGoodsBody(String goodsBody) {
        this.goodsBody = goodsBody;
    }

    public String getGoodsAttr() {
        return goodsAttr;
    }

    public void setGoodsAttr(String goodsAttr) {
        this.goodsAttr = goodsAttr;
    }

}