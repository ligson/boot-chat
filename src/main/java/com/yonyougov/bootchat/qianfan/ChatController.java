package com.yonyougov.bootchat.qianfan;

import com.yonyougov.bootchat.fw.context.SessionContext;
import com.yonyougov.bootchat.qianfan.dto.ChatMessage2;
import com.yonyougov.bootchat.qianfan.service.QianfanService;

import com.yonyougov.bootchat.vo.WebResult;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.qianfan.QianFanChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RequestMapping("/qianfan")
@RestController
public class ChatController {

    private final QianFanChatModel chatClient;
    private final QianfanService qianFanService;
    private final ZhiPuAiImageModel zhiPuAiImageModel;
    private final SessionContext sessionContext;

    public ChatController(QianFanChatModel chatClient, QianfanService qianFanService, ZhiPuAiImageModel zhiPuAiImageModel, SessionContext sessionContext) {
        this.chatClient = chatClient;
        this.qianFanService = qianFanService;
        this.zhiPuAiImageModel = zhiPuAiImageModel;
        this.sessionContext = sessionContext;
    }

    @GetMapping("/ai/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatClient.call(message));
    }

    //        @PostMapping("/ai/generateStream")
//    public Flux<ChatResponse> generateStream(@RequestBody List<ChatMessage> messages) {
//        qianFanService.stream();
//        Prompt prompt = new Prompt(
//                messages.stream().map(m -> {
//                    if (MessageType.ASSISTANT.getValue().equals(m.getRole())) {
//                        return new AssistantMessage(m.getContent());
//                    } else {
//                        return new UserMessage(m.getContent());
//                    }
//                }).collect(Collectors.toList()));
//        return chatClient.stream(prompt);
//    }
    @PostMapping("/ai/image")
    public WebResult getImage(@RequestBody ChatMessage2 messages) {
        String image = zhiPuAiImageModel.call(
                new ImagePrompt(messages.getProblem())
        ).getResult().getOutput().getUrl();
        return WebResult.newSuccessInstance().putData("image", image);
    }

    @PostMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestBody ChatMessage2 messages) {
        String userId = sessionContext.getCurrentUser().getId();
        return qianFanService.stream(userId, messages);
    }
}