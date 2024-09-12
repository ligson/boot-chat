package com.yonyougov.bootchat.config.gpt.model;

import com.yonyougov.bootchat.enums.GptModelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
public class MultiChatModel implements ChatModel {

    private List<ChatModel> chatModels;
    private final GptModelType type;

    public MultiChatModel(List<ChatModel> chatModels, GptModelType type) {
        this.chatModels = chatModels;
        this.type = type;
        log.debug("聊天模型:" + type);
    }

    public ChatModel getChatModel() {
        for (ChatModel chatModel : chatModels) {
            if (type.getChatModelClass().isAssignableFrom(chatModel.getClass())) {
                return chatModel;
            }
        }
        throw new RuntimeException("No chat model found for type " + type);
    }


    @Override
    public String call(String message) {
        return getChatModel().call(message);
    }

    @Override
    public String call(Message... messages) {
        return getChatModel().call(messages);
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        return getChatModel().call(prompt);
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return getChatModel().getDefaultOptions();
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        return getChatModel().stream(prompt);
    }
}
