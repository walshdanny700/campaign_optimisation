package com.walshdanny700.campaign_optimisation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class CampaignGroupControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void givenGetCampaignGroup_WhenSelected_ThenReturnData(){
        String url = format("http://localhost:{0,number,#}/api/v1/campaigngroups/list", port);
        assertThat(this.restTemplate.getForObject(url,
                String.class)).contains("[{\"id\":1,\"name\":\"campaigns\"}]");
    }

    @Test
    void givenGetCampaignsForGroup_WhenValidCampaignGroupId_ThenReturnData(){
        String url = format("http://localhost:{0,number,#}/api/v1/campaigngroups/1/campaigns/list", port);
        assertThat(this.restTemplate.getForObject(url,
                String.class)).contains("[{\"id\":1,\"name\":\"2021-July-BOF-Books\",\"campaignGroupId\":1,\"budget\":2108,\"impressions\":36358,\"revenue\":null},{\"id\":2,\"name\":\"test\",\"campaignGroupId\":1,\"budget\":2108,\"impressions\":36358,\"revenue\":null},{\"id\":3,\"name\":\"3_299_BBQ_G-A_CV_SHP\",\"campaignGroupId\":1,\"budget\":674,\"impressions\":29980,\"revenue\":null},{\"id\":4,\"name\":\"3_299_Bulbs_G-A_CV_SHP\",\"campaignGroupId\":1,\"budget\":2000,\"impressions\":57561,\"revenue\":null},{\"id\":5,\"name\":\"3_299_Containers_G-A_OT_SHP\",\"campaignGroupId\":1,\"budget\":500,\"impressions\":25864,\"revenue\":null},{\"id\":6,\"name\":\"3_299_Furniture_G-A_CV_SHP\",\"campaignGroupId\":1,\"budget\":1023,\"impressions\":68640,\"revenue\":null},{\"id\":7,\"name\":\"3_299_Gifts_AOC_G-A_OT_SHP\",\"campaignGroupId\":1,\"budget\":500,\"impressions\":32743,\"revenue\":null},{\"id\":8,\"name\":\"3_299_Lawn_Care_G-A_CV_SHP\",\"campaignGroupId\":1,\"budget\":4600,\"impressions\":31023,\"revenue\":null},{\"id\":9,\"name\":\"3_299_Vegepod_G-A_CV_SHP\",\"campaignGroupId\":1,\"budget\":1325,\"impressions\":15209,\"revenue\":null},{\"id\":10,\"name\":\"3_299_Wild_Bird_G-A_AOC_SHP\",\"campaignGroupId\":1,\"budget\":500,\"impressions\":4931,\"revenue\":null},{\"id\":11,\"name\":\"Walshdanny700-July2021-TOF-Test\",\"campaignGroupId\":1,\"budget\":1,\"impressions\":0,\"revenue\":null}]");
    }


    @Test
    void givenGetOptimisationForGroup_WhenValidCampaignGroupId_ThenReturnData(){
        String url = format("http://localhost:{0,number,#}/api/v1/campaigngroups/1/optimisations/latest", port);
        assertThat(this.restTemplate.getForObject(url,
                String.class)).contains("{\"id\":1,\"campaignGroupId\":1,\"status\":\"NOT_APPLIED\"}");
    }

    @Test
    void givenGetRecommendationsForOptimisation_WhenValidOptimisationId_ThenReturnData(){
        String url = format("http://localhost:{0,number,#}/api/v1/optimisations/1/recommendations/latest", port);

        assertThat(this.restTemplate.getForObject(url,
                String.class)).contains("[{\"id\":null,\"campaignId\":1,\"optimisationId\":1,\"recommendedBudget\":1646.7366528182551704},{\"id\":null,\"campaignId\":2,\"optimisationId\":1,\"recommendedBudget\":1646.7366528182551704},{\"id\":null,\"campaignId\":3,\"optimisationId\":1,\"recommendedBudget\":1357.86250210383647001},{\"id\":null,\"campaignId\":4,\"optimisationId\":1,\"recommendedBudget\":2607.06882867241268202},{\"id\":null,\"campaignId\":5,\"optimisationId\":1,\"recommendedBudget\":1171.43948480365685646},{\"id\":null,\"campaignId\":6,\"optimisationId\":1,\"recommendedBudget\":3108.8619794665559259},{\"id\":null,\"campaignId\":7,\"optimisationId\":1,\"recommendedBudget\":1483.00506692414672598},{\"id\":null,\"campaignId\":8,\"optimisationId\":1,\"recommendedBudget\":1405.10234832446042301},{\"id\":null,\"campaignId\":9,\"optimisationId\":1,\"recommendedBudget\":688.85025998990163861},{\"id\":null,\"campaignId\":10,\"optimisationId\":1,\"recommendedBudget\":223.336224078519612126},{\"id\":null,\"campaignId\":11,\"optimisationId\":1,\"recommendedBudget\":0.0}]");
    }

    @Test
    @Transactional
    void givenApplyLatestRecommendation_WhenNotValidOptimisationId_ThenReturnZeroUpdated(){

        String url = format("http://localhost:{0,number,#}/api/v1/optimisations/11111/recommendations/apply", port);

        ResponseEntity<Map<String,String>> response = this.restTemplate.postForEntity(url,
                null,  (Class<Map<String,String>>)(Class)Map.class);

        assertEquals("Campaigns Updated 0, Optimisations Not found for given optimisationId",Objects.requireNonNull(response.getBody()).get("message"));
    }

    @Test
    void givenApplyLatestRecommendation_WhenOptimisationStatusApplied_ThenReturnZeroUpdated(){
        String url = format("http://localhost:{0,number,#}/api/v1/optimisations/1/recommendations/apply", port);

        // Apply Recommendation the first time
        ResponseEntity<Map<String,String>> firstResponse = this.restTemplate.postForEntity(url,
                null,  (Class<Map<String,String>>)(Class)Map.class);

        assertEquals("Campaigns Updated 11",Objects.requireNonNull(firstResponse.getBody()).get("message"));

        // Second Time Apply The Recommendation
        ResponseEntity<Map<String,String>> secondResponse = this.restTemplate.postForEntity(url,
                null,  (Class<Map<String,String>>)(Class)Map.class);

        assertEquals("Campaigns Updated 0, Optimisations Already Applied",Objects.requireNonNull(secondResponse.getBody()).get("message"));


    }



}
