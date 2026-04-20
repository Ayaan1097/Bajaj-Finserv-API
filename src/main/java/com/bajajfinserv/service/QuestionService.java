package com.bajajfinserv.service;

import com.bajajfinserv.model.QuestionMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class QuestionService {
    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    private final WebClient webClient;
    private final String questionOneUrl;
    private final String questionTwoUrl;
    private final int timeoutSeconds;

    public QuestionService(WebClient webClient,
            @Value("${app.question.one.url}") String questionOneUrl,
            @Value("${app.question.two.url}") String questionTwoUrl,
            @Value("${app.http.timeout.seconds}") int timeoutSeconds) {
        this.webClient = webClient;
        this.questionOneUrl = questionOneUrl;
        this.questionTwoUrl = questionTwoUrl;
        this.timeoutSeconds = timeoutSeconds;
    }

    public QuestionMetadata fetchQuestion(String regNo) {
        int questionId = determineQuestionId(regNo);
        String sourceUrl = questionId == 1 ? questionOneUrl : questionTwoUrl;
        log.info("Selected question {} from {}", questionId, sourceUrl);

        String htmlContent = fetchHtml(sourceUrl);
        String description = extractProblemDescription(htmlContent);

        log.info("Fetched problem description for question {}: {}", questionId, summarize(description));
        return new QuestionMetadata(questionId, sourceUrl, description);
    }

    private int determineQuestionId(String regNo) {
        if (regNo == null || regNo.isBlank()) {
            return 1;
        }
        String digits = regNo.replaceAll(".*?(\\d{2})$", "$1");
        try {
            int value = Integer.parseInt(digits);
            return value % 2 == 0 ? 2 : 1;
        } catch (NumberFormatException e) {
            log.warn("Cannot parse last two digits of regNo='{}'. Defaulting to question 1", regNo);
            return 1;
        }
    }

    private String fetchHtml(String url) {
        try {
            return webClient.get()
                    .uri(url)
                    .accept(MediaType.TEXT_HTML)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.warn("Failed to fetch question page {}: {} (HTTP {}). Using default question.", url,
                    e.getResponseBodyAsString(), e.getRawStatusCode());
            return getDefaultQuestionHtml();
        } catch (Exception e) {
            log.warn("Unexpected error fetching question page {}: {}. Using default question.", url, e.getMessage());
            return getDefaultQuestionHtml();
        }
    }

    private String getDefaultQuestionHtml() {
        return "<html><body>" +
                "Question: Write a SQL query to retrieve employee information grouped by department " +
                "with count of employees in each department. Filter for departments having more than 5 employees. " +
                "Order by department name in ascending order." +
                "</body></html>";
    }

    private String extractProblemDescription(String html) {
        if (html == null || html.isBlank()) {
            return "No problem description could be retrieved.";
        }
        String cleaned = html.replaceAll("(?is)<(script|style)[^>]*>.*?</\\1>", " ")
                .replaceAll("(?is)<[^>]+>", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (cleaned.length() > 1200) {
            return cleaned.substring(0, 1200) + " ...";
        }
        return cleaned;
    }

    private String summarize(String text) {
        if (text == null) {
            return "<empty>";
        }
        return text.length() > 120 ? text.substring(0, 120) + "..." : text;
    }
}
