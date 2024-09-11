package com.yonyougov.bootchat.config.gpt.vector;

import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.observation.ObservationRegistry;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import jakarta.annotation.Priority;
import org.springframework.ai.autoconfigure.vectorstore.milvus.MilvusVectorStoreProperties;
import org.springframework.ai.embedding.BatchingStrategy;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.MilvusVectorStore;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.observation.VectorStoreObservationConvention;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VectorStoreConfig {


//    @Bean
//    public MilvusVectorStore vectorStore(MilvusServiceClient milvusClient, @Qualifier("multiEmbeddingModel") EmbeddingModel embeddingModel,
//                                         MilvusVectorStoreProperties properties, BatchingStrategy batchingStrategy,
//                                         ObjectProvider<ObservationRegistry> observationRegistry,
//                                         ObjectProvider<VectorStoreObservationConvention> customObservationConvention) {
//
//        MilvusVectorStore.MilvusVectorStoreConfig config = MilvusVectorStore.MilvusVectorStoreConfig.builder()
//                .withCollectionName(properties.getCollectionName())
//                .withDatabaseName(properties.getDatabaseName())
//                .withIndexType(IndexType.valueOf(properties.getIndexType().name()))
//                .withMetricType(MetricType.valueOf(properties.getMetricType().name()))
//                .withIndexParameters(properties.getIndexParameters())
//                .withEmbeddingDimension(properties.getEmbeddingDimension())
//                .build();
//
//        return new MilvusVectorStore(milvusClient, embeddingModel, config, properties.isInitializeSchema(),
//                batchingStrategy, observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP),
//                customObservationConvention.getIfAvailable(() -> null));
//    }

//    @Bean
//    @ConfigurationProperties(prefix = "spring.pg.datasource")
//    public DataSource pgDataSource() {
//        return new HikariDataSource();
//    }

    @Bean("postgresqlDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.pg.datasource")
    public DataSourceProperties pgDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "postgresqlDatasource")
    public DataSource testDataSource(@Qualifier("postgresqlDataSourceProperties") DataSourceProperties testDataSourceProperties) {
        return testDataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "postgresqlJdbcTemplate")
    public JdbcTemplate postgresqlJdbcTemplate(@Qualifier("postgresqlDatasource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    //    @Bean
//    public JdbcTemplate pgJdbcTemplate(@Qualifier("pgDataSource") DataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }
//
    @Bean
    @Primary
    public PgVectorStore myVectorStore(@Qualifier("postgresqlJdbcTemplate") JdbcTemplate pgJdbcTemplate, @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingMode) {

        return new PgVectorStore(pgJdbcTemplate, embeddingMode);
    }
}
