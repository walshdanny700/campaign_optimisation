package com.walshdanny700.campaign_optimisation.services;

import com.walshdanny700.campaign_optimisation.entity.Campaign;

import java.util.List;

public interface ICampaignService {
    List<Campaign> getAllCampaignsForCampaignGroupId(Long campaignGroupId);
}
