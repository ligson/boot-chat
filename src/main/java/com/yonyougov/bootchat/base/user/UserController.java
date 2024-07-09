package com.yonyougov.bootchat.base.user;

import com.yonyougov.bootchat.fw.context.SessionContext;
import com.yonyougov.bootchat.vo.WebResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/user")
@RestController
public class UserController {
    private final UserService userService;
    private final SessionContext sessionContext;

    public UserController(UserService userService, SessionContext sessionContext) {
        this.userService = userService;
        this.sessionContext = sessionContext;
    }

    @PostMapping("/me")
    @Operation(summary = "我的信息")
    public WebResult me() {
        return WebResult.newSuccessInstance().putData("user", sessionContext.getCurrentUser());
    }

}