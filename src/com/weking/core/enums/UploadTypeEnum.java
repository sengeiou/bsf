package com.weking.core.enums;

/**
 * 上传图片类型
 */
public enum UploadTypeEnum {
    //头像
    AVATAR(1, 200, 200, "avatar", "w200"),
    //封面
    COVER(2, 500, 500, "cover", "w500"),
    //动态
    POST(3, 600, 600, "post", "w600"),
    //商城
    SHOP(4, 500, 500, "shop", "w500"),
    //礼物
    GIFT(5, 0, 0, "gift", ""),
    //广告
    ADV(6, 0, 0, "advertisement", ""),
    //视频
    VIDEO(8, 0, 0, "video", ""),

    //邀约
    INVITE(9, 0, 0, "invite", ""),

    //相册
    ALBUM(10, 0, 0, "album", ""),

    //节目表
    SHOWLIVE(11, 0, 0, "showLive", ""),

    //任務
    TASk(12, 0, 0, "task", ""),

    //其他
    OTHER(100, 500, 500, "other", "w500");


    private int value;  //图片类型

    private int width; //图片缩略图宽度

    private int height; //图片缩略图高度

    private String name; //保存文件夹

    private String stylename; //阿里云缩略图样式

    UploadTypeEnum(int value, int width, int height, String name, String stylename) {
        this.value = value;
        this.width = width;
        this.height = height;
        this.name = name;
        this.stylename = stylename;
    }

    public static UploadTypeEnum getTypeEnum(int value) {
        UploadTypeEnum imageTypeEnum = OTHER;
        UploadTypeEnum uploadType[] = UploadTypeEnum.values();
        for (UploadTypeEnum info : uploadType) {
            if (info.getValue() == value) {
                imageTypeEnum = info;
            }
        }
        return imageTypeEnum;
    }

    public int getValue() {
        return value;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public String getStylename() {
        return stylename;
    }

}
