package com.walshdanny700.campaign_optimisation.controller;

import com.walshdanny700.campaign_optimisation.entity.Campaign;
import com.walshdanny700.campaign_optimisation.entity.CampaignGroup;
import com.walshdanny700.campaign_optimisation.entity.Optimisation;
import com.walshdanny700.campaign_optimisation.entity.OptimisationStatus;
import com.walshdanny700.campaign_optimisation.entity.Recommendation;
import com.walshdanny700.campaign_optimisation.services.ICampaignGroupService;
import com.walshdanny700.campaign_optimisation.services.ICampaignService;
import com.walshdanny700.campaign_optimisation.services.IOptimisationService;
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

    private final ICampaignGroupService campaignGroupService;
    private final ICampaignService campaignService;
    private final IOptimisationService optimisationService;

    public static final String HEADER_NAME = "Content-Type";
    public static final String HEADER_VALUE = "application/json; charset=utf-8";
    public static final String MESSAGE = "message";

    @Autowired
    public CampaignGroupController(ICampaignGroupService campaignGroupService,
                                   ICampaignService campaignService,
                                   IOptimisationService optimisationService){
        this.campaignGroupService = campaignGroupService;
        this.campaignService = campaignService;
        this.optimisationService = optimisationService;
    }

    @Override
    public ResponseEntity<List<CampaignGroup>> getCampaignGroups(){
        List<CampaignGroup> campaignGroups = this.campaignGroupService.getAllCampaignGroups();
        var headers = new HttpHeaders();
        headers.add(HEADER_NAME, HEADER_VALUE);
        return CollectionUtils.isEmpty(campaignGroups) ? ResponseEntity.notFound().headers(headers).build()
                : ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(headers).body(campaignGroups);
    }

    @Override
    public ResponseEntity<List<Campaign>> getCampaignsForGroup(Long campaignGroupId) {
        List<Campaign> campaigns = this.campaignService.getAllCampaignsForCampaignGroupId(campaignGroupId);
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

        Optional<Optimisation> optimisation = this.optimisationService.getOptimisationById(optimisationId);

        var headers = new HttpHeaders();
        headers.add(HEADER_NAME, HEADER_VALUE);

        Map<String, String> message = new HashMap<>();

        if(optimisation.isEmpty() ){
            message.put( MESSAGE, "Campaigns Updated 0, Optimisations Not found for given optimisationId");
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).headers(headers).body(message);
        }


        if(optimisation.get().getStatus().equals(OptimisationStatus.APPLIED)){
            message.put( MESSAGE, "Campaigns Updated 0, Optimisations Already Applied");
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(headers).body(message);
        }

        List<Recommendation> recommendations = this.optimisationService.getLatestRecommendations(optimisationId);
        var rowsUpdated = this.optimisationService.applyRecommendations(recommendations, optimisation.get());

        message.put( MESSAGE, "Campaigns Updated " + rowsUpdated);
        return  ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).headers(headers).body(message);
    }

}
