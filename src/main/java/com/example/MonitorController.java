package com.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MonitorController {
    private static final Logger log = LoggerFactory.getLogger(MonitorController.class);
    @Autowired
    private EmailService emailService;

    @Autowired
    private Map<String, Map<String, String>> endpoints;

    @Value("${global.schedule}")
    private long globalSchedule;

    @Value("${last.success.time.threshold}")
    private long lastSuccessTimeThreshold;

    private String authToken;
    private Map<String, Long> lastSuccessfulResponseTimes = new ConcurrentHashMap<>();

    @Value("classpath:endpoints.json")
    private Resource endpointsJson;

    @PostMapping("/sendAdHocRequest")
    public String sendAdHocRequest(@RequestBody Map<String, String> request) {
        String uniqueId = request.get("uniqueId");
        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put("url", request.get("url"));
        properties.put("method", request.get("method"));
        properties.put("endpoint", request.get("endpoint"));
        properties.put("requestBody", request.get("requestBody"));
        properties.put("contentType", request.get("contentType"));

        String responseStatus = String.valueOf(sendRequest(uniqueId, properties));

        // Determine whether the response is up, down, or unknown
        if ("200".equals(responseStatus)) {
            return "Up";
        } else if ("404".equals(responseStatus)) {
            return "Down";
        } else {
            return "Unknown";
        }

    }

    @PostConstruct
    public void init() {
        endpoints = loadEndpointsFromJson();
    }

    private Map<String, Map<String, String>> loadEndpointsFromJson() {
        try (InputStream inputStream = endpointsJson.getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException e) {
            log.error("Error loading endpoints from JSON: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }


    @Scheduled(fixedRateString = "${check.endpoints.interval}")
    public void checkEndpointsStatus() {
        Map<String, Map<String, String>> endpointProperties = getEndpoints();
        try {
            long currentTime = System.currentTimeMillis();

            for (Map.Entry<String, Map<String, String>> entry : endpoints.entrySet()) {
                String uniqueId = entry.getKey();
                Map<String, String> endpoint = entry.getValue();

                try {
                    if (!lastSuccessfulResponseTimes.containsKey(uniqueId) ||
                            currentTime - lastSuccessfulResponseTimes.get(uniqueId) > lastSuccessTimeThreshold) {

                        ResponseEntity<String> response = sendRequestforloop(uniqueId,endpointProperties);

                        if (response.getStatusCode() == HttpStatus.OK) {
                            endpoint.put("status", "UP");
                        } else {
                            endpoint.put("status", "DOWN");
                        }
                    }
                } catch (Exception e) {
                    endpoint.put("status", "ERROR");
                }
            }
        } catch (Exception e) {
            log.error("An error occurred while checking endpoints status: {}", e.getMessage());
        }
    }

    private ResponseEntity<String> sendRequestforloop(String uniqueId, Map<String, Map<String, String>> propertiesloop) {
        try {
            String urlloop = propertiesloop.get("url").toString();
            String methodloop = propertiesloop.get("method").toString();
            String endpointloop = propertiesloop.get("endpoint").toString();
            String requestBodyloop = propertiesloop.get("requestBody").toString();
            String contentTypeloop = propertiesloop.get("contentType").toString();



            String fullUrl = urlloop + "/" + endpointloop;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.setContentType(MediaType.valueOf(contentTypeloop));

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyloop, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> responseEntity = restTemplate.exchange(fullUrl, HttpMethod.valueOf(methodloop), requestEntity, String.class);

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


    @Scheduled(fixedRateString = "${global.schedule}")
    public void sendRequestsToEndpoints() {
        try {
            authenticateAndRetrieveToken();

            Map<String, Map<String, String>> endpointsList = getEndpoints();

            for (Map.Entry<String, Map<String, String>> entry : endpointsList.entrySet()) {
                String uniqueId = entry.getKey();
                Map<String, String> endpointProperties = entry.getValue();
                sendRequest(uniqueId, endpointProperties);
            }
        } catch (Exception e) {
            log.error("An error occurred: {}", e.getMessage());
        }
    }

    @GetMapping("/endpoints")
    public Map<String, Map<String, String>> getEndpoints() {
        return endpoints;
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