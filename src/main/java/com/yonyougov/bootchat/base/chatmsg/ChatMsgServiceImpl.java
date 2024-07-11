package com.yonyougov.bootchat.base.chatmsg;

import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<ChatMsg> findByUserId(String userId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QChatMsg.chatMsg.userId.eq(userId));
        return (List<ChatMsg>) chatMsgDao.findAll(builder);

    }
}
