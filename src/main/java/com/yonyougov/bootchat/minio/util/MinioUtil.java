package com.yonyougov.bootchat.minio.util;

import com.yonyougov.bootchat.gpt.qianfan.dto.ChatMessage2;
import com.yonyougov.bootchat.minio.config.MinioConfig;
import com.yonyougov.bootchat.minio.file.FileMsgService;

import io.micrometer.common.util.StringUtils;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component("MinioUtil")
@Slf4j
public class MinioUtil {
    @Autowired
    private MinioConfig prop;

    @Resource
    private MinioClient minioClient;
    @Autowired
    private FileMsgService fileMsgService;
//    @Autowired
//    private CodeService codeService;

    /**
     * 查看存储bucket是否存在
     *
     * @return boolean
     */
    public Boolean bucketExists(String bucketName) {
        Boolean found;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return found;
    }

    /**
     * 创建存储bucket
     *
     * @return Boolean
     */
    public Boolean makeBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除存储bucket
     *
     * @return Boolean
     */
    public Boolean removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取全部bucket
     */
    public List<Bucket> getAllBuckets() {
        try {
            List<Bucket> buckets = minioClient.listBuckets();
            return buckets;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 文件上传
     *
     * @param file 文件
     * @return Boolean
     */
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new RuntimeException();
        }
//        String fileName = UuidUtils.generateUuid() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = UUID.randomUUID().toString().replace("-", "") + originalFilename.substring(originalFilename.lastIndexOf("."));
//        String objectName = CommUtils.getNowDateLongStr("yyyy-MM/dd") + "/" + fileName;
        try {
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(prop.getBucketName()).object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
            //文件名称相同会覆盖
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return objectName;
    }


    public String uploadImageFromUrl(String imageUrl, ChatMessage2 messages) {
        if (StringUtils.isBlank(imageUrl)) {
            throw new IllegalArgumentException("Image URL cannot be blank.");
        }
        String uploadId = UUID.randomUUID().toString().replace("-", "");
        String objectName = uploadId + getFileExtension(imageUrl);

        try (InputStream inputStream = new URL(imageUrl).openStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            byte[] data = outputStream.toByteArray();
            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .bucket(prop.getBucketName())
                    .object(objectName)
                    .stream(new ByteArrayInputStream(data), data.length, -1)
                    .contentType("image/jpeg") // Determine content type dynamically if needed
                    .build();

            minioClient.putObject(objectArgs);
            //下载推动到minio后，存到数据库中
            fileMsgService.save(objectName, data.length, preview(objectName), messages);


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to upload image.", e);
        }

        return objectName;
    }


    private String getFileExtension(String url) {
        String[] parts = url.split("\\?");
        String path = parts[0];
        String extension = path.substring(path.lastIndexOf('.'));
        return extension.isEmpty() ? ".jpg" : extension; // Default to .jpg if no extension found
    }


    /**
     * 预览图片
     *
     * @param fileName
     * @return
     */
    public String preview(String fileName) {
        // 查看文件地址
        GetPresignedObjectUrlArgs build = new GetPresignedObjectUrlArgs().builder().bucket(prop.getBucketName()).object(fileName).method(Method.GET).build();
        try {
            String url = minioClient.getPresignedObjectUrl(build);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件下载
     *
     * @param fileName 文件名称
     * @param res      response
     * @return Boolean
     */
    public void download(String fileName, HttpServletResponse res) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(prop.getBucketName())
                .object(fileName).build();
        try (GetObjectResponse response = minioClient.getObject(objectArgs)) {
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
                while ((len = response.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                res.setCharacterEncoding("utf-8");
                // 设置强制下载不打开
                // res.setContentType("application/force-download");
                res.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                try (ServletOutputStream stream = res.getOutputStream()) {
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看文件对象
     *
     * @return 存储bucket内文件对象信息
     */
    public List<Item> listObjects() {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(prop.getBucketName()).build());
        List<Item> items = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return items;
    }

    public Item listObjects(String fileName) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(prop.getBucketName()).build());
        try {
            for (Result<Item> result : results) {
                if (result.get().objectName().equals(fileName)) {
                    return result.get();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 删除
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public boolean remove(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(prop.getBucketName()).object(fileName).build());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
