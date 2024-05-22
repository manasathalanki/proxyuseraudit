package com.bh.cp.dashboard.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

class RestClientWrapperServiceImplTest {

	@InjectMocks
	private RestClientWrapperServiceImpl restClientWrapperServiceImpl;

	@Mock
	private RestTemplate restTemplate;

	private MockHttpServletRequest mockHttpServletRequest;
	private ObjectMapper mapper;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		mapper = new ObjectMapper();
		mockHttpServletRequest = new MockHttpServletRequest();
		mockHttpServletRequest.addHeader("Authorization", "Bearer abc");
		ReflectionTestUtils.setField(restClientWrapperServiceImpl, "restTemplate", restTemplate);
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("GetResponseFromUrl")
	void testGetResponseFromUrl() throws Exception {
		String policyJson = "[\"policy1\",\"policy2\"]";
		ResponseEntity<String> privileageResponse = new ResponseEntity<String>(policyJson, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
				.thenReturn(privileageResponse);
		ResponseEntity<String> response = restClientWrapperServiceImpl.getResponseFromUrl(mockHttpServletRequest,
				"http://test.com");
		List<String> privileagesList = mapper.readValue(response.getBody(), List.class);
		assertNotNull(response);
		assertEquals("policy1", privileagesList.get(0));
	}

	@Test
	@DisplayName("PostResponseFromUrl")
	void testPostResponseFromUrl() throws Exception {
		String policyJson = "[{\"id\":\"policy2\"}]";
		ResponseEntity<String> privileageResponse = new ResponseEntity<String>(policyJson, HttpStatus.OK);
		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
				.thenReturn(privileageResponse);
		ResponseEntity<String> response = restClientWrapperServiceImpl.postBodyToUrl(mockHttpServletRequest,
				"http://test.com", policyJson);
		assertNotNull(response);
	}
}
