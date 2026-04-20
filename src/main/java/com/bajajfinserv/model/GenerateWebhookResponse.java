package com.bajajfinserv.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateWebhookResponse {
    @JsonProperty("webhook")
    private String webhookUrl;

    @JsonProperty("accessToken")
    private String accessToken;

    public GenerateWebhookResponse() {
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    @JsonProperty("webhook")
    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty("accessToken")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonAnySetter
    public void handleUnknown(String name, Object value) {
        if (value == null) {
            return;
        }
        String normalized = name.toLowerCase();
        if (normalized.contains("webhook")) {
            this.webhookUrl = value.toString();
        }
        if (normalized.contains("token")) {
            this.accessToken = value.toString();
        }
    }
}
