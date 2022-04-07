package com.walshdanny700.campaign_optimisation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CampaignOptimisationApplicationTests {

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
