package com.weking.core.enums;

/**
 * @author Xujm
 * 用户资源类型枚举
 */
public enum UserDataTypeEnum {
     //照片
    PHOTO(1,"photo",6),

    //视频
    AUTH_VIDEO(2,"auth_video",1);

    private String dataKey;

    private int dataType;
    /**
     * 允许数量
     */
    private int num;


    UserDataTypeEnum(int dataType, String dataKey, int num){
        this.dataType = dataType;
        this.dataKey = dataKey;
        this.num = num;
    }

    public String getDataKey(){
        return dataKey;
    }

    public int getDataType(){
        return dataType;
    }

    public int getNum(){
        return num;
    }


    public static UserDataTypeEnum getEnum(int dataType){
        UserDataTypeEnum userDataTypeEnum = null;
        UserDataTypeEnum[] userDataType = UserDataTypeEnum.values();
        for(UserDataTypeEnum userData:userDataType){
            if(userData.getDataType() == dataType){
                userDataTypeEnum = userData;
            }
        }
        return userDataTypeEnum;
    }


}
