package com.bajajfinserv.service;

import com.bajajfinserv.model.GenerateWebhookRequest;
import com.bajajfinserv.model.GenerateWebhookResponse;
import com.bajajfinserv.model.TestWebhookRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class WebhookService {
    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final WebClient webClient;
    private final String generateWebhookUrl;
    private final String testWebhookUrl;

    public WebhookService(WebClient webClient,
            @Value("${app.api.generate-webhook-url}") String generateWebhookUrl,
            @Value("${app.api.test-webhook-url}") String testWebhookUrl) {
        this.webClient = webClient;
        this.generateWebhookUrl = generateWebhookUrl;
        this.testWebhookUrl = testWebhookUrl;
    }

    public GenerateWebhookResponse generateWebhook(GenerateWebhookRequest request) {
        log.info("Generating webhook with regNo={}", request.getRegNo());
        try {
            GenerateWebhookResponse response = webClient.post()
                    .uri(generateWebhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GenerateWebhookResponse.class)
                    .block();

            if (response != null) {
                log.info("Webhook URL: {}", response.getWebhookUrl());
                log.info("Access Token received with length: {}",
                        response.getAccessToken() != null ? response.getAccessToken().length() : 0);
            }

            return response;
        } catch (WebClientResponseException e) {
            log.error("Webhook generation API returned {}: {}", e.getRawStatusCode(), e.getResponseBodyAsString(), e);
            throw new IllegalStateException("Failed to generate webhook", e);
        } catch (Exception e) {
            log.error("Unexpected error while generating webhook", e);
            throw new IllegalStateException("Failed to generate webhook", e);
        }
    }

    public void submitSolution(String accessToken, TestWebhookRequest request) {
        log.info("Submitting final query to test webhook");
        try {
            webClient.post()
                    .uri(testWebhookUrl)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            log.info("Solution submitted successfully");
        } catch (WebClientResponseException e) {
            log.error("Solution submission API returned {}: {}", e.getRawStatusCode(), e.getResponseBodyAsString(), e);
            throw new IllegalStateException("Failed to submit solution", e);
        } catch (Exception e) {
            log.error("Unexpected error while submitting solution", e);
            throw new IllegalStateException("Failed to submit solution", e);
        }
    }
}
