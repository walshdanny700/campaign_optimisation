package com.optily.campaign_optimisation.services;

import com.optily.campaign_optimisation.entity.Optimisation;
import com.optily.campaign_optimisation.entity.Recommendation;

import java.util.List;
import java.util.Optional;

public interface IOptimisationService {

    Optional<Optimisation> getLatestOptimisation(Long campaignGroupId);

    List<Recommendation> getLatestRecommendations(Long optimisationId);

    int applyRecommendations(List<Recommendation> recommendations, Optimisation optimisation);
}
