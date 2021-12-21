package com.optily.campaign_optimisation.controller;

import com.optily.campaign_optimisation.entity.Campaign;
import com.optily.campaign_optimisation.entity.CampaignGroup;
import com.optily.campaign_optimisation.entity.Optimisation;
import com.optily.campaign_optimisation.entity.OptimisationStatus;
import com.optily.campaign_optimisation.entity.Recommendation;
import com.optily.campaign_optimisation.repository.ICampaignGroupRepository;
import com.optily.campaign_optimisation.repository.ICampaignRepository;
import com.optily.campaign_optimisation.repository.IOptimisationRepository;
import com.optily.campaign_optimisation.repository.IRecommendationRepository;
import com.optily.campaign_optimisation.services.IOptimisationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CampaignGroupController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
public class CampaignGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private ICampaignGroupRepository campaignGroupRepository;

    @MockBean
    private ICampaignRepository campaignRepository;

    @MockBean
    private IOptimisationRepository optimisationRepository;

    @MockBean
    private IRecommendationRepository recommendationRepository;

    @MockBean
    private IOptimisationService optimisationService;


    private CampaignGroup campaignGroup;

    private Campaign campaign;

    private Optimisation optimisation;

    private Recommendation recommendation;

    @BeforeEach
    public void setup(){

        this.campaignGroup = CampaignGroup.builder()
                .id(1L)
                .name("Campaign Group One").build();

        this.campaign = Campaign.builder()
                .id(1L)
                .campaignGroupId(this.campaignGroup.getId())
                .budget(BigDecimal.ONE)
                .impressions(123D)
                .name("Fist Campaign")
                .revenue(BigDecimal.TEN)
                .build();

        this.optimisation = Optimisation.builder()
                .id(1L)
                .campaignGroupId(this.campaignGroup.getId())
                .status(OptimisationStatus.NOT_APPLIED)
                .build();

        this.recommendation = Recommendation.builder()
                .id(1L)
                .campaignId(this.campaign.getId())
                .optimisationId(this.optimisation.getId())
                .recommendedBudget(BigDecimal.ONE).build();
    }

    @Test
    void givenZeroCampaignGroups_WhenGetRequest_thenReturnNotFound() throws Exception{
        given(campaignGroupRepository.findAll()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/campaigngroups/list"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenCampaignGroups_whenGetCampaignGroups_thenReturnJsonArray() throws Exception{
        given(this.campaignGroupRepository.findAll()).willReturn(Collections.singletonList(this.campaignGroup));

        FieldDescriptor[] campaignGroup = new FieldDescriptor[] {
                fieldWithPath("id").description("Unique Identifier of Campaign Group"),
                fieldWithPath("name").description("Name of Campaign Group") };

        mockMvc.perform(get("/api/v1/campaigngroups/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].name", is(this.campaignGroup.getName())))
                .andExpect(jsonPath("$.[0].id", is(this.campaignGroup.getId()), Long.class))
                .andDo(document("campaign-groups/list", responseFields(
                        fieldWithPath("[]")
                                .description("An array of Campaign Groups"))
                                .andWithPrefix("[].", campaignGroup)));
    }

    @Test
    void givenCampaignGroupId_whenCampaignsForGroup_thenReturnJsonArray() throws Exception{
        given(this.campaignRepository.findByCampaignGroupId(any())).willReturn(Collections.singletonList(this.campaign));

        FieldDescriptor[] campaignFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("id").description("Unique Identifier for a Campaign"),
                fieldWithPath("name").description("Name of Campaign"),
                fieldWithPath("campaignGroupId").description("ID of Campaign Group that this campaign belongs to"),
                fieldWithPath("budget").description("The allocated budget for a campaign"),
                fieldWithPath("impressions").description("The impressions for a given campaign"),
                fieldWithPath("revenue").description("The revenue for a given campaign")
        };

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/campaigngroups/{campaignGroupId}/campaigns/list", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].name", is(this.campaign.getName())))
                .andExpect(jsonPath("$.[0].id", is(this.campaign.getId()), Long.class))
                .andExpect(jsonPath("$.[0].campaignGroupId", is(this.campaign.getCampaignGroupId()), Long.class))
                .andExpect(jsonPath("$.[0].budget", is(this.campaign.getBudget()), BigDecimal.class))
                .andExpect(jsonPath("$.[0].impressions", is(this.campaign.getImpressions())))
                .andExpect(jsonPath("$.[0].revenue", is(this.campaign.getRevenue()), BigDecimal.class))
                .andDo(document("campaigns/list",
                        responseFields(fieldWithPath("[]")
                                .description("An array of Campaigns"))
                                .andWithPrefix("[].", campaignFieldDescriptor),
                        pathParameters(
                                parameterWithName("campaignGroupId").description("ID of Campaign Group that this campaign belongs to")
                        )));
    }

    @Test
    void givenCampaignGroupId_whenZeroCampaignsForGroup_thenReturnNotFound() throws Exception{
        given(this.campaignRepository.findByCampaignGroupId(any())).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/campaigngroups/1/campaigns/list"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void givenCampaignGroupId_whenOptimisationsForGroup_thenReturnJsonArray() throws Exception{
        given(this.optimisationService.getLatestOptimisation(any())).willReturn(java.util.Optional.ofNullable(this.optimisation));

        FieldDescriptor[] optimisations = new FieldDescriptor[] {
                fieldWithPath("id").description("Unique Identifier for a Campaign"),
                fieldWithPath("campaignGroupId").description("ID of Campaign Group that this campaign belongs to"),
                fieldWithPath("status").description("The status of the optimisation. Either it is applied or not applied to the Campaign Group")
        };

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/campaigngroups/{campaignGroupId}/optimisations/latest", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(this.optimisation.getId()), Long.class))
                .andExpect(jsonPath("$.campaignGroupId", is(this.optimisation.getCampaignGroupId()), Long.class))
                .andExpect(jsonPath("$.status", is(this.optimisation.getStatus().name())))
                .andDo(document("optimisations",
                        responseFields(optimisations),
                        pathParameters(
                                parameterWithName("campaignGroupId").description("ID of Campaign Group that this campaign belongs to")
                        )));
    }


    @Test
    void givenCampaignGroupId_whenZeroOptimisationsForGroup_thenReturnNotFound() throws Exception{
        given(this.optimisationService.getLatestOptimisation(any())).willReturn(Optional.empty());

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/campaigngroups/{campaignGroupId}/optimisations/latest", 1))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    void givenOptimisationId_whenRecommendationsForOptimisation_thenReturnJsonArray() throws Exception{
        given(this.optimisationService.getLatestRecommendations(any())).willReturn(Collections.singletonList(this.recommendation));

        FieldDescriptor[] recommendationFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("id").description("Unique Identifier for a Recommendation"),
                fieldWithPath("campaignId").description("The Campaign Id that this Recommendation is linked to"),
                fieldWithPath("optimisationId").description("The Optimisation Id that this Recommendation is linked to"),
                fieldWithPath("recommendedBudget").description("The recommended budget of this optimisation")
        };

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/optimisations/{optimisationId}/recommendations/latest", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(this.recommendation.getId()), Long.class))
                .andExpect(jsonPath("$.[0].campaignId", is(this.recommendation.getCampaignId()), Long.class))
                .andExpect(jsonPath("$.[0].optimisationId", is(this.recommendation.getOptimisationId()), Long.class))
                .andExpect(jsonPath("$.[0].recommendedBudget", is(this.recommendation.getRecommendedBudget()), BigDecimal.class))
                .andDo(document("recommendations",
                        responseFields(fieldWithPath("[]")
                                .description("An array of Recommendations For a given optimisation"))
                                .andWithPrefix("[].", recommendationFieldDescriptor),
                        pathParameters(
                                parameterWithName("optimisationId").description("The Given Optimisation ID")
                        )));

    }

    @Test
    void givenOptimisationId_whenZeroRecommendationsForOptimisation_thenReturnNotFound() throws Exception{
        given(this.optimisationService.getLatestRecommendations(any())).willReturn(Collections.emptyList());

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/optimisations/{optimisationId}/recommendations/latest", 1))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }




}
