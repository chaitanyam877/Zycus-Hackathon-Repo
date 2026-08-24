package com.chaitanya.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.chaitanya.backend.entity.Product;
import com.chaitanya.backend.enums.TriggerReason;
import com.chaitanya.backend.service.CommerceAdvisorService;
import com.chaitanya.backend.service.ProductService;
import com.chaitanya.backend.strategy.model.CommerceRecommendation;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class CommerceAdvisorController {

    private final ProductService productService;

    private final CommerceAdvisorService advisorService;

    @PostMapping("/{id}/recommend")
    public CommerceRecommendation recommend(
            @PathVariable String id,

            @RequestParam
            TriggerReason triggerReason
    ) {

        Product product =
                productService.getProduct(id);

        // Temporary for now.
        // We'll calculate this properly later.
        double categoryAverage = 5.0;

        return advisorService.recommend(
                product,
                triggerReason,
                categoryAverage
        );
    }
}