package com.yonyougov.bootchat.chatmsg;

import com.querydsl.core.BooleanBuilder;
import com.yonyougov.bootchat.minio.file.FileMsg;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMsgServiceImpl implements ChatMsgService {
    @Autowired
    ChatMsgDao chatMsgDao;

    @Override
    public void save(ChatMsg chatMsg) {
        chatMsgDao.save(chatMsg);
    }

    @Override
    public void saveMsg(String userId, boolean assistant, String msg) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setUserId(userId);
        chatMsg.setMsg(msg);
        chatMsg.setRole(assistant ? "assistant" : "user");
        save(chatMsg);
    }

    @Override
    public void saveMsg(String userId, String msg, FileMsg fileMsg) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setUserId(userId);
        chatMsg.setMsg(msg);
        chatMsg.setCreateTime(new java.util.Date());
        chatMsg.setUpdateTime(new java.util.Date());
        chatMsg.setRole("assistant");
        chatMsg.setMsgType(fileMsg.getFileType());
        chatMsg.setUri(fileMsg.getLocalDirectory());
        chatMsg.setSize(fileMsg.getFileSize());
        save(chatMsg);
    }

    /**
     * 根据用户id删除聊天记录
     * @param userId
     */
    @Transactional
    @Override
    public void delete(String userId) {
        chatMsgDao.deleteByUserId(userId);
    }

    @Override
    public List<ChatMsg> findByUserId(String userId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QChatMsg.chatMsg.userId.eq(userId));
        return (List<ChatMsg>) chatMsgDao.findAll(builder, Sort.by(Sort.Direction.ASC, "createTime"));
    }
}
