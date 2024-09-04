package com.yonyougov.bootchat.auth.controller;

import com.yonyougov.bootchat.auth.vo.RegisterByPwdReq;
import com.yonyougov.bootchat.user.UserService;
import com.yonyougov.bootchat.fw.web.vo.WebResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/register")
public class RegisterController {
    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/by_pwd")
    public WebResult registerByPwd(@RequestBody RegisterByPwdReq registerByPwdReq) {
        return userService.registerByPwd(registerByPwdReq);
    }
}
