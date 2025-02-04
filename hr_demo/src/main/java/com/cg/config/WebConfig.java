package com.cg.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 设置前端跨域访问资源
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径的跨域请求
                .allowedOrigins("http://localhost:5174") // 允许的前端源
                .allowedMethods("*") // 允许的HTTP方法
                .allowedHeaders("*") // 允许的HTTP头
                .allowCredentials(true); // 是否允许发送Cookie
    }
}