package com.walshdanny700.campaign_optimisation.entity;

import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="OPTIMISATION")
public class Optimisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    Long id;

    @JoinColumn(name = "CAMPAIGN_GROUP_ID")
    Long campaignGroupId;

    @NonNull
    @Column(name="STATUS")
    @Enumerated(EnumType.STRING)
    OptimisationStatus status;
}
