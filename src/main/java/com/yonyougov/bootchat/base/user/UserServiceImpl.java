package com.yonyougov.bootchat.base.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonyougov.bootchat.auth.config.XAssnUserDetails;
import com.yonyougov.bootchat.auth.config.captcha.CaptchaService;
import com.yonyougov.bootchat.auth.vo.AuthByPwdReq;
import com.yonyougov.bootchat.auth.vo.RegisterByPwdReq;
import com.yonyougov.bootchat.ex.BussinessException;
import com.yonyougov.bootchat.vo.WebResult;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private final PasswordEncoder passwordEncoder;
    private final CaptchaService captchaService;

    @Autowired
    public UserServiceImpl(UserDao userDao, StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper, PasswordEncoder passwordEncoder, CaptchaService captchaService) {
        this.userDao = userDao;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.captchaService = captchaService;
    }

    private boolean passwordMatch(String rawPassword) {
        return rawPassword.matches("^(?=.*[A-Za-z0-9@#$%^&+=])(?=\\S+$).{8,}$");
    }

    @Override
    @SneakyThrows
    public WebResult authByPwd(AuthByPwdReq authByPwdReq) {
        User user = userDao.findOne(QUser.user.code.eq(authByPwdReq.getCode())).orElseThrow(() -> new BussinessException(String.format("用户名%s不存在", authByPwdReq.getCode())));
        String password = passwordEncoder.encode(authByPwdReq.getPassword());
        if (user.getPassword().equals(password)) {
            String token = DigestUtils.md5Hex(user.getName() + "#" + UUID.randomUUID());
            stringRedisTemplate.boundValueOps("ibootchat:token-user:" + token).set(objectMapper.writeValueAsString(user));
            return WebResult.newSuccessInstance().putData("token", token);
        } else {
            return WebResult.newErrorInstance("密码错误");
        }
    }

    @Override
    public WebResult registerByPwd(RegisterByPwdReq registerByPwdReq) {
        long count = userDao.count(QUser.user.name.eq(registerByPwdReq.getName()));
        if (count > 0) {
            return WebResult.newErrorInstance("用户名已存在");
        }
        if (!passwordMatch(registerByPwdReq.getPassword())) {
            return WebResult.newErrorInstance("密码不满足要求");
        }
        if (!captchaService.verify(registerByPwdReq.getCaptchaCode(), registerByPwdReq.getCaptchaKey())) {
            return WebResult.newErrorInstance("验证码错误");
        }
        User user = new User();
        user.setName(registerByPwdReq.getName());
        String password = passwordEncoder.encode(registerByPwdReq.getPassword());
        user.setPassword(password);
        user.setCode(registerByPwdReq.getCode());
        userDao.save(user);
        return WebResult.newSuccessInstance();
    }

    @Override
    public User findById(String id) {
        return userDao.findById(id).orElseThrow();
    }


    @Override
    public WebResult logout(String token) {
        // 清除认证信息
        SecurityContextHolder.clearContext();
        stringRedisTemplate.delete("ibootchat:token-user:" + token);
        return WebResult.newSuccessInstance();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findOne(QUser.user.name.eq(username)).orElseThrow(() -> new BussinessException("用户名{}不存在", username));
        return new XAssnUserDetails(user);
    }
}
