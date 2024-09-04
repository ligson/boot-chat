package com.yonyougov.bootchat.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonyougov.bootchat.auth.config.XAssnUserDetails;
import com.yonyougov.bootchat.auth.config.captcha.CaptchaService;
import com.yonyougov.bootchat.auth.vo.AuthByPwdReq;
import com.yonyougov.bootchat.auth.vo.RegisterByPwdReq;
import com.yonyougov.bootchat.base.user.QUser;
import com.yonyougov.bootchat.fw.ex.BussinessException;
import com.yonyougov.bootchat.fw.web.vo.WebResult;
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
        User user = userDao.findOne(QUser.user.code.eq(authByPwdReq.getEmail())).orElseThrow(() -> new BussinessException(String.format("邮箱%s不存在", authByPwdReq.getEmail())));
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
        long count = userDao.count(QUser.user.email.eq(registerByPwdReq.getEmail()));
        if (count > 0) {
            return WebResult.newErrorInstance("该邮箱已存在");
        }
        if (!passwordMatch(registerByPwdReq.getPassword())) {
            return WebResult.newErrorInstance("密码不满足要求");
        }
        if (!captchaService.verify(registerByPwdReq.getCaptchaCode(), registerByPwdReq.getCaptchaKey())) {
            return WebResult.newErrorInstance("验证码错误");
        }
        if (!registerByPwdReq.getEmail().endsWith("@yonyou.com")) {
            return WebResult.newErrorInstance("邮箱后缀必须为@yonyou.com");
        }
        User user = new User();
        user.setName(registerByPwdReq.getName());
        String password = passwordEncoder.encode(registerByPwdReq.getPassword());
        user.setPassword(password);
        user.setEmail(registerByPwdReq.getEmail());
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDao.findOne(QUser.user.email.eq(email)).orElseThrow(() -> new BussinessException("邮箱{}不存在", email));
        return new XAssnUserDetails(user);
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
//    }
}
