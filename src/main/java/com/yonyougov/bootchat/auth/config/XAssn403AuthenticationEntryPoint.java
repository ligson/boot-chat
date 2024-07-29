package com.yonyougov.bootchat.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonyougov.bootchat.vo.ErrorType;
import com.yonyougov.bootchat.vo.WebResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class XAssn403AuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    public XAssn403AuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String stace = ExceptionUtils.getStackTrace(authException);
        log.error("认证异常:{},stace:{}", authException.getMessage(), stace);
        WebResult web = WebResult.newErrorInstance(authException.getMessage());
        web.setErrorType(ErrorType.Inner);
        web.setStackTrace(stace);
        web.setHttpCode(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        response.getWriter().print(objectMapper.writeValueAsString(web));
    }
}
