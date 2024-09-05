package com.yonyougov.bootchat.base.chatmsg;


import com.yonyougov.bootchat.minio.file.FileMsg;

import java.util.List;

public interface ChatMsgService {
    List<ChatMsg> findByUserId(String userId);

    void save(ChatMsg chatMsg);

    void saveMsg(String userId, String msg);

    void saveMsg(String userId, String msg, FileMsg fileMsg);
    void delete(String id);
}
