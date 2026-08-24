package com.chaitanya.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LLMProperties {

    private String baseUrl;
    private String model;
    private String product;
    private String apiKey;
    private String cookie;
}