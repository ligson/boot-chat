package com.yonyougov.bootchat.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.util.List;

@Data
@Slf4j
public class MultilEmbeddingModel implements EmbeddingModel {

    private List<EmbeddingModel> embeddingModels;
    private final String type;

    public MultilEmbeddingModel(List<EmbeddingModel> embeddingModels, String type) {
        this.embeddingModels = embeddingModels;
        this.type = type;
        log.debug("分词模型:" + type);
    }

    public EmbeddingModel getEmbeddingModel() {
        for (EmbeddingModel embeddingModel : embeddingModels) {
            if (embeddingModel.getClass().getName().toLowerCase().startsWith(type.toLowerCase())) {
                return embeddingModel;
            }
        }
        throw new RuntimeException("No embedding model found for type " + type);
    }


    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        return getEmbeddingModel().call(request);
    }

    @Override
    public float[] embed(Document document) {
        return getEmbeddingModel().embed(document);
    }
}
