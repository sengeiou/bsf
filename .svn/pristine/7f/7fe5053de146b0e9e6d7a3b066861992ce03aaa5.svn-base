package com.weking.core;

import com.weking.cache.WKCache;
import com.weking.core.enums.UploadTypeEnum;
import com.wekingframework.core.LibProperties;
import com.wekingframework.core.LibSysUtils;
import com.wekingframework.file.LibImgCompress;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

public class FileUtil {

    private static Logger loger = Logger.getLogger(FileUtil.class);

    /**
     * 保存文件流图片
     *
     * @param fileItem
     * @param typeEnum 文件类型
     * @return
     */
    public static JSONObject saveImage(FileItem fileItem, UploadTypeEnum typeEnum) {
        String imagePath = getImagePath(typeEnum);
        String uploadPath = LibProperties.getConfig("weking.config.pic.url");
        String servicePath = uploadPath + imagePath;
        String fileName = save(servicePath, fileItem);
        if (fileName == null) {
            return LibSysUtils.getResultJSON(ResultCode.upload_image_error);
        }
        JSONObject object = new JSONObject();
        object.put("code", ResultCode.success);
        String bigFileName = imagePath + fileName;
        String smallFileName = "";
        JSONObject object1 = compreImage(servicePath + fileName, typeEnum.getWidth(), typeEnum.getHeight());
        if (object1.optInt("code") == 1) {
            smallFileName = bigFileName.replace("big", "small");
        }else{
            smallFileName = bigFileName;
        }
        String picServer = WKCache.get_system_cache("weking.config.pic.server");
        object.put("big_filename", bigFileName);
        object.put("big_pic_url", picServer + bigFileName);
        object.put("small_pic_url", picServer + smallFileName);
        object.put("width", object1.optInt("width"));
        object.put("height", object1.optInt("height"));
        return object;
    }

    /**
     * 保存文件流图片
     *
     * @param fileItem
     * @param typeEnum 文件类型
     * @return
     */
    public static JSONObject saveVideo(FileItem fileItem, UploadTypeEnum typeEnum) {
        String imagePath = getImagePath(typeEnum);
        String uploadPath = LibProperties.getConfig("weking.config.pic.url");
        String servicePath = uploadPath + imagePath;
        String fileName = save(servicePath, fileItem);
        if (fileName == null) {
            return LibSysUtils.getResultJSON(ResultCode.upload_image_error);
        }
        JSONObject object = new JSONObject();
        object.put("code", ResultCode.success);
        String bigFileName = imagePath + fileName;
        object.put("big_filename", bigFileName);
        object.put("big_pic_url", uploadPath + bigFileName);
        return object;
    }

    private static String save(String servicePath, FileItem fileItem) {
        File file = new File(servicePath);
        if (!file.exists()) {// 如果文件不存在，则创建该文件
            file.mkdirs();
        }
        String fileType = fileItem.getContentType();
        String[] ftype = fileType.split("/");
        String fileName = getImageName(ftype[1], true);
        String filePath = "";
        try {
            InputStream inputStream = fileItem.getInputStream();
            if (inputStream instanceof FileInputStream) {  //文件流
                FileInputStream in = (FileInputStream) inputStream;
                filePath = servicePath + fileName;
                FileOutputStream out = new FileOutputStream(new File(filePath));// 指定要写入的图片
                int n;// 每次读取的字节长度
                byte[] bb = new byte[1024];// 存储每次读取的内容
                while ((n = in.read(bb)) != -1) {
                    out.write(bb, 0, n);// 将读取的内容，写入到输出流当中
                }
                out.close();// 关闭输入输出流
                in.close();
            } else if (inputStream instanceof ByteArrayInputStream) { //字节流
                ByteArrayInputStream in = (ByteArrayInputStream) inputStream;
                filePath = servicePath + fileName;
                FileOutputStream out = new FileOutputStream(new File(filePath));// 指定要写入的图片
                int n;// 每次读取的字节长度
                byte[] bb = new byte[1024];// 存储每次读取的内容
                while ((n = in.read(bb)) != -1) {
                    out.write(bb, 0, n);// 将读取的内容，写入到输出流当中
                }
                out.close();// 关闭输入输出流
                in.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            loger.error("保存图片失败：" + e.getMessage());
            fileName = null;
            e.printStackTrace();
        }
        return fileName;
    }

    //保存网络图片到服务器
    public static String saveImage(String url, UploadTypeEnum typeEnum) {
        String fileName = getImageName("jpg", true);
        String fileNameSmall = fileName.replace("big", "small");
        String imagePath = getImagePath(typeEnum);
        String savePath = (LibProperties.getConfig("weking.config.pic.url") + imagePath).replace("/", System.getProperty("file.separator"));
        try {
            // 构造URL
            URL urlObj = new URL(url);
            // 打开连接
            URLConnection con = urlObj.openConnection();
            //设置请求超时为5s
            con.setConnectTimeout(5 * 1000);
            // 输入流
            InputStream is = con.getInputStream();
            if (AliyunOSS.useOSS()) {
                JSONObject object = AliyunOSS.uploadImage(is, "jpg", typeEnum);
                fileName = object.optString("big_filename", "");
            } else {
                // 1K的数据缓冲
                byte[] bs = new byte[1024];
                // 读取到的数据长度
                int len;
                // 输出的文件流
                File sf = new File(savePath);
                if (!sf.exists()) {
                    sf.mkdirs();
                }
                OutputStream os = new FileOutputStream(sf.getPath() + System.getProperty("file.separator") + fileName);
                // 开始读取
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                // 完毕，关闭所有链接
                os.close();
                is.close();
                String srcFilePath = savePath + fileName;
                String descFilePath = savePath + fileNameSmall;
                try {
                    LibImgCompress imgCom = new LibImgCompress(srcFilePath);
                    imgCom.resizeFix(UploadTypeEnum.AVATAR.getWidth(), UploadTypeEnum.AVATAR.getHeight(), descFilePath);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                fileName = imagePath + fileName;
            }
        } catch (Exception e) {
            fileName = "";
            loger.error("下载图片错误：" + e.getMessage());
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * 图片日期路径
     */
    public static String getImagePath(UploadTypeEnum typeEnum) {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        return String.format("%s/%d/%d/%d/", typeEnum.getName(), year, month, day);
    }

    /**
     * 获取图片名称
     *
     * @param exten  后缀名
     * @param addBig 是否加文件名是否加入big
     */
    public static String getImageName(String exten, boolean addBig) {
        if (addBig)
            return System.currentTimeMillis() + LibSysUtils.getRandomString(4) + "big." + exten;
        else
            return System.currentTimeMillis() + LibSysUtils.getRandomString(4) + "." + exten;
    }

    //压缩图片
    private static JSONObject compreImage(String filePath, int width, int height) {
        JSONObject reslut = new JSONObject();
        reslut.put("code", false);
        if (width > 0 && height > 0) {
            try {
                LibImgCompress imgCom = new LibImgCompress(filePath);
                //如果压缩的图片尺寸比实际的大，则存实际的尺寸
                if (width > imgCom.getWidth()) {
                    width = imgCom.getWidth();
                    height = imgCom.getHeight();
                }
                imgCom.resizeFix(width, height, filePath.replace("big", "small"));  //压缩图片
                reslut.put("width", imgCom.getWidth());
                reslut.put("height", imgCom.getHeight());
                reslut.put("code", true);
            } catch (IOException e) {
                loger.error("压缩图片失败：" + e.getMessage());
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return reslut;
    }
}  
