package com.yonyougov.bootchat.fw.context;


import com.yonyougov.bootchat.fw.cache.local.LocalRequestCache;
import com.yonyougov.bootchat.fw.request.RequestStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RequestContext {
    @Autowired
    private RequestStore requestStore;
    @Autowired
    private LocalRequestCache localRequestCache;

    public String getRequestId() {
        return requestStore.getHeader("X-Request-Id");
    }

    public void setAttr(String key, Object object) {
        localRequestCache.setAttr(RequestContext.class.getSimpleName() + "_" + key, object);
    }

    public Object getAttr(String key) {
        return localRequestCache.getAttr(RequestContext.class.getSimpleName() + "_" + key);
    }
}
