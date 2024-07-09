package com.yonyougov.bootchat.auth.config.captcha;


import com.yonyougov.bootchat.vo.WebResult;
import jakarta.validation.constraints.NotNull;

public interface CaptchaService {
    WebResult generate();

    boolean verify(@NotNull String captchaCode, @NotNull String captchaKey);
}
