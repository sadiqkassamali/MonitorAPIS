import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


@SpringBootTest
public class MonitorServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService Email;

    @Test
    public void testSendEmail() {
        // Mock JavaMailSender and perform assertions based on your email sending logic
        // For example, you can verify that the send method is called with the expected arguments
        Mockito.doNothing().when(javaMailSender).send(Mockito.any(SimpleMailMessage.class));

        // Call the service method to send an email
        Email.sendEmail("recipient@example.com", "Subject", "Body");

        // Add assertions here to verify the email sending behavior
    }

    // Add more test cases for other service methods
}
