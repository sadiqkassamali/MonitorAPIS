package com.example;

import com.example.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class AppConfig {


    @Bean
    public List<Map<String, String>> endpoints() throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:endpoints.json");
        List<Map<String, String>> endpoints = new ArrayList<>();

        for (Resource resource : resources) {
            endpoints.addAll(JsonUtil.readJsonFile(resource.getInputStream()));
        }

        return endpoints;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowedOrigins("http://localhost:3000"); // Add your React app's origin here
            }
        };
    }

}
