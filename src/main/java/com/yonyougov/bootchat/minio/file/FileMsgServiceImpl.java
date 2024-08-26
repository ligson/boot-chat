package com.yonyougov.bootchat.minio.file;

import com.yonyougov.bootchat.base.user.User;
import com.yonyougov.bootchat.base.user.UserService;
import com.yonyougov.bootchat.fw.context.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class FileMsgServiceImpl implements FileMsgService{


    private final SessionContext sessionContext;

    @Autowired
    private FileMsgDao fileMsgDao;
    @Autowired
    private UserService userService;
    public FileMsgServiceImpl (SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }
    @Override
    public FileMsg save(String fileMinioPath) {
        int dotIndex = fileMinioPath.lastIndexOf('.');
        // 提取基础名称
        String uploadId = fileMinioPath.substring(0, dotIndex);
        // 提取扩展名
        String FileType = fileMinioPath.substring(dotIndex + 1);
//        FileMsg fileMsg = FileMsg.builder()
//                .fileName(zanshimeiyou )
//                .fileType(FileType)
//                .uploadId(uploadId)
//                .localdirectory(fileMinioPath)
//                .build();
        return fileMsgDao.save(new FileMsg());
    }

    @Override
    public FileMsg save(String fileName, Integer size,String url) {
        String id = sessionContext.getCurrentUser().getId();
        User user = userService.findById(id);
        int dotIndex = fileName.lastIndexOf('.');
        // 提取基础名称
        String uploadId = fileName.substring(0, dotIndex);
        // 提取扩展名
        String FileType = fileName.substring(dotIndex + 1);
        //封装对象
        FileMsg fileMsg =new FileMsg();
        fileMsg.setFileName(fileName);
        fileMsg.setFileType(FileType);
        fileMsg.setUploadId(uploadId);
        fileMsg.setLocalDirectory(url);
        fileMsg.setFileSize(size);
        fileMsg.setIsUploadComplete(1);
        fileMsg.setCreateTime(Instant.now());
        fileMsg.setModifyTime(Instant.now());
        fileMsg.setCreator(user.getName());
        fileMsg.setModifier(user.getName());
        fileMsg.setId(uploadId);
        fileMsg.setPubts(Instant.now());
        //保存并返回
        return fileMsgDao.save(fileMsg);
    }


    @Override
    public FileMsg findById(String id) {
        Optional<FileMsg> byId = fileMsgDao.findById(id);
        return byId.orElse(null);
    }

    @Override
    public FileMsg findByUploadId(String uploadId) {
        return fileMsgDao.findByUploadId(uploadId);
    }
    public  FileMsg findByFileName(String fileName) {
        return fileMsgDao.findByFileName(fileName);
    }

    @Override
    public void delete(String id) {
        fileMsgDao.deleteById(id);
    }
}
