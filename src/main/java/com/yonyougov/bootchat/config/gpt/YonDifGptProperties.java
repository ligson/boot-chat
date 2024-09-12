package com.yonyougov.bootchat.config.gpt;

import com.yonyougov.bootchat.enums.GptModelType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("yondif.gpt")
@Getter
@Setter
public class YonDifGptProperties {
    private GptModelType embeddingModelType;
    private GptModelType chatModelType;
}
