package com.chaitanya.backend.strategy;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommerceAdvisorRegistry {

    private final Map<String, CommerceAdvisor> advisors;

    public CommerceAdvisor getAdvisor(String strategy) {

        CommerceAdvisor advisor =
                advisors.get(strategy.toLowerCase());

        if (advisor == null) {
            throw new IllegalArgumentException(
                    "Unknown commerce strategy: " + strategy
            );
        }

        return advisor;
    }
}