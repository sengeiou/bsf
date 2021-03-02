package com.weking.core.gash;

import com.weking.cache.WKCache;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2017/7/24.
 */
public class gash {

    private static Logger log = Logger.getLogger(gash.class);

    private static final String key;
    private static final String iv;
    private static final String returnUrl;
    private static final String cid;
    private static final String pwd;
    public static String settleUrl = "https://api.eg.gashplus.com/CP_Module/settle.asmx?wsdl";
    public static String orderUrl = "https://api.eg.gashplus.com/CP_Module/checkorder.asmx?wsdl";

    static {
        key = WKCache.get_system_cache("pay.gash.key");
        iv = WKCache.get_system_cache("pay.gash.iv");
        returnUrl = WKCache.get_system_cache("pay.gash.h5");
        cid = WKCache.get_system_cache("pay.gash.cid");
        pwd = WKCache.get_system_cache("pay.gash.pwd");
        if(LibSysUtils.toBoolean(WKCache.get_system_cache("weking.config.debug"))){
            settleUrl = "https://stage-api.eg.gashplus.com/CP_Module/settle.asmx?wsdl";
            orderUrl = "https://stage-api.eg.gashplus.com/CP_Module/checkorder.asmx?wsdl";
        }
    }

    public static String execute(int userId, String orderSn,String amount) {
        Trans trans;
        // get information from request object
        String erqc;// = request.getParameter("user");
        trans = new Trans();
        trans.setKey(key);
        trans.setIv(iv);
        trans.setPwd(pwd);
        // 交易訊息代碼
        trans.putNode("MSG_TYPE", "0100");
        // 交易處理代碼
        trans.putNode("PCODE", "300000"); // 一般交易請使用 300000, 月租交易請使用 303000, 月租退租請使用 330000
        // 商家遊戲代碼
        trans.putNode("CID", cid);
        // 商家訂單編號
        trans.putNode("COID", orderSn);
        // 幣別 ISO Alpha Code
        trans.putNode("CUID", "TWD");
        // 交易金額
        trans.putNode("AMOUNT", amount);
        // 商家接收交易結果網址
        trans.putNode("RETURN_URL", returnUrl);
        // 是否指定付款代收業者
        trans.putNode("ORDER_TYPE", "E"); // 請固定填 M
        //付款代收业者代码
//        trans.putNode("PAID", paid);
        // 交易備註 ( 此為選填 )
        //trans.putNode("MEMO", "測試交易"); // 請填寫此筆交易之備註內容
        // 商家商品名稱 ( 此為選填 )
        //trans.putNode("PRODUCT_NAME", "商家商品 I");
        // 商家商品代碼 ( 此為選填 )
        //trans.putNode("PRODUCT_ID", "");
        // 玩家帳號 ( 此為選填 )
        trans.putNode("USER_ACCTID", LibSysUtils.toString(userId));
        // ERP ID
        //trans.putNode("ERP_ID", goodsId);
        // 以商家密碼、商家密鑰 I , II ( 已於 Common.php 內設定 ) 取得 ERQC
        erqc = trans.getErqc(trans.getPwd(), trans.getKey(), trans.getIv());
        // 商家交易驗證壓碼
        trans.putNode("ERQC", erqc);
        // 取得送出之交易資料
        return trans.getSendData();
    }


    private static String settleData(String orderSn,String amount) {
        Trans trans;
        // get information from request object
        String erqc;// = request.getParameter("user");
        trans = new Trans();
        trans.setKey(key);
        trans.setIv(iv);
        trans.setPwd(pwd);
        // 交易訊息代碼
        trans.putNode("MSG_TYPE", "0500");
        // 交易處理代碼
        trans.putNode("PCODE", "300000"); // 一般交易請使用 300000, 月租交易請使用 303000, 月租退租請使用 330000
        // 商家遊戲代碼
        trans.putNode("CID", cid);
        // 商家訂單編號
        trans.putNode("COID", orderSn);
        // 幣別 ISO Alpha Code
        trans.putNode("CUID", "TWD");
        // 交易金額
        trans.putNode("AMOUNT", amount);
        // 玩家帳號 ( 此為選填 )
        //trans.putNode("USER_ACCTID", LibSysUtils.toString(userId));
        //付款代收业者代码
        //trans.putNode("PAID", paid);
        // 以商家密碼、商家密鑰 I , II ( 已於 Common.php 內設定 ) 取得 ERQC
        erqc = trans.getErqc(trans.getPwd(), trans.getKey(), trans.getIv());
        // 商家交易驗證壓碼
        trans.putNode("ERQC", erqc);
        // 取得送出之交易資料
        return trans.getSendData();
    }

