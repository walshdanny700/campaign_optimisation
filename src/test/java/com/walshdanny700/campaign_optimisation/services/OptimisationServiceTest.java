package com.walshdanny700.campaign_optimisation.services;

import com.walshdanny700.campaign_optimisation.entity.Campaign;
import com.walshdanny700.campaign_optimisation.entity.CampaignGroup;
import com.walshdanny700.campaign_optimisation.entity.Optimisation;
import com.walshdanny700.campaign_optimisation.entity.OptimisationStatus;
import com.walshdanny700.campaign_optimisation.entity.Recommendation;
import com.walshdanny700.campaign_optimisation.repository.ICampaignRepository;
import com.walshdanny700.campaign_optimisation.repository.IOptimisationRepository;
import com.walshdanny700.campaign_optimisation.repository.IRecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@WebMvcTest(OptimisationService.class)
class OptimisationServiceTest {

    @Autowired
    private OptimisationService optimisationService;

    private Optimisation optimisation;
    private Recommendation recommendationOne;
    private Campaign campaignOne;
    private Campaign campaignTwo;

    @MockBean
    private IOptimisationRepository optimisationRepository;

    @MockBean
    private IRecommendationRepository recommendationRepository;

    @MockBean
    private ICampaignRepository campaignRepository;

    @BeforeEach
    public void setup(){

        CampaignGroup campaignGroup = CampaignGroup.builder()
                .id(1L)
                .name("Campaign Group One").build();

        this.campaignOne = Campaign.builder()
                .id(1L)
                .campaignGroupId(campaignGroup.getId())
                .budget(BigDecimal.TEN)
                .impressions(10L)
                .name("Fist Campaign")
                .revenue(BigDecimal.TEN)
                .build();

        this.campaignTwo = Campaign.builder()
                .id(2L)
                .campaignGroupId(campaignGroup.getId())
                .budget(BigDecimal.TEN)
                .impressions(40L)
                .name("Second Campaign")
                .revenue(BigDecimal.TEN)
                .build();

        this.optimisation = Optimisation.builder()
                .id(1L)
                .campaignGroupId(campaignGroup.getId())
                .status(OptimisationStatus.NOT_APPLIED)
                .build();

        this.recommendationOne = Recommendation.builder()
                .campaignId(this.campaignOne.getId())
                .optimisationId(this.optimisation.getId())
                .recommendedBudget(BigDecimal.valueOf(4D)).build();

        Recommendation recommendationTwo = Recommendation.builder()
                .campaignId(this.campaignTwo.getId())
                .optimisationId(this.optimisation.getId())
                .recommendedBudget(BigDecimal.valueOf(16D)).build();
    }


    @Test
    void givenGetLatestOptimisation_WhenValidCampaignGroupId_thenReturnValid(){

        when(optimisationRepository.findByCampaignGroupIdOrderByIdDesc(anyLong())).thenReturn(Collections.singletonList(this.optimisation));

        Optional<Optimisation>  optimisation = optimisationService.getLatestOptimisation(1L);

        verify(optimisationRepository).findByCampaignGroupIdOrderByIdDesc(anyLong());
        assertTrue(optimisation.isPresent());
        assertEquals(this.optimisation, optimisation.get());
    }


    @Test
    void givenGetOptimisationById_WhenValidOptimisationId_thenReturnValid(){
        when(optimisationRepository.findById(anyLong())).thenReturn(Optional.ofNullable(this.optimisation));

        Optional<Optimisation>  optimisation = optimisationService.getOptimisationById(1L);

        verify(optimisationRepository).findById(anyLong());
        assertTrue(optimisation.isPresent());
        assertEquals(this.optimisation, optimisation.get());
    }


    @Test
    void givenApplyRecommendations_WhenValidInput_ThenReturnRowsUpdated(){

        when( campaignRepository.updateCampaign(anyLong(), any())).thenReturn(1);
        when( recommendationRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        when( optimisationRepository.save(any())).thenReturn(this.optimisation);


        int  rowsUpdated = optimisationService.applyRecommendations(
                Collections.singletonList(this.recommendationOne),  this.optimisation
        );

        assertEquals( 1, rowsUpdated);

        verify(campaignRepository).updateCampaign(anyLong(), any());
        verify(recommendationRepository).saveAll(anyCollection());
        verify(optimisationRepository).save(any());
    }


    @Test
    void givenGenerateLatestRecommendations_WhenValidInput_ThenReturnRecommendationList(){

        List<Campaign> list = new ArrayList<>();
        list.add(this.campaignOne);
        list.add(this.campaignTwo);
        List<Recommendation>  result = optimisationService.generateLatestRecommendations(list, this.optimisation);

        assertEquals( 2, result.size());
        assertEquals(result.get(0).getRecommendedBudget(), BigDecimal.valueOf(4.0));
        assertEquals(result.get(1).getRecommendedBudget(), BigDecimal.valueOf(16.0));
    }


    @Test
    void givenGetLatestRecommendations_WhenInputNotFound_ThenReturnEmptyList(){

        when(optimisationRepository.findById(anyLong())).thenReturn(Optional.empty());
        List<Recommendation> emptyList =  optimisationService.getLatestRecommendations(1L);

        verify(optimisationRepository).findById(anyLong());
        assertEquals(0, emptyList.size());
    }

    @Test
    void givenGetLatestRecommendations_WhenStatusApplied_ThenReturnEmptyList(){

        this.optimisation.setStatus(OptimisationStatus.APPLIED);
        when(optimisationRepository.findById(anyLong())).thenReturn(Optional.of(this.optimisation));
        List<Recommendation> emptyList =  optimisationService.getLatestRecommendations(1L);

        verify(optimisationRepository).findById(anyLong());
        assertEquals( 0, emptyList.size());
    }


    @Test
    void givenGetLatestRecommendations_WhenCampaignNotFound_ThenReturnEmptyList(){


        when(optimisationRepository.findById(anyLong())).thenReturn(Optional.of(this.optimisation));

        when(campaignRepository.findByCampaignGroupId(anyLong())).thenReturn(Collections.emptyList());


        List<Recommendation> emptyList =  optimisationService.getLatestRecommendations(1L);

        verify(optimisationRepository).findById(anyLong());
        verify(campaignRepository).findByCampaignGroupId(anyLong());
        assertEquals( 0, emptyList.size());
    }
}
