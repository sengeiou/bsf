package com.weking.core;

import com.gexin.fastjson.JSON;
import com.weking.cache.WKCache;
import com.weking.core.enums.UploadTypeEnum;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;


public class WkUtil {

    private static String imageUrl = WKCache.get_system_cache("weking.config.pic.server");

    private static String videoUrl = WKCache.get_system_cache("weking.video.url");

    public static JSONObject checkToken(String access_token) {
        return WKCache.check_token(access_token);
    }

    private static Logger log = Logger.getLogger(WkUtil.class);

    /**
     * 获取图片URL
     *
     * @param path      图片路径
     * @param getLowPic True:是否获取缩略图片
     * @return
     */
    public static String combineUrl(String path, UploadTypeEnum typeEnum, boolean getLowPic) {
        String result = LibSysUtils.toString(path);
        if (!result.startsWith("http")) {
            String url;
            if (typeEnum.getValue() == UploadTypeEnum.VIDEO.getValue()) {
                url = videoUrl;
            } else {
                url = imageUrl;
            }
            if (getLowPic && !LibSysUtils.isNullOrEmpty(result)) {
                if (AliyunOSS.useOSS()) {
                    result = result + "!" + typeEnum.getStylename();
                } else {
                    if (result.contains("big")) {
                        result = result.replace("big", "small");
                    }
                }
            }
            if (!LibSysUtils.isNullOrEmpty(result)) {
                result = url + result;
            }
        }
        return result;
    }

    /**
     * 获取相对图片路径
     *
     * @param result 图片绝对路径
     */
    public static String getRelativeHeadPic(String result) {
        if (!LibSysUtils.isNullOrEmpty(result)) {
           // String url = WKCache.get_system_cache("weking.config.pic.server");
            result = result.replace(imageUrl, "");
            int index = result.indexOf("!");
            if (index > 0)
                result = result.substring(0, index);
        }
        return result;
    }

    /**
     * 获取 图片URL根路径
     *
     * @return
     */
    public static final String getPicRootUrl() {
        String result;
        String url = WKCache.get_system_cache("weking.config.pic.server");
//        if (UpyunUpload.using()) {
//            result = String.format("%s%s/", url, UpyunUpload.getPath());
//        } else {
//            result = url ;
//        }
        return url;
    }

    /**
     * 向后截取n位
     */
    public static String subForward(String s, int n) {
        return s.substring(0, n);
    }

