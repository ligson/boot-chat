package com.yonyougov.bootchat.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.autoconfigure.chat.client.ChatClientBuilderConfigurer;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.qianfan.QianFanChatModel;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;

@Configuration
public class ChatConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(OllamaEmbeddingModel.class)
    public MilvusVectorStore vectorStore(MilvusServiceClient milvusClient, @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel, MilvusVectorStoreProperties properties) {
        MilvusVectorStore.MilvusVectorStoreConfig config = MilvusVectorStore.MilvusVectorStoreConfig.builder().withCollectionName(properties.getCollectionName()).withDatabaseName(properties.getDatabaseName()).withIndexType(IndexType.valueOf(properties.getIndexType().name())).withMetricType(MetricType.valueOf(properties.getMetricType().name())).withIndexParameters(properties.getIndexParameters()).withEmbeddingDimension(properties.getEmbeddingDimension()).build();
        return new MilvusVectorStore(milvusClient, embeddingModel, config, properties.isInitializeSchema());
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnClass(OllamaChatModel.class)
    public ChatClient.Builder ollamaChatClientBuilder(ChatClientBuilderConfigurer chatClientBuilderConfigurer,
                                                      OllamaChatModel ollamaChatModel) {
        ChatClient.Builder builder = ChatClient.builder(ollamaChatModel);
        return chatClientBuilderConfigurer.configure(builder);

    }

    @Bean
    @Scope("prototype")
    @ConditionalOnClass(QianFanChatModel.class)
    public ChatClient.Builder qianFanChatClientBuilder(ChatClientBuilderConfigurer chatClientBuilderConfigurer,
                                                       QianFanChatModel qianFanChatModel) {
        ChatClient.Builder builder = ChatClient.builder(qianFanChatModel);
        return chatClientBuilderConfigurer.configure(builder);
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        // Customize the RestClient.Builder here if needed
        return RestClient.builder();
    }
}
