package com.xuecheng.api.cms;


import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import org.springframework.web.multipart.MultipartFile;

/**
 * @version V1.0
 * @ClassName: com.xuecheng.api.cms.FileSystemControllerApi.java
 * @Description:fastDFS文件系统api
 * @author: heyz
 * @date: 2021/4/26 16:59
 */
@Api(value = "fastDFS文件系统管理", description = "fastDFS文件管理接口，提供图片的增、删、改、查")
public interface FileSystemControllerApi {

    /*** 上传文件
     ** @param multipartFile 文件
     ** @param filetag 文件标签
     ** @param businesskey 业务key
     ** @param metadata 元信息,json格式
     ** @return
     **/
    public UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata);

}
