package com.yonyougov.bootchat.gpt.controller;

import com.yonyougov.bootchat.fw.context.SessionContext;
import com.yonyougov.bootchat.fw.web.vo.WebResult;
import com.yonyougov.bootchat.gpt.qianfan.dto.ChatMessage2;
import com.yonyougov.bootchat.gpt.service.GptChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RequestMapping("/api/gptchat")
@RestController
public class GptChatController {

    private final ChatClient chatClient;
    private final GptChatService gptChatService;
    private final ZhiPuAiImageModel zhiPuAiImageModel;
    private final SessionContext sessionContext;

    public GptChatController(ChatClient chatClient, GptChatService gptChatService, ZhiPuAiImageModel zhiPuAiImageModel, SessionContext sessionContext) {
        this.chatClient = chatClient;
        this.gptChatService = gptChatService;
        this.zhiPuAiImageModel = zhiPuAiImageModel;
        this.sessionContext = sessionContext;
    }

    @GetMapping("/ai/generate")
    public ChatResponse generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        String userId = sessionContext.getCurrentUser().getId();
        return gptChatService.generate(userId, message);
    }

    @PostMapping("/ai/saveAllWiki")
    public WebResult saveAllWiki(@RequestBody String tooken) throws Exception {
        gptChatService.saveFile(tooken);
        return WebResult.newSuccessInstance();
    }

    @GetMapping("/ai/test")
    public void test() {
        gptChatService.AddVectorStore();
    }

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
        return gptChatService.stream(userId, messages);
    }
}
