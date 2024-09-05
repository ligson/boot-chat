package com.yonyougov.bootchat.config.gpt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("yondif.gpt")
@Getter
@Setter
public class YonDifGptProperties {
    private String embeddingModelType;
    private String chatModelType;
}
