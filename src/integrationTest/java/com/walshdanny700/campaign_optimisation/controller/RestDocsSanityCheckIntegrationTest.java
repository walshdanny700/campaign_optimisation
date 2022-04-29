package com.walshdanny700.campaign_optimisation.controller;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

import static java.text.MessageFormat.format;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class RestDocsSanityCheckIntegrationTest {

    @LocalServerPort
    private int port;

    String url;

    @Test
    void givenCallToRestDocsThenReturnValidResponse() throws SSLException {

        url = format("https://localhost:{0,number,#}", port);

        SslContext context = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();

        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(context));

        ClientHttpConnector httpConnector = new ReactorClientHttpConnector(
                httpClient);
        WebTestClient webClient = WebTestClient.bindToServer(httpConnector).baseUrl(url).build();
        webClient.get().uri("/docs/index.html").exchange().expectStatus().isEqualTo(HttpStatus.OK);

    }

}
