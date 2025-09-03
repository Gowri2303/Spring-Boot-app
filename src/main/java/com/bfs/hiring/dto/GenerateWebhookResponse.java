package com.bfs.hiring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateWebhookResponse {
    private String webhook;
    private String accessToken;
}
