package com.yonyougov.bootchat.auth.controller;

import com.yonyougov.bootchat.auth.vo.AuthByPwdReq;
import com.yonyougov.bootchat.user.UserService;
import com.yonyougov.bootchat.fw.web.vo.WebResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/by_pwd")
    public WebResult authByPwd(@RequestBody AuthByPwdReq authByPwdReq) {
        return userService.authByPwd(authByPwdReq);
    }

    @PostMapping("/logout")
    public WebResult logout(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        String token;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }else{
            return WebResult.newErrorInstance("参数错误");
        }
        return userService.logout(token);
    }
}
