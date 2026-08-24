package com.chaitanya.backend.repository;

import java.util.*;
import com.chaitanya.backend.entity.*;
import com.chaitanya.backend.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByStatus(ProductStatus status);

    List<Product> findByCategory(Category category);

    List<Product> findByStatusAndCategory(
            ProductStatus status,
            Category category);

    @Query("""
            SELECT AVG(p.demandVelocity)
            FROM Product p
            WHERE p.category = :category
            """)
    Double findAverageDemandVelocityByCategory(
            @Param("category") Category category);
}