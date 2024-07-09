package com.yonyougov.bootchat.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "yondif.cors")
@Getter
@Setter
public class SecurityConfigProperties {
    private List<String> allowedOrigins;
}
