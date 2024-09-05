package com.yonyougov.bootchat.auth.filter;

import com.yonyougov.bootchat.auth.config.TokenGenerator;
import com.yonyougov.bootchat.auth.config.XAssnUserDetails;
import com.yonyougov.bootchat.user.User;
import com.yonyougov.bootchat.fw.context.SessionContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;


@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenGenerator tokenGenerator;
    private final SessionContext sessionContext;

    public JwtAuthenticationFilter(TokenGenerator tokenGenerator,
                                   SessionContext sessionContext) {
        this.tokenGenerator = tokenGenerator;
        this.sessionContext = sessionContext;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = null;
            final String bearerToken = request.getHeader("Authorization");
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                token = bearerToken.substring(7);
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(token)) {
                User user = tokenGenerator.decodeToken(token);
                if (user != null) {
                    XAssnUserDetails userDetails = new XAssnUserDetails(user);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                    log.info("authenticated user with username :{}", user.getName());

                    // 设置 SecurityContext
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);

                    // 将当前用户放入 sessionContext
                    sessionContext.setCurrentUser(user);

                }
            }
        }

        filterChain.doFilter(request, response);
    }

}
