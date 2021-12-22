package com.optily.campaign_optimisation.services;

import com.optily.campaign_optimisation.entity.Campaign;
import com.optily.campaign_optimisation.entity.Optimisation;
import com.optily.campaign_optimisation.entity.OptimisationStatus;
import com.optily.campaign_optimisation.entity.Recommendation;
import com.optily.campaign_optimisation.repository.ICampaignRepository;
import com.optily.campaign_optimisation.repository.IOptimisationRepository;
import com.optily.campaign_optimisation.repository.IRecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OptimisationService implements IOptimisationService {


    private final IOptimisationRepository optimisationRepository;
    private final IRecommendationRepository recommendationRepository;
    private final ICampaignRepository campaignRepository;

    @Autowired
    public OptimisationService(IOptimisationRepository optimisationRepository,
                               IRecommendationRepository recommendationRepository,
                               ICampaignRepository campaignRepository){
        this.optimisationRepository = optimisationRepository;
        this.recommendationRepository = recommendationRepository;
        this.campaignRepository = campaignRepository;
    }


    @Override
    public Optional<Optimisation> getLatestOptimisation(Long campaignGroupId){
        List<Optimisation> optimisations = this.optimisationRepository.findByCampaignGroupIdOrderByIdDesc(campaignGroupId);
        return Optional.of(optimisations.get(0));
    }

    public List<Recommendation> getLatestRecommendations(Long optimisationId){
        Optional<Optimisation> optimisation  = this.optimisationRepository.findById(optimisationId);
        if(optimisation.isEmpty() || optimisation.get().getStatus().equals(OptimisationStatus.APPLIED)){
            return Collections.emptyList();
        }

        List<Campaign> campaigns = this.campaignRepository.findByCampaignGroupId(optimisation.get().getCampaignGroupId());
        if(campaigns.isEmpty()){
           return Collections.emptyList();
        }

        return this.generateLatestRecommendations(campaigns, optimisation.get());
    }

    public List<Recommendation> generateLatestRecommendations(List<Campaign> campaign, Optimisation optimisation) {

        final Double sumImpressions = campaign.stream().mapToDouble(Campaign::getImpressions).sum();
        final BigDecimal sumTotalBudget = campaign.stream().map(Campaign::getBudget).reduce(BigDecimal.ZERO,  BigDecimal::add);


        return campaign
                .stream()
                .map(c -> Recommendation.builder()
                        .campaignId(c.getId())
                        .optimisationId(optimisation.getId())
                        .recommendedBudget(sumTotalBudget.multiply(BigDecimal.valueOf(c.getImpressions() / sumImpressions)))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public int applyRecommendations(List<Recommendation> recommendations, Optimisation optimisation){

        var rowsUpdated = 0;
        for(Recommendation recommendation : recommendations){
            rowsUpdated = rowsUpdated + this.campaignRepository.updateCampaign(recommendation.getCampaignId(), recommendation.getRecommendedBudget());

        }

        this.recommendationRepository.saveAll(recommendations);

        optimisation.setStatus(OptimisationStatus.APPLIED);
        this.optimisationRepository.save(optimisation);
        return rowsUpdated;
    }
}
