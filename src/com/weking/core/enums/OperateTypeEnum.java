package com.weking.core.enums;

/**
 * 用户操作埋点记录类型
 */
public enum OperateTypeEnum {
    UNKNOWN(0,"未知"),
    GUIDE_POST(1,"引导发文"),
    VIEW_POST(2, "查看贴文"),
    HIDE_POST(3, "隐藏贴文");

    private int type;  //操作类型

    private String name; //名称


    OperateTypeEnum(int type,String name) {
        this.type = type;
        this.name = name;
    }

    public static OperateTypeEnum getTypeEnum(int type) {
        OperateTypeEnum imageTypeEnum = null;
        OperateTypeEnum uploadType[] = OperateTypeEnum.values();
        for (OperateTypeEnum info : uploadType) {
            if (info.getType() == type) {
                imageTypeEnum = info;
            }
        }
        return imageTypeEnum;
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
}
