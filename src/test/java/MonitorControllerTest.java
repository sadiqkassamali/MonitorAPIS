import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(MonitorController.class)
public class MonitorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private MonitorController monitorService;

    @InjectMocks
    private MonitorController monitorController;

    @Test
    public void testSendAdHocRequest() throws Exception {
        // Mock your service method to return a response
        Mockito.when(monitorService.sendAdHocRequest(Mockito.anyMap())).thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

        mockMvc.perform(MockMvcRequestBuilders.post("/sendAdHocRequest")
                        .contentType("application/json")
                        .content("{\"url\":\"http://example.com\", \"method\":\"GET\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Add more test cases for other controller methods
}