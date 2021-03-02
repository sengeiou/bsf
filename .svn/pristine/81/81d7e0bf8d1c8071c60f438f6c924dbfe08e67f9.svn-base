package com.weking.core;

import com.weking.cache.WKCache;
import com.wekingframework.core.LibDateUtils;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import wk.rtc.comm.WkClientStatus;
import wk.rtc.tclient.IWkRTCMsgHandler;
import wk.rtc.tclient.IWkRTCServiceHandler;
import wk.rtc.tclient.WkRTCHandlerArgs;
import wk.rtc.tclient.WkRTCService;

/**
 * 创建时间 2017/4/26.
 *     public static final short noErr = 0;
 public static final short errUserKey = 5000;
 public static final short roomNoExist = 5001;
 public static final short roomHasExist = 5002;
 public static final short clientHasExpire = 5003;
 public static final short clientOfflineOrNoExit = 5004;
 public static final short clientIDIsEmpty = 5005;
 public static final short invalideAppKey = 5006;
 public static final short containInvalideChar = 5007;
 * 创建人 zhengb
 * 功能描述 WkImClient
 */

public class WkImClient {
    private static Logger logger = Logger.getLogger(WkImClient.class);
    public static volatile boolean USEWKINGIM = false;
    public static volatile boolean _IMCONNECT = false;
    private static volatile WkRTCService _WkRTCService = null;

    static {
        USEWKINGIM = LibSysUtils.toBoolean(LibProperties.getConfig("weking.cofing.im"));
    }

