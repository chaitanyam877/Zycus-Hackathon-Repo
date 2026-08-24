package com.chaitanya.backend.strategy;
import com.chaitanya.backend.enums.*;
import com.chaitanya.backend.strategy.model.*;
import com.chaitanya.backend.entity.*;

public interface CommerceAdvisor {

    CommerceRecommendation recommend(
            Product product,
            TriggerReason triggerReason,
            double categoryAverageVelocity
    );
}