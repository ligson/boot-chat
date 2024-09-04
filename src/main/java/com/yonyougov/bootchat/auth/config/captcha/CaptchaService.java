package com.yonyougov.bootchat.auth.config.captcha;


import com.yonyougov.bootchat.fw.web.vo.WebResult;
import jakarta.validation.constraints.NotNull;

public interface CaptchaService {
    WebResult generate();

    boolean verify(@NotNull String captchaCode, @NotNull String captchaKey);
}
