package com.chaitanya.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.chaitanya.backend.entity.*;
import com.chaitanya.backend.enums.*;

import java.util.List;

public interface PricingSuggestionRepository
        extends JpaRepository<PricingSuggestion, String> {

    List<PricingSuggestion> findByStatus(
            SuggestionStatus status
    );

    boolean existsByProductIdAndTriggerReasonAndStatus(
            String productId,
            TriggerReason triggerReason,
            SuggestionStatus status
    );
}
