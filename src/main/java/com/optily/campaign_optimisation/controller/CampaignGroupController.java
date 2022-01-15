package com.optily.campaign_optimisation.controller;

import com.optily.campaign_optimisation.entity.Campaign;
import com.optily.campaign_optimisation.entity.CampaignGroup;
import com.optily.campaign_optimisation.entity.Optimisation;
import com.optily.campaign_optimisation.entity.OptimisationStatus;
import com.optily.campaign_optimisation.entity.Recommendation;
import com.optily.campaign_optimisation.repository.ICampaignGroupRepository;
import com.optily.campaign_optimisation.repository.ICampaignRepository;
import com.optily.campaign_optimisation.repository.IOptimisationRepository;
import com.optily.campaign_optimisation.services.IOptimisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
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
    private final IOptimisationService optimisationService;

    public static final String HEADER_NAME = "Content-Type";
    public static final String HEADER_VALUE = "application/json; charset=utf-8";

    @Autowired
    public CampaignGroupController(ICampaignGroupRepository campaignGroupRepository,
                                   ICampaignRepository campaignRepository,
                                   IOptimisationRepository optimisationRepository,
                                   IOptimisationService optimisationService){
        this.campaignGroupRepository = campaignGroupRepository;
        this.campaignRepository = campaignRepository;
        this.optimisationRepository = optimisationRepository;
        this.optimisationService = optimisationService;
    }

    @Override
    public ResponseEntity<List<CampaignGroup>> getCampaignGroups(){
        List<CampaignGroup> campaignGroups = this.campaignGroupRepository.findAll();
        var headers = new HttpHeaders();
        headers.add(HEADER_NAME, HEADER_VALUE);
        return CollectionUtils.isEmpty(campaignGroups) ? ResponseEntity.notFound().headers(headers).build()
                : ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(headers).body(campaignGroups);
    }

    @Override
    public ResponseEntity<List<Campaign>> getCampaignsForGroup(Long campaignGroupId) {
        List<Campaign> campaigns = this.campaignRepository.findByCampaignGroupId(campaignGroupId);
        var headers = new HttpHeaders();
        headers.add(HEADER_NAME, HEADER_VALUE);
        return  campaigns.isEmpty()  ? ResponseEntity.notFound().headers(headers).build()
                : ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(headers).body(campaigns);
    }

    @Override
    public ResponseEntity<Optimisation> getOptimisationForGroup(Long campaignGroupId) {
        Optional<Optimisation> optimisation = this.optimisationService.getLatestOptimisation(campaignGroupId);
        var headers = new HttpHeaders();
        headers.add(HEADER_NAME, HEADER_VALUE);
        return optimisation.isEmpty()  ? ResponseEntity.notFound().headers(headers).build()
                : ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(headers).body(optimisation.get());
    }

    @Override
    public ResponseEntity<List<Recommendation>> getRecommendationsForOptimisation(Long optimisationId) {
        List<Recommendation> recommendations = this.optimisationService.getLatestRecommendations(optimisationId);
        var headers = new HttpHeaders();
        headers.add(HEADER_NAME, HEADER_VALUE);
        return  recommendations.isEmpty()  ? ResponseEntity.notFound().headers(headers).build()
                : ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(headers).body(recommendations);
    }


    @Override
    @Transactional
    public ResponseEntity<Map<String, String>> applyLatestRecommendation(Long optimisationId){

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
