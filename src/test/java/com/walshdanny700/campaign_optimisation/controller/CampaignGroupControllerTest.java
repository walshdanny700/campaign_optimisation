package com.walshdanny700.campaign_optimisation.controller;

import com.walshdanny700.campaign_optimisation.entity.Campaign;
import com.walshdanny700.campaign_optimisation.entity.CampaignGroup;
import com.walshdanny700.campaign_optimisation.entity.Optimisation;
import com.walshdanny700.campaign_optimisation.entity.OptimisationStatus;
import com.walshdanny700.campaign_optimisation.entity.Recommendation;
import com.walshdanny700.campaign_optimisation.services.ICampaignGroupService;
import com.walshdanny700.campaign_optimisation.services.ICampaignService;
import com.walshdanny700.campaign_optimisation.services.IOptimisationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;


@WebFluxTest(CampaignGroupController.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class CampaignGroupControllerTest {


    @Autowired
    private ApplicationContext context;
    private WebTestClient webTestClient;


    @MockBean
    private IOptimisationService optimisationService;

    @MockBean
    private ICampaignService campaignService;

    @MockBean
    private ICampaignGroupService campaignGroupService;

    private CampaignGroup campaignGroup;

    private Campaign campaign;

    private Optimisation optimisation;

    private Recommendation recommendation;

    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentation){

        this.webTestClient = WebTestClient.bindToApplicationContext(this.context)
                .configureClient()
                .baseUrl("https://localhost:8443")
                .filter(documentationConfiguration(restDocumentation))
                .build();

        this.campaignGroup = CampaignGroup.builder()
                .id(1L)
                .name("Campaign Group One").build();

        this.campaign = Campaign.builder()
                .id(1L)
                .campaignGroupId(this.campaignGroup.getId())
                .budget(BigDecimal.ONE)
                .impressions(123L)
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
                .recommendedBudget(BigDecimal.TEN).build();
    }


    @Test
    void givenZeroCampaignGroups_WhenGetRequest_thenReturnNotFound() throws Exception{
        given(this.campaignGroupService.getAllCampaignGroups()).willReturn(Collections.emptyList());

        this.webTestClient.get().uri("/api/v1/campaigngroups/list")
                .exchange()
                .expectStatus().isNotFound();


    }

    @Test
    void givenCampaignGroups_whenGetCampaignGroups_thenReturnJsonArray() throws Exception{
        given(this.campaignGroupService.getAllCampaignGroups()).willReturn(Collections.singletonList(this.campaignGroup));

        FieldDescriptor[] campaignGroup = new FieldDescriptor[] {
                fieldWithPath("id").description("Unique Identifier of Campaign Group"),
                fieldWithPath("name").description("Name of Campaign Group") };


        this.webTestClient.get().uri("/api/v1/campaigngroups/list").accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(this.campaignGroup.getId())
                .jsonPath("$[0].name").isEqualTo(this.campaignGroup.getName())
                .consumeWith( document("campaign-groups/list",
                        responseFields(  fieldWithPath("[]")
                                .description("An array of Campaign Groups"))
                                .andWithPrefix("[].", campaignGroup)));

    }

    @Test
    void givenCampaignGroupId_whenCampaignsForGroup_thenReturnJsonArray() throws Exception{
        given(this.campaignService.getAllCampaignsForCampaignGroupId(any())).willReturn(Collections.singletonList(this.campaign));

        FieldDescriptor[] campaignFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("id").description("Unique Identifier for a Campaign"),
                fieldWithPath("name").description("Name of Campaign"),
                fieldWithPath("campaignGroupId").description("ID of Campaign Group that this campaign belongs to"),
                fieldWithPath("budget").description("The allocated budget for a campaign"),
                fieldWithPath("impressions").description("The impressions for a given campaign"),
                fieldWithPath("revenue").description("The revenue for a given campaign")
        };

        this.webTestClient.get().uri("/api/v1/campaigngroups/{campaignGroupId}/campaigns/list", 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(this.campaign.getId())
                .jsonPath("$.[0].name").isEqualTo(this.campaign.getName())
                .jsonPath("$.[0].campaignGroupId").isEqualTo(this.campaign.getCampaignGroupId())
                .jsonPath("$.[0].budget").isEqualTo(this.campaign.getBudget())
                .jsonPath("$.[0].impressions").isEqualTo(this.campaign.getImpressions())
                .jsonPath("$.[0].revenue").isEqualTo(this.campaign.getRevenue())
                .consumeWith(document("campaigns/list",
                        responseFields(fieldWithPath("[]")
                        .description("An array of Campaigns"))
                                .andWithPrefix("[].", campaignFieldDescriptor),
                        pathParameters(
                                parameterWithName("campaignGroupId").description("ID of Campaign Group that this campaign belongs to")
                        )
                ));

    }

    @Test
    void givenCampaignGroupId_whenZeroCampaignsForGroup_thenReturnNotFound() throws Exception{
        given(this.campaignService.getAllCampaignsForCampaignGroupId(any())).willReturn(Collections.emptyList());

        this.webTestClient.get().uri("/api/v1/campaigngroups/1/campaigns/list")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON);

    }

    @Test
    void givenCampaignGroupId_whenOptimisationsForGroup_thenReturnJsonArray() throws Exception{
        given(this.optimisationService.getLatestOptimisation(any())).willReturn(java.util.Optional.ofNullable(this.optimisation));

        FieldDescriptor[] optimisations = new FieldDescriptor[] {
                fieldWithPath("id").description("Unique Identifier for a Campaign"),
                fieldWithPath("campaignGroupId").description("ID of Campaign Group that this campaign belongs to"),
                fieldWithPath("status").description("The status of the optimisation. Either it is applied or not applied to the Campaign Group")
        };

        this.webTestClient.get().uri("/api/v1/campaigngroups/{campaignGroupId}/optimisations/latest", 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(this.optimisation.getId())
                .jsonPath("$.campaignGroupId").isEqualTo(this.optimisation.getCampaignGroupId())
                .jsonPath("$.status").isEqualTo(this.optimisation.getStatus().name())
                .consumeWith(document("optimisations",
                        responseFields(optimisations),
                        pathParameters(
                                parameterWithName("campaignGroupId").description("ID of Campaign Group that this campaign belongs to"))));

    }


    @Test
    void givenCampaignGroupId_whenZeroOptimisationsForGroup_thenReturnNotFound() throws Exception{
        given(this.optimisationService.getLatestOptimisation(any())).willReturn(Optional.empty());

        this.webTestClient.get().uri("/api/v1/campaigngroups/{campaignGroupId}/optimisations/latest", 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

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


        this.webTestClient.get().uri("/api/v1/optimisations/{optimisationId}/recommendations/latest", 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(this.recommendation.getId())
                .jsonPath("$.[0].campaignId").isEqualTo(this.recommendation.getCampaignId())
                .jsonPath("$.[0].optimisationId").isEqualTo(this.recommendation.getOptimisationId())
                .jsonPath("$.[0].recommendedBudget").isEqualTo(this.recommendation.getRecommendedBudget())
                .consumeWith(document("recommendations",
                        responseFields(fieldWithPath("[]")
                                .description("An array of Recommendations For a given optimisation"))
                                .andWithPrefix("[].", recommendationFieldDescriptor),
                        pathParameters(
                                parameterWithName("optimisationId").description("The Given Optimisation ID")  )));

    }

    @Test
    void givenOptimisationId_whenZeroRecommendationsForOptimisation_thenReturnNotFound() throws Exception{
        given(this.optimisationService.getLatestRecommendations(any())).willReturn(Collections.emptyList());

        this.webTestClient.get().uri("/api/v1/optimisations/{optimisationId}/recommendations/latest", 1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON);

    }

    @Test
    void givenOptimisationId_whenApplyRecommendation_thenCampaignBudgetUpdated() throws Exception{
        given(this.optimisationService.getOptimisationById(any())).willReturn(Optional.of(this.optimisation));
        given(this.optimisationService.getLatestRecommendations(any())).willReturn(Collections.singletonList(this.recommendation));
        this.campaign.setBudget(this.recommendation.getRecommendedBudget());
        given(this.optimisationService.applyRecommendations(any(), any())).willReturn(1);

        this.webTestClient.post().uri("/api/v1/optimisations/{optimisationId}/recommendations/apply", this.optimisation.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Campaigns Updated 1")
                .consumeWith(document("apply-recommendations",
                        responseFields(fieldWithPath("message")
                                .description("Returns Number of rows updated for Campaigns")),
                        pathParameters(
                                parameterWithName("optimisationId").description("The Given Optimisation ID ")
                        )));

    }

}
