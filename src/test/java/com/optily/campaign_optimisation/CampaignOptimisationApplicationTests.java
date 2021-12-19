package com.optily.campaign_optimisation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CampaignOptimisationApplicationTests {

	@Test
	void contextLoads() {
		assert(true);
	}


	@Test
	public void mainMethodTest() {
		CampaignOptimisationApplication.main(new String[] {});
	}

	@Test
	public void applicationConfigureTest() {
		SpringApplicationBuilder before = new SpringApplicationBuilder();

		SpringApplicationBuilder after = new CampaignOptimisationApplication().configure(before);
		assertEquals(before, after);
	}
}
