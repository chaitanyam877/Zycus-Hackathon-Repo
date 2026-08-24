package com.chaitanya.backend.dto;

import com.chaitanya.backend.enums.Direction;
import com.chaitanya.backend.enums.SuggestionStatus;
import com.chaitanya.backend.enums.TriggerReason;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PricingSuggestionResponse {

    private String id;

    private String productId;

    private String sku;

    private String productName;

    private BigDecimal currentPrice;

    private BigDecimal recommendedPrice;

    private Direction direction;

    private double confidence;

    private String reasoning;

    private SuggestionStatus status;

    private TriggerReason triggerReason;

    private LocalDateTime createdAt;
}
