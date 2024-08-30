package com.yonyougov.bootchat.config.gpt;

import org.springframework.ai.autoconfigure.chat.client.ChatClientBuilderConfigurer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class OllamaConfig {
    @Bean
    @Scope("prototype")
    @ConditionalOnClass(OllamaChatModel.class)
    public ChatClient.Builder ollamaChatClientBuilder(ChatClientBuilderConfigurer chatClientBuilderConfigurer,
                                                      OllamaChatModel ollamaChatModel) {
        ChatClient.Builder builder = ChatClient.builder(ollamaChatModel);
        return chatClientBuilderConfigurer.configure(builder);

    }
}
