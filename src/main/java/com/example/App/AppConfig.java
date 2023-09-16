import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public Map<String, Map<String, String>> endpoints() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/endpoints.json");
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStream, Map.class);
        } catch (IOException e) {
            // Handle exception here
            e.printStackTrace();
            return new HashMap<>(); // Return an empty map in case of error
        }
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
