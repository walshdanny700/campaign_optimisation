package com.walshdanny700.campaign_optimisation.services;

import com.walshdanny700.campaign_optimisation.entity.Campaign;
import com.walshdanny700.campaign_optimisation.repository.ICampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignService implements ICampaignService{


    private final ICampaignRepository campaignRepository;

    @Autowired
    public CampaignService(ICampaignRepository campaignRepository){
        this.campaignRepository = campaignRepository;

    }

    @Override
    public List<Campaign> getAllCampaignsForCampaignGroupId(Long campaignGroupId) {
        return this.campaignRepository.findByCampaignGroupId(campaignGroupId);
    }
}
