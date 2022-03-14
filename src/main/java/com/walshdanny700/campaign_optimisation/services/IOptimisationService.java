package com.walshdanny700.campaign_optimisation.services;

import com.walshdanny700.campaign_optimisation.entity.Optimisation;
import com.walshdanny700.campaign_optimisation.entity.Recommendation;

import java.util.List;
import java.util.Optional;

public interface IOptimisationService {

    Optional<Optimisation> getLatestOptimisation(Long campaignGroupId);

    List<Recommendation> getLatestRecommendations(Long optimisationId);

    int applyRecommendations(List<Recommendation> recommendations, Optimisation optimisation);

    Optional<Optimisation> getOptimisationById(Long optimisationId);
}
