package com.chaitanya.backend.service;

import com.chaitanya.backend.dto.PricingSuggestionResponse;
import com.chaitanya.backend.dto.ReorderSuggestionResponse;
import com.chaitanya.backend.entity.PricingSuggestion;
import com.chaitanya.backend.entity.Product;
import com.chaitanya.backend.entity.ReorderSuggestion;
import com.chaitanya.backend.enums.SuggestionStatus;
import com.chaitanya.backend.repository.PricingSuggestionRepository;
import com.chaitanya.backend.repository.ReorderSuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SuggestionService {

        private final PricingSuggestionRepository pricingSuggestionRepository;

        private final ReorderSuggestionRepository reorderSuggestionRepository;

        private final ProductService productService;

        // =========================================================
        // PRICING SUGGESTIONS
        // =========================================================

        @Transactional(readOnly = true)
        public List<PricingSuggestionResponse> getPricingSuggestions(
                        SuggestionStatus status) {

                List<PricingSuggestion> suggestions;

                if (status != null) {
                        suggestions = pricingSuggestionRepository.findByStatus(status);
                } else {
                        suggestions = pricingSuggestionRepository.findAll();
                }

                return suggestions.stream()
                                .map(this::toPricingResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public PricingSuggestionResponse getPricingSuggestion(
                        String id) {

                PricingSuggestion suggestion = pricingSuggestionRepository
                                .findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Pricing suggestion not found: " + id));

                return toPricingResponse(suggestion);
        }

        @Transactional
        public PricingSuggestionResponse updatePricingSuggestion(
                        String id,
                        SuggestionStatus newStatus) {

                validateDecision(newStatus);

                PricingSuggestion suggestion = pricingSuggestionRepository
                                .findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Pricing suggestion not found: " + id));

                if (suggestion.getStatus() != SuggestionStatus.PENDING) {
                        throw new IllegalStateException(
                                        "Only PENDING suggestions can be updated");
                }

                if (newStatus == SuggestionStatus.ACCEPTED) {

                        Product product = suggestion.getProduct();

                        product.setCurrentPrice(
                                        suggestion.getRecommendedPrice());

                        productService.updateProductAfterSuggestion(product);
                }

                suggestion.setStatus(newStatus);

                PricingSuggestion saved = pricingSuggestionRepository.save(suggestion);

                return toPricingResponse(saved);
        }

        // =========================================================
        // REORDER SUGGESTIONS
        // =========================================================

        @Transactional(readOnly = true)
        public List<ReorderSuggestionResponse> getReorderSuggestions(
                        SuggestionStatus status) {

                List<ReorderSuggestion> suggestions;

                if (status != null) {
                        suggestions = reorderSuggestionRepository.findByStatus(status);
                } else {
                        suggestions = reorderSuggestionRepository.findAll();
                }

                return suggestions.stream()
                                .map(this::toReorderResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public ReorderSuggestionResponse getReorderSuggestion(
                        String id) {

                ReorderSuggestion suggestion = reorderSuggestionRepository
                                .findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Reorder suggestion not found: " + id));

                return toReorderResponse(suggestion);
        }

        @Transactional
        public ReorderSuggestionResponse updateReorderSuggestion(
                        String id,
                        SuggestionStatus newStatus) {

                validateDecision(newStatus);

                ReorderSuggestion suggestion = reorderSuggestionRepository
                                .findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Reorder suggestion not found: " + id));

                if (suggestion.getStatus() != SuggestionStatus.PENDING) {
                        throw new IllegalStateException(
                                        "Only PENDING suggestions can be updated");
                }

                if (newStatus == SuggestionStatus.ACCEPTED) {

                        Product product = suggestion.getProduct();

                        int newStock = product.getStockLevel()
                                        + suggestion.getRecommendedQuantity();

                        product.setStockLevel(newStock);

                        productService.updateProductAfterSuggestion(product);
                }

                suggestion.setStatus(newStatus);

                ReorderSuggestion saved = reorderSuggestionRepository.save(suggestion);

                return toReorderResponse(saved);
        }

        // =========================================================
        // DTO MAPPERS
        // =========================================================

        private PricingSuggestionResponse toPricingResponse(
                        PricingSuggestion suggestion) {

                Product product = suggestion.getProduct();

                return PricingSuggestionResponse.builder()
                                .id(suggestion.getId())
                                .productId(product.getId())
                                .sku(product.getSku())
                                .productName(product.getName())
                                .currentPrice(suggestion.getCurrentPrice())
                                .recommendedPrice(suggestion.getRecommendedPrice())
                                .direction(suggestion.getDirection())
                                .confidence(suggestion.getConfidence())
                                .reasoning(suggestion.getReasoning())
                                .status(suggestion.getStatus())
                                .triggerReason(suggestion.getTriggerReason())
                                .createdAt(suggestion.getCreatedAt())
                                .build();
        }

        private ReorderSuggestionResponse toReorderResponse(
                        ReorderSuggestion suggestion) {

                Product product = suggestion.getProduct();

                return ReorderSuggestionResponse.builder()
                                .id(suggestion.getId())
                                .productId(product.getId())
                                .sku(product.getSku())
                                .productName(product.getName())
                                .currentStock(suggestion.getCurrentStock())
                                .recommendedQuantity(
                                                suggestion.getRecommendedQuantity())
                                .suggestedLeadTimeDays(
                                                suggestion.getSuggestedLeadTimeDays())
                                .confidence(suggestion.getConfidence())
                                .reasoning(suggestion.getReasoning())
                                .status(suggestion.getStatus())
                                .triggerReason(suggestion.getTriggerReason())
                                .createdAt(suggestion.getCreatedAt())
                                .build();
        }

        // =========================================================
        // VALIDATION
        // =========================================================

        private void validateDecision(
                        SuggestionStatus status) {

                if (status != SuggestionStatus.ACCEPTED
                                && status != SuggestionStatus.REJECTED) {

                        throw new IllegalArgumentException(
                                        "Status must be ACCEPTED or REJECTED");
                }
        }
}