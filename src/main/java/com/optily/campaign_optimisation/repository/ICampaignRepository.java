package com.optily.campaign_optimisation.repository;

import com.optily.campaign_optimisation.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ICampaignRepository  extends JpaRepository<Campaign, Long> {

    List<Campaign> findByCampaignGroupId(Long campaignGroupId);

    @Modifying
    @Query("UPDATE Campaign c SET c.budget = :budget WHERE c.id = :campaignId")
    int updateCampaign(@Param("campaignId") Long campaignId, @Param("budget") BigDecimal budget);
}
