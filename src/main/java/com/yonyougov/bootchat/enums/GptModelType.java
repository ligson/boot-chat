package com.yonyougov.bootchat.enums;

import lombok.Getter;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.Model;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.qianfan.QianFanChatModel;
import org.springframework.ai.qianfan.QianFanEmbeddingModel;
import org.springframework.ai.qianfan.QianFanImageModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;

@Getter
public enum GptModelType {
    /***
     * OLLAMA模型
     */
    OLLAMA(OllamaEmbeddingModel.class, OllamaChatModel.class, OllamaChatModel.class),
    /***
     * OPEN AI模型
     */
    OPEN_AI(OpenAiEmbeddingModel.class, OpenAiChatModel.class, OpenAiImageModel.class),
    /***
     * QIAN FAN模型
     */
    QIAN_FAN(QianFanEmbeddingModel.class, QianFanChatModel.class, QianFanImageModel.class),
    /***
     * 智谱AI模型
     */
    ZHI_PU_AI(ZhiPuAiEmbeddingModel.class, ZhiPuAiChatModel.class, ZhiPuAiImageModel.class);
    private final Class<? extends EmbeddingModel> embeddingModelClass;
    private final Class<? extends ChatModel> chatModelClass;
    private final Class<? extends Model> imageModelClass;

    GptModelType(Class<? extends EmbeddingModel> embeddingModelClass,
                 Class<? extends ChatModel> chatModelClass,
                 Class<? extends Model> imageModelClass) {
        this.embeddingModelClass = embeddingModelClass;
        this.chatModelClass = chatModelClass;
        this.imageModelClass = imageModelClass;
    }

}
