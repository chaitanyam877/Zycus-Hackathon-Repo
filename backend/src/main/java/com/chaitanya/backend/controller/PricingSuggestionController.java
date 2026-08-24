package com.chaitanya.backend.controller;

import com.chaitanya.backend.dto.PricingSuggestionResponse;
import com.chaitanya.backend.dto.SuggestionDecisionRequest;
import com.chaitanya.backend.enums.SuggestionStatus;
import com.chaitanya.backend.service.SuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricing-suggestions")
@RequiredArgsConstructor
public class PricingSuggestionController {

    private final SuggestionService suggestionService;


    // =========================================================
    // GET ALL PRICING SUGGESTIONS
    // =========================================================

    @GetMapping
    public List<PricingSuggestionResponse> getSuggestions(
            @RequestParam(required = false)
            SuggestionStatus status
    ) {

        return suggestionService.getPricingSuggestions(status);
    }


    // =========================================================
    // GET ONE PRICING SUGGESTION
    // =========================================================

    @GetMapping("/{id}")
    public PricingSuggestionResponse getSuggestion(
            @PathVariable String id
    ) {

        return suggestionService.getPricingSuggestion(id);
    }


    // =========================================================
    // ACCEPT / REJECT PRICING SUGGESTION
    // =========================================================

    @PatchMapping("/{id}")
    public PricingSuggestionResponse updateSuggestion(
            @PathVariable String id,

            @Valid
            @RequestBody
            SuggestionDecisionRequest request
    ) {

        return suggestionService.updatePricingSuggestion(
                id,
                request.getStatus()
        );
    }
}