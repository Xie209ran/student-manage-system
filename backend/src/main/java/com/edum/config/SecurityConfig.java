package com.edum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security配置
 * 注意：本项目使用自定义JWT认证，不使用Spring Security的默认认证机制
 * 引入spring-boot-starter-security只是为了使用BCryptPasswordEncoder
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（我们使用JWT Token认证）
            .csrf(csrf -> csrf.disable())
            // 禁用HTTP Basic认证（避免未认证请求返回401）
            .httpBasic(httpBasic -> httpBasic.disable())
            // 禁用Form Login（避免重定向到登录页）
            .formLogin(formLogin -> formLogin.disable())

            // 配置请求授权
            .authorizeHttpRequests(auth -> auth
                // 放行所有请求（由JwtInterceptor进行认证）
                .anyRequest().permitAll()
            )

            // 禁用Session管理（我们使用无状态JWT）
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
