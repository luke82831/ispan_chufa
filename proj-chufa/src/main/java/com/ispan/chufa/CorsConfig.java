package com.ispan.chufa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // 允許所有 /api/ 開頭的路由
                        .allowedOrigins("http://localhost:5173") // 允許前端請求
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允許的 HTTP 方法
                        .allowedHeaders("*") // 允許所有 Headers
                        .allowCredentials(true); // 允許帶有憑證的請求
            }
        };
    }
}
