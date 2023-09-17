package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MonitorController {
    private static final Logger log = LoggerFactory.getLogger(MonitorController.class);
    @Autowired
    private EmailService emailService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${global.schedule}")
    private long globalSchedule;

    @Value("${last.success.time.threshold}")
    private long lastSuccessTimeThreshold;

    private String authToken;
    private Map<String, Long> lastSuccessfulResponseTimes = new ConcurrentHashMap<>();

    @Scheduled(fixedRateString = "${global.schedule}")
    public void sendRequestsToEndpoints() {
        try {
            authenticateAndRetrieveToken();

            for (Map<String, String> endpoint : endpoints) {
                String uniqueId = endpoint.get("uniqueId");
                sendRequest(uniqueId, endpoint);
            }
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage());
        }
    }

    @Autowired
    private List<Map<String, String>> endpoints;


    @PostMapping("/sendAdHocRequest")
    public ResponseEntity<String> sendAdHocRequest(@RequestBody Map<String, String> request) {
        String uniqueId = request.get("uniqueId");
        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put("url", request.get("url"));
        properties.put("method", request.get("method"));
        properties.put("endpoint", request.get("endpoint"));
        properties.put("requestBody", request.get("requestBody"));
        properties.put("contentType", request.get("contentType"));

        return sendRequest(uniqueId, properties);
    }



    private void authenticateAndRetrieveToken() {
        try {
            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> authRequestEntity = new HttpEntity<>("{}", authHeaders);
            RestTemplate authRestTemplate = new RestTemplate();
            ResponseEntity<String> authResponseEntity = authRestTemplate.exchange("http://localhost:8080/authenticate", HttpMethod.POST, authRequestEntity, String.class);

            if (authResponseEntity.getStatusCode() == HttpStatus.OK) {
                authToken = authResponseEntity.getBody();
                log.info("Authentication successful! Auth token: {}", authToken);
            } else {
                log.warn("Authentication failed with status code: {}", authResponseEntity.getStatusCode());
            }
        } catch (RestClientException e) {
            log.error("Error occurred during authentication: {}", e.getMessage());
        }
    }

    private ResponseEntity<String> sendRequest(String uniqueId, Map<String, String> properties) {
        try {
            String url = properties.get("url");
            String method = properties.get("method");
            String endpoint = properties.get("endpoint");
            String requestBody = properties.get("requestBody");
            String contentType = properties.get("contentType");

            String fullUrl = url + "/" + endpoint;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.setContentType(MediaType.valueOf(contentType));

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> responseEntity = restTemplate.exchange(fullUrl, HttpMethod.valueOf(method), requestEntity, String.class);

            HttpStatusCode statusCode = responseEntity.getStatusCode();
            String statusMessage = (statusCode == HttpStatus.OK) ? "UP" : statusCode.toString();

            log.info("Monitoring {} - {} : {}", fullUrl, statusMessage);

            lastSuccessfulResponseTimes.put(fullUrl, System.currentTimeMillis());

            long currentTime = System.currentTimeMillis();
            if (lastSuccessfulResponseTimes.containsKey(fullUrl)) {
                long lastSuccessTime = lastSuccessfulResponseTimes.get(fullUrl);
                if (currentTime - lastSuccessTime > lastSuccessTimeThreshold) {
                    sendDownNotificationEmail(uniqueId);
                }
            }

            return responseEntity;
        } catch (Exception e) {
            log.error("Error occurred during request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private void sendDownNotificationEmail(String uniqueId) {
        try {
            String subject = "Unique ID Down Alert";
            String body = "The unique ID " + uniqueId + " has been down for more than 5 hours.";

            String to = "your_email@example.com";

            emailService.sendEmail(to, subject, body);
            log.info("Email notification sent for unique ID: {}", uniqueId);
        } catch (Exception e) {
            log.error("Error sending email notification for unique ID {}: {}", uniqueId, e.getMessage());
        }
    }
}