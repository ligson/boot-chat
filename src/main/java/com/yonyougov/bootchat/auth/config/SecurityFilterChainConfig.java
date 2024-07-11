package com.yonyougov.bootchat.auth.config;

import com.yonyougov.bootchat.auth.filter.JwtAuthenticationFilter;
import com.yonyougov.bootchat.auth.filter.LoginByPwdAuthenticationFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
public class SecurityFilterChainConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final LoginByPwdAuthenticationFilter loginByPwdAuthenticationFilter;
    private final XAssn403AuthenticationEntryPoint xAssn403AuthenticationEntryPoint;
    private final XAssnLogoutSuccessHandler xAssnLogoutSuccessHandler;

    private final CorsConfigurationSource corsConfigurationSource;
    private final String[] IGNORE_URLS = new String[]{
            "/api/captcha/**",
//            "/api/user/me",
            "/qianfan/ai/generateStream/**",
            "/api/login/**",
            "/api/register/**"
    };

    public SecurityFilterChainConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                                     LoginByPwdAuthenticationFilter loginByPwdAuthenticationFilter, XAssn403AuthenticationEntryPoint xAssn403AuthenticationEntryPoint, XAssnLogoutSuccessHandler xAssnLogoutSuccessHandler, CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.loginByPwdAuthenticationFilter = loginByPwdAuthenticationFilter;
        this.xAssn403AuthenticationEntryPoint = xAssn403AuthenticationEntryPoint;
        this.xAssnLogoutSuccessHandler = xAssnLogoutSuccessHandler;
        this.corsConfigurationSource = corsConfigurationSource;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(IGNORE_URLS)
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                ).exceptionHandling((exceptionHandlingConfigurer) -> exceptionHandlingConfigurer.authenticationEntryPoint(xAssn403AuthenticationEntryPoint))

                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(
                        corsConfigurationSource
                ))
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutUrl("/api/login/logout")
                        .logoutSuccessHandler(xAssnLogoutSuccessHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).authenticationManager(authenticationManager)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginByPwdAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
