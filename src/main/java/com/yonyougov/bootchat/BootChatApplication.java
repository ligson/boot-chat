package com.yonyougov.bootchat;

import com.yonyougov.bootchat.config.CustomProxySelector;
import com.yonyougov.bootchat.config.ProxyConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.ProxySelector;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class BootChatApplication {

    private final ProxyConfig proxyConfig;

    public BootChatApplication(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(BootChatApplication.class, args);
    }

    @PostConstruct
    public void init() {
        ProxySelector.setDefault(new CustomProxySelector(
                ProxySelector.getDefault(),
                proxyConfig.isEnable(),
                proxyConfig.getIp(),
                proxyConfig.getPort(),
                proxyConfig.getDomains()
        ));
    }

}
