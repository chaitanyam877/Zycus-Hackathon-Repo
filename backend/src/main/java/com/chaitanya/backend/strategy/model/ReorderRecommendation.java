package com.chaitanya.backend.strategy.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReorderRecommendation {

    private int recommendedQuantity;

    private int suggestedLeadTimeDays;

    private double confidence;

    private String reasoning;
}