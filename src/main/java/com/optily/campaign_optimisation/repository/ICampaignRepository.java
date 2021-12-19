package com.optily.campaign_optimisation.repository;

import com.optily.campaign_optimisation.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICampaignRepository  extends JpaRepository<Campaign, Long> {

    List<Campaign> findByCampaignGroupId(Long campaignGroupId);
}
