package com.yonyougov.bootchat.config;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class CustomProxySelector extends ProxySelector {

    private final ProxySelector defaultSelector;
    private final boolean enable;
    private final String ip;
    private final int port;
    private final List<String> domains;

    public CustomProxySelector(ProxySelector defaultSelector, boolean enable, String ip, int port, List<String> domains) {
        this.defaultSelector = defaultSelector;
        this.enable = enable;
        this.ip = ip;
        this.port = port;
        this.domains = domains;
    }

    @Override
    public List<Proxy> select(URI uri) {
        List<Proxy> proxies = new ArrayList<>();
        if (enable && domains.stream().anyMatch(domain -> uri.getHost().endsWith(domain))) {
            proxies.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port)));
        } else {
            proxies = defaultSelector.select(uri);
        }
        return proxies;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        defaultSelector.connectFailed(uri, sa, ioe);
    }
}