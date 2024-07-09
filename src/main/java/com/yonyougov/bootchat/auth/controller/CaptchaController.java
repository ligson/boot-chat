package com.yonyougov.bootchat.auth.controller;

import com.yonyougov.bootchat.auth.config.captcha.CaptchaService;
import com.yonyougov.bootchat.vo.WebResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "登陆模块")
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {
    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @Operation(summary = "获取验证码")
    @GetMapping("/img")
    public WebResult captcha() {
        return captchaService.generate();
    }

}
