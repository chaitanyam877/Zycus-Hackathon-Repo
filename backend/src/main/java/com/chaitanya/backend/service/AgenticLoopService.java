package com.chaitanya.backend.service;

import com.chaitanya.backend.entity.*;
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
        
        @Transactional
        public void processInventoryChange(
                        String productId) {

                Product product = productService.getProduct(productId);

                TriggerReason triggerReason = determineTriggerReason(product);

                if (triggerReason == null) {
                        return;
                }

                /*
                 * Prevent duplicate pending suggestions.
                 */
                boolean pricingAlreadyPending = pricingSuggestionRepository
                                .existsByProductIdAndTriggerReasonAndStatus(
                                                productId,
                                                triggerReason,
                                                SuggestionStatus.PENDING);

                boolean reorderAlreadyPending = reorderSuggestionRepository
                                .existsByProductIdAndTriggerReasonAndStatus(
                                                productId,
                                                triggerReason,
                                                SuggestionStatus.PENDING);

                /*
                 * Calculate category average velocity.
                 */
                double categoryAverageVelocity = productService.getCategoryAverageVelocity(
                                product.getCategory());
                System.out.println(
                                "🔥 CATEGORY AVERAGE = "
                                                + categoryAverageVelocity);
                System.out.println(
                                "🔥 CALLING COMMERCE ADVISOR");
                CommerceRecommendation recommendation = commerceAdvisorService.recommend(
                                product,
                                triggerReason,
                                categoryAverageVelocity);
                System.out.println(
                                "🔥 RECOMMENDATION = "
                                                + recommendation);
                /*
                 * Save pricing suggestion
                 */
                if (!pricingAlreadyPending) {

                        PricingRecommendation pricing = recommendation.getPricing();

                        PricingSuggestion suggestion = PricingSuggestion.builder()
                                        .product(product)
                                        .currentPrice(
                                                        product.getCurrentPrice())
                                        .recommendedPrice(
                                                        pricing.getRecommendedPrice())
                                        .direction(
                                                        pricing.getDirection())
                                        .confidence(
                                                        pricing.getConfidence())
                                        .reasoning(
                                                        pricing.getReasoning())
                                        .status(
                                                        SuggestionStatus.PENDING)
                                        .triggerReason(
                                                        triggerReason)
                                        .build();
                        System.out.println(
                                        "🔥 SAVING PRICING SUGGESTION");
                        PricingSuggestion savedPricing = pricingSuggestionRepository.save(suggestion);

                        System.out.println(
                                        "🔥 PRICING SAVED WITH ID = "
                                                        + savedPricing.getId());
                        pricingSuggestionRepository.flush();

                        System.out.println(
                                        "🔥 PRICING COUNT AFTER FLUSH = "
                                                        + pricingSuggestionRepository.count());
                        /*
                         * Product has a pricing review pending.
                         */
                        product.setStatus(
                                        com.chaitanya.backend.enums.ProductStatus.PRICE_REVIEW_PENDING);
                }

                /*
                 * Save reorder suggestion
                 */
                if (!reorderAlreadyPending) {

                        ReorderRecommendation reorder = recommendation.getReorder();

                        ReorderSuggestion suggestion = ReorderSuggestion.builder()
                                        .product(product)
                                        .currentStock(
                                                        product.getStockLevel())
                                        .recommendedQuantity(
                                                        reorder.getRecommendedQuantity())
                                        .suggestedLeadTimeDays(
                                                        reorder.getSuggestedLeadTimeDays())
                                        .confidence(
                                                        reorder.getConfidence())
                                        .reasoning(
                                                        reorder.getReasoning())
                                        .status(
                                                        SuggestionStatus.PENDING)
                                        .triggerReason(
                                                        triggerReason)
                                        .build();
                        System.out.println(
                                        "🔥 SAVING REORDER SUGGESTION");
                        ReorderSuggestion savedReorder = reorderSuggestionRepository.save(suggestion);

                        System.out.println(
                                        "🔥 REORDER SAVED WITH ID = "
                                                        + savedReorder.getId());
                }
        }

        private TriggerReason determineTriggerReason(
                        Product product) {

                /*
                 * Inventory-low gets priority.
                 */
                if (product.getStockLevel() < product.getReorderThreshold()) {

                        return TriggerReason.INVENTORY_LOW;
                }

                /*
                 * Demand spike.
                 *
                 * We will use the category average velocity.
                 * For now this will be completed after
                 * ProductService gets the average method.
                 */
                double categoryAverage = productService.getCategoryAverageVelocity(
                                product.getCategory());

                if (categoryAverage > 0
                                && product.getDemandVelocity() > 3 * categoryAverage) {

                        return TriggerReason.DEMAND_SPIKE;
                }

                return null;
        }
}