package com.optily.campaign_optimisation.services;

import com.optily.campaign_optimisation.entity.Campaign;
import com.optily.campaign_optimisation.entity.CampaignGroup;
import com.optily.campaign_optimisation.repository.ICampaignRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CampaignServiceTest {

    @InjectMocks
    private CampaignService campaignService;

    @Mock
    private ICampaignRepository campaignRepository;

    private CampaignGroup campaignGroup;
    private Campaign campaignOne;
    private Campaign campaignTwo;

    @BeforeEach
    public void setup(){
        this.campaignGroup = CampaignGroup.builder()
                .id(1L)
                .name("Campaign Group One").build();

        this.campaignOne = Campaign.builder()
                .id(1L)
                .campaignGroupId(this.campaignGroup.getId())
                .budget(BigDecimal.TEN)
                .impressions(10D)
                .name("Fist Campaign")
                .revenue(BigDecimal.TEN)
                .build();

        this.campaignTwo = Campaign.builder()
                .id(2L)
                .campaignGroupId(this.campaignGroup.getId())
                .budget(BigDecimal.TEN)
                .impressions(40D)
                .name("Second Campaign")
                .revenue(BigDecimal.TEN)
                .build();
    }

    @Test
    public void givenGetAllCampaignsForCampaignGroupId_WhenValidCampaignGroupId_thenReturnValid(){
        List<Campaign> list = new ArrayList<>();
        list.add(this.campaignOne);
        list.add(this.campaignTwo);


        when(campaignRepository.findByCampaignGroupId(anyLong())).thenReturn(list);

        List<Campaign>  result = campaignService.getAllCampaignsForCampaignGroupId(this.campaignGroup.getId());

        assertEquals(result.size(), 2);
    }


}