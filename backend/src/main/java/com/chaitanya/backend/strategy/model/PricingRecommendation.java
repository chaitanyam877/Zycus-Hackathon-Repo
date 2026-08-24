package com.chaitanya.backend.strategy.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
import com.chaitanya.backend.enums.*;

@Data
@Builder
public class PricingRecommendation {

    private BigDecimal recommendedPrice;

    private Direction direction;

    private double confidence;

    private String reasoning;
}