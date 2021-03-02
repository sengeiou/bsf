package com.weking.core.enums;

/**
 *
 * @author Xujm
 * @date 2017/11/21
 */
public enum EditTypeEnum {
    //昵称
    NICKNAME("nickname",1,7),
    //头像
    AVATAR("avatar",0,0);

    private String editKey;

    private int editTime;
    /**
     * 0免费 1扣钱 2限时
     */
    private int type;

    EditTypeEnum(String editKey,int type,int editTime){
        this.type = type;
        this.editKey = editKey;
        this.editTime = editTime;
    }


    public String getEditKey(){
        return editKey;
    }

    public int getEditTime(){
        return editTime;
    }

    public int getType(){
        return type;
    }

    public static EditTypeEnum getEnum(String editKey){
        EditTypeEnum editTypeEnum = null;
        EditTypeEnum consumeType[] = EditTypeEnum.values();
        for (EditTypeEnum info : consumeType){
            if (info.getEditKey().equals(editKey)){
                editTypeEnum = info;
            }
        }
        return editTypeEnum;
    }
}
