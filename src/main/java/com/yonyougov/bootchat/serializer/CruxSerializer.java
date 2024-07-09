package com.yonyougov.bootchat.serializer;

public interface CruxSerializer {
    <T> String serialize(T t);

    <T> T deserialize(String content, Class<T> tClazz);
}
