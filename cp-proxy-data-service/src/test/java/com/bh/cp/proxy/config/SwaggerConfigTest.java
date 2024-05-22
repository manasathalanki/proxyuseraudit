package com.bh.cp.proxy.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.swagger.v3.oas.models.OpenAPI;

class SwaggerConfigTest {

	private SwaggerConfig swaggerConfig;

	@BeforeEach
	void setUp() throws Exception {
		swaggerConfig = new SwaggerConfig();
		ReflectionTestUtils.setField(swaggerConfig, "swaggerUiPath", "http://test.com");
	}

	@Test
	@DisplayName("Test microserviceOpenAPI1_Validate Created Bean")
	void testMicroserviceOpenAPI_Positive1() {
		OpenAPI output = swaggerConfig.microserviceOpenAPI();
		assertEquals("support@bakerhughes.com", output.getInfo().getContact().getEmail());
		assertEquals("Baker Hughes", output.getInfo().getContact().getName());
		assertEquals("https://www.bakerhughes.com", output.getInfo().getContact().getUrl());
		assertEquals("CUSTOMER PORTAL - Proxy Data Service Documentation", output.getInfo().getDescription());
		assertEquals("CUSTOMER PORTAL PROXY SERVICE", output.getInfo().getTitle());
		assertEquals("Beta 1.0", output.getInfo().getVersion());
		assertEquals("Dev Server URL", output.getServers().get(0).getDescription());
		assertEquals("http://test.com", output.getServers().get(0).getUrl());
	}

}
