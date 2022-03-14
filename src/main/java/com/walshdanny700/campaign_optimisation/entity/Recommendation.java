package com.walshdanny700.campaign_optimisation.entity;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="RECOMMENDATION")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    Long id;

    @JoinColumn(name = "CAMPAIGN_ID")
    Long campaignId;


    @JoinColumn(name = "OPTIMISATION_ID")
    Long optimisationId;

    @NonNull
    @Column(name="RECOMMENDED_BUDGET")
    BigDecimal recommendedBudget;
}
