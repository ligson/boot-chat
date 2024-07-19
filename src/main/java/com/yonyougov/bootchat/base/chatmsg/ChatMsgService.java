package com.yonyougov.bootchat.base.chatmsg;


import java.util.List;

public interface ChatMsgService {
    List<ChatMsg> findByUserId(String userId);

    void save(ChatMsg chatMsg);

    void saveMsg(String userId, String msg);
}
