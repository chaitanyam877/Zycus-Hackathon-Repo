package com.chaitanya.backend.dto;

import com.chaitanya.backend.enums.SuggestionStatus;
import com.chaitanya.backend.enums.TriggerReason;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReorderSuggestionResponse {

    private String id;

    private String productId;

    private String sku;

    private String productName;

    private int currentStock;

    private int recommendedQuantity;

    private int suggestedLeadTimeDays;

    private double confidence;

    private String reasoning;

    private SuggestionStatus status;

    private TriggerReason triggerReason;

    private LocalDateTime createdAt;
}