package com.chaitanya.backend.dto;

import com.chaitanya.backend.enums.SuggestionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SuggestionDecisionRequest {

    @NotNull
    private SuggestionStatus status;
}