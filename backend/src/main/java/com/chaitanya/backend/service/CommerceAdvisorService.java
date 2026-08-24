package com.chaitanya.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.chaitanya.backend.entity.*;
import com.chaitanya.backend.strategy.*;
import com.chaitanya.backend.strategy.model.CommerceRecommendation;
import com.chaitanya.backend.config.*;
import com.chaitanya.backend.enums.*;

@Service
@RequiredArgsConstructor
public class CommerceAdvisorService {

    private final CommerceAdvisorRegistry registry;

    private final CommerceStrategyProperties properties;

    public CommerceRecommendation recommend(
            Product product,
            TriggerReason triggerReason,
            double categoryAverageVelocity
    ) {

        CommerceAdvisor advisor =
                registry.getAdvisor(
                        properties.getStrategy()
                );

        return advisor.recommend(
                product,
                triggerReason,
                categoryAverageVelocity
        );
    }
}