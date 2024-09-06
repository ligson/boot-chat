package com.yonyougov.bootchat.gpt.service;

import com.yonyougov.bootchat.gpt.dto.ChatMessage2;
import com.yonyougov.bootchat.gpt.dto.WxChatMessage;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

public interface GptChatService {
    Flux<ChatResponse> stream(String userId, ChatMessage2 chatMessage);
    void AddVectorStore();
    List<File> getFileList();
    void saveFile(String tooken) throws Exception;

    ChatResponse generate(String userId, String message);
    String call(WxChatMessage wxChatMessage);
}
