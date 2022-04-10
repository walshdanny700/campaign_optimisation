package com.walshdanny700.campaign_optimisation;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;

import static java.text.MessageFormat.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CampaignOptimisationApplicationTests {

	@LocalServerPort
	private int port;

	@Test
	void contextLoads() {
		assertTrue(true);
	}


	@Test
	void applicationConfigureTest() {
		SpringApplicationBuilder before = new SpringApplicationBuilder();

		SpringApplicationBuilder after = new CampaignOptimisationApplication().configure(before);
		assertEquals(before, after);
	}

	@Test
	void applicationStarts() {
		CampaignOptimisationApplication.main(new String[] {});
		assertTrue(true);
	}

//	/**
//	 * This will work when you run ./gradlew build
//	 * It is a sanity check for the spring rest docs
//	 *
//	 * @throws IOException
//	 */
//	@Test
//	void restDocsSanityCheck() throws IOException {
//		String url = format("http://localhost:{0,number,#}/docs/index.html", port);
//
//		HttpClient client = HttpClientBuilder.create().build();
//		HttpResponse response = client.execute(new HttpGet(url));
//		int statusCode = response.getStatusLine().getStatusCode();
//		assertThat(statusCode, equalTo(HttpStatus.SC_OK));
//
//	}

}
