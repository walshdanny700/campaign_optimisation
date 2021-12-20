package com.optily.campaign_optimisation.repository;


import com.optily.campaign_optimisation.entity.Optimisation;
import com.optily.campaign_optimisation.entity.OptimisationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IOptimisationRepository extends JpaRepository<Optimisation, Long> {

    List<Optimisation> findByCampaignGroupIdOrderByIdDesc(Long campaignGroupId);
}
