package com.yonyougov.bootchat.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yonyougov.bootchat.vo.WebResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class XAssnLogoutSuccessHandler implements LogoutSuccessHandler {
    private final ObjectMapper objectMapper;

    public XAssnLogoutSuccessHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(WebResult.newSuccessInstance()));
    }
}
