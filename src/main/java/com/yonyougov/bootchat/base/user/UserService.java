package com.yonyougov.bootchat.base.user;

import com.yonyougov.bootchat.auth.vo.AuthByPwdReq;
import com.yonyougov.bootchat.auth.vo.RegisterByPwdReq;
import com.yonyougov.bootchat.vo.WebResult;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    WebResult authByPwd(AuthByPwdReq authByPwdReq);

    WebResult registerByPwd(RegisterByPwdReq registerByPwdReq);

     User findById(String id);
    WebResult logout(String token);

}
