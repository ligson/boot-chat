package com.yonyougov.bootchat.config.gpt.vector;

import io.micrometer.observation.ObservationRegistry;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {


    @Bean
    public MilvusVectorStore vectorStore(MilvusServiceClient milvusClient, @Qualifier("multiEmbeddingModel") EmbeddingModel embeddingModel,
                                         MilvusVectorStoreProperties properties, BatchingStrategy batchingStrategy,
                                         ObjectProvider<ObservationRegistry> observationRegistry,
                                         ObjectProvider<VectorStoreObservationConvention> customObservationConvention) {

        MilvusVectorStore.MilvusVectorStoreConfig config = MilvusVectorStore.MilvusVectorStoreConfig.builder()
                .withCollectionName(properties.getCollectionName())
                .withDatabaseName(properties.getDatabaseName())
                .withIndexType(IndexType.valueOf(properties.getIndexType().name()))
                .withMetricType(MetricType.valueOf(properties.getMetricType().name()))
                .withIndexParameters(properties.getIndexParameters())
                .withEmbeddingDimension(properties.getEmbeddingDimension())
                .build();

        return new MilvusVectorStore(milvusClient, embeddingModel, config, properties.isInitializeSchema(),
                batchingStrategy, observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP),
                customObservationConvention.getIfAvailable(() -> null));
    }
}
