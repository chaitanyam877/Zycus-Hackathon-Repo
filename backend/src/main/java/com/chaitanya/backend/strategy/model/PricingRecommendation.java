package com.chaitanya.backend.strategy.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.chaitanya.backend.enums.*;

@Data
@Builder
@AllArgsConstructor
public class PricingRecommendation {

    private BigDecimal recommendedPrice;

    private Direction direction;

    private double confidence;

    private String reasoning;
}