    //获取客户端IP
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /*
     * 当前时间过去或未来N分钟
	 */
    public static String futureTime(int time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, time);
        return df.format(now.getTimeInMillis());
    }

    /*
     * 获取未来时间戳
     */
    static long getFutureTime(int minute) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minute);
        return now.getTimeInMillis() / 1000;
    }

    public static String format(long delta, String lang_code) {
        long ONE_MINUTE = 60000L;
        long ONE_HOUR = 3600000L;
        long ONE_DAY = 86400000L;
        long ONE_WEEK = 604800000L;
        String ONE_SECOND_AGO = LibProperties.getLanguage(lang_code, "weking.lang.one.second.ago");
        String ONE_MINUTE_AGO = LibProperties.getLanguage(lang_code, "weking.lang.one.minute.ago");
        String ONE_HOUR_AGO = LibProperties.getLanguage(lang_code, "weking.lang.one.hour.ago");
        String ONE_DAY_AGO = LibProperties.getLanguage(lang_code, "weking.lang.one.day.ago");
        String ONE_MONTH_AGO = LibProperties.getLanguage(lang_code, "weking.lang.one.month.ago");
        String ONE_YEAR_AGO = LibProperties.getLanguage(lang_code, "weking.lang.one.year.ago");
        if (delta < ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return String.format(ONE_SECOND_AGO, (seconds <= 0 ? 1 : seconds));
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return String.format(ONE_MINUTE_AGO, (minutes <= 0 ? 1 : minutes));
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return String.format(ONE_HOUR_AGO, (hours <= 0 ? 1 : hours));
        }
//        if (delta < 48L * ONE_HOUR) {
//            return "昨天";
//        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return String.format(ONE_DAY_AGO, (days <= 0 ? 1 : days));
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return String.format(ONE_MONTH_AGO, (months <= 0 ? 1 : months));
        } else {
            long years = toYears(delta);
            return String.format(ONE_YEAR_AGO, (years <= 0 ? 1 : years));
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }

    public static String getShortName(String name) {
        if (name.length() > 8) {
            name = name.substring(0, 7) + "..";
        }
        return name;
    }


    public static String getShortName(String name, int maxLen) {
        if (name.length() > maxLen) {
            name = name.substring(0, maxLen - 1) + "..";
        }
        return name;
    }

    /**
     * 获得某个时刻时间
     * Calendar.DATE 过去天数
     * Calendar.MONTH 月数
     * Calendar.YEAR 年数
     */
    public static long getPastTime(int calendar, int n) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(calendar, n);
        Date d = c.getTime();
        return LibSysUtils.toLong(format.format(d));
    }

    /**
     * 两数之间的整数
     */
    public static int getBetweenRandom(int min, int max) {
        if (min < 0) {
            int num;
            int jMin = Math.abs(min);
            if (jMin <= max) {
                num = getBetweenRandom(max);
                if (num > jMin) {
                    return num;
                }
            } else {
                //负数转换成整数大于最大数，取负数为最大数到0之间随机
                num = getBetweenRandom(jMin);
                //随机数大于最大数直接返回负数,否则随机正数或负数
                if (num > max) {
                    return -num;
                }
            }
            return getBetweenRandom(1) == 0 ? -num : num;
        }
        return new Random().nextInt(max) % (max - min + 1) + min;
    }

    public static String getPlatformLang(String langKey, String langCode) {
        if (LibSysUtils.isNullOrEmpty(langCode)) {
            langCode = LibProperties.getConfig("weking.config.default_lang");
        }
        String langContext = ResourceUtil.PlatformLangMap.get(String.format("%s_%s", langKey, langCode));
        if (!LibSysUtils.isNullOrEmpty(langContext)) {
            return langContext;
        }
        return langKey;
    }

    /**
     * 以，隔开字符串转换成List
     */
    public static List<String> strToList(String str) {
        if (LibSysUtils.isNullOrEmpty(str)) {
            return null;
        }
        return Arrays.asList(str.split(","));
    }

    /**
     * 随机0到某数之间的正整数[0,max]
     */
    public static int getBetweenRandom(int max) {
        return new Random().nextInt(max + 1);
    }

    public static void main2(String[] args) {
        String result = "";
        try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

                /* 读入TXT文件 */
            String pathname = "C:\\Users\\Administrator\\Desktop\\address\\address.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            line = br.readLine();
            result = result + "\"" + line + "\",";
            while (line != null) {
                line = br.readLine(); // 一次读入一行数据
                result = result + "\"" + line + "\",";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
        result = "";
        try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

                /* 读入TXT文件 */
            String pathname = "C:\\Users\\Administrator\\Desktop\\address\\qty.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            line = br.readLine();
            result = result + line + ",";
            while (line != null) {
                line = br.readLine(); // 一次读入一行数据
                result = result + line + ",";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }

    public static void main1(String[] args) {
        String result = "";
        try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

                /* 读入TXT文件 */
            String pathname = "C:\\Users\\Administrator\\Desktop\\address\\address.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            line = br.readLine();
            result = result + "\"" + line + "\",";
            while (line != null) {
                line = br.readLine(); // 一次读入一行数据
                result = result + "\"" + line + "\",";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @param srcImgPath       源图片路径
     * @param tarImgPath       保存的图片路径
     * @param waterMarkContent 水印内容
     */
    public static void addWaterMark(String srcImgPath, String tarImgPath, String waterMarkContent) {

        try {
            Color markContentColor = new Color(255, 223, 1, 255);    //水印图片色彩以及透明度
            Font font = new Font("微软雅黑", Font.PLAIN, 78);       //水印字体
            // 读取原图片信息
            File srcImgFile = new File(srcImgPath);//得到文件
            Image srcImg = ImageIO.read(srcImgFile);//文件转化为图片
            int srcImgWidth = srcImg.getWidth(null);//获取图片的宽
            int srcImgHeight = srcImg.getHeight(null);//获取图片的高
            // 加水印
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            g.setColor(markContentColor); //根据图片的背景设置水印颜色
            g.setFont(font);              //设置字体

            //设置水印的坐标
            //int x = srcImgWidth - 2 * getWatermarkLength(waterMarkContent, g);
            //int y = srcImgHeight - 2 * getWatermarkLength(waterMarkContent, g);
            int x = 160;
            int y = 1850;
            g.drawString(waterMarkContent, x, y);  //画出水印
            g.dispose();
            // 输出图片
            FileOutputStream outImgStream = new FileOutputStream(tarImgPath);
            ImageIO.write(bufImg, "jpg", outImgStream);
            System.out.println("添加水印完成");
            outImgStream.flush();
            outImgStream.close();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }

    /**
     * 计算字符串的hash值
     *
     * @param string    明文
     * @param algorithm 算法名
     * @return 字符串的hash值
     */
    public static String hash(String string, String algorithm) {
        if (string.isEmpty()) {
            return "";
        }
        MessageDigest hash = null;
        try {
            hash = MessageDigest.getInstance(algorithm);
            byte[] bytes = hash.digest(string.getBytes("UTF-8"));
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }



    public static String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {
            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     *
     * @param hexStr
     * @return
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789abcdef";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    private static int compare(String str, String target)
    {
        int d[][];              // 矩阵
        int n = str.length();
        int m = target.length();
        int i;                  // 遍历str的
        int j;                  // 遍历target的
        char ch1;               // str的
        char ch2;               // target的
        int temp;               // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) { return m; }
        if (m == 0) { return n; }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++)
        {                       // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++)
        {                       // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++)
        {                       // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++)
            {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2 || ch1 == ch2+32 || ch1+32 == ch2)
                {
                    temp = 0;
                } else
                {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    private static int min(int one, int two, int three)
    {
        return (one = one < two ? one : two) < three ? one : three;
    }

    //加密用
    public static String urlDecode(String data){
        if(StringUtils.isEmpty(data)){
            return data;
        }
        try{
            data = URLDecoder.decode(data,"utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    //解密用
    public static String entityToString(Object object){
        if (object == null){
            return null;
        }
        return JSON.toJSONString(object);
    }

    /**
     * 获取两字符串的相似度
     */

    public static float getSimilarityRatio(String str, String target)
    {
        return 1 - (float) compare(str, target) / Math.max(str.length(), target.length());
    }

    // 获取字符串的编码
    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s = encode;
                return s;
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }

    public static void main(String[] args) {
//        String srcImgPath = "E:\\123\\bg.jpg"; //源图片地址
//        String tarImgPath = "E:\\123\\t.jpg"; //待存储的地址
//        String waterMarkContent = "123456";  //水印内容
//
//        addWaterMark(srcImgPath, tarImgPath, waterMarkContent);
//        String result = hash("6tZVlfifOOM10935_S00013157981539680447706100MYR1", "SHA1");
//
//        System.out.println(result);
//
//        System.out.println(convertHexToString(result));
//        System.out.println(hexStr2Str(result));
//
//        String s1 = new String(org.apache.commons.codec.binary.Base64.encodeBase64(convertHexToString(result).getBytes()));
//        String s2 = new String(org.apache.commons.codec.binary.Base64.encodeBase64(hexStr2Str(result).getBytes()));
//        System.out.println(s1);
//        System.out.println(s2);
//
//        String re = "SkVkUwnvv71D77+9fTUZ77+9au+/ve+/vRdMLu+ /vQ== ";
//        String re2 = "iIY9nH6Uz7UoSvTF2XeLlhjQaSc=";
//        String sss = new String(org.apache.commons.codec.binary.Base64.decodeBase64(re.getBytes()));
//        String sss2 = new String(org.apache.commons.codec.binary.Base64.decodeBase64(re2.getBytes()));
//        System.out.println(sss);
//        System.out.println(sss2);
//
//        System.out.println(hexStr2Str(result).equals(sss));
//        System.out.println(hexStr2Str(result).equals(sss2));

       /* System.out.println(getSimilarityRatio("iIY9nH6Uz7UoSvTF2XeLlhjQaSc","iIY9nH6Uz7UoSvTF2XeLlhjaSc"));
        System.out.println(getSimilarityRatio("iIY9nH6Uz7UoSvTF2XeLlhjQaSc","iIY9nH6Uz7UoSvTF2XeLlhjQaSc"));
        System.out.println(getSimilarityRatio("iIY9nH6Uz7UoSvTF2XeLlhjQaSc","SvTF2XeLlhjQaSciIY9nH6Uz7Uo"));*/
        System.out.println(urlDecode("Louis%20Yane"));
    }


}

