package com.spring.springboot;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate;

    // Constructor injection
    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void executeWebhookFlow() {
        try {
            // POST to generateWebhook
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "John Doe");
            requestBody.put("regNo", "REG12347");
            requestBody.put("email", "john@example.com");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            String webhookUrl = (String) responseBody.get("webhook");
            String accessToken = (String) responseBody.get("accessToken");

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);

            // Solve SQL
            String finalQuery = solveSQLProblem("REG12347");

            // Submit solution
            submitSolution(webhookUrl, accessToken, finalQuery);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String solveSQLProblem(String regNo) {
        char lastDigit = regNo.charAt(regNo.length() - 1);
        if (Character.getNumericValue(lastDigit) % 2 == 0) {
            return getQuestion2Query(); // Call helper for even regNo
        } else {
            return getQuestion1Query(); // Call helper for odd regNo
        }
    }

    // Helper method for Question 1 SQL
    private String getQuestion1Query() {
        return "SELECT * FROM employees WHERE salary > 50000;"; // Replace with actual solution
    }

    // Helper method for Question 2 SQL
    private String getQuestion2Query() {
        return "SELECT * FROM employees WHERE department = 'HR';"; // Replace with actual solution
    }

    private void submitSolution(String webhookUrl, String accessToken, String finalQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, String> body = new HashMap<>();
        body.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, entity, String.class);

        System.out.println("Webhook Response: " + response.getBody());
    }
}
