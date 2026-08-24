package com.chaitanya.backend.controller;

import com.chaitanya.backend.dto.ReorderSuggestionResponse;
import com.chaitanya.backend.dto.SuggestionDecisionRequest;
import com.chaitanya.backend.enums.SuggestionStatus;
import com.chaitanya.backend.service.SuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reorder-suggestions")
@RequiredArgsConstructor
public class ReorderSuggestionController {

    private final SuggestionService suggestionService;

    // GET /reorder-suggestions
    // GET /reorder-suggestions?status=PENDING
    @GetMapping
    public List<ReorderSuggestionResponse> getSuggestions(
            @RequestParam(required = false)
            SuggestionStatus status
    ) {
        return suggestionService.getReorderSuggestions(status);
    }

    // GET /reorder-suggestions/{id}
    @GetMapping("/{id}")
    public ReorderSuggestionResponse getSuggestion(
            @PathVariable String id
    ) {
        return suggestionService.getReorderSuggestion(id);
    }

    // PATCH /reorder-suggestions/{id}
    @PatchMapping("/{id}")
    public ReorderSuggestionResponse updateSuggestion(
            @PathVariable String id,

            @Valid
            @RequestBody
            SuggestionDecisionRequest request
    ) {
        return suggestionService.updateReorderSuggestion(
                id,
                request.getStatus()
        );
    }
}