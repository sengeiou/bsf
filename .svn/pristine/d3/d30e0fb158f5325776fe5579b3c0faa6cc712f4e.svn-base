package com.weking.core;

import com.weking.cache.WKCache;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TencentUtil {

    //获取推流地址
    public static String getPushFlowUrl(String streamId) {
        long txTime = WkUtil.getFutureTime(360);
        String pushKey = WKCache.get_system_cache("weking.config.tencent.push.key");
        String pushUrl = WKCache.get_system_cache("tencent.livepush.url");
        String safeUrl = getSafeUrl(pushKey, streamId, txTime);
//        return "rtmp://"+bizid+".livepush.myqcloud.com/live/"+bizid+"_"+streamId+"?bizid="+bizid+"&"+safeUrl;
        return pushUrl + "/live/" + streamId + "?"  + safeUrl;
    }

    //获取台湾cdn推流地址
    public static String getPushTWUrl(String streamId) {
        long txTime = WkUtil.getFutureTime(360);
        String pushUrl = WKCache.get_system_cache("tw.push.url");
        return pushUrl + "/live/" + streamId ;
    }

    //获取rtmp播放地址
    public static String getRtmpPlayUrl(String streamId) {
        String playUrl = WKCache.get_system_cache("tencent.liveplay.url");
//        String bizid = WKCache.get_system_cache("weking.config.tencent.bizid");
//        return "rtmp://"+bizid+".livepush.myqcloud.com/live/"+bizid+"_"+streamId;
        return "rtmp://" + playUrl + "/live/" + streamId;
    }

    //获取flv播放地址
    public static String getFlvPlayUrl(String streamId) {
        String playUrl = WKCache.get_system_cache("tencent.liveplay.url");
//        String bizid = WKCache.get_system_cache("weking.config.tencent.bizid");
//        return "http://"+bizid+".liveplay.myqcloud.com/live/"+bizid+"_"+streamId+".flv";
        return "http://" + playUrl + "/live/" + streamId + ".flv";
    }

    //获取台湾播放地址
    public static String getFlvTWPlayUrl(String streamId) {
        String playUrl = WKCache.get_system_cache("tw.pull.url");
        return playUrl + "/live/" + streamId + ".flv";
    }

    //获取hls播放地址
    public static String getHlsPlayUrl(String streamId) {
        String playUrl = WKCache.get_system_cache("tencent.liveplay.url");
//        String bizid = WKCache.get_system_cache("weking.config.tencent.bizid");
//        return "http://" + bizid + ".liveplay.myqcloud.com/live/" + bizid + "_" + streamId + ".m3u8";
        return "http://" + playUrl + "/live/" + streamId + ".m3u8";
    }

    //获取hls播放地址  TW
    public static String getHlsTWPlayUrl(String streamId) {
        String playUrl = WKCache.get_system_cache("tw.hls.url");
//        String bizid = WKCache.get_system_cache("weking.config.tencent.bizid");
//        return "http://" + bizid + ".liveplay.myqcloud.com/live/" + bizid + "_" + streamId + ".m3u8";
        return playUrl+"/live/" + streamId + "/playlist.m3u8";
    }


    public static String getStreamId(String stream_id) {
        String bizid = WKCache.get_system_cache("weking.config.tencent.bizid");
        return stream_id.replace(bizid + "_", "");
    }

    private static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /*
     * KEY+ stream_id + txTime
     */
    private static String getSafeUrl(String key, String streamId, long txTime) {
        String input = new StringBuilder().
                append(key).
                append(streamId).
                append(Long.toHexString(txTime).toUpperCase()).toString();

        String txSecret = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            txSecret = byteArrayToHexString(
                    messageDigest.digest(input.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return txSecret == null ? "" :
                new StringBuilder().
                        append("txSecret=").
                        append(txSecret).
                        append("&").
                        append("txTime=").
                        append(Long.toHexString(txTime).toUpperCase()).
                        toString();
    }

    private static String byteArrayToHexString(byte[] data) {
        char[] out = new char[data.length << 1];
        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }


    public static void main(String[] args) {

        for (int i=0 ;i<1000;i++){
            double random = Math.random();
            if (random*10000<(200*0.1)) {
                System.out.println(random * 10000);
            }
        }

    }

}
