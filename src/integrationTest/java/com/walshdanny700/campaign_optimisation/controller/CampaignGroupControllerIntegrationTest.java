package com.walshdanny700.campaign_optimisation.controller;

import com.walshdanny700.campaign_optimisation.entity.Optimisation;
import com.walshdanny700.campaign_optimisation.entity.OptimisationStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.Map;

import static java.text.MessageFormat.format;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CampaignGroupControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private WebTestClient webClient;

    String url;

    @BeforeEach
    public void setup() {
        url = format("http://localhost:{0,number,#}", port);
        this.webClient =  WebTestClient.bindToServer().baseUrl(url).build();

    }

    @Test
    void givenGetCampaignGroup_WhenSelected_ThenReturnData(){

        this.webClient.get().uri("/api/v1/campaigngroups/list")
                .exchange().expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody().jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("campaigns");

    }

    @Test
    void givenGetCampaignsForGroup_WhenValidCampaignGroupId_ThenReturnData(){
        this.webClient.get().uri("/api/v1/campaigngroups/1/campaigns/list").exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody().jsonPath("$.length()").isEqualTo(11)
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("2021-July-BOF-Books")
                .jsonPath("$[0].campaignGroupId").isEqualTo(1)
                .jsonPath("$[0].budget").isEqualTo(2108)
                .jsonPath("$[0].impressions").isEqualTo(36358);

    }


    @Test
    void givenGetOptimisationForGroup_WhenValidCampaignGroupId_ThenReturnData(){

        Optimisation optimisation  = Optimisation.builder().id(1L).campaignGroupId(1L).status(OptimisationStatus.NOT_APPLIED).build();

        this.webClient.get().uri("/api/v1/campaigngroups/1/optimisations/latest").exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody().jsonPath("$.id").isEqualTo(1L)
                .jsonPath("$.campaignGroupId").isEqualTo(1L)
                .jsonPath("$.status").isEqualTo(OptimisationStatus.NOT_APPLIED.toString());

    }

    @Test
    void givenGetRecommendationsForOptimisation_WhenValidOptimisationId_ThenReturnData(){

        this.webClient.get().uri("/api/v1/optimisations/1/recommendations/latest").exchange().expectStatus()
                .isEqualTo(HttpStatus.OK).expectBody().jsonPath("$.length()").isEqualTo(11)
                .jsonPath("$[0].id").isEqualTo(null)
                .jsonPath("$[0].campaignId").isEqualTo(1)
                .jsonPath("$[0].optimisationId").isEqualTo(1)
                .jsonPath("$[0].recommendedBudget").isEqualTo(1646.7366528182551704);
    }

    @Test
    @Transactional
    void givenApplyLatestRecommendation_WhenNotValidOptimisationId_ThenReturnZeroUpdated(){
        Map<String,String> result = new HashMap<>() ;
        result.put("message","Campaigns Updated 0, Optimisations Not found for given optimisationId");

        this.webClient.post().uri("/api/v1/optimisations/11111/recommendations/apply").exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(new ParameterizedTypeReference<Map<String,String>>() {}).isEqualTo(result);

    }

    @Test
    void givenApplyLatestRecommendation_WhenOptimisationStatusApplied_ThenReturnZeroUpdated(){

        Map<String,String> resultOne = new HashMap<>() ;
        resultOne.put("message","Campaigns Updated 11");

        Map<String,String> resultTwo = new HashMap<>() ;
        resultTwo.put("message","Campaigns Updated 0, Optimisations Already Applied");


        this.webClient.post().uri("/api/v1/optimisations/1/recommendations/apply").exchange().expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(new ParameterizedTypeReference<Map<String,String>>() {}).isEqualTo(resultOne);

        this.webClient.post().uri("/api/v1/optimisations/1/recommendations/apply").exchange().expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(new ParameterizedTypeReference<Map<String,String>>() {}).isEqualTo(resultTwo);

    }

}