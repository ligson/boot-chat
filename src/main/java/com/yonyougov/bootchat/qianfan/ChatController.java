package com.yonyougov.bootchat.qianfan;

import com.yonyougov.bootchat.qianfan.dto.ChatMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.qianfan.QianFanChatModel;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/qianfan")
@RestController
public class ChatController {

    private final QianFanChatModel chatClient;

    public ChatController(QianFanChatModel chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/ai/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", chatClient.call(message));
    }

    @PostMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestBody List<ChatMessage> messages) {
        Prompt prompt = new Prompt(
                messages.stream().map(m -> {
                    if (MessageType.ASSISTANT.getValue().equals(m.getRole())) {
                        return new AssistantMessage(m.getContent());
                    } else {
                        return new UserMessage(m.getContent());
                    }
                }).collect(Collectors.toList()));
        return chatClient.stream(prompt);
    }
}