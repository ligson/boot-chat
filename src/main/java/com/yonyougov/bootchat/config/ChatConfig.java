package com.yonyougov.bootchat.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.autoconfigure.chat.client.ChatClientBuilderConfigurer;
import org.springframework.ai.autoconfigure.qianfan.QianFanChatProperties;
import org.springframework.ai.autoconfigure.qianfan.QianFanConnectionProperties;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.qianfan.QianFanChatModel;
import org.springframework.ai.qianfan.api.QianFanApi;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.MilvusVectorStore;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.transport.ProxyProvider;


@Configuration
public class ChatConfig {

    private final ProxyConfig proxyConfig;

    public ChatConfig(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }


        @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(OllamaEmbeddingModel.class)
    public MilvusVectorStore vectorStore(MilvusServiceClient milvusClient, @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel, MilvusVectorStoreProperties properties) {
        MilvusVectorStore.MilvusVectorStoreConfig config = MilvusVectorStore.MilvusVectorStoreConfig.builder().withCollectionName(properties.getCollectionName()).
                withDatabaseName(properties.getDatabaseName())
                .withIndexType(IndexType.valueOf(properties.getIndexType().name()))
                .withMetricType(MetricType.valueOf(properties.getMetricType().name()))
                .withIndexParameters(properties.getIndexParameters())
                .withEmbeddingDimension(8192)
                .build();

        return new MilvusVectorStore(milvusClient, embeddingModel, properties.isInitializeSchema());
    }
//    @Bean
//    @ConditionalOnMissingBean
//    @ConditionalOnClass(OllamaEmbeddingModel.class)
//    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
//        return new PgVectorStore(jdbcTemplate, embeddingModel);
//    }

    @Bean
    @Scope("prototype")
    @ConditionalOnClass(OllamaChatModel.class)
    public ChatClient.Builder ollamaChatClientBuilder(ChatClientBuilderConfigurer chatClientBuilderConfigurer,
                                                      OllamaChatModel ollamaChatModel) {
        ChatClient.Builder builder = ChatClient.builder(ollamaChatModel);
        return chatClientBuilderConfigurer.configure(builder);

    }

    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
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

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = QianFanChatProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true",
            matchIfMissing = true)
    public QianFanChatModel qianFanChatModel(QianFanConnectionProperties commonProperties,
                                             QianFanChatProperties chatProperties, RestClient.Builder restClientBuilder, RetryTemplate retryTemplate,
                                             ResponseErrorHandler responseErrorHandler) {

        var qianFanApi = qianFanApi(chatProperties.getBaseUrl(), commonProperties.getBaseUrl(),
                chatProperties.getApiKey(), commonProperties.getApiKey(), chatProperties.getSecretKey(),
                commonProperties.getSecretKey(), restClientBuilder, responseErrorHandler);

        return new QianFanChatModel(qianFanApi, chatProperties.getOptions(), retryTemplate);
    }

    private QianFanApi qianFanApi(String baseUrl, String commonBaseUrl, String apiKey, String commonApiKey,
                                  String secretKey, String commonSecretKey, RestClient.Builder restClientBuilder,
                                  ResponseErrorHandler responseErrorHandler) {

        String resolvedBaseUrl = StringUtils.hasText(baseUrl) ? baseUrl : commonBaseUrl;
        Assert.hasText(resolvedBaseUrl, "QianFan base URL must be set");

        String resolvedApiKey = StringUtils.hasText(apiKey) ? apiKey : commonApiKey;
        Assert.hasText(resolvedApiKey, "QianFan API key must be set");

        String resolvedSecretKey = StringUtils.hasText(secretKey) ? secretKey : commonSecretKey;
        Assert.hasText(resolvedSecretKey, "QianFan Secret key must be set");

        WebClient.Builder builder = WebClient.builder();
        if (proxyConfig.isEnable()) {
            HttpClient httpClient = HttpClient.create()
                    .proxy(proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP)
                            .host(proxyConfig.getIp())
                            .port(proxyConfig.getPort()));
            builder.clientConnector(new ReactorClientHttpConnector(httpClient));

        }
//        builder.clientConnector()
        return new QianFanApi(resolvedBaseUrl, resolvedApiKey, resolvedSecretKey, restClientBuilder, builder,
                responseErrorHandler);
    }
}
