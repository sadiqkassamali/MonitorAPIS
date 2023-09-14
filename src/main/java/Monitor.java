import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@Configuration
@PropertySource("classpath:application.properties")
@EnableScheduling
public class Monitor extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(Monitor.class, args);
    }
}
