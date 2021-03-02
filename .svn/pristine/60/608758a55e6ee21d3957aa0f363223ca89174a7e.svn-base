/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.weking.core.gash;

import org.dom4j.*;

/**
 * 交易訊息處理介面
 * @author jerryhu
 * @version 1.0.0
 */
public interface ITrans {

    /**
     * 產生交易訊息之 XML 物件
     * @return XML 物件
     */
    public Document generateXmlDoc();
    /***
     * 取得交易訊息之 XML 字串
     * @return XML 字串 
     */
    public String getXMLString();
    /**
     * 產生欲傳遞給 GPS 之交易訊息資料
     * @return 以 BASE64 編碼後之交易訊息
     */
    public String getSendData();
    /**
     * 取得交易驗證壓碼
     * @param pw Content Provider 之密碼
     * @param key 加密密鑰
     * @param iv 加密向量
     * @return Content Provider 交易驗證壓碼
     */
    public String getErqc(String pw, String key, String iv);
    /**
     * 檢測交易驗證壓碼
     * @return 正確或錯誤
     */
    public Boolean verifyErqc();
    /**
     * 取得 GPS 回應驗證壓碼
     * @param key 加密密鑰
     * @param iv 加密向量
     * @return GPS 回應驗證壓碼
     */
    public String getErpc(String key, String iv);
    /**
     * 檢測GPS 回應驗證壓碼
     * @return 正確或錯誤
     */
    public Boolean verifyErpc();

}
