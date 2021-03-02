/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.weking.core.gash;

import com.weking.core.gash.Crypt3Des;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * 交易訊息處理介面
 * @author jerryhu
 * @version 1.0.0
 */
public class Trans implements ITrans {

    /** 交易或回應訊息 XML 物件 */
    private Document doc;
    /** 解析交易或回應訊息各節點的值 */
    private Map<String, String> nodes;

    /** 收到 GPS 回應的原始資料 */
    private String odata;
    /** 以 BASE64 解碼後的 GPS 回應資料 */
    private String data;
    /** 加密密鑰 */
    private String key;
    /** 加密向量 */
    private String iv;
    /** Content Provider 密碼 */
    private String pwd;
    /** 回應訊息 */
    private String msg;
    /** 加密資料 */
    private String encrypt_data;
    /** 以 BASE64 編碼以後的加密資料 */
    private String base64_encrypt_data;
    /** GPS 解析資料是否成功 */
    private Boolean is_parsed;

    /**
     * 取得以 BASE64 解碼後的 GPS 回應資料
     * @return 以 BASE64 解碼後的 GPS 回應資料
     */
    public String getData() {
        return data;
    }

    /**
     * 設定以 BASE64 解碼後的 GPS 回應資料
     * @param data 以 BASE64 解碼後的 GPS 回應資料
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * 取得 GPS 回應資料
     * @return GPS 回應資料
     */
    public String getOdata() {
        return odata;
    }

    /**
     * 設定 GPS 回應資料
     * @param data GPS 回應資料
     */
    public void setOdata(String odata) {
        this.odata = odata;
    }

    /**
     * 取得 BASE64 編碼以後的加密資料
     * @return  BASE64 編碼以後加密資料
     */
    public String getBase64_encrypt_data() {
        return base64_encrypt_data;
    }

    /**
     * 設定 BASE64 編碼以後的加密資料
     * @param base64_encrypt_data  BASE64 編碼以後加密資料
     */
    public void setBase64_encrypt_data(String base64_encrypt_data) {
        this.base64_encrypt_data = base64_encrypt_data;
    }

    /**
     * 取得加密資料
     * @return 加密資料
     */
    public String getEncrypt_data() {
        return encrypt_data;
    }

    /**
     * 設定加密資料
     * @param encrypt_data 加密資料
     */
    public void setEncrypt_data(String encrypt_data) {
        this.encrypt_data = encrypt_data;
    }

    /**
     * 取得加密資料
     * @return 加密資料
     */
    public String getEncode_data() {
        return encrypt_data;
    }

    /**
     * 設定加密資料
     * @param encrypt_data 加密資料
     */
    public void setEncode_data(String encrypt_data) {
        this.encrypt_data = encrypt_data;
    }

    /**
     * 取得訊息字串
     * @return 訊息字串
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 設定訊息字串
     * @param msg 訊息字串
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 取得 GPS 解析資料是否成功
     * @return GPS 解析資料是否成功
     */
    public Boolean getIs_parsed() {
        return is_parsed;
    }

    /**
     * 設定 GPS 解析資料是否成功
     * @param is_parsed GPS 解析資料是否成功
     */
    public void setIs_parsed(Boolean is_parsed) {
        this.is_parsed = is_parsed;
    }

    /**
     * 取得交易訊息 XML 物件
     * @return 交易訊息 XML 物件
     */
    public Document getDoc() {
        return doc;
    }

    /**
     * 設定交易訊息 XML 物件
     * @param doc 交易訊息 XML 物件
     */
    public void setDoc(Document doc) {
        this.doc = doc;
    }

    /**
     * 取得加密向量值
     * @return 加密向量值
     */
    public String getIv() {
        return iv;
    }

    /**
     * 設定加密向量值
     * @param iv 加密向量值
     */
    public void setIv(String iv) {
        this.iv = iv;
    }

    /**
     * 取得加密密鑰
     * @return 加密密鑰
     */
    public String getKey() {
        return key;
    }

    /**
     * 設定加密密鑰
     * @param key 加密密鑰
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 取得 Content Provider 密碼
     * @return Content Provider 密碼
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * 設定 Content Provider 密碼
     * @param pwd Content Provider 密碼
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * 建構交易物件
     * @param odata 交易訊息資料
     */
    private void _Trans(String odata) {
        
        if ( odata.isEmpty() || odata.equals("") ){
            
            this.doc = DocumentFactory.getInstance().createDocument();
            this.doc.addElement("TRANS");

            this.nodes = new LinkedHashMap<String, String>( );

            this.is_parsed = true;
            
        }else{

            this.odata = odata;
            this.data = Crypt3Des.base64_decode(odata);

            try{
                
                this.doc = DocumentHelper.parseText( this.data );

            }catch(Exception ex){}

            this.is_parsed = parseNodes();

        }
    }

