package com.chaitanya.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.chaitanya.backend.entity.*;
import com.chaitanya.backend.service.*;
import com.chaitanya.backend.enums.*;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:4200"
})
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(
            @RequestBody Product product
    ) {

        return productService.createProduct(product);
    }

    @GetMapping
    public List<Product> getProducts(
            @RequestParam(required = false)
            ProductStatus status,

            @RequestParam(required = false)
            Category category
    ) {

        return productService.getProducts(
                status,
                category
        );
    }

    @GetMapping("/{id}")
    public Product getProduct(
            @PathVariable String id
    ) {

        return productService.getProduct(id);
    }

    @PatchMapping("/{id}/stock")
    public Product updateStock(
            @PathVariable String id,

            @RequestParam int stock
    ) {

        return productService.updateStock(
                id,
                stock
        );
    }

    @PostMapping("/{id}/orders")
    public Product simulateOrder(
            @PathVariable String id
    ) {

        return productService.simulateOrder(id);
    }
}