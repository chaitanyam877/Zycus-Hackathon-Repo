package com.chaitanya.backend.service;

import com.chaitanya.backend.config.LLMProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LLMClient {

    private final LLMProperties properties;
    private final ObjectMapper objectMapper;

    private final RestClient restClient =
            RestClient.builder().build();

    public String chat(String prompt) {

        Map<String, Object> request = Map.of(
                "model", properties.getModel(),
                "messages", new Object[]{
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                }
        );

        String response = restClient
                .post()
                .uri(properties.getBaseUrl())
                .header(
                        "Authorization",
                        "Bearer " + properties.getApiKey()
                )
                .header(
                        "Content-Type",
                        "application/json"
                )
                .header(
                        "product",
                        properties.getProduct()
                )
                .header(
                        "Cookie",
                        properties.getCookie()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);

        try {

            JsonNode root =
                    objectMapper.readTree(response);

            return root
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse LLM response: " + response,
                    e
            );
        }
    }
}