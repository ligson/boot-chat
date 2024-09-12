package com.yonyougov.bootchat.config.gpt.model;

import com.yonyougov.bootchat.config.gpt.YonDifGptProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
@Getter
@Setter
public class EmbeddingModelConfig {

    private final YonDifGptProperties yonDifGptProperties;

    public EmbeddingModelConfig(YonDifGptProperties yonDifGptProperties) {
        this.yonDifGptProperties = yonDifGptProperties;
    }

    @Bean
    @Primary
    public EmbeddingModel multiEmbeddingModel(List<EmbeddingModel> embeddingModels) {
        return new MultilEmbeddingModel(embeddingModels, yonDifGptProperties.getEmbeddingModelType());
    }
}