    private static String orderData(String orderSn,String amount) {
        Trans trans;
        // get information from request object
        String erqc;// = request.getParameter("user");
        trans = new Trans();
        trans.setKey(key);
        trans.setIv(iv);
        trans.setPwd(pwd);
        // 交易訊息代碼
        trans.putNode("MSG_TYPE", "0100");
        // 交易處理代碼
        trans.putNode("PCODE", "200000"); // 一般交易請使用 300000, 月租交易請使用 303000, 月租退租請使用 330000
        // 商家遊戲代碼
        trans.putNode("CID", cid);
        // 商家訂單編號
        trans.putNode("COID", orderSn);
        // 幣別 ISO Alpha Code
        trans.putNode("CUID", "TWD");
        // 交易金額
        trans.putNode("AMOUNT", amount);
        // 以商家密碼、商家密鑰 I , II ( 已於 Common.php 內設定 ) 取得 ERQC
        erqc = trans.getErqc(trans.getPwd(), trans.getKey(), trans.getIv());
        // 商家交易驗證壓碼
        trans.putNode("ERQC", erqc);
        // 取得送出之交易資料
        return trans.getSendData();
    }

    private static JSONObject getData(String data){
        Trans trans;
        JSONObject object = new JSONObject();
        trans = new Trans(data);
        trans.setKey(key);
        trans.setIv(iv);
        trans.setPwd(pwd);
        String recvRCode = trans.getNodes().get("RCODE");
        String recvPayStatus = trans.getNodes().get("PAY_STATUS");
        String recvERPC = trans.getNodes().get("ERPC");
        String recvCoid = trans.getNodes().get("COID");
        String recvRRN = trans.getNodes().get("RRN");
        String recvUser = trans.getNodes().get("USER_ACCTID");
        String amount = trans.getNodes().get("AMOUNT");
        String erpc = trans.getErpc(trans.getKey(), trans.getIv());
        object.put("order_sn",recvCoid);
        object.put("pay_status",recvPayStatus);
        object.put("rcode",recvRCode);
        object.put("trade_no",recvRRN);
        object.put("user_id",recvUser);
        object.put("amount",amount);
        object.put("erpc",erpc);
        object.put("recv_erpc",recvERPC);
        return object;
    }

    /**
     * 验证乐点gash回调数据
     */
    public static JSONObject checkData(String data){
        JSONObject object = getData(data);
        String recvERPC = object.getString("recv_erpc");
        String erpc = object.getString("erpc");
        if(!LibSysUtils.isNullOrEmpty(erpc) && erpc.equals(recvERPC)) {
            String rcode = object.getString("rcode");
            if(rcode.equals("0000")){
                switch (object.getString("pay_status")){
                    case "S":
                        object.put("is_success",true);
                        break;
                    case "0": //交易未完成
                        getRetryOrderInfo(object);
                        break;
                    case "W": //交易待确认
                        getRetryOrderInfo(object);
                        break;
                    default:
                        object.put("is_success",false);
                        break;
                }
            }else if(rcode.equals("9004") || rcode.equals("9998") || rcode.equals("2001") || rcode.equals("9999")){
                getRetryOrderInfo(object);
            }else{
                object.put("is_success",false);
            }
        }else {
            object.put("is_success",false);
        }
        log.info("checkData_check_order:"+object.toString());
        return object;
    }

    /**
     * 请款乐点gash数据
     */
    public static JSONObject settleData(String data){
        JSONObject object = getData(data);
        String recvERPC = object.getString("recv_erpc");
        String erpc = object.getString("erpc");
        if(!LibSysUtils.isNullOrEmpty(erpc) && erpc.equals(recvERPC)) {
            String rcode = object.getString("rcode");
            if(rcode.equals("0000")){
                switch (object.getString("pay_status")){
                    case "S":
                        object.put("is_success",true);
                        break;
                    case "0": //交易未完成
                        getSettleOrderInfo(object);
                        break;
                    case "W": //交易待确认
                        getSettleOrderInfo(object);
                        break;
                    default:
                        object.put("is_success",false);
                        break;
                }
            }else if(rcode.equals("9004") || rcode.equals("9998") || rcode.equals("2001") || rcode.equals("9999")){
                getSettleOrderInfo(object);
            }else{
                object.put("is_success",false);
            }
        }else {
            object.put("is_success",false);
        }
        log.info("settleData_check_order:"+object.toString());
        return object;
    }

    /**
     * 记录请款订单信息
     */
    public static JSONObject getSettleOrderInfo(JSONObject object){
        WKCache.add_gash_settle_info(object.getString("order_sn"),object.toString());
        object.put("is_success",false);
        return object;
    }


