package com.chaitanya.backend.strategy.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommerceRecommendation {

    private PricingRecommendation pricing;

    private ReorderRecommendation reorder;
}
