import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@Configuration
@PropertySource("classpath:application.properties")
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(MonitorController.class, args);
    }
}
