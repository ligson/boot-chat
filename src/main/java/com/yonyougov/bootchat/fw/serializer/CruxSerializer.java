package com.yonyougov.bootchat.fw.serializer;

public interface CruxSerializer {
    <T> String serialize(T t);

    <T> T deserialize(String content, Class<T> tClazz);
}
