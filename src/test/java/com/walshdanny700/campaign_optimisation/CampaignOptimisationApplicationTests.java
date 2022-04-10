package com.walshdanny700.campaign_optimisation;


import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

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

}
