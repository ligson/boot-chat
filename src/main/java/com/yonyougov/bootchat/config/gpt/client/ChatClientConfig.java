package com.yonyougov.bootchat.config.gpt.client;

import com.yonyougov.bootchat.config.gpt.YonDifGptProperties;
import com.yonyougov.bootchat.config.gpt.model.MultiChatModel;
import org.springframework.ai.autoconfigure.chat.client.ChatClientBuilderConfigurer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {
    private final YonDifGptProperties yonDifGptProperties;

    public ChatClientConfig(YonDifGptProperties yonDifGptProperties) {
        this.yonDifGptProperties = yonDifGptProperties;
    }

    @Bean
    public MultiChatModel multiChatModel(List<ChatModel> chatModels) {
        return new MultiChatModel(chatModels, yonDifGptProperties.getChatModelType());
    }

    @Bean
    public ChatClient.Builder multiChatClientBuilder(ChatClientBuilderConfigurer chatClientBuilderConfigurer, MultiChatModel multiChatModel) {
        ChatClient.Builder builder = ChatClient.builder(multiChatModel);
        return chatClientBuilderConfigurer.configure(builder);
    }

    @Bean
    public ChatClient chatClient(@Qualifier("multiChatClientBuilder") ChatClient.Builder multiChatClientBuilder) {
        return multiChatClientBuilder.build();
    }
}
