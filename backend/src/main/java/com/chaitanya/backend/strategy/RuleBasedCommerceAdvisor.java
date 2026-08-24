package com.chaitanya.backend.strategy;

import org.springframework.stereotype.Component;

import com.chaitanya.backend.entity.*;
import com.chaitanya.backend.enums.*;
import com.chaitanya.backend.strategy.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("rule")
public class RuleBasedCommerceAdvisor
        implements CommerceAdvisor {

    @Override
    public CommerceRecommendation recommend(
            Product product,
            TriggerReason triggerReason,
            double categoryAverageVelocity
    ) {

        PricingRecommendation pricing =
                calculatePricing(
                        product,
                        categoryAverageVelocity
                );

        ReorderRecommendation reorder =
                calculateReorder(product);

        return CommerceRecommendation.builder()
                .pricing(pricing)
                .reorder(reorder)
                .build();
    }

    private PricingRecommendation calculatePricing(
            Product product,
            double categoryAverageVelocity
    ) {

        BigDecimal currentPrice =
                product.getCurrentPrice();

        BigDecimal recommendedPrice;
        Direction direction;
        String reasoning;

        /*
         * Rule 1:
         * Stock is below reorder threshold
         */
        if (product.getStockLevel()
                < product.getReorderThreshold()) {

            recommendedPrice =
                    currentPrice.multiply(
                            BigDecimal.valueOf(1.10)
                    );

            direction = Direction.INCREASE;

            reasoning =
                    "Inventory is below the reorder threshold. "
                    + "A 10% price increase is recommended "
                    + "to protect remaining inventory.";

        /*
         * Rule 2:
         * Demand is more than 2x category average
         */
        } else if (
                categoryAverageVelocity > 0
                && product.getDemandVelocity()
                > 2 * categoryAverageVelocity
        ) {

            recommendedPrice =
                    currentPrice.multiply(
                            BigDecimal.valueOf(1.05)
                    );

            direction = Direction.INCREASE;

            reasoning =
                    "Demand velocity is more than twice "
                    + "the category average. "
                    + "A 5% price increase is recommended.";

        /*
         * Rule 3:
         * Nothing significant happening
         */
        } else {

            recommendedPrice = currentPrice;

            direction = Direction.HOLD;

            reasoning =
                    "Inventory and demand are within "
                    + "normal operating ranges. "
                    + "No price change is recommended.";
        }

        recommendedPrice =
                recommendedPrice.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        return PricingRecommendation.builder()
                .recommendedPrice(recommendedPrice)
                .direction(direction)
                .confidence(1.0)
                .reasoning(reasoning)
                .build();
    }

    private ReorderRecommendation calculateReorder(
            Product product
    ) {

        int quantity =
                (product.getReorderThreshold() * 3)
                        - product.getStockLevel();

        quantity = Math.max(quantity, 1);

        return ReorderRecommendation.builder()
                .recommendedQuantity(quantity)
                .suggestedLeadTimeDays(7)
                .confidence(1.0)
                .reasoning(
                        "Recommended quantity is calculated "
                        + "as three times the reorder threshold "
                        + "minus current stock."
                )
                .build();
    }
}