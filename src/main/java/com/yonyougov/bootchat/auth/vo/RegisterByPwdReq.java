package com.yonyougov.bootchat.auth.vo;

import com.yonyougov.bootchat.fw.web.vo.BaseReq;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RegisterByPwdReq extends BaseReq {
    @NotBlank
    private String name;
    @NotBlank
    private String password;
    @NotBlank
    private String code;
    @NotBlank
    private String email;
    @NotBlank
    private String captchaCode;
    @NotBlank
    private String captchaKey;
}
