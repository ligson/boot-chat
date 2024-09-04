package com.yonyougov.bootchat.chatmsg;

import java.lang.String;

import com.yonyougov.bootchat.fw.base.domain.CrudDao;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMsgDao extends CrudDao<ChatMsg, String> {
}
