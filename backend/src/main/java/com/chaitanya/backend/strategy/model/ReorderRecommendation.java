package com.chaitanya.backend.strategy.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReorderRecommendation {

    private int recommendedQuantity;

    private int suggestedLeadTimeDays;

    private double confidence;

    private String reasoning;
}