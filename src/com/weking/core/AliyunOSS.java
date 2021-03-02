package com.weking.core;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.weking.cache.WKCache;
import com.weking.core.enums.UploadTypeEnum;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by Administrator on 2017/11/7.
 * 上传文件到阿里云的OSS中
 */
public class AliyunOSS {
    static Logger loger = Logger.getLogger(AliyunOSS.class);
    private static volatile String accessKeyID;
    private static volatile String accessKeySecret;
    private static volatile String endpoint;
    private static volatile String pic_bucketName;
    private static volatile String video_bucketName;

    static {
        accessKeyID = WKCache.get_system_cache("aliyun.AccessKeyID");
        accessKeySecret = WKCache.get_system_cache("aliyun.AccessKeySecret");
        endpoint = WKCache.get_system_cache("aliyun.endpoint");
        pic_bucketName = WKCache.get_system_cache("aliyun.bucket.name");
        video_bucketName = WKCache.get_system_cache("aliyun.videobucket.name");
    }

    /**
     * 是否启用OSS存储
     *
     * @return
     */
    public static boolean useOSS() {
        return !LibSysUtils.isNullOrEmpty(accessKeyID);
    }

    private static OSSClient createOSSClient() {
        return new OSSClient(endpoint, accessKeyID, accessKeySecret);
    }

    /**
     * 上传视频文件
     *
     * @param fileItem
     * @param typeEnum
     * @return
     */
    public static JSONObject uploadVideo(FileItem fileItem, UploadTypeEnum typeEnum) {
        String savePath = FileUtil.getImagePath(typeEnum);
        String[] ftype = fileItem.getContentType().split("/");
        String fileName = savePath + FileUtil.getImageName(ftype[1], false);
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        try {
            result = upload(fileItem.getInputStream(), fileName, video_bucketName, typeEnum);
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * 上传图片文件
     *
     * @param fileItem
     * @param typeEnum 默认值为other
     * @return
     */
    public static JSONObject uploadImage(FileItem fileItem, UploadTypeEnum typeEnum) {
        JSONObject result = new JSONObject();
        String savePath = FileUtil.getImagePath(typeEnum);
        String fileType = fileItem.getContentType();
        String[] ftype = fileType.split("/");
        String fileName = savePath + FileUtil.getImageName(ftype[1], false);
        try {
            result = uploadImage(fileItem.getInputStream(), ftype[1], typeEnum);
        } catch (Exception e) {

        }
        return result;
    }

    public static JSONObject uploadImage(InputStream inputStream, String exten, UploadTypeEnum typeEnum) {
        String savePath = FileUtil.getImagePath(typeEnum);
        String fileName = savePath + FileUtil.getImageName(exten, false);
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        try {
            result = upload(inputStream, fileName, pic_bucketName, typeEnum);
        } catch (Exception e) {

        }
        return result;
    }

    private static JSONObject upload(InputStream inputStream, String fileName, String bucketName, UploadTypeEnum typeEnum) {
        JSONObject result = LibSysUtils.getResultJSON(ResultCode.success);
        OSSClient ossClient = createOSSClient();
        try {
            ossClient.putObject(new PutObjectRequest(bucketName, fileName, inputStream));
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            loger.error("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (Exception ce) {
            /*loger.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");*/
            loger.error("Error Message: " + ce.getMessage());
        } finally {
            ossClient.shutdown();
        }
        String image_server = WKCache.get_system_cache("weking.config.pic.server");
        result.put("big_filename", fileName);
        result.put("big_pic_url", String.format("%s%s", image_server, fileName));
        if (typeEnum.getValue() != UploadTypeEnum.VIDEO.getValue()) {
            result.put("small_pic_url", String.format("%s%s!%s", image_server, fileName, typeEnum.getStylename()));
            result.put("width", typeEnum.getWidth());
            result.put("height", typeEnum.getHeight());
        }
        return result;
    }

    /**
     * 对象是否存在
     *
     * @param bucketName 空间名称
     * @param key        对象名称
     * @return
     */
    public static boolean doesObjectExist(String bucketName, String key) {
        OSSClient ossClient = createOSSClient();
        try {
            return ossClient.doesObjectExist(bucketName, key);
        } finally {
            // 关闭client
            ossClient.shutdown();
        }
    }

    /**
     * 创建目录
     * OSS是没有文件夹这个概念的，所有元素都是以Object来存储。创建模拟文件夹本质上来说是创建了一个size为0的Object。
     * 对于这个Object可以上传下载，只是控制台会对以”/“结尾的Object以文件夹的方式展示。
     * 创建模拟文件夹本质上来说是创建了一个名字以“/”结尾的文件；
     * 对于这个文件照样可以上传下载,只是控制台会对以“/”结尾的文件以文件夹的方式展示；
     * 多级目录创建最后一级即可，比如dir1/dir2/dir3/，创建dir1/dir2/dir3/即可，dir1/、dir1/dir2/不需要创建；
     *
     * @param bucketName 空间名称
     * @param key        目录名称
     * @return
     */

    public static void createDir(String bucketName, String key) {
        OSSClient ossClient = createOSSClient();
        try {
            ossClient.putObject(bucketName, key, new ByteArrayInputStream(new byte[0]));
        } finally {
            // 关闭client
            ossClient.shutdown();
        }
    }

    /**
     * 删除文件
     *
     * @param bucketName 空间名称
     * @param key        文件名称
     */
    public static void deleteObject(String bucketName, String key) {
        OSSClient ossClient = createOSSClient();
        try {
            ossClient.deleteObject(bucketName, key);
        } finally {
            // 关闭client
            ossClient.shutdown();
        }
    }


    public static void main(String[] args) {

    }
}
