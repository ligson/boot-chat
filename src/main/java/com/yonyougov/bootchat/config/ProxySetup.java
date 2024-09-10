package com.yonyougov.bootchat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

@Slf4j
@Configuration
public class ProxySetup {
    private final ProxyConfig proxyConfig;

    public ProxySetup(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    @Bean
    public WebClient proxySelectorConfig() {
        log.warn("是否开启全局网络代理：" + proxyConfig.isEnable());
        WebClient.Builder builder = WebClient.builder();
        if (proxyConfig.isEnable()) {
            System.setProperty("http.proxyHost", proxyConfig.getIp());
            System.setProperty("http.proxyPort", String.valueOf(proxyConfig.getPort()));

            // 设置HTTPS代理
            System.setProperty("https.proxyHost", proxyConfig.getIp());
            System.setProperty("https.proxyPort", String.valueOf(proxyConfig.getPort()));
            HttpClient httpClient = HttpClient.create()
                    .proxy(proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP)
                            .host(proxyConfig.getIp())
                            .port(proxyConfig.getPort()));
            builder.clientConnector(new ReactorClientHttpConnector(httpClient));
        }
        return builder.build();
    }
}
