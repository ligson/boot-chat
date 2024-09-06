package com.yonyougov.bootchat.gpt.controller;


import com.yonyougov.bootchat.fw.web.vo.WebResult;
import com.yonyougov.bootchat.gpt.dto.ChatMessage2;
import com.yonyougov.bootchat.minio.config.MinioConfig;
import com.yonyougov.bootchat.minio.util.MinioUtil;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "product/file")
public class FileController {


    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private MinioConfig prop;

    @ApiOperation(value = "查看存储bucket是否存在")
    @GetMapping("/bucketExists")
    public WebResult bucketExists(@RequestParam("bucketName") String bucketName) {
        return WebResult.newSuccessInstance().putData("bucketName", minioUtil.bucketExists(bucketName));
    }

    @ApiOperation(value = "创建存储bucket")
    @GetMapping("/makeBucket")
    public WebResult makeBucket(String bucketName) {
        return WebResult.newSuccessInstance().putData("bucketName", minioUtil.makeBucket(bucketName));
    }

    @ApiOperation(value = "删除存储bucket")
    @GetMapping("/removeBucket")
    public WebResult removeBucket(String bucketName) {
        WebResult webResult = new WebResult();
        return WebResult.newSuccessInstance().putData("bucketName", minioUtil.removeBucket(bucketName));

    }

    @ApiOperation(value = "获取全部bucket")
    @GetMapping("/getAllBuckets")
    public WebResult getAllBuckets() {
        List<Bucket> allBuckets = minioUtil.getAllBuckets();
        return WebResult.newSuccessInstance().putData("allBuckets", allBuckets);
    }

    @ApiOperation(value = "文件上传返回url")
    @PostMapping("/upload")
    public WebResult upload(@RequestParam("file") MultipartFile file) {
        String objectName = minioUtil.upload(file);
        if (null != objectName) {
            return WebResult.newSuccessInstance().putData("url", (prop.getEndpoint() + "/" + prop.getBucketName() + "/" + objectName));
        }
        return WebResult.newErrorInstance("上传失败");
    }


    @ApiOperation(value = "图片url上传图片并预览")
    @PostMapping("/uploadImageFromUrl")
    public String uploadImageFromUrl(@RequestParam("imageUrl") String imageUrl) {
        String objectName = minioUtil.uploadImageFromUrl(imageUrl, new ChatMessage2());
        if (null != objectName) {
            return minioUtil.preview(objectName);
//            return WebResult.newSuccessInstance().putData("url",(prop.getEndpoint() + "/" + prop.getBucketName() + "/" + objectName));
        }
        return "上传失败";
//        return WebResult.newErrorInstance("上传失败");
    }

    @ApiOperation(value = "图片/视频预览")
    @GetMapping("/preview")
    public WebResult preview(@RequestParam("fileName") String fileName) {
        return WebResult.newSuccessInstance().putData("filleName", minioUtil.preview(fileName));
    }


    @ApiOperation(value = "文件下载")
    @GetMapping("/download")
    public WebResult download(@RequestParam("fileName") String fileName, HttpServletResponse res) {
        minioUtil.download(fileName, res);
        return WebResult.newSuccessInstance();
    }

    @ApiOperation(value = "删除文件", notes = "根据url地址删除文件")
    @PostMapping("/delete")
    public WebResult remove(String url) {
        String objName = url.substring(url.lastIndexOf(prop.getBucketName() + "/") + prop.getBucketName().length() + 1);
        minioUtil.remove(objName);
        return WebResult.newSuccessInstance().putData("objName", objName);
    }

    @ApiOperation(value = "浏览文件", notes = "浏览桶内文件")
    @PostMapping("/find")
    public WebResult find() {
        List<Item> items = minioUtil.listObjects();
        return WebResult.newSuccessInstance().putData("items", items);
    }

}
