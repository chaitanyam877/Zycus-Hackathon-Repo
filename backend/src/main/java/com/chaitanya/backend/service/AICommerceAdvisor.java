package com.chaitanya.backend.service;

import com.chaitanya.backend.entity.Product;
import com.chaitanya.backend.enums.Direction;
import com.chaitanya.backend.enums.TriggerReason;
import com.chaitanya.backend.strategy.model.CommerceRecommendation;
import com.chaitanya.backend.strategy.model.PricingRecommendation;
import com.chaitanya.backend.strategy.model.ReorderRecommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AICommerceAdvisor {

    private final LLMClient llmClient;
    private final ObjectMapper objectMapper;

    public CommerceRecommendation recommend(
            Product product,
            TriggerReason triggerReason,
            double categoryAverageVelocity) {

        String prompt = buildPrompt(
                product,
                triggerReason,
                categoryAverageVelocity);

        String response = llmClient.chat(prompt);

        return parseRecommendation(response);
    }

    private String buildPrompt(
            Product product,
            TriggerReason triggerReason,
            double categoryAverageVelocity) {

        return """
                You are an inventory and pricing optimization assistant.

                Analyze the product information below and provide:
                1. A pricing recommendation.
                2. A reorder recommendation.

                BUSINESS RULES:

                - Recommended price must be greater than 0.
                - Recommended reorder quantity must not be negative.
                - Confidence must be between 0 and 1.
                - Lead time must not be negative.
                - Keep the recommended price within 20 percent
                  of the current price.
                - Consider current stock versus reorder threshold.
                - Consider product demand velocity.
                - Compare product demand velocity with category average velocity.
                - If inventory is critically low, prioritize inventory protection.
                - Return ONLY valid JSON.
                - Do NOT return markdown.
                - Do NOT put code fences around the JSON.
                - Do NOT add any explanation outside the JSON.

                PRODUCT:

                Product ID: %s
                SKU: %s
                Name: %s
                Category: %s

                Current Price: %s
                Current Stock: %s
                Reorder Threshold: %s
                Demand Velocity: %s
                Category Average Velocity: %s

                Trigger Reason: %s

                Return EXACTLY this structure:

                {
                  "pricing": {
                    "recommendedPrice": 0.00,
                    "direction": "INCREASE",
                    "confidence": 0.0,
                    "reasoning": "short explanation"
                  },
                  "reorder": {
                    "recommendedQuantity": 0,
                    "suggestedLeadTimeDays": 0,
                    "confidence": 0.0,
                    "reasoning": "short explanation"
                  }
                }
                """.formatted(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getCategory(),
                product.getCurrentPrice(),
                product.getStockLevel(),
                product.getReorderThreshold(),
                product.getDemandVelocity(),
                categoryAverageVelocity,
                triggerReason);
    }

    private CommerceRecommendation parseRecommendation(
            String response) {

        try {

            String json = cleanJson(response);

            JsonNode root = objectMapper.readTree(json);

            JsonNode pricingNode = root.path("pricing");

            JsonNode reorderNode = root.path("reorder");

            if (pricingNode.isMissingNode()
                    || reorderNode.isMissingNode()) {

                throw new IllegalArgumentException(
                        "AI response is missing pricing or reorder");
            }

            // =========================
            // PRICING
            // =========================

            BigDecimal recommendedPrice = BigDecimal.valueOf(
                    pricingNode
                            .path("recommendedPrice")
                            .asDouble());

            Direction direction = Direction.valueOf(
                    pricingNode
                            .path("direction")
                            .asText()
                            .toUpperCase());

            double pricingConfidence = pricingNode
                    .path("confidence")
                    .asDouble();

            String pricingReasoning = pricingNode
                    .path("reasoning")
                    .asText();

            PricingRecommendation pricing = new PricingRecommendation(
                    recommendedPrice,
                    direction,
                    pricingConfidence,
                    pricingReasoning);

            // =========================
            // REORDER
            // =========================

            int recommendedQuantity = reorderNode
                    .path("recommendedQuantity")
                    .asInt();

            int suggestedLeadTimeDays = reorderNode
                    .path("suggestedLeadTimeDays")
                    .asInt();

            double reorderConfidence = reorderNode
                    .path("confidence")
                    .asDouble();

            String reorderReasoning = reorderNode
                    .path("reasoning")
                    .asText();

            ReorderRecommendation reorder = new ReorderRecommendation(
                    recommendedQuantity,
                    suggestedLeadTimeDays,
                    reorderConfidence,
                    reorderReasoning);

            return new CommerceRecommendation(
                    pricing,
                    reorder);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse AI recommendation: "
                            + response,
                    e);
        }
    }

    private String cleanJson(String response) {

        String json = response.trim();

        if (json.startsWith("```json")) {
            json = json.substring(7).trim();
        } else if (json.startsWith("```")) {
            json = json.substring(3).trim();
        }

        if (json.endsWith("```")) {
            json = json.substring(
                    0,
                    json.length() - 3).trim();
        }

        return json;
    }
}