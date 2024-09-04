package com.yonyougov.bootchat.gpt.qianfan.service;

import com.yonyougov.bootchat.gpt.qianfan.dto.ChatMessage2;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

public interface QianfanService {
    Flux<ChatResponse> stream(String userId, ChatMessage2 chatMessage);
    void AddVectorStore();
    List<File> getFileList();
    void saveFile(String tooken) throws Exception;
}