    /**
     * 记录重试订单信息
     */
    public static JSONObject getRetryOrderInfo(JSONObject object){
        WKCache.add_gash_order_info(object.getString("order_sn"),object.toString());
        object.put("is_success",false);
        return object;
    }

    /**
     * 请款
     */
    public static String settle(String orderSn,String amount){
        String sendData = settleData(orderSn,amount);
        String recvData = getSettleResponse(sendData);
        log.info("order_sn"+orderSn+"---settle_send_data:"+sendData+"---settle_recv_data:"+recvData);
        return recvData;
    }

    /**
     * 验证订单
     */
    public static String checkOrder(String orderSn,String amount){
        String sendData = orderData(orderSn,amount);
        String recvData = getOrderResponse(sendData);
        //getData(recvData);
        log.info("order_sn"+orderSn+"---order_send_data:"+sendData+"---order_recv_data"+recvData);
        return recvData;
//        JSONObject object = getData(recvData);
//        String recvERPC = object.getString("recv_erpc");
//        String erpc = object.getString("erpc");
//        if(!LibSysUtils.isNullOrEmpty(erpc) && erpc.equals(recvERPC)) {
//            if(object.getString("rcode").equals("0000")){
//                if(object.getString("pay_status").equals("S")){
//                    object.put("is_success",true);
//                    return object;
//                }
//            }
//        }
//        object.put("is_success",false);
//        return object;
    }

    private static String getSettleResponse( String data){
        String recvData;
        Settle ws = new Settle();
        SettleSoap port = ws.getSettleSoap();
        recvData = port.getResponse(data);
        return recvData;
    }

    private static String getOrderResponse( String data ){
        String recvData;
        Checkorder ws = new Checkorder();
        CheckorderSoap port = ws.getCheckorderSoap();
        recvData = port.getResponse(data);
        return recvData;

    }

    public static void main(String[] args){
//"check_order:{\"order_sn\":\"15791504760642849\",\"pay_status\":\"\",\"rcode\":\"1999\",\"trade_no\":\"\",\"user_id\":\"\",\"amount\":\"300\",\"erpc\":\"QTXNCyjwXtAtUDB66wbfFJRgcyE=\",\"recv_erpc\":\"\",\"is_success\":false}"

        String data = "PFRSQU5TPjxNU0dfVFlQRT4wMTEwPC9NU0dfVFlQRT48UENPREU+MzAwMDAwPC9QQ09ERT48Q0lEPkMwMDYzODAwMDEwOTQ8L0NJRD48Q09JRD4xNTc5MTUwNDc2MDY0Mjg0OTwvQ09JRD48UlJOPkdQMTcwOTA3MjAwMjQyNDQ8L1JSTj48Q1VJRD5UV0Q8L0NVSUQ+PFBBSUQ+Q09QR0FNMDU8L1BBSUQ+PEFNT1VOVD4zMDA8L0FNT1VOVD48RVJQQz5FbmVJQ3UxZ3Zza1ZyY2s5dTIxSk42SEdsM3c9PC9FUlBDPjxPUkRFUl9UWVBFPjwvT1JERVJfVFlQRT48UEFZX1NUQVRVUz5TPC9QQVlfU1RBVFVTPjxQQVlfUkNPREU+MDAwMDwvUEFZX1JDT0RFPjxSQ09ERT4wMDAwPC9SQ09ERT48RVJQX0lEPlBJTkhBTEw8L0VSUF9JRD48TUlEPk0xMDAwNjM4PC9NSUQ+PEJJRD48L0JJRD48TUVNTz48L01FTU8+PFBST0RVQ1RfTkFNRT5HQVNIUE9JTlTph5HmtYHmnI3li5k8L1BST0RVQ1RfTkFNRT48UFJPRFVDVF9JRD5QSU5IQUxMPC9QUk9EVUNUX0lEPjxVU0VSX0FDQ1RJRD4xOTU2OTwvVVNFUl9BQ0NUSUQ+PFVTRVJfR1JPVVBJRD48L1VTRVJfR1JPVVBJRD48VVNFUl9JUD40OS4yMTQuNjQuODM8L1VTRVJfSVA+PEVYVEVOU0lPTj48L0VYVEVOU0lPTj48R1BTX0lORk8+PC9HUFNfSU5GTz48VFhUSU1FPjIwMTcwOTA3MTMwNDQ1PC9UWFRJTUU+PC9UUkFOUz4=&";
        System.out.println(getData(data));
        //        Map map = execute("123546","98898888888","123");
//        System.out.println(map.toString());
    }

}
