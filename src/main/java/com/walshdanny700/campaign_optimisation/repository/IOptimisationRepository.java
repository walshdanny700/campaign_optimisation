package com.walshdanny700.campaign_optimisation.repository;


import com.walshdanny700.campaign_optimisation.entity.Optimisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOptimisationRepository extends JpaRepository<Optimisation, Long> {

    List<Optimisation> findByCampaignGroupIdOrderByIdDesc(Long campaignGroupId);
}