    /**
     * 交易物件建構式
     */
    public Trans() {

        _Trans( "" );

    }

    /**
     * 交易物件建構式
     * @param odata 交易訊息
     */
    public Trans(String odata) {
        
        _Trans(odata);
        
    }

    /**
     * 取得交易內容各節點資料
     * @return 各節點資料
     */
    public Map<String, String> getNodes() {
        return this.nodes;
    }

    /**
     * 設定交易內容各節點資料
     * @param n 各節點資料
     */
    public void setNodes(Map<String, String> n) {
        this.nodes = n;
    }

    /**
     * 設定交易內容節點以及其值
     * @param key 節點名稱
     * @param value 節點值值
     */
    public void putNode(String key, String value) {
        this.nodes.put(key, value);
    }

    /**
     * 將交易訊息解析為各節點對應資料
     * @return 解析是否成功
     */
    public Boolean parseNodes() {

        Element root;
        Element node;

        Boolean success;

        success = false;
        this.nodes = new LinkedHashMap<String, String>( );
        root = this.doc.getRootElement();
        if ( root.elements().size() > 0 ){
            Iterator it = root.elementIterator();
            while(it.hasNext()){
                node = (Element) it.next();

                putNode(node.getName(), node.getText());
            }

            success = true;
        }

        return success;
    }

    public String getSendData() {
        this.generateXmlDoc();

        return Crypt3Des.base64_encode(getXMLString());
    }

    public Document generateXmlDoc() {
        Iterator it;
        Element root;
        Element node;

        Object obj;
        String k2;

        this.doc = DocumentFactory.getInstance().createDocument();
        root = this.doc.addElement("TRANS");

        it = this.nodes.keySet().iterator();
        while(it.hasNext()){
            obj = it.next();
            k2 = obj.toString();

            node = root.addElement(k2);
            node.setText(this.nodes.get(k2));
        }

        is_parsed = true;
        return this.doc;
    }

    public String getXMLString() {
        return formatXML(this.doc, "UTF-8");
        //return this.doc.asXML();
    }

    /**
     * 從 XML 物件轉換為 XML 字串
     * @param document XML 物件
     * @param charset 字碼設定
     * @return XML 字串
     */
    private static String formatXML(Document document, String charset) {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding(charset);
            StringWriter sw = new StringWriter();
            XMLWriter xw = new XMLWriter(sw, format);
            try {
                xw.write(document);
                xw.flush();
                xw.close(); 
            } catch (IOException e) { }
            return sw.toString();
    }

    public String getErqc(String pwd, String key, String iv) {

        String cid;
        String coid;
        String cuid;
        String amt;

        if ( !this.is_parsed ) 
            this.msg = "trans data format is not valid";
        else if (this.key.isEmpty() || this.iv.isEmpty())
            this.msg = "key and iv is not valid";

        this.key = key;
        this.iv = iv;
        this.pwd = pwd;

        cid = "";
        coid = "";
        cuid = "";
        amt = "";

        // vdata = cid + coid + cuid + amt(12,2) + pwd

        // Get Content ID
        cid = this.nodes.get("CID");

        // Get Content Ordere ID
        coid = this.nodes.get("COID");

        // Get Trans Currency ID
        cuid = this.nodes.get("CUID");

        // Get Trans Amount need parse to fix format
        amt = this.nodes.get("AMOUNT");

        return getErqc(cid, coid, cuid, amt, pwd);
    }

    /**
     * 取得交易驗證壓碼
     * @param cid Content Provider 代碼
     * @param coid Content Provider 交易單號
     * @param cuid 交易幣別
     * @param amt 交易金額
     * @param pwd Content Provider 密碼
     * @return Content Provider 交易驗證壓碼
     */
    public String getErqc(String cid, String coid, String cuid, String amt, String pwd) {

        Crypt3Des des;

        String erqc;
        String vdata;

        erqc = "";
        vdata = "%s%s%s%s%s";

        // 驗證用的 AMOUNT 需整理成 14 碼
        amt = formatAmount(amt);

        //$amt = "00000000005000";
        this.encrypt_data = String.format(vdata, cid, coid, cuid, amt, pwd);

        des = new Crypt3Des(this.key,this.iv);


        try{
            this.base64_encrypt_data = des.encrypt(this.encrypt_data); // 已經 base64 過
            erqc = Crypt3Des.sha1(this.base64_encrypt_data);
        }catch(Exception ex){}

        return erqc;
    }

