package com.optily.campaign_optimisation.controller;

import com.optily.campaign_optimisation.entity.Campaign;
import com.optily.campaign_optimisation.entity.CampaignGroup;
import com.optily.campaign_optimisation.entity.Optimisation;
import com.optily.campaign_optimisation.entity.Recommendation;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICampaignGroupController {
    ResponseEntity<List<CampaignGroup>> getCampaignGroups();

    ResponseEntity<List<Campaign>> getCampaignsForGroup(Long campaignGroupId);

    ResponseEntity<Optimisation> getOptimisationForGroup(Long campaignGroupId);

    ResponseEntity<List<Recommendation>> getRecommendationsForOptimisation(Long optimisationId);
}
