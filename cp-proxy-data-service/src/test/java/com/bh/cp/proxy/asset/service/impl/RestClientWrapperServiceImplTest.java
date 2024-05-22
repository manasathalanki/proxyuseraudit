package com.bh.cp.proxy.asset.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.RestTemplate;

class RestClientWrapperServiceImplTest {

	@InjectMocks
	private RestClientWrapperServiceImpl restClientWrapperService;

	@Mock
	private RestTemplate restTemplate;

	private MockHttpServletRequest mockHttpServletRequest;

	private String requestJson = "{\"id\":\"test1\"}";

	private String responseJson = "{\"status\":\"success\"}";

	private String url = "http://localhost:8080";

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer abc");
	}

	@Test
	void testGetResponseFromUrl() {
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
				.thenReturn(ResponseEntity.of(Optional.of(responseJson)));
		ResponseEntity<String> actualResponse = restClientWrapperService.getResponseFromUrl(mockHttpServletRequest,
				url);
		assertEquals(responseJson, actualResponse.getBody());
	}

	@Test
	void testPostBodyToUrl() {
		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
				.thenReturn(ResponseEntity.of(Optional.of(responseJson)));
		ResponseEntity<String> actualResponse = restClientWrapperService.postBodyToUrl(mockHttpServletRequest,
				url,requestJson);
		assertEquals(responseJson, actualResponse.getBody());
	}

}
