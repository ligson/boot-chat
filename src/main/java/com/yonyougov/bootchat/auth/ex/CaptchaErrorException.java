package com.yonyougov.bootchat.auth.ex;

import org.springframework.security.core.AuthenticationException;

public class CaptchaErrorException extends AuthenticationException {
    public CaptchaErrorException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CaptchaErrorException(String msg) {
        super(msg);
    }
}
