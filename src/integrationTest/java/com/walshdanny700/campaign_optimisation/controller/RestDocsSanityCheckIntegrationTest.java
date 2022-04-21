package com.walshdanny700.campaign_optimisation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import static java.text.MessageFormat.format;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestDocsSanityCheckIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webClient;

    @Test
    void givenCallToRestDocsThenReturnValidResponse(){

        String url = format("http://localhost:{0,number,#}", port);

        webClient = WebTestClient.bindToServer().baseUrl(url).build();
        webClient.get().uri("/docs/index.html").exchange().expectStatus().isEqualTo(HttpStatus.OK);

    }

}
