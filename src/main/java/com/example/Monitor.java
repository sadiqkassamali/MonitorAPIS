import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@Configuration
@PropertySource("classpath:application.properties")
@EnableScheduling
public class Monitor {
    public static void main(String[] args) {
        SpringApplication.run(Monitor.class, args);
    }
}
