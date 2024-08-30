package com.yonyougov.bootchat.config.gpt;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class ChatConfig {

   /* @Bean
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
    }*/
//    @Bean
//    @ConditionalOnMissingBean
//    @ConditionalOnClass(OllamaEmbeddingModel.class)
//    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
//        return new PgVectorStore(jdbcTemplate, embeddingModel);
//    }


    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }


    @Bean
    public RestClient.Builder restClientBuilder() {
        // Customize the RestClient.Builder here if needed
        return RestClient.builder();
    }


}
