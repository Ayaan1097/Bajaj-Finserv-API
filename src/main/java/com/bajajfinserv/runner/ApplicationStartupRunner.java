package com.bajajfinserv.runner;

import com.bajajfinserv.model.GenerateWebhookRequest;
import com.bajajfinserv.model.GenerateWebhookResponse;
import com.bajajfinserv.model.QuestionMetadata;
import com.bajajfinserv.model.TestWebhookRequest;
import com.bajajfinserv.service.QuestionService;
import com.bajajfinserv.service.SolutionService;
import com.bajajfinserv.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartupRunner.class);

    private final WebhookService webhookService;
    private final QuestionService questionService;
    private final SolutionService solutionService;
    private final String name;
    private final String regNo;
    private final String email;

    public ApplicationStartupRunner(WebhookService webhookService,
            QuestionService questionService,
            SolutionService solutionService,
            @Value("${app.credentials.name}") String name,
            @Value("${app.credentials.reg-no}") String regNo,
            @Value("${app.credentials.email}") String email) {
        this.webhookService = webhookService;
        this.questionService = questionService;
        this.solutionService = solutionService;
        this.name = name;
        this.regNo = regNo;
        this.email = email;
    }

    @Override
    public void run(String... args) {
        log.info("Starting automated webhook solution runner");

        GenerateWebhookRequest generateRequest = new GenerateWebhookRequest(name, regNo, email);
        GenerateWebhookResponse generateResponse = webhookService.generateWebhook(generateRequest);

        if (generateResponse == null || generateResponse.getAccessToken() == null
                || generateResponse.getWebhookUrl() == null) {
            throw new IllegalStateException("Webhook generation response did not contain required data");
        }

        log.info("Generated webhook URL: {}", generateResponse.getWebhookUrl());
        log.info("Received access token of length {}", generateResponse.getAccessToken().length());

        QuestionMetadata questionMetadata = questionService.fetchQuestion(regNo);
        String finalQuery = solutionService.buildFinalQuery(questionMetadata);

        log.info("Final SQL query selected: {}", finalQuery);

        TestWebhookRequest testRequest = new TestWebhookRequest(finalQuery);
        webhookService.submitSolution(generateResponse.getAccessToken(), testRequest);

        log.info("Automated startup workflow completed successfully");
    }
}
