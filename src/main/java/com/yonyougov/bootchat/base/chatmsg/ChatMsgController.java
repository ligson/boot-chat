package com.yonyougov.bootchat.base.chatmsg;


import com.yonyougov.bootchat.fw.context.SessionContext;
import com.yonyougov.bootchat.vo.WebResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/msg")
@RestController
public class ChatMsgController {
    private final SessionContext sessionContext;
    private final ChatMsgService chatMsgService;

    public ChatMsgController(SessionContext sessionContext, ChatMsgService chatMsgService) {
        this.sessionContext = sessionContext;
        this.chatMsgService = chatMsgService;
    }

    @PostMapping("/findByUserId")
    public WebResult findByUserId() {
        String userId = sessionContext.getCurrentUser().getId();
        List<ChatMsg> byUserId = chatMsgService.findByUserId(userId);
        return WebResult.newSuccessInstance().putData("data", byUserId);
    }
}
