package com.weking.core.enums;

/**
 * 数字货币类型
 */
public enum CoinTypeEnum {

    SCA(1,"SCA"),
    ETH(2,"ETH"),
    KVT(3,"KVT"),
    BTC(4,"BTC");

    private int type;

    private String name;


    CoinTypeEnum(int type, String name){
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static CoinTypeEnum getEnum(String name){
        CoinTypeEnum coinTypeEnum = null;
        CoinTypeEnum coinTypeEnums[] = CoinTypeEnum.values();
        for (CoinTypeEnum info : coinTypeEnums){
            if (info.getName().equals(name)){
                coinTypeEnum = info;
            }
        }
        return coinTypeEnum;
    }
}
