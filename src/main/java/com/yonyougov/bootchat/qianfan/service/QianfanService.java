package com.yonyougov.bootchat.qianfan.service;

import com.yonyougov.bootchat.base.user.User;
import com.yonyougov.bootchat.qianfan.dto.ChatMessage2;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

public interface QianfanService {
    Flux<ChatResponse> stream(String userId, ChatMessage2 chatMessage);
}
