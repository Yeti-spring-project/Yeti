package com.example.yetiproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${feUrl}")
    public String url;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")

                .allowedOrigins(url)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With")
                .allowCredentials(true);
    }
}