    public Boolean verifyErqc() {

        String erqc;
        Boolean success;

        success = false;
        erqc = getErqc(this.pwd, this.key, this.iv);

        success = ( erqc.equals( this.nodes.get("ERQC") ) );

        return success;

    }

    public String getErpc(String key, String iv) {

        String cid;
        String coid;
        String rrn;
        String cuid;
        String amt;
        String rcode;

        if ( !this.is_parsed )
            this.msg = "trans data format is not valid";
        else if (this.key.isEmpty() || this.iv.isEmpty())
            this.msg = "key and iv is not valid";

        this.key = key;
        this.iv = iv;

        cid = "";
        coid = "";
        rrn = "";
        cuid = "";
        amt = "";
        rcode = "";

        // vdata = cid + coid + cuid + amt(12,2) + $rcode

        // Get Content ID
        cid = this.nodes.get("CID");

        // Get Content Ordere ID
        coid = this.nodes.get("COID");

        // Get GPS Ordere ID
        rrn = this.nodes.get("RRN");

        // Get Trans Currency ID
        cuid = this.nodes.get("CUID");

        // Get Trans Amount need parse to fix format
        amt = this.nodes.get("AMOUNT");

        // Get RCODE
        rcode = this.nodes.get("RCODE");

        return getErpc(cid, coid, rrn, cuid, amt, rcode);
    }

    /**
     * 取得 GPS 回應驗證壓碼
     * @param cid Content Provider 代碼
     * @param coid Content Provider 交易單號
     * @param rrn GPS 交易單號
     * @param cuid 交易幣別
     * @param amt 交易金額
     * @param rcode 交易結果代碼
     * @return GPS 回應驗證壓碼
     */
    public String getErpc(String cid, String coid, String rrn, String cuid, String amt, String rcode) {

        Crypt3Des des;

        String erqc;
        String vdata;
        
        erqc = "";
        vdata = "%s%s%s%s%s%s"; // vdata = cid + coid + rrn + cuid + amt(12,2) + $rcode

        // 驗證用的 AMOUNT 需整理成 14 碼
        amt = formatAmount(amt);

        //$amt = "00000000005000";
        this.encrypt_data = String.format(vdata, cid, coid, rrn, cuid, amt, rcode);

        des = new Crypt3Des(this.key,this.iv);

        try{
            this.base64_encrypt_data = des.encrypt(this.encrypt_data); // 已經 base64 過
            erqc = Crypt3Des.sha1(this.base64_encrypt_data);
        }catch(Exception ex){}

        return erqc;
    }

    public Boolean verifyErpc() {

        String erpc;
        Boolean success;

        success = false;
        erpc = getErpc(this.key, this.iv);

        success = ( erpc.equals( this.nodes.get("ERPC") ) );
        
        return success;

    }

    /**
     * 以指定字串和長度向右填滿
     * @param s 填入字串
     * @param n 長度
     * @return 向右填滿之字串
     */
    public static String padRight(String s, int n) {
         return String.format("%1$-" + n + "s", s).replace(' ', '0');
    }

    /**
     * 以指定字串和長度向左填滿
     * @param s 填入字串
     * @param n 長度
     * @return 向左填滿之字串
     */
    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s).replace(' ', '0');
    }

    /**
     * 格式化交易金額
     * @param amt 交易金額
     * @return 格式化後之交易金額 
     */
    public static String formatAmount(String amt) {

        int dotpos;

        if (amt.indexOf(".") > -1)
        {
            dotpos = amt.indexOf(".");
            amt = amt.substring(0, dotpos) + ((amt.length() - dotpos) > 3 ? amt.substring(dotpos + 1, 2) : padRight(amt.substring(dotpos+1), 2));// str_pad(substr($amt, (strpos($amt, ".") + 1)), 2, "0"));
            amt = padLeft(amt, 14);
        }
        else
        {
            amt = padLeft(amt, 12) + "00";
        }

        return amt;
        
    }
}
