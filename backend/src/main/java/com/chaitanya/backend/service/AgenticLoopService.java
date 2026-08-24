package com.chaitanya.backend.service;

import com.chaitanya.backend.entity.Product;
import com.chaitanya.backend.entity.PricingSuggestion;
import com.chaitanya.backend.entity.ReorderSuggestion;
import com.chaitanya.backend.enums.ProductStatus;
import com.chaitanya.backend.enums.SuggestionStatus;
import com.chaitanya.backend.enums.TriggerReason;
import com.chaitanya.backend.repository.PricingSuggestionRepository;
import com.chaitanya.backend.repository.ReorderSuggestionRepository;
import com.chaitanya.backend.strategy.model.CommerceRecommendation;
import com.chaitanya.backend.strategy.model.PricingRecommendation;
import com.chaitanya.backend.strategy.model.ReorderRecommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgenticLoopService {

    private final ProductService productService;

    private final CommerceAdvisorService commerceAdvisorService;

    private final PricingSuggestionRepository pricingSuggestionRepository;

    private final ReorderSuggestionRepository reorderSuggestionRepository;

    private final AICommerceAdvisor aiCommerceAdvisor;


    // =========================================================
    // MAIN AGENTIC FLOW
    // =========================================================

    @Transactional
    public void processInventoryChange(String productId) {

        System.out.println(
                "🔥 AGENTIC LOOP STARTED FOR PRODUCT: "
                        + productId
        );

        // -----------------------------------------------------
        // 1. Get latest product state
        // -----------------------------------------------------

        Product product =
                productService.getProduct(productId);

        System.out.println(
                "🔥 PRODUCT = "
                        + product.getSku()
        );

        // -----------------------------------------------------
        // 2. Determine why the agent was triggered
        // -----------------------------------------------------

        TriggerReason triggerReason =
                determineTriggerReason(product);

        if (triggerReason == null) {

            System.out.println(
                    "ℹ️ NO TRIGGER DETECTED"
            );

            return;
        }

        System.out.println(
                "🔥 TRIGGER = "
                        + triggerReason
        );

        // -----------------------------------------------------
        // 3. Prevent duplicate pending suggestions
        // -----------------------------------------------------

        boolean pricingAlreadyPending =
                pricingSuggestionRepository
                        .existsByProductIdAndTriggerReasonAndStatus(
                                productId,
                                triggerReason,
                                SuggestionStatus.PENDING
                        );

        boolean reorderAlreadyPending =
                reorderSuggestionRepository
                        .existsByProductIdAndTriggerReasonAndStatus(
                                productId,
                                triggerReason,
                                SuggestionStatus.PENDING
                        );

        // -----------------------------------------------------
        // 4. Calculate category demand average
        // -----------------------------------------------------

        double categoryAverageVelocity =
                productService.getCategoryAverageVelocity(
                        product.getCategory()
                );

        System.out.println(
                "🔥 CATEGORY AVERAGE = "
                        + categoryAverageVelocity
        );

        // -----------------------------------------------------
        // 5. Get recommendation
        //
        // First try AI.
        // If AI fails, use existing rule-based advisor.
        // -----------------------------------------------------

        CommerceRecommendation recommendation;

        try {

            System.out.println(
                    "🤖 CALLING AI COMMERCE ADVISOR"
            );

            recommendation =
                    aiCommerceAdvisor.recommend(
                            product,
                            triggerReason,
                            categoryAverageVelocity
                    );

            System.out.println(
                    "🤖 AI RECOMMENDATION = "
                            + recommendation
            );

        } catch (Exception e) {

            System.out.println(
                    "⚠️ AI FAILED"
            );

            System.out.println(
                    "⚠️ REASON = "
                            + e.getMessage()
            );

            System.out.println(
                    "⚠️ FALLING BACK TO RULE-BASED ADVISOR"
            );

            recommendation =
                    commerceAdvisorService.recommend(
                            product,
                            triggerReason,
                            categoryAverageVelocity
                    );

            System.out.println(
                    "🔥 RULE-BASED RECOMMENDATION = "
                            + recommendation
            );
        }

        // -----------------------------------------------------
        // 6. Save pricing recommendation
        // -----------------------------------------------------

        if (!pricingAlreadyPending) {

            PricingRecommendation pricing =
                    recommendation.getPricing();

            PricingSuggestion suggestion =
                    PricingSuggestion.builder()
                            .product(product)
                            .currentPrice(
                                    product.getCurrentPrice()
                            )
                            .recommendedPrice(
                                    pricing.getRecommendedPrice()
                            )
                            .direction(
                                    pricing.getDirection()
                            )
                            .confidence(
                                    pricing.getConfidence()
                            )
                            .reasoning(
                                    pricing.getReasoning()
                            )
                            .status(
                                    SuggestionStatus.PENDING
                            )
                            .triggerReason(
                                    triggerReason
                            )
                            .build();

            PricingSuggestion savedPricing =
                    pricingSuggestionRepository.save(
                            suggestion
                    );

            System.out.println(
                    "🔥 PRICING SUGGESTION SAVED: "
                            + savedPricing.getId()
            );

            // Mark product as requiring price review
            product.setStatus(
                    ProductStatus.PRICE_REVIEW_PENDING
            );
        }
        else {

            System.out.println(
                    "ℹ️ PRICING SUGGESTION ALREADY PENDING"
            );
        }

        // -----------------------------------------------------
        // 7. Save reorder recommendation
        // -----------------------------------------------------

        if (!reorderAlreadyPending) {

            ReorderRecommendation reorder =
                    recommendation.getReorder();

            ReorderSuggestion suggestion =
                    ReorderSuggestion.builder()
                            .product(product)
                            .currentStock(
                                    product.getStockLevel()
                            )
                            .recommendedQuantity(
                                    reorder.getRecommendedQuantity()
                            )
                            .suggestedLeadTimeDays(
                                    reorder.getSuggestedLeadTimeDays()
                            )
                            .confidence(
                                    reorder.getConfidence()
                            )
                            .reasoning(
                                    reorder.getReasoning()
                            )
                            .status(
                                    SuggestionStatus.PENDING
                            )
                            .triggerReason(
                                    triggerReason
                            )
                            .build();

            ReorderSuggestion savedReorder =
                    reorderSuggestionRepository.save(
                            suggestion
                    );

            System.out.println(
                    "🔥 REORDER SUGGESTION SAVED: "
                            + savedReorder.getId()
            );
        }
        else {

            System.out.println(
                    "ℹ️ REORDER SUGGESTION ALREADY PENDING"
            );
        }

        // -----------------------------------------------------
        // 8. Save product status
        // -----------------------------------------------------

        productService.updateProductAfterSuggestion(
                product
        );

        System.out.println(
                "✅ AGENTIC LOOP COMPLETED"
        );
    }


    // =========================================================
    // TRIGGER DETECTION
    // =========================================================

    private TriggerReason determineTriggerReason(
            Product product
    ) {

        // -----------------------------------------------------
        // Priority 1: Inventory Low
        // -----------------------------------------------------

        if (
                product.getStockLevel()
                        < product.getReorderThreshold()
        ) {

            return TriggerReason.INVENTORY_LOW;
        }

        // -----------------------------------------------------
        // Priority 2: Demand Spike
        // -----------------------------------------------------

        double categoryAverage =
                productService.getCategoryAverageVelocity(
                        product.getCategory()
                );

        if (
                categoryAverage > 0
                        &&
                product.getDemandVelocity()
                                > 3 * categoryAverage
        ) {

            return TriggerReason.DEMAND_SPIKE;
        }

        return null;
    }
}