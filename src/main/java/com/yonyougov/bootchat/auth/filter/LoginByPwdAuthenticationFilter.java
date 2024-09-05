package com.yonyougov.bootchat.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonyougov.bootchat.auth.config.TokenGenerator;
import com.yonyougov.bootchat.auth.config.XAssnUserDetails;
import com.yonyougov.bootchat.auth.config.captcha.CaptchaService;
import com.yonyougov.bootchat.auth.ex.AuthParamErrorException;
import com.yonyougov.bootchat.auth.ex.CaptchaErrorException;
import com.yonyougov.bootchat.auth.vo.AuthByPwdReq;
import com.yonyougov.bootchat.fw.ex.BussinessException;
import com.yonyougov.bootchat.fw.ex.InnerException;
import com.yonyougov.bootchat.fw.web.vo.ErrorType;
import com.yonyougov.bootchat.fw.web.vo.WebResult;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class LoginByPwdAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper objectMapper;
    private final TokenGenerator tokenGenerator;
    private final CaptchaService captchaService;
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/auth/by_pwd", HttpMethod.POST.name());

    public LoginByPwdAuthenticationFilter(ObjectMapper objectMapper, TokenGenerator tokenGenerator, AuthenticationManager authenticationManager, CaptchaService captchaService) {
        super(authenticationManager);
        this.captchaService = captchaService;
        setRequiresAuthenticationRequestMatcher(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.objectMapper = objectMapper;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        AuthByPwdReq auth;
        try {
            auth = objectMapper.readValue(request.getInputStream(), AuthByPwdReq.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new AuthParamErrorException("参数错误");
        }
        // 获取用户名和密码
        String username = auth.getEmail();
        String password = auth.getPassword();
        boolean check = captchaService.verify(auth.getCaptchaCode(), auth.getCaptchaKey());
        if (!check) {
            throw new CaptchaErrorException("验证码错误");
        }
        // 构造Token，使用UsernamepasswordAuthenticationToken
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        // 设置一些客户端IP等信息
        setDetails(request, token);
        // 交给AuthenticationManager进行认证
        return this.getAuthenticationManager().authenticate(token);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        XAssnUserDetails user = (XAssnUserDetails) authResult.getPrincipal();
        // 生成Token返回
        String token = tokenGenerator.generator(user.getUser());
        WebResult webResult = WebResult.newSuccessInstance().putData("token", "Bearer " + token);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(webResult));
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        WebResult webResult = WebResult.newInstance();
        webResult.setSuccess(false);
        webResult.setHttpCode(HttpStatus.UNAUTHORIZED.value());
        webResult.setErrorType(ErrorType.Inner);
        webResult.setStackTrace(ExceptionUtils.getStackTrace(failed));
        if (failed instanceof LockedException) {
            webResult.setErrorMsg("账号被锁定!");
            webResult.setErrorType(ErrorType.Business);
        } else if (failed instanceof CredentialsExpiredException) {
            webResult.setErrorMsg("用户密码过期!");
            webResult.setErrorType(ErrorType.Business);
        } else if (failed instanceof AccountExpiredException) {
            webResult.setErrorMsg("用户账号过期!");
            webResult.setErrorType(ErrorType.Business);
        } else if (failed instanceof DisabledException) {
            webResult.setErrorMsg("用户账号被禁用!");
            webResult.setErrorType(ErrorType.Business);
        } else if (failed instanceof BadCredentialsException) {
            webResult.setErrorMsg("用户账号或密码错误!");
            webResult.setErrorType(ErrorType.Business);
        } else if (failed instanceof AuthParamErrorException) {
            webResult.setErrorMsg(failed.getMessage());
            webResult.setErrorType(ErrorType.Business);
        } else if (failed instanceof CaptchaErrorException) {
            webResult.setErrorMsg(failed.getMessage());
            webResult.setErrorType(ErrorType.Business);
        } else if (failed.getCause() instanceof BussinessException) {
            webResult.setErrorMsg(failed.getCause().getMessage());
            webResult.setErrorType(ErrorType.Business);
        } else if (failed.getCause() instanceof InnerException) {
            webResult.setErrorMsg(failed.getCause().getMessage());
            webResult.setErrorType(ErrorType.Inner);
        } else {
            webResult.setErrorType(ErrorType.Inner);
            webResult.setErrorMsg(failed.getMessage());
        }
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(webResult));
    }
}
