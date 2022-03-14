package com.walshdanny700.campaign_optimisation.services;

import com.walshdanny700.campaign_optimisation.entity.CampaignGroup;
import com.walshdanny700.campaign_optimisation.repository.ICampaignGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampaignGroupService implements ICampaignGroupService{

    private final ICampaignGroupRepository campaignGroupRepository;

    @Autowired
    public CampaignGroupService(ICampaignGroupRepository campaignGroupRepository){
        this.campaignGroupRepository = campaignGroupRepository;

    }

    @Override
    public List<CampaignGroup> getAllCampaignGroups() {
        return this.campaignGroupRepository.findAll();
    }
}
