import com.example.MonitorController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class AppConfig {


    @Value("${json.files.directory}")
    private String jsonFilesDirectory;

    @Bean
    public MonitorController monitorController(List<Map<String, Map<String, String>>> allEndpoints) {
        return new MonitorController(allEndpoints);
    }

    @Bean
    public List<Map<String, Map<String, String>>> allEndpoints() {
        List<Map<String, Map<String, String>>> allEndpoints = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Load JSON files from a directory
            File jsonFilesDir = new File(jsonFilesDirectory);
            File[] jsonFiles = jsonFilesDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

            if (jsonFiles != null) {
                for (File jsonFile : jsonFiles) {
                    Map<String, Map<String, String>> endpoints = objectMapper.readValue(jsonFile, new TypeReference<Map<String, Map<String, String>>>() {});
                    allEndpoints.add(endpoints);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allEndpoints;
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
