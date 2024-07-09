package com.yonyougov.bootchat.auth.config.captcha;

import com.yonyougov.bootchat.vo.WebResult;
import io.springboot.captcha.GifCaptcha;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class EasyCaptchaServiceImp implements CaptchaService {
    private final StringRedisTemplate stringRedisTemplate;

    public EasyCaptchaServiceImp(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public WebResult generate() {
        // 使用gif验证码
        GifCaptcha gifCaptcha = new GifCaptcha(130, 48, 4);
        String verCode = gifCaptcha.text().toLowerCase();
        String verKey = UUID.randomUUID().toString();
        BoundValueOperations<String, String> ops = stringRedisTemplate.boundValueOps("iboot-chat:captcha:" + verKey);
        ops.set(verCode);
        ops.expire(5, TimeUnit.MINUTES);
        return WebResult.newSuccessInstance().putData("verKey", verKey).putData("img", gifCaptcha.toBase64());
    }

    @Override
    public boolean verify(String captchaCode, String captchaKey) {
        BoundValueOperations<String, String> ops = stringRedisTemplate.boundValueOps("iboot-chat:captcha:" + captchaKey);
        String code = ops.get();
        return captchaCode.equalsIgnoreCase(code);
    }
}
