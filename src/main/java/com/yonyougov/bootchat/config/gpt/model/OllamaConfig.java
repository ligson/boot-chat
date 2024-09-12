//package com.yonyougov.bootchat.config.gpt.model;
//
//import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
//import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
//import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
//import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
//import org.apache.hc.core5.http.HttpHost;
//import org.springframework.ai.autoconfigure.ollama.OllamaConnectionDetails;
//import org.springframework.ai.ollama.api.OllamaApi;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.http.client.ClientHttpRequestFactory;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.http.client.SimpleClientHttpRequestFactory;
//import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;
//import org.springframework.web.client.RestClient;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.net.InetSocketAddress;
//import java.net.Proxy;
//
//@Configuration
//public class OllamaConfig {
//
//    public  ClientHttpRequestFactory requestFactory() {
//        // 定义代理主机和端口
//        HttpHost proxy = new HttpHost("10.16.22.246", 8889);
//        // 创建 HttpClient 并设置代理
//        CloseableHttpClient client = HttpClientBuilder.create()
//                .setProxy(proxy)
//                .build();
//        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);
//        return factory;
//    }
//
//    @Bean
//    @Primary
//    public OllamaApi ollamaApi(OllamaConnectionDetails connectionDetails, RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder) {
//        ClientHttpRequestFactory clientHttpRequestFactory = requestFactory();
//        restClientBuilder.requestFactory(clientHttpRequestFactory);
////        webClientBuilder.clientConnector(new HttpComponentsClientHttpConnector());
//        return new OllamaApi(connectionDetails.getBaseUrl(), restClientBuilder, webClientBuilder);
//    }
//
//
//}
