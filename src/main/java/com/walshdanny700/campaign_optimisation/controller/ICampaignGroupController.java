package com.walshdanny700.campaign_optimisation.controller;

import com.walshdanny700.campaign_optimisation.entity.Campaign;
import com.walshdanny700.campaign_optimisation.entity.CampaignGroup;
import com.walshdanny700.campaign_optimisation.entity.Optimisation;
import com.walshdanny700.campaign_optimisation.entity.Recommendation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

public interface ICampaignGroupController {

    @GetMapping(value = "/campaigngroups/list", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<CampaignGroup>> getCampaignGroups();

    @GetMapping(value = "/campaigngroups/{campaignGroupId}/campaigns/list", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Campaign>> getCampaignsForGroup(@PathVariable Long campaignGroupId);

    @GetMapping(value = "/campaigngroups/{campaignGroupId}/optimisations/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Optimisation> getOptimisationForGroup(@PathVariable Long campaignGroupId);

    @GetMapping(value = "/optimisations/{optimisationId}/recommendations/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Recommendation>> getRecommendationsForOptimisation(@PathVariable Long optimisationId);

    @PostMapping(value = "/optimisations/{optimisationId}/recommendations/apply", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Map<String, String>> applyLatestRecommendation(@PathVariable Long optimisationId);
}
