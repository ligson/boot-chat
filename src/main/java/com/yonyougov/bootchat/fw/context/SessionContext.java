package com.yonyougov.bootchat.fw.context;

import com.yonyougov.bootchat.user.User;
import com.yonyougov.bootchat.fw.cache.local.LocalRequestCache;
import com.yonyougov.bootchat.fw.request.RequestStore;
import org.springframework.stereotype.Component;


@Component
public class SessionContext {
    private final LocalRequestCache localRequestCache;
    private final RequestStore requestStore;

    public SessionContext(LocalRequestCache localRequestCache, RequestStore requestStore) {
        this.localRequestCache = localRequestCache;
        this.requestStore = requestStore;
    }

    public void getContextId() {
        requestStore.getSessionId();
    }

    public void setCurrentUser(User user) {
        localRequestCache.setAttr(SessionContext.class.getSimpleName() + "_user", user);
    }


    public User getCurrentUser() {
        return localRequestCache.getAttr(SessionContext.class.getSimpleName() + "_user");
    }

    public void setAttr(String key, Object object) {
        localRequestCache.setAttr(SessionContext.class.getSimpleName() + "_" + key, object);
    }

    public Object getAttr(String key) {
        return localRequestCache.getAttr(SessionContext.class.getSimpleName() + "_" + key);
    }

}
