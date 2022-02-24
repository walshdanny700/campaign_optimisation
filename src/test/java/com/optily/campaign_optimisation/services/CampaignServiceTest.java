package com.optily.campaign_optimisation.services;

import com.optily.campaign_optimisation.entity.Campaign;
import com.optily.campaign_optimisation.entity.CampaignGroup;
import com.optily.campaign_optimisation.repository.ICampaignRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebMvcTest(CampaignService.class)
class CampaignServiceTest {

    @Autowired
    private CampaignService campaignService;

    @MockBean
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
    }

    @Test
    void givenGetAllCampaignsForCampaignGroupId_WhenValidCampaignGroupId_thenReturnValid(){
        List<Campaign> list = new ArrayList<>();
        list.add(this.campaignOne);
        list.add(this.campaignTwo);


        when(campaignRepository.findByCampaignGroupId(anyLong())).thenReturn(list);

        List<Campaign>  result = campaignService.getAllCampaignsForCampaignGroupId(this.campaignGroup.getId());

        assertEquals(2, result.size());
    }


}
