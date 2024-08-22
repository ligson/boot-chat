package com.yonyougov.bootchat.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

@Slf4j
public class SimpleHttpClient {
    public String doGet(String url, String cookie) throws Exception {
        long startTime = System.currentTimeMillis();
        log.debug("从url：{}获取内容开始", url);
        HttpClient client = HttpClient
                .newBuilder().connectTimeout(Duration.ofMinutes(3)).build();
        HttpResponse<String> response = client.send(HttpRequest.newBuilder(new URI(url))
                        .GET().header("Cookie", cookie).build(),
                HttpResponse.BodyHandlers.ofString());
        log.debug("从url：{}获取内容成功,耗时:{}s", url, (System.currentTimeMillis() - startTime) / 1000.0);
        return response.body();
    }

    public static void download(String url, File dest, String cookie) throws Exception {
        long startTime = System.currentTimeMillis();
        System.out.println(url + "......." + dest.getName());
        log.debug("从url：{}下载文件:{}开始", url, dest.getAbsolutePath());
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        HttpClient client = HttpClient
                .newBuilder().connectTimeout(Duration.ofMinutes(3)).build();
        client.send(HttpRequest.newBuilder(new URI(url))
                        .GET().header("Cookie", cookie).build(),
                HttpResponse.BodyHandlers
                        .ofFile(Path.of(dest.getPath())));
        log.debug("从url：{}下载文件:{}成功,耗时:{}s", url, dest.getAbsolutePath(), (System.currentTimeMillis() - startTime) / 1000.0);
    }
}
