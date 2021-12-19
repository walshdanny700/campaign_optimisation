package com.optily.campaign_optimisation.repository;

import com.optily.campaign_optimisation.entity.CampaignGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICampaignGroupRepository  extends JpaRepository<CampaignGroup, Long> {
}
