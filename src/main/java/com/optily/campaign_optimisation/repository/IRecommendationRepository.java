package com.optily.campaign_optimisation.repository;


import com.optily.campaign_optimisation.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRecommendationRepository  extends JpaRepository<Recommendation, Long> {

}
