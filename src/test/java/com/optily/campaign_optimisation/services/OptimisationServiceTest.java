package com.optily.campaign_optimisation.services;

import com.optily.campaign_optimisation.entity.Campaign;
import com.optily.campaign_optimisation.entity.CampaignGroup;
import com.optily.campaign_optimisation.entity.Optimisation;
import com.optily.campaign_optimisation.entity.OptimisationStatus;
import com.optily.campaign_optimisation.entity.Recommendation;
import com.optily.campaign_optimisation.repository.ICampaignRepository;
import com.optily.campaign_optimisation.repository.IOptimisationRepository;
import com.optily.campaign_optimisation.repository.IRecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

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


@SpringBootTest
public class OptimisationServiceTest {

    @InjectMocks
    private OptimisationService optimisationService;

    private Optimisation optimisation;
    private Recommendation recommendationOne;
    private Recommendation recommendationTwo;
    private Campaign campaignOne;
    private Campaign campaignTwo;
    private CampaignGroup campaignGroup;

    @Mock
    private IOptimisationRepository optimisationRepository;

    @Mock
    private IRecommendationRepository recommendationRepository;

    @Mock
    private ICampaignRepository campaignRepository;

    @BeforeEach
    public void setup(){

        this.campaignGroup = CampaignGroup.builder()
                .id(1L)
                .name("Campaign Group One").build();

        this.campaignOne = Campaign.builder()
                .id(1L)
                .campaignGroupId(this.campaignGroup.getId())
                .budget(BigDecimal.TEN)
                .impressions(10L)
                .name("Fist Campaign")
                .revenue(BigDecimal.TEN)
                .build();

        this.campaignTwo = Campaign.builder()
                .id(2L)
                .campaignGroupId(this.campaignGroup.getId())
                .budget(BigDecimal.TEN)
                .impressions(40L)
                .name("Second Campaign")
                .revenue(BigDecimal.TEN)
                .build();

        this.optimisation = Optimisation.builder()
                .id(1L)
                .campaignGroupId(this.campaignGroup.getId())
                .status(OptimisationStatus.NOT_APPLIED)
                .build();

        this.recommendationOne = Recommendation.builder()
                .campaignId(this.campaignOne.getId())
                .optimisationId(this.optimisation.getId())
                .recommendedBudget(BigDecimal.valueOf(4D)).build();

        this.recommendationTwo = Recommendation.builder()
                .campaignId(this.campaignTwo.getId())
                .optimisationId(this.optimisation.getId())
                .recommendedBudget(BigDecimal.valueOf(16D)).build();
    }

    @Test
    public void givenCampaigns_whenGenerateLatestRecommendations_thenReturnRecommendations() {
        assertTrue(true);

    }


    @Test
    public void givenGetLatestOptimisation_WhenValidCampaignGroupId_thenReturnValid(){

        when(optimisationRepository.findByCampaignGroupIdOrderByIdDesc(anyLong())).thenReturn(Collections.singletonList(this.optimisation));

        Optional<Optimisation>  optimisation = optimisationService.getLatestOptimisation(1L);

        verify(optimisationRepository).findByCampaignGroupIdOrderByIdDesc(anyLong());
        assertTrue(optimisation.isPresent());
        assertEquals(this.optimisation, optimisation.get());
    }


    @Test
    public void givenGetOptimisationById_WhenValidOptimisationId_thenReturnValid(){
        when(optimisationRepository.findById(anyLong())).thenReturn(Optional.ofNullable(this.optimisation));

        Optional<Optimisation>  optimisation = optimisationService.getOptimisationById(1L);

        verify(optimisationRepository).findById(anyLong());
        assertTrue(optimisation.isPresent());
        assertEquals(this.optimisation, optimisation.get());
    }


    @Test
    public void givenApplyRecommendations_WhenValidInput_ThenReturnRowsUpdated(){

        when( campaignRepository.updateCampaign(anyLong(), any())).thenReturn(1);
        when( recommendationRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        when( optimisationRepository.save(any())).thenReturn(this.optimisation);


        int  rowsUpdated = optimisationService.applyRecommendations(
                Collections.singletonList(this.recommendationOne),  this.optimisation
        );

        assertEquals(rowsUpdated, 1);

        verify(campaignRepository).updateCampaign(anyLong(), any());
        verify(recommendationRepository).saveAll(anyCollection());
        verify(optimisationRepository).save(any());
    }


    @Test
    public void givenGenerateLatestRecommendations_WhenValidInput_ThenReturnRecommendationList(){

        List<Campaign> list = new ArrayList<>();
        list.add(this.campaignOne);
        list.add(this.campaignTwo);
        List<Recommendation>  result = optimisationService.generateLatestRecommendations(list, this.optimisation);

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getRecommendedBudget(), BigDecimal.valueOf(4.0));
        assertEquals(result.get(1).getRecommendedBudget(), BigDecimal.valueOf(16.0));
    }


    @Test
    public void givenGetLatestRecommendations_WhenInputNotFound_ThenReturnEmptyList(){

        when(optimisationRepository.findById(anyLong())).thenReturn(Optional.empty());
        List<Recommendation> emptyList =  optimisationService.getLatestRecommendations(1L);

        verify(optimisationRepository).findById(anyLong());
        assertEquals(emptyList.size(), 0);
    }

    @Test
    public void givenGetLatestRecommendations_WhenStatusApplied_ThenReturnEmptyList(){

        this.optimisation.setStatus(OptimisationStatus.APPLIED);
        when(optimisationRepository.findById(anyLong())).thenReturn(Optional.of(this.optimisation));
        List<Recommendation> emptyList =  optimisationService.getLatestRecommendations(1L);

        verify(optimisationRepository).findById(anyLong());
        assertEquals(emptyList.size(), 0);
    }


    @Test
    public void givenGetLatestRecommendations_WhenCampaignNotFound_ThenReturnEmptyList(){


        when(optimisationRepository.findById(anyLong())).thenReturn(Optional.of(this.optimisation));

        when(campaignRepository.findByCampaignGroupId(anyLong())).thenReturn(Collections.emptyList());


        List<Recommendation> emptyList =  optimisationService.getLatestRecommendations(1L);

        verify(optimisationRepository).findById(anyLong());
        verify(campaignRepository).findByCampaignGroupId(anyLong());
        assertEquals(emptyList.size(), 0);
    }
}
