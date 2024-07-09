package com.yonyougov.bootchat.auth.ex;

import org.springframework.security.core.AuthenticationException;

public class AuthParamErrorException extends AuthenticationException {
    public AuthParamErrorException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthParamErrorException(String msg) {
        super(msg);
    }
}
