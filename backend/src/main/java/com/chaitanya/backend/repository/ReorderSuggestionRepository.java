package com.chaitanya.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.chaitanya.backend.entity.*;
import com.chaitanya.backend.enums.*;

import java.util.List;

public interface ReorderSuggestionRepository
        extends JpaRepository<ReorderSuggestion, String> {

    List<ReorderSuggestion> findByStatus(
            SuggestionStatus status
    );

    boolean existsByProductIdAndTriggerReasonAndStatus(
            String productId,
            TriggerReason triggerReason,
            SuggestionStatus status
    );
}