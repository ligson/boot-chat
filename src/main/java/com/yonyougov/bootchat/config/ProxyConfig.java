package com.yonyougov.bootchat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "yondif.proxy")
@Data
public class ProxyConfig {
    private boolean enable;
    private String ip;
    private int port;
    private List<String> domains;
}
