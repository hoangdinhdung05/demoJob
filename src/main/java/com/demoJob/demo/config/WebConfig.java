package com.demoJob.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Cho phép tất cả endpoint
                .allowedOrigins("http://localhost:3000") // FE chạy ở cổng 3000
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
