package com.chaitanya.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.chaitanya.backend.config.CommerceStrategyProperties;
import com.chaitanya.backend.strategy.CommerceAdvisorRegistry;

import java.util.Map;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class CommerceStrategyController {

    private final CommerceStrategyProperties properties;

    private final CommerceAdvisorRegistry registry;

    @GetMapping("/strategy")
    public Map<String, String> getStrategy() {

        return Map.of(
                "strategy",
                properties.getStrategy()
        );
    }

    @PatchMapping("/strategy")
    public Map<String, String> updateStrategy(
            @RequestParam String strategy
    ) {

        registry.getAdvisor(strategy);

        properties.setStrategy(
                strategy.toLowerCase()
        );

        return Map.of(
                "strategy",
                properties.getStrategy()
        );
    }
}