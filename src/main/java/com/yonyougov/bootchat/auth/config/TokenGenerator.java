package com.yonyougov.bootchat.auth.config;


import com.yonyougov.bootchat.base.user.User;

public interface TokenGenerator {
    String generator(User user);

    User decodeToken(String token);
}
