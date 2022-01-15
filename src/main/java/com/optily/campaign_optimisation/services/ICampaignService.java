package com.optily.campaign_optimisation.services;

import com.optily.campaign_optimisation.entity.Campaign;

import java.util.List;

public interface ICampaignService {
    List<Campaign> getAllCampaignsForCampaignGroupId(Long campaignGroupId);
}
