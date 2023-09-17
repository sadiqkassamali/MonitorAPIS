package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.InputStream;
import java.util.Map;

@Configuration
public class AppConfig {

    @Value("classpath:endpoints.json")
    private InputStream endpointsJson;

    @Bean
    public Map<String, Map<String, String>> endpoints() throws Exception {
        return JsonUtil.readJsonFile(endpointsJson);
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
