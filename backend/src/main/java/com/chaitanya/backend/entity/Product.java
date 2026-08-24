package com.chaitanya.backend.entity;

import java.math.BigDecimal;

import com.chaitanya.backend.enums.ProductStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.*;
import lombok.*;
import com.chaitanya.backend.enums.Category;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")
public class Product {
    
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal currentPrice;

    @Column(nullable = false)
    private int stockLevel;

    @Column(nullable = false)
    private int reorderThreshold;

    @Column(nullable = false)
    private int demandVelocity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    // Sprint 2 extension points

    @Column(precision = 12, scale = 2)
    private BigDecimal costPrice;

    private String supplierId;
}
