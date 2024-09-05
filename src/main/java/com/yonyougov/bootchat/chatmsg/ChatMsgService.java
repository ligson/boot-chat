package com.yonyougov.bootchat.chatmsg;


import com.yonyougov.bootchat.minio.file.FileMsg;

import java.util.List;

public interface ChatMsgService {
    List<ChatMsg> findByUserId(String userId);

    void save(ChatMsg chatMsg);

    /***
     * 保存聊天消息
     * @param userId    用户id
     * @param assistant true: assistant，false: user
     * @param msg       聊天消息
     */
    void saveMsg(String userId, boolean assistant, String msg);
    void saveMsg(String userId, String msg, FileMsg fileMsg);
    void delete(String id);

}
