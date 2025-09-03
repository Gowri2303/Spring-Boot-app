package com.bfs.hiring;

import com.bfs.hiring.dto.GenerateWebhookRequest;
import com.bfs.hiring.dto.GenerateWebhookResponse;
import com.bfs.hiring.dto.FinalSubmissionRequest;
import com.bfs.hiring.props.AppProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupRunner implements ApplicationRunner {

    private final RestTemplate restTemplate;
    private final AppProps props;

    private static final String GENERATE_WEBHOOK_URL =
            "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    @Override
    public void run(ApplicationArguments args) {
        try {
            GenerateWebhookRequest body = new GenerateWebhookRequest(
                    props.getName(), props.getRegNo(), props.getEmail());
            ResponseEntity<GenerateWebhookResponse> resp =
                    restTemplate.postForEntity(GENERATE_WEBHOOK_URL, body, GenerateWebhookResponse.class);

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new IllegalStateException("Failed to generate webhook: " + resp.getStatusCode());
            }

            String webhook = resp.getBody().getWebhook();
            String accessToken = resp.getBody().getAccessToken();
            log.info("Webhook: {}", webhook);
            log.info("Access token received.");

            boolean isOdd = isLastTwoDigitsOdd(props.getRegNo());
            String finalQuery = isOdd ? props.getFinalQuery().getOdd() : props.getFinalQuery().getEven();
            if (finalQuery == null || finalQuery.isBlank()) {
                throw new IllegalArgumentException("finalQuery is empty. Fill app.finalQuery.odd/even in application.yml");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", normalizeAuth(accessToken, props.isForceBearerPrefix()));

            FinalSubmissionRequest submit = new FinalSubmissionRequest(finalQuery.trim());
            HttpEntity<FinalSubmissionRequest> entity = new HttpEntity<>(submit, headers);

            ResponseEntity<String> submitResp = restTemplate.exchange(
                    webhook, HttpMethod.POST, entity, String.class);

            log.info("Submission status: {}", submitResp.getStatusCode());
            log.info("Submission response: {}", submitResp.getBody());
        } catch (Exception e) {
            log.error("Flow failed: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    private static boolean isLastTwoDigitsOdd(String regNo) {
        String digits = regNo.replaceAll("\\D+", "");
        if (digits.length() < 2) return Integer.parseInt(digits) % 2 != 0;
        int lastTwo = Integer.parseInt(digits.substring(digits.length() - 2));
        return (lastTwo % 2) != 0;
    }

    private static String normalizeAuth(String token, boolean forceBearer) {
        if (token == null) return "";
        String t = token.trim();
        boolean hasBearer = t.regionMatches(true, 0, "Bearer ", 0, 7);
        if (hasBearer) return t;
        return forceBearer ? "Bearer " + t : t;
    }
}
