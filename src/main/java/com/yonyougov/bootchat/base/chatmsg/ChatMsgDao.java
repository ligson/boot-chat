package com.yonyougov.bootchat.base.chatmsg;

import java.lang.String;

import com.yonyougov.bootchat.base.domain.CrudDao;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMsgDao extends CrudDao<ChatMsg, String> {
}
