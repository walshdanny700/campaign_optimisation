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
@Table(name="CAMPAIGN")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    Long id;

    @NonNull
    @Column(name="NAME", length=200)
    String name;

    @JoinColumn(name = "CAMPAIGN_GROUP_ID", referencedColumnName = "ID")
    Long campaignGroupId;

    @NonNull
    @Column(name="BUDGET")
    BigDecimal budget;

    @NonNull
    @Column(name="IMPRESSIONS")
    Long impressions;

    @NonNull
    @Column(name="REVENUE")
    BigDecimal revenue;
}