    public static void connect() {
        try {
            connect(LibProperties.getConfig("weking.cofing.im.appkey"),
                    LibProperties.getConfig("weking.cofing.im.clientid"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disConnect() {
        logger.info("--------------------------------------wekingim disConnect-----------------------------");
        try {
            _WkRTCService.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     *
     * @param appKey   appKey
     * @param clientID clientID
     */
    public static void connect(String appKey, String clientID) {
        if (_WkRTCService == null)
            _WkRTCService = new WkRTCService(appKey, clientID, false);
        _WkRTCService.setServiceHandler(new IWkRTCServiceHandler() {
            @Override
            public void onConnected() {
                logger.info("--------------------------------------wekingim onConnected-----------------------------");
            }

            @Override
            public void onValidate(boolean b) {
                _IMCONNECT = b;
//                logger.info("onValidate " + b);
                logger.info("--------------------------------------wekingim onValidate-----------------------------");
            }

            @Override
            public void onDisconnected() {
                _IMCONNECT = false;
//                logger.info("onDisconnected ");
                logger.info("--------------------------------------wekingim onDisconnected-----------------------------");
            }

            @Override
            public void onRevRoomMsg(String sendClientId, String roomId, String msg) {
//                logger.info("onRevRoomMsg:" + sendClientId + " " + roomId + " " + msg);
            }

            @Override
            public void onRevGlobalMsg(String sendClientId, String msg) {
//                logger.info("onRevGlobalMsg:" + sendClientId + " " + msg);
            }

            @Override
            public void onRevPrivateMsg(String sendClientId, String msg) {
//                logger.info("onRevPrivateMsg:" + sendClientId + " " + msg);
            }

            @Override
            public void onRevMsgSendStateNotify(String s, boolean b) {
                //发送成功移除消息
                if(!b){
                    String msg = WKCache.getVideoChatMsg(s);
                    if(!LibSysUtils.isNullOrEmpty(msg)){
                        JSONObject imObj = JSONObject.fromObject(msg);
                        imObj.put("message",imObj.optString("msg"));
                        String cid = WKCache.getUserByAccount(imObj.optString("receive_account"),"c_id");
                        PushMsg.pushSingleMsg(cid,imObj);
                    }
                }
                WKCache.delVideoChatMsg(s);
            }

            @Override
            public void onRevClientStatusChange(String s, WkClientStatus wkClientStatus) {
                //BackgroundProvider.ServerCheckRoomHeartEx(s);//主播断线
                int type;
                if (wkClientStatus == WkClientStatus.onLine) {
                    type = 1;
                } else {
                    type = 2;
                }
                WKCache.setImState(s, type);
                long time = LibDateUtils.getLibDateTime();
                logger.info(String.format("----------wekingim onRevClientStatusChange:account=%s,endtime:%d,type:%d", s, time,type));
            }

            @Override
            public void onRevClientStateResponse(String s, boolean b) {
                long time = LibDateUtils.getLibDateTime();
                logger.info(String.format("----------wekingim onRevClientStateResponse:account=%s,endtime:%d", s, time));
            }
        });
        _WkRTCService.start();
    }

    /**
     * 创建IM房间
     *
     * @param roomId        roomId
     * @param anchorAccount
     * @param times         重试
     */
    public static void createRoomAndJoin(final String roomId, String anchorAccount, int times) {
        if (times <= 0) {
            return;
        }
        int newTimes = times - 1;
        _WkRTCService.createRoom(roomId, anchorAccount, new IWkRTCMsgHandler() {
            @Override
            public void onSuccss(WkRTCHandlerArgs wkRTCHandlerArgs) {
               logger.info("--------createRoomAndJoin:onSuccss " + wkRTCHandlerArgs.getRoomID());
            }

            @Override
            public void onFailture(WkRTCHandlerArgs wkRTCHandlerArgs) {
              logger.info("---------createRoomAndJoin:onFailture " + wkRTCHandlerArgs.getRoomID());
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createRoomAndJoin(roomId, anchorAccount, newTimes);
            }
        });
    }

    /**
     * 创建IM房间
     */
    public static void createRoomAndJoin(final String roomId, String account, String otherAccount, int times) {
        if (times <= 0) {
            return;
        }
        int newTimes = times - 1;
        _WkRTCService.createRoom(roomId, account, new IWkRTCMsgHandler() {
            @Override
            public void onSuccss(WkRTCHandlerArgs wkRTCHandlerArgs) {
                logger.debug("--------createRoomAndJoin:onSuccss "+wkRTCHandlerArgs.getRoomID());
            }

            @Override
            public void onFailture(WkRTCHandlerArgs wkRTCHandlerArgs) {
                logger.debug("---------createRoomAndJoin:onFailture "+ wkRTCHandlerArgs.getRoomID());
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                createRoomAndJoin(roomId, account, otherAccount, newTimes);
            }
        });
        forceJoinRoom(roomId, otherAccount);
    }

    /**
     * delRoom
     *
     * @param roomId
     * @param times
     */
    public static void delRoom(String roomId, String anchorAccount, int times) {
        _WkRTCService.deleteRoom(roomId, new IWkRTCMsgHandler() {
            @Override
            public void onSuccss(WkRTCHandlerArgs wkRTCHandlerArgs) {
            }

            @Override
            public void onFailture(WkRTCHandlerArgs wkRTCHandlerArgs) {
//                logger.info("delRoom:onFailture " + wkRTCHandlerArgs.getRoomID());
            }
        });
        //_WkRTCService.unsubscribeUserStatus(anchorAccount, null);
    }

    /**
     * 将某人加入房间
     *
     * @param roomId   roomId
     * @param clientId clientId
     */
    public static void forceJoinRoom(String roomId, String clientId) {
        _WkRTCService.forceJoinRoom(roomId, clientId, null);
    }

    /**
     * 将某人T出房间
     *
     * @param roomId   roomId
     * @param clientId clientId
     */
    public static void forceLeaveRoom(String roomId, String clientId) {
        _WkRTCService.forceLeaveRoom(roomId, clientId, null);
    }

    public static void sendRoomMsg(String roomId, String msg, IWkRTCMsgHandler handler) {
        _WkRTCService.sendRoomMsg(roomId, msg, handler);
    }

    public static void sendRoomMsg(String roomId, String msg, int yun_ba_qos) {
        sendRoomMsg(roomId, msg, null);
    }

    public static void sendPrivateMsg(String clientId, String msg) {
        sendPrivateMsg(clientId, msg,null);
    }

    public static void sendPrivateMsg(String clientId, String msg,boolean isCache) {
        sendPrivateMsg(clientId, msg, new IWkRTCMsgHandler(){

            @Override
            public void onSuccss(WkRTCHandlerArgs wkRTCHandlerArgs) {
                if(isCache){
                    WKCache.setVideoChatMsg(wkRTCHandlerArgs.getMsgID(),wkRTCHandlerArgs.getMsgContent());
                }
            }

            @Override
            public void onFailture(WkRTCHandlerArgs wkRTCHandlerArgs) {

            }
        });
    }

    public static void sendPrivateMsg(String clientId, String msg, IWkRTCMsgHandler handler) {
        _WkRTCService.sendPrivateMsg(clientId, msg, handler);
    }

    public static void sendGlobalMsg(String msg, IWkRTCMsgHandler handler) {
        _WkRTCService.sendGlobalMsg(msg, handler);
    }

    /**
     * 订阅用户
     */
    public static void subscribe(String account) {
        _WkRTCService.subscribeUserStatus(account, new IWkRTCMsgHandler() {
            @Override
            public void onSuccss(WkRTCHandlerArgs wkRTCHandlerArgs) {

            }

            @Override
            public void onFailture(WkRTCHandlerArgs wkRTCHandlerArgs) {

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscribe(account);
            }
        });
    }

    public static void unSubscribe(String account) {
        _WkRTCService.unsubscribeUserStatus(account, new IWkRTCMsgHandler() {
            @Override
            public void onSuccss(WkRTCHandlerArgs wkRTCHandlerArgs) {

            }

            @Override
            public void onFailture(WkRTCHandlerArgs wkRTCHandlerArgs) {

            }
        });
    }

    public static void sendGlobalMsg(String msg) {
        sendGlobalMsg(msg, null);
    }
}
