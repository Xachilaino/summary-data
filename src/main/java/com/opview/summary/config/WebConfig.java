package com.opview.summary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // 1. 務必匯入這個

@Configuration
public class WebConfig implements WebMvcConfigurer { // 2. 關鍵修正：必須實作這個介面

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允許本地開發 (localhost) 和 Vercel 的網域
                // 注意：尾端的 / 是不需要的，*.vercel.app 涵蓋了所有子網域
                .allowedOriginPatterns("http://localhost:5173", "https://*.vercel.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}