package com.chaitanya.backend.strategy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommerceRecommendation {

    private PricingRecommendation pricing;

    private ReorderRecommendation reorder;
}
