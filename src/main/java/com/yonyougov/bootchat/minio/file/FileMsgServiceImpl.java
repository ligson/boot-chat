package com.yonyougov.bootchat.minio.file;

import com.yonyougov.bootchat.base.chatmsg.ChatMsg;
import com.yonyougov.bootchat.base.chatmsg.ChatMsgService;
import com.yonyougov.bootchat.base.user.User;
import com.yonyougov.bootchat.base.user.UserService;
import com.yonyougov.bootchat.fw.context.SessionContext;
import com.yonyougov.bootchat.qianfan.dto.ChatMessage2;
import org.apache.tika.utils.StringUtils;
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
    @Autowired
    private ChatMsgService chatMsgService;
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
    public FileMsg save(String fileName, Integer size, String url, ChatMessage2 chatMessage) {
        String userId = sessionContext.getCurrentUser().getId();
        User user = userService.findById(userId);
        int dotIndex = fileName.lastIndexOf('.');
        // 提取基础名称
        String uploadId = fileName.substring(0, dotIndex);
        // 提取扩展名
        String FileType = fileName.substring(dotIndex + 1);
        //封装对象
        FileMsg fileMsg =new FileMsg();
        fileMsg.setFileName(fileName);
        //这里最好判断是什么类型
        if (FileType.equals("png") || FileType.equals("jpg") || FileType.equals("jpeg") || FileType.equals("gif") || FileType.equals("bmp")) {
            fileMsg.setFileType("image");
        }else
            fileMsg.setFileType("file"); //不是的话就换成别的类型
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

        //用户提问入库，聊天记录
        if (!StringUtils.isEmpty(chatMessage.getProblem())) {
            ChatMsg chatMsg = new ChatMsg();
            chatMsg.setMsg(chatMessage.getProblem());
            chatMsg.setRole("user");
            chatMsg.setUserId(userId);
            //使上次回答的消息时间早于当前时间，以便于排序
            chatMsg.setCreateTime(new Date(System.currentTimeMillis()));
            chatMsgService.save(chatMsg);
        }
        //睡眠一秒，等待数据库写入完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //ai回复入库，聊天记录
        chatMsgService.saveMsg(user.getId(),url,fileMsg);

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
