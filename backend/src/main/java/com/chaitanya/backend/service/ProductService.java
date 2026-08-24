package com.chaitanya.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chaitanya.backend.repository.*;
import com.chaitanya.backend.entity.*;
import com.chaitanya.backend.enums.*;
import com.chaitanya.backend.event.InventoryChangedEvent;

import org.springframework.context.ApplicationEventPublisher;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Product createProduct(Product product) {

        if (product.getStockLevel() == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else {
            product.setStatus(ProductStatus.ACTIVE);
        }

        return productRepository.save(product);
    }

    public List<Product> getProducts(
            ProductStatus status,
            Category category) {

        if (status != null && category != null) {
            return productRepository.findByStatusAndCategory(
                    status,
                    category);
        }

        if (status != null) {
            return productRepository.findByStatus(status);
        }

        if (category != null) {
            return productRepository.findByCategory(category);
        }

        return productRepository.findAll();
    }

    public Product getProduct(String id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Product not found: " + id));
    }

    @Transactional
    public Product updateStock(
            String productId,
            int newStock) {

        if (newStock < 0) {
            throw new IllegalArgumentException(
                    "Stock cannot be negative");
        }

        Product product = getProduct(productId);

        product.setStockLevel(newStock);

        updateProductStatus(product);

        Product savedProduct = productRepository.save(product);

        eventPublisher.publishEvent(
                new InventoryChangedEvent(
                        savedProduct.getId()));

        return savedProduct;
    }

    @Transactional
    public Product simulateOrder(String productId) {

        Product product = getProduct(productId);

        if (product.getStockLevel() <= 0) {
            throw new IllegalStateException(
                    "Product is out of stock");
        }

        product.setStockLevel(
                product.getStockLevel() - 1);

        product.setDemandVelocity(
                product.getDemandVelocity() + 1);

        updateProductStatus(product);

        Product savedProduct = productRepository.save(product);

        eventPublisher.publishEvent(
                new InventoryChangedEvent(
                        savedProduct.getId()));

        return savedProduct;
    }

    private void updateProductStatus(Product product) {

        if (product.getStockLevel() == 0) {

            product.setStatus(
                    ProductStatus.OUT_OF_STOCK);

        } else if (product.getStockLevel() < product.getReorderThreshold()) {

            product.setStatus(
                    ProductStatus.PRICE_REVIEW_PENDING);

        } else {

            product.setStatus(
                    ProductStatus.ACTIVE);
        }
    }

    public double getCategoryAverageVelocity(
            Category category) {

        Double average = productRepository
                .findAverageDemandVelocityByCategory(
                        category);

        return average == null ? 0.0 : average;
    }

    @Transactional
    public Product updateProductAfterSuggestion(
            Product product) {

        updateProductStatus(product);

        return productRepository.save(product);
    }
}
