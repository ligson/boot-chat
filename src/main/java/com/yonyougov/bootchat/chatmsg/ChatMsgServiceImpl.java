package com.yonyougov.bootchat.chatmsg;

import com.querydsl.core.BooleanBuilder;
import com.yonyougov.bootchat.base.chatmsg.QChatMsg;
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
    public void saveMsg(String userId, String msg) {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setUserId(userId);
        chatMsg.setMsg(msg);
        chatMsg.setCreateTime(new java.util.Date());
        chatMsg.setUpdateTime(new java.util.Date());
        chatMsg.setRole("assistant");
        save(chatMsg);
    }

    @Override
    public List<ChatMsg> findByUserId(String userId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QChatMsg.chatMsg.userId.eq(userId));
        return (List<ChatMsg>) chatMsgDao.findAll(builder, Sort.by(Sort.Direction.ASC, "createTime"));
    }
}
