package com.weking.controller.system;

import com.weking.cache.WKCache;
import com.weking.core.AliyunOSS;
import com.weking.core.FileUtil;
import com.weking.core.ResultCode;
import com.weking.core.WkUtil;
import com.weking.core.enums.UploadTypeEnum;
import com.weking.service.user.UserService;
import com.wekingframework.comm.LibControllerBase;
import com.wekingframework.core.LibSysUtils;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上传
 */
@Controller
@RequestMapping({"/upload", "system/upload"})
public class UploadController extends LibControllerBase {

    @Resource
    private UserService userService;

    //上传图片
    @RequestMapping("/image")
    @SuppressWarnings("unchecked")
    public void image(HttpServletRequest request, HttpServletResponse response) {
        JSONObject object;
        Map<String, String> map = new HashMap<>();
        FileItem fileItem = null;
        String callback = getParameter(request, "callback");
        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setRepository(new File(System.getProperty("java.io.tmpdir")));//临时文件
            ServletFileUpload upload = new ServletFileUpload(factory);//得到解析器
            upload.setFileSizeMax(20 * 1024 * 1024);//设置上传文件的最大值20M
            List<FileItem> list = upload.parseRequest(request);
            object = LibSysUtils.getResultJSON(ResultCode.success);
            for (FileItem item : list) {
                if (item.isFormField()) { // 普通输入项
                    map.put(item.getFieldName(), item.getString());
                } else {
                    fileItem = item;
                }
            }
        } catch (FileUploadException e) {
            object = LibSysUtils.getResultJSON(ResultCode.upload_image_error);
            e.printStackTrace();
        }

        if (object.getInt("code") == 0) {
            UploadTypeEnum typeEnum = UploadTypeEnum.getTypeEnum(LibSysUtils.toInt(map.get("type"), UploadTypeEnum.OTHER.getValue()));//默认存到other目录
            if (AliyunOSS.useOSS()) {//是否存到阿里云中
                object = AliyunOSS.uploadImage(fileItem, typeEnum);
            } else {
                object = FileUtil.saveImage(fileItem, typeEnum);//保存文件到硬盘
            }
            if (object.getInt("code") == 0) {//保存成功
                if (typeEnum.getValue() == UploadTypeEnum.AVATAR.getValue()) {//如果是头像则要更新用户头像信息
                    String access_token = map.get("access_token");
                    List<String> user_info = WKCache.get_user(access_token, "user_id", "lang_code");
                    if (user_info.size() > 0) {
                        int userId = LibSysUtils.toInt(user_info.get(0));
                        String fileName = object.getString("big_filename");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("pichead_url", fileName);
                        object = userService.modify(userId, jsonObject);
                        if (object.getInt("code") == 0) {
                            object.put("pic_head_high", WkUtil.combineUrl(fileName,UploadTypeEnum.AVATAR, false));
                            object.put("pic_head_low", WkUtil.combineUrl(fileName, UploadTypeEnum.AVATAR,true));
                        }
                    }
                }
            }
        }
        String result = object.toString();
        if(!LibSysUtils.isNullOrEmpty(callback)){
            result = callback + "(" + result + ")";
        }
        this.out(response, result);
    }

    //上传视频
    @RequestMapping("/video")
    @SuppressWarnings("unchecked")
    public void video(HttpServletRequest request, HttpServletResponse response) {
        JSONObject object;
        FileItem fileItem = null;
        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setRepository(new File(System.getProperty("java.io.tmpdir")));//临时文件
            ServletFileUpload upload = new ServletFileUpload(factory);//得到解析器
            upload.setFileSizeMax(50 * 1024 * 1024);//设置上传文件的最大值50M
            List<FileItem> list = upload.parseRequest(request);
            object = LibSysUtils.getResultJSON(ResultCode.success);
            for (FileItem item : list) {
                if (item.isFormField()) { // 普通输入项
                    object.put(item.getFieldName(), item.getString());
                } else {
                    fileItem = item;
                }
            }
        } catch (FileUploadException e) {
            object = LibSysUtils.getResultJSON(ResultCode.upload_image_error);
            e.printStackTrace();
        }
        if (object.getInt("code") == 0) {

            UploadTypeEnum typeEnum = UploadTypeEnum.getTypeEnum(object.optInt("type", UploadTypeEnum.VIDEO.getValue()));//默认存到video目录
            if (AliyunOSS.useOSS()) {//是否存到阿里云中
                object = AliyunOSS.uploadVideo(fileItem, typeEnum);
            } else {
                object = FileUtil.saveVideo(fileItem, typeEnum);//保存文件到硬盘
            }
        }
        this.out(response, object);
    }
}
