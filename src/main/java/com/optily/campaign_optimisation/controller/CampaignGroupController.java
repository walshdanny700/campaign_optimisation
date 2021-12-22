package com.optily.campaign_optimisation.controller;

import com.optily.campaign_optimisation.entity.Campaign;
import com.optily.campaign_optimisation.entity.CampaignGroup;
import com.optily.campaign_optimisation.entity.Optimisation;
import com.optily.campaign_optimisation.entity.OptimisationStatus;
import com.optily.campaign_optimisation.entity.Recommendation;
import com.optily.campaign_optimisation.repository.ICampaignGroupRepository;
import com.optily.campaign_optimisation.repository.ICampaignRepository;
import com.optily.campaign_optimisation.repository.IOptimisationRepository;
import com.optily.campaign_optimisation.repository.IRecommendationRepository;
import com.optily.campaign_optimisation.services.IOptimisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1")
public class CampaignGroupController implements ICampaignGroupController{

    private final ICampaignGroupRepository campaignGroupRepository;
    private final ICampaignRepository campaignRepository;
    private final IOptimisationRepository optimisationRepository;
    private final IRecommendationRepository recommendationRepository;
    private final IOptimisationService optimisationService;

    public static final String HEADER_NAME = "Content-Type";
    public static final String HEADER_VALUE = "application/json; charset=utf-8";

    @Autowired
    public CampaignGroupController(ICampaignGroupRepository campaignGroupRepository,
                                   ICampaignRepository campaignRepository,
                                   IOptimisationRepository optimisationRepository,
                                   IRecommendationRepository recommendationRepository,
                                   IOptimisationService optimisationService){
        this.campaignGroupRepository = campaignGroupRepository;
        this.campaignRepository = campaignRepository;
        this.optimisationRepository = optimisationRepository;
        this.recommendationRepository = recommendationRepository;
        this.optimisationService = optimisationService;
    }

    @Override
    @GetMapping(value = "/campaigngroups/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CampaignGroup>> getCampaignGroups(){
        List<CampaignGroup> campaignGroups = this.campaignGroupRepository.findAll();
        var headers = new HttpHeaders();
        headers.add(HEADER_NAME, HEADER_VALUE);
        return CollectionUtils.isEmpty(campaignGroups) ? ResponseEntity.notFound().headers(headers).build()
                : ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(headers).body(campaignGroups);
    }

    @Override
    @GetMapping(value = "/campaigngroups/{campaignGroupId}/campaigns/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Campaign>> getCampaignsForGroup(@PathVariable Long campaignGroupId) {
        List<Campaign> campaigns = this.campaignRepository.findByCampaignGroupId(campaignGroupId);
        var headers = new HttpHeaders();
        headers.add(HEADER_NAME, HEADER_VALUE);
        return  campaigns.isEmpty()  ? ResponseEntity.notFound().headers(headers).build()
                : ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(headers).body(campaigns);
    }

    @Override
    @GetMapping(value = "/campaigngroups/{campaignGroupId}/optimisations/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Optimisation> getOptimisationForGroup(@PathVariable Long campaignGroupId) {
        Optional<Optimisation> optimisation = this.optimisationService.getLatestOptimisation(campaignGroupId);
        var headers = new HttpHeaders();
        headers.add(HEADER_NAME, HEADER_VALUE);
        return optimisation.isEmpty()  ? ResponseEntity.notFound().headers(headers).build()
                : ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(headers).body(optimisation.get());
    }

    @Override
    @GetMapping(value = "/optimisations/{optimisationId}/recommendations/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Recommendation>> getRecommendationsForOptimisation(@PathVariable Long optimisationId) {
        List<Recommendation> recommendations = this.optimisationService.getLatestRecommendations(optimisationId);
        var headers = new HttpHeaders();
        headers.add(HEADER_NAME, HEADER_VALUE);
        return  recommendations.isEmpty()  ? ResponseEntity.notFound().headers(headers).build()
                : ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(headers).body(recommendations);
    }


    @Override
    @PostMapping(value = "/optimisations/{optimisationId}/recommendations/apply", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Map<String, String>> applyLatestRecommendation(@PathVariable Long optimisationId){

        Optional<Optimisation> optimisation = this.optimisationRepository.findById(optimisationId);

        var headers = new HttpHeaders();
        headers.add(HEADER_NAME, HEADER_VALUE);

        if(optimisation.isEmpty() ){
            return ResponseEntity.notFound().headers(headers).build();
        }

        if(optimisation.get().getStatus().equals(OptimisationStatus.APPLIED)){
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(headers).build();
        }

        List<Recommendation> recommendations = this.optimisationService.getLatestRecommendations(optimisationId);
        var rowsUpdated = this.optimisationService.applyRecommendations(recommendations, optimisation.get());

        Map<String, String> message = new HashMap<>();
        message.put("message", "Campaigns Updated " + rowsUpdated);
        return  ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(headers).body(message);
    }

}
