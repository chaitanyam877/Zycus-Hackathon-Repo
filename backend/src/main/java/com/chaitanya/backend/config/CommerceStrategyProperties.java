package com.chaitanya.backend.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "commerce")
public class CommerceStrategyProperties {

    private String strategy = "rule";
}